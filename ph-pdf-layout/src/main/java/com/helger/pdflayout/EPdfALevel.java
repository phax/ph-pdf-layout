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
package com.helger.pdflayout;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * The PDF/A part and conformance level that {@link PageLayoutPDF} can declare. The ID is the
 * lower case combination of part and conformance level, e.g. <code>3b</code>.
 *
 * @author Philip Helger
 * @since 8.4.0
 */
public enum EPdfALevel implements IHasID <String>
{
  /** PDF/A-2b (ISO 19005-2, basic) */
  PDF_A_2B ("2b", 2, "B"),
  /** PDF/A-2u (ISO 19005-2, basic plus Unicode mapping of all text) */
  PDF_A_2U ("2u", 2, "U"),
  /** PDF/A-3b (ISO 19005-3, basic) */
  PDF_A_3B ("3b", 3, "B"),
  /** PDF/A-3u (ISO 19005-3, basic plus Unicode mapping of all text) */
  PDF_A_3U ("3u", 3, "U"),
  /**
   * PDF/A-3a (ISO 19005-3, accessible). This level requires a tagged PDF. It is what
   * {@link PageLayoutPDF#setCreatePDF_A(boolean)} declares.
   */
  PDF_A_3A ("3a", 3, "A");

  private final String m_sID;
  private final int m_nPart;
  private final String m_sConformance;

  EPdfALevel (@NonNull @Nonempty final String sID, final int nPart, @NonNull @Nonempty final String sConformance)
  {
    m_sID = sID;
    m_nPart = nPart;
    m_sConformance = sConformance;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The PDF/A part, e.g. 3 for PDF/A-3.
   */
  public int getPart ()
  {
    return m_nPart;
  }

  /**
   * @return The PDF/A conformance level as written to the XMP metadata, e.g. "B".
   */
  @NonNull
  @Nonempty
  public String getConformance ()
  {
    return m_sConformance;
  }

  /**
   * @return <code>true</code> if this level requires a tagged PDF (level A).
   */
  public boolean isTagged ()
  {
    return "A".equals (m_sConformance);
  }

  /**
   * @return <code>true</code> if this level allows arbitrary embedded files (PDF/A-3 and later).
   */
  public boolean isArbitraryEmbeddedFilesAllowed ()
  {
    return m_nPart >= 3;
  }

  /**
   * Find the level with the provided ID.
   *
   * @param sID
   *        The ID to search, e.g. <code>3b</code>. May be <code>null</code>.
   * @return <code>null</code> if no such level exists.
   */
  @Nullable
  public static EPdfALevel getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPdfALevel.class, sID);
  }
}
