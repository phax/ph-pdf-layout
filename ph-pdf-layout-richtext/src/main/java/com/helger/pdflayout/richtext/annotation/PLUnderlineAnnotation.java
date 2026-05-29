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
package com.helger.pdflayout.richtext.annotation;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Underline annotation for rich text. Draws a horizontal line below the annotated run at render
 * time. The baseline offset is given as a multiplier of the font size (negative values move the
 * line below the baseline) and the line weight scales relative to the font size.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLUnderlineAnnotation implements IPLRichTextAnnotation
{
  /** Default baseline offset scale: slightly below the text baseline. */
  public static final float DEFAULT_BASELINE_OFFSET_SCALE = -0.1f;
  /** Default underline weight as a multiplier of the font size. */
  public static final float DEFAULT_LINE_WEIGHT = 1f;

  private final float m_fBaselineOffsetScale;
  private final float m_fLineWeight;

  public PLUnderlineAnnotation ()
  {
    this (DEFAULT_BASELINE_OFFSET_SCALE, DEFAULT_LINE_WEIGHT);
  }

  public PLUnderlineAnnotation (final float fBaselineOffsetScale, final float fLineWeight)
  {
    m_fBaselineOffsetScale = fBaselineOffsetScale;
    m_fLineWeight = fLineWeight;
  }

  public float getBaselineOffsetScale ()
  {
    return m_fBaselineOffsetScale;
  }

  public float getLineWeight ()
  {
    return m_fLineWeight;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("BaselineOffsetScale", m_fBaselineOffsetScale)
                                       .append ("LineWeight", m_fLineWeight)
                                       .getToString ();
  }
}
