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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.pdflayout.base.IPLObject;
import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * Convenience {@link IPLRenderListener} that records where each element first appeared. Filters
 * out duplicate events from split fragments and from headers/footers, keeping only the first
 * render of each element identified by {@link IPLObject#getOriginalID()}. The resulting map is the
 * natural input for building a table of contents or PDF outline.
 * <p>
 * Usage:
 *
 * <pre>
 * final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ();
 * aPageSet.setRenderListener (aCollector);
 * aPageLayout.renderTo (aOS);
 * for (Map.Entry &lt;String, Location&gt; e : aCollector.getAll ().entrySet ()) {
 *   // e.getKey () is the element's original ID
 *   // e.getValue ().getTotalPageIndex () is the page it landed on
 * }
 * </pre>
 *
 * @author Philip Helger
 * @since 8.1.3
 */
public class PLRenderedElementCollector implements IPLRenderListener
{
  /**
   * A single recorded element placement: where on the document this element ended up.
   */
  public static final class Location
  {
    private final int m_nPageSetIndex;
    private final int m_nPageSetPageIndex;
    private final int m_nTotalPageIndex;
    private final float m_fStartLeft;
    private final float m_fStartTop;
    private final float m_fWidth;
    private final float m_fHeight;

    Location (@NonNull final PageRenderContext aCtx)
    {
      m_nPageSetIndex = aCtx.getPageSetIndex ();
      m_nPageSetPageIndex = aCtx.getPageSetPageIndex ();
      m_nTotalPageIndex = aCtx.getTotalPageIndex ();
      m_fStartLeft = aCtx.getStartLeft ();
      m_fStartTop = aCtx.getStartTop ();
      m_fWidth = aCtx.getWidth ();
      m_fHeight = aCtx.getHeight ();
    }

    @Nonnegative
    public int getPageSetIndex ()
    {
      return m_nPageSetIndex;
    }

    @Nonnegative
    public int getPageSetPageIndex ()
    {
      return m_nPageSetPageIndex;
    }

    @Nonnegative
    public int getTotalPageIndex ()
    {
      return m_nTotalPageIndex;
    }

    public float getStartLeft ()
    {
      return m_fStartLeft;
    }

    public float getStartTop ()
    {
      return m_fStartTop;
    }

    public float getWidth ()
    {
      return m_fWidth;
    }

    public float getHeight ()
    {
      return m_fHeight;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("PageSetIndex", m_nPageSetIndex)
                                         .append ("PageSetPageIndex", m_nPageSetPageIndex)
                                         .append ("TotalPageIndex", m_nTotalPageIndex)
                                         .append ("StartLeft", m_fStartLeft)
                                         .append ("StartTop", m_fStartTop)
                                         .append ("Width", m_fWidth)
                                         .append ("Height", m_fHeight)
                                         .getToString ();
    }
  }

  private final ICommonsOrderedMap <String, Location> m_aLocations = new CommonsLinkedHashMap <> ();
  private boolean m_bIncludeHeaderFooter = false;

  public PLRenderedElementCollector ()
  {}

  /**
   * @return <code>true</code> if events for page headers and page footers are recorded too.
   *         Defaults to <code>false</code>.
   */
  public final boolean isIncludeHeaderFooter ()
  {
    return m_bIncludeHeaderFooter;
  }

  /**
   * Choose whether to record events that originate inside the page header or page footer. Off by
   * default because the typical use case (table of contents from content elements) does not need
   * them.
   *
   * @param bIncludeHeaderFooter
   *        <code>true</code> to include header/footer renders.
   * @return this for chaining
   */
  @NonNull
  public final PLRenderedElementCollector setIncludeHeaderFooter (final boolean bIncludeHeaderFooter)
  {
    m_bIncludeHeaderFooter = bIncludeHeaderFooter;
    return this;
  }

  public void onElementRendered (@NonNull final IPLRenderableObject <?> aElement, @NonNull final PageRenderContext aCtx)
  {
    if (!m_bIncludeHeaderFooter)
    {
      final ERenderingElementType eType = aCtx.getElementType ();
      if (eType == ERenderingElementType.PAGE_HEADER || eType == ERenderingElementType.PAGE_FOOTER)
        return;
    }

    // Only record the first appearance of the original element. Split fragments
    // with isFirstFragment() == false are skipped; same-ID re-renders (should
    // not happen in current PLPageSet, but cheap to guard) are also skipped.
    if (!aElement.isFirstFragment ())
      return;

    final String sOriginalID = aElement.getOriginalID ();
    m_aLocations.computeIfAbsent (sOriginalID, sUnused -> new Location (aCtx));
  }

  /**
   * @param sOriginalID
   *        The unsplit element's ID, i.e. {@link IPLObject#getOriginalID()}.
   * @return The recorded location, or <code>null</code> if no such element rendered.
   */
  @Nullable
  public final Location getLocation (@NonNull @Nonempty final String sOriginalID)
  {
    return m_aLocations.get (sOriginalID);
  }

  /**
   * @return A copy of the collected mapping from original ID to render location, preserving
   *         insertion order (which is render order across pages).
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <String, Location> getAll ()
  {
    return m_aLocations.getClone ();
  }

  /**
   * Discard all collected events. Useful if the same collector is reused for a second render run.
   */
  public final void reset ()
  {
    m_aLocations.clear ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("IncludeHeaderFooter", m_bIncludeHeaderFooter)
                                       .append ("Locations", m_aLocations)
                                       .getToString ();
  }
}
