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
package com.helger.pdflayout.element.table;

import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.string.StringHelper;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.debug.PLDebugRender;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.image.PLImage;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.HeightSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Test class for class {@link PLTable}.
 *
 * @author Philip Helger
 */
public final class PLTableTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

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
    final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4) : PLTable.createWithPercentage (10, 40, 25, 25);
    aTable.setHeaderRowCount (1);
    aTable.setMargin (40);

    // Add row
    final PLTableRow aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("ID", r14b).setPadding (aPadding)
                                                                                                  .setFillColor (Color.YELLOW)),
                                                          new PLTableCell (new PLText ("Name", r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                          new PLTableCell (new PLText ("Sum1", r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                          new PLTableCell (new PLText ("Sum2", r14b).setPadding (aPadding)
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
      aTable.addRow (new PLTableCell (new PLText (Integer.toString (i), r10).setPadding (aPadding).setMargin (aMargin)),
                     new PLTableCell (new PLText ("Name " + i + (i == 2 ? " this is extra text for row 2 that makes this line longer" : ""),
                                                  r10.getCloneWithDifferentColor (i % 3 == 0 ? Color.RED : Color.BLACK))
                                                                                                                        .setPadding (aPadding)
                                                                                                                        .setMargin (aMargin)),
                     new PLTableCell (new PLText (Integer.toString (i * i), r10).setPadding (aPadding)
                                                                                .setMargin (aMargin)).setHorzAlign (EHorzAlignment.CENTER),
                     new PLTableCell (new PLText (Integer.toString (i + i), r10).setPadding (aPadding)
                                                                                .setMargin (aMargin)).setHorzAlign (EHorzAlignment.RIGHT));
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.PINK, 1));
    aPS1.addElement (aTable);
    aPS1.addElement (new PLText ("Last line", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/basic.pdf"));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> createList (final int nCount, final IntFunction <T> aSupplier)
  {
    final ICommonsList <T> ret = new CommonsArrayList <> (nCount);
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
                               nIdx -> new PLTableCell (new PLText ("Col " + (nIdx + 1),
                                                                    r14b.getCloneWithDifferentColor (Color.GRAY)).setPadding (aPadding))));

    final ICommonsList <Function <PLTableRow, PLTableRow>> aRowFcts;
    aRowFcts = new CommonsArrayList <> (x -> x, x -> x.setFillColor (aBGRow));

    final ICommonsList <Function <PLTableCell, PLTableCell>> aCellFcts;
    aCellFcts = new CommonsArrayList <> (x -> x,
                                         x -> x.setFillColor (aBGCell),
                                         x -> ((PLText) x.getElement ()).getText ().startsWith ("Cell 2") ? x.setFillColor (aBGCell) : x);

    final ICommonsList <Function <AbstractPLElement <?>, AbstractPLElement <?>>> aElementFcts;
    aElementFcts = new CommonsArrayList <> (x -> x,
                                            x -> x.setFillColor (aBGElement),
                                            x -> x.setBorder (aBorder),
                                            x -> x.setBorder (aBorder).setFillColor (aBGElement),
                                            x -> x.setBorder (aBorder).setPadding (aPadding).setFillColor (aBGElement),
                                            x -> x.setBorder (aBorder).setMargin (aMargin).setFillColor (aBGElement),
                                            x -> x.setBorder (aBorder).setPadding (aPadding).setMargin (aMargin).setFillColor (aBGElement));

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
    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/variations.pdf"));
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
      final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4) : PLTable.createWithPercentage (10, 40, 25, 25);
      aTable.setHeaderRowCount (1);

      // Add row
      final PLTableRow aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("ID", r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Name", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Sum1", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Sum2", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)));
      aHeaderRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
      aHeaderRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      aHeaderRow.setFillColor (Color.GRAY);

      // Add content lines
      for (int i = 0; i < 10; ++i)
      {
        // Width is determined by the width passed to the table creating method
        final PLTableRow aRow = aTable.addAndReturnRow (new PLTableCell (new PLText (Integer.toString (i), r10)),
                                                        new PLTableCell (new PLText ("Name " +
                                                                                     i +
                                                                                     (i == 2 ? " this is extra text for row 2 that makes this line longer"
                                                                                             : ""),
                                                                                     r10.getCloneWithDifferentColor (i %
                                                                                                                     3 == 0 ? Color.RED
                                                                                                                            : Color.BLACK))),
                                                        new PLTableCell (new PLText (Integer.toString (i * i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i + i), r10)));
        aRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
        aRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      }
      eGridType.applyGridToTable (aTable, new BorderStyleSpec (Color.PINK, 3));
      aPS1.addElement (aTable);
      aPS1.addElement (new PLText ("Text after table", r10));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/grid-types.pdf"));
  }

  @Test
  public void testPartialGridTypes () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r14b = new FontSpec (PreloadFont.REGULAR_BOLD, 14);
    final PaddingSpec aPadding = new PaddingSpec (2);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (final EPLTableGridType eGridType : EPLTableGridType.values ())
    {
      aPS1.addElement (new PLText ("Following is a table with grid type " + eGridType, r10));

      // Start table
      final PLTable aTable = PLTable.createWithEvenlySizedColumns (8);
      aTable.setHeaderRowCount (1);

      // Add row
      final PLTableRow aHeaderRow = aTable.addAndReturnRow (new PLTableCell (new PLText ("ID", r14b).setPadding (aPadding)
                                                                                                    .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Name", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Sum1", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("Sum2", r14b).setPadding (aPadding)
                                                                                                      .setFillColor (Color.YELLOW)),
                                                            new PLTableCell (new PLText ("ID", r14b).setPadding (aPadding)),
                                                            new PLTableCell (new PLText ("Name", r14b).setPadding (aPadding)),
                                                            new PLTableCell (new PLText ("Sum1", r14b).setPadding (aPadding)),
                                                            new PLTableCell (new PLText ("Sum2", r14b).setPadding (aPadding)));
      aHeaderRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
      aHeaderRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      aHeaderRow.setFillColor (Color.GRAY);

      // Add content lines
      for (int i = 0; i < 10; ++i)
      {
        // Width is determined by the width passed to the table creating method
        final PLTableRow aRow = aTable.addAndReturnRow (new PLTableCell (new PLText (Integer.toString (i), r10)),
                                                        new PLTableCell (new PLText ("Name " +
                                                                                     i +
                                                                                     (i == 2 ? " this is extra text for row 2 that makes this line longer"
                                                                                             : ""),
                                                                                     r10.getCloneWithDifferentColor (i %
                                                                                                                     3 == 0 ? Color.RED
                                                                                                                            : Color.BLACK))),
                                                        new PLTableCell (new PLText (Integer.toString (i * i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i + i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i * i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i + i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i * i), r10)),
                                                        new PLTableCell (new PLText (Integer.toString (i + i), r10)));
        aRow.getCellAtIndex (2).setHorzAlign (EHorzAlignment.CENTER);
        aRow.getCellAtIndex (3).setHorzAlign (EHorzAlignment.RIGHT);
      }
      eGridType.applyGridToTable (aTable,
                                  new PLCellRange (1, aTable.getRowCount () - 2, 1, aTable.getColumnCount () - 2),
                                  new BorderStyleSpec (Color.PINK, 3));
      aPS1.addElement (aTable);
      aPS1.addElement (new PLText ("Text after table", r10));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/grid-types-partial.pdf"));
  }

  @Test
  public void testGridTypesColspan () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    for (final EPLTableGridType eGridType : EPLTableGridType.values ())
    {
      aPS1.addElement (new PLText ("Following is a table with grid type " + eGridType, r10));

      // Start table
      final PLTable aTable = true ? PLTable.createWithEvenlySizedColumns (4) : PLTable.createWithPercentage (10, 40, 25, 25);
      aTable.setHeaderRowCount (1);

      // Add content lines
      aTable.addRow (new PLTableCell (new PLText ("10%", r10)),
                     new PLTableCell (new PLText ("20%", r10)),
                     new PLTableCell (new PLText ("30%", r10)),
                     new PLTableCell (new PLText ("40%", r10)));
      // Test colspan
      aTable.addRow (new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 2),
                     new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 2));
      aTable.addRow (new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 3),
                     new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 1));
      aTable.addRow (new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 1),
                     new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 3));
      aTable.addRow (new PLTableCell (_createNestedTable (r10, EPLTableGridType.NONE), 4));
      eGridType.applyGridToTable (aTable, new BorderStyleSpec (Color.PINK, 3));
      aPS1.addElement (aTable);
      aPS1.addElement (new PLText ("Text after table", r10));
      aPS1.addElement (new PLPageBreak (false));
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/grid-types-colspan.pdf"));
  }

  @Nonnull
  private static PLTable _createNestedTable (@Nonnull final FontSpec r10)
  {
    return _createNestedTable (r10, EPLTableGridType.FULL);
  }

  @Nonnull
  private static PLTable _createNestedTable (@Nonnull final FontSpec r10, @Nonnull final IPLTableGridType aGT)
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
    aGT.applyGridToTable (aTable, new BorderStyleSpec (Color.PINK));
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
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 2), new PLTableCell (_createNestedTable (r10), 2));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 3), new PLTableCell (_createNestedTable (r10), 1));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 1), new PLTableCell (_createNestedTable (r10), 3));
    aTable.addRow (new PLTableCell (_createNestedTable (r10), 4));
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.BLACK, LineDashPatternSpec.SOLID, 1f));
    aPS1.addElement (aTable);

    // Add content lines
    aPS1.addElement (new PLText ("Last line", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/nested.pdf"));
  }

  @Test
  public void testManyAutoHeightRows () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);
    aPS1.addElement (new PLText ("First dummy line", r10));

    // Start table
    final PLTable aTable = PLTable.createWithPercentage (10, 20, 30, 40);
    for (int i = 0; i < 1000; ++i)
    {
      final ICommonsList <PLTableCell> aRow = new CommonsArrayList <> ();
      for (int c = 0; c < aTable.getColumnCount (); ++c)
        aRow.add (new PLTableCell (new PLText ("Col " + c, r10)).setHorzAlign (EHorzAlignment.CENTER));
      // Setting the padding on the table cell (that's what happens internally)
      // may crash the existing algorithm
      aTable.addAndReturnRow (aRow, HeightSpec.auto ()).setPadding (4);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.BLUE));
    aPS1.addElement (aTable);

    // Add content lines
    aPS1.addElement (new PLText ("Last line", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/many-rows.pdf"));
  }

  @Test
  public void testCellSpawningPage () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    // Start table
    final String sLongText = StringHelper.getRepeated ("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n",
                                                       10);
    final PLTable aTable = PLTable.createWithEvenlySizedColumns (3).setID ("table");
    aTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10).setID ("longtext")).setID ("celllongtext"),
                            new PLTableCell (new PLSpacerX (0).setID ("empty")).setID ("cellempty"),
                            new PLTableCell (new PLText ("Short text", r10).setID ("shorttext")).setID ("cellshorttext"))
          .setID ("row");
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/cell-spawning-page.pdf"));
  }

  @Test
  public void testCellSpawningPage2 () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5\n  Line 6\nLine 7\n  Line 8\nLine 9";

    // Start table
    final PLTable aTable = PLTable.createWithEvenlySizedColumns (3).setID ("table");
    for (int i = 0; i < 12; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10).setID ("longtext").setPadding (2)).setID ("celllongtext"),
                              new PLTableCell (new PLSpacerX (0).setID ("empty")).setID ("cellempty"),
                              new PLTableCell (new PLText ("Short text", r10).setID ("shorttext")).setID ("cellshorttext"))
            .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/cell-spawning-page2.pdf"));
  }

  @Test
  public void testCellSpawningPageInnerTable () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5\n  Line 6\nLine 7\n  Line 8\nLine 9";

    // Start table
    final PLTable aTable = PLTable.createWithPercentage (30, 70).setID ("table");
    for (int i = 0; i < 3; ++i)
    {
      final PLTable aInnerTable = PLTable.createWithPercentage (70, 30).setID ("inner-table-" + i);
      for (int j = 0; j < 20; ++j)
        aInnerTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10).setID ("longtext").setPadding (2)).setID ("celllongtext"),
                                     new PLTableCell (new PLText ("Short text", r10).setID ("shorttext")).setID ("cellshorttext"))
                   .setID ("inner-row-" + i + "-" + j);
      EPLTableGridType.FULL.applyGridToTable (aInnerTable, new BorderStyleSpec (Color.BLUE));
      aTable.addAndReturnRow (new PLTableCell (new PLText ("First column", r10)), new PLTableCell (aInnerTable).setID ("cellshorttext"))
            .setID ("row-" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/cell-spawning-page-inner-table.pdf"));
  }

  @Test
  public void testColSpanRightAlign () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = StringHelper.getRepeated ("This is a dummy text to fill the table cell. Some other real information expected here in practice. ",
                                                       6);

    final String sMediumText = StringHelper.getRepeated ("This is a dummy text to fill the table cell. Some other real information expected here in practice. ",
                                                         2);

    // Start table
    final PLTable aTable = PLTable.createWithPercentage (10, 60, 15, 15).setHeaderRowCount (1).setID ("table");
    aTable.addAndReturnRow (new PLTableCell (new PLText ("Header col 1", r10)),
                            new PLTableCell (new PLText ("Header col 2", r10)),
                            new PLTableCell (new PLText ("Header col 3", r10)),
                            new PLTableCell (new PLText ("Header col 4", r10)))
          .setID ("row-header")
          .setPadding (2)
          .setFillColor (Color.LIGHT_GRAY);
    for (int i = 0; i < 12; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (Integer.toString (i), r10).setID ("idx")).setID ("cell-idx"),
                              new PLTableCell (new PLText (sLongText, r10).setID ("longtext")).setID ("cell-longtext"),
                              new PLTableCell (new PLSpacerX (0).setID ("empty")).setID ("cell-empty"),
                              new PLTableCell (new PLText ("Short text", r10).setID ("shorttext")).setID ("cell-shorttext"))
            .setID ("row-content-" + i)
            .setPadding (2);
      aTable.addAndReturnRow (new PLTableCell (new PLText (sMediumText, r10).setID ("spanned").setHorzAlign (EHorzAlignment.RIGHT),
                                               aTable.getColumnCount ()).setID ("cell-spanned").setHorzAlign (EHorzAlignment.RIGHT))
            .setID ("row-summary-" + i)
            .setPadding (2);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.PINK));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    PLDebugRender.withDebugRender (false, () -> {
      final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
      aPageLayout.addPageSet (aPS1);
      aPageLayout.renderTo (new File ("pdf/pltable/colspan-right-align.pdf"));
    });
  }

  @Test
  public void testDifferentColWidthTypes () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5";

    // Start table
    final PLTable aTable = new PLTable (WidthSpec.abs (100), WidthSpec.star (), WidthSpec.perc (25)).setID ("table");
    aTable.addAndReturnRow (new PLTableCell (new PLText ("100f", r10)),
                            new PLTableCell (new PLText ("star", r10)),
                            new PLTableCell (new PLText ("25%", r10)))
          .setFillColor (Color.GREEN)
          .setID ("headerrow");
    for (int i = 0; i < 12; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)))
            .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/different-width-types.pdf"));
  }

  @Test
  public void testDifferentColWidthTypesNotFullWidth () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5";

    // Start table
    final PLTable aTable = new PLTable (WidthSpec.abs (100), WidthSpec.perc (25), WidthSpec.perc (25)).setID ("table");
    aTable.addAndReturnRow (new PLTableCell (new PLText ("100f", r10)),
                            new PLTableCell (new PLText ("25%", r10)),
                            new PLTableCell (new PLText ("25%", r10)))
          .setFillColor (Color.GREEN)
          .setID ("headerrow");
    for (int i = 0; i < 12; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)))
            .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/different-width-types-not-full-width.pdf"));
  }

  @Test
  public void testDifferentColWidthTypesTooWide () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5";

    // Start table
    final PLTable aTable = new PLTable (WidthSpec.abs (100), WidthSpec.perc (50), WidthSpec.perc (50)).setID ("table");
    aTable.addAndReturnRow (new PLTableCell (new PLText ("100f", r10)),
                            new PLTableCell (new PLText ("50%", r10)),
                            new PLTableCell (new PLText ("50%", r10)))
          .setFillColor (Color.GREEN)
          .setID ("headerrow");
    for (int i = 0; i < 12; ++i)
    {
      aTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)),
                              new PLTableCell (new PLText (sLongText, r10)))
            .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/different-width-types-too-wide.pdf"));
  }

  @Test
  public void testWithImage () throws PDFCreationException, IOException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    // Source image has 33x33 pixels
    final BufferedImage aImg = ImageIO.read (new FileSystemResource ("src/test/resources/images/test1.jpg").getInputStream ());
    assertNotNull (aImg);

    // Start table
    final PLTable aTable = PLTable.createWithEvenlySizedColumns (3);
    for (int i = 0; i < 12; ++i)
    {
      // Scale image
      final PLImage aPLImage = new PLImage (aImg, 20 + i * 3, 20 + i * 3);
      final PLHBox aImageAndText = new PLHBox ();
      aImageAndText.addColumn (new PLTableCell (aPLImage), WidthSpec.abs (aPLImage.getImageWidth () + 5));
      aImageAndText.addColumn (new PLText ("This is a text", r10), WidthSpec.star ());

      aTable.addRow (i % 3 == 0 ? new PLTableCell (aPLImage) : PLTableCell.createEmptyCell (),
                     i % 3 == 1 ? new PLTableCell (aImageAndText) : PLTableCell.createEmptyCell (),
                     i % 3 == 2 ? new PLTableCell (aPLImage) : PLTableCell.createEmptyCell ());
    }
    EPLTableGridType.FULL_NO_BORDER.applyGridToTable (aTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aTable);

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/table-with-images.pdf"));
  }

  @Test
  public void testIssue21 () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5";
    final String sShortText = "Dummy";

    // Outer table
    final PLTable aOuterTable = new PLTable (WidthSpec.perc (25), WidthSpec.star ()).setID ("outer-table");
    for (int i = 0; i < 25; ++i)
    {
      final PLTable aInnerTable = new PLTable (WidthSpec.perc (30), WidthSpec.perc (70));
      for (int j = 0; j < 4; ++j)
        aInnerTable.addAndReturnRow (new PLTableCell (new PLText (sShortText, r10)), new PLTableCell (new PLText (sShortText, r10)));
      EPLTableGridType.FULL.applyGridToTable (aInnerTable, new BorderStyleSpec (Color.GREEN));

      aOuterTable.addAndReturnRow (new PLTableCell (new PLText (sLongText, r10)).setPadding (5), new PLTableCell (aInnerTable))
                 .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aOuterTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aOuterTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/issue21.pdf"));
  }

  @Test
  public void testIssue21_BoxWithPadding () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.addElement (new PLText ("First line", r10).setID ("first-line"));

    final String sLongText = "Line 1\n  Line 2\nLine 3\n  Line 4\nLine 5";
    final String sShortText = "Dummy";

    // Outer table
    final PLTable aOuterTable = new PLTable (WidthSpec.perc (25), WidthSpec.star ()).setID ("outer-table");
    for (int i = 0; i < 25; ++i)
    {
      final PLTable aInnerTable = new PLTable (WidthSpec.perc (30), WidthSpec.perc (70));
      for (int j = 0; j < 4; ++j)
        aInnerTable.addAndReturnRow (new PLTableCell (new PLText (sShortText, r10)), new PLTableCell (new PLText (sShortText, r10)));
      EPLTableGridType.FULL.applyGridToTable (aInnerTable, new BorderStyleSpec (Color.GREEN));

      // Place text in box, and add padding to box instead of the table
      aOuterTable.addAndReturnRow (new PLTableCell (new PLBox (new PLText (sLongText, r10)).setPadding (5)), new PLTableCell (aInnerTable))
                 .setID ("row" + i);
    }
    EPLTableGridType.FULL.applyGridToTable (aOuterTable, new BorderStyleSpec (Color.RED));
    aPS1.addElement (aOuterTable);

    aPS1.addElement (new PLText ("Last line", r10).setID ("last-line"));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/pltable/issue21a.pdf"));
  }
}
