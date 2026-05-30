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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;

/**
 * Background-color annotation for rich text. Fills a rectangle behind the annotated run before the
 * glyphs are emitted. The vertical extent of the rectangle is selected via
 * {@link EPLBackgroundExtent}.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLBackgroundAnnotation implements IPLRichTextAnnotation
{
  /** Default vertical extent: {@link EPLBackgroundExtent#TIGHT}. */
  public static final EPLBackgroundExtent DEFAULT_EXTENT = EPLBackgroundExtent.TIGHT;

  private final PLColor m_aColor;
  private final EPLBackgroundExtent m_eExtent;

  public PLBackgroundAnnotation (@NonNull final PLColor aColor)
  {
    this (aColor, DEFAULT_EXTENT);
  }

  public PLBackgroundAnnotation (@NonNull final PLColor aColor, @NonNull final EPLBackgroundExtent eExtent)
  {
    ValueEnforcer.notNull (aColor, "Color");
    ValueEnforcer.notNull (eExtent, "Extent");
    m_aColor = aColor;
    m_eExtent = eExtent;
  }

  @NonNull
  public PLColor getColor ()
  {
    return m_aColor;
  }

  @NonNull
  public EPLBackgroundExtent getExtent ()
  {
    return m_eExtent;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Color", m_aColor).append ("Extent", m_eExtent).getToString ();
  }
}
