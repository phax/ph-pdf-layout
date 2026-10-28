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
package com.helger.pdflayout.pdfa;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * The relationship of an associated (embedded) file to the PDF document, as written to the
 * <code>/AFRelationship</code> entry of the file specification (PDF/A-3, ISO 32000-2). The ID is
 * the value written to the PDF.
 *
 * @author Philip Helger
 * @since 8.4.0
 */
public enum EPdfAFRelationship implements IHasID <String>
{
  /** The original source material of the content of the PDF */
  SOURCE ("Source"),
  /** Information used to derive a visual presentation, such as a table or a graph */
  DATA ("Data"),
  /** An alternative representation of the content, for example audio */
  ALTERNATIVE ("Alternative"),
  /** A supplemental representation of the original source or data that may be more easily consumable */
  SUPPLEMENT ("Supplement"),
  /** The relationship is not known or cannot be described with the other values */
  UNSPECIFIED ("Unspecified");

  private final String m_sID;

  EPdfAFRelationship (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Find the relationship with the provided ID.
   *
   * @param sID
   *        The ID to search, e.g. <code>Source</code>. May be <code>null</code>.
   * @return <code>null</code> if no such relationship exists.
   */
  @Nullable
  public static EPdfAFRelationship getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPdfAFRelationship.class, sID);
  }
}
