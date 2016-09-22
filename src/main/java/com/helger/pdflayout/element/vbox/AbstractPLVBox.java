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

import java.awt.Color;
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
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.IPLElement;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Vertical box - groups several rows.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLVBox <IMPLTYPE extends AbstractPLVBox <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
                                     implements IPLHasRowBorder <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLVBox.class);

  protected final ICommonsList <PLVBoxRow> m_aRows = new CommonsArrayList<> ();
  private BorderSpec m_aRowBorder = BorderSpec.BORDER0;
  private Color m_aRowFillColor = null;
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
    setRowBorder (aSource.m_aRowBorder);
    setRowFillColor (aSource.m_aRowFillColor);
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

  /**
   * Set the border around each contained row.
   *
   * @param aRowBorder
   *        The border to set. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nonnull final BorderSpec aRowBorder)
  {
    ValueEnforcer.notNull (aRowBorder, "RowBorder");
    internalCheckNotPrepared ();
    m_aRowBorder = aRowBorder;
    return thisAsT ();
  }

  /**
   * Get the border around each contained row. By default
   * {@link BorderSpec#BORDER0} which means no border is used.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public final BorderSpec getRowBorder ()
  {
    return m_aRowBorder;
  }

  /**
   * Set the fill color to be used to fill the whole row. <code>null</code>
   * means no fill color.
   *
   * @param aRowFillColor
   *        The fill color to use. May be <code>null</code> to indicate no fill
   *        color (which is also the default).
   * @return this
   */
  @Nonnull
  public IMPLTYPE setRowFillColor (@Nullable final Color aRowFillColor)
  {
    m_aRowFillColor = aRowFillColor;
    return thisAsT ();
  }

  /**
   * Get the fill color to be used to fill the whole row. <code>null</code>
   * means no fill color.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  public Color getRowFillColor ()
  {
    return m_aRowFillColor;
  }

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor)
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
    final float fRowBorderXSumWidth = m_aRowBorder.getXSumWidth ();
    final float fRowBorderYSumWidth = m_aRowBorder.getYSumWidth ();

    float fUsedWidthFull = 0;
    float fUsedHeightFull = fRowBorderYSumWidth * m_aRows.size ();
    final float fAvailableWidth = aCtx.getAvailableWidth () - fRowBorderXSumWidth;
    final float fAvailableHeight = aCtx.getAvailableHeight () - fUsedHeightFull;

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
    fUsedWidthFull += fRowBorderXSumWidth;

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidthFull - aCtx.getAvailableWidth () > 0.01)
        s_aLogger.warn (getDebugID () +
                        " " +
                        PLDebug.getXMBP (this) +
                        " uses more width (" +
                        fUsedWidthFull +
                        ") than available (" +
                        aCtx.getAvailableWidth () +
                        ")!");
      if (fUsedHeightFull - aCtx.getAvailableHeight () > 0.01 && !isSplittable ())
        s_aLogger.warn (getDebugID () +
                        " " +
                        PLDebug.getYMBP (this) +
                        " uses more height (" +
                        fUsedHeightFull +
                        ") than available (" +
                        aCtx.getAvailableHeight () +
                        ")!");
    }

    return new SizeSpec (fUsedWidthFull, fUsedHeightFull);
  }

  @Override
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    final float fRowBorderTopWidth = m_aRowBorder.getTopWidth ();
    final float fRowBorderLeftWidth = m_aRowBorder.getLeftWidth ();
    final float fRowBorderXSumWidth = m_aRowBorder.getXSumWidth ();
    final float fRowBorderYSumWidth = m_aRowBorder.getYSumWidth ();

    final float fCurX = aCtx.getStartLeft () + getPaddingLeft () + fRowBorderLeftWidth;
    float fCurY = aCtx.getStartTop () - getPaddingTop () - fRowBorderTopWidth;
    // Disregard the padding of this VBox!!!
    final float fVBoxWidth = aCtx.getWidth () - getPaddingXSum () - fRowBorderXSumWidth;

    int nIndex = 0;
    for (final PLVBoxRow aRow : m_aRows)
    {
      final IPLRenderableObject <?> aRowElement = aRow.getElement ();
      final float fRowElementWidth = m_aPreparedRowElementWidth[nIndex];
      final float fRowElementHeight = m_aPreparedRowElementHeight[nIndex];

      // apply special row borders - debug: pink
      {
        final float fLeft = fCurX;
        final float fTop = fCurY;
        final float fWidth = fVBoxWidth;
        final float fHeight = fRowElementHeight + aRowElement.getFullYSum ();

        // Fill before border
        if (m_aRowFillColor != null)
        {
          aContentStream.setNonStrokingColor (m_aRowFillColor);
          aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
        }

        BorderSpec aRealBorder = m_aRowBorder;
        if (PLRenderHelper.shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
          aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_VBOX));
        if (aRealBorder.hasAnyBorder ())
          PLRenderHelper.renderBorder (this, aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
      }

      // Perform contained element after border
      float fStartLeft = fCurX;
      float fStartTop = fCurY;
      float fRowElementWidthWithPadding = fRowElementWidth;
      float fRowElementHeightWithPadding = fRowElementHeight;
      if (aRowElement instanceof IPLElement <?>)
      {
        final IPLElement <?> aRealElement = (IPLElement <?>) aRowElement;
        fStartLeft += aRealElement.getMarginAndBorderLeft ();
        fStartTop -= aRealElement.getMarginAndBorderTop ();
        fRowElementWidthWithPadding += aRealElement.getPaddingXSum ();
        fRowElementHeightWithPadding += aRealElement.getPaddingYSum ();
      }

      final RenderingContext aRowElementCtx = new RenderingContext (aCtx,
                                                                    fStartLeft,
                                                                    fStartTop,
                                                                    fRowElementWidthWithPadding,
                                                                    fRowElementHeightWithPadding);
      aRowElement.perform (aRowElementCtx);

      // Update Y-pos
      fCurY -= fRowElementHeight + aRowElement.getFullYSum () + fRowBorderYSumWidth;
      ++nIndex;
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("rows", m_aRows)
                            .append ("rowBorder", m_aRowBorder)
                            .appendIfNotNull ("rowFillColor", m_aRowFillColor)
                            .appendIfNotNull ("preparedRowElementWidth", m_aPreparedRowElementWidth)
                            .appendIfNotNull ("preparedRowElementHeight", m_aPreparedRowElementHeight)
                            .toString ();
  }
}
