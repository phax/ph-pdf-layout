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
package com.plenigo.pdflayout.spec;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.Since;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the possible horizontal alignments.
 *
 * @author Philip Helger
 */
public enum EHorzAlignment implements IHasID<String> {
    /**
     * Align left
     */
    LEFT("left"),

    /**
     * Align center
     */
    CENTER("center"),

    /**
     * Align right
     */
    RIGHT("right"),

    /**
     * Justify all text. This alignment has no effect for boxes, it only works on
     * PLText!
   */
  @Since ("5.0.3")
  JUSTIFY("justify");

  public static final EHorzAlignment DEFAULT = LEFT;

  private final String m_sID;

  EHorzAlignment (@Nonnull @Nonempty final String sID)
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
  public static EHorzAlignment getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EHorzAlignment.class, sID);
  }
}
