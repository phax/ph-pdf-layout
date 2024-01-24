/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.traits.IGenericImplTrait;

/**
 * Base interface for objects with a fill color
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasFillColor <IMPLTYPE extends IPLHasFillColor <IMPLTYPE>> extends IGenericImplTrait <IMPLTYPE>
{
  /**
   * By default nothing is filled, so the default value is <code>null</code>.
   */
  PLColor DEFAULT_FILL_COLOR = null;

  /**
   * Set the element fill color.
   *
   * @param aFillColor
   *        The fill color to use. May be <code>null</code>.
   * @return this
   * @since 7.2.0
   */
  @Nonnull
  IMPLTYPE setFillColor (@Nullable PLColor aFillColor);

  /**
   * @return The current fill color. May be <code>null</code>.
   */
  @Nullable
  PLColor getFillColor ();

  default boolean hasFillColor ()
  {
    return getFillColor () != null;
  }
}
