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
package com.helger.pdflayout.element;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.EPLRotate;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for rotated elements
 *
 * @author Philip Helger
 */
public final class PLRotateTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testRotate () throws PDFCreationException
  {
    final FontSpec aFS10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec aFS20 = new FontSpec (PreloadFont.REGULAR, 20);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    // 1. Text rotated
    aPS1.addElement (new PLText ("Ident 0 deg", aFS20).setBorder (PLColor.BLUE));
    aPS1.addElement (new PLText ("Ident 90 deg", aFS20).setRotate (EPLRotate.ROTATE_90).setBorder (PLColor.BLUE));
    aPS1.addElement (new PLText ("Ident 180 deg", aFS20).setRotate (EPLRotate.ROTATE_180).setBorder (PLColor.BLUE));
    aPS1.addElement (new PLText ("Ident 270 deg", aFS20).setRotate (EPLRotate.ROTATE_270).setBorder (PLColor.BLUE));

    // 2. Box rotated 90 containing Text 0
    aPS1.addElement (new PLBox ().setBorder (PLColor.RED)
                                 .setVertSplittable (false)
                                 .setRotate (EPLRotate.ROTATE_90)
                                 .setElement (new PLText ("Box 90 / Text 0", aFS10).setBorder (PLColor.GREEN)));

    // 3. Box rotated 0 containing Text 90
    aPS1.addElement (new PLBox ().setBorder (PLColor.RED)
                                 .setVertSplittable (false)
                                 .setElement (new PLText ("Box 0 / Text 90", aFS10).setRotate (EPLRotate.ROTATE_90)
                                                                                   .setBorder (PLColor.GREEN)));

    // 4. Box rotated 90 containing Text 90 -> 180
    aPS1.addElement (new PLBox ().setBorder (PLColor.RED)
                                 .setVertSplittable (false)
                                 .setRotate (EPLRotate.ROTATE_90)
                                 .setElement (new PLText ("Box 90 / Text 90", aFS10).setRotate (EPLRotate.ROTATE_90)
                                                                                    .setBorder (PLColor.GREEN)));

    // 5. Fixed size Box rotated 90
    aPS1.addElement (new PLBox ().setBorder (PLColor.MAGENTA)
                                 .setVertSplittable (false)
                                 .setMinWidth (100)
                                 .setMinHeight (50)
                                 .setRotate (EPLRotate.ROTATE_90)
                                 .setElement (new PLText ("Box 90 (100x50) / Text 0", aFS10).setBorder (
                                                                                                        PLColor.GREEN)));

    final PageLayoutPDF aPL = new PageLayoutPDF ().setCompressPDF (false);
    aPL.addPageSet (aPS1);
    aPL.renderTo (new File ("pdf/plrotate.pdf"));
  }
}
