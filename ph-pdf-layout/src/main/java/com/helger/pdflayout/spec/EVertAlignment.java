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

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Represents the possible vertical alignments.
 *
 * @author Philip Helger
 */
public enum EVertAlignment implements IHasID <String>
{
  /** Align top */
  TOP ("top"),

  /** Align middle */
  MIDDLE ("middle"),

  /** Align bottom */
  BOTTOM ("bottom");

  public static final EVertAlignment DEFAULT = TOP;

  private final String m_sID;

  EVertAlignment (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EVertAlignment getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EVertAlignment.class, sID);
  }
}
