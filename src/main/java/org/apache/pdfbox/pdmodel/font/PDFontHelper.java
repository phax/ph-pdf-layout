package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

public final class PDFontHelper
{
  private PDFontHelper ()
  {}

  public static byte [] encode (final PDFont font, final String sDrawText, final int nFallback) throws IOException
  {
    return encode (font, sDrawText, font.encode (nFallback));
  }

  public static byte [] encode (final PDFont font, final String sDrawText, final byte [] aFallback) throws IOException
  {
    final NonBlockingByteArrayOutputStream out = new NonBlockingByteArrayOutputStream ();
    int offset = 0;
    while (offset < sDrawText.length ())
    {
      final int codePoint = sDrawText.codePointAt (offset);

      // multi-byte encoding with 1 to 4 bytes
      byte [] bytes;
      try
      {
        bytes = font.encode (codePoint);
      }
      catch (final IllegalArgumentException ex)
      {
        bytes = aFallback;
      }
      out.write (bytes);

      offset += Character.charCount (codePoint);
    }
    return out.toByteArray ();
  }
}
