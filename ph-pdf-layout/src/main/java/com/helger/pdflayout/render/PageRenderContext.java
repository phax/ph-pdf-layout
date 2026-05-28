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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;

/**
 * This class contains the context for rendering a single element onto the PDF.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PageRenderContext
{
  private final ERenderingElementType m_eElementType;
  private final PDPageContentStreamWithCache m_aCS;
  private final float m_fStartLeft;
  private final float m_fStartTop;
  private final float m_fWidth;
  private final float m_fHeight;
  private final int m_nPageSetIndex;
  private final int m_nPageSetCount;
  private final int m_nPageSetPageIndex;
  private final int m_nPageSetPageCount;
  private final int m_nTotalPageIndex;
  private final int m_nTotalPageCount;
  private final IPLRenderListener m_aRenderListener;

  /**
   * @param aCtx
   *        Context to copy settings from (element type, content stream, page indices and render
   *        listener). May not be <code>null</code>.
   * @param fStartLeft
   *        Absolute page x-start position of the element. Does not contain margin, padding or
   *        border of the element to be rendered.
   * @param fStartTop
   *        Absolute page y-start position of the element. Does not contain margin, padding or
   *        border of the element to be rendered.
   * @param fWidth
   *        Available width determined from the surrounding element
   * @param fHeight
   *        Available height determined from the surrounding element
   */
  public PageRenderContext (@NonNull final PageRenderContext aCtx,
                            @Nonnegative final float fStartLeft,
                            @Nonnegative final float fStartTop,
                            @Nonnegative final float fWidth,
                            @Nonnegative final float fHeight)
  {
    this (aCtx.getElementType (),
          aCtx.getContentStream (),
          fStartLeft,
          fStartTop,
          fWidth,
          fHeight,
          aCtx.getPageSetIndex (),
          aCtx.getPageSetCount (),
          aCtx.getPageSetPageIndex (),
          aCtx.getPageSetPageCount (),
          aCtx.getTotalPageIndex (),
          aCtx.getTotalPageCount (),
          aCtx.getRenderListener ());
  }

  /**
   * @param eElementType
   *        Element type. May not be <code>null</code>.
   * @param aCS
   *        Page content stream. May not be <code>null</code>.
   * @param fStartLeft
   *        Absolute page x-start position of the element. Does not contain margin, padding or
   *        border of the element to be rendered.
   * @param fStartTop
   *        Absolute page y-start position of the element. Does not contain margin, padding or
   *        border of the element to be rendered.
   * @param fWidth
   *        Available width determined from the surrounding element
   * @param fHeight
   *        Available height determined from the surrounding element
   * @param nPageSetIndex
   *        0-based index of the current page set. Always &ge; 0.
   * @param nPageSetCount
   *        Total number of page sets. Always &ge; 0.
   * @param nPageSetPageIndex
   *        0-based index of the page within the current page set. Always &ge; 0.
   * @param nPageSetPageCount
   *        Total number of pages in the current page set. Always &ge; 0.
   * @param nTotalPageIndex
   *        0-based index of the page across all page sets. Always &ge; 0.
   * @param nTotalPageCount
   *        Total number of pages across all page sets. Always &ge; 0.
   * @param aRenderListener
   *        Optional listener that is invoked after every element render. May be <code>null</code>.
   * @since 8.2.0
   */
  public PageRenderContext (@NonNull final ERenderingElementType eElementType,
                            @NonNull final PDPageContentStreamWithCache aCS,
                            @Nonnegative final float fStartLeft,
                            @Nonnegative final float fStartTop,
                            @Nonnegative final float fWidth,
                            @Nonnegative final float fHeight,
                            @Nonnegative final int nPageSetIndex,
                            @Nonnegative final int nPageSetCount,
                            @Nonnegative final int nPageSetPageIndex,
                            @Nonnegative final int nPageSetPageCount,
                            @Nonnegative final int nTotalPageIndex,
                            @Nonnegative final int nTotalPageCount,
                            @Nullable final IPLRenderListener aRenderListener)
  {
    ValueEnforcer.notNull (eElementType, "ElementType");
    ValueEnforcer.notNull (aCS, "ContentStream");
    ValueEnforcer.isGE0 (fStartLeft, "StartLeft");
    ValueEnforcer.isGE0 (fStartTop, "StartTop");
    ValueEnforcer.isGE0 (fWidth, "Width");
    ValueEnforcer.isGE0 (fHeight, "Height");
    ValueEnforcer.isGE0 (nPageSetIndex, "PageSetIndex");
    ValueEnforcer.isGE0 (nPageSetCount, "PageSetCount");
    ValueEnforcer.isGE0 (nPageSetPageIndex, "PageSetPageIndex");
    ValueEnforcer.isGE0 (nPageSetPageCount, "PageSetPageCount");
    ValueEnforcer.isGE0 (nTotalPageIndex, "TotalPageIndex");
    ValueEnforcer.isGE0 (nTotalPageCount, "TotalPageCount");
    m_eElementType = eElementType;
    m_aCS = aCS;
    m_fStartLeft = fStartLeft;
    m_fStartTop = fStartTop;
    m_fWidth = fWidth;
    m_fHeight = fHeight;
    m_nPageSetIndex = nPageSetIndex;
    m_nPageSetCount = nPageSetCount;
    m_nPageSetPageIndex = nPageSetPageIndex;
    m_nPageSetPageCount = nPageSetPageCount;
    m_nTotalPageIndex = nTotalPageIndex;
    m_nTotalPageCount = nTotalPageCount;
    m_aRenderListener = aRenderListener;
  }

  /**
   * @return The type of the element currently rendered. Never <code>null</code> .
   */
  @NonNull
  public ERenderingElementType getElementType ()
  {
    return m_eElementType;
  }

  /**
   * @return The current content stream to write to. Never <code>null</code>.
   */
  @NonNull
  public PDPageContentStreamWithCache getContentStream ()
  {
    return m_aCS;
  }

  /**
   * @return The underlying PDF document. Never <code>null</code>.
   */
  @NonNull
  public PDDocument getDocument ()
  {
    return m_aCS.getDocument ();
  }

  /**
   * @return Absolute page x-start position. Does not contain margin, padding or border of the
   *         element to be rendered.
   */
  @Nonnegative
  public float getStartLeft ()
  {
    return m_fStartLeft;
  }

  /**
   * @return Absolute page y-start position. Does not contain margin, padding or border of the
   *         element to be rendered.
   */
  @Nonnegative
  public float getStartTop ()
  {
    return m_fStartTop;
  }

  /**
   * @return Available width determined from the surrounding element
   */
  @Nonnegative
  public float getWidth ()
  {
    return m_fWidth;
  }

  /**
   * @return Available height determined from the surrounding element
   */
  @Nonnegative
  public float getHeight ()
  {
    return m_fHeight;
  }

  /**
   * @return 0-based index of the current page set. Always &ge; 0.
   * @since 8.2.0
   */
  @Nonnegative
  public int getPageSetIndex ()
  {
    return m_nPageSetIndex;
  }

  /**
   * @return Total number of page sets. Always &ge; 0.
   * @since 8.2.0
   */
  @Nonnegative
  public int getPageSetCount ()
  {
    return m_nPageSetCount;
  }

  /**
   * @return 0-based index of the page within the current page set. Always &ge; 0.
   * @since 8.2.0
   */
  @Nonnegative
  public int getPageSetPageIndex ()
  {
    return m_nPageSetPageIndex;
  }

  /**
   * @return Total number of pages in the current page set. Always &ge; 0.
   * @since 8.2.0
   */
  @Nonnegative
  public int getPageSetPageCount ()
  {
    return m_nPageSetPageCount;
  }

  /**
   * @return 0-based index of the page across all page sets. Always &ge; 0. Use this as the page
   *         number when populating a table of contents or PDF bookmarks.
   * @since 8.2.0
   */
  @Nonnegative
  public int getTotalPageIndex ()
  {
    return m_nTotalPageIndex;
  }

  /**
   * @return Total number of pages across all page sets. Always &ge; 0.
   * @since 8.2.0
   */
  @Nonnegative
  public int getTotalPageCount ()
  {
    return m_nTotalPageCount;
  }

  /**
   * @return The listener invoked after every element render, or <code>null</code> if no listener
   *         is installed for this rendering pass.
   * @since 8.2.0
   */
  @Nullable
  public IPLRenderListener getRenderListener ()
  {
    return m_aRenderListener;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ElementType", m_eElementType)
                                       .append ("ContentStream", m_aCS)
                                       .append ("StartLeft", m_fStartLeft)
                                       .append ("StartTop", m_fStartTop)
                                       .append ("Width", m_fWidth)
                                       .append ("Height", m_fHeight)
                                       .append ("PageSetIndex", m_nPageSetIndex)
                                       .append ("PageSetCount", m_nPageSetCount)
                                       .append ("PageSetPageIndex", m_nPageSetPageIndex)
                                       .append ("PageSetPageCount", m_nPageSetPageCount)
                                       .append ("TotalPageIndex", m_nTotalPageIndex)
                                       .append ("TotalPageCount", m_nTotalPageCount)
                                       .appendIfNotNull ("RenderListener", m_aRenderListener)
                                       .getToString ();
  }
}
