/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.MustImplementEqualsAndHashcode;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Different dashed line times
 * 
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public class LineDashPatternSpec
{
  public static final LineDashPatternSpec SOLID = new LineDashPatternSpec ();
  public static final LineDashPatternSpec DASHED_2 = new LineDashPatternSpec (2f);
  public static final LineDashPatternSpec DASHED_3 = new LineDashPatternSpec (3f);
  public static final LineDashPatternSpec DASHED_4 = new LineDashPatternSpec (4f);
  public static final LineDashPatternSpec DASHED_5 = new LineDashPatternSpec (5f);

  private final float [] m_aPattern;
  private final float m_fPhase;

  public LineDashPatternSpec ()
  {
    this (new float [0], 0);
  }

  public LineDashPatternSpec (@Nonnegative final float fPattern)
  {
    this (new float [] { fPattern }, 0);
  }

  public LineDashPatternSpec (@Nonnegative final float fPatternOn, @Nonnegative final float fPatternOff)
  {
    this (new float [] { fPatternOn, fPatternOff }, 0);
  }

  /**
   * @param aPattern
   *        The pattern array. May not be <code>null</code>. Must have 0-2
   *        items. 0 items means solid line, 1 item means identical on and off
   *        length and 2 items means potentially different on and off length.
   *        All contains values must be &gt; 0.
   * @param fPhase
   *        The phase of the pattern. Where to start the painting, first
   *        counting on than off.
   */
  public LineDashPatternSpec (@Nonnull final float [] aPattern, final float fPhase)
  {
    ValueEnforcer.notNull (aPattern, "Pattern");
    if (aPattern.length > 2)
      throw new IllegalArgumentException ();
    for (final float fPatternValue : aPattern)
      if (fPatternValue <= 0)
        throw new IllegalArgumentException ("At least one pattern value is negative: " + Arrays.toString (aPattern));
    m_aPattern = ArrayHelper.getCopy (aPattern);
    m_fPhase = fPhase;
  }

  /**
   * @return A copy with all patterns. 0-2 elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public float [] getPattern ()
  {
    return ArrayHelper.getCopy (m_aPattern);
  }

  /**
   * @return The phase to use.
   */
  public float getPhase ()
  {
    return m_fPhase;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LineDashPatternSpec rhs = (LineDashPatternSpec) o;
    return EqualsUtils.equals (m_aPattern, rhs.m_aPattern) && EqualsUtils.equals (m_fPhase, rhs.m_fPhase);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aPattern).append (m_fPhase).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("pattern", m_aPattern).append ("phase", m_fPhase).toString ();
  }
}
