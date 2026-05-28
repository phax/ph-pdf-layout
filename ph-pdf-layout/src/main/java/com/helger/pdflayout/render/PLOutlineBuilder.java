/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.pdflayout.render;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.IPDDocumentCustomizer;
import com.helger.pdflayout.base.IPLObject;
import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * Convenience builder for PDF outlines (a.k.a. bookmarks). Collects an entry tree of
 * <code>(title, elementID)</code> pairs and turns them into a {@link PDDocumentOutline} after
 * rendering completes.
 * <p>
 * Wiring:
 *
 * <pre>
 * final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
 * final PLOutlineBuilder.Entry aCh2 = aOutline.addEntry ("Chapter 2", "ch2");
 * aCh2.addChild ("Section 2.1", "sec21");
 * aCh2.addChild ("Section 2.2", "sec22");
 *
 * aPageSet.setRenderListener (aOutline); // capture positions
 * aPageLayout.setDocumentCustomizer (aOutline); // build outline at save
 * </pre>
 *
 * The builder implements both {@link IPLRenderListener} (capturing where each referenced element
 * ends up during render) and {@link IPDDocumentCustomizer} (writing the outline into the document
 * after rendering). Use {@link IPDDocumentCustomizer#and(IPDDocumentCustomizer)} if you need to
 * compose with other customizers.
 * <p>
 * Entries whose <code>elementID</code> is <code>null</code> are pure grouping nodes (no
 * destination, not clickable). Entries pointing at an element that never rendered are still
 * included as non-clickable headers; a debug-consistency message is emitted to aid diagnosis.
 *
 * @author Philip Helger
 * @since 8.2.0
 */
public class PLOutlineBuilder implements IPLRenderListener, IPDDocumentCustomizer
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PLOutlineBuilder.class);

  /**
   * A single outline entry. Add children with {@link #addChild(String, String)} or
   * {@link #addChild(String, IPLObject)}.
   */
  public static final class Entry
  {
    private final String m_sTitle;
    private final String m_sElementID;
    private final ICommonsList <Entry> m_aChildren = new CommonsArrayList <> ();

    Entry (@NonNull @Nonempty final String sTitle, @Nullable final String sElementID)
    {
      ValueEnforcer.notEmpty (sTitle, "Title");
      m_sTitle = sTitle;
      m_sElementID = sElementID;
    }

    /**
     * @return The display title of this entry as set at construction. Never <code>null</code> and
     *         never empty.
     */
    @NonNull
    @Nonempty
    public String getTitle ()
    {
      return m_sTitle;
    }

    /**
     * @return The {@link IPLObject#getOriginalID()} of the element this entry points at, or
     *         <code>null</code> if this is a pure grouping node with no destination.
     */
    @Nullable
    public String getElementID ()
    {
      return m_sElementID;
    }

    /**
     * @return A copy of this entry's children, in insertion order. Never <code>null</code>, may be
     *         empty.
     */
    @NonNull
    @ReturnsMutableCopy
    public ICommonsList <Entry> getAllChildren ()
    {
      return m_aChildren.getClone ();
    }

    /**
     * @return <code>true</code> if this entry has at least one child, <code>false</code> otherwise.
     */
    public boolean hasChildren ()
    {
      return !m_aChildren.isEmpty ();
    }

    /**
     * Add a child entry pointing at the element with the given ID.
     *
     * @param sTitle
     *        Display title of the entry. Must not be empty.
     * @param sElementID
     *        The {@link IPLObject#getOriginalID()} of the target element, or <code>null</code> for
     *        a non-clickable grouping node.
     * @return The new child entry, for further nesting.
     */
    @NonNull
    public Entry addChild (@NonNull @Nonempty final String sTitle, @Nullable final String sElementID)
    {
      final Entry e = new Entry (sTitle, sElementID);
      m_aChildren.add (e);
      return e;
    }

    /**
     * Convenience overload that resolves the element ID via {@link IPLObject#getOriginalID()}.
     *
     * @param sTitle
     *        Display title of the entry. Must not be empty.
     * @param aElement
     *        The target element. Its original ID is used as the destination key. Must not be
     *        <code>null</code>.
     * @return The new child entry, for further nesting. Never <code>null</code>.
     */
    @NonNull
    public Entry addChild (@NonNull @Nonempty final String sTitle, @NonNull final IPLObject <?> aElement)
    {
      ValueEnforcer.notNull (aElement, "Element");
      return addChild (sTitle, aElement.getOriginalID ());
    }

    /**
     * Convenience overload for a grouping node (no destination).
     *
     * @param sTitle
     *        Display title of the entry. Must not be empty.
     * @return The new child entry, for further nesting. Never <code>null</code>.
     */
    @NonNull
    public Entry addChild (@NonNull @Nonempty final String sTitle)
    {
      return addChild (sTitle, (String) null);
    }
  }

  private final ICommonsList <Entry> m_aRootEntries = new CommonsArrayList <> ();
  private final PLRenderedElementCollector m_aCollector = new PLRenderedElementCollector ();
  private boolean m_bInitiallyExpanded = true;

  /**
   * Default constructor. The outline starts expanded by default; toggle with
   * {@link #setInitiallyExpanded(boolean)}.
   */
  public PLOutlineBuilder ()
  {}

  /**
   * @return <code>true</code> (default) if the outline starts expanded when the PDF opens.
   */
  public final boolean isInitiallyExpanded ()
  {
    return m_bInitiallyExpanded;
  }

  /**
   * Set whether the outline starts expanded when the PDF opens. Defaults to <code>true</code>.
   *
   * @param bInitiallyExpanded
   *        <code>true</code> to start expanded.
   * @return this for chaining
   */
  @NonNull
  public final PLOutlineBuilder setInitiallyExpanded (final boolean bInitiallyExpanded)
  {
    m_bInitiallyExpanded = bInitiallyExpanded;
    return this;
  }

  /**
   * Add a top-level entry pointing at the element with the given ID.
   *
   * @param sTitle
   *        Display title. Must not be empty.
   * @param sElementID
   *        The {@link IPLObject#getOriginalID()} of the target element, or <code>null</code> for a
   *        non-clickable grouping node.
   * @return The new entry, for adding children.
   */
  @NonNull
  public Entry addEntry (@NonNull @Nonempty final String sTitle, @Nullable final String sElementID)
  {
    final Entry e = new Entry (sTitle, sElementID);
    m_aRootEntries.add (e);
    return e;
  }

  /**
   * Convenience overload that resolves the element ID via {@link IPLObject#getOriginalID()}.
   *
   * @param sTitle
   *        Display title of the entry. Must not be empty.
   * @param aElement
   *        The target element. Its original ID is used as the destination key. Must not be
   *        <code>null</code>.
   * @return The new entry, for adding children. Never <code>null</code>.
   */
  @NonNull
  public Entry addEntry (@NonNull @Nonempty final String sTitle, @NonNull final IPLObject <?> aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");
    return addEntry (sTitle, aElement.getOriginalID ());
  }

  /**
   * Convenience overload for a grouping node (no destination).
   *
   * @param sTitle
   *        Display title of the entry. Must not be empty.
   * @return The new entry, for adding children. Never <code>null</code>.
   */
  @NonNull
  public Entry addEntry (@NonNull @Nonempty final String sTitle)
  {
    return addEntry (sTitle, (String) null);
  }

  /**
   * @return A copy of the top-level entry list, in insertion order. Never <code>null</code>, may be
   *         empty.
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsList <Entry> getAllRootEntries ()
  {
    return m_aRootEntries.getClone ();
  }

  /**
   * @return <code>true</code> if at least one top-level entry has been added, <code>false</code>
   *         otherwise. When this returns <code>false</code>, {@link #customizeDocument(PDDocument)}
   *         writes no outline to the document.
   */
  public final boolean hasEntries ()
  {
    return !m_aRootEntries.isEmpty ();
  }

  /**
   * {@inheritDoc} Delegates to the internal {@link PLRenderedElementCollector} which records the
   * first appearance of each element by {@link IPLObject#getOriginalID()}.
   *
   * @param aElement
   *        The element that was just rendered. Never <code>null</code>.
   * @param aCtx
   *        The render context used for this render call. Never <code>null</code>.
   */
  public void onElementRendered (@NonNull final IPLRenderableObject <?> aElement, @NonNull final PageRenderContext aCtx)
  {
    m_aCollector.onElementRendered (aElement, aCtx);
  }

  @NonNull
  private PDOutlineItem _buildOutlineItemRecursive (@NonNull final PDDocument aDoc, @NonNull final Entry aEntry)
  {
    final PDOutlineItem aItem = new PDOutlineItem ();
    aItem.setTitle (aEntry.getTitle ());

    final String sElementID = aEntry.getElementID ();
    if (sElementID != null)
    {
      final PLRenderedElementCollector.Location aLoc = m_aCollector.getLocation (sElementID);
      if (aLoc != null)
      {
        final int nPageIndex = aLoc.getTotalPageIndex ();
        if (nPageIndex < aDoc.getNumberOfPages ())
        {
          final PDPage aPage = aDoc.getPage (nPageIndex);
          final PDPageXYZDestination aDest = new PDPageXYZDestination ();
          aDest.setPage (aPage);
          aDest.setLeft ((int) aLoc.getStartLeft ());
          aDest.setTop ((int) aLoc.getStartTop ());
          // Leave zoom unset = preserve reader's current zoom level
          aItem.setDestination (aDest);
        }
        else
        {
          LOGGER.warn ("Outline entry '" +
                       aEntry.getTitle () +
                       "' references page index " +
                       nPageIndex +
                       " which is out of range (document has " +
                       aDoc.getNumberOfPages () +
                       " pages)");
        }
      }
      else
      {
        LOGGER.warn ("Outline entry '" +
                     aEntry.getTitle () +
                     "' references element '" +
                     sElementID +
                     "' which was not rendered");
      }
    }

    for (final Entry aChild : aEntry.getAllChildren ())
      aItem.addLast (_buildOutlineItemRecursive (aDoc, aChild));

    if (m_bInitiallyExpanded && aEntry.hasChildren ())
      aItem.openNode ();

    return aItem;
  }

  /**
   * {@inheritDoc} Writes the configured entry tree as a {@link PDDocumentOutline} into the given
   * document. Does nothing if no entries were added. Entries pointing at elements that never
   * rendered are emitted as non-clickable headers; a warning is logged.
   *
   * @param aDoc
   *        The document to attach the outline to. Never <code>null</code>.
   * @throws IOException
   *         Never thrown by this implementation, declared to satisfy the
   *         {@link IPDDocumentCustomizer} contract.
   */
  public void customizeDocument (@NonNull final PDDocument aDoc) throws IOException
  {
    if (m_aRootEntries.isNotEmpty ())
    {
      final PDDocumentOutline aOutline = new PDDocumentOutline ();
      aDoc.getDocumentCatalog ().setDocumentOutline (aOutline);

      for (final Entry aRootEntry : m_aRootEntries)
        aOutline.addLast (_buildOutlineItemRecursive (aDoc, aRootEntry));

      if (m_bInitiallyExpanded)
        aOutline.openNode ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("InitiallyExpanded", m_bInitiallyExpanded)
                                       .append ("RootEntries", m_aRootEntries)
                                       .append ("Collector", m_aCollector)
                                       .getToString ();
  }
}
