/**
 * Copyright (C) 2014 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
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

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.string.ToStringGenerator;

/**
 * The current context for preparing an element.
 * 
 * @author Philip Helger
 */
@Immutable
public final class PreparationContext
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PreparationContext.class);

  private final float m_fAvailableWidth;
  private final float m_fAvailableHeight;

  /**
   * Constructor
   * 
   * @param fAvailableWidth
   *        The available width for an element, without the element's margin and
   *        padding. Should be &gt; 0.
   * @param fAvailableHeight
   *        The available height for an element, without the element's margin
   *        and padding. Should be &gt; 0.
   */
  public PreparationContext (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableWidth < 0)
      s_aLogger.warn ("RenderPreparationContext: available width is too small: " + fAvailableWidth);
    if (fAvailableHeight < 0)
      s_aLogger.warn ("RenderPreparationContext: available height is too small: " + fAvailableHeight);
    m_fAvailableWidth = fAvailableWidth;
    m_fAvailableHeight = fAvailableHeight;
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
    return new ToStringGenerator (this).append ("availableWidth", m_fAvailableWidth)
                                       .append ("availableHeight", m_fAvailableHeight)
                                       .toString ();
  }
}
