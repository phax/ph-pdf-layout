/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.link;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.helger.commons.annotation.Nonempty;

/**
 * Contains the style for links and other interactive elements.
 *
 * @author Philip Helger
 */
public enum ELinkBorderStyle
{
  /**
   * Constant for the name of a solid style.
   */
  SOLID (PDBorderStyleDictionary.STYLE_SOLID),

  /**
   * Constant for the name of a dashed style.
   */
  DASHED (PDBorderStyleDictionary.STYLE_DASHED),

  /**
   * Constant for the name of a beveled style.
   */
  BEVELED (PDBorderStyleDictionary.STYLE_BEVELED),

  /**
   * Constant for the name of a inset style.
   */
  INSET (PDBorderStyleDictionary.STYLE_INSET),

  /**
   * Constant for the name of a underline style.
   */
  UNDERLINE (PDBorderStyleDictionary.STYLE_UNDERLINE);

  private final String m_sID;

  ELinkBorderStyle (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }
}
