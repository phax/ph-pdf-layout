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

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

/**
 * Base interface for a splittable element
 * 
 * @author Philip Helger
 */
public interface IPLSplittableElement
{
  /**
   * Split this element into sub-elements according to the available height.
   * Splitting is always done after preparation and must return prepared
   * objects!
   * 
   * @param fElementWidth
   *        The element width without padding or margin of the element.
   * @param fAvailableHeight
   *        The available height without y-padding and y-margin of this element.
   *        Must be &ge; 0.
   * @return <code>null</code> if splitting makes no sense.
   */
  @Nullable
  PLSplitResult splitElements (@Nonnegative float fElementWidth, @Nonnegative float fAvailableHeight);
}
