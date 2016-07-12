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
package com.helger.pdflayout.spec;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.pdflayout.PLConvert;

/**
 * Defines a rectangular padding.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class PaddingSpec extends AbstractRectSpec
{
  public static final PaddingSpec PADDING0 = new PaddingSpec (0, 0, 0, 0);

  public PaddingSpec (@Nonnull final PaddingSpec aOther)
  {
    super (aOther);
  }

  public PaddingSpec (final float f)
  {
    this (f, f);
  }

  public PaddingSpec (final float fY, final float fX)
  {
    this (fY, fX, fY, fX);
  }

  public PaddingSpec (final float fTop, final float fRight, final float fBottom, final float fLeft)
  {
    super (fTop, fRight, fBottom, fLeft);
  }

  @Nonnull
  public PaddingSpec getCloneWithTop (final float fTop)
  {
    if (EqualsHelper.equals (fTop, m_fTop))
      return this;
    return new PaddingSpec (fTop, m_fRight, m_fBottom, m_fLeft);
  }

  @Nonnull
  public PaddingSpec getCloneWithRight (final float fRight)
  {
    if (EqualsHelper.equals (fRight, m_fRight))
      return this;
    return new PaddingSpec (m_fTop, fRight, m_fBottom, m_fLeft);
  }

  @Nonnull
  public PaddingSpec getCloneWithBottom (final float fBottom)
  {
    if (EqualsHelper.equals (fBottom, m_fBottom))
      return this;
    return new PaddingSpec (m_fTop, m_fRight, fBottom, m_fLeft);
  }

  @Nonnull
  public PaddingSpec getCloneWithLeft (final float fLeft)
  {
    if (EqualsHelper.equals (fLeft, m_fLeft))
      return this;
    return new PaddingSpec (m_fTop, m_fRight, m_fBottom, fLeft);
  }

  @Nonnull
  public static PaddingSpec createMM (final float f)
  {
    return new PaddingSpec (PLConvert.mm2units (f));
  }

  @Nonnull
  public static PaddingSpec createMM (final float fY, final float fX)
  {
    return new PaddingSpec (PLConvert.mm2units (fY), PLConvert.mm2units (fX));
  }

  @Nonnull
  public static PaddingSpec createMM (final float fTop, final float fRight, final float fBottom, final float fLeft)
  {
    return new PaddingSpec (PLConvert.mm2units (fTop),
                            PLConvert.mm2units (fRight),
                            PLConvert.mm2units (fBottom),
                            PLConvert.mm2units (fLeft));
  }
}
