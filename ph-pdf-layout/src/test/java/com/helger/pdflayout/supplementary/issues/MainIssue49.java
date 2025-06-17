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

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.debug.PLDebugRender;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.table.PLTableRow;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test for issue 49
 *
 * @author Jeremy Kwiatkowski
 * @author Philip Helger
 */
public final class MainIssue49
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainIssue49.class);

  @Nonnull
  private static PLTable _buildTable ()
  {
    final FontSpec font = new FontSpec (PreloadFont.REGULAR, 8);

    final PLTable table = PLTable.createWithEvenlySizedColumns (2);
    table.setID ("tab1");

    table.setHeaderRowCount (1);
    final PLTableCell column1Heading = new PLTableCell (new PLText ("Header Column1", font));
    final PLTableCell column2Heading = new PLTableCell (new PLText ("Header Column2", font));
    final PLTableRow headerRow = table.addAndReturnRow (column1Heading, column2Heading);
    headerRow.setID ("header");

    // Some padding "somewhere" seems necessary to cause the issue.
    final float fPadding = 6.0f;
    final PLTableCell row1Column1 = new PLTableCell (new PLText ("Text in Row1 Column1", font).setPadding (fPadding))
                                                                                                                     .setID ("row1Column1");

    final PLVBox aVBox = new PLVBox ().setID ("inner-r1-c2");
    // Will display properly with same padding, but not with 0
    // float fPadding2 = fPadding;
    final float fPadding2 = 1f;
    aVBox.addRow (new PLText ("test1 test1 test1 test1 \n\n test1 test1", font).setPadding (fPadding2));
    final PLTableCell row1Column2 = new PLTableCell (aVBox).setID ("row1Column2");

    final PLTableRow dataRow = table.addAndReturnRow (row1Column1, row1Column2);
    dataRow.setID ("data");

    return table;
  }

  public static void main (final String [] args)
  {
    if (false)
    {
      PLDebugRender.setDebugRender (true);
      PLDebugLog.setDebugSplit (true);
    }

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.LETTER).setMargin (30);
    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);

    // Put something on the first page that consumes "most" of the available vertical space
    final PLSpacerY spacer = new PLSpacerY (705f);
    aPS1.addElement (spacer);

    aPS1.addElement (_buildTable ());

    final String outFileString = "target/issue49.pdf";
    final File outFile = new File (outFileString);
    try
    {
      aPageLayout.renderTo (outFile);
      LOGGER.info ("Done, file written to: " + outFile.getAbsolutePath ());
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error", ex);
    }
  }
}
