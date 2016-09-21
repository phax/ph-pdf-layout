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
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Horizontal box - groups several columns.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLHBox <IMPLTYPE extends AbstractPLHBox <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLHBox.class);

  protected final ICommonsList <PLHBoxColumn> m_aColumns = new CommonsArrayList<> ();
  private int m_nStarWidthItems = 0;
  private BorderSpec m_aColumnBorder = BorderSpec.BORDER0;
  private Color m_aColumnFillColor = null;

  /** prepare width (without padding and margin) */
  protected float [] m_aPreparedColumnWidth;
  /** prepare height (without padding and margin) */
  protected float [] m_aPreparedColumnHeight;

  public AbstractPLHBox ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLHBox <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setColumnBorder (aSource.m_aColumnBorder);
    setColumnFillColor (aSource.m_aColumnFillColor);
    return thisAsT ();
  }

  /**
   * @return The number of columns. Always &ge; 0.
   */
  @Nonnegative
  public int getColumnCount ()
  {
    return m_aColumns.size ();
  }

  /**
   * @return All columns. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <PLHBoxColumn> getAllColumns ()
  {
    return m_aColumns.getClone ();
  }

  @Nullable
  public PLHBoxColumn getColumnAtIndex (@Nonnegative final int nIndex)
  {
    return m_aColumns.getAtIndex (nIndex);
  }

  @Nullable
  public PLHBoxColumn getFirstColumn ()
  {
    return m_aColumns.getFirst ();
  }

  @Nullable
  public PLHBoxColumn getLastColumn ()
  {
    return m_aColumns.getLast ();
  }

  @Nullable
  public AbstractPLElement <?> getColumnElementAtIndex (@Nonnegative final int nIndex)
  {
    final PLHBoxColumn aColumn = getColumnAtIndex (nIndex);
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nullable
  public AbstractPLElement <?> getFirstColumnElement ()
  {
    final PLHBoxColumn aColumn = getFirstColumn ();
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nullable
  public AbstractPLElement <?> getLastColumnElement ()
  {
    final PLHBoxColumn aColumn = getLastColumn ();
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nonnull
  private PLHBoxColumn _addAndReturnColumn (@CheckForSigned final int nIndex,
                                            @Nonnull final AbstractPLElement <?> aElement,
                                            @Nonnull final WidthSpec aWidth)
  {
    internalCheckNotPrepared ();
    final PLHBoxColumn aItem = new PLHBoxColumn (aElement, aWidth);
    if (nIndex < 0 || nIndex >= m_aColumns.size ())
      m_aColumns.add (aItem);
    else
      m_aColumns.add (nIndex, aItem);
    if (aWidth.isStar ())
      m_nStarWidthItems++;
    return aItem;
  }

  @Nonnull
  public PLHBoxColumn addAndReturnColumn (@Nonnull final AbstractPLElement <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    internalCheckNotPrepared ();
    return _addAndReturnColumn (-1, aElement, aWidth);
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnull final AbstractPLElement <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    addAndReturnColumn (aElement, aWidth);
    return thisAsT ();
  }

  @Nonnull
  public PLHBoxColumn addAndReturnColumn (@Nonnegative final int nIndex,
                                          @Nonnull final AbstractPLElement <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    internalCheckNotPrepared ();
    return _addAndReturnColumn (nIndex, aElement, aWidth);
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnegative final int nIndex,
                             @Nonnull final AbstractPLElement <?> aElement,
                             @Nonnull final WidthSpec aWidth)
  {
    addAndReturnColumn (nIndex, aElement, aWidth);
    return thisAsT ();
  }

  @Nonnull
  public IMPLTYPE removeColumn (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    internalCheckNotPrepared ();
    final PLHBoxColumn aColumn = m_aColumns.remove (nIndex);
    if (aColumn.getWidth ().isStar ())
      m_nStarWidthItems--;
    return thisAsT ();
  }

  /**
   * Set the border around each contained column.
   *
   * @param aBorder
   *        The border style to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorder (@Nullable final BorderStyleSpec aBorder)
  {
    return setColumnBorder (new BorderSpec (aBorder));
  }

  /**
   * Set the border around each contained column.
   *
   * @param aBorderY
   *        The border to set for top and bottom. Maybe <code>null</code>.
   * @param aBorderX
   *        The border to set for left and right. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorder (@Nullable final BorderStyleSpec aBorderY,
                                         @Nullable final BorderStyleSpec aBorderX)
  {
    return setColumnBorder (new BorderSpec (aBorderY, aBorderX));
  }

  /**
   * Set the border around each contained column.
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
  public final IMPLTYPE setColumnBorder (@Nullable final BorderStyleSpec aBorderTop,
                                         @Nullable final BorderStyleSpec aBorderRight,
                                         @Nullable final BorderStyleSpec aBorderBottom,
                                         @Nullable final BorderStyleSpec aBorderLeft)
  {
    return setColumnBorder (new BorderSpec (aBorderTop, aBorderRight, aBorderBottom, aBorderLeft));
  }

  /**
   * Set the border around each contained column.
   *
   * @param aBorder
   *        The border to set. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "ColumnBorder");
    internalCheckNotPrepared ();
    m_aColumnBorder = aBorder;
    return thisAsT ();
  }

  /**
   * Set the top border value around each contained column. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    return setColumnBorder (m_aColumnBorder.getCloneWithTop (aBorder));
  }

  /**
   * Set the right border value around each contained column. This method may
   * not be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    return setColumnBorder (m_aColumnBorder.getCloneWithRight (aBorder));
  }

  /**
   * Set the bottom border value around each contained column. This method may
   * not be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    return setColumnBorder (m_aColumnBorder.getCloneWithBottom (aBorder));
  }

  /**
   * Set the left border value around each contained column. This method may not
   * be called after an element got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    return setColumnBorder (m_aColumnBorder.getCloneWithLeft (aBorder));
  }

  /**
   * Get the border around each contained column. By default
   * {@link BorderSpec#BORDER0} which means no border is used.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public final BorderSpec getColumnBorder ()
  {
    return m_aColumnBorder;
  }

  /**
   * Set the fill color to be used to fill the whole column. <code>null</code>
   * means no fill color.
   *
   * @param aColumnFillColor
   *        The fill color to use. May be <code>null</code> to indicate no fill
   *        color (which is also the default).
   * @return this
   */
  @Nonnull
  public IMPLTYPE setColumnFillColor (@Nullable final Color aColumnFillColor)
  {
    m_aColumnFillColor = aColumnFillColor;
    return thisAsT ();
  }

  /**
   * Get the fill color to be used to fill the whole column. <code>null</code>
   * means no fill color.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  public Color getColumnFillColor ()
  {
    return m_aColumnFillColor;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    m_aPreparedColumnWidth = new float [m_aColumns.size ()];
    m_aPreparedColumnHeight = new float [m_aColumns.size ()];
    final float fColumnBorderXSumWidth = m_aColumnBorder.getXSumWidth ();
    final float fColumnBorderYSumWidth = m_aColumnBorder.getYSumWidth ();

    float fUsedWidthFull = fColumnBorderXSumWidth * m_aColumns.size ();
    float fUsedHeightFull = 0;
    final float fAvailableWidth = aCtx.getAvailableWidth () - fUsedWidthFull;
    final float fAvailableHeight = aCtx.getAvailableHeight ();

    int nIndex = 0;
    float fRestWidth = fAvailableWidth;
    // 1. all non-star width items
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (!aColumn.getWidth ().isStar ())
      {
        final AbstractPLElement <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fItemWidthFull = aColumn.getWidth ().getEffectiveValue (fAvailableWidth);
        // Effective content width of this element
        final float fItemWidth = fItemWidthFull - aElement.getFullXSum ();
        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                            fItemWidth,
                                                                            fAvailableHeight))
                                          .getHeight ();
        final float fItemHeightFull = fItemHeight + aElement.getFullYSum ();
        // Update used width and height
        fUsedWidthFull += fItemWidthFull;
        fRestWidth -= fItemWidthFull;
        fUsedHeightFull = Math.max (fUsedHeightFull, fItemHeightFull);
        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnWidth[nIndex] = fItemWidth;
        m_aPreparedColumnHeight[nIndex] = fItemHeight;
      }
      ++nIndex;
    }
    // 2. all star widths items
    nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (aColumn.getWidth ().isStar ())
      {
        final AbstractPLElement <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fItemWidthFull = fRestWidth / m_nStarWidthItems;
        // Effective content width of this element
        final float fItemWidth = fItemWidthFull - aElement.getFullXSum ();
        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                            fItemWidth,
                                                                            fAvailableHeight))
                                          .getHeight ();
        final float fItemHeightFull = fItemHeight + aElement.getFullYSum ();
        // Update used width and height
        fUsedWidthFull += fItemWidthFull;
        fUsedHeightFull = Math.max (fUsedHeightFull, fItemHeightFull);
        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnWidth[nIndex] = fItemWidth;
        m_aPreparedColumnHeight[nIndex] = fItemHeight;
      }
      ++nIndex;
    }

    // Apply vertical alignment
    {
      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        final AbstractPLElement <?> aElement = aColumn.getElement ();
        if (aElement instanceof IPLHasVerticalAlignment <?>)
        {
          final EVertAlignment eVertAlignment = ((IPLHasVerticalAlignment <?>) aElement).getVertAlign ();
          float fPaddingTop;
          switch (eVertAlignment)
          {
            case TOP:
              fPaddingTop = 0f;
              break;
            case MIDDLE:
              fPaddingTop = (fUsedHeightFull - aElement.getFullYSum () - m_aPreparedColumnHeight[nIndex]) / 2;
              break;
            case BOTTOM:
              fPaddingTop = fUsedHeightFull - aElement.getFullYSum () - m_aPreparedColumnHeight[nIndex];
              break;
            default:
              throw new IllegalStateException ("Unsupported vertical alignment: " + eVertAlignment);
          }
          if (fPaddingTop != 0f)
          {
            aElement.internalMarkAsNotPrepared ();
            aElement.setPaddingTop (aElement.getPaddingTop () + fPaddingTop);
            aElement.internalMarkAsPrepared (new SizeSpec (m_aPreparedColumnWidth[nIndex],
                                                           m_aPreparedColumnHeight[nIndex] + fPaddingTop));
          }
        }
        ++nIndex;
      }
    }

    // Add at the end, because previously only the max was used
    fUsedHeightFull += fColumnBorderYSumWidth;

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
      if (fUsedHeightFull - aCtx.getAvailableHeight () > 0.01)
        if (!isSplittable ())
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
  public void doPageSetup (@Nonnull final PageSetupContext aCtx)
  {
    for (final PLHBoxColumn aColumn : m_aColumns)
      aColumn.getElement ().doPageSetup (aCtx);
  }

  @Override
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    final float fColumnBorderTopWidth = m_aColumnBorder.getTopWidth ();
    final float fColumnBorderLeftWidth = m_aColumnBorder.getLeftWidth ();
    final float fColumnBorderXSumWidth = m_aColumnBorder.getXSumWidth ();
    final float fColumnBorderYSumWidth = m_aColumnBorder.getYSumWidth ();

    float fCurX = aCtx.getStartLeft () + getPaddingLeft () + fColumnBorderLeftWidth;
    final float fCurY = aCtx.getStartTop () - getPaddingTop () - fColumnBorderTopWidth;
    final float fHBoxHeight = aCtx.getHeight () - getPaddingYSum () - fColumnBorderYSumWidth;

    int nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      final AbstractPLElement <?> aElement = aColumn.getElement ();
      final float fItemWidth = m_aPreparedColumnWidth[nIndex];
      final float fItemWidthWithPadding = fItemWidth + aElement.getPaddingXSum ();
      final float fItemHeight = m_aPreparedColumnHeight[nIndex];
      final float fItemHeightWithPadding = fItemHeight + aElement.getPaddingYSum ();

      // apply special column borders - debug: blue
      {
        // Disregard the padding of this HBox!!!
        final float fLeft = fCurX;
        final float fTop = fCurY;
        final float fWidth = fItemWidthWithPadding + aElement.getMarginAndBorderXSum ();
        final float fHeight = fHBoxHeight;

        // Fill before border
        Color aFillColor = aColumn.getFillColor ();
        if (aFillColor == null)
          aFillColor = m_aColumnFillColor;
        if (aFillColor != null)
        {
          aContentStream.setNonStrokingColor (aFillColor);
          aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
        }

        BorderSpec aRealBorder = m_aColumnBorder;
        if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
          aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_HBOX));
        if (aRealBorder.hasAnyBorder ())
          renderBorder (aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
      }

      // Perform contained element after border
      final RenderingContext aItemCtx = new RenderingContext (aCtx,
                                                              fCurX + aElement.getMarginAndBorderLeft (),
                                                              fCurY - aElement.getMarginAndBorderTop (),
                                                              fItemWidthWithPadding,
                                                              fItemHeightWithPadding);
      aElement.perform (aItemCtx);

      // Update X-pos
      fCurX += fItemWidthWithPadding + aElement.getMarginAndBorderXSum () + fColumnBorderXSumWidth;
      ++nIndex;
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("columns", m_aColumns)
                            .append ("startWidthItems", m_nStarWidthItems)
                            .append ("columnBorder", m_aColumnBorder)
                            .appendIfNotNull ("columnFillColor", m_aColumnFillColor)
                            .appendIfNotNull ("preparedWidth", m_aPreparedColumnWidth)
                            .appendIfNotNull ("preparedHeight", m_aPreparedColumnHeight)
                            .toString ();
  }
}
