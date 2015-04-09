/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Constants for this project.
 *
 * @author Philip Helger
 */
@Immutable
public final class CPageLayoutPDF
{
  /** Default user space unit DPI */
  public static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;

  @PresentForCodeCoverage
  private static final CPageLayoutPDF s_aInstance = new CPageLayoutPDF ();

  private CPageLayoutPDF ()
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
    return fMillimeters / CGlobal.MM_PER_INCH * nDPI;
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
    return fCentimeters / CGlobal.CM_PER_INCH * nDPI;
  }
}
