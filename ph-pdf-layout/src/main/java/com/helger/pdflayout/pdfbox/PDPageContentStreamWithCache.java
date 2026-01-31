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
package com.helger.pdflayout.pdfbox;

import java.io.IOException;

import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.pdflayout.spec.LoadedFont;

/**
 * A special version of PDPageContentStream with an integrated "cache" to avoid setting the same
 * information over and over again.
 *
 * @author Philip Helger
 */
public class PDPageContentStreamWithCache
{
  /** Bezier control point constant */
  private static final float BEZ = 0.551915024494f;
  private final PDDocument m_aDocument;
  private final PDPage m_aPage;
  private final PDPageContentStreamExt m_aStream;

  // Status cache
  private LoadedFont m_aLastUsedLoadedFont;
  private float m_fLastUsedFontSize = 0f;
  private PLColor m_aLastUsedStrokingColor = PLColor.BLACK;
  private PLColor m_aLastUsedNonStrokingColor = PLColor.BLACK;
  private LineDashPatternSpec m_aLastUsedLineDashPattern = LineDashPatternSpec.SOLID;
  private float m_fLastUsedLineWidth = 0f;

  private void _resetStatus ()
  {
    m_aLastUsedLoadedFont = null;
    m_fLastUsedFontSize = 0f;
    m_aLastUsedStrokingColor = PLColor.BLACK;
    m_aLastUsedNonStrokingColor = PLColor.BLACK;
    m_aLastUsedLineDashPattern = LineDashPatternSpec.SOLID;
    m_fLastUsedLineWidth = 0f;
  }

  public PDPageContentStreamWithCache (@NonNull final PDDocument aDocument,
                                       @NonNull final PDPage aSourcePage,
                                       final PDPageContentStream.@NonNull AppendMode aAppendContent,
                                       final boolean bCompress) throws IOException
  {
    m_aDocument = aDocument;
    m_aPage = aSourcePage;
    m_aStream = new PDPageContentStreamExt (aDocument, aSourcePage, aAppendContent, bCompress);
  }

  /**
   * @return The {@link PDDocument} this stream is working on. Never <code>null</code>.
   */
  @NonNull
  public final PDDocument getDocument ()
  {
    return m_aDocument;
  }

  /**
   * @return The {@link PDPage} this stream is working on. Never <code>null</code>.
   */
  @NonNull
  public final PDPage getPage ()
  {
    return m_aPage;
  }

  /**
   * @return The internal page content stream. Never <code>null</code>. Handle with care.
   * @since 6.0.2
   */
  @NonNull
  public final PDPageContentStreamExt getContentStream ()
  {
    return m_aStream;
  }

  public void setFont (@NonNull final LoadedFont aLoadedFont, @NonNull final FontSpec aFontSpec) throws IOException
  {
    ValueEnforcer.notNull (aLoadedFont, "Font");

    final float fFontSize = aFontSpec.getFontSize ();
    if (m_aLastUsedLoadedFont == null ||
        !aLoadedFont.equals (m_aLastUsedLoadedFont) ||
        !EqualsHelper.equals (fFontSize, m_fLastUsedFontSize))
    {
      m_aStream.setFont (aLoadedFont.getFont (), fFontSize);
      m_aLastUsedLoadedFont = aLoadedFont;
      m_fLastUsedFontSize = fFontSize;
    }
    setNonStrokingColor (aFontSpec.getColor ());
  }

  public void setStrokingColor (@NonNull final PLColor aColor) throws IOException
  {
    ValueEnforcer.notNull (aColor, "Color");

    if (!m_aLastUsedStrokingColor.equals (aColor))
    {
      m_aStream.setStrokingColor (aColor);
      m_aLastUsedStrokingColor = aColor;
    }
  }

  @NonNull
  public PLColor getLastUsedStrokingColor ()
  {
    return m_aLastUsedStrokingColor;
  }

  public void setNonStrokingColor (@NonNull final PLColor aColor) throws IOException
  {
    ValueEnforcer.notNull (aColor, "Color");

    if (!m_aLastUsedNonStrokingColor.equals (aColor))
    {
      m_aStream.setNonStrokingColor (aColor);
      m_aLastUsedNonStrokingColor = aColor;
    }
  }

  @NonNull
  public PLColor getLastUsedNonStrokingColor ()
  {
    return m_aLastUsedNonStrokingColor;
  }

  public void setLineDashPattern (@NonNull final LineDashPatternSpec aLineDashPattern) throws IOException
  {
    ValueEnforcer.notNull (aLineDashPattern, "LineDashPattern");

    if (!m_aLastUsedLineDashPattern.equals (aLineDashPattern))
    {
      m_aStream.setLineDashPattern (aLineDashPattern.getPattern (), aLineDashPattern.getPhase ());
      m_aLastUsedLineDashPattern = aLineDashPattern;
    }
  }

  @NonNull
  public LineDashPatternSpec getLastUsedLineDashPattern ()
  {
    return m_aLastUsedLineDashPattern;
  }

  public void setLineWidth (final float fLineWidth) throws IOException
  {
    if (fLineWidth >= 0)
      if (!EqualsHelper.equals (m_fLastUsedLineWidth, fLineWidth))
      {
        m_aStream.setLineWidth (fLineWidth);
        m_fLastUsedLineWidth = fLineWidth;
      }
  }

  public float getLastUsedLineWidth ()
  {
    return m_fLastUsedLineWidth;
  }

  public void moveTextPositionByAmount (final float tx, final float ty) throws IOException
  {
    if (tx != 0 || ty != 0)
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

  public void addRect (final float fLeft, final float fBottom, final float fWidth, final float fHeight)
                                                                                                        throws IOException
  {
    m_aStream.addRect (fLeft, fBottom, fWidth, fHeight);
  }

  public void drawLine (final float xStart, final float yStart, final float xEnd, final float yEnd) throws IOException
  {
    m_aStream.moveTo (xStart, yStart);
    m_aStream.lineTo (xEnd, yEnd);
  }

  public void fillRect (final float fX, final float fY, final float fWidth, final float fHeight) throws IOException
  {
    addRect (fX, fY, fWidth, fHeight);
    fill ();
  }

  public void drawRoundedRect (final float fX,
                               final float fY,
                               final float fWidth,
                               final float fHeight,
                               final float fRadiusTL,
                               final float fRadiusTR,
                               final float fRadiusBL,
                               final float fRadiusBR) throws IOException
  {
    // Ensure that the radiuses are not larger than half the width/height
    final float fMaxRadius = Math.min (fHeight / 2, fWidth / 2);
    final float fRealRadiusTL = Math.min (fRadiusTL, fMaxRadius);
    final float fRealRadiusTR = Math.min (fRadiusTR, fMaxRadius);
    final float fRealRadiusBL = Math.min (fRadiusBL, fMaxRadius);
    final float fRealRadiusBR = Math.min (fRadiusBR, fMaxRadius);

    // Calculate the Bezier control points
    final float fBezXTL = fRealRadiusTL * BEZ;
    final float fBezYTL = fBezXTL;
    final float fBezXTR = fRealRadiusTR * BEZ;
    final float fBezYTR = fBezXTR;
    final float fBezXBL = fRealRadiusBL * BEZ;
    final float fBezYBL = fBezXBL;
    final float fBezXBR = fRealRadiusBR * BEZ;
    final float fBezYBR = fBezXBR;

    final float fBottom = fY + fHeight;
    m_aStream.moveTo (fX + fRealRadiusBL, fY);

    // to bottom right
    m_aStream.lineTo (fX + fWidth - fRealRadiusBR, fY);
    m_aStream.curveTo (fX + fWidth - fRealRadiusBR + fBezXBR,
                       fY,
                       fX + fWidth,
                       fY + fRealRadiusBR - fBezYBR,
                       fX + fWidth,
                       fY + fRealRadiusBR);

    // to top right
    m_aStream.lineTo (fX + fWidth, fBottom - fRealRadiusTR);
    m_aStream.curveTo (fX + fWidth,
                       fBottom - fRealRadiusTR + fBezYTR,
                       fX + fWidth - fRealRadiusTR + fBezXTR,
                       fBottom,
                       fX + fWidth - fRealRadiusTR,
                       fBottom);

    // to top left
    m_aStream.lineTo (fX + fRealRadiusTL, fBottom);
    m_aStream.curveTo (fX + fRealRadiusTL - fBezXTL, fBottom, fX, fBottom - fBezYTL, fX, fBottom - fRealRadiusTL);

    // to bottom left
    m_aStream.lineTo (fX, fY + fRealRadiusBL);
    m_aStream.curveTo (fX, fY + fRealRadiusBL - fBezYBL, fX + fBezXBL, fY, fX + fRealRadiusBL, fY);
  }

  public void beginText () throws IOException
  {
    m_aStream.beginText ();
  }

  public void endText () throws IOException
  {
    m_aStream.endText ();
  }

  public void setCharacterSpacing (final float fSpacing) throws IOException
  {
    m_aStream.setCharacterSpacing (fSpacing);
  }

  public void drawString (final String sDrawText) throws IOException
  {
    if (false)
      m_aStream.showText (sDrawText);
    else
    {
      final byte [] aEncoded = m_aLastUsedLoadedFont.getEncodedForPageContentStream (sDrawText);
      COSWriter.writeString (aEncoded, m_aStream.getOutput ());
      m_aStream.write ((byte) ' ');
      m_aStream.writeOperator ((byte) 'T', (byte) 'j');
    }
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

  public void saveGraphicsState () throws IOException
  {
    m_aStream.saveGraphicsState ();
    // Make sure, that all status elements are emitted again
    _resetStatus ();
  }

  public void clip () throws IOException
  {
    m_aStream.clip ();
  }

  public void restoreGraphicsState () throws IOException
  {
    m_aStream.restoreGraphicsState ();
  }
}
