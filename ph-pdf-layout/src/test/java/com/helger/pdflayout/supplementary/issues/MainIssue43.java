/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.supplementary.issues;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.string.StringHelper;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.table.EPLTableGridType;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.PreloadFont;

public class MainIssue43
{
  public static void main (final String [] args) throws PDFCreationException
  {
    final var landscapeA4 = new PDRectangle (PDRectangle.A4.getHeight (), PDRectangle.A4.getWidth ());
    final var pageSet = new PLPageSet (landscapeA4).setMargin (40f);

    final var r10 = new FontSpec (PreloadFont.REGULAR, 10f);
    final var r14b = new FontSpec (PreloadFont.REGULAR, 10f);
    final var aPadding = new PaddingSpec (2f);

    final var aTable = PLTable.createWithEvenlySizedColumns (4);
    aTable.setHeaderRowCount (1);
    aTable.setMargin (10f);

    // Add header row
    final var aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("No", r14b).setPadding (aPadding)),
                                                   new PLTableCell (new PLText ("B", r14b).setPadding (aPadding)),
                                                   new PLTableCell (new PLText ("C", r14b).setPadding (aPadding)),
                                                   new PLTableCell (new PLText ("Page Break", r14b).setPadding (
                                                                                                                aPadding)));
    aHeaderRow.setFillColor (PLColor.GRAY);

    final var longString = "1234567890\n" + StringHelper.getRepeated ("pagebreak", 150);
    final var row = new CommonsArrayList <> ("column B", "column C", longString);
    for (int i = 0; i < 10; ++i)
    {
      aTable.addRow (new PLTableCell (new PLText (Integer.toString (i), r10).setPadding (aPadding)
                                                                            .setVertSplittable (false)),
                     new PLTableCell (new PLText (row.get (0), r10).setPadding (aPadding).setVertSplittable (false)),
                     new PLTableCell (new PLText (row.get (1), r10).setPadding (aPadding).setVertSplittable (false)),
                     new PLTableCell (new PLText (row.get (2), r10).setPadding (aPadding).setVertSplittable (false)));
    }

    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (PLColor.BLACK, 1f));
    pageSet.addElement (aTable);

    final var pageLayout = new PageLayoutPDF ();
    pageLayout.addPageSet (pageSet);

    pageLayout.renderTo (new File ("target/issue43.pdf"));
  }
}
