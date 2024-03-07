/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;
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
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLBox ().setBorder (PLColor.RED).setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox ().setPadding (5).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox ().setMargin (5).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox ().setMargin (5).setPadding (5).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW));

    // Check horizontal alignment
    aPS1.addElement (new PLBox (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (PLColor.RED))
                                                                                                                    .setPadding (2)
                                                                                                                    .setMargin (5)
                                                                                                                    .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                             .setBorder (PLColor.RED)
                                                                             .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                          .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.RED))
                                                                                                                        .setPadding (2)
                                                                                                                        .setMargin (5)
                                                                                                                        .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                                 .setBorder (PLColor.RED)
                                                                                 .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                              .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (PLColor.RED))
                                                                                                                      .setPadding (2)
                                                                                                                      .setMargin (5)
                                                                                                                      .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                               .setBorder (PLColor.RED)
                                                                               .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                            .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                 .setBorder (PLColor.RED)
                                                                                 .setPadding (5)
                                                                                 .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                              .setMargin (5)
                                                                                                              .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                               .setBorder (PLColor.RED)
                                                                               .setMargin (5)
                                                                               .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                            .setMargin (5)
                                                                                                            .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Text with margin and padding", r10).setBorder (PLColor.RED)
                                                                                .setMargin (5)
                                                                                .setPadding (5)
                                                                                .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                             .setMargin (5)
                                                                                                             .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Text with different borders,\nmargins and paddings", r10).setHorzAlign (
                                                                                                                     EHorzAlignment.CENTER)
                                                                                                      .setBorder (new BorderStyleSpec (PLColor.RED,
                                                                                                                                       5),
                                                                                                                  new BorderStyleSpec (PLColor.GREEN,
                                                                                                                                       5),
                                                                                                                  new BorderStyleSpec (PLColor.BLUE,
                                                                                                                                       5),
                                                                                                                  new BorderStyleSpec (PLColor.MAGENTA,
                                                                                                                                       5))
                                                                                                      .setMargin (5)
                                                                                                      .setPadding (5)
                                                                                                      .setFillColor (PLColor.YELLOW)).setPadding (2)
                                                                                                                                     .setMargin (5)
                                                                                                                                     .setBorder (new BorderStyleSpec (PLColor.GREEN,
                                                                                                                                                                      5),
                                                                                                                                                 new BorderStyleSpec (PLColor.BLUE,
                                                                                                                                                                      5),
                                                                                                                                                 new BorderStyleSpec (PLColor.MAGENTA,
                                                                                                                                                                      5),
                                                                                                                                                 new BorderStyleSpec (PLColor.RED,
                                                                                                                                                                      5))
                                                                                                                                     .setFillColor (PLColor.GRAY));

    aPS1.addElement (new PLBox (new PLBox (new PLText ("Text with MBP in Box in Box", r10).setBorder (PLColor.RED)
                                                                                          .setMargin (5)
                                                                                          .setPadding (5)
                                                                                          .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                                       .setMargin (5)
                                                                                                                       .setFillColor (PLColor.YELLOW)
                                                                                                                       .setBorder (PLColor.RED)).setPadding (2)
                                                                                                                                                .setMargin (5)
                                                                                                                                                .setFillColor (PLColor.GREEN)
                                                                                                                                                .setBorder (PLColor.BLUE));

    final BorderStyleSpec aBSS = new BorderStyleSpec (PLColor.BLACK, 1);
    aPS1.addElement (new PLBox (new PLText ("Table cell example", r10).setBorder (PLColor.RED)
                                                                      .setPadding (5)
                                                                      .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                   .setMinWidth (200)
                                                                                                   .setBorder (aBSS)
                                                                                                   .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLBox (new PLText ("Next row", r10).setBorder (PLColor.RED)
                                                            .setPadding (5)
                                                            .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                         .setMinWidth (200)
                                                                                         .setBorder (null,
                                                                                                     aBSS,
                                                                                                     aBSS,
                                                                                                     aBSS)
                                                                                         .setFillColor (PLColor.YELLOW));
    // Use min size on text as well
    aPS1.addElement (new PLBox (new PLText ("Third row", r10).setBorder (PLColor.RED)
                                                             .setPadding (5)
                                                             .setFillColor (PLColor.PINK)
                                                             .setHorzAlign (EHorzAlignment.RIGHT)
                                                             .setMinWidth (150)).setPadding (2)
                                                                                .setMinWidth (200)
                                                                                .setBorder (null, aBSS, aBSS, aBSS)
                                                                                .setFillColor (PLColor.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plbox/basic.pdf"));
  }

  @Test
  public void testAlignment () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (300, 300)).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLBox (new PLText ("Text " + eH.getID () + " / " + eV.getID (), r10).setFillColor (
                                                                                                                PLColor.PINK)).setFillColor (PLColor.YELLOW)
                                                                                                                              .setExactSize (150,
                                                                                                                                             30)
                                                                                                                              .setHorzAlign (eH)
                                                                                                                              .setVertAlign (eV)
                                                                                                                              .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plbox/alignment.pdf"));
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
                                                r10).setHorzAlign (EHorzAlignment.CENTER).setFillColor (PLColor.PINK))
                                                                                                                      .setFillColor (PLColor.YELLOW)
                                                                                                                      .setExactSize (150,
                                                                                                                                     50)
                                                                                                                      .setHorzAlign (eH)
                                                                                                                      .setVertAlign (eV)
                                                                                                                      .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plbox/alignment-nested.pdf"));
  }

  @Test
  public void testPageBreak () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 20);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLBox (new PLText ("Text " + eH.getID () + " / " + eV.getID (), r10).setFillColor (
                                                                                                                PLColor.PINK)).setFillColor (PLColor.YELLOW)
                                                                                                                              .setExactSize (250,
                                                                                                                                             120)
                                                                                                                              .setHorzAlign (eH)
                                                                                                                              .setVertAlign (eV)
                                                                                                                              .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plbox/pagebreak.pdf"));
  }

  @Test
  public void testPageBreakSplittable () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (int i = 0; i < 100; ++i)
      aPS1.addElement (new PLBox (new PLText ("Text " + i + "\nline 2 of 4\nshort\nAnd finally the last line", r10)
                                                                                                                   .setFillColor (PLColor.PINK)
                                                                                                                   .setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                   .setVertSplittable (true)).setVertSplittable (true)
                                                                                                                                             .setFillColor (PLColor.YELLOW)
                                                                                                                                             .setPadding (8)
                                                                                                                                             .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plbox/splittable-pagebreak.pdf"));
  }
}
