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
package org.apache.pdfbox.pdmodel;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontHelper;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;

/**
 * A special version of PDPageContentStream with an integrated "cache" to avoid
 * setting the same information over and over again.
 *
 * @author Philip Helger
 */
public class PDPageContentStreamWithCache
{
  private final PDDocument m_aDocument;
  private final PDPage m_aPage;
  private final PDPageContentStreamExt m_aStream;

  // Status cache
  private FontSpec m_aLastUsedFont;
  private Color m_aLastUsedStrokingColor = Color.BLACK;
  private Color m_aLastUsedNonStrokingColor = Color.BLACK;
  private LineDashPatternSpec m_aLastUsedLineDashPattern = LineDashPatternSpec.SOLID;

  public PDPageContentStreamWithCache (@Nonnull final PDDocument aDocument,
                                       @Nonnull final PDPage aSourcePage,
                                       final boolean bAppendContent,
                                       final boolean bCompress) throws IOException
  {
    m_aDocument = aDocument;
    m_aPage = aSourcePage;
    m_aStream = new PDPageContentStreamExt (aDocument, aSourcePage, bAppendContent, bCompress);
  }

  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDocument;
  }

  @Nonnull
  public PDPage getPage ()
  {
    return m_aPage;
  }

  public void setFont (@Nonnull final FontSpec aFont) throws IOException
  {
    ValueEnforcer.notNull (aFont, "Font");

    if (m_aLastUsedFont == null ||
        !aFont.getFont ().equals (m_aLastUsedFont.getFont ()) ||
        !EqualsHelper.equals (aFont.getFontSize (), m_aLastUsedFont.getFontSize ()))
    {
      m_aStream.setFont (aFont.getFont ().getFont (), aFont.getFontSize ());
      m_aLastUsedFont = aFont;
    }
    setNonStrokingColor (aFont.getColor ());
  }

  public void setStrokingColor (@Nonnull final Color aColor) throws IOException
  {
    ValueEnforcer.notNull (aColor, "Color");

    if (!m_aLastUsedStrokingColor.equals (aColor))
    {
      m_aStream.setStrokingColor (aColor);
      m_aLastUsedStrokingColor = aColor;
    }
  }

  @Nonnull
  public Color getLastUsedStrokingColor ()
  {
    return m_aLastUsedStrokingColor;
  }

  public void setNonStrokingColor (@Nonnull final Color aColor) throws IOException
  {
    ValueEnforcer.notNull (aColor, "Color");

    if (!m_aLastUsedNonStrokingColor.equals (aColor))
    {
      m_aStream.setNonStrokingColor (aColor);
      m_aLastUsedNonStrokingColor = aColor;
    }
  }

  @Nonnull
  public Color getLastUsedNonStrokingColor ()
  {
    return m_aLastUsedNonStrokingColor;
  }

  public void setLineDashPattern (@Nonnull final LineDashPatternSpec aLineDashPattern) throws IOException
  {
    ValueEnforcer.notNull (aLineDashPattern, "LineDashPattern");

    if (!m_aLastUsedLineDashPattern.equals (aLineDashPattern))
    {
      m_aStream.setLineDashPattern (aLineDashPattern.getPattern (), aLineDashPattern.getPhase ());
      m_aLastUsedLineDashPattern = aLineDashPattern;
    }
  }

  @Nonnull
  public LineDashPatternSpec getLastUsedLineDashPattern ()
  {
    return m_aLastUsedLineDashPattern;
  }

  public void moveTextPositionByAmount (final float tx, final float ty) throws IOException
  {
    m_aStream.newLineAtOffset (tx, ty);
  }

  public void stroke () throws IOException
  {
    m_aStream.stroke ();
  }

  public void fill () throws IOException
  {
    m_aStream.fill ();
  }

  public void addRect (final float fLeft,
                       final float fBottom,
                       final float fWidth,
                       final float fHeight) throws IOException
  {
    m_aStream.addRect (fLeft, fBottom, fWidth, fHeight);
  }

  public void drawLine (final float xStart, final float yStart, final float xEnd, final float yEnd) throws IOException
  {
    m_aStream.moveTo (xStart, yStart);
    m_aStream.lineTo (xEnd, yEnd);
    stroke ();
  }

  public void fillRect (final float fX, final float fY, final float fWidth, final float fHeight) throws IOException
  {
    addRect (fX, fY, fWidth, fHeight);
    fill ();
  }

  public void beginText () throws IOException
  {
    m_aStream.beginText ();
  }

  public void endText () throws IOException
  {
    m_aStream.endText ();
  }

  public void drawString (final String sDrawText) throws IOException
  {
    if (false)
      m_aStream.showText (sDrawText);

    final PDFont font = m_aLastUsedFont.getFont ().getFont ();

    // Unicode code points to keep when subsetting
    if (font.willBeSubset ())
    {
      for (int offset = 0; offset < sDrawText.length ();)
      {
        final int codePoint = sDrawText.codePointAt (offset);
        font.addToSubset (codePoint);
        offset += Character.charCount (codePoint);
      }
    }

    COSWriter.writeString (PDFontHelper.encode (font, sDrawText, '?'), m_aStream.output);
    m_aStream.write (" ");

    m_aStream.writeOperator ("Tj");
  }

  public void drawXObject (final PDImageXObject aImage,
                           final float fX,
                           final float fY,
                           final float fWidth,
                           final float fHeight) throws IOException
  {
    m_aStream.drawImage (aImage, fX, fY, fWidth, fHeight);
  }

  public void close () throws IOException
  {
    m_aStream.close ();
  }
}
