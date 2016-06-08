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
package com.helger.pdflayout.element;

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
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageSetupContext;
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
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLVBox.class);

  protected final ICommonsList <PLVBoxRow> m_aRows = new CommonsArrayList <> ();
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
  public AbstractPLElement <?> getRowElementAtIndex (@Nonnegative final int nIndex)
  {
    final PLVBoxRow aRow = getRowAtIndex (nIndex);
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the first row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public AbstractPLElement <?> getFirstRowElement ()
  {
    final PLVBoxRow aRow = getFirstRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the last row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public AbstractPLElement <?> getLastRowElement ()
  {
    final PLVBoxRow aRow = getLastRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  @Nonnull
  private PLVBoxRow _addAndReturnRow (@CheckForSigned final int nIndex, @Nonnull final AbstractPLElement <?> aElement)
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
  public PLVBoxRow addAndReturnRow (@Nonnull final AbstractPLElement <?> aElement)
  {
    checkNotPrepared ();
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
  public IMPLTYPE addRow (@Nonnull final AbstractPLElement <?> aElement)
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
  public PLVBoxRow addAndReturnRow (@Nonnegative final int nIndex, @Nonnull final AbstractPLElement <?> aElement)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    checkNotPrepared ();
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
  public IMPLTYPE addRow (@Nonnegative final int nIndex, @Nonnull final AbstractPLElement <?> aElement)
  {
    addAndReturnRow (nIndex, aElement);
    return thisAsT ();
  }

  @Nonnull
  public IMPLTYPE removeRow (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    checkNotPrepared ();
    m_aRows.remove (nIndex);
    return thisAsT ();
  }

  /**
   * Set the border around each contained row.
   *
   * @param aBorder
   *        The border style to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (new BorderSpec (aBorder));
  }

  /**
   * Set the border around each contained row.
   *
   * @param aBorderX
   *        The border to set for left and right. Maybe <code>null</code>.
   * @param aBorderY
   *        The border to set for top and bottom. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderX,
                                      @Nullable final BorderStyleSpec aBorderY)
  {
    return setRowBorder (new BorderSpec (aBorderX, aBorderY));
  }

  /**
   * Set the border around each contained row.
   *
   * @param aBorderTop
   *        The border to set for top. Maybe <code>null</code>.
   * @param aBorderRight
   *        The border to set for right. Maybe <code>null</code>.
   * @param aBorderBottom
   *        The border to set for bottom. Maybe <code>null</code>.
   * @param aBorderLeft
   *        The border to set for left. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderTop,
                                      @Nullable final BorderStyleSpec aBorderRight,
                                      @Nullable final BorderStyleSpec aBorderBottom,
                                      @Nullable final BorderStyleSpec aBorderLeft)
  {
    return setRowBorder (new BorderSpec (aBorderTop, aBorderRight, aBorderBottom, aBorderLeft));
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
    checkNotPrepared ();
    m_aRowBorder = aRowBorder;
    return thisAsT ();
  }

  /**
   * Set the top border value around each contained row. This method may not be
   * called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (m_aRowBorder.getCloneWithTop (aBorder));
  }

  /**
   * Set the right border value around each contained row. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (m_aRowBorder.getCloneWithRight (aBorder));
  }

  /**
   * Set the bottom border value around each contained row. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (m_aRowBorder.getCloneWithBottom (aBorder));
  }

  /**
   * Set the left border value around each contained row. This method may not be
   * called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    return setRowBorder (m_aRowBorder.getCloneWithLeft (aBorder));
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
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    m_aPreparedRowElementWidth = new float [m_aRows.size ()];
    m_aPreparedRowElementHeight = new float [m_aRows.size ()];
    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();
    float fUsedWidthFull = 0;
    float fUsedHeightFull = 0;
    int nIndex = 0;
    for (final PLVBoxRow aRow : m_aRows)
    {
      final AbstractPLElement <?> aRowElement = aRow.getElement ();
      // Full width of this element
      final float fRowElementWidthFull = fAvailableWidth;
      // Effective content width of this element
      final float fRowElementWidth = fRowElementWidthFull - aRowElement.getFullXSum ();
      // Prepare child element
      final float fRowElementHeight = aRowElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                   fRowElementWidth,
                                                                                   fAvailableHeight - aRowElement.getFullYSum ()))
                                                 .getHeight ();

      final float fRowElementHeightFull = fRowElementHeight + aRowElement.getFullYSum ();
      // Update used width and height
      fUsedWidthFull = Math.max (fUsedWidthFull, fRowElementWidthFull);
      fUsedHeightFull += fRowElementHeightFull;
      // Widthout padding and margin
      m_aPreparedRowElementWidth[nIndex] = fRowElementWidth;
      m_aPreparedRowElementHeight[nIndex] = fRowElementHeight;
      ++nIndex;
    }

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidthFull - fAvailableWidth > 0.01)
        s_aLogger.warn (getDebugID () +
                        " uses more width (" +
                        fUsedWidthFull +
                        ") than available (" +
                        fAvailableWidth +
                        ")!");
      if (fUsedHeightFull - fAvailableHeight > 0.01 && !isSplittable ())
        s_aLogger.warn (getDebugID () +
                        " uses more height (" +
                        fUsedHeightFull +
                        ") than available (" +
                        fAvailableHeight +
                        ")!");
    }

    return new SizeSpec (fUsedWidthFull, fUsedHeightFull);
  }

  @Override
  public void doPageSetup (@Nonnull final PageSetupContext aCtx)
  {
    for (final PLVBoxRow aRow : m_aRows)
      aRow.getElement ().doPageSetup (aCtx);
  }

  @Override
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    final float fVBoxLeft = aCtx.getStartLeft () + getPaddingLeft ();
    float fVBoxTop = aCtx.getStartTop () - getPaddingTop ();
    // Disregard the padding of this VBox!!!
    final float fVBoxWidth = aCtx.getWidth () - getPaddingXSum ();
    int nIndex = 0;
    for (final PLVBoxRow aRow : m_aRows)
    {
      final AbstractPLElement <?> aRowElement = aRow.getElement ();
      final float fRowElementWidth = m_aPreparedRowElementWidth[nIndex];
      final float fRowElementWidthWithPadding = fRowElementWidth + aRowElement.getPaddingXSum ();
      final float fRowElementHeight = m_aPreparedRowElementHeight[nIndex];
      final float fRowElementHeightWithPadding = fRowElementHeight + aRowElement.getPaddingYSum ();
      final RenderingContext aRowElementCtx = new RenderingContext (aCtx,
                                                                    fVBoxLeft + aRowElement.getMarginLeft (),
                                                                    fVBoxTop - aRowElement.getMarginTop (),
                                                                    fRowElementWidthWithPadding,
                                                                    fRowElementHeightWithPadding);

      // apply special row borders - debug: pink
      {
        final float fHeight = fRowElementHeightWithPadding + aRowElement.getMarginYSum ();

        // Fill before border
        if (m_aRowFillColor != null)
        {
          aContentStream.setNonStrokingColor (m_aRowFillColor);
          aContentStream.fillRect (fVBoxLeft, fVBoxTop - fHeight, fVBoxWidth, fHeight);
        }

        BorderSpec aRealBorder = m_aRowBorder;
        if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
          aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_VBOX));
        if (aRealBorder.hasAnyBorder ())
          renderBorder (aContentStream, fVBoxLeft, fVBoxTop, fVBoxWidth, fHeight, aRealBorder);
      }

      // Perform contained element after border
      aRowElement.perform (aRowElementCtx);

      // Update Y-pos
      fVBoxTop -= fRowElementHeightWithPadding + aRowElement.getMarginYSum ();
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
