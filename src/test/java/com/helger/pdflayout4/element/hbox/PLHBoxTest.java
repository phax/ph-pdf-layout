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
package com.helger.pdflayout4.element.hbox;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.box.PLBox;
import com.helger.pdflayout4.element.hbox.PLHBox;
import com.helger.pdflayout4.element.special.PLPageBreak;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.EVertAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for {@link PLHBox}
 *
 * @author Philip Helger
 */
public final class PLHBoxTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  static
  {
    PLDebug.setDebugAll (false);
  }

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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-star-inline.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-star-block.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-perc-inline.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-perc-block.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-abs-inline.pdf"));
  }

  @Test
  public void testAbsoluteWidthBlock () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED),
                     WidthSpec.abs (80));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED),
                     WidthSpec.abs (120));
    aHBox.addColumn (new PLBox (new PLText (s, r10).setFillColor (Color.YELLOW)).setBorder (Color.RED),
                     WidthSpec.abs (80));
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-abs-block.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-auto-inline.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-auto-block.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-auto-block-like-star.pdf"));
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
    aPS1.addElement (new PLPageBreak (true));

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
          aHBox.addColumn (new PLBox (new PLText ("Ich bin auch ein Stern", r10)).setBorder (Color.GREEN),
                           WidthSpec.star ());
          aPS1.addElement (aHBox);
        }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-star-advanced.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-splittable-text.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-splittable-partially-filled.pdf"));
  }

  @Test
  public void testHBoxWithAlignmentOnElement () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText ("Test string\nto have more\nlines.", r10).setBorder (Color.RED), WidthSpec.auto ());
    aHBox.addColumn (new PLBox (new PLText ("Bottom/left", r10).setBorder (Color.RED))
                                                                                      .setHorzAlign (EHorzAlignment.LEFT)
                                                                                      .setVertAlign (EVertAlignment.BOTTOM)
                                                                                      .setFillColor (Color.YELLOW),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Middle/center", r10).setBorder (Color.RED))
                                                                                        .setHorzAlign (EHorzAlignment.CENTER)
                                                                                        .setVertAlign (EVertAlignment.MIDDLE)
                                                                                        .setFillColor (Color.BLUE),
                     WidthSpec.star ());
    aHBox.addColumn (new PLBox (new PLText ("Top/right", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                    .setVertAlign (EVertAlignment.TOP)
                                                                                    .setFillColor (Color.PINK),
                     WidthSpec.star ());
    aPS1.addElement (aHBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plhbox-alignment.pdf"));
  }
}
