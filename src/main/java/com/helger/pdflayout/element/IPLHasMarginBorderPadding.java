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
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Base interface for objects having a margin, a border and a padding
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasMarginBorderPadding <IMPLTYPE extends IPLHasMarginBorderPadding <IMPLTYPE>>
                                           extends IPLObject <IMPLTYPE>
{
  /**
   * Set all margin values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
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
   * Set all margin values. This method may not be called after an element got
   * prepared!
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
   * Set all margin values to potentially different values. This method may not
   * be called after an element got prepared!
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
  default IMPLTYPE setMargin (final float fMarginTop,
                              final float fMarginRight,
                              final float fMarginBottom,
                              final float fMarginLeft)
  {
    return setMargin (new MarginSpec (fMarginTop, fMarginRight, fMarginBottom, fMarginLeft));
  }

  /**
   * Set the margin values. This method may not be called after an element got
   * prepared!
   *
   * @param aMargin
   *        Margin to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setMargin (@Nonnull MarginSpec aMargin);

  /**
   * Set the top margin value. This method may not be called after an element
   * got prepared!
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
   * Set the right margin value. This method may not be called after an element
   * got prepared!
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
   * Set the bottom margin value. This method may not be called after an element
   * got prepared!
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
   * Set the left margin value. This method may not be called after an element
   * got prepared!
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

  default float getMarginAndBorderTop ()
  {
    return getMargin ().getTop () + getBorder ().getTopWidth ();
  }

  default float getMarginAndBorderRight ()
  {
    return getMargin ().getRight () + getBorder ().getRightWidth ();
  }

  default float getMarginAndBorderBottom ()
  {
    return getMargin ().getBottom () + getBorder ().getBottomWidth ();
  }

  default float getMarginAndBorderLeft ()
  {
    return getMargin ().getLeft () + getBorder ().getLeftWidth ();
  }

  default float getMarginAndBorderXSum ()
  {
    return getMargin ().getXSum () + getBorder ().getXSumWidth ();
  }

  default float getMarginAndBorderYSum ()
  {
    return getMargin ().getYSum () + getBorder ().getYSumWidth ();
  }

  default float getFullTop ()
  {
    return getMargin ().getTop () + getBorder ().getTopWidth () + getPadding ().getTop ();
  }

  default float getFullRight ()
  {
    return getMargin ().getRight () + getBorder ().getRightWidth () + getPadding ().getRight ();
  }

  default float getFullBottom ()
  {
    return getMargin ().getBottom () + getBorder ().getBottomWidth () + getPadding ().getBottom ();
  }

  default float getFullLeft ()
  {
    return getMargin ().getLeft () + getBorder ().getLeftWidth () + getPadding ().getLeft ();
  }

  default float getFullXSum ()
  {
    return getMargin ().getXSum () + getBorder ().getXSumWidth () + getPadding ().getXSum ();
  }

  default float getFullYSum ()
  {
    return getMargin ().getYSum () + getBorder ().getYSumWidth () + getPadding ().getYSum ();
  }
}
