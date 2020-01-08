/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.spec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.NotThreadSafe;

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
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.id.IHasID;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.font.api.IFontResource;
import com.helger.pdflayout4.PLDebugLog;

/**
 * Represents an abstract font that is potentially not yet loaded and can be
 * used in multiple documents.<br>
 * Note: cannot be Serializable because {@link PDFont} is not Serializable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PreloadFont implements IHasID <String>, Serializable
{
  private static final int DEFAULT_FALLBACK_CODE_POINT = '?';

  // Must be defined before the standard fonts are registered
  private static final ICommonsOrderedMap <String, PDType1Font> STANDARD_14 = new CommonsLinkedHashMap <> ();
  private static final ICommonsOrderedMap <String, PreloadFont> STANDARD_14_PF = new CommonsLinkedHashMap <> ();

  @Nonnull
  private static PreloadFont _createPredefined (@Nonnull final PDType1Font aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    // Symbol fonts needs a different fallback code point!
    int nFallbackCodePoint;
    if (aFont == PDType1Font.SYMBOL)
      nFallbackCodePoint = '•';
    else
      if (aFont == PDType1Font.ZAPF_DINGBATS)
        nFallbackCodePoint = '✕';
      else
        nFallbackCodePoint = DEFAULT_FALLBACK_CODE_POINT;

    final PreloadFont ret = new PreloadFont (aFont, nFallbackCodePoint);
    STANDARD_14.put (aFont.getBaseFont (), aFont);
    STANDARD_14_PF.put (aFont.getBaseFont (), ret);
    return ret;
  }

  /** PDF built-in font Helvetica regular */
  public static final PreloadFont REGULAR = _createPredefined (PDType1Font.HELVETICA);
  /** PDF built-in font Helvetica bold */
  public static final PreloadFont REGULAR_BOLD = _createPredefined (PDType1Font.HELVETICA_BOLD);
  /** PDF built-in font Helvetica italic */
  public static final PreloadFont REGULAR_ITALIC = _createPredefined (PDType1Font.HELVETICA_OBLIQUE);
  /** PDF built-in font Helvetica bold and italic */
  public static final PreloadFont REGULAR_BOLD_ITALIC = _createPredefined (PDType1Font.HELVETICA_BOLD_OBLIQUE);
  /** PDF built-in font Courier regular */
  public static final PreloadFont MONOSPACE = _createPredefined (PDType1Font.COURIER);
  /** PDF built-in font Courier bold */
  public static final PreloadFont MONOSPACE_BOLD = _createPredefined (PDType1Font.COURIER_BOLD);
  /** PDF built-in font Courier italic */
  public static final PreloadFont MONOSPACE_ITALIC = _createPredefined (PDType1Font.COURIER_OBLIQUE);
  /** PDF built-in font Courier bold and italic */
  public static final PreloadFont MONOSPACE_BOLD_ITALIC = _createPredefined (PDType1Font.COURIER_BOLD_OBLIQUE);
  /** PDF built-in font Times Roman regular */
  public static final PreloadFont TIMES = _createPredefined (PDType1Font.TIMES_ROMAN);
  /** PDF built-in font Times Roman bold */
  public static final PreloadFont TIMES_BOLD = _createPredefined (PDType1Font.TIMES_BOLD);
  /** PDF built-in font Times Roman italic */
  public static final PreloadFont TIMES_ITALIC = _createPredefined (PDType1Font.TIMES_ITALIC);
  /** PDF built-in font Times Roman bold italic */
  public static final PreloadFont TIMES_BOLD_ITALIC = _createPredefined (PDType1Font.TIMES_BOLD_ITALIC);
  /** PDF built-in font Symbol */
  public static final PreloadFont SYMBOL = _createPredefined (PDType1Font.SYMBOL);
  /** PDF built-in font Zapf Dingbats */
  public static final PreloadFont ZAPF_DINGBATS = _createPredefined (PDType1Font.ZAPF_DINGBATS);

  private String m_sID;
  private PDFont m_aFont;
  private IFontResource m_aFontRes;
  private boolean m_bEmbed;
  private int m_nFallbackCodePoint;
  // Status vars
  private transient TrueTypeFont m_aTTF;
  private transient OpenTypeFont m_aOTF;

  private void _parseFontRes () throws IOException
  {
    if (m_aFontRes != null)
      switch (m_aFontRes.getFontType ())
      {
        case TTF:
          if (PLDebugLog.isDebugFont ())
            PLDebugLog.debugFont (m_aFontRes.toString (), "Loading TTF font");
          m_aTTF = new TTFParser ().parse (m_aFontRes.getInputStream ());
          break;
        case OTF:
          if (PLDebugLog.isDebugFont ())
            PLDebugLog.debugFont (m_aFontRes.toString (), "Loading OTF font");
          m_aOTF = new OTFParser ().parse (m_aFontRes.getInputStream ());
          break;
        default:
          throw new IllegalArgumentException ("Cannot parse font resources of type " + m_aFontRes.getFontType ());
      }
  }

  private void readObject (@Nonnull @WillNotClose final ObjectInputStream aOIS) throws IOException,
                                                                                ClassNotFoundException
  {
    m_sID = StreamHelper.readSafeUTF (aOIS);
    final String sBaseFontName = StreamHelper.readSafeUTF (aOIS);
    m_aFont = STANDARD_14.get (sBaseFontName);
    m_aFontRes = (IFontResource) aOIS.readObject ();
    m_bEmbed = aOIS.readBoolean ();
    m_nFallbackCodePoint = aOIS.readInt ();
    _parseFontRes ();
  }

  private void writeObject (@Nonnull @WillNotClose final ObjectOutputStream aOOS) throws IOException
  {
    StreamHelper.writeSafeUTF (aOOS, m_sID);
    StreamHelper.writeSafeUTF (aOOS, m_aFont != null ? m_aFont.getName () : null);
    aOOS.writeObject (m_aFontRes);
    aOOS.writeBoolean (m_bEmbed);
    aOOS.writeInt (m_nFallbackCodePoint);
    // TTF and OTF are not written
  }

  private PreloadFont (@Nonnull final PDFont aFont, final int nFallbackCodePoint)
  {
    ValueEnforcer.notNull (aFont, "Font");
    m_sID = aFont.getName ();
    m_aFont = aFont;
    m_aFontRes = null;
    m_bEmbed = false;
    m_nFallbackCodePoint = nFallbackCodePoint;
  }

  private PreloadFont (@Nonnull final IFontResource aFontRes, final boolean bEmbed) throws IOException
  {
    ValueEnforcer.notNull (aFontRes, "FontResource");
    m_sID = aFontRes.getID ();
    m_aFont = null;
    m_aFontRes = aFontRes;
    m_bEmbed = bEmbed;
    m_nFallbackCodePoint = DEFAULT_FALLBACK_CODE_POINT;
    // Not loaded custom font
    _parseFontRes ();
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

  /**
   * @return THe fallback code point to be used if a character is not contained
   *         in the font. Defaults to '?'.
   */
  public int getFallbackCodePoint ()
  {
    return m_nFallbackCodePoint;
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
           m_bEmbed == rhs.m_bEmbed &&
           m_nFallbackCodePoint == rhs.m_nFallbackCodePoint;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFont)
                                       .append (m_aFontRes)
                                       .append (m_bEmbed)
                                       .append (m_nFallbackCodePoint)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).appendIfNotNull ("Font", m_aFont)
                                       .appendIfNotNull ("FontResource", m_aFontRes)
                                       .append ("Embed", m_bEmbed)
                                       .append ("FallbackCodePoint", m_nFallbackCodePoint)
                                       .getToString ();
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

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, PDType1Font> getAllStandard14Fonts ()
  {
    return STANDARD_14.getClone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, PreloadFont> getAllStandard14PreloadFonts ()
  {
    return STANDARD_14_PF.getClone ();
  }
}
