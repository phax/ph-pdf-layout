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
package com.helger.pdflayout.supplementary.pdfbox;

import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class DumpContentStream
{
  public static void main (final String [] args) throws Exception
  {
    for (final String file : args)
    {
      System.out.println ("=== " + file + " ===");
      try (final PDDocument doc = Loader.loadPDF (new java.io.File (file)))
      {
        for (int i = 0; i < doc.getNumberOfPages (); i++)
        {
          final PDPage page = doc.getPage (i);
          final PDFStreamParser parser = new PDFStreamParser (page);
          final List <Object> tokens = parser.parse ();
          final StringBuilder line = new StringBuilder ();
          for (final Object token : tokens)
          {
            if (token instanceof final Operator op)
            {
              System.out.println (line.toString () + op.getName ());
              line.setLength (0);
            }
            else if (token instanceof final COSFloat f)
              line.append (f.floatValue ()).append (" ");
            else if (token instanceof final COSInteger n)
              line.append (n.longValue ()).append (" ");
            else if (token instanceof final COSName n)
              line.append ("/").append (n.getName ()).append (" ");
            else if (token instanceof final COSString s)
              line.append ("(").append (s.getString ()).append (") ");
            else
              line.append (token).append (" ");
          }
        }
      }
      System.out.println ();
    }
  }
}
