/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout4.base.EPLPlaceholder;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.hbox.PLHBox;
import com.helger.pdflayout4.element.special.PLSpacerX;
import com.helger.pdflayout4.element.special.PLSpacerY;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.element.vbox.PLVBox;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.LineDashPatternSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for class {@link PageLayoutPDF}.
 *
 * @author Philip Helger
 */
public final class PageLayoutPDFTest
{
  @Rule
  public TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r12 = r10.getCloneWithDifferentFontSize (12);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (50, 30).setPadding (15);
    aPS1.setPageHeader (new PLText ("Das ist die Kopfzeile", r10).setBorderBottom (new BorderStyleSpec (Color.BLACK))
                                                                 .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText ("Das ist die Fusszeile, Seite " +
                                    EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable () +
                                    " von " +
                                    EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable (),
                                    r10).setReplacePlaceholder (true)
                                        .setBorderTop (new BorderStyleSpec (Color.BLACK))
                                        .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Zeile 1", r10));
    {
      final PLHBox aHBox = new PLHBox ();
      // First column 30%
      aHBox.addColumn (new PLText ("Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text Spalte 1 mit Text ",
                                   r10).setMargin (10).setPadding (5).setHorzAlign (EHorzAlignment.LEFT).setBorder (
                                                                                                                    new BorderStyleSpec (Color.GREEN),
                                                                                                                    new BorderStyleSpec (Color.BLUE),
                                                                                                                    new BorderStyleSpec (Color.CYAN),
                                                                                                                    new BorderStyleSpec (Color.RED)),
                       WidthSpec.perc (30));
      // Remaining columns use each the same part of the space: WidthSpec.star()
      aHBox.addColumn (new PLText ("Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text Spalte 2 mit Text ",
                                   r10.getCloneWithDifferentColor (Color.BLUE)).setBorder (new BorderStyleSpec (Color.RED))
                                                                               .setHorzAlign (EHorzAlignment.CENTER)
                                                                               .setMaxRows (3),
                       WidthSpec.star ());
      aHBox.addColumn (new PLText ("Spalte 3 mit Text Spalte 3 mit Text Spalte 3 mit Text Ende", r10).setMarginTop (10)
                                                                                                     .setPadding (5)
                                                                                                     .setBorder (new BorderStyleSpec (Color.GREEN,
                                                                                                                                      LineDashPatternSpec.DASHED_3))
                                                                                                     .setHorzAlign (EHorzAlignment.RIGHT),
                       WidthSpec.star ());
      aHBox.addColumn (new PLText ("Spalte 4 mit Text Spalte 4 mit Text Spalte 4 mit Text Ende",
                                   r10.getCloneWithDifferentFont (PreloadFont.REGULAR_ITALIC)).setBorder (new BorderStyleSpec (Color.RED))
                                                                                              .setHorzAlign (EHorzAlignment.LEFT),
                       WidthSpec.star ());
      aPS1.addElement (aHBox);
    }
    {
      final PLHBox h = new PLHBox ();
      h.addColumn (new PLText ("Column 1", r10.getCloneWithDifferentFontSize (24)).setHorzAlign (EHorzAlignment.CENTER),
                   WidthSpec.star ());
      final PLVBox v = new PLVBox ();
      v.addRow (new PLText ("Column 2, Row 1", r10));
      v.addRow (new PLText ("Column 2, Row 2",
                            r12.getCloneWithDifferentColor (Color.RED)).setFillColor (new Color (0xdddddd)));
      v.addRow (new PLText ("Column 2, Row 3", r12));
      h.addColumn (v, WidthSpec.star ());
      final PLVBox v2 = new PLVBox ();
      v2.addRow (new PLText ("Column 3, Row 1", r10));
      v2.addRow (new PLText ("Column 3, Row 2", r10));
      v2.addRow (new PLText ("Column 3, Row 3", r10).setFillColor (new Color (0xdddddd)));
      h.addColumn (v2, WidthSpec.star ());
      aPS1.addElement (h);
    }
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r10).setHorzAlign (EHorzAlignment.CENTER)
                                     .setBorder (new BorderStyleSpec (Color.BLACK)));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12).setHorzAlign (EHorzAlignment.RIGHT)
                                     .setFillColor (new Color (0xff0000))
                                     .setPadding (0, 5));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 new FontSpec (PreloadFont.MONOSPACE, 14)));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12).setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r10).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r10).setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r10));
    aPS1.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12).setHorzAlign (EHorzAlignment.CENTER));

    final PLPageSet aPS2 = new PLPageSet (PDRectangle.A4.getWidth (), PDRectangle.A4.getWidth ()).setMargin (50, 30)
                                                                                                 .setPadding (15);
    aPS2.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r10).setHorzAlign (EHorzAlignment.RIGHT));
    aPS2.addElement (new PLText ("Zeile 2\nZeile 3\nTäst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort Täst Täst Täst Täst Täst Tästlangeswort ",
                                 r12));

    final PLPageSet aPS3 = new PLPageSet (PDRectangle.A4.getHeight (), PDRectangle.A4.getWidth ()).setMargin (50, 30)
                                                                                                  .setPadding (15);
    aPS3.setPreRenderContextCustomizer (aCtx -> {
      final int nTotal = aCtx.getPlaceholderAsInt (EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable (), -1);
      aCtx.addPlaceholder ("${pages-1}", nTotal - 1);
    });
    aPS3.setPageHeader (new PLText ("Das ist die Kopfzeile3", r10).setBorderBottom (new BorderStyleSpec (Color.BLACK))
                                                                  .setHorzAlign (EHorzAlignment.CENTER));
    aPS3.setPageFooter (new PLText ("Das ist die Fusszeile3, Seite " +
                                    EPLPlaceholder.PAGESET_PAGE_NUMBER.getVariable () +
                                    " von " +
                                    EPLPlaceholder.PAGESET_PAGE_COUNT.getVariable () +
                                    " bzw. " +
                                    EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable () +
                                    " von " +
                                    EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable () +
                                    "; my value: ${pages-1}",
                                    r10).setReplacePlaceholder (true)
                                        .setBorderTop (new BorderStyleSpec (Color.BLACK))
                                        .setHorzAlign (EHorzAlignment.CENTER));
    aPS3.addElement (new PLText ("Zeile 1\n\nZeile 3", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.addPageSet (aPS2);
    aPageLayout.addPageSet (aPS3);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test1.pdf"));
  }

  @Test
  public void testCreatePDFProperties () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);
    aPS1.addElement (new PLText ("Dummy line", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.setDocumentAuthor ("Weird author äöü");
    aPageLayout.setDocumentTitle ("Special chars €!\"§$%&/()=\uFFE5");
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-properties.pdf"));
  }

  @Test
  public void testPageLayoutPDFMarginPadding () throws PDFCreationException
  {
    final String sLID = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    final String sLIDShort = sLID.substring (0, sLID.length () / 3);

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (20, 30, 40, 10)
                                                         .setPadding (20, 30, 40, 10)
                                                         .setFillColor (new Color (0xeeeeee));
    aPS1.setPageHeader (new PLText ("Headline", r10).setBorder (new BorderStyleSpec (Color.BLACK))
                                                    .setPadding (4, 0)
                                                    .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText ("Page " +
                                    EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable () +
                                    " of " +
                                    EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable (),
                                    r10).setReplacePlaceholder (true)
                                        .setBorder (new BorderStyleSpec (Color.BLACK))
                                        .setMarginTop (10)
                                        .setPadding (4, 10)
                                        .setHorzAlign (EHorzAlignment.RIGHT));
    {
      final PLHBox h = new PLHBox ();

      h.addColumn (new PLText (sLID, r10).setHorzAlign (EHorzAlignment.CENTER).setPadding (0, 20), WidthSpec.star ());

      final PLVBox v1 = new PLVBox ();
      v1.addRow (new PLText (sLIDShort, r10).setMargin (0)
                                            .setPadding (10)
                                            .setBorder (new BorderStyleSpec (Color.BLUE)));
      v1.addRow (new PLText (sLIDShort, r10).setMargin (5)
                                            .setPadding (5)
                                            .setBorder (new BorderStyleSpec (Color.BLUE,
                                                                             LineDashPatternSpec.DASHED_2)));
      v1.addRow (new PLText (sLIDShort, r10).setMargin (10)
                                            .setPadding (0)
                                            .setBorder (new BorderStyleSpec (Color.BLUE)));
      h.addColumn (v1, WidthSpec.star ());

      final PLVBox v2 = new PLVBox ();
      v2.addRow (new PLText (sLIDShort, r10).setHorzAlign (EHorzAlignment.RIGHT));
      v2.addRow (new PLText (sLIDShort, r10).setHorzAlign (EHorzAlignment.RIGHT).setFillColor (new Color (0xdddddd)));
      v2.addRow (new PLText (sLIDShort, r10).setHorzAlign (EHorzAlignment.RIGHT));
      h.addColumn (v2, WidthSpec.star ());
      aPS1.addElement (h);
    }
    if (true)
    {
      aPS1.addElement (new PLText (sLID, r10.getCloneWithDifferentColor (Color.WHITE))
                                                                                      .setHorzAlign (EHorzAlignment.RIGHT)
                                                                                      .setBorder (new BorderStyleSpec (Color.BLACK))
                                                                                      .setFillColor (Color.RED)
                                                                                      .setMargin (0)
                                                                                      .setPadding (10));
      aPS1.addElement (new PLText (sLID, r10.getCloneWithDifferentColor (Color.RED)).setHorzAlign (EHorzAlignment.RIGHT)
                                                                                    .setBorder (new BorderStyleSpec (Color.BLACK))
                                                                                    .setFillColor (Color.GREEN)
                                                                                    .setMargin (5)
                                                                                    .setPadding (5));
      aPS1.addElement (new PLText (sLID, r10.getCloneWithDifferentColor (Color.WHITE))
                                                                                      .setHorzAlign (EHorzAlignment.RIGHT)
                                                                                      .setBorder (new BorderStyleSpec (Color.BLACK))
                                                                                      .setFillColor (Color.BLUE)
                                                                                      .setMargin (10)
                                                                                      .setPadding (0));
      aPS1.addElement (new PLText (sLID, r10).setFillColor (new Color (0xabcdef)));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test2.pdf"));
  }

  @Test
  public void testDINLetter () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    {
      final PLVBox aVBox = new PLVBox ();
      final PLHBox aHBox = new PLHBox ();
      aHBox.addColumn (new PLSpacerX (), WidthSpec.abs (PLConvert.mm2units (20)));
      {
        final PLVBox aWindow = new PLVBox ();
        aWindow.addRow (new PLSpacerY (PLConvert.mm2units (42)));
        aWindow.addRow (new PLText ("Hr. MaxMustermann\nMusterstraße 15\nA-1010 Wien",
                                    r10).setExactSize (PLConvert.mm2units (90), PLConvert.mm2units (45)));
        aWindow.addRow (new PLSpacerY (PLConvert.mm2units (12)));
        aHBox.addColumn (aWindow, WidthSpec.abs (PLConvert.mm2units (90)));
      }
      aHBox.addColumn (new PLSpacerX (), WidthSpec.star ());
      aVBox.addRow (aHBox);
      aPS1.addElement (aVBox);
    }
    aPS1.addElement (new PLSpacerY (PLConvert.mm2units (99)));
    aPS1.addElement (new PLSpacerY (PLConvert.mm2units (98.5f)));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (true);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-din-letter.pdf"));
  }
}
