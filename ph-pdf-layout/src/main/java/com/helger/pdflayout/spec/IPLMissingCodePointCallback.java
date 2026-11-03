/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.jspecify.annotations.NonNull;

/**
 * Callback for a character that a font cannot show, so that the fallback code point of the font is
 * drawn instead. It is called once per font and code point of a document, in the order in which
 * they are encountered. An exception thrown by the callback ends the creation of the PDF.
 *
 * @author Philip Helger
 * @since 8.4.2
 * @see com.helger.pdflayout.PageLayoutPDF#setMissingCodePointCallback(IPLMissingCodePointCallback)
 */
@FunctionalInterface
public interface IPLMissingCodePointCallback
{
  /**
   * Called for a code point that is not part of a font.
   *
   * @param aFont
   *        The font that does not have the code point. Never <code>null</code>.
   * @param nCodePoint
   *        The Unicode code point that cannot be shown.
   * @param nFallbackCodePoint
   *        The Unicode code point that is shown instead.
   */
  void onMissingCodePoint (@NonNull PDFont aFont, int nCodePoint, int nFallbackCodePoint);
}
