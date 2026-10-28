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
package com.helger.pdflayout.watermark;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.pdfbox.PDPageContentStreamExt;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * A single line of text drawn on every page, behind the content - for example "DRAFT". It is
 * drawn after the layout is complete and marked as an artifact (<code>/Artifact</code> of type
 * <code>Pagination</code>, subtype <code>Watermark</code>), so it is not part of the logical
 * structure of a tagged PDF. Add it with
 * {@link com.helger.pdflayout.PageLayoutPDF#addWatermark(PLTextWatermark)}.
 * <p>
 * An opacity below 1 is drawn with a transparency graphics state. Transparency is allowed in PDF/A-2
 * and PDF/A-3, but not in PDF/A-1. To avoid transparency, use opacity 1 with a lighter colour.
 * </p>
 *
 * @author Philip Helger
 * @since 8.4.0
 */
@NotThreadSafe
public class PLTextWatermark
{
  /** Default font size in points */
  public static final float DEFAULT_FONT_SIZE = 72f;
  /** Default colour: a light grey */
  public static final PLColor DEFAULT_COLOR = PLColor.gray (0xcc);
  /** Default opacity: no transparency */
  public static final float DEFAULT_OPACITY = 1f;
  /** Default rotation in degrees, counter clockwise */
  public static final float DEFAULT_ROTATION = 45f;
  /** Default position */
  public static final EPLWatermarkPosition DEFAULT_POSITION = EPLWatermarkPosition.CENTER;
  /** Default distance from the page edge in points, for all positions except the centre */
  public static final float DEFAULT_EDGE_DISTANCE = 10f;

  /** Approximate ratio of the cap height to the font size, for vertical centring */
  private static final float CAP_HEIGHT_RATIO = 0.72f;

  private final String m_sText;
  private final PreloadFont m_aFont;
  private float m_fFontSize = DEFAULT_FONT_SIZE;
  private PLColor m_aColor = DEFAULT_COLOR;
  private float m_fOpacity = DEFAULT_OPACITY;
  private float m_fRotation = DEFAULT_ROTATION;
  private EPLWatermarkPosition m_ePosition = DEFAULT_POSITION;
  private float m_fEdgeDistance = DEFAULT_EDGE_DISTANCE;

  /**
   * Constructor
   *
   * @param sText
   *        The text to draw. May neither be <code>null</code> nor empty.
   * @param aFont
   *        The font to use. May not be <code>null</code>. For PDF/A it must be an embedded font.
   */
  public PLTextWatermark (@NonNull @Nonempty final String sText, @NonNull final PreloadFont aFont)
  {
    ValueEnforcer.notEmpty (sText, "Text");
    ValueEnforcer.notNull (aFont, "Font");
    m_sText = sText;
    m_aFont = aFont;
  }

  /**
   * @return The text to draw. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public final String getText ()
  {
    return m_sText;
  }

  /**
   * @return The font. Never <code>null</code>.
   */
  @NonNull
  public final PreloadFont getFont ()
  {
    return m_aFont;
  }

  /**
   * @return The font size in points. Default is {@link #DEFAULT_FONT_SIZE}.
   */
  @Nonnegative
  public final float getFontSize ()
  {
    return m_fFontSize;
  }

  /**
   * @param fFontSize
   *        The font size in points. Must be &gt; 0.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setFontSize (final float fFontSize)
  {
    ValueEnforcer.isGT0 (fFontSize, "FontSize");
    m_fFontSize = fFontSize;
    return this;
  }

  /**
   * @return The text colour. Default is {@link #DEFAULT_COLOR}.
   */
  @NonNull
  public final PLColor getColor ()
  {
    return m_aColor;
  }

  /**
   * @param aColor
   *        The text colour. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setColor (@NonNull final PLColor aColor)
  {
    ValueEnforcer.notNull (aColor, "Color");
    m_aColor = aColor;
    return this;
  }

  /**
   * @return The opacity between 0 and 1. Default is {@link #DEFAULT_OPACITY}.
   */
  public final float getOpacity ()
  {
    return m_fOpacity;
  }

  /**
   * @param fOpacity
   *        The opacity between 0 (invisible) and 1 (opaque). A value below 1 uses transparency.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setOpacity (final float fOpacity)
  {
    ValueEnforcer.isBetweenInclusive (fOpacity, "Opacity", 0f, 1f);
    m_fOpacity = fOpacity;
    return this;
  }

  /**
   * @return The rotation in degrees, counter clockwise. Only used for
   *         {@link EPLWatermarkPosition#CENTER}. Default is {@link #DEFAULT_ROTATION}.
   */
  public final float getRotation ()
  {
    return m_fRotation;
  }

  /**
   * @param fRotation
   *        The rotation in degrees, counter clockwise. Only used for
   *        {@link EPLWatermarkPosition#CENTER}.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setRotation (final float fRotation)
  {
    m_fRotation = fRotation;
    return this;
  }

  /**
   * @return The position on the page. Default is {@link #DEFAULT_POSITION}.
   */
  @NonNull
  public final EPLWatermarkPosition getPosition ()
  {
    return m_ePosition;
  }

  /**
   * @param ePosition
   *        The position on the page. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setPosition (@NonNull final EPLWatermarkPosition ePosition)
  {
    ValueEnforcer.notNull (ePosition, "Position");
    m_ePosition = ePosition;
    return this;
  }

  /**
   * @return The distance from the page edge in points, for all positions except the centre. Default
   *         is {@link #DEFAULT_EDGE_DISTANCE}.
   */
  @Nonnegative
  public final float getEdgeDistance ()
  {
    return m_fEdgeDistance;
  }

  /**
   * @param fEdgeDistance
   *        The distance from the page edge in points. Must be &ge; 0.
   * @return this for chaining
   */
  @NonNull
  public final PLTextWatermark setEdgeDistance (final float fEdgeDistance)
  {
    ValueEnforcer.isGE0 (fEdgeDistance, "EdgeDistance");
    m_fEdgeDistance = fEdgeDistance;
    return this;
  }

  @NonNull
  private Matrix _getTextMatrix (@NonNull final PDRectangle aPageSize,
                                 final float fTextWidth,
                                 final float fTextHeight)
  {
    final float fPageWidth = aPageSize.getWidth ();
    final float fPageHeight = aPageSize.getHeight ();
    return switch (m_ePosition)
    {
      case CENTER ->
      {
        final float fCx = fPageWidth / 2f;
        final float fCy = fPageHeight / 2f;
        final double dAngle = Math.toRadians (m_fRotation);
        // Shift back by half of the text along the rotation, so that the text is centred
        final float fAnchorX = fCx -
                               (float) (Math.cos (dAngle) * fTextWidth / 2f) +
                               (float) (Math.sin (dAngle) * fTextHeight / 2f);
        final float fAnchorY = fCy -
                               (float) (Math.sin (dAngle) * fTextWidth / 2f) -
                               (float) (Math.cos (dAngle) * fTextHeight / 2f);
        yield Matrix.getRotateInstance (dAngle, fAnchorX, fAnchorY);
      }
      case TOP -> Matrix.getTranslateInstance ((fPageWidth - fTextWidth) / 2f,
                                               fPageHeight - m_fFontSize - m_fEdgeDistance);
      case BOTTOM -> Matrix.getTranslateInstance ((fPageWidth - fTextWidth) / 2f, m_fEdgeDistance);
      case BOTTOM_RIGHT -> Matrix.getTranslateInstance (fPageWidth - fTextWidth - m_fEdgeDistance, m_fEdgeDistance);
    };
  }

  /**
   * Draw the watermark behind the content of all pages of the document. Called by
   * {@link com.helger.pdflayout.PageLayoutPDF} after the layout is complete.
   *
   * @param aDoc
   *        The document. May not be <code>null</code>.
   * @param bCompress
   *        <code>true</code> to compress the created content streams.
   * @throws IOException
   *         on error
   */
  public void drawOnAllPages (@NonNull final PDDocument aDoc, final boolean bCompress) throws IOException
  {
    ValueEnforcer.notNull (aDoc, "Document");

    // Load the font only once per document
    final PDFont aFont = m_aFont.loadPDFont (aDoc);
    final float fTextWidth = aFont.getStringWidth (m_sText) / 1000f * m_fFontSize;
    final float fTextHeight = m_fFontSize * CAP_HEIGHT_RATIO;

    final COSDictionary aArtifactProps = new COSDictionary ();
    aArtifactProps.setItem (COSName.TYPE, COSName.getPDFName ("Pagination"));
    aArtifactProps.setItem (COSName.SUBTYPE, COSName.getPDFName ("Watermark"));

    PDExtendedGraphicsState aGS = null;
    if (m_fOpacity < 1f)
    {
      aGS = new PDExtendedGraphicsState ();
      aGS.setNonStrokingAlphaConstant (Float.valueOf (m_fOpacity));
    }

    for (final PDPage aPage : aDoc.getPages ())
    {
      // Prepend, so that the watermark is behind the content
      try (final PDPageContentStreamExt aCS = new PDPageContentStreamExt (aDoc,
                                                                          aPage,
                                                                          PDPageContentStream.AppendMode.PREPEND,
                                                                          bCompress,
                                                                          true))
      {
        aCS.beginMarkedContent (COSName.ARTIFACT, PDPropertyList.create (aArtifactProps));
        aCS.saveGraphicsState ();
        if (aGS != null)
          aCS.setGraphicsStateParameters (aGS);
        aCS.beginText ();
        aCS.setFont (aFont, m_fFontSize);
        aCS.setNonStrokingColor (m_aColor.getRed (), m_aColor.getGreen (), m_aColor.getBlue ());
        aCS.setTextMatrix (_getTextMatrix (aPage.getMediaBox (), fTextWidth, fTextHeight));
        aCS.showText (m_sText);
        aCS.endText ();
        aCS.restoreGraphicsState ();
        aCS.endMarkedContent ();
      }
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Text", m_sText)
                                       .append ("Font", m_aFont)
                                       .append ("FontSize", m_fFontSize)
                                       .append ("Color", m_aColor)
                                       .append ("Opacity", m_fOpacity)
                                       .append ("Rotation", m_fRotation)
                                       .append ("Position", m_ePosition)
                                       .append ("EdgeDistance", m_fEdgeDistance)
                                       .getToString ();
  }
}
