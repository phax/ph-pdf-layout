/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.render;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.pdfbox.PDPageContentStreamWithCache;

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
  private final boolean m_bDebugMode;
  private final float m_fStartLeft;
  private final float m_fStartTop;
  private final float m_fWidth;
  private final float m_fHeight;

  /**
   * @param aCtx
   *        Context to copy settings from. May not be <code>null</code>.
   * @param fStartLeft
   *        Absolute page x-start position of the element. Does not contain
   *        margin, padding or border of the element to be rendered.
   * @param fStartTop
   *        Absolute page y-start position of the element. Does not contain
   *        margin, padding or border of the element to be rendered.
   * @param fWidth
   *        available width determined from the surrounding element
   * @param fHeight
   *        available height determined from the surrounding element
   */
  public PageRenderContext (@Nonnull final PageRenderContext aCtx,
                            final float fStartLeft,
                            final float fStartTop,
                            final float fWidth,
                            final float fHeight)
  {
    this (aCtx.getElementType (),
          aCtx.getContentStream (),
          aCtx.isDebugMode (),
          fStartLeft,
          fStartTop,
          fWidth,
          fHeight);
  }

  /**
   * @param eElementType
   *        Element type. May not be <code>null</code>.
   * @param aCS
   *        Page content stream. May not be <code>null</code>.
   * @param bDebugMode
   *        debug mode?
   * @param fStartLeft
   *        Absolute page x-start position of the element. Does not contain
   *        margin, padding or border of the element to be rendered.
   * @param fStartTop
   *        Absolute page y-start position of the element. Does not contain
   *        margin, padding or border of the element to be rendered.
   * @param fWidth
   *        available width determined from the surrounding element
   * @param fHeight
   *        available height determined from the surrounding element
   */
  public PageRenderContext (@Nonnull final ERenderingElementType eElementType,
                            @Nonnull final PDPageContentStreamWithCache aCS,
                            final boolean bDebugMode,
                            final float fStartLeft,
                            final float fStartTop,
                            final float fWidth,
                            final float fHeight)
  {
    ValueEnforcer.notNull (eElementType, "ElementType");
    ValueEnforcer.notNull (aCS, "ContentStream");
    ValueEnforcer.isGE0 (fStartLeft, "StartLeft");
    ValueEnforcer.isGE0 (fStartTop, "StartTop");
    ValueEnforcer.isGE0 (fWidth, "Width");
    ValueEnforcer.isGE0 (fHeight, "Height");
    m_eElementType = eElementType;
    m_aCS = aCS;
    m_bDebugMode = bDebugMode;
    m_fStartLeft = fStartLeft;
    m_fStartTop = fStartTop;
    m_fWidth = fWidth;
    m_fHeight = fHeight;
  }

  /**
   * @return The type of the element currently rendered. Never <code>null</code>
   *         .
   */
  @Nonnull
  public ERenderingElementType getElementType ()
  {
    return m_eElementType;
  }

  /**
   * @return The current content stream to write to. Never <code>null</code>.
   */
  @Nonnull
  public PDPageContentStreamWithCache getContentStream ()
  {
    return m_aCS;
  }

  /**
   * @return The underlying PDF document. Never <code>null</code>.
   */
  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aCS.getDocument ();
  }

  /**
   * @return <code>true</code> if debug output should be emitted into the PDF,
   *         <code>false</code> otherwise.
   */
  public boolean isDebugMode ()
  {
    return m_bDebugMode;
  }

  /**
   * @return Absolute page x-start position. Does not contain margin, padding or
   *         border of the element to be rendered.
   */
  public float getStartLeft ()
  {
    return m_fStartLeft;
  }

  /**
   * @return Absolute page y-start position. Does not contain margin, padding or
   *         border of the element to be rendered.
   */
  public float getStartTop ()
  {
    return m_fStartTop;
  }

  /**
   * @return available width determined from the surrounding element
   */
  public float getWidth ()
  {
    return m_fWidth;
  }

  /**
   * @return available height determined from the surrounding element
   */
  public float getHeight ()
  {
    return m_fHeight;
  }
}
