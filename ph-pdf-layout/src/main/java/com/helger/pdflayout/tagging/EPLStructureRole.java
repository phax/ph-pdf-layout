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
package com.helger.pdflayout.tagging;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * The standard structure types of a tagged PDF (ISO 32000-1, 14.8.4) that an element can have. The
 * ID is the structure type written to the PDF.
 *
 * @author Philip Helger
 * @since 8.4.0
 */
public enum EPLStructureRole implements IHasID <String>
{
  /** The whole document - the root element, created automatically */
  DOCUMENT ("Document"),
  /** A large division of a document */
  PART ("Part"),
  /** A self-contained body of text */
  ART ("Art"),
  /** A section */
  SECT ("Sect"),
  /** A generic block-level grouping element */
  DIV ("Div"),
  /** A block of quoted text */
  BLOCK_QUOTE ("BlockQuote"),
  /** A caption of a table or figure */
  CAPTION ("Caption"),
  /** A paragraph */
  P ("P"),
  /** A heading without a level */
  H ("H"),
  /** A heading of level 1 */
  H1 ("H1"),
  /** A heading of level 2 */
  H2 ("H2"),
  /** A heading of level 3 */
  H3 ("H3"),
  /** A heading of level 4 */
  H4 ("H4"),
  /** A heading of level 5 */
  H5 ("H5"),
  /** A heading of level 6 */
  H6 ("H6"),
  /** A list */
  L ("L"),
  /** A list item, containing {@link #LBL} and {@link #LBODY} */
  LI ("LI"),
  /** The label of a list item - the bullet or number */
  LBL ("Lbl"),
  /** The body of a list item */
  LBODY ("LBody"),
  /** A table */
  TABLE ("Table"),
  /** A table row */
  TR ("TR"),
  /** A table header cell */
  TH ("TH"),
  /** A table data cell */
  TD ("TD"),
  /** A generic inline element */
  SPAN ("Span"),
  /** An inline quote */
  QUOTE ("Quote"),
  /** A note, e.g. a footnote */
  NOTE ("Note"),
  /** A citation or cross reference */
  REFERENCE ("Reference"),
  /** Computer program text */
  CODE ("Code"),
  /** A link - contains the link annotation and its text */
  LINK ("Link"),
  /** A figure; it should have an alternate text */
  FIGURE ("Figure"),
  /** A formula; it should have an alternate text */
  FORMULA ("Formula");

  private final String m_sID;

  EPLStructureRole (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  /**
   * @return The structure type written to the PDF, e.g. <code>P</code>.
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Find the role with the provided ID.
   *
   * @param sID
   *        The structure type, e.g. <code>P</code>. May be <code>null</code>.
   * @return <code>null</code> if no such role exists.
   */
  @Nullable
  public static EPLStructureRole getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPLStructureRole.class, sID);
  }
}
