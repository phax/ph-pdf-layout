/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.spec;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.font.api.IFontResource;

/**
 * Wrapper for loading custom fonts.
 *
 * @author Philip Helger
 */
@Immutable
public final class FontLoader
{
  /** A single PDDocument that keeps all the loaded fonts. */
  private static PDDocument s_aFontLoadingDoc = new PDDocument ();

  private FontLoader ()
  {}

  @Nonnull
  public static PDFont loadFontResource (@Nonnull final IFontResource aFontRes) throws IOException
  {
    return loadFontResource (aFontRes, true);
  }

  @Nonnull
  public static PDFont loadFontResource (@Nonnull final IFontResource aFontRes, final boolean bEmbed) throws IOException
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");

    switch (aFontRes.getFontType ())
    {
      case TTF:
        return PDType0Font.load (s_aFontLoadingDoc, aFontRes.getInputStream (), bEmbed);
      case OTF:
        return PDType0Font.load (s_aFontLoadingDoc, new OTFParser ().parse (aFontRes.getInputStream ()), bEmbed);
      default:
        throw new IllegalArgumentException ("Cannot load font resources of type " + aFontRes.getFontType ());
    }
  }

  public static void cleanUp ()
  {
    // Close the temporary document
    StreamHelper.close (s_aFontLoadingDoc);
    s_aFontLoadingDoc = null;
  }
}
