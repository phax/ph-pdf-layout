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
package com.helger.pdflayout;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Conversion utility methods for this project.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLConvert
{
  /** Default user space unit DPI: 72 */
  public static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;

  @PresentForCodeCoverage
  private static final PLConvert s_aInstance = new PLConvert ();

  private PLConvert ()
  {}

  /**
   * Utility function to convert from millimeters to PDF units. It uses
   * {@link #DEFAULT_USER_SPACE_UNIT_DPI} for the DPIs.
   *
   * @param fMillimeters
   *        Source millimeters
   * @return The PDF units.
   */
  public static float mm2units (final float fMillimeters)
  {
    return mm2units (fMillimeters, DEFAULT_USER_SPACE_UNIT_DPI);
  }

  /**
   * Utility function to convert from millimeters to PDF units.
   *
   * @param fMillimeters
   *        Source millimeters
   * @param nDPI
   *        User space DPIs to use.
   * @return The PDF units.
   */
  public static float mm2units (final float fMillimeters, @Nonnegative final int nDPI)
  {
    return fMillimeters * nDPI / CGlobal.MM_PER_INCH;
  }

  /**
   * Utility function to convert from PDF units to millimeters. It uses
   * {@link #DEFAULT_USER_SPACE_UNIT_DPI} for the DPIs.
   *
   * @param fUnits
   *        Source PDF units
   * @return The millimeters.
   */
  public static float units2mm (final float fUnits)
  {
    return units2mm (fUnits, DEFAULT_USER_SPACE_UNIT_DPI);
  }

  /**
   * Utility function to convert from PDF units to millimeters.
   *
   * @param fUnits
   *        Source PDF units
   * @param nDPI
   *        User space DPIs to use.
   * @return The millimeters.
   */
  public static float units2mm (final float fUnits, @Nonnegative final int nDPI)
  {
    return fUnits * CGlobal.MM_PER_INCH / nDPI;
  }

  /**
   * Utility function to convert from centimeters to PDF units. It uses
   * {@link #DEFAULT_USER_SPACE_UNIT_DPI} for the DPIs.
   *
   * @param fCentimeters
   *        Source centimeters
   * @return The PDF units.
   */
  public static float cm2units (final float fCentimeters)
  {
    return cm2units (fCentimeters, DEFAULT_USER_SPACE_UNIT_DPI);
  }

  /**
   * Utility function to convert from centimeters to PDF units.
   *
   * @param fCentimeters
   *        Source centimeters
   * @param nDPI
   *        User space DPIs to use.
   * @return The PDF units.
   */
  public static float cm2units (final float fCentimeters, @Nonnegative final int nDPI)
  {
    return fCentimeters * nDPI / CGlobal.CM_PER_INCH;
  }

  /**
   * Utility function to convert from PDF units to centimeters. It uses
   * {@link #DEFAULT_USER_SPACE_UNIT_DPI} for the DPIs.
   *
   * @param fUnits
   *        Source PDF units
   * @return The centimeters.
   */
  public static float units2cm (final float fUnits)
  {
    return units2cm (fUnits, DEFAULT_USER_SPACE_UNIT_DPI);
  }

  /**
   * Utility function to convert from PDF units to centimeters.
   *
   * @param fUnits
   *        Source PDF units
   * @param nDPI
   *        User space DPIs to use.
   * @return The centimeters.
   */
  public static float units2cm (final float fUnits, @Nonnegative final int nDPI)
  {
    return fUnits * CGlobal.CM_PER_INCH / nDPI;
  }

  public static float getWidthForFontSize (final float fWidth, final float fFontSize)
  {
    // The width is in 1000 unit of text space, ie 333 or 777
    return fWidth * fFontSize / 1000f;
  }
}
