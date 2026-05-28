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
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;

import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.EPLLinkStyle;
import com.helger.pdflayout.richtext.annotation.PLAnchorAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;

/**
 * Catalog of all built-in markup character factories used by
 * {@link PLMarkupParser}. The set is intentionally a fixed collection — anyone
 * needing additional markers can pass extra factories to the parser
 * constructor.
 *
 * <p>Recognised markup:</p>
 * <ul>
 * <li>{@code *bold*} — toggles bold</li>
 * <li>{@code _italic_} — toggles italic</li>
 * <li>{@code __text__} — toggles underline</li>
 * <li>{@code {color:#rrggbb}} — switches the current color</li>
 * <li>{@code {link:ul[http://example.com]}} — wraps text in a hyperlink. The
 * style (<code>ul</code> = underline, <code>none</code> = no decoration) is
 * optional and defaults to underline.</li>
 * <li>{@code {anchor:name}} — declares a named anchor as a link target</li>
 * <li>Backslash before any marker escapes it ({@code \*} renders a literal {@code *}).</li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class PLMarkupCharacters
{
  /** Factory for the bold marker {@code *}. */
  public static final IPLMarkupCharacterFactory BOLD = new ToggleFactory ("(?<!\\\\)(\\\\\\\\)*\\*",
                                                                          "*",
                                                                          IPLMarkupToken.BoldToggle.INSTANCE);

  /** Factory for the italic marker {@code _} (but not the double-underscore underline). */
  public static final IPLMarkupCharacterFactory ITALIC = new ToggleFactory ("(?<!\\\\)(\\\\\\\\)*(?<!_)_(?!_)",
                                                                            "_",
                                                                            IPLMarkupToken.ItalicToggle.INSTANCE);

  /** Factory for the newline marker {@code \n} or {@code \r\n}. */
  public static final IPLMarkupCharacterFactory NEWLINE = new NewLineFactory ();

  /** Factory for the color marker {@code {color:#rrggbb}}. */
  public static final IPLMarkupCharacterFactory COLOR = new ColorFactory ();

  /** Factory for the underline marker {@code __}. */
  public static final IPLMarkupCharacterFactory UNDERLINE = new UnderlineFactory ();

  /** Factory for the hyperlink marker {@code {link:style[uri]}}. */
  public static final IPLMarkupCharacterFactory HYPERLINK = new HyperlinkFactory ();

  /** Factory for the anchor marker {@code {anchor:name}}. */
  public static final IPLMarkupCharacterFactory ANCHOR = new AnchorFactory ();

  private PLMarkupCharacters ()
  {}

  // ---------------------------------------------------------------------------

  private static class ToggleFactory implements IPLMarkupCharacterFactory
  {
    private final Pattern m_aPattern;
    private final String m_sMarker;
    private final IPLMarkupToken m_aToken;

    ToggleFactory (final String sPattern, final String sMarker, final IPLMarkupToken aToken)
    {
      m_aPattern = Pattern.compile (sPattern);
      m_sMarker = sMarker;
      m_aToken = aToken;
    }

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return m_aPattern;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      return m_aToken;
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (m_sMarker), m_sMarker);
    }
  }

  // ---------------------------------------------------------------------------

  private static final class NewLineFactory implements IPLMarkupCharacterFactory
  {
    private static final Pattern PATTERN = Pattern.compile ("(\r\n|\n)");

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      return IPLMarkupToken.NewLine.INSTANCE;
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText;
    }
  }

  // ---------------------------------------------------------------------------

  private static final class ColorFactory implements IPLMarkupCharacterFactory
  {
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{color:#(\\p{XDigit}{6})\\}");
    private static final String MARKER = "{";

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      final String sHex = aMatcher.group (2);
      final int nR = Integer.parseUnsignedInt (sHex.substring (0, 2), 16);
      final int nG = Integer.parseUnsignedInt (sHex.substring (2, 4), 16);
      final int nB = Integer.parseUnsignedInt (sHex.substring (4, 6), 16);
      return new IPLMarkupToken.Color (new PLColor (nR, nG, nB));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }
  }

  // ---------------------------------------------------------------------------

  private static final class UnderlineFactory implements IPLMarkupCharacterFactory
  {
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*(__(\\{(-?\\d+(\\.\\d*)?)?\\:(-?\\d+(\\.\\d*)?)?\\})?)");
    private static final String MARKER = "__";

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      final float fBaselineOffsetScale = _parseFloat (aMatcher.group (4),
                                                      PLUnderlineAnnotation.DEFAULT_BASELINE_OFFSET_SCALE);
      final float fLineWeight = _parseFloat (aMatcher.group (6), PLUnderlineAnnotation.DEFAULT_LINE_WEIGHT);
      return new IPLMarkupToken.AnnotationToggle (new PLUnderlineAnnotation (fBaselineOffsetScale, fLineWeight));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }

    private static float _parseFloat (final String sValue, final float fDefault)
    {
      if (sValue == null)
        return fDefault;
      try
      {
        return Float.parseFloat (sValue);
      }
      catch (final NumberFormatException ignore)
      {
        return fDefault;
      }
    }
  }

  // ---------------------------------------------------------------------------

  private static final class HyperlinkFactory implements IPLMarkupCharacterFactory
  {
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{link(:(ul|none))?(\\[(([^}]+))\\])?\\}");
    private static final String MARKER = "{";

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      final String sUri = aMatcher.group (5);
      final String sStyle = aMatcher.group (3);
      final EPLLinkStyle eLinkStyle = "none".equals (sStyle) ? EPLLinkStyle.NONE : EPLLinkStyle.UNDERLINE;
      if (sUri == null)
      {
        // closing marker — emit a "neutral" toggle (matched against active annotation by type)
        return new IPLMarkupToken.AnnotationToggle (new PLHyperlinkAnnotation ("#close", eLinkStyle));
      }
      return new IPLMarkupToken.AnnotationToggle (new PLHyperlinkAnnotation (sUri, eLinkStyle));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }
  }

  // ---------------------------------------------------------------------------

  private static final class AnchorFactory implements IPLMarkupCharacterFactory
  {
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{anchor(:((\\w+)))?\\}");
    private static final String MARKER = "{";

    @Override
    @NonNull
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    @NonNull
    public IPLMarkupToken createToken (@NonNull final String sText, @NonNull final Matcher aMatcher)
    {
      final String sName = aMatcher.group (3);
      if (sName == null)
      {
        // closing marker
        return new IPLMarkupToken.AnnotationToggle (new PLAnchorAnnotation ("__close__"));
      }
      return new IPLMarkupToken.AnnotationToggle (new PLAnchorAnnotation (sName));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }
  }
}
