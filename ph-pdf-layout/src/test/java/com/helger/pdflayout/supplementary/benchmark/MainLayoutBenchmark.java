/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.supplementary.benchmark;

import java.io.OutputStream;
import java.util.Locale;
import java.util.function.Supplier;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.NullOutputStream;
import com.helger.font.open_sans.EFontResourceOpenSans;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.table.EPLTableGridType;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Scenario benchmark for the complete layout pipeline (build, prepare, render, save). Pass a
 * scenario name as first argument to run only that one, and optionally the number of measured
 * rounds as second argument and a scale factor for the content size as third argument.
 *
 * @author Philip Helger
 */
public final class MainLayoutBenchmark
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainLayoutBenchmark.class);

  private static final String LOREM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. ";
  private static final int WARMUP_ROUNDS = 3;

  private static int s_nScale = 1;

  private static final PreloadFont OPEN_SANS = PreloadFont.createEmbedding (EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ());

  private MainLayoutBenchmark ()
  {}

  private static String _repeat (final int nCount)
  {
    final StringBuilder aSB = new StringBuilder (LOREM.length () * nCount);
    for (int i = 0; i < nCount; ++i)
      aSB.append (LOREM);
    return aSB.toString ();
  }

  private static PageLayoutPDF _wrap (final PLPageSet aPS)
  {
    final PageLayoutPDF ret = new PageLayoutPDF ();
    ret.addPageSet (aPS);
    return ret;
  }

  private static PageLayoutPDF _createLongText (final PreloadFont aFont)
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (30);
    aPS.addElement (new PLText (_repeat (400 * s_nScale), new FontSpec (aFont, 10)));
    return _wrap (aPS);
  }

  private static PageLayoutPDF _createParagraphs ()
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (30);
    for (int i = 0; i < 2000 * s_nScale; ++i)
      aPS.addElement (new PLText ("Paragraph " + i + ": " + LOREM, r10).setMarginBottom (4));
    return _wrap (aPS);
  }

  private static PageLayoutPDF _createTable ()
  {
    final FontSpec r9 = new FontSpec (PreloadFont.REGULAR, 9);
    final FontSpec b9 = new FontSpec (PreloadFont.REGULAR_BOLD, 9);
    final PLTable aTable = PLTable.createWithPercentage (10, 40, 15, 15, 20);
    aTable.setHeaderRowCount (1);
    aTable.addRow (new PLTableCell (new PLText ("No", b9).setPadding (2)),
                   new PLTableCell (new PLText ("Description", b9).setPadding (2)),
                   new PLTableCell (new PLText ("Qty", b9).setPadding (2)),
                   new PLTableCell (new PLText ("Price", b9).setPadding (2)),
                   new PLTableCell (new PLText ("Total", b9).setPadding (2)));
    for (int i = 0; i < 3000 * s_nScale; ++i)
      aTable.addRow (new PLTableCell (new PLText (Integer.toString (i), r9).setPadding (2)),
                     new PLTableCell (new PLText ("Article " + i + " with a somewhat longer description that wraps",
                                                  r9).setPadding (2)),
                     new PLTableCell (new PLText (Integer.toString (i % 17), r9).setPadding (2)),
                     new PLTableCell (new PLText ((i % 100) + ".99", r9).setPadding (2)),
                     new PLTableCell (new PLText ((i % 100) * (i % 17) + ".00", r9).setPadding (2)));
    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (PLColor.BLACK));
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (30);
    aPS.addElement (aTable);
    return _wrap (aPS);
  }

  private static PageLayoutPDF _createNested ()
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (30);
    for (int i = 0; i < 600 * s_nScale; ++i)
    {
      final PLHBox aHBox = new PLHBox ();
      for (int c = 0; c < 3; ++c)
      {
        final PLVBox aVBox = new PLVBox ();
        aVBox.addRow (new PLText ("Block " + i + "/" + c, r10));
        aVBox.addRow (new PLText (LOREM.substring (0, 80 + c * 20), r10));
        aVBox.addRow (new PLText ("Footer " + c, r10).setFillColor (PLColor.LIGHT_GRAY));
        aHBox.addColumn (new PLBox (aVBox).setPadding (3).setBorder (new BorderSpec (new BorderStyleSpec (PLColor.GRAY))),
                         WidthSpec.star ());
      }
      aPS.addElement (new PLBox (aHBox).setMarginBottom (5));
    }
    return _wrap (aPS);
  }

  private static void _run (final String sName, final Supplier <PageLayoutPDF> aFactory, final int nRounds)
    throws Exception
  {
    for (int i = 0; i < WARMUP_ROUNDS; ++i)
      try (final OutputStream aOS = new NullOutputStream ())
      {
        aFactory.get ().renderTo (aOS);
      }

    long nBuildTotal = 0;
    long nRenderTotal = 0;
    long nBest = Long.MAX_VALUE;
    for (int i = 0; i < nRounds; ++i)
    {
      final long nStart = System.nanoTime ();
      final PageLayoutPDF aLayout = aFactory.get ();
      final long nBuilt = System.nanoTime ();
      try (final OutputStream aOS = new NullOutputStream ())
      {
        aLayout.renderTo (aOS);
      }
      final long nEnd = System.nanoTime ();
      nBuildTotal += nBuilt - nStart;
      nRenderTotal += nEnd - nBuilt;
      nBest = Math.min (nBest, nEnd - nStart);
    }
    LOGGER.info (String.format (Locale.ROOT,
                                "%-12s build %8.2f ms  render %8.2f ms  best total %8.2f ms",
                                sName,
                                Double.valueOf (nBuildTotal / 1e6 / nRounds),
                                Double.valueOf (nRenderTotal / 1e6 / nRounds),
                                Double.valueOf (nBest / 1e6)));
  }

  public static void main (final String [] args) throws Exception
  {
    final String sOnly = args.length > 0 ? args[0] : null;
    final int nRounds = args.length > 1 ? Integer.parseInt (args[1]) : 10;
    if (args.length > 2)
      s_nScale = Integer.parseInt (args[2]);

    if (sOnly == null || sOnly.equals ("text-std"))
      _run ("text-std", () -> _createLongText (PreloadFont.REGULAR), nRounds);
    if (sOnly == null || sOnly.equals ("text-ttf"))
      _run ("text-ttf", () -> _createLongText (OPEN_SANS), nRounds);
    if (sOnly == null || sOnly.equals ("paragraphs"))
      _run ("paragraphs", MainLayoutBenchmark::_createParagraphs, nRounds);
    if (sOnly == null || sOnly.equals ("table"))
      _run ("table", MainLayoutBenchmark::_createTable, nRounds);
    if (sOnly == null || sOnly.equals ("nested"))
      _run ("nested", MainLayoutBenchmark::_createNested, nRounds);
  }
}
