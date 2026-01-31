/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
  @Override
  default float getOutlineTop ()
  {
    return getMarginTop () + getBorderTopWidth () + getPaddingTop ();
  }

  @Override
  default float getOutlineRight ()
  {
    return getMarginRight () + getBorderRightWidth () + getPaddingRight ();
  }

  @Override
  default float getOutlineBottom ()
  {
    return getMarginBottom () + getBorderBottomWidth () + getPaddingBottom ();
  }

  @Override
  default float getOutlineLeft ()
  {
    return getMarginLeft () + getBorderLeftWidth () + getPaddingLeft ();
  }

  @Override
  default float getOutlineXSum ()
  {
    return getMarginXSum () + getBorderXSumWidth () + getPaddingXSum ();
  }

  @Override
  default float getOutlineYSum ()
  {
    return getMarginYSum () + getBorderYSumWidth () + getPaddingYSum ();
  }
}
