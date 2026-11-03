/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Tests the placeholders of the page in a {@link PLRichText}.
 *
 * @author Philip Helger
 */
public final class PLRichTextPlaceholderTest
{
  private static String _getPageText (final boolean bReplacePlaceholder, final int nPage) throws IOException,
                                                                                         PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A6).setMargin (20);
    for (int i = 0; i < 40; ++i)
      aPS.addElement (new PLText ("line " + i, new FontSpec (PreloadFont.REGULAR, 10f)));
    aPS.setPageFooter (PLRichText.createFromMarkup ("**Page** $\\{total-page-number\\} of $\\{total-page-count\\}",
                                                    PLFontFamily.regular (),
                                                    8f,
                                                    PLColor.BLACK)
                                 .setReplacePlaceholder (bReplacePlaceholder));

    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      new PageLayoutPDF ().addPageSet (aPS).renderTo (aOS);
      try (final PDDocument aDoc = Loader.loadPDF (aOS.toByteArray ()))
      {
        final PDFTextStripper aStripper = new PDFTextStripper ();
        aStripper.setStartPage (nPage);
        aStripper.setEndPage (nPage);
        return aStripper.getText (aDoc);
      }
    }
  }

  @Test
  public void testReplaced () throws IOException, PDFCreationException
  {
    final String sPage1 = _getPageText (true, 1);
    assertTrue (sPage1, sPage1.contains ("Page 1 of 2"));
    final String sPage2 = _getPageText (true, 2);
    assertTrue (sPage2, sPage2.contains ("Page 2 of 2"));
  }

  @Test
  public void testNotReplacedByDefault () throws IOException, PDFCreationException
  {
    final String sPage1 = _getPageText (false, 1);
    assertTrue (sPage1, sPage1.contains ("${total-page-number}"));
    assertFalse (sPage1, sPage1.contains ("Page 1 of"));
  }
}
