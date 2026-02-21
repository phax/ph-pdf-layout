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

public class TopLeftTextExample
{
  public static void main (final String [] args) throws IOException
  {
    try (final PDDocument doc = new PDDocument ())
    {
      final PDPage page = new PDPage (PDRectangle.A4);
      doc.addPage (page);

      final float margin = 50; // Top/left margin from page edge
      final float pageHeight = page.getMediaBox ().getHeight ();
      final float lineHeight = 20;
      final float fontSize = 14;

      // Top-left Y position (PDF Y=0 at bottom)
      final float topY = pageHeight - margin;
      final float leftX = margin;

      final PDFont font = PreloadFont.REGULAR_BOLD.loadPDFont (doc);
      final LoadedFont aLoadedFont = new PreparationContextGlobal (doc).getLoadedFont (new FontSpec (PreloadFont.REGULAR_BOLD,
                                                                                                     fontSize));

      try (PDPageContentStream contentStream = new PDPageContentStream (doc, page))
      {
        // 1. "Test" at top-left (normal orientation)
        {
          contentStream.beginText ();
          contentStream.newLineAtOffset (leftX, topY);
          contentStream.setFont (font, fontSize);
          contentStream.showText ("Test");
          contentStream.endText ();
        }

        // 2. "Test90" rotated 90° right, left-top aligned with "Test" left-top
        // Approx text width for positioning
        {
          final String sText = "Test90";
          final float textWidth = aLoadedFont.getStringWidth (sText, fontSize);

          contentStream.saveGraphicsState ();
          // Rotate 90° clockwise around the target top-left corner (leftX, topY)
          contentStream.transform (Matrix.getRotateInstance (Math.toRadians (90),
                                                             leftX + (lineHeight / 2),
                                                             topY - textWidth));

          contentStream.beginText ();
          // Position baseline at 0 relative to rotation point (top-left of rotated bbox)
          if (false)
            contentStream.newLineAtOffset (0, true ? 0 : -fontSize * 0.2f);
          contentStream.setFont (font, fontSize);
          contentStream.showText (sText);
          contentStream.endText ();

          contentStream.restoreGraphicsState ();
        }
      }

      doc.save ("top-left-rotated-text.pdf");
    }
  }
}
