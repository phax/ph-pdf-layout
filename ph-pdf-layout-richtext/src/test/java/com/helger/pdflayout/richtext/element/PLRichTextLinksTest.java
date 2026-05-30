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

/**
 * Port of the {@code LinksTest} from the pdfbox-layout source repo. Exercises external hyperlinks,
 * the no-underline link style, internal anchor references, and anchor declarations.
 *
 * @author Philip Helger
 */
public final class PLRichTextLinksTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();

  @Test
  public void testLinks () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    aPS.addElement (PLRichText.createFromMarkup ("This is a link to {link[https://github.com/phax/ph-pdf-layout]}ph-pdf-layout{link}.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Now the same link with color instead of underline {color:#ff5000}{link:none[https://github.com/phax/ph-pdf-layout]}ph-pdf-layout{link}{color:#000000}.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("And here comes a link to an internal anchor name {color:#ff5000}{link[#hello]}hello{link}{color:#000000}.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));

    aPS.addElement (PLRichText.createFromMarkup ("{anchor:hello}Here{anchor} comes the internal anchor named **hello**.",
                                                 FONT_FAMILY,
                                                 15f,
                                                 PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-links.pdf"));
  }
}
