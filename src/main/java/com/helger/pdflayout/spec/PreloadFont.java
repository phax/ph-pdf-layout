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
package com.helger.pdflayout.spec;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.font.api.IFontResource;

/**
 * Represents an abstract font that is potentially not yet loaded and can be
 * used in multiple documents.
 *
 * @author Philip Helger
 */
@Immutable
public final class PreloadFont
{
  public static final PreloadFont REGULAR = PreloadFont.createPredefined (PDType1Font.HELVETICA);
  public static final PreloadFont REGULAR_BOLD = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD);
  public static final PreloadFont REGULAR_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_OBLIQUE);
  public static final PreloadFont REGULAR_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD_OBLIQUE);
  public static final PreloadFont MONOSPACE = PreloadFont.createPredefined (PDType1Font.COURIER);
  public static final PreloadFont MONOSPACE_BOLD = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD);
  public static final PreloadFont MONOSPACE_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_OBLIQUE);
  public static final PreloadFont MONOSPACE_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD_OBLIQUE);
  public static final PreloadFont TIMES = PreloadFont.createPredefined (PDType1Font.TIMES_ROMAN);
  public static final PreloadFont TIMES_BOLD = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD);
  public static final PreloadFont TIMES_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_ITALIC);
  public static final PreloadFont TIMES_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD_ITALIC);
  public static final PreloadFont SYMBOL = PreloadFont.createPredefined (PDType1Font.SYMBOL);
  public static final PreloadFont ZAPF_DINGBATS = PreloadFont.createPredefined (PDType1Font.ZAPF_DINGBATS);

  private static final Logger s_aLogger = LoggerFactory.getLogger (PreloadFont.class);

  private final PDFont m_aFont;
  private final IFontResource m_aFontRes;
  private TrueTypeFont m_aTTF;
  private OpenTypeFont m_aOTF;
  private final boolean m_bEmbed = true;

  private PreloadFont (@Nullable final PDFont aFont, @Nullable final IFontResource aFontRes)
  {
    m_aFont = aFont;
    m_aFontRes = aFontRes;
  }

  private void _parseFontResource () throws IOException
  {
    if (m_aFontRes != null)
    {
      // Not loaded custom font
      switch (m_aFontRes.getFontType ())
      {
        case TTF:
          s_aLogger.info ("Loading TTF font " + m_aFontRes);
          m_aTTF = new TTFParser ().parse (m_aFontRes.getInputStream ());
          break;
        case OTF:
          s_aLogger.info ("Loading OTF font " + m_aFontRes);
          m_aOTF = new OTFParser ().parse (m_aFontRes.getInputStream ());
          break;
        default:
          throw new IllegalArgumentException ("Cannot parse font resources of type " + m_aFontRes.getFontType ());
      }
    }
  }

  /**
   * Load the PDFont associated to this font, without caching the result in this
   * class.
   *
   * @param aDoc
   *        The {@link PDDocument} to which the font should be attached to.
   * @return The loaded font.
   * @throws IOException
   *         In case loading the external file fails
   */
  @Nonnull
  public PDFont loadPDFont (@Nonnull final PDDocument aDoc) throws IOException
  {
    if (m_aFontRes != null)
    {
      PDFont ret = null;
      if (m_aTTF != null)
        ret = PDType0Font.load (aDoc, m_aTTF, m_bEmbed);
      else
        if (m_aOTF != null)
          ret = PDType0Font.load (aDoc, m_aOTF, m_bEmbed);

      if (ret == null)
        throw new IllegalArgumentException ("Cannot load font resources of type " + m_aFontRes.getFontType ());
      return ret;
    }
    return m_aFont;
  }

  @Nonnull
  public static PreloadFont createPredefined (@Nonnull final PDFont aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    return new PreloadFont (aFont, null);
  }

  @Nonnull
  public static PreloadFont createEmbedding (@Nonnull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    final PreloadFont ret = new PreloadFont (null, aFontRes);
    try
    {
      ret._parseFontResource ();
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Cannot use the passed font resource " + aFontRes, ex);
    }
    return ret;
  }
}
