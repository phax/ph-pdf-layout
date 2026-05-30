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

import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.EPLBackgroundExtent;
import com.helger.pdflayout.richtext.annotation.EPLLinkStyle;
import com.helger.pdflayout.richtext.color.PLCMYKColor;
import com.helger.pdflayout.richtext.annotation.PLAnchorAnnotation;
import com.helger.pdflayout.richtext.annotation.PLBackgroundAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;

/**
 * Catalog of all built-in markup character factories used by
 * {@link PLMarkupParser}. The set is intentionally a fixed collection — anyone
 * needing additional markers can pass extra factories to the parser
 * constructor.
 *
 * <p>Recognised markup (Markdown-style for bold and italic):</p>
 * <ul>
 * <li>{@code **bold**} — toggles bold (CommonMark style; double asterisk)</li>
 * <li>{@code *italic*} or {@code _italic_} — toggles italic (CommonMark style;
 * single asterisk OR single underscore not flanked by another underscore)</li>
 * <li>{@code __text__} — toggles underline. NOTE: this differs from CommonMark
 * where {@code __} is an alternative bold marker — here it is reserved for
 * underline as there is no standard Markdown for underline.</li>
 * <li>{@code ***bold-italic***} — combines bold and italic.</li>
 * <li>{@code {_}sub{_}} — toggles subscript (font scaled, baseline shifted down)</li>
 * <li>{@code {^}sup{^}} — toggles superscript (font scaled, baseline shifted up)</li>
 * <li>{@code {_:0.5|0.2}sub{_}} — subscript with custom font / baseline scale</li>
 * <li>{@code {color:#rrggbb}} — switches the current color</li>
 * <li>{@code {bg:#rrggbb}text{bg}} — fills a background rectangle behind the text.
 * The extent defaults to {@link EPLBackgroundExtent#TIGHT tight} (the segment's
 * own font box); writing {@code {bg:line:#rrggbb}} selects
 * {@link EPLBackgroundExtent#LINE_HEIGHT line-height} (full line slot, contiguous
 * across adjacent segments and across wrapped lines).</li>
 * <li>{@code {link:ul[http://example.com]}} — wraps text in a hyperlink. The
 * style (<code>ul</code> = underline, <code>none</code> = no decoration) is
 * optional and defaults to underline.</li>
 * <li>{@code {anchor:name}} — declares a named anchor as a link target</li>
 * <li>Backslash before any marker escapes it ({@code \*} renders a literal {@code *},
 * {@code \**} renders a literal {@code **}).</li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class PLMarkupCharacters
{
  /**
   * Factory for the bold marker {@code **} (Markdown / CommonMark style).
   * <p>
   * Regex (Java source has every backslash doubled): {@code (?<!\\)(\\\\)*\*\*(?!\*)}
   * <ul>
   * <li>{@code (?<!\\)} — negative lookbehind: not preceded by a single backslash;
   * this is how a literal {@code \**} escapes itself.</li>
   * <li>{@code (\\\\)*} — but ANY number of double-backslashes is fine, so
   * {@code \\**} (a literal backslash followed by an unescaped marker) still
   * matches the marker.</li>
   * <li>{@code \*\*} — the actual double-asterisk marker.</li>
   * <li>{@code (?!\*)} — negative lookahead: NOT followed by a third {@code *}.
   * This makes {@code ***foo***} parse as italic+bold around "foo" because the
   * regex skips the first two {@code *} of the leading run and matches the
   * remaining {@code **}; the leftover {@code *} on each side is later
   * recognised by {@link #ITALIC}.</li>
   * </ul>
   * MUST run before {@link #ITALIC} (a single {@code *}) so {@code **} isn't
   * consumed as two italic toggles.
   */
  public static final IPLMarkupCharacterFactory BOLD = new ToggleFactory ("(?<!\\\\)(\\\\\\\\)*\\*\\*(?!\\*)",
                                                                          "**",
                                                                          IPLMarkupToken.BoldToggle.INSTANCE);

  /**
   * Factory for the italic marker {@code *} (but not the double-asterisk bold).
   * <p>
   * Regex: {@code (?<!\\)(\\\\)*(?<!\*)\*(?!\*)}
   * <ul>
   * <li>{@code (?<!\\)(\\\\)*} — same backslash-escape guard as BOLD.</li>
   * <li>{@code (?<!\*)\*(?!\*)} — a single asterisk NOT flanked by another
   * asterisk on either side; this is how the italic marker dodges the
   * double-asterisk bold marker.</li>
   * </ul>
   */
  public static final IPLMarkupCharacterFactory ITALIC = new ToggleFactory ("(?<!\\\\)(\\\\\\\\)*(?<!\\*)\\*(?!\\*)",
                                                                            "*",
                                                                            IPLMarkupToken.ItalicToggle.INSTANCE);

  /**
   * Factory for the italic alias marker {@code _} (single underscore not
   * flanked by another underscore). Provided so users can use the CommonMark
   * underscore form interchangeably with {@link #ITALIC} ({@code *italic*}).
   * <p>
   * Regex: {@code (?<!\\)(\\\\)*(?<!_)_(?!_)}
   * <ul>
   * <li>{@code (?<!\\)(\\\\)*} — standard backslash-escape guard.</li>
   * <li>{@code (?<!_)_(?!_)} — single underscore NOT flanked by another
   * underscore, so the double-underscore {@link #UNDERLINE} marker (which is
   * NOT bold here, see class-level docs) is left alone.</li>
   * </ul>
   * MUST run after {@link #UNDERLINE} so {@code __} isn't consumed as two
   * italic toggles.
   */
  public static final IPLMarkupCharacterFactory ITALIC_UNDERSCORE = new ToggleFactory ("(?<!\\\\)(\\\\\\\\)*(?<!_)_(?!_)",
                                                                                       "_",
                                                                                       IPLMarkupToken.ItalicToggle.INSTANCE);

  /**
   * Factory for the soft-break marker — a bare {@code \n} or {@code \r\n} that
   * is NOT preceded by a hard-break trigger (see {@link #HARD_BREAK}). The
   * emitted token is {@link IPLMarkupToken.SoftBreak} which the run-builder
   * renders as a single space.
   */
  public static final IPLMarkupCharacterFactory NEWLINE = new NewLineFactory ();

  /**
   * Factory for the hard line break: either two-or-more trailing spaces before
   * the line ending, OR a single backslash before the line ending (CommonMark
   * style). Emits {@link IPLMarkupToken.NewLine}. MUST run before
   * {@link #NEWLINE} so the trailing spaces / backslash are consumed together
   * with the line ending; otherwise NEWLINE would grab the line ending first
   * and the trigger characters would be left as literal text.
   */
  public static final IPLMarkupCharacterFactory HARD_BREAK = new HardBreakFactory ();

  /** Factory for the color marker {@code {color:#rrggbb}}. */
  public static final IPLMarkupCharacterFactory COLOR = new ColorFactory ();

  /**
   * Factory for the CMYK colour marker {@code {color_cmyk:C,M,Y,K}}. The four
   * components are percent values in {@code 0..100} (floats accepted). See
   * <a href="https://github.com/ralfstuckert/pdfbox-layout/issues/94">ralfstuckert/pdfbox-layout#94</a>
   * for the original proposal by Christopher Dargel (vanDarg).
   */
  public static final IPLMarkupCharacterFactory COLOR_CMYK = new ColorCMYKFactory ();

  /** Factory for the underline marker {@code __}. */
  public static final IPLMarkupCharacterFactory UNDERLINE = new UnderlineFactory ();

  /** Factory for the subscript / superscript metrics marker ({@code {_}} / {@code {^}}). */
  public static final IPLMarkupCharacterFactory METRICS = new MetricsFactory ();

  /** Factory for the hyperlink marker {@code {link:style[uri]}}. */
  public static final IPLMarkupCharacterFactory HYPERLINK = new HyperlinkFactory ();

  /** Factory for the anchor marker {@code {anchor:name}}. */
  public static final IPLMarkupCharacterFactory ANCHOR = new AnchorFactory ();

  /** Factory for the background marker {@code {bg:#rrggbb}}…{@code {bg}}. */
  public static final IPLMarkupCharacterFactory BACKGROUND = new BackgroundFactory ();

  private PLMarkupCharacters ()
  {}

  /**
   * A simple "the same single character toggles a style on/off" factory. The
   * same matcher is reused for {@link #BOLD} ({@code *}) and {@link #ITALIC}
   * ({@code _}); only the pattern and the token differ. The token is a
   * singleton — open and close produce the same instance, and the run-builder
   * tracks state by flipping a boolean when it sees the toggle.
   *
   * @author Philip Helger
   */
  private static final class ToggleFactory implements IPLMarkupCharacterFactory
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
      // Replace `\X` with a literal `X` so users can embed the marker as-is in
      // their text. The leading `\\\\` becomes a literal backslash in the
      // regex; combined with Pattern.quote(marker) we match `\*` and emit `*`.
      return sText.replaceAll ("\\\\" + Pattern.quote (m_sMarker), m_sMarker);
    }
  }

  /**
   * Recognises LF and CRLF as a SOFT line-break token. A soft break is
   * rendered as a single space; this mirrors the CommonMark inline-context
   * semantics where bare line endings collapse to a space and word-wrap
   * decides actual layout breaks. For an explicit hard line break the markup
   * must use the {@link HardBreakFactory} trigger (two-or-more trailing
   * spaces or a backslash before the line ending). There is no backslash
   * escape mechanism for the line ending itself — but writing
   * {@code \<newline>} produces a HARD break (handled by
   * {@link HardBreakFactory}).
   *
   * @author Philip Helger
   */
  private static final class NewLineFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(\r\n|\n)</code>
     * <ul>
     * <li><code>\r\n</code> — Windows-style CRLF</li>
     * <li><code>\n</code> — Unix LF</li>
     * </ul>
     * The alternation order matters: CRLF first so we don't first match the LF
     * alone and then leave a stray CR in the text segment.
     */
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
      return IPLMarkupToken.SoftBreak.INSTANCE;
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText;
    }
  }

  /**
   * Recognises a CommonMark hard line break: either two-or-more trailing
   * spaces, or a single backslash, immediately before the line ending.
   * <p>
   * Examples:
   * </p>
   * <ul>
   * <li><code>foo  \n</code> — two trailing spaces — hard break</li>
   * <li><code>foo\\\n</code> — single backslash — hard break</li>
   * <li><code>foo\n</code> — bare newline — SOFT break (handled by
   * {@link NewLineFactory})</li>
   * </ul>
   * The trailing spaces (or backslash) are consumed as part of the match so
   * they do not show up in the rendered output.
   *
   * @author Philip Helger
   */
  private static final class HardBreakFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>( {2,}|(?&lt;!\\)\\)(\r\n|\n)</code>
     * <ul>
     * <li><code>{2,}</code> — two-or-more space characters; OR</li>
     * <li><code>(?&lt;!\\)\\</code> — a single backslash whose immediately
     * preceding character is NOT a backslash, so an escaped backslash
     * (<code>\\</code>) doesn't produce a hard break.</li>
     * <li><code>(\r\n|\n)</code> — the line ending.</li>
     * </ul>
     */
    private static final Pattern PATTERN = Pattern.compile ("( {2,}|(?<!\\\\)\\\\)(\r\n|\n)");

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

  /**
   * Recognises {@code {color:#rrggbb}}. Unlike the style toggles this does NOT
   * flip — every occurrence sets the current colour to whatever was matched.
   * To "reset" to black users must write {@code {color:#000000}} explicitly.
   * The regex requires exactly 6 hex digits via {@code \p{XDigit}{6}}; shorter
   * forms are NOT recognised. The negative lookbehind on {@code \\\\} makes
   * {@code \{color:#...}} a literal escaped marker that is unescaped by
   * {@code unescape(...)} below.
   *
   * @author Philip Helger
   */
  private static final class ColorFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{color:#(\p{XDigit}{6})\}</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — standard escape guard (see
     * {@link #BOLD}).</li>
     * <li><code>\{color:#</code> — literal opening <code>{color:#</code>.</li>
     * <li><code>(\p{XDigit}{6})</code> — group 2: exactly six hex digits,
     * captured for parsing. Shorter forms (e.g. <code>{color:#f00}</code>)
     * intentionally don't match — we require <code>RRGGBB</code>.</li>
     * <li><code>\}</code> — literal closing <code>}</code>.</li>
     * </ul>
     */
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

  /**
   * Recognises {@code __} and the parameterised variant
   * {@code __{offsetScale:lineWeight}}. Underline uses double underscore as
   * there is no standard Markdown for underline; the choice mirrors what
   * Discord and Slack use. The optional parameters allow strike-through-like
   * effects: {@code __{0.25:}foo__} puts the line above mid-height, etc. The
   * produced token wraps a {@link PLUnderlineAnnotation} and toggles by class
   * — see {@code PLRichTextRunBuilder} for the open/close logic.
   *
   * @author Philip Helger
   */
  private static final class UnderlineFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*(__(\{(-?\d+(\.\d*)?)?:(-?\d+(\.\d*)?)?\})?)</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — escape guard (see {@link #BOLD}).</li>
     * <li><code>__</code> — the underline marker proper.</li>
     * <li><code>(\{...\})?</code> — OPTIONAL parameter block, e.g.
     * <code>{0.25:1.5}</code>:
     * <ul>
     * <li><code>(-?\d+(\.\d*)?)?</code> — group 4: optional
     * {@code baselineOffsetScale} (float, may be negative)</li>
     * <li><code>:</code> — literal colon separator</li>
     * <li><code>(-?\d+(\.\d*)?)?</code> — group 6: optional
     * {@code lineWeight} (float, may be negative)</li>
     * </ul>
     * </li>
     * </ul>
     * Either or both numbers may be omitted: <code>{0.25:}</code>,
     * <code>{:1.5}</code>, or just <code>{}</code> are all valid.
     */
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

  /**
   * Recognises subscript {@code {_}} and superscript {@code {^}} and their
   * parameterised forms {@code {_:fontScale|baselineOffsetScale}}. Each toggle
   * emits a {@link IPLMarkupToken.MetricsToggle} token; the builder opens a
   * metrics scope on the first token and closes it when it sees one with the
   * SAME key. The key is built from the marker plus parameters so a bare
   * {@code {_}} matches another bare {@code {_}} (open/close pair) but
   * {@code {_:0.5|0.2}foo{_}} opens with a parameterised key and the bare
   * {@code {_}} close has a different key — this mirrors the original library
   * where the close marker is also always written as bare {@code {_}} /
   * {@code {^}}. The factory MUST run before {@link #HYPERLINK} /
   * {@link #ANCHOR} because all three start with {@code {}; the parser order
   * in {@link PLMarkupParser#DEFAULT_FACTORIES} handles this.
   *
   * @author Philip Helger
   */
  private static final class MetricsFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{(_|\^)(:(-?\d+(\.\d*)?)\|(-?\d+(\.\d*)?))?\}</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — escape guard.</li>
     * <li><code>\{</code> — literal <code>{</code>.</li>
     * <li><code>(_|\^)</code> — group 2: marker character; <code>_</code> =
     * subscript, <code>^</code> = superscript.</li>
     * <li><code>(:...)?</code> — OPTIONAL parameter block:
     * <ul>
     * <li><code>:</code> — literal colon</li>
     * <li><code>(-?\d+(\.\d*)?)</code> — group 4: {@code fontScale} (REQUIRED
     * inside the block)</li>
     * <li><code>\|</code> — literal {@code |} separator</li>
     * <li><code>(-?\d+(\.\d*)?)</code> — group 6:
     * {@code baselineOffsetScale} (REQUIRED inside the block)</li>
     * </ul>
     * </li>
     * <li><code>\}</code> — literal <code>}</code>.</li>
     * </ul>
     * Note that unlike {@link UnderlineFactory}'s parameter block, here both
     * numbers are required once the colon is present — <code>{_:|}</code> or
     * <code>{_:0.5|}</code> do NOT match. The bare close <code>{_}</code> /
     * <code>{^}</code> therefore CAN'T accidentally consume the parameterised
     * form.
     */
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{(_|\\^)(:(-?\\d+(\\.\\d*)?)\\|(-?\\d+(\\.\\d*)?))?\\}");
    private static final String MARKER = "{";

    /** Default font scale for both subscript and superscript. */
    public static final float DEFAULT_FONT_SCALE = 0.61f;
    /** Default baseline offset scale for superscript (shifts text up). */
    public static final float DEFAULT_SUPERSCRIPT_BASELINE_OFFSET_SCALE = -0.4f;
    /** Default baseline offset scale for subscript (shifts text down). */
    public static final float DEFAULT_SUBSCRIPT_BASELINE_OFFSET_SCALE = 0.15f;

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
      final String sMarker = aMatcher.group (2);
      final boolean bIsSuperscript = "^".equals (sMarker);
      final String sFontScale = aMatcher.group (4);
      final String sBaselineOffsetScale = aMatcher.group (6);
      final float fFontScale = _parseFloat (sFontScale, DEFAULT_FONT_SCALE);
      final float fBaselineOffsetScale = _parseFloat (sBaselineOffsetScale,
                                                       bIsSuperscript ? DEFAULT_SUPERSCRIPT_BASELINE_OFFSET_SCALE
                                                                      : DEFAULT_SUBSCRIPT_BASELINE_OFFSET_SCALE);
      // Build a canonical key. Two toggles with the same marker AND the same
      // params are considered an open/close pair. A bare "{_}foo{_}" therefore
      // matches itself; "{_:0.5|0.2}foo{_}" intentionally does NOT match (the
      // open differs from the close) — mirror the original library where the
      // close marker is also a bare "{_}".
      final StringBuilder aKey = new StringBuilder ();
      aKey.append (sMarker);
      if (sFontScale != null || sBaselineOffsetScale != null)
      {
        aKey.append (':');
        aKey.append (sFontScale == null ? "" : sFontScale);
        aKey.append ('|');
        aKey.append (sBaselineOffsetScale == null ? "" : sBaselineOffsetScale);
      }
      return new IPLMarkupToken.MetricsToggle (aKey.toString (), fFontScale, fBaselineOffsetScale);
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

  /**
   * Recognises the opening {@code {link[uri]}} and {@code {link:style[uri]}}
   * (style is {@code ul} for underline-decorated, {@code none} for plain) plus
   * the closing bare {@code {link}}. Both shapes emit an
   * {@link IPLMarkupToken.AnnotationToggle} carrying a
   * {@link PLHyperlinkAnnotation}. Open/close pairing is by ANNOTATION CLASS,
   * not key — so two {@code {link[...]}} with different URIs do NOT nest; the
   * second one simply closes the first. The closing bare {@code {link}}
   * carries a sentinel URI {@code #close}; the builder pops the annotation by
   * type so the sentinel is never actually emitted as a visible link.
   *
   * @author Philip Helger
   */
  private static final class HyperlinkFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{link(:(ul|none))?(\[([^}]+)\])?\}</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — escape guard.</li>
     * <li><code>\{link</code> — literal <code>{link</code>.</li>
     * <li><code>(:(ul|none))?</code> — OPTIONAL style suffix: <code>:ul</code>
     * (default underline, kept for explicitness) or <code>:none</code> (no
     * decoration). group 3 captures the style literal.</li>
     * <li><code>(\[([^}]+)\])?</code> — OPTIONAL URI in square brackets. group
     * 5 captures the URI content; we use <code>[^}]</code> (not <code>[^]]</code>)
     * so that a stray closing brace in the URI ends the marker cleanly. The
     * whole bracket block is optional, which is how the CLOSING marker
     * <code>{link}</code> is recognised — no brackets means "close".</li>
     * <li><code>\}</code> — literal <code>}</code>.</li>
     * </ul>
     */
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

  /**
   * Recognises {@code {anchor:name}} (opens) and bare {@code {anchor}}
   * (closes). The opening token registers a named destination at the current
   * x/y at render time; the closing token bounds the clickable region for
   * tools that want to highlight the anchor itself. Pairing follows the same
   * by-class rule as {@link HyperlinkFactory hyperlinks}; the bare close uses
   * a sentinel name {@code __close__} which is replaced via the builder's
   * type-pop logic.
   *
   * @author Philip Helger
   */
  private static final class AnchorFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{anchor(:(\w+))?\}</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — escape guard.</li>
     * <li><code>\{anchor</code> — literal <code>{anchor</code>.</li>
     * <li><code>(:(\w+))?</code> — OPTIONAL name. <code>:name</code> where
     * name is one or more word characters. group 3 captures the name. Absent
     * bracket → closing marker <code>{anchor}</code>.</li>
     * <li><code>\}</code> — literal <code>}</code>.</li>
     * </ul>
     * Naming is intentionally restrictive ({@code \w+} only) — spaces,
     * hyphens, or colons in the name would make the destination unusable as
     * an anchor-reference URI {@code #name} in hyperlink markup.
     */
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

  /**
   * Recognises the opening {@code {bg:#rrggbb}} and {@code {bg:line:#rrggbb}}
   * (the literal {@code line} selects {@link EPLBackgroundExtent#LINE_HEIGHT};
   * absent defaults to {@link EPLBackgroundExtent#TIGHT}) plus the closing bare
   * {@code {bg}}. Both shapes emit an {@link IPLMarkupToken.AnnotationToggle}
   * carrying a {@link PLBackgroundAnnotation}. Open/close pairing is by
   * ANNOTATION CLASS, not parameter — so two {@code {bg:#...}} with different
   * colours do NOT nest; the second one simply closes the first. The closing
   * bare {@code {bg}} carries a sentinel transparent color; the builder pops
   * the annotation by type so the sentinel is never actually painted.
   *
   * @author Philip Helger
   */
  private static final class BackgroundFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{bg(:(tight|line))?(:#(\p{XDigit}{6}))?\}</code>
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — escape guard.</li>
     * <li><code>\{bg</code> — literal <code>{bg</code>.</li>
     * <li><code>(:(tight|line))?</code> — OPTIONAL extent suffix. group 3
     * captures the extent literal. Absent defaults to
     * {@link EPLBackgroundExtent#TIGHT}.</li>
     * <li><code>(:#(\p{XDigit}{6}))?</code> — OPTIONAL colour. group 5 captures
     * the six hex digits. Absent → CLOSING marker {@code {bg}}.</li>
     * <li><code>\}</code> — literal <code>}</code>.</li>
     * </ul>
     */
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{bg(:(tight|line))?(:#(\\p{XDigit}{6}))?\\}");
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
      final String sExtent = aMatcher.group (3);
      final EPLBackgroundExtent eExtent = "line".equals (sExtent) ? EPLBackgroundExtent.LINE_HEIGHT
                                                                  : EPLBackgroundExtent.TIGHT;
      final String sHex = aMatcher.group (5);
      if (sHex == null)
      {
        // closing marker — emit a "neutral" toggle (matched by type)
        return new IPLMarkupToken.AnnotationToggle (new PLBackgroundAnnotation (PLColor.BLACK, eExtent));
      }
      final int nR = Integer.parseUnsignedInt (sHex.substring (0, 2), 16);
      final int nG = Integer.parseUnsignedInt (sHex.substring (2, 4), 16);
      final int nB = Integer.parseUnsignedInt (sHex.substring (4, 6), 16);
      return new IPLMarkupToken.AnnotationToggle (new PLBackgroundAnnotation (new PLColor (nR, nG, nB), eExtent));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }
  }

  /**
   * Recognises {@code {color_cmyk:C,M,Y,K}}. The four components are percent
   * values in {@code 0..100} (floats accepted). Like {@link ColorFactory} this
   * is a SET, not a toggle — every occurrence replaces the current colour;
   * reset by writing another colour marker (RGB or CMYK).
   * <p>
   * The original request for CMYK markup support comes from
   * <a href="https://github.com/ralfstuckert/pdfbox-layout/issues/94">ralfstuckert/pdfbox-layout#94</a>.
   * We keep the existing {@code {color:#rrggbb}} marker untouched (the issue
   * proposed renaming it to {@code {color_rgb:}} but we intentionally do not).
   *
   * @author Philip Helger
   */
  private static final class ColorCMYKFactory implements IPLMarkupCharacterFactory
  {
    /**
     * Regex: <code>(?&lt;!\\)(\\\\)*\{color_cmyk:N,N,N,N\}</code> where each
     * {@code N} is {@code \d+(\.\d*)?} (a non-negative decimal in
     * {@code 0..100}).
     * <ul>
     * <li><code>(?&lt;!\\)(\\\\)*</code> — standard escape guard.</li>
     * <li><code>\{color_cmyk:</code> — literal opening
     * <code>{color_cmyk:</code>.</li>
     * <li><code>(\d+(\.\d*)?)</code> ×4 — groups 2/4/6/8: the cyan, magenta,
     * yellow and black (key) percentages. The optional fractional groups
     * 3/5/7/9 are not used by the createToken logic.</li>
     * <li><code>,</code> — literal comma separators.</li>
     * <li><code>\}</code> — literal closing <code>}</code>.</li>
     * </ul>
     * Whitespace around the commas is NOT permitted — match the rest of the
     * library where parameter syntax is whitespace-free.
     */
    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{color_cmyk:(\\d+(\\.\\d*)?),(\\d+(\\.\\d*)?),(\\d+(\\.\\d*)?),(\\d+(\\.\\d*)?)\\}");
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
      final float fC = Float.parseFloat (aMatcher.group (2));
      final float fM = Float.parseFloat (aMatcher.group (4));
      final float fY = Float.parseFloat (aMatcher.group (6));
      final float fK = Float.parseFloat (aMatcher.group (8));
      return new IPLMarkupToken.Color (PLCMYKColor.fromPercent (fC, fM, fY, fK));
    }

    @Override
    @NonNull
    public String unescape (@NonNull final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (MARKER), MARKER);
    }
  }
}
