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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.richtext.annotation.IPLRichTextAnnotation;
import com.helger.pdflayout.richtext.run.PLRichTextRun;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;

/**
 * A single rendered segment within a {@link PLRichTextLine}. A segment is a stretch of text from
 * one {@link PLRichTextRun} that fits on the current line. It carries the resolved font information
 * and the precomputed width so that the render phase doesn't have to call into PDFBox metrics.
 *
 * @author Philip Helger
 */
@Immutable
final class PLRichTextSegment
{
  private final String m_sText;
  private final FontSpec m_aFontSpec;
  private final LoadedFont m_aLoadedFont;
  private final float m_fWidth;
  private final ICommonsList <IPLRichTextAnnotation> m_aAnnotations;
  private final float m_fBaselineOffsetScale;

  PLRichTextSegment (@NonNull final String sText,
                     @NonNull final FontSpec aFontSpec,
                     @NonNull final LoadedFont aLoadedFont,
                     final float fWidth,
                     @NonNull final ICommonsList <IPLRichTextAnnotation> aAnnotations)
  {
    this (sText, aFontSpec, aLoadedFont, fWidth, aAnnotations, 0f);
  }

  PLRichTextSegment (@NonNull final String sText,
                     @NonNull final FontSpec aFontSpec,
                     @NonNull final LoadedFont aLoadedFont,
                     final float fWidth,
                     @NonNull final ICommonsList <IPLRichTextAnnotation> aAnnotations,
                     final float fBaselineOffsetScale)
  {
    m_sText = sText;
    m_aFontSpec = aFontSpec;
    m_aLoadedFont = aLoadedFont;
    m_fWidth = fWidth;
    m_aAnnotations = aAnnotations;
    m_fBaselineOffsetScale = fBaselineOffsetScale;
  }

  @NonNull
  String getText ()
  {
    return m_sText;
  }

  @NonNull
  FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @NonNull
  LoadedFont getLoadedFont ()
  {
    return m_aLoadedFont;
  }

  float getWidth ()
  {
    return m_fWidth;
  }

  @NonNull
  @ReturnsMutableObject
  ICommonsList <IPLRichTextAnnotation> annotations ()
  {
    return m_aAnnotations;
  }

  float getBaselineOffsetScale ()
  {
    return m_fBaselineOffsetScale;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Text", m_sText)
                                       .append ("FontSpec", m_aFontSpec)
                                       .append ("Width", m_fWidth)
                                       .append ("Annotations", m_aAnnotations)
                                       .append ("BaselineOffsetScale", m_fBaselineOffsetScale)
                                       .getToString ();
  }
}
