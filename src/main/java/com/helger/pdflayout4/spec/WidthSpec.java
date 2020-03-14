/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class defines a dependent width of an elements:
 * <ul>
 * <li>absolute - element has a fixed width</li>
 * <li>percentage - element width is a certain percentage of the surrounding
 * element</li>
 * <li>star - element width is a relative part of the unused width of the
 * surrounding element</li>
 * </ul>
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
@SuppressFBWarnings ("JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS")
public class WidthSpec implements Serializable
{
  private final EValueUOMType m_eType;
  private final float m_fValue;

  public WidthSpec (@Nonnull final EValueUOMType eType, final float fValue)
  {
    ValueEnforcer.notNull (eType, "WidthType");
    m_eType = eType;
    m_fValue = fValue;
  }

  /**
   * @return The width type. Never <code>null</code>.
   */
  @Nonnull
  public final EValueUOMType getType ()
  {
    return m_eType;
  }

  /**
   * @return The ID of the width type. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public final String getTypeID ()
  {
    return m_eType.getID ();
  }

  /**
   * @return <code>true</code> if type is 'absolute' or 'percentage'. Only
   *         absolute entries need to provide a value!
   */
  public final boolean isAbsolute ()
  {
    return m_eType.isValueRequired ();
  }

  /**
   * @return <code>true</code> if type is 'star'.
   */
  public final boolean isStar ()
  {
    return m_eType == EValueUOMType.STAR;
  }

  /**
   * @return <code>true</code> if type is 'auto'.
   */
  public final boolean isAuto ()
  {
    return m_eType == EValueUOMType.AUTO;
  }

  /**
   * @return The width value - is either an absolute value or a percentage value
   *         - depending on {@link #getType()}. For star width elements this is
   *         0.
   */
  @Nonnegative
  public final float getValue ()
  {
    return m_fValue;
  }

  /**
   * Get the effective width based on the passed available width. This may not
   * be called for star or auto width elements.
   *
   * @param fAvailableWidth
   *        The available width.
   * @return The effective width to use.
   * @see #isAbsolute()
   */
  @Nonnegative
  public float getEffectiveValue (final float fAvailableWidth)
  {
    switch (m_eType)
    {
      case ABSOLUTE:
        return Math.min (m_fValue, fAvailableWidth);
      case PERCENTAGE:
        return fAvailableWidth * m_fValue / 100;
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
    final WidthSpec rhs = (WidthSpec) o;
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
    return new ToStringGenerator (null).append ("Type", m_eType)
                                       .appendIf ("Value", m_fValue, x -> isAbsolute ())
                                       .getToString ();
  }

  /**
   * Create a width element with an absolute value.
   *
   * @param fValue
   *        The width to use. Must be &gt; 0.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static WidthSpec abs (@Nonnegative final float fValue)
  {
    ValueEnforcer.isGT0 (fValue, "Value");
    return new WidthSpec (EValueUOMType.ABSOLUTE, fValue);
  }

  /**
   * Create a width element with an percentage value.
   *
   * @param fPerc
   *        The width percentage to use. Must be &gt; 0.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static WidthSpec perc (@Nonnegative final float fPerc)
  {
    ValueEnforcer.isGT0 (fPerc, "Perc");
    return new WidthSpec (EValueUOMType.PERCENTAGE, fPerc);
  }

  /**
   * Create a new star width element.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static WidthSpec star ()
  {
    return new WidthSpec (EValueUOMType.STAR, 0);
  }

  /**
   * Create a new auto width element.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static WidthSpec auto ()
  {
    return new WidthSpec (EValueUOMType.AUTO, 0);
  }
}
