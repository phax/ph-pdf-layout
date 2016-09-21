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

/**
 * Base interface for objects having a margin, a border and a padding
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHasMarginBorderPadding <IMPLTYPE extends IPLHasMarginBorderPadding <IMPLTYPE>> extends
                                           IPLHasMargin <IMPLTYPE>,
                                           IPLHasPadding <IMPLTYPE>,
                                           IPLHasBorder <IMPLTYPE>
{
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
