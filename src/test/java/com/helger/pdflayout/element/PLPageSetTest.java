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
package com.helger.pdflayout.element;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.element.text.PLTextWithPlaceholders;
import com.helger.pdflayout.render.RenderPageIndex;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

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
    if (false)
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

    aPS1.setPageHeader (new PLText (sHeader +
                                    sHeader +
                                    "last line of header",
                                    r10).setFillColor (Color.PINK).setHorzAlign (EHorzAlignment.RIGHT));
    aPS1.addElement (new PLText ("First body line", r10).setBorder (Color.BLUE));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getOutputStream ("pdf/test-plpageset-header-aligned.pdf"));
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
  public void testWithPlaceholder () throws PDFCreationException
  {
    final String sHeader = "This is a page header that is repeated on every page.";

    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);

    aPS1.setPageHeader (new PLText (sHeader, r10).setBorder (Color.RED));
    aPS1.setPageFooter (new PLTextWithPlaceholders (RenderPageIndex.PLACEHOLDER_PAGESET_INDEX +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_PAGESET_PAGE_INDEX +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_PAGESET_PAGE_NUMBER +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_PAGESET_PAGE_COUNT +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_TOTAL_PAGE_INDEX +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_TOTAL_PAGE_NUMBER +
                                                    "/" +
                                                    RenderPageIndex.PLACEHOLDER_TOTAL_PAGE_COUNT +
                                                    "/" +
                                                    "${custom-var}",
                                                    r10).setFillColor (Color.PINK)
                                                        .setHorzAlign (EHorzAlignment.CENTER));

    for (int i = 0; i < 80; ++i)
      aPS1.addElement (new PLText ("Line " + i, r10));

    aPS1.setRenderingContextCustomizer (aRC -> {
      aRC.setPlaceholder ("${custom-var}", "ph-pdf-layout is cool :)");
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
}
