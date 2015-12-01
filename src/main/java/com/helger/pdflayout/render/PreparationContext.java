/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.render;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * The current context for preparing an element.
 *
 * @author Philip Helger
 */
@Immutable
public final class PreparationContext
{
  private final PDDocument m_aDoc;
  private final float m_fAvailableWidth;
  private final float m_fAvailableHeight;

  /**
   * Constructor
   *
   * @param aDoc
   *        The {@link PDDocument} worked upon
   * @param fAvailableWidth
   *        The available width for an element, without the element's margin and
   *        padding. Should be &gt; 0.
   * @param fAvailableHeight
   *        The available height for an element, without the element's margin
   *        and padding. Should be &gt; 0.
   */
  public PreparationContext (@Nonnull final PDDocument aDoc, @Nonnegative final float fAvailableWidth, @Nonnegative final float fAvailableHeight)
  {
    ValueEnforcer.notNull (aDoc, "PDDocument");
    ValueEnforcer.isGE0 (fAvailableWidth, "AvailableWidth");
    ValueEnforcer.isGE0 (fAvailableHeight, "AvailableHeight");
    m_aDoc = aDoc;
    m_fAvailableWidth = fAvailableWidth;
    m_fAvailableHeight = fAvailableHeight;
  }

  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDoc;
  }

  /**
   * @return The available width for an element, without the element's margin
   *         and padding. Should be &gt; 0.
   */
  public float getAvailableWidth ()
  {
    return m_fAvailableWidth;
  }

  /**
   * @return The available height for an element, without the element's margin
   *         and padding. Should be &gt; 0.
   */
  public float getAvailableHeight ()
  {
    return m_fAvailableHeight;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("availableWidth", m_fAvailableWidth).append ("availableHeight", m_fAvailableHeight).toString ();
  }
}
