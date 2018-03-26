/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileSystemRecursiveIterator;

public final class MainIssue3204
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainIssue3204.class);

  public static void main (final String [] args) throws IOException
  {
    int n = 0;
    for (final File f : new FileSystemRecursiveIterator ("/"))
      if (f.isFile () && f.getName ().endsWith (".pdf"))
      {
        s_aLogger.info (f.getAbsolutePath ());
        try (PDDocument aDoc = PDDocument.load (f))
        {
          new PDFRenderer (aDoc).renderImageWithDPI (0, 195);
          n++;
        }
      }
    s_aLogger.info (n + " files found");
  }
}
