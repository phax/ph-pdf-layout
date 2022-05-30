/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.element.hbox;

import com.plenigo.pdflayout.PDFCreationException;
import com.plenigo.pdflayout.PLDebugTestRule;
import com.plenigo.pdflayout.PageLayoutPDF;
import com.plenigo.pdflayout.base.PLPageSet;
import com.plenigo.pdflayout.element.box.PLBox;
import com.plenigo.pdflayout.element.special.PLPageBreak;
import com.plenigo.pdflayout.element.text.PLText;
import com.plenigo.pdflayout.spec.EHorzAlignment;
import com.plenigo.pdflayout.spec.EVertAlignment;
import com.plenigo.pdflayout.spec.FontSpec;
import com.plenigo.pdflayout.spec.PreloadFont;
import com.plenigo.pdflayout.spec.WidthSpec;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.awt.*;
import java.io.File;

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
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.star ());
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.star ());
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/star-inline.pdf"));
  }

  @Test
  public void testStarWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/star-block.pdf"));
  }

  @Test
  public void testPercWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.perc (20));
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.perc (40));
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.perc (40));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/perc-inline.pdf"));
  }

  @Test
  public void testPercWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.perc (20));
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.perc (40));
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.perc (40));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/perc-block.pdf"));
  }

  @Test
  public void testAbsoluteWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.abs (80));
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.abs (120));
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.abs (80));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/abs-inline.pdf"));
  }

  @Test
  public void testAbsoluteWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED), WidthSpec.abs (80));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED), WidthSpec.abs (120));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED), WidthSpec.abs (80));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/abs-block.pdf"));
  }

  @Test
  public void testAutoWidthInline () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLText (s, r10).setBorder (Color.RED), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/auto-inline.pdf"));
  }

  @Test
  public void testAutoWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED).setMinWidth (200), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED).setMinWidth (200), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/auto-block.pdf"));
  }

  @Test
  public void testAutoWidthBlockBehaveLikeStar () throws PDFCreationException
  {
    final String s = "This is a test String. That is followed by a String which is also followed by some text just so that the text gets long enough to create 'star' like behaviour!";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED).setMinWidth (200), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText (s, r10)).setBorder (Color.RED).setMinWidth (200), WidthSpec.auto ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/auto-block-like-star.pdf"));
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
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aPS1.addElement (aHBox);
        }
      aPS1.addElement(new PLPageBreak(true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), WidthSpec.star ());
          aPS1.addElement (aHBox);
        }
    aPS1.addElement (new PLPageBreak (true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), WidthSpec.star ());
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aPS1.addElement (aHBox);
        }
    aPS1.addElement (new PLPageBreak (true));

    for (int i = 0; i < 2; ++i)
      for (int j = 0; j < 2; ++j)
        for (int k = 0; k < 2; ++k)
        {
          final PLHBox aHBox = new PLHBox ();
          aHBox.addColumn (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), WidthSpec.star ());
          aHBox.addColumn (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.RED), WidthSpec.auto ());
          aHBox.addColumn (new PLBox (new PLText ("Ich bin auch ein Stern", r10)).setBorder (Color.GREEN), WidthSpec.star ());
          aPS1.addElement (aHBox);
        }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/star-advanced.pdf"));
  }

  @Test
  public void testHBoxWithPageBreak () throws PDFCreationException
  {
    final String s = "http://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLink";
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("Line before", r10).setBorder (Color.RED));
    final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
    for (int i = 0; i < 3; ++i)
      aHBox.addColumn (new PLText (s + s, r10).setMargin (10)
                                              .setPadding (5)
                                              .setBorder (Color.GRAY)
                                              .setFillColor (Color.PINK)
                                              .setVertSplittable (true),
                       WidthSpec.star ());
    aPS1.addElement (aHBox);
    aPS1.addElement (new PLText ("Line after", r10).setBorder (Color.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/splittable-text.pdf"));
  }

  @Test
  public void testHBoxWithPageBreakPartial () throws PDFCreationException
  {
    final String s = "http://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLink";
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("Line before", r10).setBorder (Color.RED));
    final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
    for (int i = 0; i < 3; ++i)
      aHBox.addColumn (new PLText (i == 1 ? "" : s + s, r10).setMargin (10)
                                                            .setPadding (5)
                                                            .setBorder (Color.GRAY)
                                                            .setFillColor (Color.PINK)
                                                            .setVertSplittable (true),
                       WidthSpec.star ());
    aPS1.addElement (aHBox);
    aPS1.addElement (new PLText ("Line after", r10).setBorder (Color.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/splittable-partially-filled.pdf"));
  }

  @Test
  public void testHBoxWithAlignmentOnElement () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText ("Test string\nto have more\nlines.", r10).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText ("Bottom/left", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.LEFT)
                                                                                      .setVertAlign (EVertAlignment.BOTTOM)
                                                                                      .setFillColor (Color.YELLOW),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Middle/center", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.CENTER)
                                                                                        .setVertAlign (EVertAlignment.MIDDLE)
                                                                                        .setFillColor (Color.BLUE),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Top/right", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                    .setVertAlign (EVertAlignment.TOP)
                                                                                    .setFillColor (Color.PINK),
                     WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plhbox/alignment.pdf"));
  }
}
