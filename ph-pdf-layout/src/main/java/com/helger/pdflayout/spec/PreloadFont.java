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
package com.helger.pdflayout.spec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.jspecify.annotations.NonNull;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.id.IHasID;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.font.api.IFontResource;
import com.helger.pdflayout.debug.PLDebugLog;

/**
 * Represents an abstract font that is potentially not yet loaded and can be used in multiple
 * documents.<br>
 * Note: {@link PDFont} is not Serializable.
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

  @NonNull
  private static PreloadFont _createPredefined (final Standard14Fonts.@NonNull FontName eFontName)
  {
    ValueEnforcer.notNull (eFontName, "Font");
    // Symbol fonts needs a different fallback code point!
    int nFallbackCodePoint;
    if (eFontName == Standard14Fonts.FontName.SYMBOL)
      nFallbackCodePoint = '•';
    else
      if (eFontName == Standard14Fonts.FontName.ZAPF_DINGBATS)
        nFallbackCodePoint = '✕';
      else
        nFallbackCodePoint = DEFAULT_FALLBACK_CODE_POINT;

    final PDType1Font aFont = new PDType1Font (eFontName);
    final PreloadFont ret = new PreloadFont (aFont, nFallbackCodePoint);
    STANDARD_14.put (aFont.getBaseFont (), aFont);
    STANDARD_14_PF.put (aFont.getBaseFont (), ret);
    return ret;
  }

  /** PDF built-in font Helvetica regular */
  public static final PreloadFont REGULAR = _createPredefined (Standard14Fonts.FontName.HELVETICA);
  /** PDF built-in font Helvetica bold */
  public static final PreloadFont REGULAR_BOLD = _createPredefined (Standard14Fonts.FontName.HELVETICA_BOLD);
  /** PDF built-in font Helvetica italic */
  public static final PreloadFont REGULAR_ITALIC = _createPredefined (Standard14Fonts.FontName.HELVETICA_OBLIQUE);
  /** PDF built-in font Helvetica bold and italic */
  public static final PreloadFont REGULAR_BOLD_ITALIC = _createPredefined (Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);
  /** PDF built-in font Courier regular */
  public static final PreloadFont MONOSPACE = _createPredefined (Standard14Fonts.FontName.COURIER);
  /** PDF built-in font Courier bold */
  public static final PreloadFont MONOSPACE_BOLD = _createPredefined (Standard14Fonts.FontName.COURIER_BOLD);
  /** PDF built-in font Courier italic */
  public static final PreloadFont MONOSPACE_ITALIC = _createPredefined (Standard14Fonts.FontName.COURIER_OBLIQUE);
  /** PDF built-in font Courier bold and italic */
  public static final PreloadFont MONOSPACE_BOLD_ITALIC = _createPredefined (Standard14Fonts.FontName.COURIER_BOLD_OBLIQUE);
  /** PDF built-in font Times Roman regular */
  public static final PreloadFont TIMES = _createPredefined (Standard14Fonts.FontName.TIMES_ROMAN);
  /** PDF built-in font Times Roman bold */
  public static final PreloadFont TIMES_BOLD = _createPredefined (Standard14Fonts.FontName.TIMES_BOLD);
  /** PDF built-in font Times Roman italic */
  public static final PreloadFont TIMES_ITALIC = _createPredefined (Standard14Fonts.FontName.TIMES_ITALIC);
  /** PDF built-in font Times Roman bold italic */
  public static final PreloadFont TIMES_BOLD_ITALIC = _createPredefined (Standard14Fonts.FontName.TIMES_BOLD_ITALIC);
  /** PDF built-in font Symbol */
  public static final PreloadFont SYMBOL = _createPredefined (Standard14Fonts.FontName.SYMBOL);
  /** PDF built-in font Zapf Dingbats */
  public static final PreloadFont ZAPF_DINGBATS = _createPredefined (Standard14Fonts.FontName.ZAPF_DINGBATS);

  private String m_sID;
  private PDFont m_aFont;
  private IFontResource m_aFontRes;
  private boolean m_bEmbed;
  private int m_nFallbackCodePoint;
  private float m_fFontLineHeight;
  // Status vars
  private transient TrueTypeFont m_aTTF;
  private transient OpenTypeFont m_aOTF;

  private void _parseFontRes () throws IOException
  {
    if (m_aFontRes != null)
      switch (m_aFontRes.getFontType ())
      {
        case TTF:
        {
          if (PLDebugLog.isDebugFont ())
            PLDebugLog.debugFont (m_aFontRes.toString (), "Loading TTF font");
          m_aOTF = null;
          m_aTTF = new TTFParser ().parse (new RandomAccessReadBuffer (m_aFontRes.getInputStream ()));

          if (false)
          {
            final float fFactor = 1000.0f / m_aTTF.getHeader ().getUnitsPerEm ();
            System.out.println ("head: " + (m_aTTF.getHeader ().getYMax () - m_aTTF.getHeader ().getYMin ()) * fFactor);
            System.out.println ("hhea: " +
                                (m_aTTF.getHorizontalHeader ().getAscender () -
                                 m_aTTF.getHorizontalHeader ().getDescender () +
                                 m_aTTF.getHorizontalHeader ().getLineGap ()) * fFactor);
            System.out.println ("os2: " +
                                (m_aTTF.getOS2Windows ().getTypoAscender () -
                                 m_aTTF.getOS2Windows ().getTypoDescender () +
                                 m_aTTF.getOS2Windows ().getTypoLineGap ()) * fFactor);
          }
          break;
        }
        case OTF:
        {
          if (PLDebugLog.isDebugFont ())
            PLDebugLog.debugFont (m_aFontRes.toString (), "Loading OTF font");
          m_aTTF = null;
          m_aOTF = new OTFParser ().parse (new RandomAccessReadBuffer (m_aFontRes.getInputStream ()));
          break;
        }
        default:
          throw new IllegalArgumentException ("Cannot parse font resources of type " + m_aFontRes.getFontType ());
      }
  }

  private void readObject (@NonNull @WillNotClose final ObjectInputStream aOIS) throws IOException,
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

  private void writeObject (@NonNull @WillNotClose final ObjectOutputStream aOOS) throws IOException
  {
    StreamHelper.writeSafeUTF (aOOS, m_sID);
    StreamHelper.writeSafeUTF (aOOS, m_aFont != null ? m_aFont.getName () : null);
    aOOS.writeObject (m_aFontRes);
    aOOS.writeBoolean (m_bEmbed);
    aOOS.writeInt (m_nFallbackCodePoint);
    // TTF and OTF are not written
  }

  /**
   * Constructor for a predefined font
   *
   * @param aFont
   *        The font to use. May not be <code>null</code>.
   * @param nFallbackCodePoint
   *        The fallback code point to be used if a character is not contained in the font.
   */
  private PreloadFont (@NonNull final PDFont aFont, final int nFallbackCodePoint)
  {
    ValueEnforcer.notNull (aFont, "Font");
    m_sID = aFont.getName ();
    m_aFont = aFont;
    m_aFontRes = null;
    m_bEmbed = false;
    m_nFallbackCodePoint = nFallbackCodePoint;
    // Font height needs to be determined by the bounding box
    m_fFontLineHeight = -1;
  }

  /**
   * Constructor for a custom font provided as {@link IFontResource}
   *
   * @param aFontRes
   *        The font resource to use. May not be <code>null</code>.
   * @param bEmbed
   *        <code>true</code> to embed the font, <code>false</code> to not embed it.
   * @param nFallbackCodePoint
   *        The fallback code point to be used if a character is not contained in the font.
   * @throws IOException
   *         in case loading the font fails
   */
  private PreloadFont (@NonNull final IFontResource aFontRes, final boolean bEmbed, final int nFallbackCodePoint)
                                                                                                                  throws IOException
  {
    ValueEnforcer.notNull (aFontRes, "FontResource");
    m_sID = aFontRes.getID ();
    m_aFont = null;
    m_aFontRes = aFontRes;
    m_bEmbed = bEmbed;
    m_nFallbackCodePoint = nFallbackCodePoint;
    // Not loaded custom font
    _parseFontRes ();
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Load the {@link PDFont} associated to this preload font. This class uses no caching!
   *
   * @param aDoc
   *        The {@link PDDocument} to which the font should be attached to. May not be
   *        <code>null</code>.
   * @return The loaded font.
   * @throws IOException
   *         In case loading the external file fails
   */
  @NonNull
  public PDFont loadPDFont (@NonNull final PDDocument aDoc) throws IOException
  {
    if (m_aFont != null)
    {
      // Pre-defined font
      return m_aFont;
    }

    final PDFont ret;
    if (m_aTTF != null)
      ret = PDType0Font.load (aDoc, m_aTTF, m_bEmbed);
    else
      if (m_aOTF != null)
        ret = PDType0Font.load (aDoc, m_aOTF, m_bEmbed);
      else
        ret = null;

    if (ret == null)
      throw new IllegalArgumentException ("Cannot load font resources of type " + m_aFontRes.getFontType ());
    return ret;
  }

  /**
   * @return The fallback code point to be used if a character is not contained in the font.
   *         Defaults to '?'.
   */
  public int getFallbackCodePoint ()
  {
    return m_nFallbackCodePoint;
  }

  /**
   * Set the font line height based on the TTF/OTF font resource <code>hhea</code> table. This
   * method is especially helpful for the "Noto" or the "Kurinto" font family. See issue #46 for
   * details.
   *
   * @return ESuccess.SUCCESS if the line height was set, ESuccess.FAILURE if not.
   * @since 7.3.7
   */
  @NonNull
  public ESuccess setUseFontLineHeightFromHHEA ()
  {
    try
    {
      final HeaderTable aHeaderTable = m_aTTF != null ? m_aTTF.getHeader () : m_aOTF != null ? m_aOTF.getHeader ()
                                                                                             : null;
      final HorizontalHeaderTable aHorzHeaderTable = m_aTTF != null ? m_aTTF.getHorizontalHeader () : m_aOTF != null
                                                                                                                     ? m_aOTF.getHorizontalHeader ()
                                                                                                                     : null;
      if (aHeaderTable == null || aHorzHeaderTable == null)
        return ESuccess.FAILURE;

      m_fFontLineHeight = (aHorzHeaderTable.getAscender () -
                           aHorzHeaderTable.getDescender () +
                           aHorzHeaderTable.getLineGap ()) * (1000.0f / aHeaderTable.getUnitsPerEm ());
      if (PLDebugLog.isDebugFont ())
        PLDebugLog.debugFont (m_aFontRes.toString (), "Loaded font has 'hhea' line height " + m_fFontLineHeight);
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to read the 'hhea' table from the font resource", ex);
    }
  }

  /**
   * Set the font line height based on the TTF/OTF font resource <code>os/2</code> table. See issue
   * #46 for details.
   *
   * @return ESuccess.SUCCESS if the line height was set, ESuccess.FAILURE if not.
   * @since 7.3.7
   */
  @NonNull
  public ESuccess setUseFontLineHeightFromOS2 ()
  {
    try
    {
      final HeaderTable aHeaderTable = m_aTTF != null ? m_aTTF.getHeader () : m_aOTF != null ? m_aOTF.getHeader ()
                                                                                             : null;
      final OS2WindowsMetricsTable aOS2Table = m_aTTF != null ? m_aTTF.getOS2Windows () : m_aOTF != null ? m_aOTF
                                                                                                                 .getOS2Windows ()
                                                                                                         : null;
      if (aHeaderTable == null || aOS2Table == null)
        return ESuccess.FAILURE;

      m_fFontLineHeight = (aOS2Table.getTypoAscender () - aOS2Table.getTypoDescender () + aOS2Table.getTypoLineGap ()) *
                          (1000.0f / aHeaderTable.getUnitsPerEm ());
      if (PLDebugLog.isDebugFont ())
        PLDebugLog.debugFont (m_aFontRes.toString (), "Loaded font has 'os/2' line height " + m_fFontLineHeight);
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to read the 'hhea' table from the font resource", ex);
    }
  }

  /**
   * @return The font line height taken from the external font resource <code>hhea</code> table.
   *         This value is &lt; 0 for embedded fonts.
   * @since 7.3.7
   */
  @CheckForSigned
  public float getFontLineHeight ()
  {
    return m_fFontLineHeight;
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
    return new ToStringGenerator (null).append ("ID", m_sID)
                                       .appendIfNotNull ("Font", m_aFont)
                                       .appendIfNotNull ("FontResource", m_aFontRes)
                                       .append ("Embed", m_bEmbed)
                                       .append ("FallbackCodePoint", m_nFallbackCodePoint)
                                       .getToString ();
  }

  /**
   * Create a new {@link PreloadFont} from an existing {@link IFontResource} where the subset cannot
   * be embedded into the resulting PDF.
   *
   * @param aFontRes
   *        The font resource to include. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @throws IllegalArgumentException
   *         If the font could not be loaded.
   */
  @NonNull
  public static PreloadFont createNonEmbedding (@NonNull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    try
    {
      return new PreloadFont (aFontRes, false, DEFAULT_FALLBACK_CODE_POINT);
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Cannot use the passed font resource " + aFontRes, ex);
    }
  }

  /**
   * Create a new {@link PreloadFont} from an existing {@link IFontResource} where the subset can be
   * embedded into the resulting PDF.
   *
   * @param aFontRes
   *        The font resource to include. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @throws IllegalArgumentException
   *         If the font could not be loaded.
   */
  @NonNull
  public static PreloadFont createEmbedding (@NonNull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    try
    {
      return new PreloadFont (aFontRes, true, DEFAULT_FALLBACK_CODE_POINT);
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Cannot use the passed font resource " + aFontRes, ex);
    }
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, PDType1Font> getAllStandard14Fonts ()
  {
    return STANDARD_14.getClone ();
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, PreloadFont> getAllStandard14PreloadFonts ()
  {
    return STANDARD_14_PF.getClone ();
  }
}
