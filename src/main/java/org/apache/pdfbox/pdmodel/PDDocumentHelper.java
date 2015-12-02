package org.apache.pdfbox.pdmodel;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.font.PDFont;

public final class PDDocumentHelper
{
  private PDDocumentHelper ()
  {}

  public static void handleFontSubset (@Nonnull final PDDocument aDoc, @Nonnull final PDFont aFont)
  {
    // getFontsToSubset is package private
    if (aFont.willBeSubset () && !aDoc.getFontsToSubset ().contains (aFont))
      aDoc.getFontsToSubset ().add (aFont);
  }
}
