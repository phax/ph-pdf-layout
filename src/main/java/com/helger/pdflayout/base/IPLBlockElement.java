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

import javax.annotation.Nonnull;

/**
 * Base interface for block elements. Compared to normal elements
 * ({@link IPLElement}) they additionally have a horizontal alignment
 * ({@link IPLHasHorizontalAlignment}) and vertical alignment
 * ({@link IPLHasVerticalAlignment}).
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLBlockElement <IMPLTYPE extends IPLBlockElement <IMPLTYPE>> extends
                                 IPLElement <IMPLTYPE>,
                                 IPLHasHorizontalAlignment <IMPLTYPE>,
                                 IPLHasVerticalAlignment <IMPLTYPE>
{
  /**
   * By default all block elements are full width.
   */
  boolean DEFAULT_FULL_WIDTH = true;

  /** By default content is not clipped */
  boolean DEFAULT_CLIP_CONTENT = false;

  /**
   * @return Should the element occupy the full width? The default is
   *         {@link #DEFAULT_FULL_WIDTH}.
   */
  boolean isFullWidth ();

  /**
   * Set usage of full width.
   *
   * @param bFullWidth
   *        <code>true</code> to enable full width, <code>false</code> to use
   *        only what is available.
   * @return this for chaining
   */
  @Nonnull
  IMPLTYPE setFullWidth (boolean bFullWidth);

  /**
   * @return <code>true</code> if any overflowing content should be clipped,
   *         <code>false</code> if not. Default is
   *         {@link #DEFAULT_CLIP_CONTENT}.
   * @since 7.3.1
   */
  boolean isClipContent ();

  /**
   * Enable the clipping of content, so that only the content inside the
   * rendering area is shown. Similar to CSS style <code>overflow:hidden</code>.
   * This usually only makes sense if a maximum width or height is defined
   * additionally.
   *
   * @param bClipContent
   *        <code>true</code> to enable it, <code>false</code> to disable it.
   * @return this for chaining
   * @since 7.3.1
   */
  @Nonnull
  IMPLTYPE setClipContent (boolean bClipContent);
}
