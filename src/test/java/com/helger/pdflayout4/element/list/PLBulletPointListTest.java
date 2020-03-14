/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * Test class for {@link PLBulletPointList}
 *
 * @author Philip Helger
 */
public final class PLBulletPointListTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    // This is the "Bullet point" in "Symbol font" (char 183)
    final PLBulletPointList aList = new PLBulletPointList (15f, new BulletPointCreatorSymbol (10f));
    for (int i = 0; i < 10; ++i)
      aList.addBulletPoint (new PLText ("Bullet point item " + i, r10));
    aPS1.addElement (aList);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream (new File ("pdf/plbulletpointlist/basic.pdf")));
  }

  @Test
  public void testBigBullets () throws PDFCreationException
  {
    final FontSpec r30 = new FontSpec (PreloadFont.REGULAR, 30);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    // This is the "Bullet point" in "Symbol font" (char 183)
    final PLBulletPointList aList = new PLBulletPointList (15f, new BulletPointCreatorSymbol (30f));
    for (int i = 0; i < 10; ++i)
      aList.addBulletPoint (new PLText ("Bullet point item " + i, r30));
    aPS1.addElement (aList);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream (new File ("pdf/plbulletpointlist/big-bullets.pdf")));
  }
}
