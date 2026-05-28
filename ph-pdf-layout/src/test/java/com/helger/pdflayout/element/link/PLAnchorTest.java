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
package com.helger.pdflayout.element.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.jspecify.annotations.NonNull;
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
 * Test class for {@link PLAnchor}, {@link PLInternalLink}, and anchor-name support on block
 * elements via {@link com.helger.pdflayout.base.IPLHasAnchorName}.
 *
 * @author Philip Helger
 */
public final class PLAnchorTest
{
  @Rule
  public final PLDebugTestRule m_aRule = new PLDebugTestRule ();

  private static final FontSpec FONT = new FontSpec (PreloadFont.REGULAR, 12);

  private static byte [] _renderToBytes (final @NonNull PageLayoutPDF aLayout) throws PDFCreationException
  {
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      aLayout.renderTo (aOS);
      return aOS.toByteArray ();
    }
  }

  private static @NonNull Map <String, PDPageDestination> _loadNamedDestinations (final @NonNull PDDocument aDoc) throws IOException
  {
    final PDDocumentNameDictionary aNames = aDoc.getDocumentCatalog ().getNames ();
    assertNotNull ("Document must have a /Names dictionary", aNames);
    assertNotNull ("Document must have a /Names/Dests tree", aNames.getDests ());
    return aNames.getDests ().getNames ();
  }

  @Test
  public void testPLAnchorRegistersNamedDestination () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Before anchor", FONT));
    aPS.addElement (new PLAnchor ("section1"));
    aPS.addElement (new PLText ("After anchor", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertNotNull (aDests);
      assertTrue ("Anchor 'section1' must be registered", aDests.containsKey ("section1"));

      final PDPageDestination aDest = aDests.get ("section1");
      assertTrue (aDest instanceof PDPageXYZDestination);
      assertEquals (0, aDest.retrievePageNumber ());
    }
  }

  @Test
  public void testBlockElementAsAnchorViaSetAnchorName () throws PDFCreationException, IOException
  {
    // Setting an anchor name on a block element makes it its own anchor target,
    // without needing a separate PLAnchor marker.
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Heading", FONT).setAnchorName ("intro"));
    aPS.addElement (new PLText ("Body", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertNotNull (aDests.get ("intro"));
      assertEquals (0, aDests.get ("intro").retrievePageNumber ());
    }
  }

  @Test
  public void testAnchorRegistersOnFirstFragmentOnly () throws PDFCreationException, IOException
  {
    // Make a tall text that splits across pages. Only the first fragment should
    // register the named destination.
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < 30; i++)
      aSB.append ("Filler line ").append (i).append ('\n');

    final PLPageSet aPS = new PLPageSet (300, 120).setMargin (10);
    aPS.addElement (new PLText (aSB.toString (), FONT).setID ("longtext")
                                                      .setVertSplittable (true)
                                                      .setAnchorName ("long"));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      assertTrue ("Test setup must produce at least 2 pages", aReadDoc.getNumberOfPages () >= 2);
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertEquals ("Exactly one destination per anchor name", 1, aDests.size ());
      assertEquals ("Anchor must point at the first page where the element appeared",
                    0,
                    aDests.get ("long").retrievePageNumber ());
    }
  }

  @Test
  public void testPLAnchorOnSecondPage () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Page 1 content", FONT));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLAnchor ("page2anchor"));
    aPS.addElement (new PLText ("Page 2 content", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertEquals (1, aDests.get ("page2anchor").retrievePageNumber ());
    }
  }

  @Test
  public void testDuplicateAnchorNameKeepsFirst () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLAnchor ("dup"));
    aPS.addElement (new PLText ("First labelled section", FONT));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLAnchor ("dup")); // duplicate - must be ignored, warning logged
    aPS.addElement (new PLText ("Second labelled section", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertEquals (1, aDests.size ());
      assertEquals ("First registration must win, pointing at page 0", 0, aDests.get ("dup").retrievePageNumber ());
    }
  }

  @Test
  public void testPLInternalLinkProducesGoToAnnotation () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLInternalLink (new PLText ("Click to jump", FONT)).setTargetAnchorName ("target"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLAnchor ("target"));
    aPS.addElement (new PLText ("You arrived", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      // Find the link annotation on page 0.
      PDAnnotationLink aFound = null;
      for (final PDAnnotation aAnn : aReadDoc.getPage (0).getAnnotations ())
      {
        if (aAnn instanceof PDAnnotationLink)
        {
          aFound = (PDAnnotationLink) aAnn;
          break;
        }
      }
      assertNotNull ("Expected a link annotation on page 0", aFound);

      // Its action must be a GoTo with the target named destination.
      assertTrue ("Internal link action must be PDActionGoTo", aFound.getAction () instanceof PDActionGoTo);
      final PDActionGoTo aGoTo = (PDActionGoTo) aFound.getAction ();
      final PDDestination aDest = aGoTo.getDestination ();
      assertTrue ("Destination must be a named destination", aDest instanceof PDNamedDestination);
      assertEquals ("target", ((PDNamedDestination) aDest).getNamedDestination ());

      // The named destination resolves to page 1 in the document.
      final Map <String, PDPageDestination> aDests = _loadNamedDestinations (aReadDoc);
      assertEquals (1, aDests.get ("target").retrievePageNumber ());
    }
  }

  @Test
  public void testInternalLinkWithoutTargetRendersNothing () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLInternalLink (new PLText ("No target", FONT)));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      // No link annotation should be added when the target is null.
      for (final PDAnnotation aAnn : aReadDoc.getPage (0).getAnnotations ())
        assertNull ("No link annotation should be emitted without a target",
                    aAnn instanceof PDAnnotationLink ? aAnn : null);
    }
  }

  @Test
  public void testNoNamedDestinationsWhenNoAnchorsPresent () throws PDFCreationException, IOException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Plain content", FONT));

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    try (final PDDocument aReadDoc = Loader.loadPDF (_renderToBytes (aLayout)))
    {
      // Documents without anchors must not get a /Names dictionary.
      assertNull (aReadDoc.getDocumentCatalog ().getNames ());
    }
  }
}
