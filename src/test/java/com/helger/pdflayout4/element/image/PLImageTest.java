/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.image;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PLDebugTestRule;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.hbox.PLHBox;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for {@link PLImage} and {@link PLStreamImage}
 *
 * @author Philip Helger
 */
public final class PLImageTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testBasic () throws PDFCreationException, IOException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.addElement (new PLText ("First line - left image below", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (Color.RED));
    aPS1.addElement (new PLImage (ImageIO.read (ClassPathResource.getInputStream ("images/test1.jpg")), 50, 50));

    aPS1.addElement (new PLText ("Second line - table with 5 columns below", r10).setHorzAlign (EHorzAlignment.CENTER)
                                                                                 .setBorder (new BorderStyleSpec (Color.BLUE)));
    final PLHBox aHBox = new PLHBox ();
    aHBox.addColumn (new PLText ("Col1", r10), WidthSpec.perc (10));
    aHBox.addColumn (new PLStreamImage (new ClassPathResource ("images/test1.jpg"), 50, 50).setFillColor (Color.BLUE), WidthSpec.abs (50));
    aHBox.addColumn (new PLText ("Col2", r10).setHorzAlign (EHorzAlignment.CENTER), WidthSpec.star ());
    aHBox.addColumn (new PLStreamImage (new ClassPathResource ("images/test1.jpg"), 50, 50).setFillColor (Color.RED).setBorder (Color.PINK),
                     WidthSpec.abs (50));
    aHBox.addColumn (new PLText ("Col3", r10), WidthSpec.perc (10));
    aPS1.addElement (aHBox);

    aPS1.addElement (new PLText ("Last line", r10).setHorzAlign (EHorzAlignment.CENTER).setBorder (Color.GREEN));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plimage/basic.pdf"));
  }

  @Test
  public void testTextOverImage () throws PDFCreationException, IOException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    final PLHBox aBox = new PLHBox ();
    aBox.addColumn (new PLImage (ImageIO.read (ClassPathResource.getInputStream ("images/test1.jpg")), 50, 50), WidthSpec.abs (50));
    aBox.addColumn (new PLText ("Text over image", r10.getCloneWithDifferentColor (Color.RED)).setMarginLeft (-50).setMarginTop (10),
                    WidthSpec.abs (50));
    aPS1.addElement (aBox);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plimage/text-over-image.pdf"));
  }
}
