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
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.text.PLText;
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
  static
  {
    PLDebug.setDebugAll (false);
  }

  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testBasic () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final MarginSpec aMargin = new MarginSpec (5);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("First dummy line", r10));

    // Start table
    final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4)
                                : PLTable.createWithPercentage (10, 40, 25, 25);
    aTable.setHeaderRowCount (1);

    // Add row
    final PLTableRow aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("ID", r14b)
                                                                                                          .setPadding (aPadding)
                                                                                                          .setFillColor (Color.YELLOW)),
                                                                  new PLTableCell (new PLText ("Name",
                                                                                               r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                                  new PLTableCell (new PLText ("Sum1",
                                                                                               r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                                  new PLTableCell (new PLText ("Sum2",
                                                                                               r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)));
    aHeaderRow.setFillColor (Color.GRAY);
    aHeaderRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
    aHeaderRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);

    // Test colspan
    aTable.addRow (new PLTableCell (new PLText ("Colspan 2a", r10), 2),
                           new PLTableCell (new PLText ("Colspan 2b", r10).setFillColor (Color.YELLOW), 2));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 3a", r10), 3),
                           new PLTableCell (new PLText ("Colspan 1b", r10).setFillColor (Color.YELLOW), 1));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 1a", r10), 1),
                           new PLTableCell (new PLText ("Colspan 3b", r10).setFillColor (Color.YELLOW), 3));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 4", r10).setFillColor (Color.YELLOW), 4));

    // Add content lines
    for (int i = 0; i < 184; ++i)
    {
      // Width is determined by the width passed to the table creating method
      final PLTableRow aRow = aTable.addAndReturnRow (new PLTableCell (new PLText (Integer.toString (i), r10)
                                                                                                                     .setPadding (aPadding)
                                                                                                                     .setMargin (aMargin)),
                                                              new PLTableCell (new PLText ("Name " +
                                                                                           i +
                                                                                           (i == 2 ? " this is extra text for row 2 that makes this line longer"
                                                                                                   : ""),
                                                                                           r10.getCloneWithDifferentColor (i %
                                                                                                                           3 == 0 ? Color.RED
                                                                                                                                  : Color.BLACK)).setPadding (aPadding)
                                                                                                                                                 .setMargin (aMargin)),
                                                              new PLTableCell (new PLText (Integer.toString (i * i),
                                                                                           r10).setPadding (aPadding)
                                                                                               .setMargin (aMargin)),
                                                              new PLTableCell (new PLText (Integer.toString (i + i),
                                                                                           r10).setPadding (aPadding)
                                                                                               .setMargin (aMargin)));
      aRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
      aRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
    }
    aTable.setGridType (EPLTableGridType.FULL).setGridBorderStyle (new BorderStyleSpec (Color.PINK, 1));
    aPS1.addElement (aTable);
    aPS1.addElement (new PLText ("Last line", r10));

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
  public void testTableWithStyleVariations () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final MarginSpec aMargin = new MarginSpec (5);
    final PaddingSpec aPadding = new PaddingSpec (2);
    final Color aBGElement = Color.WHITE;
    final Color aBGCell = Color.BLUE;
    final Color aBGRow = Color.RED;
    final Color aBGTable = Color.MAGENTA;
    final BorderSpec aBorder = new BorderSpec (new BorderStyleSpec (1));

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30).setFillColor (aBGTable);

    // Start table
    final int nCols = 4;
    final int nRepeats = 3;

    final PLTable aTable = PLTable.createWithEvenlySizedColumns (nCols);
    // Add header row
    aTable.setHeaderRowCount (1);

    aTable.addRow (createList (nCols,
                                       nIdx -> new PLTableCell (new PLText ("Col " +
                                                                            (nIdx + 1),
                                                                            r14b.getCloneWithDifferentColor (Color.GRAY)).setPadding (aPadding))));

    final ICommonsList <Function <PLTableRow, PLTableRow>> aRowFcts;
    aRowFcts = new CommonsArrayList<> (x -> x, x -> x.setFillColor (aBGRow));

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
    for (final Function <PLTableRow, PLTableRow> aRowFct : aRowFcts)
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
            aRowFct.apply (aTable.addAndReturnRow (createList (nCols,
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
          aTable.addRow (new PLTableCell (new PLSpacerY (5), nCols));
          ++nElementFunc;
        }
        nCellFunc++;
      }
      nRowFunc++;
    }

    aPS1.addElement (aTable);
    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltable-variations.pdf"));
  }

  @Test
  public void testGridTypes () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (final EPLTableGridType eGridType : EPLTableGridType.values ())
    {
      aPS1.addElement (new PLText ("Following is a table with grid type " + eGridType, r10));

      // Start table
      final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4)
                                  : PLTable.createWithPercentage (10, 40, 25, 25);
      aTable.setHeaderRowCount (1);

      // Add row
      final PLTableRow aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("ID", r14b)
                                                                                                            .setPadding (aPadding)
                                                                                                            .setFillColor (Color.YELLOW)),
                                                                    new PLTableCell (new PLText ("Name",
                                                                                                 r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                                    new PLTableCell (new PLText ("Sum1",
                                                                                                 r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                                    new PLTableCell (new PLText ("Sum2",
                                                                                                 r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)));
      aHeaderRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
      aHeaderRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      aHeaderRow.setFillColor (Color.GRAY);

      // Add content lines
      for (int i = 0; i < 10; ++i)
      {
        // Width is determined by the width passed to the table creating method
        final PLTableRow aRow = aTable.addAndReturnRow (new PLTableCell (new PLText (Integer.toString (i),
                                                                                             r10)),
                                                                new PLTableCell (new PLText ("Name " +
                                                                                             i +
                                                                                             (i == 2 ? " this is extra text for row 2 that makes this line longer"
                                                                                                     : ""),
                                                                                             r10.getCloneWithDifferentColor (i %
                                                                                                                             3 == 0 ? Color.RED
                                                                                                                                    : Color.BLACK))),
                                                                new PLTableCell (new PLText (Integer.toString (i * i),
                                                                                             r10)),
                                                                new PLTableCell (new PLText (Integer.toString (i + i),
                                                                                             r10)));
        aRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
        aRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      }
      aTable.setGridType (eGridType).setGridBorderStyle (new BorderStyleSpec (Color.PINK, 3));
      aPS1.addElement (aTable);
      aPS1.addElement (new PLText ("Text after table", r10));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltable-grid-types.pdf"));
  }

  @Nonnull
  private static PLTable _createNestedTable (@Nonnull final FontSpec r10)
  {
    final PLTable aTable = PLTable.createWithEvenlySizedColumns (4);
    // Test colspan
    aTable.addRow (new PLTableCell (new PLText ("Colspan 2a", r10), 2),
                           new PLTableCell (new PLText ("Colspan 2b", r10).setFillColor (Color.YELLOW), 2));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 3a", r10), 3),
                           new PLTableCell (new PLText ("Colspan 1b", r10).setFillColor (Color.YELLOW), 1));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 1a", r10), 1),
                           new PLTableCell (new PLText ("Colspan 3b", r10).setFillColor (Color.YELLOW), 3));
    aTable.addRow (new PLTableCell (new PLText ("Colspan 4", r10).setFillColor (Color.YELLOW), 4));
    aTable.setGridType (EPLTableGridType.FULL).setGridBorderStyle (new BorderStyleSpec (Color.PINK));
    return aTable;
  }

  @Test
  public void testNestedTable () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("First dummy line", r10));

    // Start table
    final PLTable aTable = PLTable.createWithPercentage (10, 20, 30, 40);
    aTable.addRow (new PLTableCell (new PLText ("10%", r10)),
                           new PLTableCell (new PLText ("20%", r10)),
                           new PLTableCell (new PLText ("30%", r10)),
                           new PLTableCell (new PLText ("40%", r10)));
    // Test colspan
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 2),
                           new PLTableCell (_createNestedTable (r10), 2));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 3),
                           new PLTableCell (_createNestedTable (r10), 1));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 1),
                           new PLTableCell (_createNestedTable (r10), 3));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 4));
    aTable.setGridType (EPLTableGridType.FULL);
    aPS1.addElement (aTable);

    // Add content lines
    aPS1.addElement (new PLText ("Last line", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-pltable-nested.pdf"));
  }
}
