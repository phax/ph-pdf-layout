/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.base;

import com.helger.commons.string.StringHelper;
import com.plenigo.pdflayout.PDFCreationException;
import com.plenigo.pdflayout.PLDebugTestRule;
import com.plenigo.pdflayout.PageLayoutPDF;
import com.plenigo.pdflayout.element.box.PLBox;
import com.plenigo.pdflayout.element.table.PLTable;
import com.plenigo.pdflayout.element.table.PLTableCell;
import com.plenigo.pdflayout.element.text.PLText;
import com.plenigo.pdflayout.spec.BorderStyleSpec;
import com.plenigo.pdflayout.spec.EHorzAlignment;
import com.plenigo.pdflayout.spec.FontSpec;
import com.plenigo.pdflayout.spec.PreloadFont;
import com.plenigo.pdflayout.spec.WidthSpec;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.awt.*;
import java.io.File;

/**
 * Test class for {@link PLPageSet}
 *
 * @author Philip Helger
 */
public final class PLPageSetTest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @Test
  public void testHeader () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (100, 50, 30, 20);

    aPS1.setPageHeader (new PLBox (new PLText (sHeader + sHeader + "last line of header",
                                               r10).setBorder (Color.RED)).setBorder (Color.GREEN));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));
    aPS1.addElement (new PLBox (new PLText ("Second body line", r10).setBorder (Color.BLUE)).setBorder (Color.PINK));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/header.pdf"));
  }

  @Test
  public void testHeaderAligned () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLBox (new PLText (sHeader + sHeader + "last line of header",
                                               r10).setFillColor (Color.PINK)).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/header-aligned.pdf"));
  }

  @Test
  public void testHeaderTableAligned () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    final PLTable aTable = new PLTable (WidthSpec.star ());
    aTable.addRow (new PLTableCell (new PLText (sHeader + sHeader + "last line of header",
                                                r10).setFillColor (Color.PINK)).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.setPageHeader (aTable);
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/header-table-aligned.pdf"));
  }

  @Test
  public void testFooter () throws PDFCreationException
  {
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setBorder (Color.RED));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/footer.pdf"));
  }

  @Test
  public void testFooterAligned () throws PDFCreationException
  {
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/footer-aligned.pdf"));
  }

  @Test
  public void testBoth () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/both.pdf"));
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
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setPadding (5)
                                                                                   .setMargin (5)
                                                                                   .setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/both-styled.pdf"));
  }

  @Test
  public void testWithPlaceholder () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (0);

    aPS1.setPageHeader (new PLBox (new PLText (EPLPlaceholder.PAGESET_INDEX.getVariable () +
                                               " / " +
                                               EPLPlaceholder.PAGESET_NUMBER.getVariable () +
                                               " / " +
                                               EPLPlaceholder.PAGESET_COUNT.getVariable () +
                                               " / " +
                                               EPLPlaceholder.PAGESET_PAGE_INDEX.getVariable () +
                                               " / " +
                                               EPLPlaceholder.PAGESET_PAGE_NUMBER.getVariable () +
                                               " / " +
                                               EPLPlaceholder.PAGESET_PAGE_COUNT.getVariable () +
                                               " / " +
                                               EPLPlaceholder.TOTAL_PAGE_INDEX.getVariable () +
                                               " / " +
                                               EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable () +
                                               " / " +
                                               EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable () +
                                               " / ${custom-var}",
                                               r10).setID ("header")
                                                   .setReplacePlaceholder (true)
                                                   .setFillColor (Color.PINK)).setID ("headerbox")
                                                                              .setHorzAlign (EHorzAlignment.CENTER)
                                                                              .setBorder (Color.GREEN));

    final StringBuilder aText = new StringBuilder ();
    for (int i = 0; i < 80; ++i)
      aText.append ("Line ").append (i).append ('\n');
    aPS1.addElement (new PLText (aText.toString (), r10).setVertSplittable (true).setID ("content"));

    aPS1.setPreRenderContextCustomizer (aCtx -> {
      aCtx.addPlaceholder ("${custom-var}", "ph-pdf-layout is cool :)");
    });

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/placeholder.pdf"));
  }

  @Test
  public void testMultiplePages () throws PDFCreationException {
      final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
      final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

      final FontSpec r10 = new FontSpec(PreloadFont.REGULAR, 10);
      final PLPageSet aPS1 = new PLPageSet(PDRectangle.A4).setMargin(30).setWaterMark("T E S T").setFoldMark(true);

      aPS1.setPageHeader(new PLText(sHeader + sHeader + "last line of header", r10).setBorder(Color.RED)
              .setHorzAlign(EHorzAlignment.CENTER));
      aPS1.setPageFooter(new PLText(sFooter + sFooter + "last line of footer", r10).setFillColor(Color.PINK)
              .setHorzAlign(EHorzAlignment.RIGHT));

      for (int i = 0; i < 80; ++i)
          aPS1.addElement(new PLText("Line " + i, r10));

      final PageLayoutPDF aPageLayout = new PageLayoutPDF();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/multiple-pages.pdf"));
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

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/fillcolor.pdf"));
  }

  @Test
  public void testDifferentFirstPageHeaderFooter () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setDifferentFirstPageHeader (true)
                                                         .setDifferentFirstPageFooter (true);

    aPS1.setFirstPageHeader (new PLBox (new PLText ("First page header only", r10)).setBorder (Color.BLUE));
    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setFirstPageFooter (new PLBox (new PLText ("First page footer only", r10)).setBorder (Color.BLUE));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));

    for (int i = 0; i < 1000; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-different.pdf"));
  }

  @Test
  public void testDifferentFirstPageHeader () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30).setDifferentFirstPageHeader (true);

    aPS1.setFirstPageHeader (new PLBox (new PLText ("First page header only", r10)).setBorder (Color.BLUE));
    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));

    for (int i = 0; i < 1000; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-different-header.pdf"));
  }

  @Test
  public void testDifferentFirstPageFooter () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30).setDifferentFirstPageFooter (true);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setFirstPageFooter (new PLBox (new PLText ("First page footer only", r10)).setBorder (Color.BLUE));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));

    for (int i = 0; i < 1000; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-different-footer.pdf"));
  }

  @Test
  public void testNoFirstPageHeaderFooter () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.\nIt can have multiple lines etc.\n";
    final String sFooter = "This is a page footer that is repeated on every page.\nIt can have multiple lines etc.\n";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setDifferentFirstPageHeader (true)
                                                         .setDifferentFirstPageFooter (true);

    aPS1.setPageHeader (new PLText (sHeader + sHeader + "last line of header", r10).setBorder (Color.RED)
                                                                                   .setHorzAlign (EHorzAlignment.CENTER));
    aPS1.setPageFooter (new PLText (sFooter + sFooter + "last line of footer", r10).setFillColor (Color.PINK)
                                                                                   .setHorzAlign (EHorzAlignment.RIGHT));

    for (int i = 0; i < 1000; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-none.pdf"));
  }

  @Test
  public void testOnlyFirstPageHeaderFooter () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setDifferentFirstPageHeader (true)
                                                         .setDifferentFirstPageFooter (true);

    aPS1.setFirstPageHeader (new PLBox (new PLText ("First page header only", r10)).setBorder (Color.BLUE));
    aPS1.setFirstPageFooter (new PLBox (new PLText ("First page footer only", r10)).setBorder (Color.BLUE));

    for (int i = 0; i < 1000; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-only.pdf"));
  }

  @Test
  public void testFirstPageLarger () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setDifferentFirstPageHeader (true)
                                                         .setDifferentFirstPageFooter (true);

    aPS1.setFirstPageHeader (new PLBox (new PLText (StringHelper.getRepeated ("First page header. It is so exiciting. ", 30),
                                                    r10)).setBorder (Color.BLUE));
    aPS1.setPageHeader (new PLBox (new PLText ("Regular page header. Boring.", r10)).setBorder (Color.RED));

    aPS1.setFirstPageFooter (new PLBox (new PLText (StringHelper.getRepeated ("First page footer. It is so exiciting. ", 30),
                                                    r10)).setBorder (Color.BLUE));
    aPS1.setPageFooter (new PLBox (new PLText ("Regular page footer. Boring.", r10)).setBorder (Color.RED));

    for (int i = 0; i < 200; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-larger.pdf"));
  }

  @Test
  public void testFirstPageSmaller () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30)
                                                         .setDifferentFirstPageHeader (true)
                                                         .setDifferentFirstPageFooter (true);

    aPS1.setFirstPageHeader (new PLBox (new PLText ("First page header. Boring.", r10)).setBorder (Color.RED));
    aPS1.setPageHeader (new PLBox (new PLText (StringHelper.getRepeated ("Regular page header. It is so exiciting. ", 30),
                                               r10)).setBorder (Color.BLUE));

    aPS1.setFirstPageFooter (new PLBox (new PLText ("First page footer. Boring.", r10)).setBorder (Color.RED));
    aPS1.setPageFooter (new PLBox (new PLText (StringHelper.getRepeated ("Regular page footer. It is so exiciting. ", 30),
                                               r10)).setBorder (Color.BLUE));

    for (int i = 0; i < 200; ++i)
      aPS1.addElement (new PLText ("Dummy line " + i, r10).setMargin (3, 0));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (new File ("pdf/plpageset/firstpage-smaller.pdf"));
  }
}
