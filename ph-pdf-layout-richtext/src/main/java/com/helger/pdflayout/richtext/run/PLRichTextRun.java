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
package com.helger.pdflayout.richtext.run;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.richtext.annotation.IPLRichTextAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;
import com.helger.pdflayout.spec.FontSpec;

/**
 * Immutable container for a single styled run of text inside a
 * {@link com.helger.pdflayout.richtext.element.PLRichText}. A run carries the text itself (without
 * any markup), the font/size/color via a {@link FontSpec} and an arbitrary list of
 * {@link IPLRichTextAnnotation inline annotations} (hyperlink, anchor, underline, ...).
 *
 * @author Philip Helger
 */
@Immutable
public final class PLRichTextRun
{
  private final String m_sText;
  private final FontSpec m_aFontSpec;
  private final ICommonsList <IPLRichTextAnnotation> m_aAnnotations;
  private final float m_fBaselineOffsetScale;

  public PLRichTextRun (@NonNull final String sText, @NonNull final FontSpec aFontSpec)
  {
    this (sText, aFontSpec, null, 0f);
  }

  public PLRichTextRun (@NonNull final String sText,
                        @NonNull final FontSpec aFontSpec,
                        @Nullable final ICommonsList <IPLRichTextAnnotation> aAnnotations)
  {
    this (sText, aFontSpec, aAnnotations, 0f);
  }

  public PLRichTextRun (@NonNull final String sText,
                        @NonNull final FontSpec aFontSpec,
                        @Nullable final ICommonsList <IPLRichTextAnnotation> aAnnotations,
                        final float fBaselineOffsetScale)
  {
    ValueEnforcer.notNull (sText, "Text");
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    m_sText = sText;
    m_aFontSpec = aFontSpec;
    m_aAnnotations = aAnnotations == null ? new CommonsArrayList <> () : new CommonsArrayList <> (aAnnotations);
    m_fBaselineOffsetScale = fBaselineOffsetScale;
  }

  @NonNull
  public String getText ()
  {
    return m_sText;
  }

  @NonNull
  public FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <IPLRichTextAnnotation> getAllAnnotations ()
  {
    return m_aAnnotations.getClone ();
  }

  public boolean hasAnyAnnotation ()
  {
    return m_aAnnotations.isNotEmpty ();
  }

  /**
   * @return the baseline offset scale (used for subscript / superscript). A positive value shifts
   *         the text downward (subscript), a negative one upward (superscript). Zero means no
   *         offset.
   */
  public float getBaselineOffsetScale ()
  {
    return m_fBaselineOffsetScale;
  }

  /**
   * @return the first underline annotation attached to this run, or <code>null</code> if none.
   */
  @Nullable
  public PLUnderlineAnnotation getUnderline ()
  {
    return m_aAnnotations.findFirstMapped (PLUnderlineAnnotation.class::isInstance, PLUnderlineAnnotation.class::cast);
  }

  /**
   * @return the first hyperlink annotation attached to this run, or <code>null</code> if none.
   */
  @Nullable
  public PLHyperlinkAnnotation getHyperlink ()
  {
    return m_aAnnotations.findFirstMapped (PLHyperlinkAnnotation.class::isInstance, PLHyperlinkAnnotation.class::cast);
  }

  /**
   * Creates a new run identical to this one, but with the given text.
   *
   * @param sText
   *        the new text. May not be <code>null</code>.
   * @return a new run.
   */
  @NonNull
  public PLRichTextRun withText (@NonNull final String sText)
  {
    return new PLRichTextRun (sText, m_aFontSpec, m_aAnnotations, m_fBaselineOffsetScale);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Text", m_sText)
                                       .append ("FontSpec", m_aFontSpec)
                                       .append ("Annotations", m_aAnnotations)
                                       .append ("BaselineOffsetScale", m_fBaselineOffsetScale)
                                       .getToString ();
  }
}
