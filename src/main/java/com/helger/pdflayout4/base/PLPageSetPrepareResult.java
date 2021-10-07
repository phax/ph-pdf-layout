/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.base;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Page set prepare result. Used only internally.
 *
 * @author Philip Helger
 */
public final class PLPageSetPrepareResult
{
  private PLMarginBorderPadding m_aFirstPageMBP;
  private float m_fFirstHeaderHeight = Float.NaN;
  private float m_fHeaderHeight = Float.NaN;
  private final ICommonsList <PLElementWithSize> m_aContentHeight = new CommonsArrayList <> ();
  private float m_fFirstFooterHeight = Float.NaN;
  private float m_fFooterHeight = Float.NaN;
  private final ICommonsList <ICommonsList <PLElementWithSize>> m_aPerPageElements = new CommonsArrayList <> ();

  PLPageSetPrepareResult ()
  {}

  @Nonnull
  PLMarginBorderPadding getFirstPageMBP ()
  {
    final PLMarginBorderPadding ret = m_aFirstPageMBP;
    if (ret == null)
      throw new IllegalStateException ("No first page margin border padding present");
    return ret;
  }

  void setFirstPageMBP (@Nonnull final PLMarginBorderPadding aFirstPageMBP)
  {
    ValueEnforcer.notNull (aFirstPageMBP, "FirstPageMBP");
    m_aFirstPageMBP = aFirstPageMBP;
  }

  /**
   * @param nPageIndex
   *        0-based page index
   * @return Page header height without margin, border and padding.
   */
  float getHeaderHeight (@Nonnegative final int nPageIndex)
  {
    if (nPageIndex == 0 && !Float.isNaN (m_fFirstHeaderHeight))
      return m_fFirstHeaderHeight;
    return m_fHeaderHeight;
  }

  /**
   * Set the page header height.
   *
   * @param fHeaderHeight
   *        Height without margin, border and padding.
   */
  void setHeaderHeight (final float fHeaderHeight)
  {
    // Set the maximum value only
    if (Float.isNaN (m_fHeaderHeight))
      m_fHeaderHeight = fHeaderHeight;
    else
      m_fHeaderHeight = Math.max (m_fHeaderHeight, fHeaderHeight);
  }

  /**
   * Set the page header height of the first page. This method may only be
   * called once.
   *
   * @param fFooterHeight
   *        Height without padding or margin.
   */
  void setFirstHeaderHeight (final float fHeaderHeight)
  {
    // Set the maximum value only
    ValueEnforcer.isTrue (Float.isNaN (m_fFirstHeaderHeight), "First header height was already set");
    m_fFirstHeaderHeight = fHeaderHeight;
  }

  /**
   * @param aElement
   *        The element to be added. May not be <code>null</code>. The element
   *        height must be without padding or margin.
   */
  void addElement (@Nonnull final PLElementWithSize aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");
    m_aContentHeight.add (aElement);
  }

  /**
   * @return A list of all elements. Never <code>null</code>. The height of the
   *         contained elements is without padding or margin.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <PLElementWithSize> getAllElements ()
  {
    return m_aContentHeight.getClone ();
  }

  @Nonnull
  @ReturnsMutableObject ("speed")
  ICommonsList <ICommonsList <PLElementWithSize>> directGetPerPageElements ()
  {
    return m_aPerPageElements;
  }

  /**
   * Add a list of elements for a single page. This implicitly creates a new
   * page.
   *
   * @param aCurPageElements
   *        The list to use. May neither be <code>null</code> nor empty.
   */
  void addPerPageElements (@Nonnull @Nonempty final ICommonsList <PLElementWithSize> aCurPageElements)
  {
    ValueEnforcer.notEmptyNoNullValue (aCurPageElements, "CurPageElements");
    m_aPerPageElements.add (aCurPageElements);
  }

  @Nonnegative
  public int getPageCount ()
  {
    return m_aPerPageElements.size ();
  }

  @Nonnegative
  public int getPageNumber ()
  {
    return getPageCount () + 1;
  }

  /**
   * @param nPageIndex
   *        0-based page index
   * @return Page footer height without padding or margin.
   */
  float getFooterHeight (@Nonnegative final int nPageIndex)
  {
    if (nPageIndex == 0 && !Float.isNaN (m_fFirstFooterHeight))
      return m_fFirstFooterHeight;
    return m_fFooterHeight;
  }

  /**
   * Set the page footer height of the first page. This method may only be
   * called once.
   *
   * @param fFooterHeight
   *        Height without padding or margin.
   */
  void setFirstFooterHeight (final float fFooterHeight)
  {
    // Set the maximum value only
    ValueEnforcer.isTrue (Float.isNaN (m_fFirstFooterHeight), "First footer height was already set");
    m_fFirstFooterHeight = fFooterHeight;
  }

  /**
   * Set the page footer height. The maximum height is used.
   *
   * @param fFooterHeight
   *        Height without padding or margin.
   */
  void setFooterHeight (final float fFooterHeight)
  {
    // Set the maximum value only
    if (Float.isNaN (m_fFooterHeight))
      m_fFooterHeight = fFooterHeight;
    else
      m_fFooterHeight = Math.max (m_fFooterHeight, fFooterHeight);
  }
}
