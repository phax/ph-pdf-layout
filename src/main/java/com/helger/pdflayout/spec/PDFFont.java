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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class wraps PDF Fonts and offers some sanity methods.
 * 
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class PDFFont
{
  public static final PDFFont REGULAR = new PDFFont (PDType1Font.HELVETICA);
  public static final PDFFont REGULAR_BOLD = new PDFFont (PDType1Font.HELVETICA_BOLD);
  public static final PDFFont REGULAR_ITALIC = new PDFFont (PDType1Font.HELVETICA_OBLIQUE);
  public static final PDFFont REGULAR_BOLD_ITALIC = new PDFFont (PDType1Font.HELVETICA_BOLD_OBLIQUE);
  public static final PDFFont MONOSPACE = new PDFFont (PDType1Font.COURIER);
  public static final PDFFont MONOSPACE_BOLD = new PDFFont (PDType1Font.COURIER_BOLD);
  public static final PDFFont MONOSPACE_ITALIC = new PDFFont (PDType1Font.COURIER_OBLIQUE);
  public static final PDFFont MONOSPACE_BOLD_ITALIC = new PDFFont (PDType1Font.COURIER_BOLD_OBLIQUE);
  public static final PDFFont TIMES = new PDFFont (PDType1Font.TIMES_ROMAN);
  public static final PDFFont TIMES_BOLD = new PDFFont (PDType1Font.TIMES_BOLD);
  public static final PDFFont TIMES_ITALIC = new PDFFont (PDType1Font.TIMES_ITALIC);
  public static final PDFFont TIMES_BOLD_ITALIC = new PDFFont (PDType1Font.TIMES_BOLD_ITALIC);
  public static final PDFFont SYMBOL = new PDFFont (PDType1Font.SYMBOL);
  public static final PDFFont ZAPF_DINGBATS = new PDFFont (PDType1Font.ZAPF_DINGBATS);

  private static final Logger s_aLogger = LoggerFactory.getLogger (PDFFont.class);

  private final PDFont m_aFont;
  // Helper
  private final float m_fBBHeight;
  private float [] m_aIso88591WidthCache;

  public PDFFont (@Nonnull final PDFont aFont)
  {
    m_aFont = ValueEnforcer.notNull (aFont, "Font");
    final PDFontDescriptor aFD = aFont.getFontDescriptor ();
    // 2.0.0 code. Does not work with 1.8.4
    // if (aFD == null)
    // {
    // if (aFont instanceof PDType0Font)
    // {
    // final PDFont aDescendantFont = ((PDType0Font) aFont).getDescendantFont
    // ();
    // if (aDescendantFont != null)
    // aFD = aDescendantFont.getFontDescriptor ();
    // }
    // }
    if (aFD == null)
      throw new IllegalArgumentException ("Failed to determined FontDescriptor from specified font " + aFont);

    m_fBBHeight = aFD.getFontBoundingBox ().getHeight ();
  }

  /**
   * @return The underyling font. Never <code>null</code>.
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

  @Nonnegative
  public float getStringWidth (@Nonnull final String sText,
                               @Nonnull final Charset aCharset,
                               @Nonnegative final float fFontSize) throws IOException
  {
    if (false)
    {
      float fWidth = 0;
      final int nMax = sText.length ();
      for (int nIndex = 0; nIndex < nMax; ++nIndex)
      {
        final byte [] aCharBytes = CharsetManager.getAsBytes (sText.substring (nIndex, nIndex + 1), aCharset);
        fWidth += m_aFont.getFontWidth (aCharBytes, 0, aCharBytes.length);
        ++nIndex;
      }

      // The width is in 1000 unit of text space, ie 333 or 777
      return fWidth * fFontSize / 1000f;
    }

    float fWidth = 0;
    if (aCharset.equals (CCharset.CHARSET_ISO_8859_1_OBJ))
    {
      // Performance improvement, because each char is always the same width
      if (m_aIso88591WidthCache == null)
      {
        m_aIso88591WidthCache = new float [256];
        for (int i = 0; i < 256; ++i)
          m_aIso88591WidthCache[i] = m_aFont.getFontWidth (new byte [] { (byte) i }, 0, 1);
      }

      for (final char c : sText.toCharArray ())
        if (c > 255)
        {
          // Character not in iso-8859-1 range
          fWidth += m_aIso88591WidthCache['?'];
        }
        else
          fWidth += m_aIso88591WidthCache[c];
    }
    else
    {
      // Should be quicker than m_aFont.getStringWidth (sText)
      final byte [] aTextBytes = CharsetManager.getAsBytes (sText, aCharset);

      // Need to it per byte, as getFontWidth (aTextBytes, 0, aTextBytes.length)
      // does not work
      for (int i = 0; i < aTextBytes.length; i++)
        fWidth += m_aFont.getFontWidth (aTextBytes, i, 1);
    }

    // The width is in 1000 unit of text space, ie 333 or 777
    return fWidth * fFontSize / 1000f;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <TextAndWidthSpec> getFitToWidth (@Nullable final String sText,
                                                @Nonnull final Charset aCharset,
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
      outer: while ((fCurLineWidth = getStringWidth (sCurLine, aCharset, fFontSize)) > fMaxWidth)
      {
        // Line is too long to fit

        // Try to break line as late as possible, at a whitespace position
        boolean bFoundSpace = false;
        for (int i = sCurLine.length () - 1; i >= 0; i--)
          if (Character.isWhitespace (sCurLine.charAt (i)))
          {
            // Whitespace found
            final String sLineStart = sCurLine.substring (0, i);
            final float fLineStartWidth = getStringWidth (sLineStart, aCharset, fFontSize);
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
            final float fWordPartLength = getStringWidth (sWordPart, aCharset, fFontSize);
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
    if (!(o instanceof PDFFont))
      return false;
    final PDFFont rhs = (PDFFont) o;
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
