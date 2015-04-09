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
  public static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;

  /** Conversion rate from MM to PDF units */
  public static final float MM_TO_UNITS = 1 / CGlobal.MM_PER_INCH * DEFAULT_USER_SPACE_UNIT_DPI;

  /** Conversion rate from CEM to PDF units */
  public static final float CM_TO_UNITS = 1 / CGlobal.CM_PER_INCH * DEFAULT_USER_SPACE_UNIT_DPI;

  @PresentForCodeCoverage
  private static final CPageLayoutPDF s_aInstance = new CPageLayoutPDF ();

  private CPageLayoutPDF ()
  {}
}
