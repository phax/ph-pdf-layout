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
package com.helger.pdflayout4.spec;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class defines a dependent height of an elements:
 * <ul>
 * <li>absolute - element has a fixed height</li>
 * <li>percentage - element height is a certain percentage of the surrounding
 * element</li>
 * <li>star - element height is a relative part of the unused height of the
 * surrounding element</li>
 * </ul>
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class HeightSpec implements Serializable
{
  private final EValueUOMType m_eType;
  private final float m_fValue;

  public HeightSpec (@Nonnull final EValueUOMType eType, final float fValue)
  {
    ValueEnforcer.notNull (eType, "HeightType");
    m_eType = eType;
    m_fValue = fValue;
  }

  /**
   * @return The height type. Never <code>null</code>.
   */
  @Nonnull
  public EValueUOMType getType ()
  {
    return m_eType;
  }

  /**
   * @return The ID of the height type. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getTypeID ()
  {
    return m_eType.getID ();
  }

  /**
   * @return <code>true</code> if type is 'absolute' or 'percentage'
   */
  public boolean isAbsolute ()
  {
    return m_eType == EValueUOMType.ABSOLUTE || m_eType == EValueUOMType.PERCENTAGE;
  }

  /**
   * @return <code>true</code> if type is 'star'.
   */
  public boolean isStar ()
  {
    return m_eType == EValueUOMType.STAR;
  }

  /**
   * @return <code>true</code> if type is 'auto'.
   */
  public boolean isAuto ()
  {
    return m_eType == EValueUOMType.AUTO;
  }

  /**
   * @return The height value - is either an absolute value or a percentage
   *         value - depending on {@link #getType()}. For star height elements
   *         this is 0.
   */
  @Nonnegative
  public float getValue ()
  {
    return m_fValue;
  }

  /**
   * Get the effective height based on the passed available height. This may not
   * be called for star height elements.
   *
   * @param fAvailableHeight
   *        The available height.
   * @return The effective height to use.
   */
  @Nonnegative
  public float getEffectiveValue (final float fAvailableHeight)
  {
    switch (m_eType)
    {
      case ABSOLUTE:
        return Math.min (m_fValue, fAvailableHeight);
      case PERCENTAGE:
        return fAvailableHeight * m_fValue / 100;
      default:
        throw new IllegalStateException ("Unsupported: " + m_eType + " - must be calculated outside!");
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final HeightSpec rhs = (HeightSpec) o;
    return m_eType.equals (rhs.m_eType) && EqualsHelper.equals (m_fValue, rhs.m_fValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eType).append (m_fValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("type", m_eType).append ("value", m_fValue).toString ();
  }

  /**
   * Create a height element with an absolute value.
   *
   * @param fValue
   *        The height to use. Must be &gt; 0.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static HeightSpec abs (@Nonnegative final float fValue)
  {
    ValueEnforcer.isGT0 (fValue, "Value");
    return new HeightSpec (EValueUOMType.ABSOLUTE, fValue);
  }

  /**
   * Create a height element with an percentage value.
   *
   * @param fPerc
   *        The height percentage to use. Must be &gt; 0.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static HeightSpec perc (@Nonnegative final float fPerc)
  {
    ValueEnforcer.isGT0 (fPerc, "Perc");
    return new HeightSpec (EValueUOMType.PERCENTAGE, fPerc);
  }

  /**
   * Create a new star height element.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static HeightSpec star ()
  {
    return new HeightSpec (EValueUOMType.STAR, 0);
  }

  /**
   * Create a new auto height element.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static HeightSpec auto ()
  {
    return new HeightSpec (EValueUOMType.AUTO, 0);
  }
}
