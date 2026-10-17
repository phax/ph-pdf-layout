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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Tests rich text inside the rows of a table that has to be split across pages.
 *
 * @author Philip Helger
 */
public final class PLRichTextTableSplitTest
{
  private static IPLRenderableObject <?> _cell (final String sMarkup)
  {
    final PLRichText aRT = PLRichText.createFromMarkup (sMarkup, PLFontFamily.regular (), 10f, PLColor.BLACK);
    aRT.setVertSplittable (true);
    return new PLBox (aRT).setPadding (3);
  }

  private static String _firstPageText (final int nIntroLines) throws IOException, PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    for (int i = 0; i < nIntroLines; ++i)
      aPS.addElement (new PLText ("intro line " + i, new FontSpec (PreloadFont.REGULAR, 12f)));

    final PLTable aTable = PLTable.createWithPercentage (60, 20, 20);
    aTable.setHeaderRowCount (1);
    aTable.addRow (new PLTableCell (_cell ("H1")), new PLTableCell (_cell ("H2")), new PLTableCell (_cell ("H3")));
    for (int i = 0; i < 20; ++i)
      aTable.addRow (new PLTableCell (_cell ("row " + i + "\\\nsecond line")),
                     new PLTableCell (_cell ("2 EA")),
                     new PLTableCell (_cell ("9.95")));
    aPS.addElement (aTable);

    final File aFile = Files.createTempFile ("plrichtext-tablesplit-", ".pdf").toFile ();
    aFile.deleteOnExit ();
    new PageLayoutPDF ().addPageSet (aPS).renderTo (aFile);
    try (final PDDocument aDoc = Loader.loadPDF (aFile))
    {
      final PDFTextStripper aStripper = new PDFTextStripper ();
      aStripper.setStartPage (1);
      aStripper.setEndPage (1);
      return aStripper.getText (aDoc);
    }
  }

  @Test
  public void testTableStartsOnTheCurrentPageWhateverTheRemainingSpace () throws IOException, PDFCreationException
  {
    // Whatever the remaining space on the first page is, the table has to start there. The rows
    // that do not fit move to the next page, but not the whole table.
    for (int nIntro = 20; nIntro <= 40; ++nIntro)
    {
      final String sFirstPage = _firstPageText (nIntro);
      assertTrue ("Table did not start on the first page with " + nIntro + " intro lines", sFirstPage.contains ("row 0"));
    }
  }

  @Test
  public void testHeaderRowIsRepeated () throws IOException, PDFCreationException
  {
    assertEquals (1, _firstPageText (30).split ("H1", -1).length - 1);
  }
}
