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
package com.helger.pdflayout4.element.vbox;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.commons.string.StringHelper;
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
import com.helger.pdflayout4.spec.HeightSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.SizeSpec;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for {@link PLVBox}
 *
 * @author Philip Helger
 */
public final class PLVBoxTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  static
  {
    PLDebug.setDebugAll (false);
  }

  @Test
  public void testBasic () throws PDFCreationException
  {
    final String s = "This is a test String";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLVBox aVBox = new PLVBox ();
    aVBox.addRow (new PLText (s, r10).setBorder (Color.RED));

    // Check horizontal alignment
    aVBox.addRow (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (Color.RED).setID ("left1"));
    aVBox.addRow (new PLText ("Left\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                           .setBorder (Color.RED)
                                                           .setID ("left2"));
    aVBox.addRow (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER)
                                            .setBorder (Color.RED)
                                            .setID ("center1"));
    aVBox.addRow (new PLText ("Center\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                               .setBorder (Color.RED)
                                                               .setID ("center2"));
    aVBox.addRow (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (Color.RED).setID ("right1"));
    aVBox.addRow (new PLText ("Right\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                             .setBorder (Color.RED)
                                                             .setMarginTop (2)
                                                             .setPadding (5)
                                                             .setFillColor (Color.PINK)
                                                             .setID ("right2"));
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (true);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-basic.pdf"));
  }

  @Test
  public void testStarAutoStarFullWidth () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (new SizeSpec (400, 600));

    final PLVBox aVBox = new PLVBox ();
    aVBox.addRow (new PLText ("This is a test String determining the width of the content", r10).setBorder (Color.RED));

    // Check horizontal alignment
    aVBox.addRow (new PLBox (new PLText ("Left/top", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.LEFT)
                                                                                .setVertAlign (EVertAlignment.TOP)
                                                                                .setFillColor (Color.YELLOW),
                  HeightSpec.star ());
    aVBox.addRow (new PLBox (new PLText ("Center/middle", r10).setBorder (Color.RED))
                                                                                     .setHorzAlign (EHorzAlignment.CENTER)
                                                                                     .setVertAlign (EVertAlignment.MIDDLE)
                                                                                     .setFillColor (Color.PINK),
                  HeightSpec.star ());
    aVBox.addRow (new PLBox (new PLText ("Right/bottom", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                    .setVertAlign (EVertAlignment.BOTTOM)
                                                                                    .setFillColor (Color.MAGENTA),
                  HeightSpec.star ());
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-height-star.pdf"));
  }

  @Test
  public void testStarAutoStarFullWidthNotFullWidth () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLVBox aVBox = new PLVBox ().setFullWidth (false);
    aVBox.addRow (new PLText ("This is a test String determining the width of the content", r10).setBorder (Color.RED));

    // Check horizontal alignment
    aVBox.addRow (new PLBox (new PLText ("Left", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.LEFT)
                                                                            .setFillColor (Color.YELLOW),
                  HeightSpec.star ());
    aVBox.addRow (new PLBox (new PLText ("Center", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.CENTER)
                                                                              .setFillColor (Color.PINK),
                  HeightSpec.auto ());
    aVBox.addRow (new PLBox (new PLText ("Right", r10).setBorder (Color.RED)).setHorzAlign (EHorzAlignment.RIGHT)
                                                                             .setFillColor (Color.MAGENTA),
                  HeightSpec.star ());
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-height-star-small.pdf"));
  }

  @Test
  public void testAutoHeightAdvanced () throws PDFCreationException
  {
    final String s1 = "This is a test";
    final String s2 = "This is also a test string \nbut much much much much longer as the other one. \nCan you believe this???? \nNo this is not believable\nThis\nshall\ncreate\nmore\nlines\n!";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (int h = 0; h < 2; h++)
      for (int i = 0; i < 2; ++i)
        for (int j = 0; j < 2; ++j)
          for (int k = 0; k < 2; ++k)
          {
            final PLVBox aVBox = new PLVBox ().setFullWidth (h == 0);
            aVBox.addRow (new PLText ("This should be a " +
                                      (h == 0 ? "full-width " : "") +
                                      "example.",
                                      r10).setBorder (Color.RED));
            aVBox.addRow (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.YELLOW), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.GREEN), HeightSpec.auto ());
            aPS1.addElement (aVBox);
          }
    aPS1.addElement (new PLPageBreak (true));

    for (int h = 0; h < 2; h++)
      for (int i = 0; i < 2; ++i)
        for (int j = 0; j < 2; ++j)
          for (int k = 0; k < 2; ++k)
          {
            final PLVBox aVBox = new PLVBox ().setFullWidth (h == 0);
            aVBox.addRow (new PLText ("This should be a " +
                                      (h == 0 ? "full-width " : "") +
                                      "example.",
                                      r10).setBorder (Color.RED));
            aVBox.addRow (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.YELLOW), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.GREEN), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), HeightSpec.star ());
            aPS1.addElement (aVBox);
          }
    aPS1.addElement (new PLPageBreak (true));

    for (int h = 0; h < 2; h++)
      for (int i = 0; i < 2; ++i)
        for (int j = 0; j < 2; ++j)
          for (int k = 0; k < 2; ++k)
          {
            final PLVBox aVBox = new PLVBox ().setFullWidth (h == 0);
            aVBox.addRow (new PLText ("This should be a " +
                                      (h == 0 ? "full-width " : "") +
                                      "example.",
                                      r10).setBorder (Color.RED));
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), HeightSpec.star ());
            aVBox.addRow (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.YELLOW), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.GREEN), HeightSpec.auto ());
            aPS1.addElement (aVBox);
          }
    aPS1.addElement (new PLPageBreak (true));

    for (int h = 0; h < 2; h++)
      for (int i = 0; i < 2; ++i)
        for (int j = 0; j < 2; ++j)
          for (int k = 0; k < 2; ++k)
          {
            final PLVBox aVBox = new PLVBox ().setFullWidth (h == 0);
            aVBox.addRow (new PLText ("This should be a " +
                                      (h == 0 ? "full-width " : "") +
                                      "example.",
                                      r10).setBorder (Color.RED));
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), HeightSpec.star ());
            aVBox.addRow (new PLBox (new PLText (i == 0 ? s1 : s2, r10)).setBorder (Color.RED), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (j == 0 ? s1 : s2, r10)).setBorder (Color.YELLOW), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (k == 0 ? s1 : s2, r10)).setBorder (Color.GREEN), HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10)).setBorder (Color.BLUE), HeightSpec.star ());
            aPS1.addElement (aVBox);
          }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-auto-advanced.pdf"));
  }

  @Test
  public void testAutoHeightAdvancedSplittable () throws PDFCreationException
  {
    final String s1 = "This is a test";
    final String s2 = "This is also a test string \nbut much much much much longer as the other one. \nCan you believe this???? \nNo this is not believable\nThis\nshall\ncreate\nmore\nlines\n\n!";
    final String s3 = StringHelper.getRepeated (s2, 6);

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setID ("p");

    int nCount = 0;
    for (int h = 0; h < 2; h++)
      for (int i = 0; i < 2; ++i)
        for (int j = 0; j < 2; ++j)
          for (int k = 0; k < 2; ++k)
          {
            if (nCount++ > 0)
            {
              aPS1.addElement (new PLText ("Forced page break following", r10));
              aPS1.addElement (new PLPageBreak (false));
            }

            final String sIDPrefix = h + "-" + i + "-" + j + "-" + k + "-";
            final PLVBox aVBox = new PLVBox ().setID (sIDPrefix + "vbox")
                                              .setFullWidth (h == 0)
                                              .setVertSplittable (true);
            aVBox.addRow (new PLText ("This is a " +
                                      (h == 0 ? "full-width" : "regular width") +
                                      " example (" +
                                      sIDPrefix +
                                      ").",
                                      r10).setBorder (Color.RED));
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10).setID (sIDPrefix + "t1"))
                                                                                                    .setID (sIDPrefix +
                                                                                                            "star1")
                                                                                                    .setBorder (Color.BLUE),
                          HeightSpec.star ());
            aVBox.addRow (new PLBox (new PLText (i == 0 ? s1 : s3, r10).setID (sIDPrefix + "t2")
                                                                       .setVertSplittable (true)).setID (sIDPrefix +
                                                                                                         "auto1")
                                                                                                 .setBorder (Color.RED)
                                                                                                 .setVertSplittable (true),
                          HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (j == 0 ? s1 : s3, r10).setID (sIDPrefix + "t3")
                                                                       .setVertSplittable (true)).setID (sIDPrefix +
                                                                                                         "auto2")
                                                                                                 .setBorder (Color.YELLOW)
                                                                                                 .setVertSplittable (true),
                          HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText (k == 0 ? s1 : s3, r10).setID (sIDPrefix + "t4")
                                                                       .setVertSplittable (true)).setID (sIDPrefix +
                                                                                                         "auto3")
                                                                                                 .setBorder (Color.GREEN)
                                                                                                 .setVertSplittable (true),
                          HeightSpec.auto ());
            aVBox.addRow (new PLBox (new PLText ("Ich bin ein Stern", r10).setID (sIDPrefix + "t5"))
                                                                                                    .setID (sIDPrefix +
                                                                                                            "star2")
                                                                                                    .setBorder (Color.BLUE),
                          HeightSpec.star ());
            aPS1.addElement (aVBox);
          }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-auto-advanced-splittable.pdf"));
  }

  @Test
  public void testVBoxWithPageBreak () throws PDFCreationException
  {
    final String s = "http://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLinkhttp://portal-dev.ecosio.com/outbox?25-1.ILinkListener-mailboxPanel-messagesGrid-receivedMessagesTable-body-rows-1-cells-5-cell-1-container-detailBar-editLi!!";
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLVBox aVBox = new PLVBox ().setVertSplittable (true);
    for (int i = 0; i < 3; ++i)
      aVBox.addRow (new PLText (s, r10).setPadding (5).setBorder (Color.RED).setVertSplittable (true));
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-splittable-text.pdf"));
  }

  @Test
  public void testSplittableContentSplittable () throws PDFCreationException
  {
    final String s = "This is a test String\nwith 2 lines";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLVBox aVBox = new PLVBox ().setVertSplittable (true);
    for (int i = 0; i < 40; ++i)
    {
      final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
      for (int j = 0; j < 10; ++j)
        aHBox.addColumn (new PLText (s + (j == 0 ? s : ""), r10).setBorder (new Color (j, j * 10, j * 20))
                                                                .setVertSplittable (true),
                         WidthSpec.star ());
      aVBox.addRow (aHBox);
    }
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-splittable-content-splittable.pdf"));
  }

  @Test
  public void testSplittableContentFixed () throws PDFCreationException
  {
    final String s = "This is a test String\nwith 2 lines";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLVBox aVBox = new PLVBox ().setVertSplittable (true);
    for (int i = 0; i < 40; ++i)
    {
      final PLHBox aHBox = new PLHBox ().setVertSplittable (true);
      for (int j = 0; j < 10; ++j)
        aHBox.addColumn (new PLText (s + (j == 0 ? s : ""), r10).setBorder (new Color (j, j * 10, j * 20))
                                                                .setVertSplittable (false),
                         WidthSpec.star ());
      aVBox.addRow (aHBox);
    }
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-splittable-content-fixed.pdf"));
  }
}
