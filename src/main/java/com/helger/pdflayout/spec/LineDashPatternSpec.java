/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Different dashed line times
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public class LineDashPatternSpec implements Serializable
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
    ValueEnforcer.isTrue (aPattern.length <= 2,
                          () -> "Too many patterns (" + aPattern.length + ") provided. At max 2 items are allowed.");
    for (final float fPatternValue : aPattern)
      ValueEnforcer.isGT0 (fPatternValue, "PatternValue");

    m_aPattern = ArrayHelper.getCopy (aPattern);
    m_fPhase = fPhase;
  }

  /**
   * @return A copy with all patterns. 0-2 elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final float [] getPattern ()
  {
    return ArrayHelper.getCopy (m_aPattern);
  }

  /**
   * @return A COS array with 0-2 elements. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final COSArray getPatternCOSArray ()
  {
    final COSArray ret = new COSArray ();
    for (final float f : m_aPattern)
      ret.add (new COSFloat (f));
    return ret;
  }

  /**
   * @return The phase to use.
   */
  public final float getPhase ()
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
    return EqualsHelper.equals (m_aPattern, rhs.m_aPattern) && EqualsHelper.equals (m_fPhase, rhs.m_fPhase);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aPattern).append (m_fPhase).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Pattern", m_aPattern).append ("Phase", m_fPhase).getToString ();
  }
}
