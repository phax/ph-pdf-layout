/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout4.spec.BorderSpec;
import com.helger.pdflayout4.spec.BorderStyleSpec;

/**
 * Base interface for objects having a border
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasBorder <IMPLTYPE extends IPLHasBorder <IMPLTYPE>> extends IPLObject <IMPLTYPE>, IPLHasOutline
{
  BorderSpec DEFAULT_BORDER = BorderSpec.BORDER0;

  /**
   * Set all border values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
   *
   * @param aColor
   *        The color to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorder (@Nonnull final Color aColor)
  {
    return setBorder (new BorderStyleSpec (aColor));
  }

  /**
   * Set all border values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
   *
   * @param aBorder
   *        The border style specification to use. May be <code>null</code> to
   *        indicate no border.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (new BorderSpec (aBorder));
  }

  /**
   * Set all border values. This method may not be called after an element got
   * prepared!
   *
   * @param aBorderY
   *        The Y-value to use (for top and bottom). May be <code>null</code> to
   *        indicate no border.
   * @param aBorderX
   *        The X-value to use (for left and right). May be <code>null</code> to
   *        indicate no border.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorderY, @Nullable final BorderStyleSpec aBorderX)
  {
    return setBorder (new BorderSpec (aBorderY, aBorderX));
  }

  /**
   * Set all border values to potentially different values. This method may not
   * be called after an element got prepared!
   *
   * @param aBorderTop
   *        Top. May be <code>null</code> to indicate no border.
   * @param aBorderRight
   *        Right. May be <code>null</code> to indicate no border.
   * @param aBorderBottom
   *        Bottom. May be <code>null</code> to indicate no border.
   * @param aBorderLeft
   *        Left. May be <code>null</code> to indicate no border.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorderTop,
                              @Nullable final BorderStyleSpec aBorderRight,
                              @Nullable final BorderStyleSpec aBorderBottom,
                              @Nullable final BorderStyleSpec aBorderLeft)
  {
    return setBorder (new BorderSpec (aBorderTop, aBorderRight, aBorderBottom, aBorderLeft));
  }

  /**
   * Set the border values. This method may not be called after an element got
   * prepared!
   *
   * @param aBorder
   *        Border to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setBorder (@Nonnull BorderSpec aBorder);

  /**
   * Set the top border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (getBorder ().getCloneWithTop (aBorder));
  }

  /**
   * Set the right border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (getBorder ().getCloneWithRight (aBorder));
  }

  /**
   * Set the bottom border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (getBorder ().getCloneWithBottom (aBorder));
  }

  /**
   * Set the left border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (getBorder ().getCloneWithLeft (aBorder));
  }

  /**
   * @return The current border. Never <code>null</code>.
   */
  @Nonnull
  BorderSpec getBorder ();

  /**
   * @return The current top border.
   */
  default float getBorderTopWidth ()
  {
    return getBorder ().getTopWidth ();
  }

  /**
   * @return The current right border.
   */
  default float getBorderRightWidth ()
  {
    return getBorder ().getRightWidth ();
  }

  /**
   * @return The current bottom border.
   */
  default float getBorderBottomWidth ()
  {
    return getBorder ().getBottomWidth ();
  }

  /**
   * @return The current left border.
   */
  default float getBorderLeftWidth ()
  {
    return getBorder ().getLeftWidth ();
  }

  /**
   * @return The sum of left and right border.
   */
  default float getBorderXSumWidth ()
  {
    return getBorder ().getXSumWidth ();
  }

  /**
   * @return The sum of top and bottom border.
   */
  default float getBorderYSumWidth ()
  {
    return getBorder ().getYSumWidth ();
  }

  default float getOutlineTop ()
  {
    return getBorderTopWidth ();
  }

  default float getOutlineRight ()
  {
    return getBorderRightWidth ();
  }

  default float getOutlineBottom ()
  {
    return getBorderBottomWidth ();
  }

  default float getOutlineLeft ()
  {
    return getBorderLeftWidth ();
  }

  default float getOutlineXSum ()
  {
    return getBorderXSumWidth ();
  }

  default float getOutlineYSum ()
  {
    return getBorderYSumWidth ();
  }
}
