/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.vbox;

import java.io.IOException;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Vertical box - groups several rows.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLVBox <IMPLTYPE extends AbstractPLVBox <IMPLTYPE>>
                                     extends AbstractPLRenderableObject <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLVBox.class);

  protected final ICommonsList <PLVBoxRow> m_aRows = new CommonsArrayList<> ();

  /** prepare width (without padding and margin) */
  protected float [] m_aPreparedRowElementWidth;
  /** prepare height (without padding and margin) */
  protected float [] m_aPreparedRowElementHeight;

  public AbstractPLVBox ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLVBox <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    return thisAsT ();
  }

  /**
   * @return The number of rows. Always &ge; 0.
   */
  @Nonnegative
  public int getRowCount ()
  {
    return m_aRows.size ();
  }

  /**
   * @return All rows. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <PLVBoxRow> getAllRows ()
  {
    return m_aRows.getClone ();
  }

  /**
   * Get the row at the specified index.
   *
   * @param nIndex
   *        The index to use. Should be &ge; 0.
   * @return <code>null</code> if an invalid index was provided.
   */
  @Nullable
  public PLVBoxRow getRowAtIndex (@Nonnegative final int nIndex)
  {
    return m_aRows.getAtIndex (nIndex);
  }

  /**
   * @return The first row or <code>null</code> if no row is present.
   */
  @Nullable
  public PLVBoxRow getFirstRow ()
  {
    return m_aRows.getFirst ();
  }

  /**
   * @return The last row or <code>null</code> if no row is present.
   */
  @Nullable
  public PLVBoxRow getLastRow ()
  {
    return m_aRows.getLast ();
  }

  /**
   * Get the element in the row at the specified index.
   *
   * @param nIndex
   *        The index to use. Should be &ge; 0.
   * @return <code>null</code> if an invalid index was provided.
   */
  @Nullable
  public IPLRenderableObject <?> getRowElementAtIndex (@Nonnegative final int nIndex)
  {
    final PLVBoxRow aRow = getRowAtIndex (nIndex);
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the first row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public IPLRenderableObject <?> getFirstRowElement ()
  {
    final PLVBoxRow aRow = getFirstRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the last row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public IPLRenderableObject <?> getLastRowElement ()
  {
    final PLVBoxRow aRow = getLastRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  @Nonnull
  private PLVBoxRow _addAndReturnRow (@CheckForSigned final int nIndex, @Nonnull final IPLRenderableObject <?> aElement)
  {
    final PLVBoxRow aItem = new PLVBoxRow (aElement);
    if (nIndex < 0 || nIndex >= m_aRows.size ())
      m_aRows.add (aItem);
    else
      m_aRows.add (nIndex, aItem);
    return aItem;
  }

  /**
   * Add a row to this VBox.
   *
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return the created row
   */
  @Nonnull
  public PLVBoxRow addAndReturnRow (@Nonnull final IPLRenderableObject <?> aElement)
  {
    internalCheckNotPrepared ();
    return _addAndReturnRow (-1, aElement);
  }

  /**
   * Add a row to this VBox.
   *
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public IMPLTYPE addRow (@Nonnull final IPLRenderableObject <?> aElement)
  {
    addAndReturnRow (aElement);
    return thisAsT ();
  }

  /**
   * Add a row to this VBox.
   *
   * @param nIndex
   *        The index where the row should be added. Must be &ge; 0.
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return the created row
   */
  @Nonnull
  public PLVBoxRow addAndReturnRow (@Nonnegative final int nIndex, @Nonnull final IPLRenderableObject <?> aElement)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    internalCheckNotPrepared ();
    return _addAndReturnRow (nIndex, aElement);
  }

  /**
   * Add a row to this VBox.
   *
   * @param nIndex
   *        The index where the row should be added. Must be &ge; 0.
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public IMPLTYPE addRow (@Nonnegative final int nIndex, @Nonnull final IPLRenderableObject <?> aElement)
  {
    addAndReturnRow (nIndex, aElement);
    return thisAsT ();
  }

  @Nonnull
  public IMPLTYPE removeRow (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    internalCheckNotPrepared ();
    m_aRows.remove (nIndex);
    return thisAsT ();
  }

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    for (final PLVBoxRow aRow : m_aRows)
      aRow.getElement ().visit (aVisitor);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    m_aPreparedRowElementWidth = new float [m_aRows.size ()];
    m_aPreparedRowElementHeight = new float [m_aRows.size ()];

    float fUsedWidthFull = 0;
    float fUsedHeightFull = 0;
    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();

    int nIndex = 0;
    for (final PLVBoxRow aRow : m_aRows)
    {
      final IPLRenderableObject <?> aRowElement = aRow.getElement ();
      // Full width of this element
      final float fRowElementWidthFull = fAvailableWidth;
      // Effective content width of this element
      final float fRowElementWidth = fRowElementWidthFull - aRowElement.getFullXSum ();
      // Prepare child element
      final float fRowElementHeight = aRowElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                   fRowElementWidth,
                                                                                   fAvailableHeight -
                                                                                                     aRowElement.getFullYSum ()))
                                                 .getHeight ();

      final float fRowElementHeightFull = fRowElementHeight + aRowElement.getFullYSum ();
      // Update used width and height
      fUsedWidthFull = Math.max (fUsedWidthFull, fRowElementWidthFull);
      fUsedHeightFull += fRowElementHeightFull;
      // Without padding and margin
      m_aPreparedRowElementWidth[nIndex] = fRowElementWidth;
      m_aPreparedRowElementHeight[nIndex] = fRowElementHeight;
      ++nIndex;
    }

    // Add at the end, because previously only the max was used

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidthFull - aCtx.getAvailableWidth () > 0.01)
        s_aLogger.warn (getDebugID () +
                        " uses more width (" +
                        fUsedWidthFull +
                        ") than available (" +
                        aCtx.getAvailableWidth () +
                        ")!");
      if (fUsedHeightFull - aCtx.getAvailableHeight () > 0.01 && !isSplittable ())
        s_aLogger.warn (getDebugID () +
                        " uses more height (" +
                        fUsedHeightFull +
                        ") than available (" +
                        aCtx.getAvailableHeight () +
                        ")!");
    }

    return new SizeSpec (fUsedWidthFull, fUsedHeightFull);
  }

  @Override
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    final float fCurX = aCtx.getStartLeft () + getFullLeft ();
    float fCurY = aCtx.getStartTop () - getFullTop ();

    int nIndex = 0;
    for (final PLVBoxRow aRow : m_aRows)
    {
      final IPLRenderableObject <?> aRowElement = aRow.getElement ();
      final float fRowElementWidth = m_aPreparedRowElementWidth[nIndex];
      final float fRowElementHeight = m_aPreparedRowElementHeight[nIndex];

      // Perform contained element after border
      final PageRenderContext aRowElementCtx = new PageRenderContext (aCtx,
                                                                      fCurX,
                                                                      fCurY,
                                                                      fRowElementWidth,
                                                                      fRowElementHeight);
      aRowElement.perform (aRowElementCtx);

      // Update Y-pos
      fCurY -= fRowElementHeight + aRowElement.getFullYSum ();
      ++nIndex;
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("rows", m_aRows)
                            .appendIfNotNull ("preparedRowElementWidth", m_aPreparedRowElementWidth)
                            .appendIfNotNull ("preparedRowElementHeight", m_aPreparedRowElementHeight)
                            .toString ();
  }
}
