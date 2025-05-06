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
package com.helger.pdflayout;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.string.StringHelper;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.RenderingException;

/**
 * Test class helper to easily compare PDFs.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDFTestComparer
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PDFTestComparer.class);

  private PDFTestComparer ()
  {}

  public static void renderAndCompare (@Nonnull final PageLayoutPDF aPageLayout, final File fTarget)
                                                                                                     throws PDFCreationException
  {
    // Render
    aPageLayout.renderTo (fTarget);

    // Get comparison file
    final File fExpected = FileHelper.getCanonicalFileOrNull (new File ("../example-files/",
                                                                        StringHelper.trimStart (fTarget.getPath (),
                                                                                                "pdf")));
    assertTrue ("Non-existing PDF file '" + fExpected.getAbsolutePath () + "' to compare to", fExpected.isFile ());

    try
    {
      LOGGER.warn ("Now comparing '" + fExpected.getAbsolutePath () + "' with '" + fTarget.getAbsolutePath () + "'");
      final CompareResult aResult = new PdfComparator <> (fExpected, fTarget).compare ();
      assertTrue ("Difference in file " + fTarget.getAbsolutePath (), aResult.isEqual ());
    }
    catch (final RenderingException | IOException ex)
    {
      throw new PDFCreationException ("Failed to compare PDFs", ex);
    }
  }
}
