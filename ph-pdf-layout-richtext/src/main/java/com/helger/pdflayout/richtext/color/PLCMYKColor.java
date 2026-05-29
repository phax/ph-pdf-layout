/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.richtext.color;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.PLColor;

/**
 * A {@link PLColor} that carries CMYK components in addition to the inherited RGB approximation.
 * Created so the rich-text colour marker can emit CMYK without having to widen {@link PLColor}
 * itself.
 * <p>
 * The four components are stored as fractions in {@code [0, 1]}; the convenience
 * {@link #fromPercent(float, float, float, float)} factory accepts the more common {@code 0..100}
 * percent form. The inherited {@code getRed()}/{@code getGreen()}/{@code getBlue()} return a naive
 * RGB approximation computed via the standard {@code R = 255 * (1 - C) * (1 - K)} (etc.) formula —
 * adequate when something in the pipeline insists on RGB; the authoritative path is
 * {@link #getAsPDColor()} which emits a {@link PDDeviceCMYK}-typed {@link PDColor}.
 * <p>
 * Proposed by Christopher Dargel (vanDarg) at <a href=
 * "https://github.com/ralfstuckert/pdfbox-layout/issues/94">ralfstuckert/pdfbox-layout#94</a>.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public final class PLCMYKColor extends PLColor
{
  private final float m_fC;
  private final float m_fM;
  private final float m_fY;
  private final float m_fK;

  private static int _rgbR (final float fC, final float fK)
  {
    return Math.round (255f * (1f - fC) * (1f - fK));
  }

  private static int _rgbG (final float fM, final float fK)
  {
    return Math.round (255f * (1f - fM) * (1f - fK));
  }

  private static int _rgbB (final float fY, final float fK)
  {
    return Math.round (255f * (1f - fY) * (1f - fK));
  }

  /**
   * Construct with all four components as fractions in {@code [0, 1]}.
   *
   * @param fC
   *        cyan, 0..1
   * @param fM
   *        magenta, 0..1
   * @param fY
   *        yellow, 0..1
   * @param fK
   *        black (key), 0..1
   */
  public PLCMYKColor (final float fC, final float fM, final float fY, final float fK)
  {
    super (_rgbR (fC, fK), _rgbG (fM, fK), _rgbB (fY, fK));
    ValueEnforcer.isBetweenInclusive (fC, "C", 0f, 1f);
    ValueEnforcer.isBetweenInclusive (fM, "M", 0f, 1f);
    ValueEnforcer.isBetweenInclusive (fY, "Y", 0f, 1f);
    ValueEnforcer.isBetweenInclusive (fK, "K", 0f, 1f);
    m_fC = fC;
    m_fM = fM;
    m_fY = fY;
    m_fK = fK;
  }

  public float getC ()
  {
    return m_fC;
  }

  public float getM ()
  {
    return m_fM;
  }

  public float getY ()
  {
    return m_fY;
  }

  public float getK ()
  {
    return m_fK;
  }

  @Override
  @NonNull
  public PDColor getAsPDColor ()
  {
    return new PDColor (new float [] { m_fC, m_fM, m_fY, m_fK }, PDDeviceCMYK.INSTANCE);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PLCMYKColor rhs = (PLCMYKColor) o;
    return EqualsHelper.equals (m_fC, rhs.m_fC) &&
           EqualsHelper.equals (m_fM, rhs.m_fM) &&
           EqualsHelper.equals (m_fY, rhs.m_fY) &&
           EqualsHelper.equals (m_fK, rhs.m_fK);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_fC).append (m_fM).append (m_fY).append (m_fK).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("C", m_fC)
                                       .append ("M", m_fM)
                                       .append ("Y", m_fY)
                                       .append ("K", m_fK)
                                       .getToString ();
  }

  /**
   * Convenience for the more common {@code 0..100} percent form (the same shape suggested in the
   * issue: {@code {color_cmyk:75,15,0,20}}).
   *
   * @param fCPct
   *        cyan as percent, 0..100
   * @param fMPct
   *        magenta as percent, 0..100
   * @param fYPct
   *        yellow as percent, 0..100
   * @param fKPct
   *        black (key) as percent, 0..100
   * @return the CMYK colour.
   */
  @NonNull
  public static PLCMYKColor fromPercent (final float fCPct, final float fMPct, final float fYPct, final float fKPct)
  {
    return new PLCMYKColor (fCPct / 100f, fMPct / 100f, fYPct / 100f, fKPct / 100f);
  }
}
