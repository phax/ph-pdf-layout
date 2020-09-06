package com.helger.pdflayout4.supplementary.issues;

import java.awt.Color;
import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.table.EPLTableGridType;
import com.helger.pdflayout4.element.table.PLTable;
import com.helger.pdflayout4.element.table.PLTableCell;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

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
