/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
 * Port of the {@code MultiplePagesTest} from the pdfbox-layout source repo.
 * Renders enough markup paragraphs that the {@link PLRichText} block has to
 * split vertically across multiple pages, exercising the splittable path of
 * {@code PLRichText}.
 *
 * @author Philip Helger
 */
public final class PLRichTextMultiplePagesTest
{
  private static final PLFontFamily FONT_FAMILY = new PLFontFamily (PreloadFont.TIMES,
                                                                    PreloadFont.TIMES_BOLD,
                                                                    PreloadFont.TIMES_ITALIC,
                                                                    PreloadFont.TIMES_BOLD_ITALIC);

  @Test
  public void testMultiplePages () throws PDFCreationException
  {
    final String sText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                         "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                         "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* " +
                         "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata " +
                         "sanctus est *Lorem ipsum dolor sit* amet. Lorem ipsum dolor sit amet, " +
                         "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
                         "ut labore et dolore magna aliquyam erat, *sed diam voluptua*.\n\n" +
                         "At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd " +
                         "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    // Three blocks at three different font sizes — combined height is well above
    // a single A4 page, so the vertical split path runs.
    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 14f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-multiplepages.pdf"));
  }
}
