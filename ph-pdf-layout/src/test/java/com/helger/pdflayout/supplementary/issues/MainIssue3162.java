/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.helger.font.open_sans.EFontResourceOpenSans;

public final class MainIssue3162
{
  public static void main (final String [] args) throws IOException
  {
    try (final PDDocument doc = new PDDocument ())
    {
      final PDPage page = new PDPage (PDRectangle.A4);
      doc.addPage (page);

      try (final PDPageContentStream contents = new PDPageContentStream (doc, page))
      {
        contents.beginText ();
        contents.setFont (PDType0Font.load (doc, EFontResourceOpenSans.OPEN_SANS_NORMAL.getFontResource ().getInputStream ()), 12);
        contents.endText ();
      }
      // No need to save
    }
  }
}
