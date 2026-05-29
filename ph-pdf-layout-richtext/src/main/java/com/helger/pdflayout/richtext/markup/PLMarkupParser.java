/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Parses a markup string into a list of {@link IPLMarkupToken tokens}.
 * <p>
 * The algorithm mirrors the one used in the predecessor {@code rst.pdfbox.layout}
 * library: the input is split successively by each registered marker factory.
 * Each split step replaces the matched marker with the corresponding token and
 * unescapes the marker inside the surrounding plain text segments. The order
 * of factories matters — markers that contain other markers (e.g. the
 * double-underscore underline) must come before the simpler ones.
 *
 * @author Philip Helger
 */
public final class PLMarkupParser
{
  /**
   * Default factory order:
   * <ol>
   * <li>NEWLINE — splits lines first so other markers don't accidentally cross
   * line breaks.</li>
   * <li>UNDERLINE (<code>__</code>) — before ITALIC (<code>_</code>) so the
   * pair isn't consumed as two italics.</li>
   * <li>METRICS (<code>{_}</code> / <code>{^}</code>) — must run before ITALIC
   * so the bare <code>_</code> inside <code>{_}</code> isn't swallowed as an
   * italic marker.</li>
   * <li>BOLD (<code>*</code>)</li>
   * <li>ITALIC (<code>_</code>)</li>
   * <li>COLOR_CMYK (<code>{color_cmyk:C,M,Y,K}</code>) — must run before
   * ITALIC because the marker contains an underscore in {@code color_cmyk}
   * which ITALIC would otherwise grab.</li>
   * <li>BOLD (<code>*</code>)</li>
   * <li>ITALIC (<code>_</code>)</li>
   * <li>COLOR (<code>{color:#xxxxxx}</code>)</li>
   * <li>HYPERLINK (<code>{link...}</code>)</li>
   * <li>ANCHOR (<code>{anchor:...}</code>)</li>
   * </ol>
   */
  public static final ICommonsList <IPLMarkupCharacterFactory> DEFAULT_FACTORIES = new CommonsArrayList <> (PLMarkupCharacters.NEWLINE,
                                                                                                            PLMarkupCharacters.UNDERLINE,
                                                                                                            PLMarkupCharacters.METRICS,
                                                                                                            PLMarkupCharacters.COLOR_CMYK,
                                                                                                            PLMarkupCharacters.BOLD,
                                                                                                            PLMarkupCharacters.ITALIC,
                                                                                                            PLMarkupCharacters.COLOR,
                                                                                                            PLMarkupCharacters.HYPERLINK,
                                                                                                            PLMarkupCharacters.ANCHOR);

  private final ICommonsList <IPLMarkupCharacterFactory> m_aFactories;

  /** Creates a parser using {@link #DEFAULT_FACTORIES}. */
  public PLMarkupParser ()
  {
    this (DEFAULT_FACTORIES);
  }

  public PLMarkupParser (@NonNull final ICommonsList <IPLMarkupCharacterFactory> aFactories)
  {
    m_aFactories = aFactories.getClone ();
  }

  /**
   * Tokenises the given markup string. The result interleaves
   * {@link IPLMarkupToken.Text Text} tokens (plain text segments) with marker
   * tokens (style toggles, color changes, annotations, newlines).
   *
   * @param sMarkup
   *        the markup string to parse.
   * @return the tokenised representation.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <IPLMarkupToken> parse (@NonNull final String sMarkup)
  {
    // Start with a single text segment.
    ICommonsList <Object> aSegments = new CommonsArrayList <> ();
    aSegments.add (sMarkup);

    // Split by each factory in turn.
    for (final IPLMarkupCharacterFactory aFactory : m_aFactories)
    {
      aSegments = _splitOnce (aFactory, aSegments);
    }

    // Unescape any remaining backslash-backslash pairs in the plain text.
    final ICommonsList <IPLMarkupToken> aResult = new CommonsArrayList <> ();
    for (final Object aSegment : aSegments)
    {
      if (aSegment instanceof final String sPlain)
      {
        if (!sPlain.isEmpty ())
        {
          aResult.add (new IPLMarkupToken.Text (sPlain.replace ("\\\\", "\\")));
        }
      }
      else
        if (aSegment instanceof final IPLMarkupToken aToken)
        {
          aResult.add (aToken);
        }
    }
    return aResult;
  }

  /**
   * Splits every {@link String} segment in {@code aIn} by the factory's pattern,
   * keeping non-String segments unchanged. Matched regions are replaced by
   * tokens; surrounding plain text is unescaped.
   */
  @NonNull
  private static ICommonsList <Object> _splitOnce (@NonNull final IPLMarkupCharacterFactory aFactory,
                                                   @NonNull final ICommonsList <Object> aIn)
  {
    final ICommonsList <Object> aOut = new CommonsArrayList <> ();
    boolean bBeginOfLine = true;
    for (final Object aCurrent : aIn)
    {
      if (aCurrent instanceof final String sCurrent)
      {
        int nBegin = 0;
        if (!aFactory.patternMatchesBeginOfLine () || bBeginOfLine)
        {
          final Matcher aMatcher = aFactory.getPattern ().matcher (sCurrent);
          while (aMatcher.find ())
          {
            final String sPart = sCurrent.substring (nBegin, aMatcher.start ());
            nBegin = aMatcher.end ();

            if (!sPart.isEmpty ())
            {
              aOut.add (aFactory.unescape (sPart));
            }

            aOut.add (aFactory.createToken (sCurrent, aMatcher));
          }
        }

        if (nBegin < sCurrent.length ())
        {
          aOut.add (aFactory.unescape (sCurrent.substring (nBegin)));
        }
      }
      else
      {
        aOut.add (aCurrent);
      }
      // begin-of-line tracking: any non-NEWLINE token resets to false
      if (aCurrent instanceof IPLMarkupToken.NewLine)
        bBeginOfLine = true;
      else
        bBeginOfLine = false;
    }
    return aOut;
  }
}
