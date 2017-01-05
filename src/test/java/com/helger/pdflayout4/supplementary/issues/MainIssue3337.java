/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.supplementary.issues;

import java.io.File;
import java.io.IOException;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.helger.font.open_sans.EFontResourceOpenSans;

public final class MainIssue3337
{
  public static void main (final String [] args) throws IOException
  {
    final TrueTypeFont aTTF = new TTFParser ().parse (EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ()
                                                                                            .getInputStream ());

    for (int i = 0; i < 2; ++i)
    {
      System.out.println ("Create PDF " + i);
      try (final PDDocument doc = new PDDocument ())
      {
        final PDPage page = new PDPage (PDRectangle.A4);
        doc.addPage (page);

        try (final PDPageContentStream contents = new PDPageContentStream (doc, page))
        {
          contents.beginText ();
          contents.setFont (PDType0Font.load (doc, aTTF, true), 12);
          contents.showText ("Test");
          contents.endText ();
        }
        doc.save (new File ("pdf/test-3337-" + i + ".pdf"));
      }
    }
  }
}
