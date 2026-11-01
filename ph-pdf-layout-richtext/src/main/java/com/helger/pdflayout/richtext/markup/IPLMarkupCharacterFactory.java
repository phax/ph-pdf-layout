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
package com.helger.pdflayout.richtext.markup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;

/**
 * Factory for one markup character class. Each factory owns a regex pattern and knows how to:
 * <ul>
 * <li>create a {@link IPLMarkupToken} from a matched region,</li>
 * <li>unescape the marker character inside the surrounding plain text (e.g. {@code \*} →
 * {@code *}).</li>
 * </ul>
 *
 * @author Philip Helger
 */
public interface IPLMarkupCharacterFactory
{
  /**
   * @return the regex pattern that recognises the marker. Never <code>null</code>.
   */
  @NonNull
  Pattern getPattern ();

  /**
   * Get the position up to which the text before a match is kept. All of the match after this
   * position is replaced by the token.
   *
   * @param aMatcher
   *        The matcher, positioned on the current match. Never <code>null</code>.
   * @return The start of the match by default. A factory overrides this if the beginning of the
   *         match is text and only needed to decide about the match.
   */
  default int getTextEnd (@NonNull final Matcher aMatcher)
  {
    return aMatcher.start ();
  }

  /**
   * @return <code>true</code> if the pattern is only meaningful at the begin of a line. Default is
   *         <code>false</code>.
   */
  default boolean patternMatchesBeginOfLine ()
  {
    return false;
  }

  /**
   * Creates the token for a successful regex match.
   *
   * @param sText
   *        the full text being parsed.
   * @param aMatcher
   *        the matcher positioned on the successful match.
   * @return the resulting token.
   */
  @NonNull
  IPLMarkupToken createToken (@NonNull String sText, @NonNull Matcher aMatcher);

  /**
   * Unescapes the marker character in the given plain text. By default a backslash-escaped marker
   * is removed.
   *
   * @param sText
   *        the plain text segment to unescape.
   * @return the unescaped text.
   */
  @NonNull
  String unescape (@NonNull String sText);
}
