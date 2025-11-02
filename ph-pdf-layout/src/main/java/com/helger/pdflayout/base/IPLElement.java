/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Base interface for renderable objects having a margin, a border and a
 * padding<br>
 * Each object is self-responsible for handling its margin, border and padding!
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLElement <IMPLTYPE extends IPLElement <IMPLTYPE>> extends
                            IPLRenderableObject <IMPLTYPE>,
                            IPLHasMarginBorderPadding <IMPLTYPE>,
                            IPLHasFillColor <IMPLTYPE>
{
  SizeSpec DEFAULT_MIN_SIZE = SizeSpec.SIZE0;
  SizeSpec DEFAULT_MAX_SIZE = SizeSpec.SIZE_MAX;

  /**
   * @return The minimum size to be used. Excluding outline. Never
   *         <code>null</code>.
   */
  @NonNull
  SizeSpec getMinSize ();

  /**
   * @return The minimum width of the element.
   */
  default float getMinWidth ()
  {
    return getMinSize ().getWidth ();
  }

  /**
   * @return The minimum height of the element.
   */
  default float getMinHeight ()
  {
    return getMinSize ().getHeight ();
  }

  /**
   * Set the minimum size to be used. Excluding outline.
   *
   * @param fMinWidth
   *        Minimum width. Must be &ge; 0.
   * @param fMinHeight
   *        Minimum height. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMinSize (@Nonnegative final float fMinWidth, @Nonnegative final float fMinHeight)
  {
    return setMinSize (new SizeSpec (fMinWidth, fMinHeight));
  }

  /**
   * Set the minimum size to be used. Excluding outline.
   *
   * @param aMinSize
   *        Minimum size. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  IMPLTYPE setMinSize (@NonNull SizeSpec aMinSize);

  /**
   * Set the minimum width to be used. Excluding outline.
   *
   * @param fMinWidth
   *        Minimum width. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMinWidth (@Nonnegative final float fMinWidth)
  {
    return setMinSize (fMinWidth, getMinHeight ());
  }

  /**
   * Set the minimum height to be used. Excluding outline.
   *
   * @param fMinHeight
   *        Minimum height. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMinHeight (@Nonnegative final float fMinHeight)
  {
    return setMinSize (getMinWidth (), fMinHeight);
  }

  /**
   * @return The maximum size to be used. Excluding outline. Never
   *         <code>null</code>.
   */
  @NonNull
  SizeSpec getMaxSize ();

  /**
   * @return The max width of the element.
   */
  default float getMaxWidth ()
  {
    return getMaxSize ().getWidth ();
  }

  /**
   * @return The maximum height of the element.
   */
  default float getMaxHeight ()
  {
    return getMaxSize ().getHeight ();
  }

  /**
   * Set the maximum size to be used. Excluding outline.
   *
   * @param fMaxWidth
   *        Maximum width. Must be &ge; 0.
   * @param fMaxHeight
   *        Maximum height. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMaxSize (@Nonnegative final float fMaxWidth, @Nonnegative final float fMaxHeight)
  {
    return setMaxSize (new SizeSpec (fMaxWidth, fMaxHeight));
  }

  /**
   * Set the maximum size to be used. Excluding outline.
   *
   * @param aMaxSize
   *        Maximum size. May not be <code>null</code>. Must both be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE setMaxSize (@NonNull SizeSpec aMaxSize);

  /**
   * Set the maximum width to be used. Excluding outline.
   *
   * @param fMaxWidth
   *        Maximum width. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMaxWidth (@Nonnegative final float fMaxWidth)
  {
    return setMaxSize (fMaxWidth, getMaxHeight ());
  }

  /**
   * Set the maximum height to be used. Excluding outline.
   *
   * @param fMaxHeight
   *        Maximum height. Must be &ge; 0.
   * @return this
   */
  @NonNull
  default IMPLTYPE setMaxHeight (@Nonnegative final float fMaxHeight)
  {
    return setMaxSize (getMaxWidth (), fMaxHeight);
  }

  /**
   * Set the exact size to be used. Excluding outline. This is a shortcut for
   * setting minimum and maximum size to the same values.
   *
   * @param fWidth
   *        Width to use. Must be &ge; 0.
   * @param fHeight
   *        Height to use. Must be &ge; 0.
   * @return this
   * @see #setMinSize(float, float)
   * @see #setMaxSize(float, float)
   */
  @NonNull
  default IMPLTYPE setExactSize (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    setMinSize (fWidth, fHeight);
    return setMaxSize (fWidth, fHeight);
  }

  /**
   * Set the exact width to be used. Excluding outline. This is a shortcut for
   * setting minimum and maximum width to the same values.
   *
   * @param fWidth
   *        Width to use. Must be &ge; 0.
   * @return this
   * @see #setMinWidth(float)
   * @see #setMaxWidth(float)
   */
  @NonNull
  default IMPLTYPE setExactWidth (@Nonnegative final float fWidth)
  {
    setMinWidth (fWidth);
    return setMaxWidth (fWidth);
  }

  /**
   * Set the exact height to be used. Excluding outline. This is a shortcut for
   * setting minimum and maximum height to the same values.
   *
   * @param fHeight
   *        Height to use. Must be &ge; 0.
   * @return this
   * @see #setMinHeight(float)
   * @see #setMaxHeight(float)
   */
  @NonNull
  default IMPLTYPE setExactHeight (@Nonnegative final float fHeight)
  {
    setMinHeight (fHeight);
    return setMaxHeight (fHeight);
  }
}
