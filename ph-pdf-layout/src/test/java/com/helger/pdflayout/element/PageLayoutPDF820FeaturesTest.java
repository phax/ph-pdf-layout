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
package com.helger.pdflayout.element;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PDFTestComparer;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.EPLPlaceholder;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.link.PLAnchor;
import com.helger.pdflayout.element.link.PLInternalLink;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.render.PLOutlineBuilder;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Example document showcasing the new layout features introduced with ph-pdf-layout 8.2.0:
 * <ul>
 * <li>{@link PLAnchor} and anchor names on block elements via
 * {@link com.helger.pdflayout.base.IPLHasAnchorName}</li>
 * <li>{@link PLInternalLink} for clickable in-document cross references</li>
 * <li>{@link PLOutlineBuilder} for PDF outlines (a.k.a. bookmarks), wired through the new
 * {@link com.helger.pdflayout.render.IPLRenderListener} and
 * {@link com.helger.pdflayout.IPDDocumentCustomizer} hooks</li>
 * </ul>
 * Produces {@code pdf/test-820-features.pdf}.
 *
 * @author Philip Helger
 */
public final class PageLayoutPDF820FeaturesTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  private static final String ANCHOR_TOC = "toc";
  private static final String ANCHOR_CH1 = "ch1";
  private static final String ANCHOR_CH1_SEC1 = "ch1-sec1";
  private static final String ANCHOR_CH1_SEC2 = "ch1-sec2";
  private static final String ANCHOR_CH2 = "ch2";
  private static final String ANCHOR_CH2_SEC1 = "ch2-sec1";
  private static final String ANCHOR_CH2_SEC2 = "ch2-sec2";
  private static final String ANCHOR_CH3 = "ch3";

  @Test
  public void testCreate820FeaturesDemo () throws PDFCreationException
  {
    final FontSpec aFontBody = new FontSpec (PreloadFont.REGULAR, 11);
    final FontSpec aFontSmall = new FontSpec (PreloadFont.REGULAR, 9);
    final FontSpec aFontH1 = new FontSpec (PreloadFont.REGULAR_BOLD, 20);
    final FontSpec aFontH2 = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final FontSpec aFontLink = new FontSpec (PreloadFont.REGULAR, 11, PLColor.BLUE);

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (50, 40);

    aPS.setPageHeader (new PLText ("ph-pdf-layout 8.2.0 - feature showcase", aFontSmall).setBorderBottom (
                                                                                                          new BorderStyleSpec (PLColor.gray (0x99)))
                                                                                        .setPadding (0, 0, 4, 0)
                                                                                        .setHorzAlign (EHorzAlignment.CENTER));

    aPS.setPageFooter (new PLText ("Page " +
                                   EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable () +
                                   " of " +
                                   EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable (),
                                   aFontSmall).setReplacePlaceholder (true)
                                              .setBorderTop (new BorderStyleSpec (PLColor.gray (0x99)))
                                              .setPadding (4, 0, 0, 0)
                                              .setHorzAlign (EHorzAlignment.CENTER));

    // Title page; the heading itself acts as the named destination "toc" so we
    // can link back to it from any chapter.
    aPS.addElement (new PLSpacerY (60));
    aPS.addElement (new PLText ("Table of Contents", aFontH1).setHorzAlign (EHorzAlignment.CENTER)
                                                             .setAnchorName (ANCHOR_TOC));
    aPS.addElement (new PLSpacerY (20));

    // Each TOC entry is a PLInternalLink wrapping a styled PLText that jumps
    // to the chapter's anchor name.
    aPS.addElement (new PLInternalLink (new PLText ("1. Introduction", aFontLink)).setTargetAnchorName (ANCHOR_CH1));
    aPS.addElement (new PLInternalLink (new PLText ("    1.1 Why this library exists", aFontLink)).setTargetAnchorName (
                                                                                                                        ANCHOR_CH1_SEC1));
    aPS.addElement (new PLInternalLink (new PLText ("    1.2 Document model", aFontLink)).setTargetAnchorName (
                                                                                                               ANCHOR_CH1_SEC2));
    aPS.addElement (new PLInternalLink (new PLText ("2. New in 8.2.0", aFontLink)).setTargetAnchorName (ANCHOR_CH2));
    aPS.addElement (new PLInternalLink (new PLText ("    2.1 Anchors and internal links", aFontLink))
                                                                                                     .setTargetAnchorName (ANCHOR_CH2_SEC1));
    aPS.addElement (new PLInternalLink (new PLText ("    2.2 PDF outlines (bookmarks)", aFontLink))
                                                                                                   .setTargetAnchorName (ANCHOR_CH2_SEC2));
    aPS.addElement (new PLInternalLink (new PLText ("3. Conclusion", aFontLink)).setTargetAnchorName (ANCHOR_CH3));

    aPS.addElement (new PLPageBreak (false));

    // Chapter 1; the headings use setAnchorName so PLInternalLink targets and
    // PLOutlineBuilder destinations resolve to the same spot. An explicit
    // setID is used so the outline builder can reference it through
    // PLRenderedElementCollector by element ID as an alternative addressing
    // scheme.
    aPS.addElement (new PLText ("1. Introduction", aFontH1).setID (ANCHOR_CH1).setAnchorName (ANCHOR_CH1));
    aPS.addElement (new PLSpacerY (8));
    aPS.addElement (new PLText ("1.1 Why this library exists", aFontH2).setID (ANCHOR_CH1_SEC1)
                                                                       .setAnchorName (ANCHOR_CH1_SEC1));
    aPS.addElement (new PLText ("ph-pdf-layout sits on top of Apache PDFBox and provides a fluid, CSS-like " +
                                "box model so that callers can think in margins, padding and borders rather " +
                                "than in raw PDF content streams.",
                                aFontBody));
    aPS.addElement (new PLSpacerY (10));
    aPS.addElement (new PLText ("1.2 Document model", aFontH2).setID (ANCHOR_CH1_SEC2).setAnchorName (ANCHOR_CH1_SEC2));
    aPS.addElement (new PLText ("A PageLayoutPDF contains one or more PLPageSet instances; each page set " +
                                "groups elements that share a page size and margins. Elements flow top to " +
                                "bottom and split across pages automatically when they do not fit.",
                                aFontBody));
    aPS.addElement (new PLSpacerY (20));

    // Back-to-TOC link. Demonstrates a forward-referenced anchor was set on
    // the very first element of the document.
    aPS.addElement (new PLInternalLink (new PLText ("[ Back to table of contents ]", aFontLink)).setTargetAnchorName (
                                                                                                                      ANCHOR_TOC));

    aPS.addElement (new PLPageBreak (false));

    // Chapter 2; this one uses a standalone PLAnchor marker before the
    // heading to demonstrate the "anchor next to an element" pattern as an
    // alternative to setAnchorName on the heading itself.
    aPS.addElement (new PLAnchor (ANCHOR_CH2));
    aPS.addElement (new PLText ("2. New in 8.2.0", aFontH1).setID (ANCHOR_CH2));
    aPS.addElement (new PLSpacerY (8));
    aPS.addElement (new PLText ("2.1 Anchors and internal links", aFontH2).setID (ANCHOR_CH2_SEC1)
                                                                          .setAnchorName (ANCHOR_CH2_SEC1));
    aPS.addElement (new PLText ("PLAnchor and the new IPLHasAnchorName interface let any element register a " +
                                "PDF named destination. PLInternalLink wraps another element and turns it " +
                                "into a clickable jump to such a destination - much like an HTML <a href=\"#id\">.",
                                aFontBody));
    aPS.addElement (new PLSpacerY (10));
    aPS.addElement (new PLText ("2.2 PDF outlines (bookmarks)", aFontH2).setID (ANCHOR_CH2_SEC2)
                                                                        .setAnchorName (ANCHOR_CH2_SEC2));
    aPS.addElement (new PLText ("PLOutlineBuilder collects a tree of (title, elementID) pairs and turns " +
                                "them into a PDF document outline. It implements IPLRenderListener so it " +
                                "captures element positions during rendering, and IPDDocumentCustomizer so " +
                                "the outline tree is written into the document right before save.",
                                aFontBody));
    aPS.addElement (new PLSpacerY (20));
    aPS.addElement (new PLInternalLink (new PLText ("[ Back to table of contents ]", aFontLink)).setTargetAnchorName (
                                                                                                                      ANCHOR_TOC));

    aPS.addElement (new PLPageBreak (false));

    // Chapter 3.
    aPS.addElement (new PLText ("3. Conclusion", aFontH1).setID (ANCHOR_CH3).setAnchorName (ANCHOR_CH3));
    aPS.addElement (new PLSpacerY (8));
    aPS.addElement (new PLText ("With named destinations, internal links and document outlines, " +
                                "ph-pdf-layout 8.2.0 makes long PDFs navigable without leaving the " +
                                "fluent layout API. Open this file in any modern PDF reader to see the " +
                                "outline panel populated and the table-of-contents entries become " +
                                "clickable.",
                                aFontBody));
    aPS.addElement (new PLSpacerY (20));
    aPS.addElement (new PLInternalLink (new PLText ("[ Back to table of contents ]", aFontLink)).setTargetAnchorName (
                                                                                                                      ANCHOR_TOC));

    // Build the outline tree matching the in-document structure. Entry IDs
    // reference the IPLObject.getOriginalID() (== setID(...)) of each
    // heading; PLOutlineBuilder resolves the actual page and coordinates
    // when it observes the corresponding element rendering.
    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    final PLOutlineBuilder.Entry aCh1 = aOutline.addEntry ("1. Introduction", ANCHOR_CH1);
    aCh1.addChild ("1.1 Why this library exists", ANCHOR_CH1_SEC1);
    aCh1.addChild ("1.2 Document model", ANCHOR_CH1_SEC2);
    final PLOutlineBuilder.Entry aCh2 = aOutline.addEntry ("2. New in 8.2.0", ANCHOR_CH2);
    aCh2.addChild ("2.1 Anchors and internal links", ANCHOR_CH2_SEC1);
    aCh2.addChild ("2.2 PDF outlines (bookmarks)", ANCHOR_CH2_SEC2);
    aOutline.addEntry ("3. Conclusion", ANCHOR_CH3);

    // The outline builder is both the per-page render listener (to capture
    // element positions) and the document customizer (to write the outline
    // tree into the saved PDF).
    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDocumentTitle ("ph-pdf-layout 8.2.0 feature showcase")
                                                          .setDocumentAuthor ("Philip Helger")
                                                          .addPageSet (aPS)
                                                          .setDocumentCustomizer (aOutline);

    PDFTestComparer.renderAndCompare (aPageLayout, new File ("pdf/test-820-features.pdf"));
  }
}
