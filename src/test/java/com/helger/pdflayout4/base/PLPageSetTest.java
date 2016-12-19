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
package com.helger.pdflayout4.base;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.element.box.PLBox;
import com.helger.pdflayout4.element.table.PLTable;
import com.helger.pdflayout4.element.table.PLTableCell;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.render.PagePreRenderContext;
import com.helger.pdflayout4.spec.BorderStyleSpec;
import com.helger.pdflayout4.spec.EHorzAlignment;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Test class for {@link PLPageSet}
 *
 * @author Philip Helger
 */
public final class PLPageSetTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  static
  {
    if (true)
      PLDebug.setDebugAll (true);
  }

  @Test
  public void testHeader () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-header.pdf"));
  }

  @Test
  public void testHeaderAligned () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLBox (new PLText (sHeader +
                                               sHeader +
                                               "last line of header",
                                               r10).setFillColor (Color.PINK)).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-header-aligned.pdf"));
  }

  @Test
  public void testHeaderTableAligned () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    final PLTable aTable = new PLTable (new CommonsArrayList <> (WidthSpec.star ()));
    aTable.addRow (new PLTableCell (new PLText (sHeader +
                                                sHeader +
                                                "last line of header",
                                                r10).setFillColor (Color.PINK)).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.setPageHeader (aTable);
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-header-table-aligned.pdf"));
  }

  @Test
  public void testFooter () throws PDFCreationException
  {
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setBorder (Color.RED));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-footer.pdf"));
  }

  @Test
  public void testFooterAligned () throws PDFCreationException
  {
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageFooter (new PLText (sFooter +
                                    sFooter +
                                    "last line of footer",
                                    r10).setFillColor (Color.PINK).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-footer-aligned.pdf"));
  }

  @Test
  public void testBoth () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLText (sHeader +
                                    sHeader +
                                    "last line of header",
                                    r10).setBorder (Color.RED).setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText (sFooter +
                                    sFooter +
                                    "last line of footer",
                                    r10).setFillColor (Color.PINK).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-both.pdf"));
  }

  @Test
  public void testBothStyled () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER)
                                                                                   .setPadding (5)
                                                                                   .setMargin (5)
                                                                                   .setFillColor (Color.YELLOW));
    aPS1.setPageFooter (new PLText (sFooter +
                                    sFooter +
                                    "last line of footer",
                                    r10).setPadding (5)
                                        .setMargin (5)
                                        .setFillColor (Color.PINK)
                                        .setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-both-styled.pdf"));
  }

  @Test
  public void testWithPlaceholder () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLBox (new PLText (PagePreRenderContext.PLACEHOLDER_PAGESET_INDEX +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_PAGESET_PAGE_INDEX +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_PAGESET_PAGE_NUMBER +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_PAGESET_PAGE_COUNT +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_TOTAL_PAGE_INDEX +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_TOTAL_PAGE_NUMBER +
                                               " / " +
                                               PagePreRenderContext.PLACEHOLDER_TOTAL_PAGE_COUNT +
                                               " / ${custom-var}",
                                               r10).setID ("header")
                                                   .setReplacePlaceholder (true)
                                                   .setFillColor (Color.PINK)).setID ("headerbox")
                                                                              .setHorzAlign (EHorzAlignment.CENTER));

    final StringBuilder aText = new StringBuilder ();
    for (int i = 0; i < 80; ++i)
      aText.append ("Line ").append (i).append ('\n');
    aPS1.addElement (new PLText (aText.toString (), r10).setVertSplittable (true));

    aPS1.setPreRenderContextCustomizer (aCtx -> {
      aCtx.addPlaceholder ("${custom-var}", "ph-pdf-layout is cool :)");
    });

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-placeholder.pdf"));
  }

  @Test
  public void testMultiplePages () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLText (sHeader +
                                    sHeader +
                                    "last line of header",
                                    r10).setBorder (Color.RED).setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText (sFooter +
                                    sFooter +
                                    "last line of footer",
                                    r10).setFillColor (Color.PINK).setHorzAlign (EHorzAlignment.RIGHT));

    for (int i = 0; i < 80; ++i)
      aPS1.addElement (new PLText ("Line " + i, r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-multiple-pages.pdf"));
  }

  @Test
  public void testFillColor () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (5)
                                                         .setPadding (5)
                                                         .setBorder (new BorderStyleSpec (5))
                                                         .setFillColor (Color.ORANGE);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10));
    aPS1.addElement (new PLText ("First body line in orange area", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-fillcolor.pdf"));
  }
}
