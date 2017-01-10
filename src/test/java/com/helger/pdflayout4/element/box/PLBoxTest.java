/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.box;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.EVertAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * Test class for {@link PLBox}
 *
 * @author Philip Helger
 */
public final class PLBoxTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

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

    aPS1.addElement (new PLBox (new PLBox (new PLText ("Text with MBP in Box in Box",
                                                       r10).setBorder (Color.RED)
                                                           .setMargin (5)
                                                           .setPadding (5)
                                                           .setFillColor (Color.PINK)).setPadding (2)
                                                                                      .setMargin (5)
                                                                                      .setFillColor (Color.YELLOW)
                                                                                      .setBorder (Color.RED)).setPadding (2)
                                                                                                             .setMargin (5)
                                                                                                             .setFillColor (Color.GREEN)
                                                                                                             .setBorder (Color.BLUE));

    final BorderStyleSpec aBSS = new BorderStyleSpec (Color.BLACK, 1);
    aPS1.addElement (new PLBox (new PLText ("Table cell example",
                                            r10).setBorder (Color.RED)
                                                .setPadding (5)
                                                .setFillColor (Color.PINK)).setPadding (2)
                                                                           .setMinWidth (200)
                                                                           .setBorder (aBSS)
                                                                           .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Next row", r10).setBorder (Color.RED)
                                                            .setPadding (5)
                                                            .setFillColor (Color.PINK)).setPadding (2)
                                                                                       .setMinWidth (200)
                                                                                       .setBorder (null,
                                                                                                   aBSS,
                                                                                                   aBSS,
                                                                                                   aBSS)
                                                                                       .setFillColor (Color.YELLOW));
    // Use min size on text as well
    aPS1.addElement (new PLBox (new PLText ("Third row", r10).setBorder (Color.RED)
                                                             .setPadding (5)
                                                             .setFillColor (Color.PINK)
                                                             .setHorzAlign (EHorzAlignment.RIGHT)
                                                             .setMinWidth (150)).setPadding (2)
                                                                                .setMinWidth (200)
                                                                                .setBorder (null, aBSS, aBSS, aBSS)
                                                                                .setFillColor (Color.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-basic.pdf"));
  }

  @Test
  public void testAlignment () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (300, 300)).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLBox (new PLText ("Text " +
                                                eH.getID () +
                                                " / " +
                                                eV.getID (),
                                                r10).setFillColor (Color.PINK)).setFillColor (Color.YELLOW)
                                                                               .setExactSize (150, 30)
                                                                               .setHorzAlign (eH)
                                                                               .setVertAlign (eV)
                                                                               .setBorder (Color.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-alignment.pdf"));
  }

  @Test
  public void testNestedAlignment () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLBox (new PLText ("Text " +
                                                eH.getID () +
                                                " / " +
                                                eV.getID () +
                                                "\nText is always centered\nLine 3",
                                                r10).setHorzAlign (EHorzAlignment.CENTER)
                                                    .setFillColor (Color.PINK)).setFillColor (Color.YELLOW)
                                                                               .setExactSize (150, 50)
                                                                               .setHorzAlign (eH)
                                                                               .setVertAlign (eV)
                                                                               .setBorder (Color.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-alignment-nested.pdf"));
  }

  @Test
  public void testPageBreak () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 20);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLBox (new PLText ("Text " +
                                                eH.getID () +
                                                " / " +
                                                eV.getID (),
                                                r10).setFillColor (Color.PINK)).setFillColor (Color.YELLOW)
                                                                               .setExactSize (250, 120)
                                                                               .setHorzAlign (eH)
                                                                               .setVertAlign (eV)
                                                                               .setBorder (Color.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-pagebreak.pdf"));
  }

  @Test
  public void testPageBreakSplittable () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (int i = 0; i < 100; ++i)
      aPS1.addElement (new PLBox (new PLText ("Text " +
                                              i +
                                              "\nline 2 of 4\nshort\nAnd finally the last line",
                                              r10).setFillColor (Color.PINK)
                                                  .setHorzAlign (EHorzAlignment.RIGHT)
                                                  .setVertSplittable (true)).setVertSplittable (true)
                                                                            .setFillColor (Color.YELLOW)
                                                                            .setPadding (8)
                                                                            .setBorder (Color.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plbox-splittable-pagebreak.pdf"));
  }
}
