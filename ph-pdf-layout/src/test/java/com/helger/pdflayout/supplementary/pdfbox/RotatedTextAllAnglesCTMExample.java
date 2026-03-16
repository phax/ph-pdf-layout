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

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;

import com.helger.pdflayout.render.PreparationContextGlobal;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Uses CTM transforms (via
 * {@code saveGraphicsState}/{@code transform}/{@code restoreGraphicsState}) instead of setting the
 * text matrix directly. Each text block is rendered at the origin of a rotated+translated
 * coordinate system.
 * <p>
 * Per the PDF spec the text rendering matrix is Trm = Tfs x Tm x CTM, so the CTM rotation is
 * applied to text glyphs as well.
 * <p>
 * <b>CTM call order:</b> PDF uses row-vector convention: {@code [x', y', 1] = [x, y, 1] × CTM}.
 * {@code transform(M)} pre-multiplies: {@code CTM_new = M × CTM_old}. To achieve "rotate then
 * translate" (i.e. rotate the point first, then translate the result), the effective CTM must be
 * {@code R × T}. Since each {@code transform()} pre-multiplies, call {@code transform(T)} first,
 * then {@code transform(R)} second: {@code CTM = R × T}.
 */
public class RotatedTextAllAnglesCTMExample
{
  public static void main (final String [] args) throws IOException
  {
    try (final PDDocument doc = new PDDocument ())
    {
      final PDPage page = new PDPage (PDRectangle.A4);
      doc.addPage (page);

      final float margin = 50;
      final float pageHeight = page.getMediaBox ().getHeight ();
      final float fontSize = 18;
      final float gap = 5;

      final float leftX = margin;
      float currentY = pageHeight - margin;

      final PDFont font = PreloadFont.REGULAR_BOLD.loadPDFont (doc);
      final LoadedFont loadedFont = new PreparationContextGlobal (doc).getLoadedFont (new FontSpec (PreloadFont.REGULAR_BOLD,
                                                                                                    fontSize));

      try (final PDPageContentStream cs = new PDPageContentStream (doc, page))
      {
        // Set globally
        cs.setFont (font, fontSize);

        // 1. "Test" — no rotation (just a translate via CTM for consistency)
        {
          final String text = "Test";

          cs.saveGraphicsState ();
          cs.transform (Matrix.getTranslateInstance (leftX, currentY - fontSize));

          cs.beginText ();
          cs.newLineAtOffset (0, 0);
          cs.showText (text);
          cs.endText ();

          cs.restoreGraphicsState ();

          currentY -= fontSize + gap;
        }

        // 2. "Test90" — 90° CW rotation
        // Desired CTM = R(-90°) × T(leftX, currentY)
        // Call order: T first, then R (each pre-multiplies)
        // Text at origin flows downward from (leftX, currentY)
        {
          final String text = "Test90";
          final float textWidth = loadedFont.getStringWidth (text, fontSize);

          cs.saveGraphicsState ();
          cs.transform (Matrix.getTranslateInstance (leftX, currentY));
          cs.transform (Matrix.getRotateInstance (Math.toRadians (-90), 0, 0));

          cs.beginText ();
          cs.newLineAtOffset (0, 0);
          cs.showText (text);
          cs.endText ();

          cs.restoreGraphicsState ();

          currentY -= textWidth + gap;
        }

        // 3. "Test 180" — 180° rotation
        // Desired CTM = R(180°) × T(leftX + textWidth, currentY)
        // Text at origin flows leftward from the right edge
        {
          final String text = "Test 180";
          final float textWidth = loadedFont.getStringWidth (text, fontSize);

          cs.saveGraphicsState ();
          cs.transform (Matrix.getTranslateInstance (leftX + textWidth, currentY));
          cs.transform (Matrix.getRotateInstance (Math.toRadians (180), 0, 0));

          cs.beginText ();
          cs.newLineAtOffset (0, 0);
          cs.showText (text);
          cs.endText ();

          cs.restoreGraphicsState ();

          currentY -= fontSize + gap;
        }

        // 4. "Test 270" — 270° CW rotation (= 90° CCW)
        // Desired CTM = R(+90°) × T(leftX + fontSize, currentY - textWidth)
        // Text at origin flows upward
        {
          final String text = "Test 270";
          final float textWidth = loadedFont.getStringWidth (text, fontSize);

          cs.saveGraphicsState ();
          cs.transform (Matrix.getTranslateInstance (leftX + fontSize, currentY - textWidth));
          cs.transform (Matrix.getRotateInstance (Math.toRadians (90), 0, 0));

          cs.beginText ();
          cs.newLineAtOffset (0, 0);
          cs.showText (text);
          cs.endText ();

          cs.restoreGraphicsState ();

          currentY -= textWidth + gap;
        }
      }

      doc.save ("rotated-text-all-angles-ctm.pdf");
    }
  }
}
