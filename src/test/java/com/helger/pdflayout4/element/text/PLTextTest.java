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
package com.helger.pdflayout4.element.text;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.CGlobal;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.commons.random.RandomHelper;
import com.helger.font.lato2.EFontResourceLato2;
import com.helger.font.open_sans.EFontResourceOpenSans;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * Test class for {@link PLText}
 *
 * @author Philip Helger
 */
public final class PLTextTest
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
    aPS1.addElement (new PLText ("Text with different borders,\nmargins and paddings",
                                 r10).setHorzAlign (EHorzAlignment.CENTER)
                                     .setBorder (new BorderStyleSpec (Color.RED, 5),
                                                 new BorderStyleSpec (Color.GREEN, 5),
                                                 new BorderStyleSpec (Color.BLUE, 5),
                                                 new BorderStyleSpec (Color.MAGENTA, 5))
                                     .setMargin (5)
                                     .setPadding (5)
                                     .setFillColor (Color.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltext-basic.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltext-font-open-sans.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltext-font-lato2.pdf"));
  }

  @Test
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
        aSB.append ((char) ('a' + RandomHelper.getRandom ().nextInt (26)))
           .append ((char) ('a' + RandomHelper.getRandom ().nextInt (26)));
      aSB.append ("This is a dummy");
    }

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText (aSB.toString (), r10).setBorder (Color.RED));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltext-split.pdf"));
  }
}
