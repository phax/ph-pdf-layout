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
package com.helger.pdflayout4.spec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the type of width unit of measure used.
 *
 * @author Philip Helger
 */
public enum EValueUOMType implements IHasID <String>
{
  /** Absolute value provided */
  ABSOLUTE ("abs"),
  /** Percentage value provided */
  PERCENTAGE ("perc"),
  /** '*' value provided */
  STAR ("star"),
  /** Automatic scaling */
  AUTO ("auto");

  private final String m_sID;

  EValueUOMType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this unit of measure requires a value,
   *         <code>false</code> if not.
   */
  public boolean isValueRequired ()
  {
    return this == ABSOLUTE || this == PERCENTAGE;
  }

  /**
   * @return <code>true</code> if this unit of measure depends on the width of
   *         the surrounding element, <code>false</code> if this unit of measure
   *         defines the width based on the content of this element.
   */
  public boolean isOuterElementDependent ()
  {
    return this == ABSOLUTE || this == PERCENTAGE || this == STAR;
  }

  @Nullable
  public static EValueUOMType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EValueUOMType.class, sID);
  }
}
