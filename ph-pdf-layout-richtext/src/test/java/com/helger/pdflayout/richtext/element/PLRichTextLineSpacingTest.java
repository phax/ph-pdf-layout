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
 * Port of the {@code LineSpacingTest} from the pdfbox-layout source repo. Renders the same markup
 * string at three different {@link PLRichText#setLineSpacing line-spacing} values so the spacing
 * plumbing can be pixel-diffed against a checked-in reference.
 *
 * @author Philip Helger
 */
public final class PLRichTextLineSpacingTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();

  @Test
  public void testLineSpacing () throws PDFCreationException
  {
    final String sText = "*Lorem ipsum* dolor sit amet, consetetur sadipscing elitr, " +
                         "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                         "aliquyam erat, _sed diam_ voluptua.";

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK).setLineSpacing (1.0f));

    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK).setLineSpacing (1.5f));

    aPS.addElement (PLRichText.createFromMarkup (sText, FONT_FAMILY, 11f, PLColor.BLACK).setLineSpacing (2.0f));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-linespacing.pdf"));
  }
}
