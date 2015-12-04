/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.supplementary.benchmark;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.helger.font.open_sans.EFontResource;

public final class MainHelloWorldType0
{
  public static void main (final String [] args) throws IOException
  {
    final String s = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. ";
    final String file = "pdf/benchmark-type0.pdf";

    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < 300; i++)
      aSB.append (s).append (' ');
    final String message = aSB.toString ();

    final long nStart = System.currentTimeMillis ();
    final PDDocument doc = new PDDocument ();
    try
    {
      final PDPage page = new PDPage ();
      doc.addPage (page);

      final PDFont font = PDType0Font.load (doc, EFontResource.OPEN_SANS_NORMAL.getFontResource ().getInputStream ());
      System.out.println ("Finished loading font at " + (System.currentTimeMillis () - nStart) + "ms");

      for (int i = 0; i < 100; ++i)
        font.getStringWidth (message);
      System.out.println ("Finished string width (" +
                          message.length () +
                          " chars) after " +
                          (System.currentTimeMillis () - nStart) +
                          "ms");

      final PDPageContentStream contents = new PDPageContentStream (doc, page);
      contents.beginText ();
      contents.setFont (font, 12);
      contents.newLineAtOffset (100, 700);
      contents.showText (message);
      contents.endText ();
      contents.close ();

      doc.save (file);
    }
    finally
    {
      doc.close ();
    }
    System.out.println (file + " created! Took " + (System.currentTimeMillis () - nStart) + "ms");
  }
}
