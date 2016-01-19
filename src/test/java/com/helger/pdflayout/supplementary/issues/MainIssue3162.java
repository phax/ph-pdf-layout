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
package com.helger.pdflayout.supplementary.issues;

import java.io.IOException;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentHelper;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.helger.font.open_sans.EFontResource;

public final class MainIssue3162
{
  public static void main (final String [] args) throws IOException
  {
    final String s = "This is a test";
    final String file = "pdf/issue-3162.pdf";

    final PDDocument doc = new PDDocument ();
    try
    {
      final PDPage page = new PDPage (PDRectangle.A4);
      doc.addPage (page);

      final TrueTypeFont ttf = new TTFParser ().parse (EFontResource.OPEN_SANS_NORMAL.getFontResource ()
                                                                                     .getInputStream ());
      final PDFont font = PDType0Font.load (doc, ttf, true);
      final PDFont font2 = PDType0Font.load (doc, ttf, true);
      PDDocumentHelper.handleFontSubset (doc, font);
      PDDocumentHelper.handleFontSubset (doc, font2);

      final PDPageContentStream contents = new PDPageContentStream (doc, page);
      try
      {
        contents.beginText ();
        contents.setFont (font, 12);
        contents.newLineAtOffset (100, 700);
        contents.showText (s);
        contents.setFont (font2, 12);
        contents.newLineAtOffset (0, -14);
        contents.showText (s + " line 2");
        contents.endText ();
      }
      finally
      {
        contents.close ();
      }

      doc.save (file);
    }
    finally
    {
      doc.close ();
    }
  }
}
