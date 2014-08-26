/**
 * Copyright (C) 2014 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.render.PDPageContentStreamWithCache;
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
 */
public abstract class AbstractPLVBox <IMPLTYPE extends AbstractPLVBox <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
{
  @NotThreadSafe
  public static final class Row
  {
    private AbstractPLElement <?> m_aElement;

    public Row (@Nonnull final AbstractPLElement <?> aElement)
    {
      setElement (aElement);
    }

    @Nonnull
    public AbstractPLElement <?> getElement ()
    {
      return m_aElement;
    }

    @Nonnull
    public Row setElement (@Nonnull final AbstractPLElement <?> aElement)
    {
      m_aElement = ValueEnforcer.notNull (aElement, "Element");
      return this;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("element", m_aElement).toString ();
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLVBox.class);

  protected final List <Row> m_aRows = new ArrayList <Row> ();
  private BorderSpec m_aRowBorder = BorderSpec.BORDER0;
  private Color m_aRowFillColor = null;
  /** prepare width (without padding and margin) */
  protected float [] m_aPreparedWidth;
  /** prepare height (without padding and margin) */
  protected float [] m_aPreparedHeight;

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
  public List <Row> getAllRows ()
  {
    return ContainerHelper.newList (m_aRows);
  }

  /**
   * Get the row at the specified index.
   * 
   * @param nIndex
   *        The index to use. Should be &ge; 0.
   * @return <code>null</code> if an invalid index was provided.
   */
  @Nullable
  public Row getRowAtIndex (@Nonnegative final int nIndex)
  {
    return ContainerHelper.getSafe (m_aRows, nIndex);
  }

  /**
   * @return The first row or <code>null</code> if no row is present.
   */
  @Nullable
  public Row getFirstRow ()
  {
    return ContainerHelper.getFirstElement (m_aRows);
  }

  /**
   * @return The last row or <code>null</code> if no row is present.
   */
  @Nullable
  public Row getLastRow ()
  {
    return ContainerHelper.getLastElement (m_aRows);
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
    final Row aRow = getRowAtIndex (nIndex);
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the first row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public AbstractPLElement <?> getFirstRowElement ()
  {
    final Row aRow = getFirstRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * @return The element in the last row or <code>null</code> if no row is
   *         present.
   */
  @Nullable
  public AbstractPLElement <?> getLastRowElement ()
  {
    final Row aRow = getLastRow ();
    return aRow == null ? null : aRow.getElement ();
  }

  /**
   * Add a row to this VBox.
   * 
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return the created row
   */
  @Nonnull
  public Row addAndReturnRow (@Nonnull final AbstractPLElement <?> aElement)
  {
    checkNotPrepared ();
    final Row aItem = new Row (aElement);
    m_aRows.add (aItem);
    return aItem;
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
   * @param aElement
   *        The row to be added. May not be <code>null</code>.
   * @return the created row
   */
  @Nonnull
  public Row addAndReturnRow (@Nonnegative final int nIndex, @Nonnull final AbstractPLElement <?> aElement)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    checkNotPrepared ();
    final Row aItem = new Row (aElement);
    if (nIndex >= m_aRows.size ())
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
   * @return this
   */
  @Nonnull
  public IMPLTYPE addRow (@Nonnegative final int nIndex, @Nonnull final AbstractPLElement <?> aElement)
  {
    addAndReturnRow (nIndex, aElement);
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
  public final IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderX, @Nullable final BorderStyleSpec aBorderY)
  {
    return setRowBorder (new BorderSpec (aBorderX, aBorderY));
  }

  /**
   * Set the border around each contained row.
   * 
   * @param aBorderLeft
   *        The border to set for left. Maybe <code>null</code>.
   * @param aBorderTop
   *        The border to set for top. Maybe <code>null</code>.
   * @param aBorderRight
   *        The border to set for right. Maybe <code>null</code>.
   * @param aBorderBottom
   *        The border to set for bottom. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nullable final BorderStyleSpec aBorderLeft,
                                      @Nullable final BorderStyleSpec aBorderTop,
                                      @Nullable final BorderStyleSpec aBorderRight,
                                      @Nullable final BorderStyleSpec aBorderBottom)
  {
    return setRowBorder (new BorderSpec (aBorderLeft, aBorderTop, aBorderRight, aBorderBottom));
  }

  /**
   * Set the border around each contained row.
   * 
   * @param aBorder
   *        The border to set. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setRowBorder (@Nonnull final BorderSpec aBorder)
  {
    if (aBorder == null)
      throw new NullPointerException ("RowBorder");
    checkNotPrepared ();
    m_aRowBorder = aBorder;
    return thisAsT ();
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
    m_aPreparedWidth = new float [m_aRows.size ()];
    m_aPreparedHeight = new float [m_aRows.size ()];
    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();
    float fUsedWidth = 0;
    float fUsedHeight = 0;
    int nIndex = 0;
    for (final Row aRow : m_aRows)
    {
      final AbstractPLElement <?> aElement = aRow.getElement ();
      // Full width of this element
      final float fItemWidthFull = fAvailableWidth;
      // Effective content width of this element
      final float fItemWidth = fItemWidthFull - aElement.getMarginPlusPaddingXSum ();
      // Prepare child element
      final float fItemHeight = aElement.prepare (new PreparationContext (fItemWidth, fAvailableHeight)).getHeight ();
      final float fItemHeightFull = fItemHeight + aElement.getMarginPlusPaddingYSum ();
      // Update used width and height
      fUsedWidth = Math.max (fUsedWidth, fItemWidthFull);
      fUsedHeight += fItemHeightFull;
      // Remember width and height for element (without padding and margin)
      m_aPreparedWidth[nIndex] = fItemWidth;
      m_aPreparedHeight[nIndex] = fItemHeight;
      ++nIndex;
    }

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidth - fAvailableWidth > 0.01)
        s_aLogger.warn ("VBox uses more width (" + fUsedWidth + ") than available (" + fAvailableWidth + ")!");
      if (fUsedHeight - fAvailableHeight > 0.01 && !isSplittable ())
        s_aLogger.warn ("VBox uses more height (" + fUsedHeight + ") than available (" + fAvailableHeight + ")!");
    }

    return new SizeSpec (fUsedWidth, fUsedHeight);
  }

  @Override
  public void doPageSetup (@Nonnull final PageSetupContext aCtx)
  {
    for (final Row aRow : m_aRows)
      aRow.getElement ().doPageSetup (aCtx);
  }

  @Override
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    final float fCurX = aCtx.getStartLeft () + getPaddingLeft ();
    float fCurY = aCtx.getStartTop () - getPaddingTop ();
    int nIndex = 0;
    for (final Row aRow : m_aRows)
    {
      final AbstractPLElement <?> aElement = aRow.getElement ();
      final float fItemWidth = m_aPreparedWidth[nIndex];
      final float fItemWidthWithPadding = fItemWidth + aElement.getPaddingXSum ();
      final float fItemHeight = m_aPreparedHeight[nIndex];
      final float fItemHeightWithPadding = fItemHeight + aElement.getPaddingYSum ();
      final RenderingContext aItemCtx = new RenderingContext (aCtx,
                                                              fCurX + aElement.getMarginLeft (),
                                                              fCurY - aElement.getMarginTop (),
                                                              fItemWidthWithPadding,
                                                              fItemHeightWithPadding);

      // apply special row borders - debug: blue
      {
        // Disregard the padding of this VBox!!!
        final float fLeft = fCurX;
        final float fTop = fCurY;
        final float fWidth = aCtx.getWidth () - getPaddingXSum ();
        final float fHeight = fItemHeightWithPadding + aElement.getMarginYSum ();

        // Fill before border
        if (m_aRowFillColor != null)
        {
          aContentStream.setNonStrokingColor (m_aRowFillColor);
          aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
        }

        BorderSpec aRealBorder = m_aRowBorder;
        if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
          aRealBorder = new BorderSpec (new BorderStyleSpec (new Color (0, 0, 255)));
        if (aRealBorder.hasAnyBorder ())
          renderBorder (aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
      }

      // Perform contained element after border
      aElement.perform (aItemCtx);

      // Update Y-pos
      fCurY -= fItemHeightWithPadding + aElement.getMarginYSum ();
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
                            .appendIfNotNull ("preparedWidth", m_aPreparedWidth)
                            .appendIfNotNull ("preparedHeight", m_aPreparedHeight)
                            .toString ();
  }
}
