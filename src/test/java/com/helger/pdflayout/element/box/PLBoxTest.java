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
package com.helger.pdflayout.element.box;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for {@link PLBox}
 *
 * @author Philip Helger
 */
public final class PLBoxTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  static
  {
    if (true)
      PLDebug.setDebugAll (true);
  }

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLBox ().setBorder (Color.RED).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox ().setPadding (5).setBorder (Color.RED).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox ().setMargin (5).setBorder (Color.RED).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox ().setMargin (5).setPadding (5).setBorder (Color.RED).setFillColor (Color.YELLOW));

    // Check horizontal alignment
    aPS1.addElement (new PLBox (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                        .setBorder (Color.RED)).setPadding (2)
                                                                               .setMargin (5)
                                                                               .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Left and\nLeft but longer",
                                            r10).setHorzAlign (EHorzAlignment.LEFT)
                                                .setBorder (Color.RED)
                                                .setFillColor (Color.PINK)).setPadding (2).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                          .setBorder (Color.RED)).setPadding (2)
                                                                                 .setMargin (5)
                                                                                 .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Center and\nCenter but longer",
                                            r10).setHorzAlign (EHorzAlignment.CENTER)
                                                .setBorder (Color.RED)
                                                .setFillColor (Color.PINK)).setPadding (2).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                         .setBorder (Color.RED)).setPadding (2)
                                                                                .setMargin (5)
                                                                                .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight but longer",
                                            r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                .setBorder (Color.RED)
                                                .setFillColor (Color.PINK)).setPadding (2).setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight with padding",
                                            r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                .setBorder (Color.RED)
                                                .setPadding (5)
                                                .setFillColor (Color.PINK)).setPadding (2)
                                                                           .setMargin (5)
                                                                           .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight but margin",
                                            r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                .setBorder (Color.RED)
                                                .setMargin (5)
                                                .setFillColor (Color.PINK)).setPadding (2)
                                                                           .setMargin (5)
                                                                           .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Text with margin and padding",
                                            r10).setBorder (Color.RED)
                                                .setMargin (5)
                                                .setPadding (5)
                                                .setFillColor (Color.PINK)).setPadding (2)
                                                                           .setMargin (5)
                                                                           .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Text with different borders,\nmargins and paddings",
                                            r10).setHorzAlign (EHorzAlignment.CENTER)
                                                .setBorder (new BorderStyleSpec (Color.RED, 5),
                                                            new BorderStyleSpec (Color.GREEN, 5),
                                                            new BorderStyleSpec (Color.BLUE, 5),
                                                            new BorderStyleSpec (Color.MAGENTA, 5))
                                                .setMargin (5)
                                                .setPadding (5)
                                                .setFillColor (Color.YELLOW)).setPadding (2)
                                                                             .setMargin (5)
                                                                             .setBorder (new BorderStyleSpec (Color.GREEN,
                                                                                                              5),
                                                                                         new BorderStyleSpec (Color.BLUE,
                                                                                                              5),
                                                                                         new BorderStyleSpec (Color.MAGENTA,
                                                                                                              5),
                                                                                         new BorderStyleSpec (Color.RED,
                                                                                                              5))
                                                                             .setFillColor (Color.GRAY));

    final BorderStyleSpec aBSS = new BorderStyleSpec (Color.BLACK);
    aPS1.addElement (new PLBox (new PLText ("Table cell example",
                                            r10).setBorder (Color.RED)
                                                .setPadding (5)
                                                .setFillColor (Color.PINK)).setPadding (2)
                                                                           .setMinSize (200, 0)
                                                                           .setBorder (aBSS)
                                                                           .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Next row", r10).setBorder (Color.RED)
                                                            .setPadding (5)
                                                            .setFillColor (Color.PINK)).setPadding (2)
                                                                                       .setMinSize (200, 0)
                                                                                       .setBorder (null,
                                                                                                   aBSS,
                                                                                                   aBSS,
                                                                                                   aBSS)
                                                                                       .setFillColor (Color.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-basic.pdf"));
  }
}
