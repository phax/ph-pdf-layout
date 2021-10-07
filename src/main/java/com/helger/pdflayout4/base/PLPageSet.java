/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.base;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.debug.PLDebugLog;
import com.helger.pdflayout4.element.special.PLPageBreak;
import com.helger.pdflayout4.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout4.render.ERenderingElementType;
import com.helger.pdflayout4.render.IPreRenderContextCustomizer;
import com.helger.pdflayout4.render.IRenderContextCustomizer;
import com.helger.pdflayout4.render.PLRenderHelper;
import com.helger.pdflayout4.render.PagePreRenderContext;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.render.PreparationContextGlobal;
import com.helger.pdflayout4.spec.BorderSpec;
import com.helger.pdflayout4.spec.MarginSpec;
import com.helger.pdflayout4.spec.PaddingSpec;
import com.helger.pdflayout4.spec.SizeSpec;

/**
 * Represents a single page layout as element. It consists of a page size, a
 * page header and footer as well as a set of page body elements.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLPageSet extends AbstractPLObject <PLPageSet> implements IPLHasMarginBorderPadding <PLPageSet>, IPLHasFillColor <PLPageSet>
{
  public static final boolean DEFAULT_DIFFERENT_FIRST_PAGE_HEADER = false;
  public static final boolean DEFAULT_DIFFERENT_FIRST_PAGE_FOOTER = false;
  private static final Logger LOGGER = LoggerFactory.getLogger (PLPageSet.class);

  private final SizeSpec m_aPageSize;
  private MarginSpec m_aMargin = DEFAULT_MARGIN;
  private PaddingSpec m_aPadding = DEFAULT_PADDING;
  private BorderSpec m_aBorder = DEFAULT_BORDER;
  private Color m_aFillColor = DEFAULT_FILL_COLOR;
  private boolean m_bDifferentFirstPageHeader = DEFAULT_DIFFERENT_FIRST_PAGE_HEADER;
  private IPLRenderableObject <?> m_aFirstPageHeader;
  private IPLRenderableObject <?> m_aPageHeader;
  private final ICommonsList <IPLRenderableObject <?>> m_aElements = new CommonsArrayList <> ();
  private boolean m_bDifferentFirstPageFooter = DEFAULT_DIFFERENT_FIRST_PAGE_FOOTER;
  private IPLRenderableObject <?> m_aFirstPageFooter;
  private IPLRenderableObject <?> m_aPageFooter;
  private IPreRenderContextCustomizer m_aPRCCustomizer;
  private IRenderContextCustomizer m_aRCCustomizer;

  public PLPageSet (@Nonnull final PDRectangle aPageRect)
  {
    this (SizeSpec.create (aPageRect));
  }

  public PLPageSet (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    this (new SizeSpec (fWidth, fHeight));
  }

  public PLPageSet (@Nonnull final SizeSpec aPageSize)
  {
    m_aPageSize = ValueEnforcer.notNull (aPageSize, "PageSize");
  }

  @Nonnull
  public final SizeSpec getPageSize ()
  {
    return m_aPageSize;
  }

  public final float getPageWidth ()
  {
    return m_aPageSize.getWidth ();
  }

  public final float getPageHeight ()
  {
    return m_aPageSize.getHeight ();
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @Nonnull
  public final PLPageSet setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return this;
  }

  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public final PLPageSet setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return this;
  }

  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @Nonnull
  public final PLPageSet setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    m_aBorder = aBorder;
    return this;
  }

  @Nullable
  public final Color getFillColor ()
  {
    return m_aFillColor;
  }

  @Nonnull
  public final PLPageSet setFillColor (@Nullable final Color aFillColor)
  {
    m_aFillColor = aFillColor;
    return this;
  }

  @Nullable
  public final IPreRenderContextCustomizer getPreRenderContextCustomizer ()
  {
    return m_aPRCCustomizer;
  }

  @Nonnull
  public final PLPageSet setPreRenderContextCustomizer (@Nullable final IPreRenderContextCustomizer aPRCCustomizer)
  {
    m_aPRCCustomizer = aPRCCustomizer;
    return this;
  }

  @Nullable
  public final IRenderContextCustomizer getRenderContextCustomizer ()
  {
    return m_aRCCustomizer;
  }

  @Nonnull
  public final PLPageSet setRenderContextCustomizer (@Nullable final IRenderContextCustomizer aRCCustomizer)
  {
    m_aRCCustomizer = aRCCustomizer;
    return this;
  }

  /**
   * @param aObj
   *        The object of which the outline should be subtracted.
   * @return The usable page width without the x-paddings, x-borders and
   *         x-margins
   */
  @Nonnegative
  private float _getAvailableWidth (@Nonnull final IPLHasMarginBorderPadding <?> aObj)
  {
    return m_aPageSize.getWidth () - aObj.getOutlineXSum ();
  }

  /**
   * @return The usable page width without the x-paddings, x-borders and
   *         x-margins
   */
  @Nonnegative
  public float getAvailableWidth ()
  {
    return _getAvailableWidth (this);
  }

  /**
   * @param aObj
   *        The object of which the outline should be subtracted.
   * @return The usable page height without the y-paddings, y-borders and
   *         y-margins
   */
  @Nonnegative
  private float _getAvailableHeight (@Nonnull final IPLHasMarginBorderPadding <?> aObj)
  {
    return m_aPageSize.getHeight () - aObj.getOutlineYSum ();
  }

  /**
   * @return The usable page height without the y-paddings, y-borders and
   *         y-margins
   */
  @Nonnegative
  public float getAvailableHeight ()
  {
    return _getAvailableHeight (this);
  }

  /**
   * @return <code>true</code> if a special page header should be used on the
   *         first page, <code>false</code> if the same header should be used.
   * @since 5.0.2
   */
  public boolean isDifferentFirstPageHeader ()
  {
    return m_bDifferentFirstPageHeader;
  }

  /**
   * Enable/disable usage of special header on the first page. To have an
   * effect, {@link #setFirstPageHeader(IPLRenderableObject)} must be called
   *
   * @param bDifferentFirstPageHeader
   *        <code>true</code> for special page header on the first page
   * @return this for chaining
   * @see #setFirstPageHeader(IPLRenderableObject)
   * @since 5.0.2
   */
  @Nonnull
  public PLPageSet setDifferentFirstPageHeader (final boolean bDifferentFirstPageHeader)
  {
    m_bDifferentFirstPageHeader = bDifferentFirstPageHeader;
    return this;
  }

  /**
   * @return The global first page header. May be <code>null</code>.
   * @since 5.0.2
   */
  @Nullable
  public IPLRenderableObject <?> getFirstPageHeader ()
  {
    return m_aFirstPageHeader;
  }

  /**
   * @return <code>true</code> if a global first page header is present,
   *         <code>false</code> if not.
   * @since 5.0.2
   */
  public boolean hasFirstPageHeader ()
  {
    return m_aFirstPageHeader != null;
  }

  /**
   * Set the global first page header. Must be enabled explicitly via
   * {@link #setDifferentFirstPageHeader(boolean)} to take effect.
   *
   * @param aPageHeader
   *        The global page header. May be <code>null</code>.
   * @return this
   * @see #setDifferentFirstPageHeader(boolean)
   * @since 5.0.2
   */
  @Nonnull
  public PLPageSet setFirstPageHeader (@Nullable final IPLRenderableObject <?> aPageHeader)
  {
    m_aFirstPageHeader = aPageHeader;
    return this;
  }

  /**
   * @return The global page header. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getPageHeader ()
  {
    return m_aPageHeader;
  }

  /**
   * @return <code>true</code> if a global page header is present,
   *         <code>false</code> if not.
   */
  public boolean hasPageHeader ()
  {
    return m_aPageHeader != null;
  }

  /**
   * Set the global page header
   *
   * @param aPageHeader
   *        The global page header. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageHeader (@Nullable final IPLRenderableObject <?> aPageHeader)
  {
    m_aPageHeader = aPageHeader;
    return this;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <? extends IPLRenderableObject <?>> getAllElements ()
  {
    return m_aElements.getClone ();
  }

  @Nonnegative
  public int getElementCount ()
  {
    return m_aElements.size ();
  }

  public void forEachElement (@Nonnull final Consumer <? super IPLRenderableObject <?>> aConsumer)
  {
    m_aElements.forEach (aConsumer);
  }

  /**
   * Add an element to this page set.
   *
   * @param aElement
   *        The element to add. May not be <code>null</code>.
   * @return this for chaining.
   */
  @Nonnull
  public PLPageSet addElement (@Nonnull final IPLRenderableObject <?> aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");
    m_aElements.add (aElement);
    return this;
  }

  /**
   * @return <code>true</code> if a special page footer should be used on the
   *         first page, <code>false</code> if the same footer should be used.
   * @since 5.0.2
   */
  public boolean isDifferentFirstPageFooter ()
  {
    return m_bDifferentFirstPageFooter;
  }

  /**
   * Enable/disable usage of special footer on the first page. To have an
   * effect, {@link #setFirstPageFooter(IPLRenderableObject)} must be called.
   *
   * @param bDifferentFirstPageFooter
   *        <code>true</code> for special page footer on the first page
   * @return this for chaining
   * @see #setFirstPageFooter(IPLRenderableObject)
   * @since 5.0.2
   */
  @Nonnull
  public PLPageSet setDifferentFirstPageFooter (final boolean bDifferentFirstPageFooter)
  {
    m_bDifferentFirstPageFooter = bDifferentFirstPageFooter;
    return this;
  }

  /**
   * @return The global first page footer. May be <code>null</code>.
   * @since 5.0.2
   */
  @Nullable
  public IPLRenderableObject <?> getFirstPageFooter ()
  {
    return m_aFirstPageFooter;
  }

  /**
   * @return <code>true</code> if a global first page footer is present,
   *         <code>false</code> if not.
   * @since 5.0.2
   */
  public boolean hasFirstPageFooter ()
  {
    return m_aFirstPageFooter != null;
  }

  /**
   * Set the global page footer. Must be enabled explicitly via
   * {@link #setDifferentFirstPageFooter(boolean)} to take effect.
   *
   * @param aFirstPageFooter
   *        The global first page footer. May be <code>null</code>.
   * @return this
   * @see #setDifferentFirstPageFooter(boolean)
   * @since 5.0.2
   */
  @Nonnull
  public PLPageSet setFirstPageFooter (@Nullable final IPLRenderableObject <?> aFirstPageFooter)
  {
    m_aFirstPageFooter = aFirstPageFooter;
    return this;
  }

  /**
   * @return The global page footer. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getPageFooter ()
  {
    return m_aPageFooter;
  }

  /**
   * @return <code>true</code> if a global page footer is present,
   *         <code>false</code> if not.
   */
  public boolean hasPageFooter ()
  {
    return m_aPageFooter != null;
  }

  /**
   * Set the global page footer
   *
   * @param aPageFooter
   *        The global page footer. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageFooter (@Nullable final IPLRenderableObject <?> aPageFooter)
  {
    m_aPageFooter = aPageFooter;
    return this;
  }

  private float _getYTop (@Nonnull final IPLHasMarginBorderPadding <?> aObj)
  {
    return m_aPageSize.getHeight () - aObj.getOutlineTop ();
  }

  /**
   * @return The y-top of the page excluding top padding, top-border and
   *         top-margin
   */
  public float getYTop ()
  {
    return _getYTop (this);
  }

  @Nonnull
  public EChange visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    EChange ret = EChange.UNCHANGED;
    aVisitor.onPageSetStart (this);
    if (m_bDifferentFirstPageHeader && m_aFirstPageHeader != null)
      ret = ret.or (m_aFirstPageHeader.visit (aVisitor));
    if (m_aPageHeader != null)
      ret = ret.or (m_aPageHeader.visit (aVisitor));
    if (m_bDifferentFirstPageFooter && m_aFirstPageFooter != null)
      ret = ret.or (m_aFirstPageFooter.visit (aVisitor));
    if (m_aPageFooter != null)
      ret = ret.or (m_aPageFooter.visit (aVisitor));
    for (final IPLRenderableObject <?> aElement : m_aElements)
      ret = ret.or (aElement.visit (aVisitor));
    aVisitor.onPageSetEnd (this);
    return ret;
  }

  @Nonnull
  public PLPageSetPrepareResult prepareAllPages (@Nonnull final PreparationContextGlobal aGlobalCtx)
  {
    // The result element
    final PLPageSetPrepareResult ret = new PLPageSetPrepareResult ();

    // By default first page is identical to all other pages
    final PLMarginBorderPadding aFirstPageMBP = new PLMarginBorderPadding (m_aMargin, m_aPadding, m_aBorder);

    // Prepare first page header
    if (m_bDifferentFirstPageHeader && m_aFirstPageHeader != null)
    {
      // Page header does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx,
                                                              m_aPageSize.getWidth () - aFirstPageMBP.getMarginXSum (),
                                                              aFirstPageMBP.getMarginTop ());

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this,
                                 "Start preparing first page header on width=" +
                                       aRPC.getAvailableWidth () +
                                       " and height=" +
                                       aRPC.getAvailableHeight ());

      final SizeSpec aElementSize = m_aFirstPageHeader.prepare (aRPC);
      // Remember largest height
      ret.setFirstHeaderHeight (aElementSize.getHeight ());

      final float fEffectiveHeaderHeight = aElementSize.getHeight () + m_aFirstPageHeader.getOutlineYSum ();
      if (fEffectiveHeaderHeight > aFirstPageMBP.getMarginTop ())
      {
        // If the height of the header exceeds the available top-margin, modify
        // the margin so that the header fits!
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("PageSet margin top was changed from " +
                       aFirstPageMBP.getMarginTop () +
                       " to " +
                       fEffectiveHeaderHeight +
                       " so that firstPageHeader fits!");
        aFirstPageMBP.setMarginTop (fEffectiveHeaderHeight);
      }
    }

    // Prepare default page header
    if (m_aPageHeader != null)
    {
      // Page header does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx, m_aPageSize.getWidth () - getMarginXSum (), getMarginTop ());

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this,
                                 "Start preparing page header on width=" +
                                       aRPC.getAvailableWidth () +
                                       " and height=" +
                                       aRPC.getAvailableHeight ());

      final SizeSpec aElementSize = m_aPageHeader.prepare (aRPC);
      // Remember largest height
      ret.setHeaderHeight (aElementSize.getHeight ());

      final float fEffectiveHeaderHeight = aElementSize.getHeight () + m_aPageHeader.getOutlineYSum ();
      if (fEffectiveHeaderHeight > getMarginTop ())
      {
        // If the height of the header exceeds the available top-margin, modify
        // the margin so that the header fits!
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("PageSet margin top was changed from " +
                       getMarginTop () +
                       " to " +
                       fEffectiveHeaderHeight +
                       " so that pageHeader fits!");
        setMarginTop (fEffectiveHeaderHeight);
      }
    }

    // Prepare first page footer
    if (m_bDifferentFirstPageFooter && m_aFirstPageFooter != null)
    {
      // Page footer does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx,
                                                              m_aPageSize.getWidth () - aFirstPageMBP.getMarginXSum (),
                                                              aFirstPageMBP.getMarginBottom ());

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this,
                                 "Start preparing first page footer on width=" +
                                       aRPC.getAvailableWidth () +
                                       " and height=" +
                                       aRPC.getAvailableHeight ());

      final SizeSpec aElementSize = m_aFirstPageFooter.prepare (aRPC);
      // Remember largest height
      ret.setFirstFooterHeight (aElementSize.getHeight ());

      final float fEffectiveFooterHeight = aElementSize.getHeight () + m_aFirstPageFooter.getOutlineYSum ();
      if (fEffectiveFooterHeight > aFirstPageMBP.getMarginBottom ())
      {
        // If the height of the footer exceeds the available bottom-margin,
        // modify the margin so that the footer fits!
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("PageSet margin bottom was changed from " +
                       aFirstPageMBP.getMarginBottom () +
                       " to " +
                       fEffectiveFooterHeight +
                       " so that firstPageFooter fits!");
        aFirstPageMBP.setMarginBottom (fEffectiveFooterHeight);
      }
    }

    // Prepare default page footer
    if (m_aPageFooter != null)
    {
      // Page footer does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx, m_aPageSize.getWidth () - getMarginXSum (), getMarginBottom ());

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this,
                                 "Start preparing page footer on width=" +
                                       aRPC.getAvailableWidth () +
                                       " and height=" +
                                       aRPC.getAvailableHeight ());

      final SizeSpec aElementSize = m_aPageFooter.prepare (aRPC);
      // Remember largest height
      ret.setFooterHeight (aElementSize.getHeight ());

      final float fEffectiveFooterHeight = aElementSize.getHeight () + m_aPageFooter.getOutlineYSum ();
      if (fEffectiveFooterHeight > getMarginBottom ())
      {
        // If the height of the footer exceeds the available bottom-margin,
        // modify the margin so that the footer fits!
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("PageSet margin bottom was changed from " +
                       getMarginBottom () +
                       " to " +
                       fEffectiveFooterHeight +
                       " so that pageFooter fits!");
        setMarginBottom (fEffectiveFooterHeight);
      }
    }

    if (aFirstPageMBP.getMarginYSum () > m_aPageSize.getHeight ())
      throw new IllegalStateException ("First page header and footer together (" +
                                       aFirstPageMBP.getMarginYSum () +
                                       ") take more height than available on the page (" +
                                       m_aPageSize.getHeight () +
                                       ")! Cannot render!");
    if (getMarginYSum () > m_aPageSize.getHeight ())
      throw new IllegalStateException ("Header and footer together (" +
                                       getMarginYSum () +
                                       ") take more height than available on the page (" +
                                       m_aPageSize.getHeight () +
                                       ")! Cannot render!");

    ret.setFirstPageMBP (aFirstPageMBP);

    // Prepare all elements
    {
      // For splitting reasons use the smaller height between first and other
      // pages. This is not really nice, but here we don't have a page
      // assignment yet.
      // The width should be identical anyway
      final float fAvailWidth = Math.min (_getAvailableWidth (aFirstPageMBP), _getAvailableWidth (this));
      final float fAvailHeight = Math.min (_getAvailableHeight (aFirstPageMBP), _getAvailableHeight (this));

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this,
                                 "Start preparing elements on width=" +
                                       fAvailWidth +
                                       "+" +
                                       getOutlineXSum () +
                                       " and height=" +
                                       fAvailHeight +
                                       "+" +
                                       getOutlineYSum ());

      // Prepare content elements
      // Must be done after header and footer, because the pageset margins may
      // have been adopted!
      for (final IPLRenderableObject <?> aElement : m_aElements)
      {
        final PreparationContext aRPC = new PreparationContext (aGlobalCtx, fAvailWidth, fAvailHeight);
        final SizeSpec aElementPreparedSize = aElement.prepare (aRPC);
        ret.addElement (new PLElementWithSize (aElement, aElementPreparedSize));
      }

      if (PLDebugLog.isDebugPrepare ())
        PLDebugLog.debugPrepare (this, "Finished preparing elements");
    }

    // Split into pieces that fit onto a page
    // final float fYTop = getYTop ();
    // final float fYLeast = getOutlineBottom ();

    {
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this, "Start splitting elements");

      ICommonsList <PLElementWithSize> aCurPageElements = new CommonsArrayList <> ();

      // Start at the top of the first page
      float fCurY = _getYTop (aFirstPageMBP);

      // Create a copy of the list, so that we can safely modify it
      final ICommonsList <PLElementWithSize> aElementsWithSize = ret.getAllElements ();
      while (aElementsWithSize.isNotEmpty ())
      {
        // Use the first element
        final PLElementWithSize aElementWithSize = aElementsWithSize.remove (0);
        final IPLRenderableObject <?> aElement = aElementWithSize.getElement ();

        boolean bIsPagebreakDesired = aElement instanceof PLPageBreak;
        if (bIsPagebreakDesired && aCurPageElements.isEmpty () && !((PLPageBreak) aElement).isForcePageBreak ())
        {
          // a new page was just started and no forced break is present, so no
          // page break is necessary
          bIsPagebreakDesired = false;
        }

        final float fElementPreparedWidth = aElementWithSize.getWidth ();
        final float fElementHeightFull = aElementWithSize.getHeightFull ();
        // First or other page?
        final float fYLeast = (ret.getPageCount () == 0 ? aFirstPageMBP : this).getOutlineBottom ();
        final float fAvailableHeight = fCurY - fYLeast;
        if (fCurY - fElementHeightFull < fYLeast || bIsPagebreakDesired)
        {
          // Element does not fit on page - try to split
          final boolean bIsVertSplittable = aElement.isVertSplittable ();
          if (bIsVertSplittable)
          {
            // split elements
            final float fSplitHeight = fAvailableHeight - aElement.getOutlineYSum ();
            if (fSplitHeight > 0)
            {
              if (PLDebugLog.isDebugSplit ())
                PLDebugLog.debugSplit (this,
                                       "Trying to split " +
                                             aElement.getDebugID () +
                                             " into pieces for available width " +
                                             fElementPreparedWidth +
                                             " and height " +
                                             fSplitHeight);

              final PLSplitResult aSplitResult = aElement.getAsSplittable ().splitElementVert (fElementPreparedWidth, fSplitHeight);
              if (aSplitResult != null)
                assert fSplitHeight > 0;
              if (fSplitHeight <= 0)
                assert aSplitResult == null;

              if (aSplitResult != null)
              {
                // Re-add them to the list and try again (they may be splitted
                // recursively)
                aElementsWithSize.add (0, aSplitResult.getFirstElement ());
                aElementsWithSize.add (1, aSplitResult.getSecondElement ());

                if (PLDebugLog.isDebugSplit ())
                {
                  PLDebugLog.debugSplit (this,
                                         "Split " +
                                               aElement.getDebugID () +
                                               " into pieces: " +
                                               aSplitResult.getFirstElement ().getElement ().getDebugID () +
                                               " (" +
                                               aSplitResult.getFirstElement ().getWidth () +
                                               "+" +
                                               aSplitResult.getFirstElement ().getElement ().getOutlineXSum () +
                                               " & " +
                                               aSplitResult.getFirstElement ().getHeight () +
                                               "+" +
                                               aSplitResult.getFirstElement ().getElement ().getOutlineYSum () +
                                               ") and " +
                                               aSplitResult.getSecondElement ().getElement ().getDebugID () +
                                               " (" +
                                               aSplitResult.getSecondElement ().getWidth () +
                                               "+" +
                                               aSplitResult.getSecondElement ().getElement ().getOutlineXSum () +
                                               " & " +
                                               aSplitResult.getSecondElement ().getHeight () +
                                               "+" +
                                               aSplitResult.getSecondElement ().getElement ().getOutlineYSum () +
                                               ")");
                }

                // Try to fit resulting split pieces onto page
                continue;
              }
              if (PLDebugLog.isDebugSplit ())
              {
                PLDebugLog.debugSplit (this,
                                       "The single element " +
                                             aElement.getDebugID () +
                                             " does not fit onto a single page (" +
                                             fSplitHeight +
                                             ") even though it is vertically splittable!");
              }
            } // splitHeight > 0
          }

          // Next page
          if (aCurPageElements.isEmpty ())
          {
            if (!bIsPagebreakDesired)
            {
              // one element too large for a page
              if (LOGGER.isWarnEnabled ())
                LOGGER.warn ("The single element " +
                             aElement.getDebugID () +
                             " does not fit onto a single page" +
                             (bIsVertSplittable ? " even though it is vertically splittable!" : " and is not vertically splittable!"));
            }
          }
          else
          {
            // We found elements fitting onto a page (at least one)
            if (LOGGER.isDebugEnabled ())
              LOGGER.debug ("Adding " + aCurPageElements.size () + " elements to page " + ret.getPageNumber ());

            if (PLDebugLog.isDebugPrepare ())
            {
              final ICommonsList <String> aLastPageContent = new CommonsArrayList <> (aCurPageElements, x -> x.getElement ().getDebugID ());
              PLDebugLog.debugPrepare (this,
                                       "Finished page " + ret.getPageNumber () + " with: " + StringHelper.getImploded (aLastPageContent));
            }

            // Something on the current page -> start a new page
            ret.addPerPageElements (aCurPageElements);
            aCurPageElements = new CommonsArrayList <> ();

            // Re-add element and continue from start, so that splitting happens
            aElementsWithSize.add (0, aElementWithSize);

            // We have surely left the first page
            // Start at the top again
            fCurY = _getYTop (this);

            // Continue with next element
            continue;
          }
        }

        // Add element to current page (may also be a page break)
        aCurPageElements.add (aElementWithSize);

        // Go down
        fCurY -= fElementHeightFull;
      }

      if (aCurPageElements.isNotEmpty ())
      {
        // Add elements of last page
        if (PLDebugLog.isDebugSplit ())
        {
          final ICommonsList <String> aLastPageContent = new CommonsArrayList <> (aCurPageElements, x -> x.getElement ().getDebugID ());
          PLDebugLog.debugSplit (this,
                                 "Finished last page " +
                                       ret.getPageNumber () +
                                       " with: " +
                                       StringHelper.getImploded (", ", aLastPageContent));
        }

        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Adding " + aCurPageElements.size () + " elements to page " + ret.getPageNumber ());

        ret.addPerPageElements (aCurPageElements);
      }

      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this, "Finished splitting elements");
    }

    return ret;
  }

  /**
   * Render all pages of this layout to the specified PDDocument
   *
   * @param aPrepareResult
   *        The preparation result. May not be <code>null</code>.
   * @param aDoc
   *        The PDDocument. May not be <code>null</code>.
   * @param bCompressPDF
   *        <code>true</code> for create enflated PDF content
   * @param nPageSetIndex
   *        Page set index. Always &ge; 0.
   * @param nPageSetCount
   *        Page set count. Always &ge; 1.
   * @param nTotalPageStartIndex
   *        Total page index. Always &ge; 0.
   * @param nTotalPageCount
   *        Total page count. Always &ge; 1.
   * @throws IOException
   *         In case of render errors
   */
  public void renderAllPages (@Nonnull final PLPageSetPrepareResult aPrepareResult,
                              @Nonnull final PDDocument aDoc,
                              final boolean bCompressPDF,
                              @Nonnegative final int nPageSetIndex,
                              @Nonnegative final int nPageSetCount,
                              @Nonnegative final int nTotalPageStartIndex,
                              @Nonnegative final int nTotalPageCount) throws IOException
  {
    // Start at the left top
    final float fXLeft = getOutlineLeft ();

    int nPageIndex = 0;
    final int nPageCount = aPrepareResult.getPageCount ();
    for (final ICommonsList <PLElementWithSize> aPerPage : aPrepareResult.directGetPerPageElements ())
    {
      final boolean bFirstPage = nPageIndex == 0;
      final IPLHasMarginBorderPadding <?> aMBP = bFirstPage ? aPrepareResult.getFirstPageMBP () : this;
      if (PLDebugLog.isDebugRender ())
        PLDebugLog.debugRender (this,
                                "Start rendering page index " +
                                      nPageIndex +
                                      " (" +
                                      (nTotalPageStartIndex + nPageIndex) +
                                      ") with page size " +
                                      PLDebugLog.getWH (getPageWidth (), getPageHeight ()) +
                                      " and available size " +
                                      PLDebugLog.getWH (_getAvailableWidth (aMBP), _getAvailableHeight (aMBP)));

      // Layout in memory
      final PDPage aPage = new PDPage (m_aPageSize.getAsRectangle ());
      aDoc.addPage (aPage);

      final IPLRenderableObject <?> aPageHeader = bFirstPage && m_bDifferentFirstPageHeader ? m_aFirstPageHeader : m_aPageHeader;
      final IPLRenderableObject <?> aPageFooter = bFirstPage && m_bDifferentFirstPageFooter ? m_aFirstPageFooter : m_aPageFooter;

      {
        final PagePreRenderContext aPreRenderCtx = new PagePreRenderContext (this,
                                                                             aDoc,
                                                                             aPage,
                                                                             nPageSetIndex,
                                                                             nPageSetCount,
                                                                             nPageIndex,
                                                                             nPageCount,
                                                                             nTotalPageStartIndex + nPageIndex,
                                                                             nTotalPageCount);
        if (m_aPRCCustomizer != null)
          m_aPRCCustomizer.customizePreRenderContext (aPreRenderCtx);

        // Call "beforeRender" on all elements
        final IPLVisitor aVisitor = IPLVisitor.createElementVisitor (x -> x.beforeRender (aPreRenderCtx));

        if (aPageHeader != null)
          aPageHeader.visit (aVisitor);

        if (aPageFooter != null)
          aPageFooter.visit (aVisitor);

        for (final PLElementWithSize aElementWithHeight : aPerPage)
          aElementWithHeight.getElement ().visit (aVisitor);
      }

      final PDPageContentStreamWithCache aContentStream = new PDPageContentStreamWithCache (aDoc,
                                                                                            aPage,
                                                                                            PDPageContentStream.AppendMode.OVERWRITE,
                                                                                            bCompressPDF);
      try
      {
        // Page rect before content - debug: red
        {
          final float fLeft = 0 + aMBP.getMarginLeft ();
          final float fTop = m_aPageSize.getHeight () - aMBP.getMarginTop ();
          final float fWidth = m_aPageSize.getWidth () - aMBP.getMarginXSum ();
          final float fHeight = m_aPageSize.getHeight () - aMBP.getMarginYSum ();

          PLRenderHelper.fillAndRenderBorder (this, fLeft, fTop, fWidth, fHeight, aContentStream);
        }

        // Start with the page rectangle
        if (aPageHeader != null)
        {
          // Page header does not care about page padding
          // header top-left
          final float fStartLeft = aMBP.getMarginLeft ();
          final float fStartTop = m_aPageSize.getHeight ();
          final float fWidth = m_aPageSize.getWidth () - aMBP.getMarginXSum ();
          final float fHeight = aPrepareResult.getHeaderHeight (nPageIndex);
          final PageRenderContext aRCtx = new PageRenderContext (ERenderingElementType.PAGE_HEADER,
                                                                 aContentStream,
                                                                 fStartLeft,
                                                                 fStartTop,
                                                                 fWidth,
                                                                 fHeight);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderContext (aRCtx);
          aPageHeader.render (aRCtx);
        }

        float fCurY = _getYTop (aMBP);
        for (final PLElementWithSize aElementWithHeight : aPerPage)
        {
          final IPLRenderableObject <?> aElement = aElementWithHeight.getElement ();
          // Get element extent
          final float fStartLeft = fXLeft;
          final float fStartTop = fCurY;
          final float fWidth = _getAvailableWidth (aMBP);
          final float fHeight = aElementWithHeight.getHeightFull ();

          final PageRenderContext aRCtx = new PageRenderContext (ERenderingElementType.CONTENT_ELEMENT,
                                                                 aContentStream,
                                                                 fStartLeft,
                                                                 fStartTop,
                                                                 fWidth,
                                                                 fHeight);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderContext (aRCtx);
          aElement.render (aRCtx);

          // In
          fCurY -= aElementWithHeight.getHeightFull ();
        }

        if (aPageFooter != null)
        {
          // Page footer does not care about page padding
          // footer top-left
          final float fStartLeft = aMBP.getMarginLeft ();
          final float fStartTop = aMBP.getMarginBottom ();
          final float fWidth = m_aPageSize.getWidth () - aMBP.getMarginXSum ();
          final float fHeight = aPrepareResult.getFooterHeight (nPageIndex);
          final PageRenderContext aRCtx = new PageRenderContext (ERenderingElementType.PAGE_FOOTER,
                                                                 aContentStream,
                                                                 fStartLeft,
                                                                 fStartTop,
                                                                 fWidth,
                                                                 fHeight);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderContext (aRCtx);
          aPageFooter.render (aRCtx);
        }
      }
      finally
      {
        aContentStream.close ();
      }
      ++nPageIndex;
    }
    if (PLDebugLog.isDebugRender ())
      PLDebugLog.debugRender (this, "Finished rendering");
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("PageSize", m_aPageSize)
                            .append ("Margin", m_aMargin)
                            .append ("Padding", m_aPadding)
                            .append ("Border", m_aBorder)
                            .append ("FillColor", m_aFillColor)
                            .appendIfNotNull ("FirstPageHeader", m_aFirstPageHeader)
                            .appendIfNotNull ("PageHeader", m_aPageHeader)
                            .append ("Elements", m_aElements)
                            .appendIfNotNull ("FirstPageFooter", m_aFirstPageFooter)
                            .appendIfNotNull ("PageFooter", m_aPageFooter)
                            .appendIfNotNull ("PRCCustomizer", m_aPRCCustomizer)
                            .appendIfNotNull ("RCCustomizer", m_aRCCustomizer)
                            .getToString ();
  }
}
