/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.render.PDPageContentStreamWithCache;
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
public class AbstractPLHBox <IMPLTYPE extends AbstractPLHBox <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLHBox.class);

  protected final List <PLHBoxColumn> m_aColumns = new ArrayList <PLHBoxColumn> ();
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
  public List <PLHBoxColumn> getAllColumns ()
  {
    return ContainerHelper.newList (m_aColumns);
  }

  @Nullable
  public PLHBoxColumn getColumnAtIndex (@Nonnegative final int nIndex)
  {
    return ContainerHelper.getSafe (m_aColumns, nIndex);
  }

  @Nullable
  public PLHBoxColumn getFirstColumn ()
  {
    return ContainerHelper.getFirstElement (m_aColumns);
  }

  @Nullable
  public PLHBoxColumn getLastColumn ()
  {
    return ContainerHelper.getLastElement (m_aColumns);
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
  public PLHBoxColumn addAndReturnColumn (@Nonnull final AbstractPLElement <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    checkNotPrepared ();
    final PLHBoxColumn aItem = new PLHBoxColumn (aElement, aWidth);
    m_aColumns.add (aItem);
    if (aWidth.isStar ())
      m_nStarWidthItems++;
    return aItem;
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnull final AbstractPLElement <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    addAndReturnColumn (aElement, aWidth);
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
   * @param aBorderX
   *        The border to set for left and right. Maybe <code>null</code>.
   * @param aBorderY
   *        The border to set for top and bottom. Maybe <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setColumnBorder (@Nullable final BorderStyleSpec aBorderX,
                                         @Nullable final BorderStyleSpec aBorderY)
  {
    return setColumnBorder (new BorderSpec (aBorderX, aBorderY));
  }

  /**
   * Set the border around each contained column.
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
  public final IMPLTYPE setColumnBorder (@Nullable final BorderStyleSpec aBorderLeft,
                                         @Nullable final BorderStyleSpec aBorderTop,
                                         @Nullable final BorderStyleSpec aBorderRight,
                                         @Nullable final BorderStyleSpec aBorderBottom)
  {
    return setColumnBorder (new BorderSpec (aBorderLeft, aBorderTop, aBorderRight, aBorderBottom));
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
    checkNotPrepared ();
    m_aColumnBorder = aBorder;
    return thisAsT ();
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
    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();
    float fUsedWidth = 0;
    float fUsedHeight = 0;
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
        final float fItemWidth = fItemWidthFull - aElement.getMarginPlusPaddingXSum ();
        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (fItemWidth, fAvailableHeight)).getHeight ();
        final float fItemHeightFull = fItemHeight + aElement.getMarginPlusPaddingYSum ();
        // Update used width and height
        fUsedWidth += fItemWidthFull;
        fRestWidth -= fItemWidthFull;
        fUsedHeight = Math.max (fUsedHeight, fItemHeightFull);
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
        final float fItemWidth = fItemWidthFull - aElement.getMarginPlusPaddingXSum ();
        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (fItemWidth, fAvailableHeight)).getHeight ();
        final float fItemHeightFull = fItemHeight + aElement.getMarginPlusPaddingYSum ();
        // Update used width and height
        fUsedWidth += fItemWidthFull;
        fUsedHeight = Math.max (fUsedHeight, fItemHeightFull);
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
              fPaddingTop = (fUsedHeight - aElement.getMarginPlusPaddingYSum () - m_aPreparedColumnHeight[nIndex]) / 2;
              break;
            case BOTTOM:
              fPaddingTop = fUsedHeight - aElement.getMarginPlusPaddingYSum () - m_aPreparedColumnHeight[nIndex];
              break;
            default:
              throw new IllegalStateException ("Unsupported vertical alignment: " + eVertAlignment);
          }
          if (fPaddingTop != 0f)
          {
            aElement.markAsNotPrepared ();
            aElement.setPaddingTop (aElement.getPaddingTop () + fPaddingTop);
            aElement.markAsPrepared (new SizeSpec (m_aPreparedColumnWidth[nIndex], m_aPreparedColumnHeight[nIndex] +
                                                                                   fPaddingTop));
          }
        }
        ++nIndex;
      }
    }

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidth - fAvailableWidth > 0.01)
        s_aLogger.warn ("HBox uses more width (" + fUsedWidth + ") than available (" + fAvailableWidth + ")!");
      if (fUsedHeight - fAvailableHeight > 0.01)
        if (!isSplittable ())
          s_aLogger.warn ("HBox uses more height (" + fUsedHeight + ") than available (" + fAvailableHeight + ")!");
    }

    return new SizeSpec (fUsedWidth, fUsedHeight);
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
    float fCurX = aCtx.getStartLeft () + getPaddingLeft ();
    final float fCurY = aCtx.getStartTop () - getPaddingTop ();
    int nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      final AbstractPLElement <?> aElement = aColumn.getElement ();
      final float fItemWidth = m_aPreparedColumnWidth[nIndex];
      final float fItemWidthWithPadding = fItemWidth + aElement.getPaddingXSum ();
      final float fItemHeight = m_aPreparedColumnHeight[nIndex];
      final float fItemHeightWithPadding = fItemHeight + aElement.getPaddingYSum ();
      final RenderingContext aItemCtx = new RenderingContext (aCtx,
                                                              fCurX + aElement.getMarginLeft (),
                                                              fCurY - aElement.getMarginTop (),
                                                              fItemWidthWithPadding,
                                                              fItemHeightWithPadding);

      // apply special column borders - debug: blue
      {
        // Disregard the padding of this HBox!!!
        final float fLeft = fCurX;
        final float fTop = fCurY;
        final float fWidth = fItemWidthWithPadding + aElement.getMarginXSum ();
        final float fHeight = aCtx.getHeight () - getPaddingYSum ();

        // Fill before border
        if (m_aColumnFillColor != null)
        {
          aContentStream.setNonStrokingColor (m_aColumnFillColor);
          aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
        }

        BorderSpec aRealBorder = m_aColumnBorder;
        if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
          aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_HBOX));
        if (aRealBorder.hasAnyBorder ())
          renderBorder (aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
      }

      // Perform contained element after border
      aElement.perform (aItemCtx);

      // Update X-pos
      fCurX += fItemWidthWithPadding + aElement.getMarginXSum ();
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
