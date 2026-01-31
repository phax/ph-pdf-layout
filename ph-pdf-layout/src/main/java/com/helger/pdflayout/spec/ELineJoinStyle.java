/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.base.id.IHasIntID;
import com.helger.base.lang.EnumHelper;

/**
 * Possible line join styles.
 *
 * @author Philip Helger
 * @since 7.4.0
 */
public enum ELineJoinStyle implements IHasIntID
{
  MITER (0),
  ROUND (1),
  BEVEL (2);

  private final int m_nValue;

  ELineJoinStyle (@Nonnegative final int nValue)
  {
    m_nValue = nValue;
  }

  @Nonnegative
  public int getID ()
  {
    return m_nValue;
  }

  @Nullable
  public static ELineJoinStyle getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (ELineJoinStyle.class, nID);
  }
}
