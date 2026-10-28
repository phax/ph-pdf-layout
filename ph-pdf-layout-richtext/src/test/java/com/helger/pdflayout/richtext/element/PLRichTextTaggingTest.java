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
package com.helger.pdflayout.richtext.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.tagging.EPLStructureRole;

/**
 * Test class for {@link PLRichText} in a tagged PDF.
 *
 * @author Philip Helger
 */
public final class PLRichTextTaggingTest
{
  @Test
  public void testParagraphWithLink () throws Exception
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (PLRichText.createFromMarkup ("A heading", PLFontFamily.regular (), 16f, PLColor.BLACK)
                              .setStructureRole (EPLStructureRole.H1));
    aPS.addElement (PLRichText.createFromMarkup ("Text with a {link[https://github.com/phax/ph-pdf-layout]}link{link} and **bold** text.",
                                                 PLFontFamily.regular (),
                                                 11f,
                                                 PLColor.BLACK));
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    new PageLayoutPDF ().addPageSet (aPS).setCreateTaggedPdf (true).renderTo (aBAOS);

    try (final PDDocument aDoc = Loader.loadPDF (aBAOS.toByteArray ()))
    {
      final PDStructureElement aDocElement = (PDStructureElement) aDoc.getDocumentCatalog ()
                                                                      .getStructureTreeRoot ()
                                                                      .getKids ()
                                                                      .get (0);
      final List <Object> aBlocks = aDocElement.getKids ();
      assertEquals (2, aBlocks.size ());
      assertEquals ("H1", ((PDStructureElement) aBlocks.get (0)).getStructureType ());

      // The paragraph contains the text before, the link and the text after
      final PDStructureElement aP = (PDStructureElement) aBlocks.get (1);
      assertEquals ("P", aP.getStructureType ());
      PDStructureElement aLink = null;
      int nMCRs = 0;
      for (final Object aKid : aP.getKids ())
        if (aKid instanceof final PDStructureElement aElement)
          aLink = aElement;
        else
          if (aKid instanceof PDMarkedContentReference)
            nMCRs++;
      assertTrue (nMCRs >= 2);
      assertNotNull (aLink);
      assertEquals ("Link", aLink.getStructureType ());

      // The link contains its text and its annotation
      boolean bMCR = false;
      boolean bOBJR = false;
      for (final Object aKid : aLink.getKids ())
      {
        if (aKid instanceof PDMarkedContentReference)
          bMCR = true;
        if (aKid instanceof PDObjectReference)
          bOBJR = true;
      }
      assertTrue (bMCR);
      assertTrue (bOBJR);

      final PDAnnotation aAnnotation = aDoc.getPage (0).getAnnotations ().get (0);
      assertEquals ("https://github.com/phax/ph-pdf-layout", aAnnotation.getContents ());
    }
  }
}
