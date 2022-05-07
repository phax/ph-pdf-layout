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
package com.helger.pdflayout.element.link;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.debug.PLDebugRender;
import com.helger.pdflayout.element.image.PLImage;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for {@link PLExternalLink}
 *
 * @author Philip Helger
 */
public final class PLExternalLinkTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testBasic () throws PDFCreationException, IOException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r12 = new FontSpec (PreloadFont.REGULAR, 12);

    if (false)
      PLDebugRender.setDebugRender (true);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLExternalLink (new PLText ("bla", r10)).setURI ("https://example.org")
                                                                 .setBorder (Color.RED)
                                                                 .setFillColor (Color.YELLOW)
                                                                 .setMarginBottom (20));
    aPS1.addElement (new PLExternalLink (new PLText ("bla", r10)).setURI ("https://example.org")
                                                                 .setBorder (Color.GREEN)
                                                                 .setBorder (new BorderStyleSpec (new LineDashPatternSpec (2, 3)))
                                                                 .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLExternalLink (new PLText ("bla p10", r10)).setURI ("https://example.org")
                                                                     .setBorder (Color.GREEN)
                                                                     .setBorder (new BorderStyleSpec (new LineDashPatternSpec (2, 3)))
                                                                     .setPadding (10)
                                                                     .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLExternalLink (new PLText ("bla m10", r10)).setURI ("https://example.org")
                                                                     .setBorder (Color.GREEN)
                                                                     .setBorder (new BorderStyleSpec (new LineDashPatternSpec (2, 3)))
                                                                     .setMargin (10)
                                                                     .setFillColor (Color.YELLOW));
    aPS1.addElement (new PLExternalLink (new PLText ("This is much longer text p20", r12)).setURI ("https://example.org")
                                                                                          .setBorder (Color.RED)
                                                                                          .setFillColor (Color.YELLOW)
                                                                                          .setMargin (20));
    aPS1.addElement (new PLExternalLink (new PLText ("And this looks like in the browser",
                                                     r10)).setURI ("https://example.org")
                                                          .setBorderBottom (new BorderStyleSpec (Color.BLUE)));
    aPS1.addElement (new PLText ("This is no link", r10));

    aPS1.addElement (new PLExternalLink (new PLImage (ImageIO.read (ClassPathResource.getInputStream ("images/test1.jpg")),
                                                      50,
                                                      50)).setURI ("https://example.org")
                                                          .setBorder (Color.RED)
                                                          .setFillColor (Color.YELLOW));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plexternallink/basic.pdf"));
  }
}
