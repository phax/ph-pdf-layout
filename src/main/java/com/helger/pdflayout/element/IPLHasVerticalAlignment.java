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
package com.helger.pdflayout.element;

import javax.annotation.Nonnull;

import com.helger.pdflayout.spec.EVertAlignment;

/**
 * Base interface for objects with a vertical alignment
 * 
 * @author Philip Helger
 */
public interface IPLHasVerticalAlignment <IMPLTYPE extends IPLHasVerticalAlignment <IMPLTYPE>>
{
  /**
   * @return The vertical alignment of this element. By default it is
   *         {@link EVertAlignment#DEFAULT}. Never <code>null</code>.
   */
  @Nonnull
  EVertAlignment getVertAlign ();

  /**
   * Set the vertical alignment of this element.
   * 
   * @param eVertAlign
   *        The new vertical alignment. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setVertAlign (@Nonnull EVertAlignment eVertAlign);
}
