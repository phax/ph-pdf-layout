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
package com.helger.pdflayout.element.text;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.string.StringHelper;
import com.helger.font.alegreya_sans.EFontResourceAlegreyaSans;
import com.helger.font.anaheim.EFontResourceAnaheim;
import com.helger.font.api.IHasFontResource;
import com.helger.font.exo2.EFontResourceExo2;
import com.helger.font.kurinto.mono.EFontResourceKurintoMono;
import com.helger.font.kurinto.sans.EFontResourceKurintoSans;
import com.helger.font.lato2.EFontResourceLato2;
import com.helger.font.markazi.EFontResourceMarkazi;
import com.helger.font.noto_sans_hk.EFontResourceNotoSansHK;
import com.helger.font.noto_sans_sc.EFontResourceNotoSansSC;
import com.helger.font.noto_sans_tc.EFontResourceNotoSansTC;
import com.helger.font.open_sans.EFontResourceOpenSans;
import com.helger.font.roboto.EFontResourceRoboto;
import com.helger.font.source_sans_pro.EFontResourceSourceSansPro;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PDFTestComparer;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.spec.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.File;
import java.util.Map;

/**
 * Test class for {@link PLText}
 *
 * @author Philip Helger
 */
public final class PLRTextTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  @Ignore
  public void testBasic () throws PDFCreationException
  {
    final String s = "{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}{\\f1 Verdana;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1\\f1\\fs20 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql{\\f1\\fs20\\cf0 vielen Dank f\\u252\\'fcr Ihre Bestellung.}\\f1\\fs20\\cf0\\par}{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql\\par}";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLRText (s, r10).setBorder (PLColor.RED));

    // All chars from 32-127
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 32; i <= 0x7e; ++i)
      aSB.append ((char) i);
    aPS1.addElement (new PLRText ("Chars 32-127: " + aSB.toString (), r10).setBorder (PLColor.GREEN));

    // Check horizontal alignment
    aPS1.addElement (new PLRText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (PLColor.RED));
    aPS1.addElement (new PLRText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                  .setBorder (PLColor.RED)
                                                                  .setFillColor (PLColor.PINK));
    aPS1.addElement (new PLRText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.RED));
    aPS1.addElement (new PLRText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                      .setBorder (PLColor.RED)
                                                                      .setFillColor (PLColor.PINK));
    aPS1.addElement (new PLRText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (PLColor.RED));
    aPS1.addElement (new PLRText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (PLColor.RED)
                                                                    .setFillColor (PLColor.PINK));
    aPS1.addElement (new PLRText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                      .setBorder (PLColor.RED)
                                                                      .setPadding (5)
                                                                      .setFillColor (PLColor.PINK));
    aPS1.addElement (new PLRText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (PLColor.RED)
                                                                    .setMargin (5)
                                                                    .setFillColor (PLColor.PINK));
    aPS1.addElement (new PLRText ("Text with margin and padding", r10).setBorder (PLColor.RED)
                                                                     .setMargin (5)
                                                                     .setPadding (5)
                                                                     .setFillColor (PLColor.PINK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/basic.pdf"));
  }

  @Test
  @Ignore
  public void testBasicWithLineSpacing () throws PDFCreationException
  {
    final String s = "{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}{\\f1 Verdana;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1\\f1\\fs20 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql{\\f1\\fs20\\cf0 vielen Dank f\\u252\\'fcr Ihre Bestellung.}\\f1\\fs20\\cf0\\par}{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql\\par}";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    final float fLineSpacing = 1.5f;

    aPS1.addElement (new PLRText ("All texts are using a line spacing of " + fLineSpacing, r10).setLineSpacing (
                                                                                                               fLineSpacing));
    aPS1.addElement (new PLRText (s, r10).setBorder (PLColor.RED).setLineSpacing (fLineSpacing));

    // All chars from 32-127
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 32; i <= 0x7e; ++i)
      aSB.append ((char) i);
    aPS1.addElement (new PLRText ("Chars 32-127: " + aSB.toString (), r10).setBorder (PLColor.GREEN)
                                                                         .setLineSpacing (fLineSpacing));

    // Check horizontal alignment
    aPS1.addElement (new PLRText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT)
                                             .setBorder (PLColor.RED)
                                             .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Left and\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT)
                                                                  .setBorder (PLColor.RED)
                                                                  .setFillColor (PLColor.PINK)
                                                                  .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER)
                                               .setBorder (PLColor.RED)
                                               .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Center and\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                      .setBorder (PLColor.RED)
                                                                      .setFillColor (PLColor.PINK)
                                                                      .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (PLColor.RED));
    aPS1.addElement (new PLRText ("Right and\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (PLColor.RED)
                                                                    .setFillColor (PLColor.PINK)
                                                                    .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Right and\nRight with padding", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                      .setBorder (PLColor.RED)
                                                                      .setPadding (5)
                                                                      .setFillColor (PLColor.PINK)
                                                                      .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Right and\nRight but margin", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                                    .setBorder (PLColor.RED)
                                                                    .setMargin (5)
                                                                    .setFillColor (PLColor.PINK)
                                                                    .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLRText ("Text with margin and padding", r10).setBorder (PLColor.RED)
                                                                     .setMargin (5)
                                                                     .setPadding (5)
                                                                     .setFillColor (PLColor.PINK)
                                                                     .setLineSpacing (fLineSpacing));
    aPS1.addElement (new PLPageBreak (false));
    String sVeryLongText = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                           "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                           "Tataa: €";
    sVeryLongText = StringHelper.getRepeated (sVeryLongText, 48);
    aPS1.addElement (new PLRText (sVeryLongText, r10).setLineSpacing (fLineSpacing));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/line-spacing.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontOpenSans () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ());

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";
    final FontSpec r10 = new FontSpec (aFont, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-open-sans.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontLato2 () throws PDFCreationException
  {
    // Load OTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceLato2.LATO2_NORMAL.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceLato2.LATO2_BLACK.getFontResource ());

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-lato2.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontKurintoSans () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceKurintoSans.KURINTO_SANS_REGULAR.getFontResource ());
    aFont.setUseFontLineHeightFromHHEA ();
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceKurintoSans.KURINTO_SANS_BOLD.getFontResource ());
    aFont2.setUseFontLineHeightFromHHEA ();

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.setCompressPDF (false);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-kurinto-sans.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontKurintoMono () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceKurintoMono.KURINTO_MONO_REGULAR.getFontResource ());
    aFont.setUseFontLineHeightFromHHEA ();
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceKurintoMono.KURINTO_MONO_BOLD.getFontResource ());
    aFont2.setUseFontLineHeightFromHHEA ();

    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: €";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.setCompressPDF (false);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-kurinto-mono.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontMarkazi () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceMarkazi.MARKAZI_NORMAL.getFontResource ());
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceMarkazi.MARKAZI_BOLD.getFontResource ());

    final String s = "Ascii line 1\n" + "تراکنش ریالی\n" + "معاملة بالدولار\n" + "Ascii EOL";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-markazi.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontNotoSansSC () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceNotoSansSC.NOTO_SANS_SC_REGULAR.getFontResource ());
    aFont.setUseFontLineHeightFromHHEA ();
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceNotoSansSC.NOTO_SANS_SC_BOLD.getFontResource ());
    aFont2.setUseFontLineHeightFromHHEA ();

    final String s = "Ascii line 1\n" + "Ascii line 2\n" + "他们所有的设备和仪器彷佛都是有生命的。\n" + "Ascii before EOL\n" + "Ascii EOL";
    final String sAsciiOnly = "Ascii line 3\n" + "Ascii line 4\n" + "Ascii 5";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (sAsciiOnly, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));
    aPS1.addElement (new PLRText (sAsciiOnly, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-noto-sans-sc.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontNotoSansTC () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceNotoSansTC.NOTO_SANS_TC_REGULAR.getFontResource ());
    aFont.setUseFontLineHeightFromHHEA ();
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceNotoSansTC.NOTO_SANS_TC_BOLD.getFontResource ());
    aFont2.setUseFontLineHeightFromHHEA ();

    final String s = "Ascii line 1\n" + "Ascii line 2\n" + "他们所有的设备和仪器彷佛都是有生命的。\n" + "Ascii before EOL\n" + "Ascii EOL";
    final String sAsciiOnly = "Ascii line 3\n" + "Ascii line 4\n" + "Ascii 5";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (sAsciiOnly, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));
    aPS1.addElement (new PLRText (sAsciiOnly, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-noto-sans-tc.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontNotoSansHK () throws PDFCreationException
  {
    // Load TTF font
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceNotoSansHK.NOTO_SANS_HK_REGULAR.getFontResource ());
    aFont.setUseFontLineHeightFromHHEA ();
    final PreloadFont aFont2 = PreloadFont.createEmbedding (EFontResourceNotoSansHK.NOTO_SANS_HK_BOLD.getFontResource ());
    aFont2.setUseFontLineHeightFromHHEA ();

    final String s = "Ascii line 1\n" + "Ascii line 2\n" + "他们所有的设备和仪器彷佛都是有生命的。\n" + "Ascii before EOL\n" + "Ascii EOL";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    aPS1.addElement (new PLRText (s, new FontSpec (aFont, 10)));
    aPS1.addElement (new PLRText (s, new FontSpec (aFont2, 10)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-noto-sans-hk.pdf"));
  }

  @Test
  @Ignore
  public void testCustomFontMultiple () throws PDFCreationException
  {
    final String s = "Xaver schreibt für Wikipedia zum Spaß quälend lang über Yoga, Soja und Öko.\n" +
                     "Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.\n" +
                     "Tataa: € - and some specials: áàéèíìóòúù ÁÀÉÈÍÌÓÒÚÙ\n" +
                     "Arabic: <تراکنش ریالی>";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (40);

    for (final Map.Entry <String, PreloadFont> aEntry : PreloadFont.getAllStandard14PreloadFonts ().entrySet ())
    {
      aPS1.addElement (new PLRText ("[Standard] [" + aEntry.getKey () + "]: " + s + "\n",
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
                                                                    EFontResourceMarkazi.MARKAZI_BOLD,
                                                                    EFontResourceNotoSansHK.NOTO_SANS_HK_REGULAR,
                                                                    EFontResourceNotoSansHK.NOTO_SANS_HK_BLACK,
                                                                    EFontResourceNotoSansSC.NOTO_SANS_SC_REGULAR,
                                                                    EFontResourceNotoSansSC.NOTO_SANS_SC_BLACK,
                                                                    EFontResourceNotoSansTC.NOTO_SANS_TC_REGULAR,
                                                                    EFontResourceNotoSansTC.NOTO_SANS_TC_BLACK,
                                                                    EFontResourceKurintoMono.KURINTO_MONO_REGULAR,
                                                                    EFontResourceKurintoMono.KURINTO_MONO_BOLD,
                                                                    EFontResourceKurintoSans.KURINTO_SANS_REGULAR,
                                                                    EFontResourceKurintoSans.KURINTO_SANS_BOLD))
    {
      // Load TTF font
      final PreloadFont aFont = PreloadFont.createEmbedding (aHasFont.getFontResource ());
      if (aHasFont.getFontResource ().getFontName ().startsWith ("Kurinto ") ||
          aHasFont.getFontResource ().getFontName ().startsWith ("Noto "))
        aFont.setUseFontLineHeightFromHHEA ();

      aPS1.addElement (new PLRText ("[External] [" + aHasFont.getFontResourceID () + "]: " + s + "\n",
                                   new FontSpec (aFont, 10)));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/font-multiple.pdf"));
  }

  @Test
  @Ignore
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
        aPS1.addElement (new PLRText ("Alignment: " + eHorzAlign + "\n" + s, r10).setHorzAlign (eHorzAlign));
        aPS1.addElement (new PLRText ("Second text with a single line only.", r10).setHorzAlign (eHorzAlign)
                                                                                 .setMarginBottom (10));
      }

    {
      final PLRText aText = new PLRText ("For issue #7 (V1)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                            PLColor.RED))
                                                                           .setHorzAlign (EHorzAlignment.RIGHT);
      aPS1.addElement (aText);
    }
    {
      final PLBox aBox = new PLBox (new PLRText ("For issue #7 (V2)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                                     PLColor.RED))).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                                   .setBorder (new BorderStyleSpec (PLColor.BLUE));
      aPS1.addElement (aBox);
    }
    {
      final PLRText aText = new PLRText ("For issue #7 (V3)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                            PLColor.RED))
                                                                           .setHorzAlign (EHorzAlignment.RIGHT);

      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (aText, WidthSpec.perc (50));
      aPS1.addElement (aHBox);
    }
    {
      final PLBox aBox = new PLBox (new PLRText ("For issue #7 (V4)\nMultiline", r10).setBorder (new BorderStyleSpec (
                                                                                                                     PLColor.RED))).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                                                                   .setBorder (new BorderStyleSpec (PLColor.BLUE));

      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (aBox, WidthSpec.perc (50));
      aPS1.addElement (aHBox);
    }

    {
      aPS1.addElement (new PLSpacerY (20));
      final String s2 = "!" +
                        StringHelper.getRepeated ("Die heiße Zypernsonne quälte Max und Victoria ja böse auf dem Weg bis zur Küste.",
                                                  50);
      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (new PLRText (s2, r10).setHorzAlign (EHorzAlignment.BLOCK), WidthSpec.perc (50));
      aPS1.addElement (aHBox);
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/horz-alignment.pdf"));
  }

  @Test
  @Ignore
  public void testWithTextExceedingPage () throws PDFCreationException
  {
    final StringBuilder aSB = new StringBuilder (5 * CGlobal.BYTES_PER_MEGABYTE);
    for (int i = 0; i < 4000; ++i)
    {
      if (aSB.length () > 0)
        aSB.append (" - ");
      if (false)
        if ((i % 100) == 0)
          aSB.append ('\n');
      if ((i % 100) == 0)
        aSB.append ("<surprise>");
      aSB.append ("This is a dummy");
    }

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLRText (aSB.toString (), r10).setBorder (PLColor.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/split-vertically.pdf"));
  }

  @Test
  @Ignore
  public void testCenterIssue31 () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final String s1 = "Hello centered world";
    final String s2 = "Hello centered world\nLine 2";

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLRText ("The following entries have plText.setHorzAlign(EHorzAlignment.CENTER)", r10)
                                                                                                              .setMarginY (10));

    for (final String sText : new String [] { s1, s2 })
    {
      // Text on Page set
      aPS1.addElement (new PLRText (sText, r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.RED));

      // Text in PLVBox
      {
        final PLVBox aPLVBox = new PLVBox ();
        aPLVBox.addRow (new PLRText (sText, r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.BLUE));
        aPS1.addElement (aPLVBox);
      }

      // Text in PLBox
      {
        final PLBox aPLBox = new PLBox ();
        aPLBox.setElement (new PLRText (sText, r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (PLColor.GREEN));
        aPLBox.setHorzAlign (EHorzAlignment.CENTER);
        aPS1.addElement (aPLBox);
      }
    }

    aPS1.addElement (new PLRText ("The following entries DO NOT use plText.setHorzAlign(EHorzAlignment.CENTER)", r10)
                                                                                                                    .setMarginY (10));

    for (final String sText : new String [] { s1, s2 })
    {
      // Text on Page set
      aPS1.addElement (new PLRText (sText, r10).setBorder (PLColor.RED));

      // Text in PLVBox
      {
        final PLVBox aPLVBox = new PLVBox ();
        aPLVBox.addRow (new PLRText (sText, r10).setBorder (PLColor.BLUE));
        aPS1.addElement (aPLVBox);
      }

      // Text in PLBox
      {
        final PLBox aPLBox = new PLBox ();
        aPLBox.setElement (new PLRText (sText, r10).setBorder (PLColor.GREEN));
        aPLBox.setHorzAlign (EHorzAlignment.CENTER);
        aPS1.addElement (aPLBox);
      }
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/center-issue31.pdf"));
  }

  @Test
  @Ignore
  public void testCreateSimpleFooter () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10, PLColor.RED);
    final FontSpec r12 = new FontSpec (PreloadFont.REGULAR, 12);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.setPadding (15);
    aPS1.setMarginBottom (40);

    aPS1.addElement (new PLRText ("This is the main text - bla bla", r12));

    final StringBuilder aSB = new StringBuilder ();
    for (final String s : new String [] { "Name and whatever else you need",
                                          "Streetname",
                                          "Building number or complex, whatever is needed",
                                          "Postal code or postbox",
                                          "City or village",
                                          "Country code",
                                          "Country name",
                                          "Anything",
                                          "Just for testing purposes" })
    {
      if (aSB.length () > 0)
        aSB.append (" • ");
      aSB.append (s);
    }
    aPS1.setPageFooter (new PLBox (new PLRText (aSB.toString (), r10)).setPadding (5));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/plrtext/simple-footer.pdf"));
  }
}
