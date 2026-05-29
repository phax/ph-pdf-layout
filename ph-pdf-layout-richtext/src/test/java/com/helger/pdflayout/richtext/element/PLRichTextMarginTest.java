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
 * Port of the {@code MarginTest} from the pdfbox-layout source repo. The original test exercised
 * per-element margins via {@code VerticalLayoutHint(margin...)}; in ph-pdf-layout the closest
 * analogue is {@link PLPageSet#setMargin}. This test renders three blocks of markup on a
 * {@link PLPageSet} with an asymmetric margin so the page-level margin plumbing is covered by a
 * pixel-diff regression.
 *
 * @author Philip Helger
 */
public final class PLRichTextMarginTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();

  @Test
  public void testMargin () throws PDFCreationException
  {
    final String sText1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                          "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                          "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
                          "duo dolores et ea rebum.";

    final String sText2 = "short text, right aligned with some margin";

    // Asymmetric margins: small top/bottom, large left/right.
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 150, 40, 150);

    aPS.addElement (PLRichText.createFromMarkup (sText1, FONT_FAMILY, 11f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText2, FONT_FAMILY, 11f, PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup (sText1, FONT_FAMILY, 11f, PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-margin.pdf"));
  }
}
