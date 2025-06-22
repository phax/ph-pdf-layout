/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PDFTestComparer;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.File;

/**
 * Test class for {@link PLRBox}
 *
 * @author Philip Helger
 * @author Marco De Angelis
 */
public final class PLRBoxTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  @Ignore
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLRBox ().setBorder (PLColor.RED).setFillColor (PLColor.YELLOW).setBorderRadius (2));
    aPS1.addElement (new PLRBox ().setPadding (10).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW).setBorderRadius (2));
    aPS1.addElement (new PLRBox ().setMargin (10).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW).setBorderRadius (2));
    aPS1.addElement (new PLRBox ().setMargin (10).setPadding (5).setBorder (PLColor.RED).setFillColor (PLColor.YELLOW));

    // Check horizontal alignment
    aPS1.addElement (new PLRBox (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (PLColor.RED))
                                                                                                                    .setPadding (2)
                                                                                                                    .setMargin (5)
                                                                                                                    .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                             .setBorder (PLColor.RED)
                                                                             .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                          .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.RED))
                                                                                                                        .setPadding (2)
                                                                                                                        .setMargin (5)
                                                                                                                        .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                                 .setBorder (PLColor.RED)
                                                                                 .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                              .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (PLColor.RED))
                                                                                                                      .setPadding (2)
                                                                                                                      .setMargin (5)
                                                                                                                      .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                               .setBorder (PLColor.RED)
                                                                               .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                            .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                 .setBorder (PLColor.RED)
                                                                                 .setPadding (5)
                                                                                 .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                              .setMargin (5)
                                                                                                              .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                               .setBorder (PLColor.RED)
                                                                               .setMargin (5)
                                                                               .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                            .setMargin (5)
                                                                                                            .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLText ("Text with margin and padding", r10).setBorder (PLColor.RED)
                                                                                .setMargin (5)
                                                                                .setPadding (5)
                                                                                .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                             .setMargin (5)
                                                                                                             .setFillColor (PLColor.YELLOW));
    aPS1.addElement (new PLRBox (new PLRBox (new PLText ("Text with MBP in Box in Box", r10).setBorder (PLColor.RED)
                                                                                          .setMargin (5)
                                                                                          .setPadding (5)
                                                                                          .setFillColor (PLColor.PINK)).setPadding (2)
                                                                                                                       .setMargin (5)
                                                                                                                       .setFillColor (PLColor.YELLOW)
                                                                                                                       .setBorder (PLColor.RED)).setPadding (2)
                                                                                                                                                .setMargin (5)
                                                                                                                                                .setFillColor (PLColor.GREEN)
                                                                                                                                                .setBorder (PLColor.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrbox/basic.pdf"));
  }

  @Test
  @Ignore
  public void testAlignment () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (300, 300)).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLRBox (new PLText ("Text " + eH.getID () + " / " + eV.getID (), r10).setFillColor (
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
  @Ignore
  public void testNestedAlignment () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLRBox (new PLText ("Text " +
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
                                                                                                                      .setBorder (PLColor.BLACK)
                                                                                                                      .setClipContent(true)
        );

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrbox/alignment-nested.pdf"));
  }

  @Test
  @Ignore
  public void testPageBreak () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 20);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (final EHorzAlignment eH : EHorzAlignment.values ())
      for (final EVertAlignment eV : EVertAlignment.values ())
        aPS1.addElement (new PLRBox (new PLText ("Text " + eH.getID () + " / " + eV.getID (), r10).setFillColor (
                                                                                                                PLColor.PINK)).setFillColor (PLColor.YELLOW)
                                                                                                                              .setExactSize (250,
                                                                                                                                             120)
                                                                                                                              .setHorzAlign (eH)
                                                                                                                              .setVertAlign (eV)
                                                                                                                              .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrbox/pagebreak.pdf"));
  }

  @Test
  @Ignore
  public void testPageBreakSplittable () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (10);

    for (int i = 0; i < 100; ++i)
      aPS1.addElement (new PLRBox (new PLText ("Text " + i + "\nline 2 of 4\nshort\nAnd finally the last line", r10)
                                                                                                                   .setFillColor (PLColor.PINK)
                                                                                                                   .setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                   .setVertSplittable (true)).setVertSplittable (true)
                                                                                                                                             .setFillColor (PLColor.YELLOW)
                                                                                                                                             .setPadding (8)
                                                                                                                                             .setBorder (PLColor.BLACK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrbox/splittable-pagebreak.pdf"));
  }
}
