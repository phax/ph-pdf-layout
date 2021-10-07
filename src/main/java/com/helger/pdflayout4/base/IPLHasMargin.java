/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import com.helger.commons.traits.IGenericImplTrait;
import com.helger.pdflayout4.spec.MarginSpec;

/**
 * Base interface for objects having a margin
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasMargin <IMPLTYPE extends IPLHasMargin <IMPLTYPE>> extends IPLHasOutline, IGenericImplTrait <IMPLTYPE>
{
  MarginSpec DEFAULT_MARGIN = MarginSpec.MARGIN0;

  /**
   * Set all margin values (left, top, right, bottom) to the same value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMargin (final float fMargin)
  {
    return setMargin (fMargin, fMargin);
  }

  /**
   * Set all margin values.
   *
   * @param fMarginY
   *        The Y-value to use (for top and bottom).
   * @param fMarginX
   *        The X-value to use (for left and right).
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMargin (final float fMarginY, final float fMarginX)
  {
    return setMargin (fMarginY, fMarginX, fMarginY, fMarginX);
  }

  /**
   * Set all margin values to potentially different values.
   *
   * @param fMarginTop
   *        Top
   * @param fMarginRight
   *        Right
   * @param fMarginBottom
   *        Bottom
   * @param fMarginLeft
   *        Left
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMargin (final float fMarginTop, final float fMarginRight, final float fMarginBottom, final float fMarginLeft)
  {
    return setMargin (new MarginSpec (fMarginTop, fMarginRight, fMarginBottom, fMarginLeft));
  }

  /**
   * Set the margin values.
   *
   * @param aMargin
   *        Margin to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setMargin (@Nonnull MarginSpec aMargin);

  /**
   * Set the top margin value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMarginTop (final float fMargin)
  {
    return setMargin (getMargin ().getCloneWithTop (fMargin));
  }

  /**
   * Set the right margin value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMarginRight (final float fMargin)
  {
    return setMargin (getMargin ().getCloneWithRight (fMargin));
  }

  /**
   * Set the bottom margin value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMarginBottom (final float fMargin)
  {
    return setMargin (getMargin ().getCloneWithBottom (fMargin));
  }

  /**
   * Set the left margin value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setMarginLeft (final float fMargin)
  {
    return setMargin (getMargin ().getCloneWithLeft (fMargin));
  }

  /**
   * Set the left margin left AND right value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE setMarginX (final float fMargin)
  {
    return setMarginLeft (fMargin).setMarginRight (fMargin);
  }

  /**
   * Set the left margin top AND bottom value.
   *
   * @param fMargin
   *        The value to use.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE setMarginY (final float fMargin)
  {
    return setMarginTop (fMargin).setMarginBottom (fMargin);
  }

  /**
   * Add to the top margin value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addMarginTop (final float fMargin)
  {
    if (fMargin == 0f)
      return thisAsT ();
    return setMarginTop (getMarginTop () + fMargin);
  }

  /**
   * Add to the right margin value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addMarginRight (final float fMargin)
  {
    if (fMargin == 0f)
      return thisAsT ();
    return setMarginRight (getMarginRight () + fMargin);
  }

  /**
   * Add to the bottom margin value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addMarginBottom (final float fMargin)
  {
    if (fMargin == 0f)
      return thisAsT ();
    return setMarginBottom (getMarginBottom () + fMargin);
  }

  /**
   * Add to the left margin value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addMarginLeft (final float fMargin)
  {
    if (fMargin == 0f)
      return thisAsT ();
    return setMarginLeft (getMarginLeft () + fMargin);
  }

  /**
   * Add to the left margin left AND right value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE addMarginX (final float fMargin)
  {
    return addMarginLeft (fMargin).addMarginRight (fMargin);
  }

  /**
   * Add to the left margin top AND bottom value.
   *
   * @param fMargin
   *        The value to add.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE addMarginY (final float fMargin)
  {
    return addMarginTop (fMargin).addMarginBottom (fMargin);
  }

  /**
   * @return The current margin. Never <code>null</code>.
   */
  @Nonnull
  MarginSpec getMargin ();

  /**
   * @return The current top margin.
   */
  default float getMarginTop ()
  {
    return getMargin ().getTop ();
  }

  /**
   * @return The current right margin.
   */
  default float getMarginRight ()
  {
    return getMargin ().getRight ();
  }

  /**
   * @return The current bottom margin.
   */
  default float getMarginBottom ()
  {
    return getMargin ().getBottom ();
  }

  /**
   * @return The current left margin.
   */
  default float getMarginLeft ()
  {
    return getMargin ().getLeft ();
  }

  /**
   * @return The sum of left and right margin.
   */
  default float getMarginXSum ()
  {
    return getMargin ().getXSum ();
  }

  /**
   * @return The sum of top and bottom margin.
   */
  default float getMarginYSum ()
  {
    return getMargin ().getYSum ();
  }

  @Override
  default float getOutlineTop ()
  {
    return getMarginTop ();
  }

  @Override
  default float getOutlineRight ()
  {
    return getMarginRight ();
  }

  @Override
  default float getOutlineBottom ()
  {
    return getMarginBottom ();
  }

  @Override
  default float getOutlineLeft ()
  {
    return getMarginLeft ();
  }

  @Override
  default float getOutlineXSum ()
  {
    return getMarginXSum ();
  }

  @Override
  default float getOutlineYSum ()
  {
    return getMarginYSum ();
  }
}
