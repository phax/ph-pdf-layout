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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout.spec.EVertAlignment;

/**
 * Base interface for objects with a vertical alignment
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasVerticalAlignment <IMPLTYPE extends IPLHasVerticalAlignment <IMPLTYPE>> extends IPLRenderableObject <IMPLTYPE>
{
  EVertAlignment DEFAULT_VERT_ALIGNMENT = EVertAlignment.DEFAULT;

  /**
   * @return The vertical alignment of this element. By default it is
   *         {@link EVertAlignment#DEFAULT}. Never <code>null</code>. The
   *         vertical alignment may only be applied to contained children!
   */
  @Nonnull
  EVertAlignment getVertAlign ();

  /**
   * Set the vertical alignment of this element. The vertical alignment may only
   * be applied to contained children!
   *
   * @param eVertAlign
   *        The new vertical alignment. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setVertAlign (@Nonnull EVertAlignment eVertAlign);

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * prepared height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @return The indentation offset. Always &ge; 0.
   */
  @Nonnegative
  default float getIndentY (final float fAvailableHeight)
  {
    return getIndentY (fAvailableHeight, getPreparedHeight ());
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * provided element height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element. This is usually
   *        larger than fElementHeight.
   * @param fElementHeight
   *        The height of the element to align.
   * @return The indentation offset. Always &ge; 0.
   */
  @Nonnegative
  default float getIndentY (final float fAvailableHeight, final float fElementHeight)
  {
    switch (getVertAlign ())
    {
      case TOP:
        return 0f;
      case MIDDLE:
        return Math.max ((fAvailableHeight - fElementHeight) / 2f, 0f);
      case BOTTOM:
        return Math.max (fAvailableHeight - fElementHeight, 0f);
      default:
        throw new IllegalStateException ("Unsupported vertical alignment " + getVertAlign ());
    }
  }
}
