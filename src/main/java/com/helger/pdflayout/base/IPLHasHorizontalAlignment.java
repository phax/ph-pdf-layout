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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout.spec.EHorzAlignment;

/**
 * Base interface for objects with a horizontal alignment
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasHorizontalAlignment <IMPLTYPE extends IPLHasHorizontalAlignment <IMPLTYPE>> extends IPLRenderableObject <IMPLTYPE>
{
  EHorzAlignment DEFAULT_HORZ_ALIGNMENT = EHorzAlignment.DEFAULT;

  /**
   * @return The horizontal alignment of this element. By default it is
   *         {@link EHorzAlignment#DEFAULT}. Never <code>null</code>. The
   *         horizontal alignment may only be applied to contained children!
   */
  @Nonnull
  EHorzAlignment getHorzAlign ();

  /**
   * Set the horizontal alignment of this element. The horizontal alignment may
   * only be applied to contained children!
   *
   * @param eHorzAlign
   *        The new horizontal alignment. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  IMPLTYPE setHorzAlign (@Nonnull EHorzAlignment eHorzAlign);

  /**
   * Get the indentation for a certain horizontal alignment. This method uses
   * the provided element width as the basis for alignment.
   *
   * @param fAvailableWidth
   *        The available width of the surrounding element. This is usually
   *        larger than fElementWidth.
   * @param fElementWidth
   *        The width of the element to align.
   * @return The indentation offset. Always &ge; 0.
   */
  @Nonnegative
  default float getIndentX (final float fAvailableWidth, final float fElementWidth)
  {
    switch (getHorzAlign ())
    {
      case LEFT:
        return 0f;
      case CENTER:
        return Math.max ((fAvailableWidth - fElementWidth) / 2, 0f);
      case RIGHT:
        return Math.max (fAvailableWidth - fElementWidth, 0f);
      case JUSTIFY:
        return 0f;
      default:
        throw new IllegalStateException ("Unsupported horizontal alignment " + getHorzAlign ());
    }
  }
}
