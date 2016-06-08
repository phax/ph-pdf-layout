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
package com.helger.pdflayout.element;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * TEst class for {@link PLImage}
 *
 * @author Philip Helger
 */
public final class PLBorderTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testDifferentBorders () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 20f;
    final float fSpacer = 10f;
    final BorderStyleSpec aTop = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aRight = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aBottom = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aLeft = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aAll = new BorderStyleSpec (Color.GRAY, fValue);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setID ("pageset")
                                                         .setMargin (fValue)
                                                         .setFillColor (Color.GREEN)
                                                         .setBorder (new BorderSpec (aAll));
    aPS1.addElement (new PLText ("Border top", r10).setID ("top").setBorderTop (aTop));
    aPS1.addElement (new PLText ("Border right", r10).setID ("right").setBorderRight (aRight));
    aPS1.addElement (new PLText ("Border bottom", r10).setID ("bottom").setBorderBottom (aBottom));
    aPS1.addElement (new PLText ("Border left", r10).setID ("left").setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top left", r10).setID ("top left").setBorderTop (aTop).setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right", r10).setID ("top right")
                                                         .setBorderTop (aTop)
                                                         .setBorderRight (aRight));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom right", r10).setID ("bottom right")
                                                            .setBorderBottom (aBottom)
                                                            .setBorderRight (aRight));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom left", r10).setID ("bottom left")
                                                           .setBorderBottom (aBottom)
                                                           .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right left", r10).setID ("top right left")
                                                              .setBorderTop (aTop)
                                                              .setBorderRight (aRight)
                                                              .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border right bottom left", r10).setID ("right bottom left")
                                                                 .setBorderRight (aRight)
                                                                 .setBorderBottom (aBottom)
                                                                 .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right bottom", r10).setID ("top right bottom")
                                                                .setBorderTop (aTop)
                                                                .setBorderRight (aRight)
                                                                .setBorderBottom (aBottom));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top bottom left", r10).setID ("top bottom left")
                                                               .setBorderTop (aTop)
                                                               .setBorderBottom (aBottom)
                                                               .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right bottom left", r10).setID ("top right bottom left")
                                                                     .setBorderTop (aTop)
                                                                     .setBorderRight (aRight)
                                                                     .setBorderBottom (aBottom)
                                                                     .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border all", r10).setID ("all").setBorder (aAll));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-border.pdf"));
  }

  @Test
  public void testBorderWithHeaderAndFooter () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 10f;
    final BorderStyleSpec aRed = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aBlue = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aMagenta = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aCyan = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aGray = new BorderStyleSpec (Color.GRAY, fValue);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setID ("pageset")
                                                         .setMargin (100, fValue)
                                                         .setFillColor (Color.GREEN)
                                                         .setBorder (new BorderSpec (aGray));
    {
      final PLHBox aPageHeader = new PLHBox ().setID ("pageheader");
      aPageHeader.setMargin (fValue);
      aPageHeader.setBorder (aRed);
      aPageHeader.setColumnBorder (aBlue);
      aPageHeader.addColumn (new PLText ("Header1", r10).setBorder (aMagenta), WidthSpec.perc (50));
      aPageHeader.addColumn (new PLText ("Header2", r10).setBorder (aCyan), WidthSpec.perc (50));
      aPS1.setPageHeader (aPageHeader);
    }
    {
      final PLHBox aPageFooter = new PLHBox ().setID ("pagefooter");
      aPageFooter.setMargin (fValue);
      aPageFooter.setBorder (aRed);
      aPageFooter.setColumnBorder (aBlue);
      aPageFooter.addColumn (new PLText ("Footer1", r10).setBorder (aMagenta), WidthSpec.perc (50));
      aPageFooter.addColumn (new PLText ("Footer2", r10).setBorder (aCyan), WidthSpec.perc (50));
      aPS1.setPageFooter (aPageFooter);
    }
    aPS1.addElement (new PLText ("Only content", r10).setID ("all").setBorder (aRed));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-border-header-footer.pdf"));
  }
}
