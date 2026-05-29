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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.IPLRichTextAnnotation;

/**
 * Sealed token type emitted by {@link PLMarkupParser}. The parser turns a
 * markup string like {@code "Hello *world*"} into a list of these tokens,
 * which is then walked by the run-builder to produce
 * {@link com.helger.pdflayout.richtext.run.PLRichTextRun}s.
 *
 * @author Philip Helger
 */
public sealed interface IPLMarkupToken permits IPLMarkupToken.Text,
                                               IPLMarkupToken.BoldToggle,
                                               IPLMarkupToken.ItalicToggle,
                                               IPLMarkupToken.Color,
                                               IPLMarkupToken.NewLine,
                                               IPLMarkupToken.AnnotationToggle,
                                               IPLMarkupToken.MetricsToggle
{
  /** Plain text — the actual content. */
  @Immutable
  final class Text implements IPLMarkupToken
  {
    private final String m_sText;

    public Text (@NonNull final String sText)
    {
      ValueEnforcer.notNull (sText, "Text");
      m_sText = sText;
    }

    @NonNull
    public String getText ()
    {
      return m_sText;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Text", m_sText).getToString ();
    }
  }

  /** Toggle bold style on or off (the {@code *} marker). */
  @Immutable
  final class BoldToggle implements IPLMarkupToken
  {
    public static final BoldToggle INSTANCE = new BoldToggle ();

    private BoldToggle ()
    {}

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).getToString ();
    }
  }

  /** Toggle italic style on or off (the {@code _} marker). */
  @Immutable
  final class ItalicToggle implements IPLMarkupToken
  {
    public static final ItalicToggle INSTANCE = new ItalicToggle ();

    private ItalicToggle ()
    {}

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).getToString ();
    }
  }

  /** Switch the current text color (the {@code {color:#xxxxxx}} marker). */
  @Immutable
  final class Color implements IPLMarkupToken
  {
    private final PLColor m_aColor;

    public Color (@NonNull final PLColor aColor)
    {
      ValueEnforcer.notNull (aColor, "Color");
      m_aColor = aColor;
    }

    @NonNull
    public PLColor getColor ()
    {
      return m_aColor;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Color", m_aColor).getToString ();
    }
  }

  /** A new line in the markup ({@code \n} or {@code \r\n}). */
  @Immutable
  final class NewLine implements IPLMarkupToken
  {
    public static final NewLine INSTANCE = new NewLine ();

    private NewLine ()
    {}

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).getToString ();
    }
  }

  /**
   * Toggle an annotation by class (underline, hyperlink, anchor). The first
   * occurrence of a given annotation type pushes the annotation onto the active
   * set; the next occurrence pops it off.
   */
  @Immutable
  final class AnnotationToggle implements IPLMarkupToken
  {
    private final IPLRichTextAnnotation m_aAnnotation;

    public AnnotationToggle (@NonNull final IPLRichTextAnnotation aAnnotation)
    {
      ValueEnforcer.notNull (aAnnotation, "Annotation");
      m_aAnnotation = aAnnotation;
    }

    @NonNull
    public IPLRichTextAnnotation getAnnotation ()
    {
      return m_aAnnotation;
    }

    @NonNull
    public Class <? extends IPLRichTextAnnotation> getAnnotationType ()
    {
      return m_aAnnotation.getClass ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Annotation", m_aAnnotation).getToString ();
    }
  }

  /**
   * Toggle a subscript/superscript metrics scope (the <code>{_}</code> and
   * <code>{^}</code> markers). The same marker both opens and closes the scope —
   * the run-builder compares tokens via {@link #getKey()} to identify the
   * matching close.
   */
  @Immutable
  final class MetricsToggle implements IPLMarkupToken
  {
    private final String m_sKey;
    private final float m_fFontScale;
    private final float m_fBaselineOffsetScale;

    public MetricsToggle (@NonNull final String sKey,
                          final float fFontScale,
                          final float fBaselineOffsetScale)
    {
      ValueEnforcer.notNull (sKey, "Key");
      m_sKey = sKey;
      m_fFontScale = fFontScale;
      m_fBaselineOffsetScale = fBaselineOffsetScale;
    }

    /**
     * @return a canonical key identifying this metrics toggle. Two tokens with
     *         the same key (i.e. the same marker and the same parameter values)
     *         are considered a matching open/close pair.
     */
    @NonNull
    public String getKey ()
    {
      return m_sKey;
    }

    public float getFontScale ()
    {
      return m_fFontScale;
    }

    public float getBaselineOffsetScale ()
    {
      return m_fBaselineOffsetScale;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Key", m_sKey)
                                         .append ("FontScale", m_fFontScale)
                                         .append ("BaselineOffsetScale", m_fBaselineOffsetScale)
                                         .getToString ();
    }
  }
}
