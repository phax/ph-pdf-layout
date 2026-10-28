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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.junit.Test;

/**
 * Test class for class {@link PLTaggingContext}.
 *
 * @author Philip Helger
 */
public final class PLTaggingContextTest
{
  @Test
  public void testBasic ()
  {
    final PLTaggingContext aCtx = new PLTaggingContext ("de-DE");
    final PDStructureElement aDocElement = aCtx.getDocumentElement ();
    assertEquals ("Document", aDocElement.getStructureType ());
    assertEquals ("de-DE", aDocElement.getLanguage ());
    // PDFBox creates new wrapper objects, so compare the dictionaries
    assertSame (aDocElement.getCOSObject (), ((PDStructureElement) aCtx.getStructureTreeRoot ().getKids ().get (0)).getCOSObject ());
    assertEquals ("Document", aCtx.getCurrentStructureType ());

    final PDPage aPage = new PDPage ();
    aCtx.startPage (aPage);
    assertEquals (0, aPage.getStructParents ());

    // A paragraph with one marked content
    assertTrue (aCtx.beginElement ("p1", false, EPLStructureRole.P, null));
    assertEquals ("P", aCtx.getCurrentStructureType ());
    assertEquals (0, aCtx.createMarkedContent ());
    assertEquals (1, aCtx.createMarkedContent ());
    aCtx.endElement ();
    assertEquals ("Document", aCtx.getCurrentStructureType ());

    final PDStructureElement aP = (PDStructureElement) aDocElement.getKids ().get (0);
    final List <Object> aKids = aP.getKids ();
    assertEquals (2, aKids.size ());
    assertTrue (aKids.get (0) instanceof PDMarkedContentReference);
    assertEquals (1, ((PDMarkedContentReference) aKids.get (1)).getMCID ());

    // Continuing the same key does not create a new element
    assertTrue (aCtx.beginElement ("p1", true, EPLStructureRole.P, null));
    aCtx.endElement ();
    assertEquals (1, aDocElement.getKids ().size ());
    // Not continuing creates a new one
    assertTrue (aCtx.beginElement ("p1", false, EPLStructureRole.P, null));
    aCtx.endElement ();
    assertEquals (2, aDocElement.getKids ().size ());

    // Alternate text
    assertTrue (aCtx.beginElement ("fig", false, EPLStructureRole.FIGURE, "A picture"));
    aCtx.endElement ();
    assertEquals ("A picture", ((PDStructureElement) aDocElement.getKids ().get (2)).getAlternateDescription ());
  }

  @Test
  public void testHeaderRowAndArtifact ()
  {
    final PLTaggingContext aCtx = new PLTaggingContext (null);
    aCtx.startPage (new PDPage ());

    // TD in a header row becomes TH
    aCtx.beginHeaderRow ();
    assertTrue (aCtx.beginElement ("c1", false, EPLStructureRole.TD, null));
    assertEquals ("TH", aCtx.getCurrentStructureType ());
    aCtx.endElement ();
    aCtx.endHeaderRow ();
    assertTrue (aCtx.beginElement ("c2", false, EPLStructureRole.TD, null));
    assertEquals ("TD", aCtx.getCurrentStructureType ());
    aCtx.endElement ();

    // No elements in an artifact
    assertFalse (aCtx.isInArtifact ());
    aCtx.beginArtifact ();
    aCtx.beginArtifact ();
    assertTrue (aCtx.isInArtifact ());
    assertFalse (aCtx.beginElement ("p", false, EPLStructureRole.P, null));
    aCtx.endArtifact ();
    assertTrue (aCtx.isInArtifact ());
    aCtx.endArtifact ();
    assertFalse (aCtx.isInArtifact ());

    try
    {
      aCtx.endArtifact ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    try
    {
      aCtx.endHeaderRow ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    try
    {
      aCtx.endElement ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testAnnotation ()
  {
    final PLTaggingContext aCtx = new PLTaggingContext (null);
    final PDPage aPage = new PDPage ();
    aCtx.startPage (aPage);

    final PDActionURI aAction = new PDActionURI ();
    aAction.setURI ("https://example.org");
    final PDAnnotationLink aLink = new PDAnnotationLink ();
    aLink.setAction (aAction);

    assertTrue (aCtx.beginElement ("l", false, EPLStructureRole.LINK, null));
    aCtx.addAnnotation (aLink);
    aCtx.endElement ();
    // The link target is the alternate description
    assertEquals ("https://example.org", aLink.getContents ());
    assertEquals (1, aLink.getStructParent ());
    final PDStructureElement aLinkElement = (PDStructureElement) aCtx.getDocumentElement ().getKids ().get (0);
    assertTrue (aLinkElement.getKids ().get (0) instanceof PDObjectReference);

    // A link in an artifact gets its own link element
    final PDAnnotationLink aLink2 = new PDAnnotationLink ();
    aLink2.setAction (aAction);
    aCtx.beginArtifact ();
    aCtx.addAnnotation (aLink2);
    aCtx.endArtifact ();
    assertEquals (2, aCtx.getDocumentElement ().getKids ().size ());
    assertEquals ("Link", ((PDStructureElement) aCtx.getDocumentElement ().getKids ().get (1)).getStructureType ());
  }

  @Test
  public void testFinish ()
  {
    final PLTaggingContext aCtx = new PLTaggingContext (null);
    try (final PDDocument aDoc = new PDDocument ())
    {
      final PDPage aPage = new PDPage ();
      aDoc.addPage (aPage);
      aCtx.startPage (aPage);
      aCtx.beginElement ("p", false, EPLStructureRole.P, null);

      // Open element
      try
      {
        aCtx.finish (aDoc);
        fail ();
      }
      catch (final IllegalStateException ex)
      {
        // expected
      }
      aCtx.endElement ();

      aCtx.finish (aDoc);
      assertSame (aCtx.getStructureTreeRoot ().getCOSObject (),
                  aDoc.getDocumentCatalog ().getStructureTreeRoot ().getCOSObject ());
      assertTrue (aDoc.getDocumentCatalog ().getMarkInfo ().isMarked ());
      assertNotNull (aCtx.getStructureTreeRoot ().getParentTree ());
      assertEquals (1, aCtx.getStructureTreeRoot ().getParentTreeNextKey ());
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException (ex);
    }
  }
}
