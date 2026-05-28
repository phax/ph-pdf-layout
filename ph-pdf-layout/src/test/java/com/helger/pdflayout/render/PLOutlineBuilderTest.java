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
package com.helger.pdflayout.render;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.junit.Rule;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for {@link PLOutlineBuilder}.
 *
 * @author Philip Helger
 */
public final class PLOutlineBuilderTest
{
  @Rule
  public final PLDebugTestRule m_aRule = new PLDebugTestRule ();

  private static final FontSpec FONT = new FontSpec (PreloadFont.REGULAR, 12);

  private static byte [] _renderToBytes (final PageLayoutPDF aLayout) throws PDFCreationException
  {
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      aLayout.renderTo (aOS);
      return aOS.toByteArray ();
    }
  }

  @Test
  public void testBasicFlatOutline () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Chapter 1", FONT).setID ("ch1"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLText ("Chapter 2", FONT).setID ("ch2"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLText ("Chapter 3", FONT).setID ("ch3"));

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    aOutline.addEntry ("Chapter 1", "ch1");
    aOutline.addEntry ("Chapter 2", "ch2");
    aOutline.addEntry ("Chapter 3", "ch3");

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final PDDocumentOutline aDocOutline = aReadDoc.getDocumentCatalog ().getDocumentOutline ();
      assertNotNull ("Document outline must exist", aDocOutline);

      final Iterator <PDOutlineItem> aChildren = aDocOutline.children ().iterator ();
      final PDOutlineItem aItem1 = aChildren.next ();
      final PDOutlineItem aItem2 = aChildren.next ();
      final PDOutlineItem aItem3 = aChildren.next ();
      assertNull ("Only three top-level entries expected",
                  aChildren.hasNext () ? aChildren.next ()
                                       : null);

      assertEquals ("Chapter 1", aItem1.getTitle ());
      assertEquals ("Chapter 2", aItem2.getTitle ());
      assertEquals ("Chapter 3", aItem3.getTitle ());

      assertEquals (0, ((PDPageDestination) aItem1.getDestination ()).retrievePageNumber ());
      assertEquals (1, ((PDPageDestination) aItem2.getDestination ()).retrievePageNumber ());
      assertEquals (2, ((PDPageDestination) aItem3.getDestination ()).retrievePageNumber ());
    }
  }

  @Test
  public void testNestedOutlineTree () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Chapter 1", FONT).setID ("ch1"));
    aPS.addElement (new PLText ("Section 1.1", FONT).setID ("sec11"));
    aPS.addElement (new PLText ("Section 1.2", FONT).setID ("sec12"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLText ("Chapter 2", FONT).setID ("ch2"));

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    final PLOutlineBuilder.Entry aCh1 = aOutline.addEntry ("Chapter 1", "ch1");
    aCh1.addChild ("Section 1.1", "sec11");
    aCh1.addChild ("Section 1.2", "sec12");
    aOutline.addEntry ("Chapter 2", "ch2");

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final PDDocumentOutline aDocOutline = aReadDoc.getDocumentCatalog ().getDocumentOutline ();
      final Iterator <PDOutlineItem> aRoots = aDocOutline.children ().iterator ();

      final PDOutlineItem aCh1Item = aRoots.next ();
      assertEquals ("Chapter 1", aCh1Item.getTitle ());
      assertTrue ("Chapter 1 should have children", aCh1Item.hasChildren ());

      final Iterator <PDOutlineItem> aSecs = aCh1Item.children ().iterator ();
      assertEquals ("Section 1.1", aSecs.next ().getTitle ());
      assertEquals ("Section 1.2", aSecs.next ().getTitle ());

      final PDOutlineItem aCh2Item = aRoots.next ();
      assertEquals ("Chapter 2", aCh2Item.getTitle ());
      assertEquals (1, ((PDPageDestination) aCh2Item.getDestination ()).retrievePageNumber ());
    }
  }

  @Test
  public void testGroupingEntryWithoutDestination () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Body", FONT).setID ("body"));

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    final PLOutlineBuilder.Entry aPart = aOutline.addEntry ("Part I");
    aPart.addChild ("Body", "body");

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final PDDocumentOutline aDocOutline = aReadDoc.getDocumentCatalog ().getDocumentOutline ();
      final PDOutlineItem aPartItem = aDocOutline.getFirstChild ();
      assertEquals ("Part I", aPartItem.getTitle ());
      // Grouping node has no destination
      final PDDestination aDest = aPartItem.getDestination ();
      assertNull ("Grouping entry must not have a destination", aDest);
      // But it does have its child
      assertEquals ("Body", aPartItem.getFirstChild ().getTitle ());
    }
  }

  @Test
  public void testMissingElementSkippedGracefully () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Body", FONT).setID ("body"));

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    aOutline.addEntry ("Body", "body");
    aOutline.addEntry ("Phantom", "phantom"); // never rendered

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final PDDocumentOutline aDocOutline = aReadDoc.getDocumentCatalog ().getDocumentOutline ();
      final Iterator <PDOutlineItem> aChildren = aDocOutline.children ().iterator ();
      assertEquals ("Body", aChildren.next ().getTitle ());
      // The phantom entry is still emitted (visible as a header) but without a destination.
      final PDOutlineItem aPhantom = aChildren.next ();
      assertEquals ("Phantom", aPhantom.getTitle ());
      assertNull (aPhantom.getDestination ());
    }
  }

  @Test
  public void testEmptyBuilderDoesNotEmitOutline () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Just content", FONT).setID ("body"));

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    // No entries added.

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      assertNull ("Empty builder must not create an outline", aReadDoc.getDocumentCatalog ().getDocumentOutline ());
    }
  }

  @Test
  public void testAddEntryByElementReference () throws PDFCreationException, IOException
  {
    final PLText aChapterOne = new PLText ("Chapter 1", FONT).setID ("ch1");

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (aChapterOne);

    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    aOutline.addEntry ("Chapter 1", aChapterOne);

    aPS.setRenderListener (aOutline);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS).setDocumentCustomizer (aOutline);

    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final PDOutlineItem aItem = aReadDoc.getDocumentCatalog ().getDocumentOutline ().getFirstChild ();
      assertEquals ("Chapter 1", aItem.getTitle ());
      assertEquals (0, ((PDPageDestination) aItem.getDestination ()).retrievePageNumber ());
    }
  }

  @Test
  public void testExcessiveNestingRefusedWithClearException () throws IOException
  {
    // Build an entry chain one level deeper than the configured cap.
    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    PLOutlineBuilder.Entry aTip = aOutline.addEntry ("level-1");
    for (int i = 2; i <= PLOutlineBuilder.MAX_OUTLINE_DEPTH + 1; i++)
      aTip = aTip.addChild ("level-" + i);

    try (final PDDocument aDoc = new PDDocument ())
    {
      aDoc.addPage (new org.apache.pdfbox.pdmodel.PDPage ());
      try
      {
        aOutline.customizeDocument (aDoc);
        fail ("Expected IllegalStateException due to depth cap");
      }
      catch (final IllegalStateException ex)
      {
        assertTrue ("Exception message must reference the depth cap, got: " + ex.getMessage (),
                    ex.getMessage ().contains ("maximum depth of " + PLOutlineBuilder.MAX_OUTLINE_DEPTH));
      }
    }
  }

  @Test
  public void testAtMaxDepthIsAccepted () throws IOException
  {
    // Exactly MAX_OUTLINE_DEPTH levels must succeed.
    final PLOutlineBuilder aOutline = new PLOutlineBuilder ();
    PLOutlineBuilder.Entry aTip = aOutline.addEntry ("level-1");
    for (int i = 2; i <= PLOutlineBuilder.MAX_OUTLINE_DEPTH; i++)
      aTip = aTip.addChild ("level-" + i);

    try (final PDDocument aDoc = new PDDocument ())
    {
      aDoc.addPage (new org.apache.pdfbox.pdmodel.PDPage ());
      aOutline.customizeDocument (aDoc); // must not throw
      assertNotNull (aDoc.getDocumentCatalog ().getDocumentOutline ());
    }
  }
}
