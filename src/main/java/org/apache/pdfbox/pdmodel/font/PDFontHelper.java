package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

public final class PDFontHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PDFontHelper.class);

  private PDFontHelper ()
  {}

  public static byte [] encodeWithFallback (final PDFont aFont,
                                            final String sDrawText,
                                            final int nFallbackCodepoint,
                                            final boolean bPerformSubsetting) throws IOException
  {
    final byte [] aFallbackBytes = aFont.encode (nFallbackCodepoint);
    final boolean bAddToSubset = bPerformSubsetting && aFont.willBeSubset ();

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    int nCPOfs = 0;
    while (nCPOfs < sDrawText.length ())
    {
      final int nCP = sDrawText.codePointAt (nCPOfs);

      // multi-byte encoding with 1 to 4 bytes
      byte [] aCPBytes;
      try
      {
        // This method is package private
        aCPBytes = aFont.encode (nCP);

        if (bAddToSubset)
          aFont.addToSubset (nCP);
      }
      catch (final IllegalArgumentException ex)
      {
        s_aLogger.warn ("No code point " + nCP + " in font " + aFont);
        aCPBytes = aFallbackBytes;
      }
      aBAOS.write (aCPBytes);

      nCPOfs += Character.charCount (nCP);
    }
    return aBAOS.toByteArray ();
  }
}
