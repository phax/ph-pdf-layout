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
package com.helger.pdflayout.spec;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Defines a rectangular object.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public abstract class AbstractRectSpec implements Serializable
{
  protected final float m_fTop;
  protected final float m_fRight;
  protected final float m_fBottom;
  protected final float m_fLeft;
  // Helper vars only
  private final float m_fXSum;
  private final float m_fYSum;

  public AbstractRectSpec (@Nonnull final AbstractRectSpec aOther)
  {
    this (aOther.m_fTop, aOther.m_fRight, aOther.m_fBottom, aOther.m_fLeft);
  }

  public AbstractRectSpec (final float fTop, final float fRight, final float fBottom, final float fLeft)
  {
    ValueEnforcer.isFalse (Float.isNaN (fTop), "Top may not be NaN");
    ValueEnforcer.isFalse (Float.isNaN (fRight), "Right may not be NaN");
    ValueEnforcer.isFalse (Float.isNaN (fBottom), "Bottom may not be NaN");
    ValueEnforcer.isFalse (Float.isNaN (fLeft), "Left may not be NaN");
    m_fTop = fTop;
    m_fRight = fRight;
    m_fBottom = fBottom;
    m_fLeft = fLeft;
    m_fXSum = fLeft + fRight;
    m_fYSum = fTop + fBottom;
  }

  public final boolean hasAnyValue ()
  {
    return m_fTop != 0 || m_fRight != 0 || m_fBottom != 0 || m_fLeft != 0;
  }

  /**
   * @return Top value
   */
  public final float getTop ()
  {
    return m_fTop;
  }

  /**
   * @return Right value
   */
  public final float getRight ()
  {
    return m_fRight;
  }

  /**
   * @return Bottom value
   */
  public final float getBottom ()
  {
    return m_fBottom;
  }

  /**
   * @return Left value
   */
  public final float getLeft ()
  {
    return m_fLeft;
  }

  /**
   * @return Left + right value
   */
  public final float getXSum ()
  {
    return m_fXSum;
  }

  /**
   * @return Top + bottom value
   */
  public final float getYSum ()
  {
    return m_fYSum;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractRectSpec rhs = (AbstractRectSpec) o;
    return EqualsHelper.equals (m_fTop, rhs.m_fTop) &&
           EqualsHelper.equals (m_fRight, rhs.m_fRight) &&
           EqualsHelper.equals (m_fBottom, rhs.m_fBottom) &&
           EqualsHelper.equals (m_fLeft, rhs.m_fLeft);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_fTop).append (m_fRight).append (m_fBottom).append (m_fLeft).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Top", m_fTop)
                                       .append ("Right", m_fRight)
                                       .append ("Bottom", m_fBottom)
                                       .append ("Left", m_fLeft)
                                       .append ("XSum", m_fXSum)
                                       .append ("YSum", m_fYSum)
                                       .getToString ();
  }
}
