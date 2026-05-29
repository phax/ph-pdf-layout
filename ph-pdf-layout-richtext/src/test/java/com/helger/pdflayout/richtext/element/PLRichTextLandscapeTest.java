/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.richtext.element;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.richtext.PLRichTextTestComparer;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Port of the {@code LandscapeTest} from the pdfbox-layout source repo. Renders
 * markup on an A4 page rotated to landscape orientation so the page-size
 * plumbing for non-portrait sheets is covered by a pixel-diff regression.
 *
 * @author Philip Helger
 */
public final class PLRichTextLandscapeTest
{
  private static final PLFontFamily FONT_FAMILY = new PLFontFamily (PreloadFont.TIMES,
                                                                    PreloadFont.TIMES_BOLD,
                                                                    PreloadFont.TIMES_ITALIC,
                                                                    PreloadFont.TIMES_BOLD_ITALIC);

  @Test
  public void testLandscape () throws PDFCreationException
  {
    final String sText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                         "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                         "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* " +
                         "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata " +
                         "sanctus est *Lorem ipsum dolor sit* amet.";

    // A4 rotated to landscape: swap width and height of the standard A4 rectangle.
    final PDRectangle aLandscape = new PDRectangle (PDRectangle.A4.getHeight (), PDRectangle.A4.getWidth ());
    final PLPageSet aPS = new PLPageSet (aLandscape).setMargin (40, 60, 40, 60);

    aPS.addElement (PLRichText.createFromMarkup ("*Format A4 in Landscape*", FONT_FAMILY, 20f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-landscape.pdf"));
  }
}
