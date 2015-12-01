package org.apache.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.font.PDFont;

public final class PDDocumentHelper
{
  private PDDocumentHelper ()
  {}

  public static void handleFontSubset (final PDDocument doc, final PDFont font)
  {
    if (font.willBeSubset () && !doc.getFontsToSubset ().contains (font))
    {
      doc.getFontsToSubset ().add (font);
    }
  }
}
