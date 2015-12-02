package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import javax.annotation.Nonnull;

public final class PDFontHelper
{
  private PDFontHelper ()
  {}

  public static byte [] encode (@Nonnull final PDFont aFont, final int nCodePoint) throws IllegalArgumentException,
                                                                                   IOException
  {
    // encode method is protected
    return aFont.encode (nCodePoint);
  }
}
