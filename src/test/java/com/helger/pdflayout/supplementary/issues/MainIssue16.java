/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.awt.Color;
import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.table.EPLTableGridType;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

public class MainIssue16
{
  public static void main (final String [] args) throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r13 = new FontSpec (PreloadFont.REGULAR, 13);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final String [] summaryKey = { "Applications Under Test", "Start Time", "End Time", "Duration" };

    // Table1
    final PLTable aTable = PLTable.createWithPercentage (30, 70).setMarginLeft (200).setID ("t1");
    aTable.addAndReturnRow (new PLTableCell (new PLText ("SUMMARY", r13)), new PLTableCell (new PLText ("", r13)))
          .setFillColor (Color.BLUE)
          .setID ("headerrow");
    for (int i = 0; i < 4; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (summaryKey[i] + "\n", r10)), new PLTableCell (new PLText ("Duration", r10)))
            .setID ("row");
    }

    // Table2
    final PLTable aTable2 = PLTable.createWithPercentage (30, 70).setMarginLeft (40).setID ("t2");
    aTable2.addAndReturnRow (new PLTableCell (new PLText ("RESOURCE", r13)), new PLTableCell (new PLText ("", r13)))
           .setFillColor (Color.BLUE)
           .setID ("headerrow");
    for (int i = 0; i < 4; ++i)
    {
      aTable2.addAndReturnRow (new PLTableCell (new PLText (summaryKey[i] + "\n", r10)), new PLTableCell (new PLText ("Duration", r10)))
             .setID ("row");
    }

    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.LIGHT_GRAY));
    EPLTableGridType.FULL.applyGridToTable (aTable2, new BorderStyleSpec (Color.LIGHT_GRAY));

    aPS1.addElement (aTable);
    aTable2.setVertSplittable (true);
    aPS1.addElement (aTable2);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (true);
    aPageLayout.addPageSet (aPS1);

    aPageLayout.renderTo (new File ("target/issue16.pdf"));
  }

}
