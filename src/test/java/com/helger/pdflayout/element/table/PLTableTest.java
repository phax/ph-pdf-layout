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
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for class {@link PLTable}.
 *
 * @author Philip Helger
 */
public final class PLTableTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final MarginSpec aMargin = new MarginSpec (5);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setPadding (0, 20, 0, 10)
                                                         .setFillColor (new Color (0xddffff));
    aPS1.setPageHeader (new PLText ("Headline", r10).setBorder (new BorderStyleSpec (Color.BLACK))
                                                    .setPadding (4, 0)
                                                    .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText ("Page " +
                                    PagePreRenderContext.PLACEHOLDER_PAGESET_PAGE_NUMBER +
                                    "/" +
                                    PagePreRenderContext.PLACEHOLDER_TOTAL_PAGE_COUNT,
                                    r10).setReplacePlaceholder (true)
                                        .setBorder (new BorderStyleSpec (Color.RED))
                                        .setPadding (4, 0)
                                        .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.addElement (new PLText ("Erste Dummy Zeile", r10));

    // Start table
    final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4)
                                : PLTable.createWithPercentage (10, 40, 25, 25);
    aTable.setHeaderRowCount (1);

    // Add row
    aTable.addTableRow (new PLText ("ID", r14b).setPadding (aPadding),
                        new PLText ("Name", r14b).setPadding (aPadding),
                        new PLText ("Sum1", r14b).setPadding (aPadding).setHorzAlign (EHorzAlignment.CENTER),
                        new PLText ("Sum2", r14b).setPadding (aPadding).setHorzAlign (EHorzAlignment.RIGHT));

    // Test colspan
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 2a", r10), 2),
                           new PLTableCell (new PLText ("Colspan 2b", r10), 2));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 3a", r10), 3),
                           new PLTableCell (new PLText ("Colspan 1b", r10), 1));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 1a", r10), 1),
                           new PLTableCell (new PLText ("Colspan 3b", r10), 3));
    aTable.addTableRowExt (new PLTableCell (new PLText ("Colspan 4", r10), 4));

    // Add content lines
    for (int i = 0; i < 184; ++i)
    {
      // Width is determined by the width passed to the table creating method
      aTable.addTableRow (new PLText (Integer.toString (i), r10).setPadding (aPadding).setMargin (aMargin),
                          new PLText ("Name " +
                                      i +
                                      (i == 2 ? " this is extra text for row 2 that makes this line longer" : ""),
                                      r10.getCloneWithDifferentColor (i % 3 == 0 ? Color.RED
                                                                                 : Color.BLACK)).setPadding (aPadding)
                                                                                                .setMargin (aMargin),
                          new PLText (Integer.toString (i * i), r10).setPadding (aPadding)
                                                                    .setMargin (aMargin)
                                                                    .setHorzAlign (EHorzAlignment.CENTER),
                          new PLText (Integer.toString (i + i), r10).setPadding (aPadding)
                                                                    .setMargin (aMargin)
                                                                    .setHorzAlign (EHorzAlignment.RIGHT));
    }
    aPS1.addElement (aTable);

    // Start a new page
    aPS1.addElement (new PLPageBreak (false));
    aPS1.addElement (new PLText ("First line on bottom of new page", r10));
    // Next page
    aPS1.addElement (new PLPageBreak (false));
    // empty page by using forced page break
    aPS1.addElement (new PLPageBreak (true));
    aPS1.addElement (new PLText ("First line on top of last page after one empty page", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltable.pdf"));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> createList (final int nCount, final IntFunction <T> aSupplier)
  {
    final ICommonsList <T> ret = new CommonsArrayList<> (nCount);
    for (int i = 0; i < nCount; ++i)
      ret.add (aSupplier.apply (i));
    return ret;
  }

  @Test
  public void testGrid () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final MarginSpec aMargin = new MarginSpec (5);
    final PaddingSpec aPadding = new PaddingSpec (2);
    final Color aBGElement = Color.WHITE;
    final Color aBGCell = Color.BLUE;
    final Color aBGTable = Color.MAGENTA;
    final BorderSpec aBorder = new BorderSpec (new BorderStyleSpec (1));

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30).setFillColor (aBGTable);

    // Start table
    final int nCols = 4;
    final int nRepeats = 3;

    final PLTable aTable = PLTable.createWithEvenlySizedColumns (nCols);

    // Add header row
    aTable.setHeaderRowCount (1);
    aTable.addTableRow (createList (nCols,
                                    nIdx -> new PLText ("Col " +
                                                        (nIdx + 1),
                                                        r14b.getCloneWithDifferentColor (Color.GRAY)).setPadding (aPadding)));

    final ICommonsList <Function <PLHBox, PLHBox>> aRowFcts;
    aRowFcts = new CommonsArrayList<> (x -> x);

    final ICommonsList <Function <PLTableCell, PLTableCell>> aCellFcts;
    aCellFcts = new CommonsArrayList<> (x -> x,
                                        x -> x.setFillColor (aBGCell),
                                        x -> ((PLText) x.getElement ()).getText ().startsWith ("Cell 2")
                                                                                                         ? x.setFillColor (aBGCell)
                                                                                                         : x);

    final ICommonsList <Function <AbstractPLElement <?>, AbstractPLElement <?>>> aElementFcts;
    aElementFcts = new CommonsArrayList<> (x -> x,
                                           x -> x.setFillColor (aBGElement),
                                           x -> x.setBorder (aBorder),
                                           x -> x.setBorder (aBorder).setFillColor (aBGElement),
                                           x -> x.setBorder (aBorder).setPadding (aPadding).setFillColor (aBGElement),
                                           x -> x.setBorder (aBorder).setMargin (aMargin).setFillColor (aBGElement),
                                           x -> x.setBorder (aBorder)
                                                 .setPadding (aPadding)
                                                 .setMargin (aMargin)
                                                 .setFillColor (aBGElement));

    int nRowFunc = 0;
    for (final Function <PLHBox, PLHBox> aRowFct : aRowFcts)
    {
      final int nCurRowFunc = nRowFunc;
      int nCellFunc = 0;
      for (final Function <PLTableCell, PLTableCell> aCellFct : aCellFcts)
      {
        final int nCurCellFunc = nCellFunc;
        int nElementFunc = 0;
        for (final Function <AbstractPLElement <?>, AbstractPLElement <?>> aElementFct : aElementFcts)
        {
          final int nCurElementFunc = nElementFunc;
          for (int i = 0; i < nRepeats; ++i)
            aRowFct.apply (aTable.addTableRowExt (createList (nCols,
                                                              nIdx -> aCellFct.apply (new PLTableCell (aElementFct.apply (new PLText ("Cell " +
                                                                                                                                      (nIdx +
                                                                                                                                       1) +
                                                                                                                                      " @ " +
                                                                                                                                      nCurRowFunc +
                                                                                                                                      "/" +
                                                                                                                                      nCurCellFunc +
                                                                                                                                      "/" +
                                                                                                                                      nCurElementFunc,
                                                                                                                                      r10)))))));
          aTable.addTableRowExt (new PLTableCell (new PLSpacerY (5), nCols));
          ++nElementFunc;
        }
        nCellFunc++;
      }
      nRowFunc++;
    }

    aPS1.addElement (aTable);
    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltable-grid.pdf"));
  }
}
