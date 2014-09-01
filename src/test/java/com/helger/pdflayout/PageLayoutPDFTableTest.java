/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.pdflayout;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileUtils;
import com.helger.commons.mock.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.PLHBoxSplittable;
import com.helger.pdflayout.element.PLPageBreak;
import com.helger.pdflayout.element.PLPageSet;
import com.helger.pdflayout.element.PLTable;
import com.helger.pdflayout.element.PLText;
import com.helger.pdflayout.element.PLTextWithPlaceholders;
import com.helger.pdflayout.element.PLTable.PLTableCell;
import com.helger.pdflayout.render.RenderPageIndex;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PDFFont;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Test class for class {@link PageLayoutPDF}.
 *
 * @author Philip Helger
 */
public final class PageLayoutPDFTableTest
{
  @Rule
  public TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PDFFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PDFFont.REGULAR_BOLD, 14);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDPage.PAGE_SIZE_A4).setMargin (30)
                                                              .setPadding (10, 0, 20, 0)
                                                              .setFillColor (new Color (0xddffff));
    aPS1.setPageHeader (new PLText ("Headline", r10).setBorder (new BorderStyleSpec (Color.BLACK))
                                                    .setPadding (0, 4)
                                                    .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLTextWithPlaceholders ("Page " +
                                                    RenderPageIndex.PLACEHOLDER_PAGESET_PAGE_NUMBER +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_TOTAL_PAGE_COUNT, r10).setBorder (new BorderStyleSpec (Color.RED))
                                                                                                      .setPadding (0, 4)
                                                                                                      .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Erste Dummy Zeile", r10));

    // Start table
    final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4) : PLTable.createWithPercentage (10,
                                                                                                           40,
                                                                                                           25,
                                                                                                           25);
    aTable.setHeaderRowCount (1);

    // Add row
    PLHBoxSplittable aRow = aTable.addTableRow (new PLText ("ID", r14b).setPadding (aPadding),
                                                new PLText ("Name", r14b).setPadding (aPadding),
                                                new PLText ("Sum1", r14b).setPadding (aPadding)
                                                                         .setHorzAlign (EHorzAlignment.CENTER),
                                                new PLText ("Sum2", r14b).setPadding (aPadding)
                                                                         .setHorzAlign (EHorzAlignment.RIGHT));
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

    // Add content lines
    for (int i = 0; i < 184; ++i)
    {
      // Width is determined by the width passed to the table creating method
      aRow = aTable.addTableRow (new PLText (Integer.toString (i), r10).setPadding (aPadding)
                                                                       .setVertAlign (EVertAlignment.BOTTOM),
                                 new PLText ("Name " +
                                                 i +
                                                 (i == 2 ? " this is extra text for row 2 that makes this line longer"
                                                        : ""),
                                             r10.getCloneWithDifferentColor (i % 3 == 0 ? Color.RED : Color.BLACK)).setPadding (aPadding),
                                 new PLText (Integer.toString (i * i), r10).setPadding (aPadding)
                                                                           .setHorzAlign (EHorzAlignment.CENTER),
                                 new PLText (Integer.toString (i + i), r10).setPadding (aPadding)
                                                                           .setHorzAlign (EHorzAlignment.RIGHT));
      if ((i % 4) == 0)
        aRow.setColumnBorder (new BorderStyleSpec (Color.GREEN)).setColumnBorderTop (null);
    }
    aPS1.addElement (aTable);

    // Start a new page
    aPS1.addElement (new PLPageBreak (false));
    aPS1.addElement (new PLText ("First line on new page", r10).setVertAlign (EVertAlignment.BOTTOM));
    // Next page
    aPS1.addElement (new PLPageBreak (false));
    // empty page by using forced page break
    aPS1.addElement (new PLPageBreak (true));
    aPS1.addElement (new PLText ("First line on last page after one empty page", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileUtils.getOutputStream ("pdf/test-table.pdf"));
  }
}
