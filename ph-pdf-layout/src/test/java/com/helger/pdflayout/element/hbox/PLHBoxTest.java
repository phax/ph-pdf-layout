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
package com.helger.pdflayout.element.hbox;

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
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Test class for {@link PLHBox}
 *
 * @author Philip Helger
 */
public final class PLHBoxTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testStarWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.star ());
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.star ());
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/star-inline.pdf"));
  }

  @Test
  public void testStarWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/star-block.pdf"));
  }

  @Test
  public void testPercWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.perc (20));
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.perc (40));
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.perc (40));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/perc-inline.pdf"));
  }

  @Test
  public void testPercWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.perc (20));
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.perc (40));
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.perc (40));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/perc-block.pdf"));
  }

  @Test
  public void testAbsoluteWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.abs (80));
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.abs (120));
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.abs (80));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/abs-inline.pdf"));
  }

  @Test
  public void testAbsoluteWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (PLColor.YELLOW)).setBorder (PLColor.RED),
                     WidthSpec.abs (80));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (PLColor.YELLOW)).setBorder (PLColor.RED),
                     WidthSpec.abs (120));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (PLColor.YELLOW)).setBorder (PLColor.RED),
                     WidthSpec.abs (80));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/abs-block.pdf"));
  }

  @Test
  public void testAutoWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLText (s, r10).setBorder (PLColor.RED), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/auto-inline.pdf"));
  }

  @Test
  public void testAutoWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED).setMinWidth (200), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED).setMinWidth (200), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/auto-block.pdf"));
  }

  @Test
  public void testAutoWidthBlockBehaveLikeStar () throws PDFCreationException
  {
    final String s = "This is a test String. That is followed by a String which is also followed by some text just so that the text gets long enough to create 'star' like behaviour!";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED).setMinWidth (200), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (PLColor.RED).setMinWidth (200), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/auto-block-like-star.pdf"));
  }

  @Test
  public void testAutoWidthAdvanced () throws PDFCreationException
  {
    final String s1 = "This is a test";
    final String s2 = "This is also a test string but much much much much longer as the other one. Can you believe this???? No this is not believable";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aPS1.addElement (aHBox);
        }
    aPS1.addElement (new PLPageBreak (true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (PLColor.BLUE),
                           WidthSpec.star ());
          aPS1.addElement (aHBox);
        }
    aPS1.addElement (new PLPageBreak (true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (PLColor.BLUE),
                           WidthSpec.star ());
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aPS1.addElement (aHBox);
        }
    aPS1.addElement (new PLPageBreak (true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (PLColor.BLUE),
                           WidthSpec.star ());
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (PLColor.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText ("Ich bin auch ein Stern", r10)).setBorder (PLColor.GREEN),
                           WidthSpec.star ());
          aPS1.addElement (aHBox);
        }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/star-advanced.pdf"));
  }

  @Test
  public void testHBoxWithPageBreak () throws PDFCreationException
  {
    final String s = "http://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLink";
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("Line before", r10).setBorder (PLColor.RED));
    final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
    for (int i = 0; i < 3; ++i)
      aHBox.addColumn (new PLText (s + s, r10).setMargin (10)
                                              .setPadding (5)
                                              .setBorder (PLColor.GRAY)
                                              .setFillColor (PLColor.PINK)
                                              .setVertSplittable (true), WidthSpec.star ());
    aPS1.addElement (aHBox);
    aPS1.addElement (new PLText ("Line after", r10).setBorder (PLColor.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/splittable-text.pdf"));
  }

  @Test
  public void testHBoxWithPageBreakPartial () throws PDFCreationException
  {
    final String s = "http://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLink";
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("Line before", r10).setBorder (PLColor.RED));
    final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
    for (int i = 0; i < 3; ++i)
      aHBox.addColumn (new PLText (i == 1 ? "" : s + s, r10).setMargin (10)
                                                            .setPadding (5)
                                                            .setBorder (PLColor.GRAY)
                                                            .setFillColor (PLColor.PINK)
                                                            .setVertSplittable (true), WidthSpec.star ());
    aPS1.addElement (aHBox);
    aPS1.addElement (new PLText ("Line after", r10).setBorder (PLColor.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/splittable-partially-filled.pdf"));
  }

  @Test
  public void testHBoxWithAlignmentOnElement () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText ("Test string\nto have more\nlines.", r10).setBorder (PLColor.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText ("Bottom/left", r10).setBorder (PLColor.RED)).setHorzAlign (
                                                                                                       EHorzAlignment.LEFT)
                                                                                        .setVertAlign (EVertAlignment.BOTTOM)
                                                                                        .setFillColor (PLColor.YELLOW),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Middle/center", r10).setBorder (PLColor.RED)).setHorzAlign (
                                                                                                         EHorzAlignment.CENTER)
                                                                                          .setVertAlign (EVertAlignment.MIDDLE)
                                                                                          .setFillColor (PLColor.BLUE),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Top/right", r10).setBorder (PLColor.RED)).setHorzAlign (
                                                                                                     EHorzAlignment.RIGHT)
                                                                                      .setVertAlign (EVertAlignment.TOP)
                                                                                      .setFillColor (PLColor.PINK),
                     WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plhbox/alignment.pdf"));
  }
}
