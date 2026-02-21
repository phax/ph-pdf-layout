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

  /**
   * @param aCtx
   *        Context to copy settings from. May not be <code>null</code>.
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
    this (aCtx.getElementType (), aCtx.getContentStream (), fStartLeft, fStartTop, fWidth, fHeight);
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
   */
  public PageRenderContext (@NonNull final ERenderingElementType eElementType,
                            @NonNull final PDPageContentStreamWithCache aCS,
                            @Nonnegative final float fStartLeft,
                            @Nonnegative final float fStartTop,
                            @Nonnegative final float fWidth,
                            @Nonnegative final float fHeight)
  {
    ValueEnforcer.notNull (eElementType, "ElementType");
    ValueEnforcer.notNull (aCS, "ContentStream");
    ValueEnforcer.isGE0 (fStartLeft, "StartLeft");
    ValueEnforcer.isGE0 (fStartTop, "StartTop");
    ValueEnforcer.isGE0 (fWidth, "Width");
    ValueEnforcer.isGE0 (fHeight, "Height");
    m_eElementType = eElementType;
    m_aCS = aCS;
    m_fStartLeft = fStartLeft;
    m_fStartTop = fStartTop;
    m_fWidth = fWidth;
    m_fHeight = fHeight;
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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ElementType", m_eElementType)
                                       .append ("ContentStream", m_aCS)
                                       .append ("StartLeft", m_fStartLeft)
                                       .append ("StartTop", m_fStartTop)
                                       .append ("Width", m_fWidth)
                                       .append ("Height", m_fHeight)
                                       .getToString ();
  }
}
