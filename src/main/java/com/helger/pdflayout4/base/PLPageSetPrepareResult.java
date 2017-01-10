package com.helger.pdflayout4.base;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;

/**
 * Page set preparae result. Used only internally.
 * 
 * @author Philip Helger
 */
public final class PLPageSetPrepareResult implements Serializable
{
  private float m_fHeaderHeight = Float.NaN;
  private final ICommonsList <PLElementWithSize> m_aContentHeight = new CommonsArrayList <> ();
  private float m_fFooterHeight = Float.NaN;
  private final ICommonsList <ICommonsList <PLElementWithSize>> m_aPerPageElements = new CommonsArrayList <> ();

  PLPageSetPrepareResult ()
  {}

  /**
   * Set the page header height.
   *
   * @param fHeaderHeight
   *        Height without margin, border and padding.
   */
  void setHeaderHeight (final float fHeaderHeight)
  {
    m_fHeaderHeight = fHeaderHeight;
  }

  /**
   * @return Page header height without margin, border and padding.
   */
  public float getHeaderHeight ()
  {
    return m_fHeaderHeight;
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

  /**
   * Set the page footer height.
   *
   * @param fFooterHeight
   *        Height without padding or margin.
   */
  void setFooterHeight (final float fFooterHeight)
  {
    m_fFooterHeight = fFooterHeight;
  }

  /**
   * @return Page footer height without padding or margin.
   */
  public float getFooterHeight ()
  {
    return m_fFooterHeight;
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

  @Nonnull
  @ReturnsMutableObject ("speed")
  ICommonsList <ICommonsList <PLElementWithSize>> directGetPerPageElements ()
  {
    return m_aPerPageElements;
  }
}
