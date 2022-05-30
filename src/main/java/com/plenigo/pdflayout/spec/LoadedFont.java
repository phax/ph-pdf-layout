/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.spec;

import com.helger.collection.map.IntFloatMap;
import com.helger.collection.map.IntObjectMap;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.plenigo.pdflayout.PLConvert;
import com.plenigo.pdflayout.debug.PLDebugLog;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontHelper;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents a wrapper around a {@link PDFont} that is uniquely
 * assigned to a PDDocument.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class LoadedFont
{
  private static final class EncodedCodePoint implements Serializable
  {
    private final int m_nCodePoint;
    private final byte [] m_aEncoded;
    // Lazy inited
    private Integer m_aEncodedValue;

    private static int _toInt (@Nonnull final byte [] aEncoded)
    {
      int ret = 0;
      for (final byte b : aEncoded)
      {
        ret <<= 8;
        ret |= (b + 256) % 256;
      }
      return ret;
    }

    private EncodedCodePoint (final int nCodePoint, @Nonnull final byte [] aEncoded)
    {
      m_nCodePoint = nCodePoint;
      m_aEncoded = aEncoded;
    }

    /**
     * @return The effective code point use.
     */
    public int getCodePoint ()
    {
      return m_nCodePoint;
    }

    public void writeEncodedBytes (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
    {
      aOS.write (m_aEncoded);
    }

    public int getEncodedIntValue ()
    {
      Integer ret = m_aEncodedValue;
      if (ret == null)
      {
        // Lazy init
        ret = m_aEncodedValue = Integer.valueOf (_toInt (m_aEncoded));
      }
      return ret.intValue ();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (LoadedFont.class);

  /** The underlying PDFBox font */
  private final PDFont m_aFont;
  /**
   * The fallback character to be used in case an unmappable character is
   * contained
   */
  private final int m_nFallbackCodePoint;
  // Status vars
  private final float m_fBBHeight;
  private final float m_fDescent;
  private final boolean m_bFontWillBeSubset;
  private final IntObjectMap <EncodedCodePoint> m_aEncodedCodePointCache = new IntObjectMap <> ();
  private final IntFloatMap m_aCodePointWidthCache = new IntFloatMap ();

  public LoadedFont (@Nonnull final PDFont aFont, final int nFallbackCodePoint)
  {
    ValueEnforcer.notNull (aFont, "Font");
    m_aFont = aFont;
    m_nFallbackCodePoint = nFallbackCodePoint;

    PDFontDescriptor aFD = aFont.getFontDescriptor ();
    if (aFD == null)
    {
      if (aFont instanceof PDType0Font)
      {
        final PDCIDFont aDescendantFont = ((PDType0Font) aFont).getDescendantFont ();
        if (aDescendantFont != null)
          aFD = aDescendantFont.getFontDescriptor ();
      }
    }
    if (aFD == null)
      throw new IllegalArgumentException ("Failed to determine FontDescriptor from specified font " + aFont);

    m_fBBHeight = aFD.getFontBoundingBox ().getHeight ();
    m_fDescent = aFD.getDescent ();
    m_bFontWillBeSubset = m_aFont.willBeSubset ();
  }

  /**
   * @return The underlying font. Never <code>null</code>.
   */
  @Nonnull
  public final PDFont getFont ()
  {
    return m_aFont;
  }

  @Nonnegative
  public final float getDescent (@Nonnegative final float fFontSize)
  {
    return PLConvert.getWidthForFontSize (m_fDescent, fFontSize);
  }

  @Nonnegative
  public final float getTextHeight (@Nonnegative final float fFontSize)
  {
    return PLConvert.getWidthForFontSize (m_fBBHeight, fFontSize);
  }

  @Nonnull
  public static EncodedCodePoint encodeCodepointWithFallback (@Nonnull final PDFont aFont,
                                                              final int nCodepoint,
                                                              final int nFallbackCodepoint) throws IOException
  {
    try
    {
      // multi-byte encoding with 1 to 4 bytes
      final byte [] aEncodedBytes = PDFontHelper.encode (aFont, nCodepoint);
      return new EncodedCodePoint (nCodepoint, aEncodedBytes);
    }
    catch (final IllegalArgumentException ex)
    {
      if (PLDebugLog.isDebugFont ())
        PLDebugLog.debugFont (aFont.toString (), "No code point " + nCodepoint + " in this font - " + ex.getMessage ());

      try
      {
        // Use fallback code point
        final byte [] aEncodedBytes = PDFontHelper.encode (aFont, nFallbackCodepoint);
        return new EncodedCodePoint (nFallbackCodepoint, aEncodedBytes);
      }
      catch (final IllegalArgumentException ex2)
      {
        if (PLDebugLog.isDebugFont ())
          PLDebugLog.debugFont (aFont.toString (), "No fallback code point " + nFallbackCodepoint + " in this font - " + ex2.getMessage ());
        throw ex2;
      }
    }
  }

  @Nonnull
  private EncodedCodePoint _getEncodedCodePoint (final int nCodePoint) throws IOException
  {
    EncodedCodePoint aECP = m_aEncodedCodePointCache.get (nCodePoint);
    if (aECP == null)
    {
      // Encode code point according to the font rules
      aECP = encodeCodepointWithFallback (m_aFont, nCodePoint, m_nFallbackCodePoint);
      // put in cache
      m_aEncodedCodePointCache.put (nCodePoint, aECP);
    }
    return aECP;
  }

  private float _getCodePointWidth (final int nCodePoint) throws IOException
  {
    float fWidth = m_aCodePointWidthCache.get (nCodePoint, -1f);
    if (fWidth < 0)
    {
      // Get encoded code point (from its own cache)
      final EncodedCodePoint aECP = _getEncodedCodePoint (nCodePoint);

      // Get width of encoded value
      fWidth = m_aFont.getWidth (aECP.getEncodedIntValue ());

      // Map code point to width to save encoding
      m_aCodePointWidthCache.put (nCodePoint, fWidth);
    }
    return fWidth;
  }

  @Nonnegative
  public float getStringWidth (@Nonnull final String sText, @Nonnegative final float fFontSize) throws IOException
  {
    if (false)
    {
      // Toooo slow
      return PLConvert.getWidthForFontSize (m_aFont.getStringWidth (sText), fFontSize);
    }

    float fWidth = 0;

    // Iterate on code point basis
    int nCPOfs = 0;
    final int nLength = sText.length ();
    while (nCPOfs < nLength)
    {
      final int nCP = sText.codePointAt (nCPOfs);
      nCPOfs += Character.charCount (nCP);

      // Use code point cache for maximum performance
      fWidth += _getCodePointWidth (nCP);
    }

    // The width is in 1000 unit of text space, ie 333 or 777
    return PLConvert.getWidthForFontSize (fWidth, fFontSize);
  }

  /**
   * A quick version to encode the passed text so that it can be written with
   * <code>COSWriter.writeString</code>
   *
   * @param sText
   *        Text to be written.
   * @return The byte array that can be written with the COSWrite. Never
   *         <code>null</code>.
   * @throws IOException
   *         In case something goes wrong
   */
  @Nonnull
  public byte [] getEncodedForPageContentStream (@Nonnull final String sText) throws IOException
  {
    // Minimum is 1*string length
    // Maximum is 4*string length
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (sText.length () * 2))
    {
      int nCPOfs = 0;
      while (nCPOfs < sText.length ())
      {
        final int nCP = sText.codePointAt (nCPOfs);
        nCPOfs += Character.charCount (nCP);

        final EncodedCodePoint aECP = _getEncodedCodePoint (nCP);
        if (m_bFontWillBeSubset)
          m_aFont.addToSubset (aECP.getCodePoint ());
        aECP.writeEncodedBytes (aBAOS);
      }
      return aBAOS.toByteArray ();
    }
  }

  private void _getLineFitToWidthForward (@Nonnull final String sLine,
                                          @Nonnegative final float fFontSize,
                                          @Nonnegative final float fMaxWidth,
                                          @Nonnull final List <TextAndWidthSpec> ret) throws IOException
  {
    String sCurLine = sLine;
    float fSumWidth = 0f;
    int nCodePointOffset = 0;
    float fSumWidthOfLastWhitespace = 0f;
    int nCodePointOffsetOfLastWhitespace = 0;
    boolean bWarnedOnTooSmallMaxWidth = false;

    // For each code point
    while (nCodePointOffset < sCurLine.length ())
    {
      final int nCodePoint = sCurLine.codePointAt (nCodePointOffset);
      final float fCodePointWidth = PLConvert.getWidthForFontSize (_getCodePointWidth (nCodePoint), fFontSize);

      if (Character.isWhitespace (nCodePoint))
      {
        // Whitespace is considered a word break and allows us to break the line
        // here, so remember it before the increment
        nCodePointOffsetOfLastWhitespace = nCodePointOffset;
        fSumWidthOfLastWhitespace = fSumWidth;
      }

      final float fNewWidth = fSumWidth + fCodePointWidth;

      boolean bSplitNow = fNewWidth > fMaxWidth;
      if (bSplitNow && nCodePointOffset == 0)
      {
        if (!bWarnedOnTooSmallMaxWidth)
        {
          if (LOGGER.isWarnEnabled ())
            LOGGER.warn ("The provided max width (" +
                         fMaxWidth +
                         ") is too small to hold a single character! Will create an overlap! Problem string=<" +
                         sLine +
                         ">");
          bWarnedOnTooSmallMaxWidth = true;
        }
        bSplitNow = false;
      }

      if (bSplitNow)
      {
        // Maximum width reached
        if (nCodePointOffsetOfLastWhitespace > 0)
        {
          // Use everything up to but excluding the last whitespace
          final String sPart = sCurLine.substring (0, nCodePointOffsetOfLastWhitespace);
          // Skip whitespace char in this case
          sCurLine = sCurLine.substring (nCodePointOffsetOfLastWhitespace + 1);
          ret.add (new TextAndWidthSpec (sPart, fSumWidthOfLastWhitespace));
        }
        else
        {
          // No whitespace - use up to but excluding last char
          final String sPart = sCurLine.substring (0, nCodePointOffset);
          sCurLine = sCurLine.substring (nCodePointOffset);
          ret.add (new TextAndWidthSpec (sPart, fSumWidth));
        }

        // Reset counter for the rest of the line
        fSumWidth = 0f;
        nCodePointOffset = 0;
        fSumWidthOfLastWhitespace = 0f;
        nCodePointOffsetOfLastWhitespace = 0;
      }
      else
      {
        // Add current char
        nCodePointOffset += Character.charCount (nCodePoint);
        fSumWidth = fNewWidth;
      }
    }

    // Add the rest (even if it is empty, otherwise empty lines won't get
    // printed)
    ret.add (new TextAndWidthSpec (sCurLine, fSumWidth));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <TextAndWidthSpec> getFitToWidth (@Nullable final String sText,
                                                        @Nonnegative final float fFontSize,
                                                        @Nonnegative final float fMaxWidth) throws IOException
  {
    ValueEnforcer.isGT0 (fFontSize, "FontSize");
    ValueEnforcer.isGT0 (fMaxWidth, "MaxWidth");

    // First split by the contained line breaks
    // In the constructor we ensured that only "\n" is used
    final String [] aLines = StringHelper.getExplodedArray ('\n', sText);

    final ICommonsList <TextAndWidthSpec> ret = new CommonsArrayList <> ();
    for (final String sLine : aLines)
      _getLineFitToWidthForward (sLine, fFontSize, fMaxWidth, ret);

    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LoadedFont rhs = (LoadedFont) o;
    return m_aFont.equals (rhs.m_aFont);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFont).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Font", m_aFont)
                                       .append ("FallbackCodePoint", m_nFallbackCodePoint)
                                       .append ("BBHeight", m_fBBHeight)
                                       .append ("Descent", m_fDescent)
                                       .append ("FontWillBeSubset", m_bFontWillBeSubset)
                                       .getToString ();
  }
}
