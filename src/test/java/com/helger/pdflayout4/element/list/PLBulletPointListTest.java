/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.list;

import java.awt.Color;
import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.string.StringHelper;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PLDebugTestRule;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for {@link PLBulletPointList}
 *
 * @author Philip Helger
 */
public final class PLBulletPointListTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    // This is the "Bullet point" in "Symbol font" (char 183)
    {
      final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (15f), BulletPointCreatorSymbol.createFilledDot (10f));
      for (int i = 0; i < 10; ++i)
        aList.addBulletPoint (new PLText ("Bullet point item " + i, r10));
      aPS1.addElement (aList);
    }

    // Numbered items
    {
      final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (25f),
                                                             new BulletPointCreatorNumeric (x -> "[" + (x + 1) + ".)", r10));
      for (int i = 0; i < 10; ++i)
        aList.addBulletPoint (new PLText ("Bullet point item " + i, r10));
      aPS1.addElement (aList);
    }

    new PageLayoutPDF ().addPageSet (aPS1).setCompressPDF (false).renderTo (new File ("pdf/plbulletpointlist/basic.pdf"));
  }

  @Test
  public void testMultilineBullets () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final String sBaseText = StringHelper.getRepeated ("In typography, cap height is the height of a capital letter above the baseline for a particular typeface.[1] It specifically is the height of capital letters that are flat—such as H or I—as opposed to round letters such as O, or pointed letters like A, both of which may display overshoot. The height of the small letters is the x-height. ",
                                                       5);

    final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (15f), BulletPointCreatorSymbol.createEmptyDot (10f));
    for (int i = 0; i < 10; ++i)
      aList.addBulletPoint (new PLText (sBaseText, r10));
    aPS1.addElement (aList);

    new PageLayoutPDF ().addPageSet (aPS1).setCompressPDF (false).renderTo (new File ("pdf/plbulletpointlist/basic-multiline.pdf"));
  }

  @Test
  public void testBigBullets () throws PDFCreationException
  {
    final FontSpec r30 = new FontSpec (PreloadFont.REGULAR, 30);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (30f), BulletPointCreatorSymbol.createFilledDot (30f));
    for (int i = 0; i < 10; ++i)
      aList.addBulletPoint (new PLText ("Bullet point item " + i, r30).setFillColor (Color.GREEN));
    aPS1.addElement (aList);

    new PageLayoutPDF ().addPageSet (aPS1).setCompressPDF (false).renderTo (new File ("pdf/plbulletpointlist/big-bullets.pdf"));
  }

  @Test
  public void testDifferentBulletSizes () throws PDFCreationException
  {
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (int i = 1; i < 12; ++i)
    {
      final float f = i * 5;
      final FontSpec r = new FontSpec (PreloadFont.REGULAR, f);
      final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (f + 10), BulletPointCreatorSymbol.createFilledDot (f));
      aList.addBulletPoint (new PLText ("Bullet point item " + f, r));
      aPS1.addElement (aList);
    }

    new PageLayoutPDF ().addPageSet (aPS1).setCompressPDF (false).renderTo (new File ("pdf/plbulletpointlist/different-bullet-sizes.pdf"));
  }
}
