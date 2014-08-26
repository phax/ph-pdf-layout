/**
 * Copyright (C) 2014 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
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
package com.helger.pdflayout.element;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.CGlobal;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.mock.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.PLHBox;
import com.helger.pdflayout.element.PLPageSet;
import com.helger.pdflayout.element.PLTable;
import com.helger.pdflayout.element.PLText;
import com.helger.pdflayout.element.PLTextSplittable;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PDFFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Test class for {@link PLText} and {@link PLTextSplittable}
 * 
 * @author Philip Helger
 */
public final class PLTextTest
{
  @Rule
  public TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testWithWordBreak () throws PDFCreationException
  {
    final String s = "{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}{\\f1 Verdana;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1\\f1\\fs20 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql{\\f1\\fs20\\cf0 vielen Dank f\\u252\\'fcr Ihre Bestellung.}\\f1\\fs20\\cf0\\par}{\\rtf1\\deff0{\\fonttbl{\\f0 Times New Roman;}}{\\colortbl\\red0\\green0\\blue0 ;\\red0\\green0\\blue255 ;}{\\*\\listoverridetable}{\\stylesheet {\\ql Normal;}{\\*\\cs1 Default Paragraph Font;}{\\*\\cs2\\sbasedon1 Line Number;}{\\*\\cs3\\ul\\cf1 Hyperlink;}{\\*\\ts4\\tsrowd\\ql\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Normal Table;}{\\*\\ts5\\tsrowd\\sbasedon4\\ql\\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10\\trautofit1\\tscellpaddfl3\\tscellpaddl108\\tscellpaddfr3\\tscellpaddr108\\tsvertalt\\cltxlrtb Table Simple 1;}}\\nouicompat\\splytwnine\\htmautsp\\sectd\\pard\\plain\\ql\\par}";

    final FontSpec r10 = new FontSpec (PDFFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDPage.PAGE_SIZE_A4).setMargin (30);
    final PLTable aTable = PLTable.createWithPercentage (50, 50);
    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText (s, r10).setBorder (new BorderStyleSpec (Color.RED)), WidthSpec.star ());
    aTable.addRow (aHBox);
    aPS1.addElement (aTable);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileUtils.getOutputStream ("pdf/test-pltext.pdf"));
  }

  @Test
  public void testWithTextExceedingPage () throws PDFCreationException
  {
    final StringBuilder aSB = new StringBuilder (5 * CGlobal.BYTES_PER_MEGABYTE);
    for (int i = 0; i < 500; ++i)
    {
      if (aSB.length () > 0)
        aSB.append (" - ");
      if (false)
        if ((i % 100) == 0)
          aSB.append ('\n');
      aSB.append ("This is a dummy");
    }

    final FontSpec r10 = new FontSpec (PDFFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDPage.PAGE_SIZE_A4).setMargin (30);
    aPS1.addElement (new PLTextSplittable (aSB.toString (), r10).setBorder (new BorderStyleSpec (Color.RED)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileUtils.getOutputStream ("pdf/test-pltext-split.pdf"));
  }
}
