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
package com.helger.pdflayout4.spec;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.pdflayout4.PLConvert;

/**
 * Defines a rectangular padding.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class PaddingSpec extends AbstractRectSpec
{
  public static final float DEFAULT_FLOAT = 0f;
  public static final PaddingSpec PADDING0 = new PaddingSpec (DEFAULT_FLOAT, DEFAULT_FLOAT, DEFAULT_FLOAT, DEFAULT_FLOAT);

  /**
   * Pseudo copy constructor.
   *
   * @param aOther
   *        Value to copy from. May not be <code>null</code>.
   */
  public PaddingSpec (@Nonnull final AbstractRectSpec aOther)
  {
    super (aOther);
  }

  /**
   * Constructor with the same value for all axis.
   *
   * @param f
   *        value for top, right, bottom, left
   */
  public PaddingSpec (final float f)
  {
    this (f, f);
  }

  /**
   * Constructor with the same value for X and Y axis.
   *
   * @param fY
   *        top and bottom value
   * @param fX
   *        left and right value
   */
  public PaddingSpec (final float fY, final float fX)
  {
    this (fY, fX, fY, fX);
  }

  /**
   * Constructor with explicit values
   *
   * @param fTop
   *        top value
   * @param fRight
   *        right value
   * @param fBottom
   *        bottom value
   * @param fLeft
   *        left value
   */
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

  @Nonnull
  public static PaddingSpec top (final float fTop)
  {
    return new PaddingSpec (fTop, DEFAULT_FLOAT, DEFAULT_FLOAT, DEFAULT_FLOAT);
  }

  @Nonnull
  public static PaddingSpec right (final float fRight)
  {
    return new PaddingSpec (DEFAULT_FLOAT, fRight, DEFAULT_FLOAT, DEFAULT_FLOAT);
  }

  @Nonnull
  public static PaddingSpec bottom (final float fBottom)
  {
    return new PaddingSpec (DEFAULT_FLOAT, DEFAULT_FLOAT, fBottom, DEFAULT_FLOAT);
  }

  @Nonnull
  public static PaddingSpec left (final float fLeft)
  {
    return new PaddingSpec (DEFAULT_FLOAT, DEFAULT_FLOAT, DEFAULT_FLOAT, fLeft);
  }
}
