/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.table;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.PLPageSet;
import com.helger.pdflayout.element.hbox.PLHBoxSplittable;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.element.text.PLTextWithPlaceholders;
import com.helger.pdflayout.render.RenderPageIndex;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for class {@link PLTable}.
 *
 * @author Philip Helger
 */
public final class PLTableTest2
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setPaddingRight (20)
                                                         .setFillColor (Color.YELLOW);
    aPS1.setPageHeader (new PLText ("Headline", r10).setBorder (new BorderStyleSpec (Color.BLACK))
                                                    .setPadding (4, 0)
                                                    .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLTextWithPlaceholders ("Page " +
                                                    RenderPageIndex.PLACEHOLDER_PAGESET_PAGE_NUMBER +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_TOTAL_PAGE_COUNT,
                                                    r10).setBorder (new BorderStyleSpec (Color.RED))
                                                        .setPadding (4, 0)
                                                        .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Erste Dummy Zeile", r10));

    // Start table
    final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4)
                                : PLTable.createWithPercentage (10, 40, 25, 25);
    aTable.setHeaderRowCount (1);

    // Add row
    final PLHBoxSplittable aRow = aTable.addTableRow (new PLText ("ID", r14b).setPadding (aPadding),
                                                      new PLText ("Name", r14b).setPadding (aPadding),
                                                      new PLText ("Sum1", r14b).setPadding (aPadding)
                                                                               .setHorzAlign (EHorzAlignment.CENTER),
                                                      new PLText ("Sum2", r14b).setPadding (aPadding)
                                                                               .setHorzAlign (EHorzAlignment.RIGHT));
    if (false)
      aRow.setColumnBorder (new BorderStyleSpec (Color.GRAY)).setFillColor (Color.WHITE);

    // Test colspan
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 2a", r10), 2),
                           new PLTableCell (new PLText ("Colspan 2b", r10), 2))
          .setColumnBorder (new BorderStyleSpec (Color.BLACK));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 3a", r10), 3),
                           new PLTableCell (new PLText ("Colspan 1b", r10), 1))
          .setColumnBorder (new BorderStyleSpec (Color.BLACK));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 1a", r10), 1),
                           new PLTableCell (new PLText ("Colspan 3b", r10), 3))
          .setColumnBorder (new BorderStyleSpec (Color.BLACK));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 4", r10), 4))
          .setColumnBorder (new BorderStyleSpec (Color.BLACK));
    aPS1.addElement (aTable);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-table-new.pdf"));
  }
}
