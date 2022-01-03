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
package com.helger.pdflayout4.link;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import com.helger.commons.annotation.Nonempty;

/**
 * Contains the highlight mode for links.
 *
 * @author Philip Helger
 */
public enum ELinkHighlightMode
{
  /**
   * Constant values of the Text as defined in the PDF 1.6 reference Table 8.19.
   */
  NONE (PDAnnotationLink.HIGHLIGHT_MODE_NONE),
  /**
   * Constant values of the Text as defined in the PDF 1.6 reference Table 8.19.
   */
  INVERT (PDAnnotationLink.HIGHLIGHT_MODE_INVERT),
  /**
   * Constant values of the Text as defined in the PDF 1.6 reference Table 8.19.
   */
  OUTLINE (PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE),
  /**
   * Constant values of the Text as defined in the PDF 1.6 reference Table 8.19.
   */
  PUSH (PDAnnotationLink.HIGHLIGHT_MODE_PUSH);

  // Default in PDFBox
  public static final ELinkHighlightMode DEFAULT = INVERT;

  private final String m_sID;

  ELinkHighlightMode (@Nonnull @Nonempty final String sID)
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
