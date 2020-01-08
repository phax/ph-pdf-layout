/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

/**
 * Base interface for block elements. They additionally have a horizontal and
 * vertical alignment.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLBlockElement <IMPLTYPE extends IPLBlockElement <IMPLTYPE>> extends
                                 IPLElement <IMPLTYPE>,
                                 IPLHasHorizontalAlignment <IMPLTYPE>,
                                 IPLHasVerticalAlignment <IMPLTYPE>
{
  boolean DEFAULT_FULL_WIDTH = true;

  /**
   * @return Should the element occupy the full width? The default is
   *         {@link #DEFAULT_FULL_WIDTH}.
   */
  boolean isFullWidth ();

  /**
   * Set usage of full width.
   *
   * @param bFullWidth
   *        <code>true</code> to enable full width, <code>false</code> to use
   *        only what is available.
   * @return this for chaining
   */
  @Nonnull
  IMPLTYPE setFullWidth (final boolean bFullWidth);
}
