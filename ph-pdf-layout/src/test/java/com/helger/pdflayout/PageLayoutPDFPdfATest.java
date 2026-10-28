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
package com.helger.pdflayout;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.jspecify.annotations.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.font.roboto.EFontResourceRoboto;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.image.PLImage;
import com.helger.pdflayout.element.link.PLExternalLink;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.pdfa.EPdfAFRelationship;
import com.helger.pdflayout.pdfa.EPdfALevel;
import com.helger.pdflayout.pdfa.PdfAXmpExtensionSchema;
import com.helger.pdflayout.pdfa.PdfAssociatedFile;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for the PDF/A support of class {@link PageLayoutPDF}.
 *
 * @author Philip Helger
 */
public final class PageLayoutPDFPdfATest
{
  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @NonNull
  private static PageLayoutPDF _createLayout (@NonNull final PreloadFont aFont)
  {
    final FontSpec r10 = new FontSpec (aFont, 10);
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (50);
    aPS.addElement (new PLText ("Hello PDF/A - Grüße", r10));
    aPS.addElement (new PLExternalLink (new PLText ("A link", r10)).setURI ("https://github.com/phax/ph-pdf-layout"));

    // An image with an alpha channel
    final BufferedImage aImage = new BufferedImage (20, 20, BufferedImage.TYPE_INT_ARGB);
    for (int x = 0; x < 20; ++x)
      aImage.setRGB (x, x, 0x80ff0000);
    aPS.addElement (new PLImage (aImage));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDocumentTitle ("Title")
                                                          .setDocumentSubject ("Subject")
                                                          .setDocumentKeywords ("k1 k2");
    aPageLayout.addPageSet (aPS);
    return aPageLayout;
  }

  @NonNull
  private static PreloadFont _getEmbeddedFont ()
  {
    return PreloadFont.createEmbedding (EFontResourceRoboto.ROBOTO_NORMAL.getFontResource ());
  }

  @Test
  public void testLegacy () throws Exception
  {
    final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setCreatePDF_A (true);
    assertTrue (aPageLayout.isCreatePDF_A ());
    PdfAValidatorHelper.renderAndAssertValid (aPageLayout, "3a");
  }

  @Test
  public void testUntaggedLevels () throws Exception
  {
    for (final EPdfALevel eLevel : EPdfALevel.values ())
      if (!eLevel.isTagged ())
      {
        final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setPdfALevel (eLevel);
        assertTrue (aPageLayout.isCreatePDF_A ());
        PdfAValidatorHelper.renderAndAssertValid (aPageLayout, eLevel.getID ());
      }
  }

  @Test
  public void testStandardFontRejected ()
  {
    final PageLayoutPDF aPageLayout = _createLayout (PreloadFont.REGULAR).setPdfALevel (EPdfALevel.PDF_A_3B);
    try
    {
      aPageLayout.renderTo (new NonBlockingByteArrayOutputStream ());
      fail ();
    }
    catch (final PDFCreationException ex)
    {
      assertTrue (ex.getMessage (), ex.getMessage ().contains ("Helvetica"));
    }
  }

  @Test
  public void testAssociatedFile () throws Exception
  {
    final byte [] aXml = "<?xml version='1.0'?><root>Grüße</root>".getBytes (StandardCharsets.UTF_8);
    final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setPdfALevel (EPdfALevel.PDF_A_3B)
                                                                         .addAssociatedFile (new PdfAssociatedFile (aXml,
                                                                                                                    "source.xml",
                                                                                                                    "text/xml",
                                                                                                                    "The source",
                                                                                                                    EPdfAFRelationship.SOURCE));
    final byte [] aPdf = PdfAValidatorHelper.renderAndAssertValid (aPageLayout, "3b");

    try (final PDDocument aDoc = Loader.loadPDF (aPdf))
    {
      final PDComplexFileSpecification aFileSpec = aDoc.getDocumentCatalog ()
                                                       .getNames ()
                                                       .getEmbeddedFiles ()
                                                       .getNames ()
                                                       .get ("source.xml");
      assertNotNull (aFileSpec);
      assertEquals ("Source", aFileSpec.getCOSObject ().getNameAsString (COSName.AF_RELATIONSHIP));
      assertArrayEquals (aXml, aFileSpec.getEmbeddedFileUnicode ().toByteArray ());
      final COSArray aAF = aDoc.getDocumentCatalog ().getCOSObject ().getCOSArray (COSName.AF);
      assertNotNull (aAF);
      assertEquals (1, aAF.size ());
    }
  }

  @Test
  public void testAssociatedFileNotAllowedInPdfA2 ()
  {
    final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setPdfALevel (EPdfALevel.PDF_A_2B)
                                                                         .addAssociatedFile (new PdfAssociatedFile (new byte [] { 1 },
                                                                                                                    "a.bin",
                                                                                                                    "application/octet-stream",
                                                                                                                    null,
                                                                                                                    EPdfAFRelationship.DATA));
    try
    {
      aPageLayout.renderTo (new NonBlockingByteArrayOutputStream ());
      fail ();
    }
    catch (final PDFCreationException ex)
    {
      assertTrue (ex.getMessage (), ex.getMessage ().contains ("2b"));
    }
  }

  @Test
  public void testXmpExtensionSchema () throws Exception
  {
    final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setPdfALevel (EPdfALevel.PDF_A_3B)
                                                                         .addXmpExtensionSchema (new PdfAXmpExtensionSchema ("urn:example:test:1p0#",
                                                                                                                             "ext",
                                                                                                                             "Test schema").addTextProperty ("DocumentType",
                                                                                                                                                             "The type of the document",
                                                                                                                                                             "INVOICE")
                                                                                                                                             .addTextProperty ("Version",
                                                                                                                                                               "The version",
                                                                                                                                                               "1.0"));
    final byte [] aPdf = PdfAValidatorHelper.renderAndAssertValid (aPageLayout, "3b");

    try (final PDDocument aDoc = Loader.loadPDF (aPdf))
    {
      final String sXmp = new String (aDoc.getDocumentCatalog ().getMetadata ().toByteArray (),
                                      StandardCharsets.UTF_8);
      assertTrue (sXmp, sXmp.contains ("urn:example:test:1p0#"));
      assertTrue (sXmp, sXmp.contains ("INVOICE"));
      assertTrue (sXmp, sXmp.contains ("pdfaExtension:schemas"));
    }
  }

  @Test
  public void testXmpWithoutExtensionSchemaIsInvalid () throws Exception
  {
    // A custom schema without the PDF/A extension schema description is not PDF/A conformant
    final PageLayoutPDF aPageLayout = _createLayout (_getEmbeddedFont ()).setPdfALevel (EPdfALevel.PDF_A_3B)
                                                                         .setMetadataCustomizer (x -> x.createAndAddDefaultSchema ("ext",
                                                                                                                                   "urn:example:test:1p0#")
                                                                                                       .setTextPropertyValue ("Version",
                                                                                                                              "1.0"));
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    aPageLayout.renderTo (aBAOS);
    assertTrue (PdfAValidatorHelper.validate (aBAOS.toByteArray (), "3b").isNotEmpty ());
  }

  @Test
  public void testLegacyStandardFontNotRejected () throws Exception
  {
    // Compatibility: the legacy API does not check the fonts
    _createLayout (PreloadFont.REGULAR).setCreatePDF_A (true).renderTo (new NonBlockingByteArrayOutputStream ());
  }
}
