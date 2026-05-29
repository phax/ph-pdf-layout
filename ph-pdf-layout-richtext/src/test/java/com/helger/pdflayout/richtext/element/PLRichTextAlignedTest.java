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
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Port of the {@code AlignedTest} from the pdfbox-layout source repo. Renders
 * three {@link PLRichText} blocks with {@link EHorzAlignment#LEFT},
 * {@link EHorzAlignment#CENTER} and {@link EHorzAlignment#RIGHT} so the
 * horizontal alignment plumbing on {@code PLRichText} can be pixel-diffed
 * against a checked-in reference.
 *
 * @author Philip Helger
 */
public final class PLRichTextAlignedTest
{
  private static final PLFontFamily FONT_FAMILY = new PLFontFamily (PreloadFont.TIMES,
                                                                    PreloadFont.TIMES_BOLD,
                                                                    PreloadFont.TIMES_ITALIC,
                                                                    PreloadFont.TIMES_BOLD_ITALIC);

  @Test
  public void testAligned () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    aPS.addElement (PLRichText.createFromMarkup ("This is some left aligned text", FONT_FAMILY, 11f, PLColor.BLACK)
                              .setHorzAlign (EHorzAlignment.LEFT));

    aPS.addElement (PLRichText.createFromMarkup ("This is some centered text", FONT_FAMILY, 11f, PLColor.BLACK)
                              .setHorzAlign (EHorzAlignment.CENTER));

    aPS.addElement (PLRichText.createFromMarkup ("This is some right aligned text", FONT_FAMILY, 11f, PLColor.BLACK)
                              .setHorzAlign (EHorzAlignment.RIGHT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-aligned.pdf"));
  }
}
