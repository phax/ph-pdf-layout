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
package com.helger.pdflayout.tagging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.jspecify.annotations.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.font.roboto.EFontResourceRoboto;
import com.helger.io.file.SimpleFileIO;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.PdfAValidatorHelper;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.image.PLImage;
import com.helger.pdflayout.element.link.PLExternalLink;
import com.helger.pdflayout.element.list.BulletPointCreatorConstant;
import com.helger.pdflayout.element.list.PLBulletPointList;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.pdfa.EPdfALevel;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;
import com.helger.pdflayout.watermark.PLTextWatermark;

/**
 * Test class for tagged PDFs created by {@link com.helger.pdflayout.PageLayoutPDF} with
 * {@link PLTaggingContext}.
 *
 * @author Philip Helger
 */
public final class PLTaggedPDFTest
{
  private static final int TABLE_BODY_ROWS = 80;

  @Rule
  public final TestRule m_aRule = new PLDebugTestRule ();

  @NonNull
  private static PageLayoutPDF _createLayout ()
  {
    final PreloadFont aFont = PreloadFont.createEmbedding (EFontResourceRoboto.ROBOTO_NORMAL.getFontResource ());
    final PreloadFont aFontBold = PreloadFont.createEmbedding (EFontResourceRoboto.ROBOTO_BOLD.getFontResource ());
    final FontSpec r10 = new FontSpec (aFont, 10);
    final FontSpec b16 = new FontSpec (aFontBold, 16);

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (50, 40);
    aPS.setPageHeader (new PLText ("Page header", r10));
    aPS.setPageFooter (new PLText ("Page footer", r10));

    aPS.addElement (new PLText ("Heading", b16).setStructureRole (EPLStructureRole.H1));
    aPS.addElement (new PLText ("A first paragraph.", r10));
    aPS.addElement (new PLExternalLink (new PLText ("A link", r10)).setURI ("https://github.com/phax/ph-pdf-layout"));

    final BufferedImage aImage = new BufferedImage (20, 20, BufferedImage.TYPE_INT_RGB);
    aPS.addElement (new PLImage (aImage, 20, 20).setAlternateText ("A black square"));

    final PLBulletPointList aList = new PLBulletPointList (WidthSpec.abs (15),
                                                           new BulletPointCreatorConstant ("•", r10));
    aList.addBulletPoint (new PLText ("First item", r10));
    aList.addBulletPoint (new PLText ("Second item", r10));
    aPS.addElement (aList);

    // A table over several pages with a repeated header row
    final PLTable aTable = new PLTable (WidthSpec.perc (50), WidthSpec.perc (50));
    aTable.setHeaderRowCount (1);
    aTable.addRow (new PLTableCell (new PLText ("Name", r10)), new PLTableCell (new PLText ("Value", r10)));
    for (int i = 0; i < TABLE_BODY_ROWS; ++i)
      aTable.addRow (new PLTableCell (new PLText ("Row " + i, r10)), new PLTableCell (new PLText ("Value " + i, r10)));
    aPS.addElement (aTable);

    aPS.addElement (new PLText ("The end.", r10));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDocumentTitle ("Tagged PDF test");
    aPageLayout.addPageSet (aPS);
    aPageLayout.addWatermark (new PLTextWatermark ("DRAFT", aFontBold));
    return aPageLayout;
  }

  private static void _collectRecursive (@NonNull final PDStructureNode aNode,
                                         @NonNull final ICommonsMap <String, ICommonsList <PDStructureElement>> aByType)
  {
    for (final Object aKid : aNode.getKids ())
      if (aKid instanceof final PDStructureElement aElement)
      {
        aByType.computeIfAbsent (aElement.getStructureType (), k -> new CommonsArrayList <> ()).add (aElement);
        _collectRecursive (aElement, aByType);
      }
  }

  @Test
  public void testStructure () throws Exception
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    _createLayout ().setCreateTaggedPDF (true).renderTo (aBAOS);
    final byte [] aPdf = aBAOS.toByteArray ();
    SimpleFileIO.writeFile (new File ("pdf/tagging/structure.pdf"), aPdf);

    try (final PDDocument aDoc = Loader.loadPDF (aPdf))
    {
      assertTrue (aDoc.getNumberOfPages () > 1);
      assertTrue (aDoc.getDocumentCatalog ().getMarkInfo ().isMarked ());
      final PDStructureTreeRoot aRoot = aDoc.getDocumentCatalog ().getStructureTreeRoot ();
      assertNotNull (aRoot);
      assertNotNull (aRoot.getParentTree ());

      final ICommonsMap <String, ICommonsList <PDStructureElement>> aByType = new CommonsHashMap <> ();
      _collectRecursive (aRoot, aByType);

      assertEquals (1, aByType.get ("Document").size ());
      assertEquals (1, aByType.get ("H1").size ());
      // The page header and footer are artifacts and therefore not counted
      assertEquals (aByType.toString (), 1, aByType.get ("Table").size ());
      // The header row is only once in the structure, although it is repeated on every page
      assertEquals (1 + TABLE_BODY_ROWS, aByType.get ("TR").size ());
      assertEquals (2, aByType.get ("TH").size ());
      assertEquals (2 * TABLE_BODY_ROWS, aByType.get ("TD").size ());
      assertEquals (1, aByType.get ("L").size ());
      assertEquals (2, aByType.get ("LI").size ());
      assertEquals (2, aByType.get ("Lbl").size ());
      assertEquals (2, aByType.get ("LBody").size ());

      final ICommonsList <PDStructureElement> aFigures = aByType.get ("Figure");
      assertEquals (1, aFigures.size ());
      assertEquals ("A black square", aFigures.getFirstOrNull ().getAlternateDescription ());

      final ICommonsList <PDStructureElement> aLinks = aByType.get ("Link");
      assertEquals (1, aLinks.size ());
      assertTrue (aLinks.getFirstOrNull ().getKids ().stream ().anyMatch (PDObjectReference.class::isInstance));
    }
  }

  @Test
  public void testPdfA3a () throws Exception
  {
    final PageLayoutPDF aPageLayout = _createLayout ().setPdfALevel (EPdfALevel.PDF_A_3A);
    final byte [] aPdf = PdfAValidatorHelper.renderAndAssertValid (aPageLayout, "3a");
    SimpleFileIO.writeFile (new File ("pdf/tagging/pdfa-3a.pdf"), aPdf);
  }

  @Test
  public void testPdfUA1 () throws Exception
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    _createLayout ().setCreateTaggedPDF (true).renderTo (aBAOS);
    final ICommonsList <String> aErrors = PdfAValidatorHelper.validate (aBAOS.toByteArray (), PDFAFlavour.PDFUA_1);
    // All content is tagged or an artifact. The only remaining PDF/UA failure is the missing XMP
    // metadata (7.1-8), because no PDF/UA identification is written.
    aErrors.removeIf (x -> x.startsWith ("7.1-8:"));
    assertTrue (String.join ("\n", aErrors), aErrors.isEmpty ());
  }

  @Test
  public void testUntaggedHasNoStructure () throws PDFCreationException, Exception
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    _createLayout ().renderTo (aBAOS);
    try (final PDDocument aDoc = Loader.loadPDF (aBAOS.toByteArray ()))
    {
      assertEquals (null, aDoc.getDocumentCatalog ().getStructureTreeRoot ());
    }
  }
}
