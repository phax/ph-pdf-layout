/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.base;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base interface for a splittable element
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLSplittableObject <IMPLTYPE extends IPLSplittableObject <IMPLTYPE>> extends IPLObject <IMPLTYPE>
{
  boolean DEFAULT_VERT_SPLITTABLE = true;

  /**
   * Create a new object of the same type as this object.
   * 
   * @param aBase
   *        The source object to copy data from.
   * @return Never <code>null</code>.
   */
  @Nonnull
  IMPLTYPE internalCreateNewVertSplitObject (@Nonnull IMPLTYPE aBase);

  /**
   * @return <code>true</code> if this element is vertically splittable,
   *         <code>false</code> otherwise. The default is
   *         {@link #DEFAULT_VERT_SPLITTABLE}.
   */
  boolean isVertSplittable ();

  /**
   * Split this element vertically into sub-elements according to the available
   * height. Splitting is always done after preparation and must return prepared
   * objects!
   *
   * @param fAvailableWidth
   *        The available width without outline of the element.
   * @param fAvailableHeight
   *        The available height without outline of this element. Must be &ge;
   *        0.
   * @return <code>null</code> if splitting makes no sense.
   */
  @Nullable
  PLSplitResult splitElementVert (@Nonnegative float fAvailableWidth, @Nonnegative float fAvailableHeight);
}
