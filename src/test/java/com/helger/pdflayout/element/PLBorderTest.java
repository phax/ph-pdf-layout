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
import com.helger.pdflayout.PLDebug;
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
  public TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testDifferentBorders () throws PDFCreationException
  {
    PLDebug.setDebugAll (true);
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setID ("pageset")
                                                         .setMargin (0)
                                                         .setMarginTop (30)
                                                         .setBorder (new BorderSpec (new BorderStyleSpec (Color.GREEN,
                                                                                                          10)));
    aPS1.addElement (new PLText ("Border top", r10).setID ("top").setBorderTop (new BorderStyleSpec (Color.RED, 10)));
    aPS1.addElement (new PLText ("Border right", r10).setID ("right")
                                                     .setBorderRight (new BorderStyleSpec (Color.BLUE, 10)));
    aPS1.addElement (new PLText ("Border bottom", r10).setID ("bottom")
                                                      .setBorderBottom (new BorderStyleSpec (Color.MAGENTA, 10)));
    aPS1.addElement (new PLText ("Border left", r10).setID ("left")
                                                    .setBorderLeft (new BorderStyleSpec (Color.CYAN, 10)));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top left", r10).setID ("top left")
                                                        .setBorderTop (new BorderStyleSpec (Color.RED, 10))
                                                        .setBorderLeft (new BorderStyleSpec (Color.RED, 10)));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right", r10).setID ("top right")
                                                         .setBorderTop (new BorderStyleSpec (Color.BLUE, 10))
                                                         .setBorderRight (new BorderStyleSpec (Color.BLUE, 10)));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom right", r10).setID ("bottom right")
                                                            .setBorderBottom (new BorderStyleSpec (Color.MAGENTA, 10))
                                                            .setBorderRight (new BorderStyleSpec (Color.MAGENTA, 10)));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom left", r10).setID ("bottom left")
                                                           .setBorderBottom (new BorderStyleSpec (Color.GREEN, 10))
                                                           .setBorderLeft (new BorderStyleSpec (Color.GREEN, 10)));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border all", r10).setID ("all").setBorder (new BorderStyleSpec (Color.CYAN, 10)));

    if (false)
      aPS1.addElement (new PLHBox ().setID ("hbox")
                                    .addColumn (new PLText ("First line", r10)
                                                                              .setBorder (new BorderStyleSpec (Color.RED)),
                                                WidthSpec.star ()));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-border.pdf"));
  }
}
