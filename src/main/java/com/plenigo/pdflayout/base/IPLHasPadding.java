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
package com.plenigo.pdflayout.base;

import com.helger.commons.traits.IGenericImplTrait;
import com.plenigo.pdflayout.spec.PaddingSpec;

import javax.annotation.Nonnull;

/**
 * Base interface for objects having a padding
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasPadding <IMPLTYPE extends IPLHasPadding <IMPLTYPE>> extends IPLHasOutline, IGenericImplTrait <IMPLTYPE>
{
  PaddingSpec DEFAULT_PADDING = PaddingSpec.PADDING0;

  /**
   * Set all padding values (left, top, right, bottom) to the same value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPadding (final float fPadding)
  {
    return setPadding (fPadding, fPadding);
  }

  /**
   * Set all padding values.
   *
   * @param fPaddingY
   *        The Y-value to use (for top and bottom).
   * @param fPaddingX
   *        The X-value to use (for left and right).
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPadding (final float fPaddingY, final float fPaddingX)
  {
    return setPadding (fPaddingY, fPaddingX, fPaddingY, fPaddingX);
  }

  /**
   * Set all padding values to potentially different values.
   *
   * @param fPaddingTop
   *        Top
   * @param fPaddingRight
   *        Right
   * @param fPaddingBottom
   *        Bottom
   * @param fPaddingLeft
   *        Left
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPadding (final float fPaddingTop, final float fPaddingRight, final float fPaddingBottom, final float fPaddingLeft)
  {
    return setPadding (new PaddingSpec (fPaddingTop, fPaddingRight, fPaddingBottom, fPaddingLeft));
  }

  /**
   * Set the padding values.
   *
   * @param aPadding
   *        Padding to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setPadding (@Nonnull PaddingSpec aPadding);

  /**
   * Set the top padding value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPaddingTop (final float fPadding)
  {
    return setPadding (getPadding ().getCloneWithTop (fPadding));
  }

  /**
   * Set the right padding value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPaddingRight (final float fPadding)
  {
    return setPadding (getPadding ().getCloneWithRight (fPadding));
  }

  /**
   * Set the bottom padding value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPaddingBottom (final float fPadding)
  {
    return setPadding (getPadding ().getCloneWithBottom (fPadding));
  }

  /**
   * Set the left padding value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setPaddingLeft (final float fPadding)
  {
    return setPadding (getPadding ().getCloneWithLeft (fPadding));
  }

  /**
   * Set the left padding left AND right value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE setPaddingX (final float fPadding)
  {
    return setPaddingLeft (fPadding).setPaddingRight (fPadding);
  }

  /**
   * Set the left padding top AND bottom value.
   *
   * @param fPadding
   *        The value to use.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE setPaddingY (final float fPadding)
  {
    return setPaddingTop (fPadding).setPaddingBottom (fPadding);
  }

  /**
   * Add to the top padding value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addPaddingTop (final float fPadding)
  {
    if (fPadding == 0f)
      return thisAsT ();
    return setPaddingTop (getPaddingTop () + fPadding);
  }

  /**
   * Add to the right padding value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addPaddingRight (final float fPadding)
  {
    if (fPadding == 0f)
      return thisAsT ();
    return setPaddingRight (getPaddingRight () + fPadding);
  }

  /**
   * Add to the bottom padding value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addPaddingBottom (final float fPadding)
  {
    if (fPadding == 0f)
      return thisAsT ();
    return setPaddingBottom (getPaddingBottom () + fPadding);
  }

  /**
   * Add to the left padding value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   */
  @Nonnull
  default IMPLTYPE addPaddingLeft (final float fPadding)
  {
    if (fPadding == 0f)
      return thisAsT ();
    return setPaddingLeft (getPaddingLeft () + fPadding);
  }

  /**
   * Add to the left padding left AND right value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE addPaddingX (final float fPadding)
  {
    return addPaddingLeft (fPadding).addPaddingRight (fPadding);
  }

  /**
   * Add to the left padding top AND bottom value.
   *
   * @param fPadding
   *        The value to add.
   * @return this
   * @since 5.2.2
   */
  @Nonnull
  default IMPLTYPE addPaddingY (final float fPadding)
  {
    return addPaddingTop (fPadding).addPaddingBottom (fPadding);
  }

  /**
   * @return The current padding. Never <code>null</code>.
   */
  @Nonnull
  PaddingSpec getPadding ();

  /**
   * @return The current top padding.
   */
  default float getPaddingTop ()
  {
    return getPadding ().getTop ();
  }

  /**
   * @return The current right padding.
   */
  default float getPaddingRight ()
  {
    return getPadding ().getRight ();
  }

  /**
   * @return The current bottom padding.
   */
  default float getPaddingBottom ()
  {
    return getPadding ().getBottom ();
  }

  /**
   * @return The current left padding.
   */
  default float getPaddingLeft ()
  {
    return getPadding ().getLeft ();
  }

  /**
   * @return The sum of left and right padding.
   */
  default float getPaddingXSum ()
  {
    return getPadding ().getXSum ();
  }

  /**
   * @return The sum of top and bottom padding.
   */
  default float getPaddingYSum ()
  {
    return getPadding ().getYSum ();
  }

  @Override
  default float getOutlineTop ()
  {
    return getPaddingTop ();
  }

  @Override
  default float getOutlineRight ()
  {
    return getPaddingRight ();
  }

  @Override
  default float getOutlineBottom ()
  {
    return getPaddingBottom ();
  }

  @Override
  default float getOutlineLeft ()
  {
    return getPaddingLeft ();
  }

  @Override
  default float getOutlineXSum ()
  {
    return getPaddingXSum ();
  }

  @Override
  default float getOutlineYSum ()
  {
    return getPaddingYSum ();
  }
}
