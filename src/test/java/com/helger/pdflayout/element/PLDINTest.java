/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileUtils;
import com.helger.commons.mock.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.PLHBox;
import com.helger.pdflayout.element.PLPageSet;
import com.helger.pdflayout.element.PLSpacerX;
import com.helger.pdflayout.element.PLSpacerY;
import com.helger.pdflayout.element.PLText;
import com.helger.pdflayout.element.PLVBox;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PDFFont;
import com.helger.pdflayout.spec.WidthSpec;

public final class PLDINTest
{
  @Rule
  public TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testDINLetter () throws PDFCreationException
  {
    final float fMMToUnits = 1 / (10 * 2.54f) * 72;
    final FontSpec r10 = new FontSpec (PDFFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDPage.PAGE_SIZE_A4);
    {
      final PLVBox aVBox = new PLVBox ();
      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (new PLSpacerX (), WidthSpec.abs (20 * fMMToUnits));
      {
        final PLVBox aWindow = new PLVBox ();
        aWindow.addRow (new PLSpacerY (42 * fMMToUnits));
        aWindow.addRow (new PLText ("Hr. MaxMustermann\nMusterstraße 15\nA-1010 Wien", r10).setMinSize (90 * fMMToUnits,
                                                                                                        45 * fMMToUnits)
                                                                                           .setMaxSize (90 * fMMToUnits,
                                                                                                        45 * fMMToUnits));
        aWindow.addRow (new PLSpacerY (12 * fMMToUnits));
        aHBox.addColumn (aWindow, WidthSpec.abs (90 * fMMToUnits));
      }
      aHBox.addColumn (new PLSpacerX (), WidthSpec.star ());
      aVBox.addRow (aHBox);
      aPS1.addElement (aVBox);
    }
    aPS1.addElement (new PLSpacerY (99 * fMMToUnits));
    aPS1.addElement (new PLSpacerY (98.5f * fMMToUnits));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (true);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileUtils.getOutputStream ("pdf/test-din-letter.pdf"));
  }
}
