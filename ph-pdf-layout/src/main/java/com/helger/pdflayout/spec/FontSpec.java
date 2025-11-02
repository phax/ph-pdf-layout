/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.spec;

import java.io.Serializable;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;

/**
 * Defines a text font specification containing the font, the font size and the
 * text color.
 *
 * @author Philip Helger
 */
@NotThreadSafe
@MustImplementEqualsAndHashcode
public class FontSpec implements Serializable
{
  /** The default font color: black */
  public static final PLColor DEFAULT_COLOR = PLColor.BLACK;

  private final PreloadFont m_aPreloadFont;
  private final float m_fFontSize;
  private final PLColor m_aColor;

  /**
   * Constructor with a {@link PreloadFont} and a font size, using the default
   * color {@link #DEFAULT_COLOR}.
   *
   * @param aPreloadFont
   *        Preload font to use. May not be <code>null</code>.
   * @param fFontSize
   *        Font size to use. Must be &gt; 0.
   */
  public FontSpec (@NonNull final PreloadFont aPreloadFont, @Nonnegative final float fFontSize)
  {
    this (aPreloadFont, fFontSize, DEFAULT_COLOR);
  }

  /**
   * Constructor with a {@link PreloadFont}, a font size and a custom color.
   *
   * @param aPreloadFont
   *        Preload font to use. May not be <code>null</code>.
   * @param fFontSize
   *        Font size to use. Must be &gt; 0.
   * @param aColor
   *        The color to use. May not be <code>null</code>.
   * @since 7.2.0
   */
  public FontSpec (@NonNull final PreloadFont aPreloadFont,
                   @Nonnegative final float fFontSize,
                   @NonNull final PLColor aColor)
  {
    ValueEnforcer.notNull (aPreloadFont, "Font");
    ValueEnforcer.isFalse (Float.isNaN (fFontSize), "FontSize may not be NaN");
    ValueEnforcer.isGT0 (fFontSize, "FontSize");
    ValueEnforcer.notNull (aColor, "Color");
    m_aPreloadFont = aPreloadFont;
    m_fFontSize = fFontSize;
    m_aColor = aColor;
  }

  /**
   * @return The font to use. Never <code>null</code>.
   */
  @NonNull
  public final PreloadFont getPreloadFont ()
  {
    return m_aPreloadFont;
  }

  /**
   * @return The ID of the font to use. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  public final String getPreloadFontID ()
  {
    return m_aPreloadFont.getID ();
  }

  /**
   * @return The font size in points. Always &gt; 0.
   */
  @Nonnegative
  public final float getFontSize ()
  {
    return m_fFontSize;
  }

  /**
   * @return The text color to use.
   */
  @NonNull
  public final PLColor getColor ()
  {
    return m_aColor;
  }

  /**
   * Return a clone of this object but with a different font.
   *
   * @param aNewFont
   *        The new font to use. Must not be <code>null</code>.
   * @return this if the fonts are equal - a new object otherwise.
   */
  @NonNull
  public FontSpec getCloneWithDifferentFont (@NonNull final PreloadFont aNewFont)
  {
    ValueEnforcer.notNull (aNewFont, "NewFont");
    if (aNewFont.equals (m_aPreloadFont))
      return this;
    // Don't copy loaded font!
    return new FontSpec (aNewFont, m_fFontSize, m_aColor);
  }

  /**
   * Return a clone of this object but with a different font size.
   *
   * @param fNewFontSize
   *        The new font size to use. Must be &gt; 0.
   * @return this if the font sizes are equal - a new object otherwise.
   */
  @NonNull
  public FontSpec getCloneWithDifferentFontSize (final float fNewFontSize)
  {
    ValueEnforcer.isGT0 (fNewFontSize, "FontSize");
    if (EqualsHelper.equals (fNewFontSize, m_fFontSize))
      return this;
    return new FontSpec (m_aPreloadFont, fNewFontSize, m_aColor);
  }

  /**
   * Return a clone of this object but with a different color.
   *
   * @param aNewColor
   *        The new color to use. May not be <code>null</code>.
   * @return this if the colors are equal - a new object otherwise.
   * @since 7.2.0
   */
  @NonNull
  public FontSpec getCloneWithDifferentColor (@NonNull final PLColor aNewColor)
  {
    ValueEnforcer.notNull (aNewColor, "NewColor");
    if (aNewColor.equals (m_aColor))
      return this;
    return new FontSpec (m_aPreloadFont, m_fFontSize, aNewColor);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FontSpec rhs = (FontSpec) o;
    return m_aPreloadFont.equals (rhs.m_aPreloadFont) &&
           EqualsHelper.equals (m_fFontSize, rhs.m_fFontSize) &&
           m_aColor.equals (rhs.m_aColor);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aPreloadFont).append (m_fFontSize).append (m_aColor).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("PreloadFont", m_aPreloadFont)
                                       .append ("FontSize", m_fFontSize)
                                       .append ("Color", m_aColor)
                                       .getToString ();
  }
}
