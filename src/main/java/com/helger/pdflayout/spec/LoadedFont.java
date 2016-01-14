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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontHelper;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.util.IntFloatMap;
import com.helger.pdflayout.util.IntObjectMap;

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
  private static final class EncodedCodePoint
  {
    private final int m_nCodePoint;
    private final byte [] m_aEncoded;
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
     * @return The effective codepoint use.
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
      if (m_aEncodedValue == null)
      {
        // Lazy init
        m_aEncodedValue = Integer.valueOf (_toInt (m_aEncoded));
      }
      return m_aEncodedValue.intValue ();
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (LoadedFont.class);

  private final PDFont m_aFont;
  private final int m_nFallbackCodePoint;
  // Status vars
  private final float m_fBBHeight;
  private final boolean m_bFontWillBeSubset;
  private final IntObjectMap <EncodedCodePoint> m_aEncodedCodePointCache = new IntObjectMap <EncodedCodePoint> ();
  private final IntFloatMap m_aCodepointWidthCache = new IntFloatMap ();

  public LoadedFont (@Nonnull final PDFont aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    m_aFont = aFont;

    // The fallback character to be used in case an unmappable character is
    // contained
    m_nFallbackCodePoint = '?';

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
    m_bFontWillBeSubset = m_aFont.willBeSubset ();
  }

  /**
   * @return The underlying font. Never <code>null</code>.
   */
  @Nonnull
  public PDFont getFont ()
  {
    return m_aFont;
  }

  @Nonnegative
  public float getTextHeight (@Nonnegative final float fFontSize)
  {
    return m_fBBHeight * fFontSize / 1000f;
  }

  @Nonnegative
  public float getLineHeight (@Nonnegative final float fFontSize)
  {
    // By default add 5% from text height line
    return getTextHeight (fFontSize) * 1.05f;
  }

  @Nonnull
  public static EncodedCodePoint encodeCodepointWithFallback (@Nonnull final PDFont aFont,
                                                              final int nCodepoint,
                                                              final int nFallbackCodepoint) throws IOException
  {
    // multi-byte encoding with 1 to 4 bytes
    try
    {
      return new EncodedCodePoint (nCodepoint, PDFontHelper.encode (aFont, nCodepoint));
    }
    catch (final IllegalArgumentException ex)
    {
      s_aLogger.warn ("No code point " + nCodepoint + " in font " + aFont);

      // Use fallback codepoint
      return new EncodedCodePoint (nFallbackCodepoint, PDFontHelper.encode (aFont, nFallbackCodepoint));
    }
  }

  @Nonnull
  private EncodedCodePoint _getEncodedCodePoint (final int nCodePoint) throws IOException
  {
    EncodedCodePoint aECP = m_aEncodedCodePointCache.get (nCodePoint);
    if (aECP == null)
    {
      // Encode codepoint according to the font rules
      aECP = encodeCodepointWithFallback (m_aFont, nCodePoint, m_nFallbackCodePoint);
      // put in cache
      m_aEncodedCodePointCache.put (nCodePoint, aECP);
    }
    return aECP;
  }

  private float _getCodepointWidth (final int nCodePoint) throws IOException
  {
    float fWidth = m_aCodepointWidthCache.get (nCodePoint, -1f);
    if (fWidth < 0)
    {
      // Get encoded codepoint (from its own cache)
      final EncodedCodePoint aECP = _getEncodedCodePoint (nCodePoint);

      // Get width of encoded value
      fWidth = m_aFont.getWidth (aECP.getEncodedIntValue ());

      // Map codepoint to width to save encoding
      m_aCodepointWidthCache.put (nCodePoint, fWidth);
    }
    return fWidth;
  }

  @Nonnegative
  public float getStringWidth (@Nonnull final String sText, @Nonnegative final float fFontSize) throws IOException
  {
    if (false)
    {
      // Toooo slow
      return m_aFont.getStringWidth (sText) * fFontSize / 1000f;
    }

    float fWidth = 0;

    // Iterate on codepoint basis
    int nCPOfs = 0;
    while (nCPOfs < sText.length ())
    {
      final int nCP = sText.codePointAt (nCPOfs);
      nCPOfs += Character.charCount (nCP);

      // Use codepoint cache for maximum performance
      fWidth += _getCodepointWidth (nCP);
    }

    // The width is in 1000 unit of text space, ie 333 or 777
    return fWidth * fFontSize / 1000f;
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
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (sText.length () * 2);
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

  @Nonnull
  @ReturnsMutableCopy
  public List <TextAndWidthSpec> getFitToWidth (@Nullable final String sText,
                                                @Nonnegative final float fFontSize,
                                                @Nonnegative final float fMaxWidth) throws IOException
  {
    final List <TextAndWidthSpec> ret = new ArrayList <TextAndWidthSpec> ();

    // First split by the contained line breaks
    final String [] aLines = StringHelper.getExplodedArray ('\n', sText);
    for (final String sLine : aLines)
    {
      // Now split each source line into the best matching sub-lines
      String sCurLine = sLine;
      float fCurLineWidth;
      outer: while ((fCurLineWidth = getStringWidth (sCurLine, fFontSize)) > fMaxWidth)
      {
        // Line is too long to fit

        // Try to break line as late as possible, at a whitespace position
        boolean bFoundSpace = false;
        for (int i = sCurLine.length () - 1; i >= 0; i--)
          if (Character.isWhitespace (sCurLine.charAt (i)))
          {
            // Whitespace found
            final String sLineStart = sCurLine.substring (0, i);
            final float fLineStartWidth = getStringWidth (sLineStart, fFontSize);
            if (fLineStartWidth <= fMaxWidth)
            {
              // We found a line - continue with the rest of the line
              ret.add (new TextAndWidthSpec (sLineStart, fLineStartWidth));

              // Automatically skip the white space and continue with the rest
              // of the line
              sCurLine = sCurLine.substring (i + 1);
              bFoundSpace = true;
              break;
            }
          }

        if (!bFoundSpace)
        {
          // No word break found - split in the middle of the word
          int nIndex = 1;
          float fPrevWordPartLength = -1;
          do
          {
            final String sWordPart = sCurLine.substring (0, nIndex);
            final float fWordPartLength = getStringWidth (sWordPart, fFontSize);
            if (fWordPartLength > fMaxWidth)
            {
              // We have an overflow - take everything except the last char
              if (nIndex == 1)
              {
                s_aLogger.warn ("A single character exceeds the maximum width of " + fMaxWidth);

                // Continue anyway
                ++nIndex;
                fPrevWordPartLength = fWordPartLength;
              }
              else
              {
                // Add everything except the last character.
                final String sWordPartToUse = sCurLine.substring (0, nIndex - 1);
                ret.add (new TextAndWidthSpec (sWordPartToUse, fPrevWordPartLength));

                // Remove the current word part
                sCurLine = sCurLine.substring (nIndex - 1);

                // And check for the next whitespace
                continue outer;
              }
            }
            else
            {
              // No overflow yet
              ++nIndex;
              fPrevWordPartLength = fWordPartLength;
            }
          } while (nIndex < sCurLine.length ());

          // The rest of the string is added below!
          break;
        }
      }

      // Add the part of the line that fits
      ret.add (new TextAndWidthSpec (sCurLine, fCurLineWidth));
    }

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
    return new ToStringGenerator (this).append ("font", m_aFont).append ("bbHeight", m_fBBHeight).toString ();
  }
}
