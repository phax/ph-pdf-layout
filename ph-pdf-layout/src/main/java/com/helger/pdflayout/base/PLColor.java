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
package com.helger.pdflayout.base;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * A replacement for java.awt.color that is not available on Android.
 *
 * @author Philip Helger
 * @since 7.2.0
 */
@Immutable
public class PLColor
{
  /**
   * The color white. In the default sRGB space.
   */
  public static final PLColor WHITE = new PLColor (255, 255, 255);

  /**
   * The color light gray. In the default sRGB space.
   */
  public static final PLColor LIGHT_GRAY = new PLColor (192, 192, 192);

  /**
   * The color gray. In the default sRGB space.
   */
  public static final PLColor GRAY = new PLColor (128, 128, 128);

  /**
   * The color dark gray. In the default sRGB space.
   */
  public static final PLColor DARK_GRAY = new PLColor (64, 64, 64);

  /**
   * The color black. In the default sRGB space.
   */
  public static final PLColor BLACK = new PLColor (0, 0, 0);

  /**
   * The color red. In the default sRGB space.
   */
  public static final PLColor RED = new PLColor (255, 0, 0);

  /**
   * The color pink. In the default sRGB space.
   */
  public static final PLColor PINK = new PLColor (255, 175, 175);

  /**
   * The color orange. In the default sRGB space.
   */
  public static final PLColor ORANGE = new PLColor (255, 200, 0);

  /**
   * The color yellow. In the default sRGB space.
   */
  public static final PLColor YELLOW = new PLColor (255, 255, 0);

  /**
   * The color green. In the default sRGB space.
   */
  public static final PLColor GREEN = new PLColor (0, 255, 0);

  /**
   * The color magenta. In the default sRGB space.
   */
  public static final PLColor MAGENTA = new PLColor (255, 0, 255);

  /**
   * The color cyan. In the default sRGB space.
   */
  public static final PLColor CYAN = new PLColor (0, 255, 255);

  /**
   * The color blue. In the default sRGB space.
   */
  public static final PLColor BLUE = new PLColor (0, 0, 255);

  private final int m_nRed;

  private final int m_nGreen;

  private final int m_nBlue;

  public PLColor (final int r, final int g, final int b)
  {
    ValueEnforcer.isBetweenInclusive (r, "red", 0, 255);
    ValueEnforcer.isBetweenInclusive (g, "green", 0, 255);
    ValueEnforcer.isBetweenInclusive (b, "blue", 0, 255);
    m_nRed = r;
    m_nGreen = g;
    m_nBlue = b;
  }

  public int getRed ()
  {
    return m_nRed;
  }

  public int getGreen ()
  {
    return m_nGreen;
  }

  public int getBlue ()
  {
    return m_nBlue;
  }

  @Nonnull
  public PDColor getAsPDColor ()
  {
    final float [] aComponents = { m_nRed / 255f, m_nGreen / 255f, m_nBlue / 255f };
    return new PDColor (aComponents, PDDeviceRGB.INSTANCE);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PLColor rhs = (PLColor) o;
    return m_nRed == rhs.m_nRed && m_nGreen == rhs.m_nGreen && m_nBlue == rhs.m_nBlue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nRed).append (m_nGreen).append (m_nBlue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Red", m_nRed)
                                       .append ("Green", m_nGreen)
                                       .append ("Blue", m_nBlue)
                                       .getToString ();
  }

  @Nonnull
  public static PLColor gray (final int nPart)
  {
    return new PLColor (nPart, nPart, nPart);
  }
}
