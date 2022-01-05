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
package com.helger.pdflayout.render;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.string.StringParser;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.EPLPlaceholder;
import com.helger.pdflayout.base.PLPageSet;

/**
 * This class describes the index of the current page.
 *
 * @author Philip Helger
 */
@Immutable
public class PagePreRenderContext
{
  private final PLPageSet m_aPageSet;
  private final PDDocument m_aDoc;
  private final PDPage m_aPage;
  private final int m_nPageSetIndex;
  private final int m_nPageSetCount;
  private final int m_nPageSetPageIndex;
  private final int m_nPageSetPageCount;
  private final int m_nTotalPageIndex;
  private final int m_nTotalPageCount;
  private final ICommonsOrderedMap <String, String> m_aPlaceholders = new CommonsLinkedHashMap <> ();

  public PagePreRenderContext (@Nonnull final PLPageSet aPageSet,
                               @Nonnull final PDDocument aDoc,
                               @Nonnull final PDPage aPage,
                               @Nonnegative final int nPageSetIndex,
                               @Nonnegative final int nPageSetCount,
                               @Nonnegative final int nPageSetPageIndex,
                               @Nonnegative final int nPageSetPageCount,
                               @Nonnegative final int nTotalPageIndex,
                               @Nonnegative final int nTotalPageCount)

  {
    ValueEnforcer.notNull (aPageSet, "PageSet");
    ValueEnforcer.notNull (aDoc, "Document");
    ValueEnforcer.notNull (aPage, "Page");
    ValueEnforcer.isGE0 (nPageSetIndex, "PageSetIndex");
    ValueEnforcer.isGE0 (nPageSetCount, "PageSetCount");
    ValueEnforcer.isGE0 (nPageSetPageIndex, "PageSetPageIndex");
    ValueEnforcer.isGE0 (nPageSetPageCount, "PageSetPageCount");
    ValueEnforcer.isGE0 (nTotalPageIndex, "TotalPageIndex");
    ValueEnforcer.isGE0 (nTotalPageCount, "TotalPageCount");

    m_aPageSet = aPageSet;
    m_aDoc = aDoc;
    m_aPage = aPage;
    m_nPageSetIndex = nPageSetIndex;
    m_nPageSetCount = nPageSetCount;
    m_nPageSetPageIndex = nPageSetPageIndex;
    m_nPageSetPageCount = nPageSetPageCount;
    m_nTotalPageIndex = nTotalPageIndex;
    m_nTotalPageCount = nTotalPageCount;

    // Add default placeholders
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_INDEX.getVariable (), Integer.toString (getPageSetIndex ()));
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_NUMBER.getVariable (), Integer.toString (getPageSetNumber ()));
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_COUNT.getVariable (), Integer.toString (getPageSetCount ()));
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_PAGE_INDEX.getVariable (), Integer.toString (getPageSetPageIndex ()));
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_PAGE_NUMBER.getVariable (), Integer.toString (getPageSetPageNumber ()));
    m_aPlaceholders.put (EPLPlaceholder.PAGESET_PAGE_COUNT.getVariable (), Integer.toString (getPageSetPageCount ()));
    m_aPlaceholders.put (EPLPlaceholder.TOTAL_PAGE_INDEX.getVariable (), Integer.toString (getTotalPageIndex ()));
    m_aPlaceholders.put (EPLPlaceholder.TOTAL_PAGE_NUMBER.getVariable (), Integer.toString (getTotalPageNumber ()));
    m_aPlaceholders.put (EPLPlaceholder.TOTAL_PAGE_COUNT.getVariable (), Integer.toString (getTotalPageCount ()));
  }

  /**
   * @return the document
   */
  @Nonnull
  public PLPageSet getPageSet ()
  {
    return m_aPageSet;
  }

  /**
   * @return the PDFBox document
   */
  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDoc;
  }

  /**
   * @return the new PDFBox page
   */
  @Nonnull
  public PDPage getPage ()
  {
    return m_aPage;
  }

  /**
   * @return The index of the current page set. 0-based. Always &ge; 0.
   */
  @Nonnegative
  public int getPageSetIndex ()
  {
    return m_nPageSetIndex;
  }

  /**
   * @return The number of the current page set. 1-based. Always &ge; 1.
   */
  @Nonnegative
  public int getPageSetNumber ()
  {
    return m_nPageSetIndex + 1;
  }

  /**
   * @return The total number of page sets. Always &ge; 1.
   */
  @Nonnegative
  public int getPageSetCount ()
  {
    return m_nPageSetCount;
  }

  /**
   * @return The index of the page in the current page set. 0-based. Always &ge;
   *         0.
   */
  @Nonnegative
  public int getPageSetPageIndex ()
  {
    return m_nPageSetPageIndex;
  }

  /**
   * @return The number of the page in the current page set. 1-based. Always
   *         &ge; 1.
   */
  @Nonnegative
  public int getPageSetPageNumber ()
  {
    return m_nPageSetPageIndex + 1;
  }

  /**
   * @return The total number of pages in the current page set. Always &ge; 0.
   */
  @Nonnegative
  public int getPageSetPageCount ()
  {
    return m_nPageSetPageCount;
  }

  /**
   * @return The index of the page over all page sets. 0-based. Always &ge; 0.
   */
  @Nonnegative
  public int getTotalPageIndex ()
  {
    return m_nTotalPageIndex;
  }

  /**
   * @return The number of the page over all page sets. 1-based. Always &ge; 1.
   */
  @Nonnegative
  public int getTotalPageNumber ()
  {
    return m_nTotalPageIndex + 1;
  }

  /**
   * @return The overall number of pages. Always &ge; 0.
   */
  @Nonnegative
  public int getTotalPageCount ()
  {
    return m_nTotalPageCount;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, String> getAllPlaceholders ()
  {
    return m_aPlaceholders.getClone ();
  }

  @Nullable
  public String getPlaceholder (@Nullable final String sKey)
  {
    return m_aPlaceholders.get (sKey);
  }

  public int getPlaceholderAsInt (@Nullable final String sKey, final int nDefault)
  {
    return StringParser.parseInt (getPlaceholder (sKey), nDefault);
  }

  public void addPlaceholder (@Nonnull @Nonempty final String sKey, final int nValue)
  {
    addPlaceholder (sKey, Integer.toString (nValue));
  }

  public void addPlaceholder (@Nonnull @Nonempty final String sKey, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sKey, "Key");
    ValueEnforcer.notNull (sValue, "Value");
    m_aPlaceholders.put (sKey, sValue);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("PageSet", m_aPageSet)
                                       .append ("PDDoc", m_aDoc)
                                       .append ("PDPage", m_aPage)
                                       .append ("PageSetIndex", m_nPageSetIndex)
                                       .append ("PageSetCount", m_nPageSetCount)
                                       .append ("PageSetPageIndex", m_nPageSetPageIndex)
                                       .append ("PageSetPageCount", m_nPageSetPageCount)
                                       .append ("TotalPageIndex", m_nTotalPageIndex)
                                       .append ("TotalPageCount", m_nTotalPageCount)
                                       .append ("Placeholders", m_aPlaceholders)
                                       .getToString ();
  }
}
