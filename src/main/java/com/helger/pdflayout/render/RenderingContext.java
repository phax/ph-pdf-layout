/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.string.StringParser;

/**
 * This class contains the context for rendering a single element onto the PDF.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class RenderingContext
{
  private final ERenderingElementType m_eElementType;
  private final PDPageContentStreamWithCache m_aCS;
  private final boolean m_bDebugMode;
  private final float m_fStartLeft;
  private final float m_fStartTop;
  private final float m_fWidth;
  private final float m_fHeight;
  private final Map <String, String> m_aPlaceholders = new LinkedHashMap <String, String> ();

  /**
   * @param aCtx
   *        Context to copy settings from. May not be <code>null</code>.
   * @param fStartLeft
   *        Absolute page x-start position with element x-margin but without
   *        element x-padding
   * @param fStartTop
   *        Absolute page y-start position with element y-margin but without
   *        element y-padding
   * @param fWidth
   *        width without margin but including x-padding of the surrounding
   *        element
   * @param fHeight
   *        height without margin but including y-padding of the surrounding
   *        element
   */
  public RenderingContext (@Nonnull final RenderingContext aCtx,
                           final float fStartLeft,
                           final float fStartTop,
                           final float fWidth,
                           final float fHeight)
  {
    this (aCtx.getElementType (), aCtx.getContentStream (), aCtx.isDebugMode (), fStartLeft, fStartTop, fWidth, fHeight);
    m_aPlaceholders.putAll (aCtx.m_aPlaceholders);
  }

  /**
   * @param eElementType
   *        Element type. May not be <code>null</code>.
   * @param aCS
   *        Page content stream. May not be <code>null</code>.
   * @param bDebugMode
   *        debug mode?
   * @param fStartLeft
   *        Absolute page x-start position with element x-margin but without
   *        element x-padding
   * @param fStartTop
   *        Absolute page y-start position with element y-margin but without
   *        element y-padding
   * @param fWidth
   *        width without margin but including padding of the surrounding
   *        element
   * @param fHeight
   *        width without margin but including padding of the surrounding
   *        element
   */
  public RenderingContext (@Nonnull final ERenderingElementType eElementType,
                           @Nonnull final PDPageContentStreamWithCache aCS,
                           final boolean bDebugMode,
                           final float fStartLeft,
                           final float fStartTop,
                           final float fWidth,
                           final float fHeight)
  {
    ValueEnforcer.notNull (eElementType, "ElementType");
    ValueEnforcer.notNull (aCS, "ContentStream");
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
   * Get the placeholder value with the specified name.
   *
   * @param sName
   *        The name to search. May be <code>null</code>.
   * @return <code>null</code> if no such placeholder exists.
   */
  @Nullable
  public String getPlaceholder (final String sName)
  {
    return m_aPlaceholders.get (sName);
  }

  public int getPlaceholderAsInt (@Nullable final String sName, final int nDefault)
  {
    return StringParser.parseInt (getPlaceholder (sName), nDefault);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getAllPlaceholders ()
  {
    return CollectionHelper.newOrderedMap (m_aPlaceholders);
  }

  @Nonnull
  public RenderingContext setPlaceholder (@Nonnull @Nonempty final String sName, final int nValue)
  {
    return setPlaceholder (sName, Integer.toString (nValue));
  }

  @Nonnull
  public RenderingContext setPlaceholder (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (sValue, "Value");
    m_aPlaceholders.put (sName, sValue);
    return this;
  }

  /**
   * @return Absolute page x-start position with element x-margin but without
   *         element x-padding
   */
  public float getStartLeft ()
  {
    return m_fStartLeft;
  }

  /**
   * @return Absolute page y-start position with element y-margin but without
   *         element y-padding
   */
  public float getStartTop ()
  {
    return m_fStartTop;
  }

  /**
   * @return width without margin but including padding of the surrounding
   *         element
   */
  public float getWidth ()
  {
    return m_fWidth;
  }

  /**
   * @return width without margin but including padding of the surrounding
   *         element
   */
  public float getHeight ()
  {
    return m_fHeight;
  }
}
