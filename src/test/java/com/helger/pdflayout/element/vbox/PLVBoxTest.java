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
package com.helger.pdflayout.element.vbox;

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
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

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
    aVBox.addRow (new PLText ("Left", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (Color.RED));
    aVBox.addRow (new PLText ("Left\nLeft but longer", r10).setHorzAlign (EHorzAlignment.LEFT).setBorder (Color.RED));
    aVBox.addRow (new PLText ("Center", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (Color.RED));
    aVBox.addRow (new PLText ("Center\nCenter but longer", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                               .setBorder (Color.RED));
    aVBox.addRow (new PLText ("Right", r10).setHorzAlign (EHorzAlignment.RIGHT).setBorder (Color.RED));
    aVBox.addRow (new PLText ("Right\nRight but longer", r10).setHorzAlign (EHorzAlignment.RIGHT)
                                                             .setBorder (Color.RED)
                                                             .setMarginTop (2)
                                                             .setPadding (5)
                                                             .setFillColor (Color.PINK));
    aPS1.addElement (aVBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plvbox-basic.pdf"));
  }
}
