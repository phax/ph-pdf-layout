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
import javax.annotation.Nullable;

import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;

/**
 * Base interface for objects with a row border
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasRowBorder <IMPLTYPE extends IPLHasRowBorder <IMPLTYPE>> extends IPLObject <IMPLTYPE>
{
  /**
   * Set the border around each contained row.
   *
   * @param aBorder
   *        The border style to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (new BorderSpec (aBorder));
  }

  /**
   * Set the border around each contained row.
   *
   * @param aBorderY
   *        The border to set for top and bottom. Maybe <code>null</code>.
   * @param aBorderX
   *        The border to set for left and right. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderY, @Nullable final BorderStyleSpec aBorderX)
  {
    return setRowBorder (new BorderSpec (aBorderY, aBorderX));
  }

  /**
   * Set the border around each contained row.
   *
   * @param aBorderTop
   *        The border to set for top. Maybe <code>null</code>.
   * @param aBorderRight
   *        The border to set for right. Maybe <code>null</code>.
   * @param aBorderBottom
   *        The border to set for bottom. Maybe <code>null</code>.
   * @param aBorderLeft
   *        The border to set for left. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderTop,
                                 @Nullable final BorderStyleSpec aBorderRight,
                                 @Nullable final BorderStyleSpec aBorderBottom,
                                 @Nullable final BorderStyleSpec aBorderLeft)
  {
    return setRowBorder (new BorderSpec (aBorderTop, aBorderRight, aBorderBottom, aBorderLeft));
  }

  /**
   * Set the border around each contained row.
   *
   * @param aRowBorder
   *        The border to set. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setRowBorder (@Nonnull BorderSpec aRowBorder);

  /**
   * Set the top border value around each contained row. This method may not be
   * called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (getRowBorder ().getCloneWithTop (aBorder));
  }

  /**
   * Set the right border value around each contained row. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (getRowBorder ().getCloneWithRight (aBorder));
  }

  /**
   * Set the bottom border value around each contained row. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (getRowBorder ().getCloneWithBottom (aBorder));
  }

  /**
   * Set the left border value around each contained row. This method may not be
   * called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setRowBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (getRowBorder ().getCloneWithLeft (aBorder));
  }

  /**
   * Get the border around each contained row. By default
   * {@link BorderSpec#BORDER0} which means no border is used.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  BorderSpec getRowBorder ();
}
