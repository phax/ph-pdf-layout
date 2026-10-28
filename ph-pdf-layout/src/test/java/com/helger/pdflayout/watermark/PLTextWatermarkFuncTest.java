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

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.jspecify.annotations.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PDFTestComparer;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for {@link PLTextWatermark} that compares the rendered PDFs with the reference files
 * in <code>example-files/watermark</code>: every position, rotation, opacity and several pages.
 *
 * @author Philip Helger
 */
public final class PLTextWatermarkFuncTest
{
  private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ";

  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @NonNull
  private static PageLayoutPDF _createLayout (final int nParagraphs, @NonNull final PLTextWatermark... aWatermarks)
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    for (int i = 0; i < nParagraphs; ++i)
      aPS.addElement (new PLText ((i + 1) + ". " + TEXT + TEXT + TEXT, r10).setMarginBottom (6));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS);
    for (final PLTextWatermark aWatermark : aWatermarks)
      aPageLayout.addWatermark (aWatermark);
    return aPageLayout;
  }

  @NonNull
  private static PLTextWatermark _wm (@NonNull final String sText)
  {
    return new PLTextWatermark (sText, PreloadFont.REGULAR_BOLD);
  }

  @Test
  public void testCenterDefault () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10, _wm ("DRAFT")), new File ("pdf/watermark/center-default.pdf"));
  }

  @Test
  public void testCenterNoRotation () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10, _wm ("DRAFT").setRotation (0)),
                                      new File ("pdf/watermark/center-rotation-0.pdf"));
  }

  @Test
  public void testCenterNegativeRotation () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10, _wm ("COPY").setRotation (-45)),
                                      new File ("pdf/watermark/center-rotation-minus-45.pdf"));
  }

  @Test
  public void testCenterOpacity () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10,
                                                     _wm ("CONFIDENTIAL").setFontSize (60)
                                                                         .setColor (PLColor.RED)
                                                                         .setOpacity (0.3f)),
                                      new File ("pdf/watermark/center-opacity.pdf"));
  }

  @Test
  public void testTop () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10,
                                                     _wm ("Top watermark").setFontSize (14)
                                                                          .setColor (PLColor.BLUE)
                                                                          .setPosition (EPLWatermarkPosition.TOP)),
                                      new File ("pdf/watermark/top.pdf"));
  }

  @Test
  public void testBottom () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10,
                                                     _wm ("Bottom watermark").setFontSize (14)
                                                                             .setColor (PLColor.BLUE)
                                                                             .setPosition (EPLWatermarkPosition.BOTTOM)),
                                      new File ("pdf/watermark/bottom.pdf"));
  }

  @Test
  public void testBottomRight () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10,
                                                     _wm ("Bottom right watermark").setFontSize (8)
                                                                                   .setColor (PLColor.GRAY)
                                                                                   .setPosition (EPLWatermarkPosition.BOTTOM_RIGHT)),
                                      new File ("pdf/watermark/bottom-right.pdf"));
  }

  @Test
  public void testEdgeDistance () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (10,
                                                     _wm ("Top, 30 pt").setFontSize (14)
                                                                       .setPosition (EPLWatermarkPosition.TOP)
                                                                       .setEdgeDistance (30),
                                                     _wm ("Bottom, 0 pt").setFontSize (14)
                                                                         .setPosition (EPLWatermarkPosition.BOTTOM)
                                                                         .setEdgeDistance (0)),
                                      new File ("pdf/watermark/edge-distance.pdf"));
  }

  @Test
  public void testAllPositionsOnSeveralPages () throws PDFCreationException
  {
    PDFTestComparer.renderAndCompare (_createLayout (40,
                                                     _wm ("DRAFT").setOpacity (0.5f),
                                                     _wm ("Top").setFontSize (14).setPosition (EPLWatermarkPosition.TOP),
                                                     _wm ("Bottom").setFontSize (14)
                                                                   .setPosition (EPLWatermarkPosition.BOTTOM),
                                                     _wm ("Bottom right").setFontSize (8)
                                                                         .setPosition (EPLWatermarkPosition.BOTTOM_RIGHT)),
                                      new File ("pdf/watermark/all-positions-several-pages.pdf"));
  }
}
