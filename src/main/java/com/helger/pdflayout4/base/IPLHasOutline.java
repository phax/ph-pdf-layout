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

/**
 * Base interface for objects having an outline. If an outline is present, it is
 * the combination or margin, border and padding.
 *
 * @author Philip Helger
 */
public interface IPLHasOutline
{
  /**
   * @return margin top + border top + padding top.
   */
  default float getOutlineTop ()
  {
    return 0;
  }

  /**
   * @return margin right + border right + padding right.
   */
  default float getOutlineRight ()
  {
    return 0;
  }

  /**
   * @return margin bottom + border bottom + padding bottom.
   */
  default float getOutlineBottom ()
  {
    return 0;
  }

  /**
   * @return margin left + border left + padding left.
   */
  default float getOutlineLeft ()
  {
    return 0;
  }

  /**
   * Sum of left and right outline
   *
   * @return {@link #getOutlineLeft()} + {@link #getOutlineRight()}
   */
  default float getOutlineXSum ()
  {
    return 0;
  }

  /**
   * Sum of top and bottom outline
   *
   * @return {@link #getOutlineTop()} + {@link #getOutlineBottom()}
   */
  default float getOutlineYSum ()
  {
    return 0;
  }
}
