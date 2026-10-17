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
 * Strikethrough annotation for rich text. Draws a horizontal line through the annotated run at
 * render time. The baseline offset is given as a multiplier of the font size (positive values move
 * the line above the baseline, so the default puts it roughly through the middle of the lower case
 * letters) and the line weight scales relative to the font size.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLStrikethroughAnnotation implements IPLRichTextAnnotation
{
  /** Default baseline offset scale: through the middle of the lower case letters. */
  public static final float DEFAULT_BASELINE_OFFSET_SCALE = 0.3f;
  /** Default strikethrough weight as a multiplier of the font size. */
  public static final float DEFAULT_LINE_WEIGHT = 1f;

  private final float m_fBaselineOffsetScale;
  private final float m_fLineWeight;

  /** Create a strikethrough with the default position and weight. */
  public PLStrikethroughAnnotation ()
  {
    this (DEFAULT_BASELINE_OFFSET_SCALE, DEFAULT_LINE_WEIGHT);
  }

  /**
   * Create a strikethrough with a custom position and weight.
   *
   * @param fBaselineOffsetScale
   *        The distance of the line from the baseline as a multiplier of the font size. Positive
   *        values are above the baseline.
   * @param fLineWeight
   *        The weight of the line as a multiplier of the font size.
   */
  public PLStrikethroughAnnotation (final float fBaselineOffsetScale, final float fLineWeight)
  {
    m_fBaselineOffsetScale = fBaselineOffsetScale;
    m_fLineWeight = fLineWeight;
  }

  /**
   * @return The distance of the line from the baseline as a multiplier of the font size.
   */
  public float getBaselineOffsetScale ()
  {
    return m_fBaselineOffsetScale;
  }

  /**
   * @return The weight of the line as a multiplier of the font size.
   */
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
