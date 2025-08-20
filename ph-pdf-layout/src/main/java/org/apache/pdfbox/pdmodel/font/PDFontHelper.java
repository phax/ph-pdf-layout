/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import jakarta.annotation.Nonnull;

/**
 * Helper class to access package private classes of {@link PDFont}.
 *
 * @author Philip Helger
 */
public final class PDFontHelper
{
  private PDFontHelper ()
  {}

  public static byte [] encode (@Nonnull final PDFont aFont, final int nCodePoint) throws IOException
  {
    // encode method is protected
    return aFont.encode (nCodePoint);
  }
}
