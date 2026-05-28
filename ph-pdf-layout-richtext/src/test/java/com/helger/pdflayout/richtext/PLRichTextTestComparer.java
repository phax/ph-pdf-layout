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
package com.helger.pdflayout.richtext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.RenderingException;

/**
 * Helper for the rich-text test classes. Renders a {@link PageLayoutPDF} to a
 * target file and compares it to a checked-in reference under
 * {@code ../example-files/richtext/&lt;name&gt;.pdf} (relative to the module
 * directory) using the same pdf-compare library that
 * {@code com.helger.pdflayout.PDFTestComparer} uses in the main module.
 *
 * <p>If the reference file is missing and the system property
 * {@code richtext.recordReferences} is set to {@code true}, the just-generated
 * PDF is copied to the reference location and the test passes (recording
 * mode). Otherwise a missing reference causes a test failure with a clear
 * message — the standard mode for CI.</p>
 *
 * @author Philip Helger
 */
@Immutable
public final class PLRichTextTestComparer
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PLRichTextTestComparer.class);
  private static final String SYS_PROP_RECORD = "richtext.recordReferences";
  /** Where the reference PDFs live, relative to the module dir. */
  public static final String REFERENCE_DIR = "../example-files/richtext";

  private PLRichTextTestComparer ()
  {}

  public static void renderAndCompare (@NonNull final PageLayoutPDF aPageLayout, @NonNull final File aTarget) throws PDFCreationException
  {
    // 1. ensure the target's parent dir exists, then render
    final File aTargetDir = aTarget.getParentFile ();
    if (aTargetDir != null && !aTargetDir.exists ())
      aTargetDir.mkdirs ();
    aPageLayout.renderTo (aTarget);
    assertTrue ("Target PDF was not written: " + aTarget.getAbsolutePath (), aTarget.isFile () && aTarget.length () > 0);

    // 2. resolve reference
    final File aReference = new File (REFERENCE_DIR + "/" + aTarget.getName ());

    if (!aReference.isFile ())
    {
      if (Boolean.getBoolean (SYS_PROP_RECORD))
      {
        // recording mode: copy target → reference, pass
        try
        {
          if (!aReference.getParentFile ().exists ())
            aReference.getParentFile ().mkdirs ();
          Files.copy (aTarget.toPath (), aReference.toPath (), StandardCopyOption.REPLACE_EXISTING);
          LOGGER.warn ("Recorded reference PDF at " + aReference.getAbsolutePath ());
          return;
        }
        catch (final IOException ex)
        {
          fail ("Failed to record reference PDF: " + ex.getMessage ());
        }
      }
      fail ("Reference PDF not found at " + aReference.getAbsolutePath () +
            " — re-run with -D" + SYS_PROP_RECORD + "=true to record.");
    }

    // 3. pixel-diff comparison
    try
    {
      LOGGER.info ("Comparing '" + aReference.getAbsolutePath () + "' with '" + aTarget.getAbsolutePath () + "'");
      final CompareResult aResult = new PdfComparator <> (aReference, aTarget).compare ();
      assertTrue ("Difference in file " + aTarget.getAbsolutePath (), aResult.isEqual ());
    }
    catch (final RenderingException | IOException ex)
    {
      throw new PDFCreationException ("Failed to compare PDFs", ex);
    }
  }
}
