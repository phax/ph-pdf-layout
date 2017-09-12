/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import javax.annotation.concurrent.Immutable;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.id.IHasID;
import com.helger.commons.string.ToStringGenerator;
import com.helger.font.api.IFontResource;
import com.helger.pdflayout.PLDebug;

/**
 * Represents an abstract font that is potentially not yet loaded and can be
 * used in multiple documents.<br>
 * Note: cannot be Serializable because {@link PDFont} is not Serializable.
 *
 * @author Philip Helger
 */
@Immutable
public final class PreloadFont implements IHasID <String>
{
  /** PDF built-in font Helvetica regular */
  public static final PreloadFont REGULAR = PreloadFont.createPredefined (PDType1Font.HELVETICA);
  /** PDF built-in font Helvetica bold */
  public static final PreloadFont REGULAR_BOLD = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD);
  /** PDF built-in font Helvetica italic */
  public static final PreloadFont REGULAR_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_OBLIQUE);
  /** PDF built-in font Helvetica bold and italic */
  public static final PreloadFont REGULAR_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD_OBLIQUE);
  /** PDF built-in font Courier regular */
  public static final PreloadFont MONOSPACE = PreloadFont.createPredefined (PDType1Font.COURIER);
  /** PDF built-in font Courier bold */
  public static final PreloadFont MONOSPACE_BOLD = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD);
  /** PDF built-in font Courier italic */
  public static final PreloadFont MONOSPACE_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_OBLIQUE);
  /** PDF built-in font Courier bold and italic */
  public static final PreloadFont MONOSPACE_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD_OBLIQUE);
  /** PDF built-in font Times Roman regular */
  public static final PreloadFont TIMES = PreloadFont.createPredefined (PDType1Font.TIMES_ROMAN);
  /** PDF built-in font Times Roman bold */
  public static final PreloadFont TIMES_BOLD = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD);
  /** PDF built-in font Times Roman italic */
  public static final PreloadFont TIMES_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_ITALIC);
  /** PDF built-in font Times Roman bold italic */
  public static final PreloadFont TIMES_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD_ITALIC);
  /** PDF built-in font Symbol */
  public static final PreloadFont SYMBOL = PreloadFont.createPredefined (PDType1Font.SYMBOL);
  /** PDF built-in font Zapf Dingbats */
  public static final PreloadFont ZAPF_DINGBATS = PreloadFont.createPredefined (PDType1Font.ZAPF_DINGBATS);

  private final String m_sID;
  private final PDFont m_aFont;
  private final IFontResource m_aFontRes;
  private final boolean m_bEmbed;
  // Status vars
  private TrueTypeFont m_aTTF;
  private OpenTypeFont m_aOTF;

  private PreloadFont (@Nonnull final PDFont aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    m_sID = aFont.getName ();
    m_aFont = aFont;
    m_aFontRes = null;
    m_bEmbed = false;
  }

  private PreloadFont (@Nonnull final IFontResource aFontRes, final boolean bEmbed) throws IOException
  {
    ValueEnforcer.notNull (aFontRes, "FontResource");
    m_sID = aFontRes.getID ();
    m_aFont = null;
    m_aFontRes = aFontRes;
    m_bEmbed = bEmbed;
    // Not loaded custom font
    switch (aFontRes.getFontType ())
    {
      case TTF:
        if (PLDebug.isDebugFont ())
          PLDebug.debugFont (aFontRes.toString (), "Loading TTF font");
        m_aTTF = new TTFParser ().parse (aFontRes.getInputStream ());
        break;
      case OTF:
        if (PLDebug.isDebugFont ())
          PLDebug.debugFont (aFontRes.toString (), "Loading OTF font");
        m_aOTF = new OTFParser ().parse (aFontRes.getInputStream ());
        break;
      default:
        throw new IllegalArgumentException ("Cannot parse font resources of type " + aFontRes.getFontType ());
    }
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Load the {@link PDFont} associated to this preload font. This class uses no
   * caching!
   *
   * @param aDoc
   *        The {@link PDDocument} to which the font should be attached to. May
   *        not be <code>null</code>.
   * @return The loaded font.
   * @throws IOException
   *         In case loading the external file fails
   */
  @Nonnull
  public PDFont loadPDFont (@Nonnull final PDDocument aDoc) throws IOException
  {
    if (m_aFont != null)
    {
      // Pre-defined font
      return m_aFont;
    }

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

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PreloadFont rhs = (PreloadFont) o;
    return EqualsHelper.equals (m_aFont, rhs.m_aFont) &&
           EqualsHelper.equals (m_aFontRes, rhs.m_aFontRes) &&
           m_bEmbed == rhs.m_bEmbed;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFont).append (m_aFontRes).append (m_bEmbed).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).appendIfNotNull ("Font", m_aFont)
                                       .appendIfNotNull ("FontResource", m_aFontRes)
                                       .append ("Embed", m_bEmbed)
                                       .getToString ();
  }

  @Nonnull
  public static PreloadFont createPredefined (@Nonnull final PDType1Font aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    return new PreloadFont (aFont);
  }

  @Nonnull
  public static PreloadFont createNonEmbedding (@Nonnull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    try
    {
      return new PreloadFont (aFontRes, false);
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Cannot use the passed font resource " + aFontRes, ex);
    }
  }

  @Nonnull
  public static PreloadFont createEmbedding (@Nonnull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    try
    {
      return new PreloadFont (aFontRes, true);
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Cannot use the passed font resource " + aFontRes, ex);
    }
  }
}
