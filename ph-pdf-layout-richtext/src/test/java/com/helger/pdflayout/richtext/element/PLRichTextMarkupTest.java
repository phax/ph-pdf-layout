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
 * Port of the {@code MarkupTest} from the pdfbox-layout source repo to the ph-pdf-layout rich-text
 * element. Renders the same bold/italic/colour/underline/sub-superscript markup combinations and
 * pixel-diffs against a checked-in reference.
 * <p>
 * The Markdown-style indentation prefixes ({@code --}, {@code -!}, {@code -+}, {@code -#}) from the
 * original test live in {@code PLRichTextIndentationTest} because they operate at the block /
 * paragraph level rather than the inline run level.
 * </p>
 *
 * @author Philip Helger
 */
public final class PLRichTextMarkupTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();

  @Test
  public void testMarkup () throws PDFCreationException
  {
    // Hard breaks need a CommonMark trigger (two trailing spaces or backslash);
    // bare \n is a soft break (rendered as a space).
    final String sText1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                          "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                          "aliquyam erat, *sed diam* voluptua. At vero eos et **accusam et justo** " +
                          "duo dolores et ea rebum.  \nStet clita kasd gubergren, no sea takimata " +
                          "sanctus est **Lorem ipsum *dolor** sit* amet. Lorem ipsum dolor sit amet, " +
                          "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
                          "ut labore et dolore magna aliquyam erat, **sed diam voluptua.  \n" +
                          "At vero eos et accusam** et justo duo dolores et ea rebum. Stet clita kasd " +
                          "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    aPS.addElement (PLRichText.createFromMarkup (sText1, FONT_FAMILY, 11f, PLColor.BLACK));

    aPS.addElement (PLRichText.createFromMarkup ("Markup supports **bold**, *italic*, and **even *mixed** markup*.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Escape \\* with \\\\\\* and \\** with \\\\\\** in markup.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("And now also {color:#ff0000}c{color:#00ff00}o{color:#0000ff}l{color:#00cccc}o{color:#cc00cc}r{color:#000000}.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("You can alternate the position and thickness of an __underline__, " +
                                                 "so you may also use this to __{0.25:}strike through__ or blacken __{0.25:20}things__ out.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Inline metrics: water H{_}2{_}O, areas in m{^}2{^} and m{^}3{^}, " +
                                                 "{_}subscript{_} and {^}superscript{^}.",
                                                 FONT_FAMILY,
                                                 11f,
                                                 PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-markup.pdf"));
  }
}
