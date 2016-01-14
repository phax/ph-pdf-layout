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

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class contains the styling of a single border part. Currently only the
 * color can be set.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class BorderStyleSpec
{
  /** The default border color: black */
  public static final Color DEFAULT_COLOR = Color.BLACK;

  /** The default border style: solid */
  public static final LineDashPatternSpec DEFAULT_LINE_DASH_PATTERN = LineDashPatternSpec.SOLID;

  private final Color m_aColor;
  private final LineDashPatternSpec m_aLineDashPattern;

  public BorderStyleSpec ()
  {
    this (DEFAULT_COLOR, DEFAULT_LINE_DASH_PATTERN);
  }

  public BorderStyleSpec (@Nonnull final Color aColor)
  {
    this (aColor, DEFAULT_LINE_DASH_PATTERN);
  }

  public BorderStyleSpec (@Nonnull final LineDashPatternSpec aLineDashPattern)
  {
    this (DEFAULT_COLOR, aLineDashPattern);
  }

  public BorderStyleSpec (@Nonnull final Color aColor, @Nonnull final LineDashPatternSpec aLineDashPattern)
  {
    ValueEnforcer.notNull (aColor, "Color");
    ValueEnforcer.notNull (aLineDashPattern, "LineDashPattern");

    m_aColor = aColor;
    m_aLineDashPattern = aLineDashPattern;
  }

  /**
   * @return The border color to use. Never <code>null</code>.
   */
  @Nonnull
  public Color getColor ()
  {
    return m_aColor;
  }

  /**
   * @return The border line style to use. Never <code>null</code>.
   */
  @Nonnull
  public LineDashPatternSpec getLineDashPattern ()
  {
    return m_aLineDashPattern;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BorderStyleSpec rhs = (BorderStyleSpec) o;
    return m_aColor.equals (rhs.m_aColor) && m_aLineDashPattern.equals (rhs.m_aLineDashPattern);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aColor).append (m_aLineDashPattern).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("color", m_aColor)
                                       .append ("lineDashPattern", m_aLineDashPattern)
                                       .toString ();
  }
}
