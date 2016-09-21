/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import javax.annotation.Nonnull;

import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Base interface for objects having a padding
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasPadding <IMPLTYPE extends IPLHasPadding <IMPLTYPE>> extends IPLObject <IMPLTYPE>
{
  PaddingSpec DEFAULT_PADDING = PaddingSpec.PADDING0;

  /**
   * Set all padding values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
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
   * Set all padding values. This method may not be called after an element got
   * prepared!
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
   * Set all padding values to potentially different values. This method may not
   * be called after an element got prepared!
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
  default IMPLTYPE setPadding (final float fPaddingTop,
                               final float fPaddingRight,
                               final float fPaddingBottom,
                               final float fPaddingLeft)
  {
    return setPadding (new PaddingSpec (fPaddingTop, fPaddingRight, fPaddingBottom, fPaddingLeft));
  }

  /**
   * Set the padding values. This method may not be called after an element got
   * prepared!
   *
   * @param aPadding
   *        Padding to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setPadding (@Nonnull PaddingSpec aPadding);

  /**
   * Set the top padding value. This method may not be called after an element
   * got prepared!
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
   * Set the right padding value. This method may not be called after an element
   * got prepared!
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
   * Set the bottom padding value. This method may not be called after an
   * element got prepared!
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
   * Set the left padding value. This method may not be called after an element
   * got prepared!
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
}
