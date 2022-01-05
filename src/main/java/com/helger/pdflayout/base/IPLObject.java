/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Base interface for a PDF layout object
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLObject <IMPLTYPE extends IPLObject <IMPLTYPE>> extends IHasID <String>, IGenericImplTrait <IMPLTYPE>, IPLVisitable
{
  default boolean hasID (@Nullable final String sID)
  {
    return getID ().equals (sID);
  }

  /**
   * @return The debug ID of this element. Neither <code>null</code> nor empty.
   *         The debug ID is usually automatically created automatically from
   *         the ID.
   */
  @Nonnull
  @Nonempty
  String getDebugID ();

  /**
   * @return <code>true</code> if this element is splittable, <code>false</code>
   *         otherwise.
   */
  default boolean isVertSplittable ()
  {
    return this instanceof IPLSplittableObject <?, ?>;
  }

  /**
   * @return this as an {@link IPLSplittableObject} - ensure to call it only if
   *         {@link #isVertSplittable()} returns <code>true</code>.
   */
  @Nonnull
  default IPLSplittableObject <?, ?> getAsSplittable ()
  {
    return (IPLSplittableObject <?, ?>) this;
  }

  /**
   * For copying stuff internally. Must always call super method
   *
   * @param aSource
   *        Source object to copy from. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  IMPLTYPE setBasicDataFrom (@Nonnull IMPLTYPE aSource);
}
