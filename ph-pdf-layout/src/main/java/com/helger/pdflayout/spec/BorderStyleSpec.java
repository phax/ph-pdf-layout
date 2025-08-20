/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;

import jakarta.annotation.Nonnull;

/**
 * This class contains the styling of a single border part. Currently only the
 * color, the dash pattern and the line width can be set.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class BorderStyleSpec implements Serializable
{
  /** The default border color: black */
  public static final PLColor DEFAULT_COLOR = PLColor.BLACK;

  /** The default border style: solid */
  public static final LineDashPatternSpec DEFAULT_LINE_DASH_PATTERN = LineDashPatternSpec.SOLID;

  /**
   * Use the default line width if unspecified. A width of 0 is also valid and
   * would create a hair line
   */
  public static final float DEFAULT_LINE_WIDTH = 1f;

  public static final BorderStyleSpec EMPTY = new BorderStyleSpec ();

  private final PLColor m_aColor;
  private final LineDashPatternSpec m_aLineDashPattern;
  private final float m_fLineWidth;

  public BorderStyleSpec ()
  {
    this (DEFAULT_COLOR, DEFAULT_LINE_DASH_PATTERN, DEFAULT_LINE_WIDTH);
  }

  public BorderStyleSpec (@Nonnull final PLColor aColor)
  {
    this (aColor, DEFAULT_LINE_DASH_PATTERN, DEFAULT_LINE_WIDTH);
  }

  public BorderStyleSpec (@Nonnull final LineDashPatternSpec aLineDashPattern)
  {
    this (DEFAULT_COLOR, aLineDashPattern, DEFAULT_LINE_WIDTH);
  }

  public BorderStyleSpec (final float fLineWidth)
  {
    this (DEFAULT_COLOR, DEFAULT_LINE_DASH_PATTERN, fLineWidth);
  }

  public BorderStyleSpec (@Nonnull final PLColor aColor, final float fLineWidth)
  {
    this (aColor, DEFAULT_LINE_DASH_PATTERN, fLineWidth);
  }

  public BorderStyleSpec (@Nonnull final PLColor aColor, @Nonnull final LineDashPatternSpec aLineDashPattern)
  {
    this (aColor, aLineDashPattern, DEFAULT_LINE_WIDTH);
  }

  public BorderStyleSpec (@Nonnull final PLColor aColor,
                          @Nonnull final LineDashPatternSpec aLineDashPattern,
                          @Nonnegative final float fLineWidth)
  {
    ValueEnforcer.notNull (aColor, "Color");
    ValueEnforcer.notNull (aLineDashPattern, "LineDashPattern");
    ValueEnforcer.isFalse (Float.isNaN (fLineWidth), "LineWidth may not be NaN");
    ValueEnforcer.isGE0 (fLineWidth, "LineWidth");

    m_aColor = aColor;
    m_aLineDashPattern = aLineDashPattern;
    m_fLineWidth = fLineWidth;
  }

  /**
   * @return The border color to use. Never <code>null</code>.
   */
  @Nonnull
  public final PLColor getColor ()
  {
    return m_aColor;
  }

  /**
   * @return The border line style to use. Never <code>null</code>.
   */
  @Nonnull
  public final LineDashPatternSpec getLineDashPattern ()
  {
    return m_aLineDashPattern;
  }

  @Nonnegative
  public final float getLineWidth ()
  {
    return m_fLineWidth;
  }

  /**
   * @return <code>true</code> if all values are set to default,
   *         <code>false</code> otherwise.
   */
  public final boolean isDefault ()
  {
    return m_aColor.equals (DEFAULT_COLOR) &&
           m_aLineDashPattern.equals (DEFAULT_LINE_DASH_PATTERN) &&
           EqualsHelper.equals (m_fLineWidth, DEFAULT_LINE_WIDTH);
  }

  @Nonnull
  public BorderStyleSpec getCloneWithColor (@Nonnull final PLColor aNewColor)
  {
    ValueEnforcer.notNull (aNewColor, "NewColor");
    if (aNewColor.equals (m_aColor))
      return this;
    return new BorderStyleSpec (aNewColor, m_aLineDashPattern, m_fLineWidth);
  }

  @Nonnull
  public BorderStyleSpec getCloneWithLineDashPattern (@Nonnull final LineDashPatternSpec aNewLineDashPattern)
  {
    ValueEnforcer.notNull (aNewLineDashPattern, "NewLineDashPattern");
    if (aNewLineDashPattern.equals (m_aLineDashPattern))
      return this;
    return new BorderStyleSpec (m_aColor, aNewLineDashPattern, m_fLineWidth);
  }

  @Nonnull
  public BorderStyleSpec getCloneWithLineWidth (final float fNewLineWidth)
  {
    ValueEnforcer.isFalse (Float.isNaN (fNewLineWidth), "LineWidth may not be NaN");
    ValueEnforcer.isGE0 (fNewLineWidth, "LineWidth");
    if (EqualsHelper.equals (fNewLineWidth, m_fLineWidth))
      return this;
    return new BorderStyleSpec (m_aColor, m_aLineDashPattern, fNewLineWidth);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BorderStyleSpec rhs = (BorderStyleSpec) o;
    return m_aColor.equals (rhs.m_aColor) &&
           m_aLineDashPattern.equals (rhs.m_aLineDashPattern) &&
           EqualsHelper.equals (m_fLineWidth, rhs.m_fLineWidth);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aColor)
                                       .append (m_aLineDashPattern)
                                       .append (m_fLineWidth)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Color", m_aColor)
                                       .append ("LineDashPattern", m_aLineDashPattern)
                                       .append ("LineWidth", m_fLineWidth)
                                       .getToString ();
  }
}
