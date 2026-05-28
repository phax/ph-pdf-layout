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
package com.helger.pdflayout.richtext.run;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Bundles the four font variants needed to render bold/italic combinations in
 * a single rich-text paragraph. When the markup parser flips a bold or italic
 * style flag, the run builder picks the matching variant from this family.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLFontFamily
{
  private final PreloadFont m_aRegular;
  private final PreloadFont m_aBold;
  private final PreloadFont m_aItalic;
  private final PreloadFont m_aBoldItalic;

  public PLFontFamily (@NonNull final PreloadFont aRegular,
                       @NonNull final PreloadFont aBold,
                       @NonNull final PreloadFont aItalic,
                       @NonNull final PreloadFont aBoldItalic)
  {
    ValueEnforcer.notNull (aRegular, "Regular");
    ValueEnforcer.notNull (aBold, "Bold");
    ValueEnforcer.notNull (aItalic, "Italic");
    ValueEnforcer.notNull (aBoldItalic, "BoldItalic");
    m_aRegular = aRegular;
    m_aBold = aBold;
    m_aItalic = aItalic;
    m_aBoldItalic = aBoldItalic;
  }

  /**
   * Convenience: build a family from a single font (no actual bold/italic
   * variants — useful for tests or single-style documents).
   *
   * @param aFont
   *        the single font.
   * @return a family where all four variants are the same font.
   */
  @NonNull
  public static PLFontFamily ofSingle (@NonNull final PreloadFont aFont)
  {
    return new PLFontFamily (aFont, aFont, aFont, aFont);
  }

  @NonNull
  public PreloadFont getRegular ()
  {
    return m_aRegular;
  }

  @NonNull
  public PreloadFont getBold ()
  {
    return m_aBold;
  }

  @NonNull
  public PreloadFont getItalic ()
  {
    return m_aItalic;
  }

  @NonNull
  public PreloadFont getBoldItalic ()
  {
    return m_aBoldItalic;
  }

  /**
   * Picks the variant matching the bold/italic flags.
   *
   * @param bBold
   *        bold flag.
   * @param bItalic
   *        italic flag.
   * @return the matching {@link PreloadFont} variant.
   */
  @NonNull
  public PreloadFont resolve (final boolean bBold, final boolean bItalic)
  {
    if (bBold && bItalic)
      return m_aBoldItalic;
    if (bBold)
      return m_aBold;
    if (bItalic)
      return m_aItalic;
    return m_aRegular;
  }
}
