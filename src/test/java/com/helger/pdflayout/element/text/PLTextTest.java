/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.text;

import java.awt.Color;
import java.io.File;
import java.util.Map;
import java.util.Random;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.string.StringHelper;
import com.helger.font.alegreya_sans.EFontResourceAlegreyaSans;
import com.helger.font.anaheim.EFontResourceAnaheim;
import com.helger.font.api.IHasFontResource;
import com.helger.font.exo2.EFontResourceExo2;
import com.helger.font.lato2.EFontResourceLato2;
import com.helger.font.markazi.EFontResourceMarkazi;
import com.helger.font.noto_sans_hk.EFontResourceNotoSansHK;
import com.helger.font.noto_sans_sc.EFontResourceNotoSansSC;
import com.helger.font.open_sans.EFontResourceOpenSans;
import com.helger.font.roboto.EFontResourceRoboto;
import com.helger.font.source_sans_pro.EFontResourceSourceSansPro;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.EPLSimpleRotation;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Test class for {@link PLText}
 *
 * @author Philip Helger
 */
public final class PLTextTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final String s = "{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}{\\f1 Verdana;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1\\f1\\fs20 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql{\\f1\\fs20\\cf0 vielen Dank f\\u252\\'fcr Ihre Bestellung.}\\f1\\fs20\\cf0\\par}{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql\\par}";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText (s, r10).setBorder (Color.RED));

    // All chars from 32-127
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 32; i <= 0x7e; ++i)
      aSB.append ((char) i);
    aPS1.addElement (new PLText ("Chars 32-127: " + aSB.toString (), r10).setBorder (Color.GREEN));

    // Check horizontal alignment
    aPS1.addElement (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (Color.RED));
    aPS1.addElement (new PLText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                  .setBorder (Color.RED)
                                                                  .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (Color.RED));
    aPS1.addElement (new PLText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                      .setBorder (Color.RED)
                                                                      .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (Color.RED));
    aPS1.addElement (new PLText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (Color.RED)
                                                                    .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                      .setBorder (Color.RED)
                                                                      .setPadding (5)
                                                                      .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (Color.RED)
                                                                    .setMargin (5)
                                                                    .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Text with margin and padding", r10).setBorder (Color.RED)
                                                                     .setMargin (5)
                                                                     .setPadding (5)
                                                                     .setFillColor (Color.PINK));
    aPS1.addElement (new PLText ("Text with different borders,\nmargins and paddings", r10).setHorzAlign (
                                                                                                          EHorzAlignment.CENTER)
                                                                                           .setBorder (new BorderStyleSpec (Color.RED,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.GREEN,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.BLUE,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.MAGENTA,
                                                                                                                            5))
                                                                                           .setMargin (5)
                                                                                           .setPadding (5)
                                                                                           .setFillColor (Color.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/basic.pdf"));
  }

  @Test
  public void testBasicWithLineSpacing () throws PDFCreationException
  {
    final String s = "{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}{\\f1 Verdana;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1\\f1\\fs20 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql{\\f1\\fs20\\cf0 vielen Dank f\\u252\\'fcr Ihre Bestellung.}\\f1\\fs20\\cf0\\par}{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql\\par}";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final float fLineSpacing = 1.5f;

    aPS1.addElement (new PLText ("All texts are using a line spacing of " + fLineSpacing, r10).setLineSpacing (
                                                                                                               fLineSpacing));
    aPS1.addElement (new PLText (s, r10).setBorder (Color.RED).setLineSpacing (fLineSpacing));

    // All chars from 32-127
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 32; i <= 0x7e; ++i)
      aSB.append ((char) i);
    aPS1.addElement (new PLText ("Chars 32-127: " + aSB.toString (), r10).setBorder (Color.GREEN)
                                                                         .setLineSpacing (fLineSpacing));

    // Check horizontal alignment
    aPS1.addElement (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT)
                                             .setBorder (Color.RED)
                                             .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                  .setBorder (Color.RED)
                                                                  .setFillColor (Color.PINK)
                                                                  .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER)
                                               .setBorder (Color.RED)
                                               .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                      .setBorder (Color.RED)
                                                                      .setFillColor (Color.PINK)
                                                                      .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (Color.RED));
    aPS1.addElement (new PLText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (Color.RED)
                                                                    .setFillColor (Color.PINK)
                                                                    .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                      .setBorder (Color.RED)
                                                                      .setPadding (5)
                                                                      .setFillColor (Color.PINK)
                                                                      .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (Color.RED)
                                                                    .setMargin (5)
                                                                    .setFillColor (Color.PINK)
                                                                    .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Text with margin and padding", r10).setBorder (Color.RED)
                                                                     .setMargin (5)
                                                                     .setPadding (5)
                                                                     .setFillColor (Color.PINK)
                                                                     .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText ("Text with different borders,\nmargins and paddings", r10).setHorzAlign (
                                                                                                          EHorzAlignment.CENTER)
                                                                                           .setBorder (new BorderStyleSpec (Color.RED,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.GREEN,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.BLUE,
                                                                                                                            5),
                                                                                                       new BorderStyleSpec (Color.MAGENTA,
                                                                                                                            5))
                                                                                           .setMargin (5)
                                                                                           .setPadding (5)
                                                                                           .setFillColor (Color.YELLOW)
                                                                                           .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLPageBreak (false));
    String sVeryLongText = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                           "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                           "Tataa: €";
    sVeryLongText = StringHelper.getRepeated (sVeryLongText, 48);
    aPS1.addElement (new PLText (sVeryLongText, r10).setLineSpacing (fLineSpacing));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/line-spacing.pdf"));
  }

  @Test
  public void testCustomFontOpenSans () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ());

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";
    final FontSpec r10 = new FontSpec (aFont, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLText (s, r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-open-sans.pdf"));
  }

  @Test
  public void testCustomFontLato2 () throws PDFCreationException
  {
    // Load OTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceLato2.LATO2_NORMAL.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceLato2.LATO2_BLACK.getFontResource ());

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-lato2.pdf"));
  }

  @Test
  public void testArabicCharacters () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceMarkazi.MARKAZI_NORMAL.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceMarkazi.MARKAZI_BOLD.getFontResource ());

    final String s = "Ascii line 1\n" + "تراکنش ریالی\n" + "معاملة بالدولار\n" + "Ascii EOL";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-markazi.pdf"));
  }

  @Test
  public void testSimplifiedChineseCharacters () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceNotoSansSC.NOTO_SANS_SC_REGULAR.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceNotoSansSC.NOTO_SANS_SC_BOLD.getFontResource ());

    final String s = "Ascii line 1\n" + "Ascii line 2\n" + "他们所有的设备和仪器彷佛都是有生命的。\n" + "Ascii before EOL\n" + "Ascii EOL";
    final String sAsciiOnly = "Ascii line 3\n" + "Ascii line 4\n" + "Ascii 5";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    final float fLineSpacing = 0.5f;
    aPS1.addElement (new PLText (s, new FontSpec (aFont, 10)).setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText (sAsciiOnly, new FontSpec (aFont, 10)).setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText (s, new FontSpec (aFont2, 10)).setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLText (sAsciiOnly, new FontSpec (aFont2, 10)).setLineSpacing (fLineSpacing));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-noto-sans-sc.pdf"));
  }

  @Test
  public void testChineseCharacters () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceNotoSansHK.NOTO_SANS_HK_REGULAR.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceNotoSansHK.NOTO_SANS_HK_BOLD.getFontResource ());

    final String s = "Ascii line 1\n" + "Ascii line 2\n" + "他们所有的设备和仪器彷佛都是有生命的。\n" + "Ascii before EOL\n" + "Ascii EOL";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-noto-sans-hk.pdf"));
  }

  @Test
  public void testCustomFontMultiple () throws PDFCreationException
  {
    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: € - and some specials: áàéèíìóòúù ÁÀÉÈÍÌÓÒÚÙ\n" +
                     "Arabic: <تراکنش ریالی>";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    for (final Map.Entry <String, PreloadFont> aEntry : PreloadFont.getAllStandard14PreloadFonts ().entrySet ())
    {
      aPS1.addElement (new PLText ("[Standard] [" + aEntry.getKey () + "]: " + s + "\n",
                                   new FontSpec (aEntry.getValue (), 10)));
    }
    for (final IHasFontResource aHasFont : new CommonsArrayList <> (EFontResourceAlegreyaSans.ALGREYA_SANS_NORMAL,
                                                                    EFontResourceAlegreyaSans.ALGREYA_SANS_BLACK,
                                                                    EFontResourceAnaheim.ANAHEIM_REGULAR,
                                                                    EFontResourceExo2.EXO2_NORMAL,
                                                                    EFontResourceExo2.EXO2_BLACK,
                                                                    EFontResourceLato2.LATO2_NORMAL,
                                                                    EFontResourceLato2.LATO2_BLACK,
                                                                    EFontResourceOpenSans.OPEN_SANS_NORMAL,
                                                                    EFontResourceOpenSans.OPEN_SANS_BOLD,
                                                                    EFontResourceRoboto.ROBOTO_NORMAL,
                                                                    EFontResourceRoboto.ROBOTO_BOLD,
                                                                    EFontResourceSourceSansPro.SOURCE_SANS_PRO_NORMAL,
                                                                    EFontResourceSourceSansPro.SOURCE_SANS_PRO_BOLD,
                                                                    EFontResourceMarkazi.MARKAZI_NORMAL,
                                                                    EFontResourceMarkazi.MARKAZI_BOLD))
    {
      // Load TTF font
      final PreloadFont aFont = PreloadFont.createEmbedding (aHasFont.getFontResource ());

      aPS1.addElement (new PLText ("[External] [" + aHasFont.getFontResourceID () + "]: " + s + "\n",
                                   new FontSpec (aFont, 10)));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/font-multiple.pdf"));
  }

  @Test
  public void testTextHorzAlignment () throws PDFCreationException
  {
    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     StringHelper.getRepeated ("Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.",
                                               5) +
                     "\n" +
                     "Tataa: € - and some specials: áàéèíìóòúù ÁÀÉÈÍÌÓÒÚÙ\n" +
                     "Very short line";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    // Repeat to ensure values are reset correctly
    for (int i = 0; i < 2; ++i)
      for (final EHorzAlignment eHorzAlign : EHorzAlignment.values ())
      {
        aPS1.addElement (new PLText ("Alignment: " + eHorzAlign + "\n" + s, r10).setHorzAlign (eHorzAlign));
        aPS1.addElement (new PLText ("Second text with a single line only.", r10).setHorzAlign (eHorzAlign)
                                                                                 .setMarginBottom (10));
      }

    {
      final PLText aText = new PLText ("For issue #7 (V1)\nMultiline", r10).setBorder (new BorderStyleSpec (Color.RED))
                                                                           .setHorzAlign (EHorzAlignment.RIGHT);
      aPS1.addElement (aText);
    }
    {
      final PLBox aBox = new PLBox (new PLText ("For issue #7 (V2)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                                     Color.RED))).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                                 .setBorder (new BorderStyleSpec (Color.BLUE));
      aPS1.addElement (aBox);
    }
    {
      final PLText aText = new PLText ("For issue #7 (V3)\nMultiline", r10).setBorder (new BorderStyleSpec (Color.RED))
                                                                           .setHorzAlign (EHorzAlignment.RIGHT);

      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (aText, WidthSpec.perc (50));
      aPS1.addElement (aHBox);
    }
    {
      final PLBox aBox = new PLBox (new PLText ("For issue #7 (V4)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                                     Color.RED))).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                                 .setBorder (new BorderStyleSpec (Color.BLUE));

      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (aBox, WidthSpec.perc (50));
      aPS1.addElement (aHBox);
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/horz-alignment.pdf"));
  }

  @Test
  public void testWithTextExceedingPage () throws PDFCreationException
  {
    final Random aRandom = new Random ();
    final StringBuilder aSB = new StringBuilder (5 * CGlobal.BYTES_PER_MEGABYTE);
    for (int i = 0; i < 4000; ++i)
    {
      if (aSB.length () > 0)
        aSB.append (" - ");
      if (false)
        if ((i % 100) == 0)
          aSB.append ('\n');
      if ((i % 100) == 0)
        aSB.append ((char) ('a' + aRandom.nextInt (26))).append ((char) ('a' + aRandom.nextInt (26)));
      aSB.append ("This is a dummy");
    }

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText (aSB.toString (), r10).setBorder (Color.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/split-vertically.pdf"));
  }

  @Test
  public void testWithTextRotate90 () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("Hello rotated world", r10).setBorder (Color.RED)
                                                            .setSimpleRotation (EPLSimpleRotation.ROTATE_180));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltext/rotate-90.pdf"));
  }
}
