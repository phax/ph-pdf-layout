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

/**
 * Defines a rectangular margin.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class MarginSpec extends AbstractRectSpec
{
  public static final MarginSpec MARGIN0 = new MarginSpec (0, 0, 0, 0);

  public MarginSpec (@Nonnull final MarginSpec aOther)
  {
    super (aOther);
  }

  public MarginSpec (final float f)
  {
    this (f, f);
  }

  public MarginSpec (final float fX, final float fY)
  {
    this (fY, fX, fY, fX);
  }

  public MarginSpec (final float fTop, final float fRight, final float fBottom, final float fLeft)
  {
    super (fTop, fRight, fBottom, fLeft);
  }

  @Nonnull
  public MarginSpec getCloneWithTop (final float fTop)
  {
    if (EqualsHelper.equals (fTop, m_fTop))
      return this;
    return new MarginSpec (fTop, m_fRight, m_fBottom, m_fLeft);
  }

  @Nonnull
  public MarginSpec getCloneWithRight (final float fRight)
  {
    if (EqualsHelper.equals (fRight, m_fRight))
      return this;
    return new MarginSpec (m_fTop, fRight, m_fBottom, m_fLeft);
  }

  @Nonnull
  public MarginSpec getCloneWithBottom (final float fBottom)
  {
    if (EqualsHelper.equals (fBottom, m_fBottom))
      return this;
    return new MarginSpec (m_fTop, m_fRight, fBottom, m_fLeft);
  }

  @Nonnull
  public MarginSpec getCloneWithLeft (final float fLeft)
  {
    if (EqualsHelper.equals (fLeft, m_fLeft))
      return this;
    return new MarginSpec (m_fTop, m_fRight, m_fBottom, fLeft);
  }
}
