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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.misc.Since;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * Represents the possible horizontal alignments.
 *
 * @author Philip Helger
 */
public enum EHorzAlignment implements IHasID <String>
{
  /** Align left */
  LEFT ("left"),

  /** Align center */
  CENTER ("center"),

  /** Align right */
  RIGHT ("right"),

  /**
   * Justify all text. This alignment has no effect for boxes, it only works on
   * PLText!
   */
  @Since ("5.0.3")
  JUSTIFY("justify"),

  /**
   * This is a special case for justifying text, but without justifying explicit
   * newlines and not the last line. This alignment has no effect for boxes, it
   * only works on PLText!
   */
  @Since ("7.3.3")
  BLOCK("block");

  public static final EHorzAlignment DEFAULT = LEFT;

  private final String m_sID;

  EHorzAlignment (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @NonNull
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
