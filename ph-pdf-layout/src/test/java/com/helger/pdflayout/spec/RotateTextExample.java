package com.helger.pdflayout.spec;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.util.Matrix;

public class RotateTextExample
{
  public static void main (final String [] args) throws IOException
  {
    try (final PDDocument doc = new PDDocument ())
    {
      final PDPage page = new PDPage (PDRectangle.A4);
      doc.addPage (page);

      try (PDPageContentStream contentStream = new PDPageContentStream (doc, page))
      {
        // A4 dimensions: 595 x 842 points
        final float pageWidth = page.getMediaBox ().getWidth ();
        final float pageHeight = page.getMediaBox ().getHeight ();
        final float centerX = pageWidth / 2;
        final float centerY = pageHeight / 2;
        final float fontSize = 24;
        // // Small offset to separate texts
        final float textOffset = true ? 0 : 20 * fontSize / 2;
        final float padding = 8;

        // Blue box and "Test" - 90° clockwise
        contentStream.saveGraphicsState (); // Save state before rotation
        contentStream.transform (Matrix.getRotateInstance (Math.PI / 2, centerX, centerY));

        // Draw blue box around text position (pre-rotation coordinates, adjusted)
        contentStream.setNonStrokingColor (Color.BLUE);
        contentStream.addRect (-textOffset - padding,
                               -fontSize / 2 - padding,
                               fontSize + 2 * padding,
                               fontSize + 2 * padding);
        contentStream.fill ();

        contentStream.beginText ();
        // Rotate 90° clockwise around center (Math.PI/2 radians)
        if (false)
          contentStream.setTextMatrix (Matrix.getRotateInstance (Math.PI / 2, centerX, centerY));
        // Position text baseline at center (pre-rotation coordinates)
        // Adjust position slightly
        contentStream.newLineAtOffset (-textOffset, textOffset);
        contentStream.setFont (new PDType1Font (FontName.HELVETICA_BOLD), fontSize);
        contentStream.showText ("Test");
        contentStream.endText ();

        contentStream.restoreGraphicsState ();

        // "Test2" - 90° counter-clockwise (negative angle)
        contentStream.beginText ();
        contentStream.setTextMatrix (Matrix.getRotateInstance (-Math.PI / 2, centerX, centerY));
        // Adjust position slightly
        contentStream.newLineAtOffset (textOffset, -textOffset);
        contentStream.setFont (new PDType1Font (FontName.HELVETICA_BOLD), fontSize);
        contentStream.showText ("Test2");
        contentStream.endText ();
      }

      doc.save ("rotated-text-a4.pdf");
    }
  }
}
