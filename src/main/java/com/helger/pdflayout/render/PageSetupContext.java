/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.render;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * This class contains the context for setting a new page in the PDF.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class PageSetupContext
{
  private final PDDocument m_aDoc;
  private final PDPage m_aPage;

  public PageSetupContext (@Nonnull final PDDocument aDoc, @Nonnull final PDPage aPage)
  {
    if (aDoc == null)
      throw new NullPointerException ("doc");
    if (aPage == null)
      throw new NullPointerException ("page");
    m_aDoc = aDoc;
    m_aPage = aPage;
  }

  /**
   * @return the document
   */
  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDoc;
  }

  /**
   * @return the new page
   */
  @Nonnull
  public PDPage getPage ()
  {
    return m_aPage;
  }
}
