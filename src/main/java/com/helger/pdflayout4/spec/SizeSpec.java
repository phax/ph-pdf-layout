/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.PLConvert;

/**
 * This class defines a size.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class SizeSpec implements Serializable
{
  public static final float DEFAULT_FLOAT = 0f;
  public static final SizeSpec SIZE0 = new SizeSpec (DEFAULT_FLOAT, DEFAULT_FLOAT);
  public static final SizeSpec SIZE_MAX = new SizeSpec (Float.MAX_VALUE, Float.MAX_VALUE);

  private final float m_fWidth;
  private final float m_fHeight;

  /**
   * Constructor
   *
   * @param fWidth
   *        Width. Must be &ge; 0.
   * @param fHeight
   *        Height. Must be &ge; 0.
   */
  public SizeSpec (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    // ValueEnforcer.isGE0 (fWidth, "Width");
    // ValueEnforcer.isGE0 (fHeight, "Height");

    m_fWidth = fWidth;
    m_fHeight = fHeight;
  }

  /**
   * @return Width. Always &ge; 0.
   */
  @Nonnegative
  public float getWidth ()
  {
    return m_fWidth;
  }

  /**
   * @return Height. Always &ge; 0.
   */
  @Nonnegative
  public float getHeight ()
  {
    return m_fHeight;
  }

  @Nonnull
  public PDRectangle getAsRectangle ()
  {
    return new PDRectangle (m_fWidth, m_fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec plus (final float fWidth, final float fHeight)
  {
    return new SizeSpec (m_fWidth + fWidth, m_fHeight + fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec plus (@Nonnull final SizeSpec aOther)
  {
    return plus (aOther.m_fWidth, aOther.m_fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec minus (final float fWidth, final float fHeight)
  {
    return new SizeSpec (m_fWidth - fWidth, m_fHeight - fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec minus (@Nonnull final SizeSpec aOther)
  {
    return minus (aOther.m_fWidth, aOther.m_fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec withWidth (final float fWidth)
  {
    return new SizeSpec (fWidth, m_fHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeSpec withHeight (final float fHeight)
  {
    return new SizeSpec (m_fWidth, fHeight);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SizeSpec rhs = (SizeSpec) o;
    return EqualsHelper.equals (m_fWidth, rhs.m_fWidth) && EqualsHelper.equals (m_fHeight, rhs.m_fHeight);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_fWidth).append (m_fHeight).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Width", m_fWidth).append ("Height", m_fHeight).getToString ();
  }

  @Nonnull
  public static SizeSpec create (@Nonnull final PDRectangle aRect)
  {
    return new SizeSpec (aRect.getWidth (), aRect.getHeight ());
  }

  @Nonnull
  public static SizeSpec createMM (final float fWidth, final float fHeight)
  {
    return new SizeSpec (PLConvert.mm2units (fWidth), PLConvert.mm2units (fHeight));
  }

  @Nonnull
  public static SizeSpec width (final float fWidth)
  {
    return new SizeSpec (fWidth, DEFAULT_FLOAT);
  }

  @Nonnull
  public static SizeSpec height (final float fHeight)
  {
    return new SizeSpec (DEFAULT_FLOAT, fHeight);
  }
}
