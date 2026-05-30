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
 * Focused test for the {@code {bg:...}...{bg}} markup. Exercises ONLY the two background extent
 * modes (TIGHT and LINE_HEIGHT) so the visual difference between them is directly comparable in the
 * reference PDF — no other markup features (bold, italic, sub/superscript, ...) are mixed into the
 * highlighted runs.
 *
 * @author Philip Helger
 */
public final class PLRichTextBackgroundTest
{
  private static final PLFontFamily FONT_FAMILY = PLFontFamily.timesNewRoman ();
  private static final float FONT_SIZE = 12f;
  private static final float FONT_SIZE_LARGE = 20f;

  @Test
  public void testBackgroundModes () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40, 60, 40, 60);

    // ---------- TIGHT extent (the default) ----------
    aPS.addElement (PLRichText.createFromMarkup ("TIGHT: default extent (no qualifier).",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Before {bg:#ffff00}highlighted yellow{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Explicit qualifier: before {bg:tight:#c0ffc0}highlighted green{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Same line, multiple non-overlapping tight highlights: adjacent segments must NOT merge,
    // each box stays bounded by its own span.
    aPS.addElement (PLRichText.createFromMarkup ("Three spans on one line: {bg:#ffd0d0}red{bg} {bg:#d0ffd0}green{bg} {bg:#d0d0ff}blue{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Tight highlight that wraps across a line break: each wrapped fragment gets its own box.
    aPS.addElement (PLRichText.createFromMarkup ("Tight wrap: lead-in text {bg:#ffe0c0}lorem ipsum dolor sit amet " +
                                                 "consectetur adipiscing elit sed do eiusmod tempor incididunt ut " +
                                                 "labore et dolore magna aliqua{bg} trailing text.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Tight at a larger font size — the box must scale with the segment font.
    aPS.addElement (PLRichText.createFromMarkup ("Tight large: before {bg:#ffff00}highlighted yellow{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE_LARGE,
                                                 PLColor.BLACK));
    // Tight + bold / italic / bold-italic inside the highlight.
    aPS.addElement (PLRichText.createFromMarkup ("Tight + styles: {bg:#ffe0a0}plain, **bold**, *italic*, ***bold-italic*** and __underlined__{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Tight + subscript / superscript inside the highlight. The TIGHT box
    // follows each segment's own font and shifted baseline, so the highlight
    // "stair-steps" through H{_}2{_} and m{^}2{^}.
    aPS.addElement (PLRichText.createFromMarkup ("Tight + sub/sup: {bg:#c0e0ff}water H{_}2{_}O and area m{^}2{^} inside{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));

    // ---------- LINE_HEIGHT extent ----------
    aPS.addElement (PLRichText.createFromMarkup ("LINE_HEIGHT: qualifier {bg:line:#...}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Before {bg:line:#ffff00}highlighted yellow{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    aPS.addElement (PLRichText.createFromMarkup ("Before {bg:line:#c0ffc0}highlighted green{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Same line, multiple non-overlapping line-height highlights: even though all share the
    // SAME Y bounds, the fills stay separated by the non-highlighted whitespace between them.
    aPS.addElement (PLRichText.createFromMarkup ("Three spans on one line: {bg:line:#ffd0d0}red{bg} {bg:line:#d0ffd0}green{bg} {bg:line:#d0d0ff}blue{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Line-height highlight that wraps: the two wrapped fragments must paint contiguous fills
    // (no seam between them) because the LINE_HEIGHT rule fills the inter-baseline slot.
    aPS.addElement (PLRichText.createFromMarkup ("Line-height wrap: lead-in text {bg:line:#ffe0c0}lorem ipsum dolor sit amet " +
                                                 "consectetur adipiscing elit sed do eiusmod tempor incididunt ut " +
                                                 "labore et dolore magna aliqua{bg} trailing text.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Line-height at a larger font size — slot grows with the element's textHeight.
    aPS.addElement (PLRichText.createFromMarkup ("Line-height large: before {bg:line:#ffff00}highlighted yellow{bg} after.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE_LARGE,
                                                 PLColor.BLACK));
    // Line-height + bold / italic / bold-italic inside the highlight: the slot
    // stays uniform because all segments share the line's baseline and height.
    aPS.addElement (PLRichText.createFromMarkup ("Line-height + styles: {bg:line:#ffe0a0}plain, **bold**, *italic*, ***bold-italic*** and __underlined__{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));
    // Line-height + subscript / superscript: contrast with the TIGHT version
    // above — here the slot stays a single rectangle across the sub/sup runs.
    aPS.addElement (PLRichText.createFromMarkup ("Line-height + sub/sup: {bg:line:#c0e0ff}water H{_}2{_}O and area m{^}2{^} inside{bg}.",
                                                 FONT_FAMILY,
                                                 FONT_SIZE,
                                                 PLColor.BLACK));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    PLRichTextTestComparer.renderAndCompare (aLayout, new File ("target/test-pdfs/richtext-background.pdf"));
  }
}
