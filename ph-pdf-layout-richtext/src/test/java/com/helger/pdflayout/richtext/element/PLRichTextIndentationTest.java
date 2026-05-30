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

import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.IPLElement;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.richtext.PLRichTextTestComparer;
import com.helger.pdflayout.richtext.run.PLFontFamily;

/**
 * Render-and-pixel-diff regression for {@link PLRichTextBlocks}. Builds a markup string mirroring
 * the indentation portion of the original {@code IndentationTest} (plain paragraph + bullet list +
 * numbered list + closing paragraph) and compares the resulting PDF against a checked-in reference.
 *
 * @author Philip Helger
 */
public final class PLRichTextIndentationTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();

  @Test
  public void testIndent () throws PDFCreationException
  {
    final String sMarkup = "For your convenience, you can do all that much easier with markup, e.g. **simple indentation**\n" +
                           "--At vero eos et accusam\n" +
                           "-!And end the indentation. Now a list:\n" +
                           "-+This is a list item\n" +
                           "-+Another list item\n" +
                           " -+A sub list item\n" +
                           "-+And yet another one\n" +
                           "-!Even enumeration is supported:\n" +
                           "-#This is a list item\n" +
                           "-#Another list item\n" +
                           "-#And yet another one\n" +
                           "-!And that's the end of it.";

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    final ICommonsList <IPLElement <?>> aBlocks = PLRichTextBlocks.parseMarkup (sMarkup,
                                                                                FONT_FAMILY,
                                                                                11f,
                                                                                PLColor.BLACK);
    for (final IPLElement <?> aBlock : aBlocks)
      aPS.addElement (aBlock);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-indent.pdf"));
  }
}
