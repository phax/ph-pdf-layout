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
package com.helger.pdflayout.element.hbox;

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
import com.helger.pdflayout.base.IPLHasMargin;
import com.helger.pdflayout.base.IPLHasVerticalAlignment;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Horizontal box - groups several columns.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLHBox <IMPLTYPE extends AbstractPLHBox <IMPLTYPE>>
                                     extends AbstractPLRenderableObject <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLHBox.class);

  protected final ICommonsList <PLHBoxColumn> m_aColumns = new CommonsArrayList<> ();
  private int m_nStarWidthItems = 0;

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
  public IPLRenderableObject <?> getColumnElementAtIndex (@Nonnegative final int nIndex)
  {
    final PLHBoxColumn aColumn = getColumnAtIndex (nIndex);
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nullable
  public IPLRenderableObject <?> getFirstColumnElement ()
  {
    final PLHBoxColumn aColumn = getFirstColumn ();
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nullable
  public IPLRenderableObject <?> getLastColumnElement ()
  {
    final PLHBoxColumn aColumn = getLastColumn ();
    return aColumn == null ? null : aColumn.getElement ();
  }

  @Nonnull
  private PLHBoxColumn _addAndReturnColumn (@CheckForSigned final int nIndex,
                                            @Nonnull final IPLRenderableObject <?> aElement,
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
  public PLHBoxColumn addAndReturnColumn (@Nonnull final IPLRenderableObject <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    return _addAndReturnColumn (-1, aElement, aWidth);
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnull final IPLRenderableObject <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    addAndReturnColumn (aElement, aWidth);
    return thisAsT ();
  }

  @Nonnull
  public PLHBoxColumn addAndReturnColumn (@Nonnegative final int nIndex,
                                          @Nonnull final IPLRenderableObject <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    return _addAndReturnColumn (nIndex, aElement, aWidth);
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnegative final int nIndex,
                             @Nonnull final IPLRenderableObject <?> aElement,
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

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    for (final PLHBoxColumn aColumn : m_aColumns)
      aColumn.getElement ().visit (aVisitor);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    m_aPreparedColumnWidth = new float [m_aColumns.size ()];
    m_aPreparedColumnHeight = new float [m_aColumns.size ()];

    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();
    float fUsedWidthFull = 0;
    float fUsedHeightFull = 0;

    int nIndex = 0;
    float fRestWidth = fAvailableWidth;
    // 1. prepare all non-star width items
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (!aColumn.getWidth ().isStar ())
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fItemWidth = aColumn.getWidth ().getEffectiveValue (fAvailableWidth);
        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                            fItemWidth,
                                                                            fAvailableHeight))
                                          .getHeight ();
        // Update used width
        fUsedWidthFull += fItemWidth;
        fRestWidth -= fItemWidth;

        // Update used height
        final float fItemHeightFull = fItemHeight + aElement.getFullYSum ();
        fUsedHeightFull = Math.max (fUsedHeightFull, fItemHeightFull);

        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnWidth[nIndex] = fItemWidth;
        m_aPreparedColumnHeight[nIndex] = fItemHeight;
      }
      ++nIndex;
    }

    // 2. prepare all star widths items
    nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (aColumn.getWidth ().isStar ())
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fItemWidth = fRestWidth / m_nStarWidthItems;

        // Prepare child element
        final float fItemHeight = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                            fItemWidth,
                                                                            fAvailableHeight))
                                          .getHeight ();

        // Update used width
        fUsedWidthFull += fItemWidth;

        // Update used height
        final float fItemHeightFull = fItemHeight + aElement.getFullYSum ();
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
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        if (aElement instanceof IPLHasVerticalAlignment <?> && aElement instanceof IPLHasMargin <?>)
        {
          final float fMarginTop = ((IPLHasVerticalAlignment <?>) aElement).getIndentY (fUsedHeightFull,
                                                                                        m_aPreparedColumnHeight[nIndex] +
                                                                                                         aElement.getFullYSum ());
          if (fMarginTop != 0f)
            ((IPLHasMargin <?>) aElement).addMarginTop (fMarginTop);
        }
        ++nIndex;
      }
    }

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
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    float fCurX = aCtx.getStartLeft ();
    final float fStartY = aCtx.getStartTop ();

    int nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      final IPLRenderableObject <?> aElement = aColumn.getElement ();
      final float fItemWidth = m_aPreparedColumnWidth[nIndex];
      final float fItemHeight = m_aPreparedColumnHeight[nIndex];

      final PageRenderContext aItemCtx = new PageRenderContext (aCtx, fCurX, fStartY, fItemWidth, fItemHeight);
      aElement.perform (aItemCtx);

      // Update X-pos
      fCurX += fItemWidth + aElement.getFullXSum ();
      ++nIndex;
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("columns", m_aColumns)
                            .append ("startWidthItems", m_nStarWidthItems)
                            .appendIfNotNull ("preparedWidth", m_aPreparedColumnWidth)
                            .appendIfNotNull ("preparedHeight", m_aPreparedColumnHeight)
                            .toString ();
  }
}
