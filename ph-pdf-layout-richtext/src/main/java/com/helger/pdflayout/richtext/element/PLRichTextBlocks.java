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
package com.helger.pdflayout.richtext.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.IPLElement;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.run.PLFontFamily;

/**
 * Block-level companion to {@link PLRichText}. Parses a markup string that may contain line-prefix
 * indent markers and turns it into a list of {@link IPLElement}s, one per paragraph or list item.
 * The intended use is to add the returned elements directly to a
 * {@link com.helger.pdflayout.base.PLPageSet}.
 * <p>
 * Recognised line-prefix markers (verbatim from the original {@code rst.pdfbox.layout} library):
 * </p>
 * <ul>
 * <li>{@code --} — plain indent block (no label)</li>
 * <li>{@code -+} — bullet list item (default bullet {@code •})</li>
 * <li>{@code -#} — numbered list item (default arabic, restarts after a reset or whenever the
 * markup switches away from {@code -#})</li>
 * <li>{@code -!} — reset / end indentation block; produces no element</li>
 * <li>Leading spaces before the marker increase the indent level by one each. Maximum supported
 * depth is 3 levels.</li>
 * </ul>
 * <p>
 * Each block's inline content is itself markup and is rendered via
 * {@link PLRichText#createFromMarkup}, so bold/italic/colour/sub-superscript markup remain
 * available inside the block.
 * </p>
 * <p>
 * This is intentionally a "structurally equivalent" port of the original indentation feature —
 * bullets render as bullets, numbers as numbers, indentation is applied via {@code marginLeft} on
 * the produced element — but the pixel-perfect appearance of the original is not reproduced (no
 * right-aligned label cells, no per-level bullet glyph swap).
 * </p>
 *
 * @author Philip Helger
 */
@Immutable
public final class PLRichTextBlocks
{
  private enum EIndentKind
  {
    PLAIN,
    BULLET,
    NUMBERED,
    RESET
  }

  @Immutable
  private static final class IndentMarker
  {
    final EIndentKind m_eKind;
    final int m_nLevel;
    final String m_sContent;

    IndentMarker (@NonNull final EIndentKind eKind, final int nLevel, @NonNull final String sContent)
    {
      m_eKind = eKind;
      m_nLevel = nLevel;
      m_sContent = sContent;
    }
  }

  /** Approximate "em" multiplier per indentation level. */
  public static final float DEFAULT_INDENT_EM = 1.5f;
  /** Maximum supported indentation depth. */
  public static final int MAX_LEVEL = 3;
  /** Default bullet character for {@code -+} items. */
  public static final char DEFAULT_BULLET = '•';

  private static final Pattern INDENT_PATTERN = Pattern.compile ("^( *)(--|-\\+|-#|-!)(\\{([^}]*)\\})?");

  private PLRichTextBlocks ()
  {}

  @Nullable
  private static IndentMarker _parseIndentMarker (@NonNull final String sLine)
  {
    final Matcher aMatcher = INDENT_PATTERN.matcher (sLine);
    if (!aMatcher.find ())
      return null;

    final int nLeadingSpaces = aMatcher.group (1).length ();
    final String sMarker = aMatcher.group (2);
    final int nLevel = nLeadingSpaces + 1;
    final EIndentKind eKind = switch (sMarker)
    {
      case "--" -> EIndentKind.PLAIN;
      case "-+" -> EIndentKind.BULLET;
      case "-#" -> EIndentKind.NUMBERED;
      case "-!" -> EIndentKind.RESET;
      default -> EIndentKind.RESET;
    };

    final String sContent = sLine.substring (aMatcher.end ());
    return new IndentMarker (eKind, nLevel, sContent);
  }

  private static void _addItem (@NonNull final ICommonsList <IPLElement <?>> aResult,
                                @NonNull final IndentMarker aMarker,
                                @NonNull final String sPrefix,
                                @NonNull final PLFontFamily aFontFamily,
                                final float fFontSize,
                                @NonNull final PLColor aDefaultColor)
  {
    final int nLevel = Math.min (Math.max (1, aMarker.m_nLevel), MAX_LEVEL);
    final float fMarginLeft = nLevel * DEFAULT_INDENT_EM * fFontSize;
    final String sContent = sPrefix + aMarker.m_sContent;
    final PLRichText aRT = PLRichText.createFromMarkup (sContent, aFontFamily, fFontSize, aDefaultColor);
    aRT.setMarginLeft (fMarginLeft);
    aResult.add (aRT);
  }

  private static void _flushParagraph (@NonNull final ICommonsList <IPLElement <?>> aResult,
                                       @NonNull final StringBuilder aPara,
                                       @NonNull final PLFontFamily aFontFamily,
                                       final float fFontSize,
                                       @NonNull final PLColor aDefaultColor)
  {
    if (aPara.length () == 0)
      return;
    aResult.add (PLRichText.createFromMarkup (aPara.toString (), aFontFamily, fFontSize, aDefaultColor));
    aPara.setLength (0);
  }

  /**
   * Parse a markup string into a list of block-level elements. The result can be added one-by-one
   * to a {@link com.helger.pdflayout.base.PLPageSet}.
   *
   * @param sMarkup
   *        the markup string, possibly with line-prefix indent markers.
   * @param aFontFamily
   *        the font family used for each {@link PLRichText}.
   * @param fFontSize
   *        the font size used for each {@link PLRichText}.
   * @param aDefaultColor
   *        the default text colour.
   * @return the list of block elements; may be empty but never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <IPLElement <?>> parseMarkup (@NonNull final String sMarkup,
                                                           @NonNull final PLFontFamily aFontFamily,
                                                           final float fFontSize,
                                                           @NonNull final PLColor aDefaultColor)
  {
    ValueEnforcer.notNull (sMarkup, "Markup");
    ValueEnforcer.notNull (aFontFamily, "FontFamily");
    ValueEnforcer.isGT0 (fFontSize, "FontSize");
    ValueEnforcer.notNull (aDefaultColor, "DefaultColor");

    final ICommonsList <IPLElement <?>> aResult = new CommonsArrayList <> ();
    final String [] aLines = sMarkup.split ("\n", -1);

    final StringBuilder aPara = new StringBuilder ();
    int nArabicCounter = 0;
    boolean bLastWasNumbered = false;

    for (final String sLine : aLines)
    {
      final IndentMarker aMarker = _parseIndentMarker (sLine);
      if (aMarker == null)
      {
        // Plain text - accumulate into paragraph buffer.
        if (aPara.length () > 0)
          aPara.append ('\n');
        aPara.append (sLine);
        continue;
      }

      // Block boundary: flush any accumulated paragraph first.
      _flushParagraph (aResult, aPara, aFontFamily, fFontSize, aDefaultColor);

      // Reset numbered counter when leaving a -# run.
      if (aMarker.m_eKind != EIndentKind.NUMBERED)
      {
        nArabicCounter = 0;
        bLastWasNumbered = false;
      }

      switch (aMarker.m_eKind)
      {
        case RESET:
          // Reset only - no element emitted.
          nArabicCounter = 0;
          bLastWasNumbered = false;
          break;
        case PLAIN:
          _addItem (aResult, aMarker, "", aFontFamily, fFontSize, aDefaultColor);
          break;
        case BULLET:
          _addItem (aResult, aMarker, DEFAULT_BULLET + " ", aFontFamily, fFontSize, aDefaultColor);
          break;
        case NUMBERED:
          if (!bLastWasNumbered)
            nArabicCounter = 0;
          nArabicCounter++;
          bLastWasNumbered = true;
          _addItem (aResult, aMarker, nArabicCounter + ". ", aFontFamily, fFontSize, aDefaultColor);
          break;
      }
    }

    // Trailing paragraph buffer.
    _flushParagraph (aResult, aPara, aFontFamily, fFontSize, aDefaultColor);

    return aResult;
  }
}
