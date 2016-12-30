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
package com.helger.pdflayout4.element.hbox;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

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
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.base.AbstractPLElement;
import com.helger.pdflayout4.base.AbstractPLRenderableObject;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.base.IPLSplittableObject;
import com.helger.pdflayout4.base.IPLVisitor;
import com.helger.pdflayout4.base.PLElementWithSize;
import com.helger.pdflayout4.base.PLSplitResult;
import com.helger.pdflayout4.element.special.PLSpacerX;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.SizeSpec;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * Horizontal box - groups several columns. Each column was a width with one of
 * the supported types:
 * <ul>
 * <li><b>absolute</b> - the width is explicitly specified in user units</li>
 * <li><b>percentage</b> - the width is specified in percentage of the
 * surrounding element</li>
 * <li><b>star</b> - the width of all columns with this type is evenly spaced on
 * the available width. So if at least one 'star' width column is available, the
 * hbox uses the complete available width.</li>
 * <li><b>auto</b> - the width of the column is determined by the width of the
 * content. The maximum width assigned to this column type is the same as for
 * 'star' width columns.</li>
 * </ul>
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLHBox <IMPLTYPE extends AbstractPLHBox <IMPLTYPE>> extends
                                     AbstractPLRenderableObject <IMPLTYPE> implements IPLSplittableObject <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLHBox.class);

  private final ICommonsList <PLHBoxColumn> m_aColumns = new CommonsArrayList<> ();
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  /** prepared column size (with outline of contained element) */
  private SizeSpec [] m_aPreparedColumnSize;
  /** prepared element size (without outline) */
  private SizeSpec [] m_aPreparedElementSize;

  public AbstractPLHBox ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLHBox <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setVertSplittable (aSource.m_bVertSplittable);
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

  /**
   * @return All columns. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Iterable <PLHBoxColumn> getColumns ()
  {
    return m_aColumns;
  }

  public void forEachColumn (@Nonnull final Consumer <? super PLHBoxColumn> aConsumer)
  {
    m_aColumns.forEach (aConsumer);
  }

  public void forEachColumn (@Nonnull final ObjIntConsumer <? super PLHBoxColumn> aConsumer)
  {
    m_aColumns.forEach (aConsumer);
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

  private void _addAndReturnColumn (@CheckForSigned final int nIndex, @Nonnull final PLHBoxColumn aColumn)
  {
    internalCheckNotPrepared ();
    if (nIndex < 0 || nIndex >= m_aColumns.size ())
      m_aColumns.add (aColumn);
    else
      m_aColumns.add (nIndex, aColumn);
  }

  @Nonnull
  public PLHBoxColumn addAndReturnColumn (@Nonnull final IPLRenderableObject <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    return addAndReturnColumn (-1, aElement, aWidth);
  }

  @Nonnull
  public IMPLTYPE addColumn (@Nonnull final IPLRenderableObject <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    addAndReturnColumn (-1, aElement, aWidth);
    return thisAsT ();
  }

  @Nonnull
  public PLHBoxColumn addAndReturnColumn (@CheckForSigned final int nIndex,
                                          @Nonnull final IPLRenderableObject <?> aElement,
                                          @Nonnull final WidthSpec aWidth)
  {
    final PLHBoxColumn aColumn = new PLHBoxColumn (aElement, aWidth);
    _addAndReturnColumn (nIndex, aColumn);
    return aColumn;
  }

  @Nonnull
  public IMPLTYPE addColumn (@CheckForSigned final int nIndex,
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

    m_aColumns.removeAtIndex (nIndex);
    return thisAsT ();
  }

  public boolean isVertSplittable ()
  {
    return m_bVertSplittable;
  }

  @Nonnull
  public IMPLTYPE setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return thisAsT ();
  }

  public boolean containsAnySplittableElement ()
  {
    return m_aColumns.containsAny (x -> x.getElement ().isVertSplittable ());
  }

  @Override
  @Nonnull
  public EChange visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    EChange ret = EChange.UNCHANGED;
    for (final PLHBoxColumn aColumn : m_aColumns)
      ret = ret.or (aColumn.getElement ().visit (aVisitor));
    return ret;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    m_aPreparedColumnSize = new SizeSpec [m_aColumns.size ()];
    m_aPreparedElementSize = new SizeSpec [m_aColumns.size ()];

    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();
    float fUsedWidthFull = 0;
    float fMaxColumnHeightFull = 0;
    float fMaxContentHeightNet = 0;

    int nStarColumns = 0;
    int nAutoColumns = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
      switch (aColumn.getWidth ().getType ())
      {
        case STAR:
          ++nStarColumns;
          break;
        case AUTO:
          ++nAutoColumns;
          break;
      }

    int nIndex = 0;
    float fRestWidth = fElementWidth;
    // 1. prepare all absolute width items
    {
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        if (aColumn.getWidth ().isAbsolute ())
        {
          final IPLRenderableObject <?> aElement = aColumn.getElement ();
          // Full width of this element
          final float fColumnWidth = aColumn.getWidth ().getEffectiveValue (fElementWidth);

          // Prepare child element
          final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                          fColumnWidth,
                                                                                          fElementHeight));

          // Update used width
          fUsedWidthFull += fColumnWidth;
          fRestWidth -= fColumnWidth;

          // Update used height
          fMaxContentHeightNet = Math.max (fMaxContentHeightNet, aElementPreparedSize.getHeight ());
          final float fColumnHeightFull = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
          fMaxColumnHeightFull = Math.max (fMaxColumnHeightFull, fColumnHeightFull);

          // Remember width and height for element (without padding and margin)
          m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeightFull);
          m_aPreparedElementSize[nIndex] = aElementPreparedSize;
        }
        ++nIndex;
      }
    }

    // 2. prepare all auto widths items
    {
      // First pass: identify all auto columns that directly fit in their
      // available column width

      float fRemainingWidthAutoFull = 0;
      float fUsedWidthAutoTooWide = 0;

      // Full width of this element
      final float fAvailableAutoColumnWidth = fRestWidth / (nAutoColumns + nStarColumns);
      final float fAvailableAutoColumnWidthAll = fAvailableAutoColumnWidth * nAutoColumns;

      final SizeSpec [] aTooWideAutoCols = new SizeSpec [m_aColumns.size ()];

      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        if (aColumn.getWidth ().isAuto ())
        {
          final IPLRenderableObject <?> aElement = aColumn.getElement ();

          // Prepare child element
          final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                          fAvailableAutoColumnWidthAll,
                                                                                          fElementHeight));

          // Use the used size of the element as the column width
          final float fColumnWidthFull = aElementPreparedSize.getWidth () + aElement.getOutlineXSum ();

          if (fColumnWidthFull <= fAvailableAutoColumnWidth)
          {
            // Update used width
            fUsedWidthFull += fColumnWidthFull;

            // What's left for other auto columns?
            fRemainingWidthAutoFull += fAvailableAutoColumnWidth - fColumnWidthFull;

            // Update used height
            fMaxContentHeightNet = Math.max (fMaxContentHeightNet, aElementPreparedSize.getHeight ());
            final float fColumnHeightFull = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
            fMaxColumnHeightFull = Math.max (fMaxColumnHeightFull, fColumnHeightFull);

            // Remember width and height for element (without padding and
            // margin)
            m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidthFull, fColumnHeightFull);
            m_aPreparedElementSize[nIndex] = aElementPreparedSize;
          }
          else
          {
            // Remember prepared sized
            aTooWideAutoCols[nIndex] = aElementPreparedSize;

            // The whole column width remains
            fRemainingWidthAutoFull += fAvailableAutoColumnWidth;

            // What would be used ideally
            fUsedWidthAutoTooWide += fColumnWidthFull;
          }
        }
        ++nIndex;
      }

      // Second pass: split all too wide auto columns on fRemainingWidthAuto
      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        // Only consider too-wide auto columns
        if (aColumn.getWidth ().isAuto () && aTooWideAutoCols[nIndex] != null)
        {
          final IPLRenderableObject <?> aElement = aColumn.getElement ();

          // Previously prepared size including outline
          final float fTooWideColumnWidth = aTooWideAutoCols[nIndex].getWidth () + aElement.getOutlineXSum ();

          // Percentage of used width compared to total used width of all too
          // wide columns (0-1)
          final float fAvailableColumnWidthPerc = fTooWideColumnWidth / fUsedWidthAutoTooWide;

          // Use x% of remaining width
          final float fNewAvailableColumnWidth = fRemainingWidthAutoFull * fAvailableColumnWidthPerc;

          // Prepare child element
          ((AbstractPLRenderableObject <?>) aElement).internalMarkAsNotPrepared ();
          final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                          fNewAvailableColumnWidth,
                                                                                          fElementHeight));

          // Use the used size of the element as the column width
          final float fColumnWidthWidth = aElementPreparedSize.getWidth () + aElement.getOutlineXSum ();
          // Update used width
          fUsedWidthFull += fColumnWidthWidth;

          // Update used height
          fMaxContentHeightNet = Math.max (fMaxContentHeightNet, aElementPreparedSize.getHeight ());
          final float fColumnHeightFull = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
          fMaxColumnHeightFull = Math.max (fMaxColumnHeightFull, fColumnHeightFull);

          // Remember width and height for element (without padding and margin)
          m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidthWidth, fColumnHeightFull);
          m_aPreparedElementSize[nIndex] = aElementPreparedSize;
        }
        ++nIndex;
      }

      // remaining unused parts of auto columns is automatically available to
      // star width columns (based on fUsedWidthFull)
    }

    // 3. prepare all star widths items
    {
      fRestWidth = fElementWidth - fUsedWidthFull;
      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        if (aColumn.getWidth ().isStar ())
        {
          final IPLRenderableObject <?> aElement = aColumn.getElement ();
          // Full width of this element
          final float fColumnWidth = fRestWidth / nStarColumns;

          // Prepare child element
          final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                          fColumnWidth,
                                                                                          fElementHeight));

          // Update used width
          fUsedWidthFull += fColumnWidth;
          // Don't change rest-width!

          // Update used height
          fMaxContentHeightNet = Math.max (fMaxContentHeightNet, aElementPreparedSize.getHeight ());
          final float fColumnHeightFull = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
          fMaxColumnHeightFull = Math.max (fMaxColumnHeightFull, fColumnHeightFull);

          // Remember width and height for element (without padding and margin)
          m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeightFull);
          m_aPreparedElementSize[nIndex] = aElementPreparedSize;
        }
        ++nIndex;
      }
    }

    // Set min size for block elements
    {
      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        if (aElement instanceof AbstractPLElement <?>)
        {
          final AbstractPLElement <?> aRealElement = (AbstractPLElement <?>) aElement;
          // Set minimum column width and height as prepared width
          aRealElement.setMinSize (m_aPreparedColumnSize[nIndex].getWidth () -
                                   aRealElement.getOutlineXSum (),
                                   fMaxContentHeightNet);
        }
        ++nIndex;
      }
    }

    // Small consistency check (with rounding included)
    if (GlobalDebug.isDebugMode ())
    {
      if (fUsedWidthFull - fElementWidth > 0.01)
        s_aLogger.warn (getDebugID () +
                        " uses more width (" +
                        fUsedWidthFull +
                        ") than available (" +
                        fElementWidth +
                        ")!");
      if (fMaxColumnHeightFull - fElementHeight > 0.01)
        if (!isVertSplittable ())
          s_aLogger.warn (getDebugID () +
                          " uses more height (" +
                          fMaxColumnHeightFull +
                          ") than available (" +
                          fElementHeight +
                          ")!");
    }

    return new SizeSpec (fUsedWidthFull, fMaxColumnHeightFull);
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    m_aPreparedColumnSize = null;
    m_aPreparedElementSize = null;
    for (final PLHBoxColumn aColumn : m_aColumns)
      ((AbstractPLRenderableObject <?>) aColumn.getElement ()).internalMarkAsNotPrepared ();
  }

  /**
   * Create an empty element that is to be used as a place holder for splitting
   *
   * @param fWidth
   *        Width
   * @param fHeight
   *        height
   * @return Never <code>null</code>.
   */
  @Nonnull
  protected IPLRenderableObject <?> splitVertCreateEmptyElement (final float fWidth, final float fHeight)
  {
    return PLSpacerX.createPrepared (fWidth, 0);
  }

  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    if (!containsAnySplittableElement ())
    {
      // Splitting makes no sense
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "cannot split because no splittable elements are contained");
      return null;
    }

    final int nCols = m_aColumns.size ();

    // Check if height is exceeded
    {
      boolean bAnySplittingPossibleAndNecessary = false;
      for (int i = 0; i < nCols; ++i)
      {
        // Is the current element higher and splittable?
        final IPLRenderableObject <?> aColumnElement = getColumnElementAtIndex (i);
        if (aColumnElement.isVertSplittable ())
        {
          final float fColumnHeightFull = m_aPreparedColumnSize[i].getHeight ();
          if (fColumnHeightFull > fAvailableHeight)
          {
            bAnySplittingPossibleAndNecessary = true;
            break;
          }
        }
      }

      if (!bAnySplittingPossibleAndNecessary)
      {
        // Splitting makes no sense
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "no need to split because all splittable elements easily fit into the available height (" +
                                    fAvailableHeight +
                                    ")");
        return null;
      }
    }

    final AbstractPLHBox <?> aHBox1 = internalCreateNewObject (thisAsT ()).setBasicDataFrom (this)
                                                                          .setID (getID () + "-1")
                                                                          .setVertSplittable (false);
    final AbstractPLHBox <?> aHBox2 = internalCreateNewObject (thisAsT ()).setBasicDataFrom (this)
                                                                          .setID (getID () + "-2")
                                                                          .setVertSplittable (true);

    // Fill all columns with empty content
    for (int i = 0; i < nCols; ++i)
    {
      final PLHBoxColumn aColumn = getColumnAtIndex (i);
      final WidthSpec aColumnWidth = aColumn.getWidth ();
      final float fElementWidth = m_aPreparedColumnSize[i].getWidth ();
      final float fElementHeight = fAvailableHeight;

      // Create empty element with the same width as the original element
      aHBox1.addColumn (splitVertCreateEmptyElement (fElementWidth, fElementHeight), aColumnWidth);
      aHBox2.addColumn (splitVertCreateEmptyElement (fElementWidth, fElementHeight), aColumnWidth);
    }

    float fHBox1MaxHeight = 0;
    float fHBox2MaxHeight = 0;
    final SizeSpec [] fHBox1ColumnSizes = new SizeSpec [m_aPreparedColumnSize.length];
    final SizeSpec [] fHBox2ColumnSizes = new SizeSpec [m_aPreparedColumnSize.length];
    final SizeSpec [] fHBox1ElementSizes = new SizeSpec [m_aPreparedElementSize.length];
    final SizeSpec [] fHBox2ElementSizes = new SizeSpec [m_aPreparedElementSize.length];

    // Start splitting columns
    boolean bDidSplitAnyColumn = false;
    for (int nCol = 0; nCol < nCols; nCol++)
    {
      final IPLRenderableObject <?> aColumnElement = getColumnElementAtIndex (nCol);
      final boolean bIsSplittable = aColumnElement.isVertSplittable ();
      final float fColumnWidth = m_aPreparedColumnSize[nCol].getWidth ();
      final float fColumnHeight = m_aPreparedColumnSize[nCol].getHeight ();
      final float fElementWidth = m_aPreparedElementSize[nCol].getWidth ();

      boolean bDidSplitColumn = false;
      if (fColumnHeight > fAvailableHeight && bIsSplittable)
      {
        final float fSplitWidth = fElementWidth;
        final float fSplitHeight = fAvailableHeight - aColumnElement.getOutlineYSum ();
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Trying to split " +
                                    aColumnElement.getDebugID () +
                                    " with height " +
                                    fColumnHeight +
                                    " into pieces for remaining size " +
                                    PLDebug.getWH (fSplitWidth, fSplitHeight));

        // Use width and height without padding and margin!
        final PLSplitResult aSplitResult = aColumnElement.getAsSplittable ().splitElementVert (fSplitWidth,
                                                                                               fSplitHeight);
        if (aSplitResult != null)
        {
          final IPLRenderableObject <?> aHBox1Element = aSplitResult.getFirstElement ().getElement ();
          aHBox1.getColumnAtIndex (nCol).internalSetElement (aHBox1Element);

          final IPLRenderableObject <?> aHBox2Element = aSplitResult.getSecondElement ().getElement ();
          aHBox2.getColumnAtIndex (nCol).internalSetElement (aHBox2Element);

          // Use the full height, because the column itself has no padding or
          // margin!
          fHBox1ColumnSizes[nCol] = new SizeSpec (fColumnWidth, aSplitResult.getFirstElement ().getHeightFull ());
          fHBox2ColumnSizes[nCol] = new SizeSpec (fColumnWidth, aSplitResult.getSecondElement ().getHeightFull ());
          fHBox1ElementSizes[nCol] = new SizeSpec (fElementWidth, aSplitResult.getFirstElement ().getHeight ());
          fHBox2ElementSizes[nCol] = new SizeSpec (fElementWidth, aSplitResult.getSecondElement ().getHeight ());
          bDidSplitColumn = true;
          bDidSplitAnyColumn = true;

          if (PLDebug.isDebugSplit ())
            PLDebug.debugSplit (this,
                                "Split column element " +
                                      aColumnElement.getDebugID () +
                                      " (Column " +
                                      nCol +
                                      ") into pieces: " +
                                      aHBox1Element.getDebugID () +
                                      " (" +
                                      aSplitResult.getFirstElement ().getWidth () +
                                      "+" +
                                      aHBox1Element.getOutlineXSum () +
                                      " & " +
                                      aSplitResult.getFirstElement ().getHeight () +
                                      "+" +
                                      aHBox1Element.getOutlineYSum () +
                                      ") and " +
                                      aHBox2Element.getDebugID () +
                                      " (" +
                                      aSplitResult.getSecondElement ().getWidth () +
                                      "+" +
                                      aHBox2Element.getOutlineXSum () +
                                      " & " +
                                      aSplitResult.getSecondElement ().getHeight () +
                                      "+" +
                                      aHBox2Element.getOutlineYSum () +
                                      ") for available height " +
                                      fAvailableHeight);
        }
        else
        {
          if (PLDebug.isDebugSplit ())
            PLDebug.debugSplit (this,
                                "Failed to split column element " +
                                      aColumnElement.getDebugID () +
                                      " (Column " +
                                      nCol +
                                      ") with height " +
                                      fColumnHeight +
                                      " into pieces for available height " +
                                      fAvailableHeight);
        }
      }

      if (!bDidSplitColumn)
      {
        // No splitting and cell fits totally in available height
        aHBox1.getColumnAtIndex (nCol).internalSetElement (aColumnElement);

        fHBox1ColumnSizes[nCol] = new SizeSpec (fColumnWidth, Math.min (fColumnHeight, fAvailableHeight));
        fHBox2ColumnSizes[nCol] = new SizeSpec (fColumnWidth, 0);
        fHBox1ElementSizes[nCol] = m_aPreparedElementSize[nCol];
        fHBox2ElementSizes[nCol] = new SizeSpec (fElementWidth, 0);
      }

      // calculate max column height
      fHBox1MaxHeight = Math.max (fHBox1MaxHeight, fHBox1ColumnSizes[nCol].getHeight ());
      fHBox2MaxHeight = Math.max (fHBox2MaxHeight, fHBox2ColumnSizes[nCol].getHeight ());
    }

    if (!bDidSplitAnyColumn)
    {
      // Nothing was splitted
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Weird: No column was split and the height is OK!");
      return null;
    }

    // Set min size for block elements
    {
      for (int nIndex = 0; nIndex < m_aColumns.size (); ++nIndex)
      {
        final IPLRenderableObject <?> aElement1 = aHBox1.getColumnElementAtIndex (nIndex);
        if (aElement1 instanceof AbstractPLElement <?>)
        {
          // Set minimum column width and height as prepared width
          final AbstractPLElement <?> aRealElement1 = (AbstractPLElement <?>) aElement1;
          aRealElement1.setMinSize (m_aPreparedColumnSize[nIndex].getWidth () -
                                    aRealElement1.getOutlineXSum (),
                                    fHBox1MaxHeight);
        }
        final IPLRenderableObject <?> aElement2 = aHBox2.getColumnElementAtIndex (nIndex);
        if (aElement2 instanceof AbstractPLElement <?>)
        {
          // Set minimum column width and height as prepared width
          final AbstractPLElement <?> aRealElement2 = (AbstractPLElement <?>) aElement2;
          aRealElement2.setMinSize (m_aPreparedColumnSize[nIndex].getWidth () -
                                    aRealElement2.getOutlineXSum (),
                                    fHBox2MaxHeight);
        }
      }
    }

    // mark new hboxes as prepared
    aHBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fHBox1MaxHeight));
    aHBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fHBox2MaxHeight));
    // set prepared column sizes
    aHBox1.m_aPreparedColumnSize = fHBox1ColumnSizes;
    aHBox2.m_aPreparedColumnSize = fHBox2ColumnSizes;
    // set prepared element sizes
    aHBox1.m_aPreparedElementSize = fHBox1ElementSizes;
    aHBox2.m_aPreparedElementSize = fHBox2ElementSizes;

    return new PLSplitResult (new PLElementWithSize (aHBox1, new SizeSpec (fAvailableWidth, fHBox1MaxHeight)),
                              new PLElementWithSize (aHBox2, new SizeSpec (fAvailableWidth, fHBox2MaxHeight)));
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    float fCurX = aCtx.getStartLeft ();
    final float fStartY = aCtx.getStartTop ();

    int nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      final IPLRenderableObject <?> aElement = aColumn.getElement ();
      final float fColumnWidth = m_aPreparedColumnSize[nIndex].getWidth ();
      final float fColumnHeight = m_aPreparedColumnSize[nIndex].getHeight ();

      final PageRenderContext aItemCtx = new PageRenderContext (aCtx, fCurX, fStartY, fColumnWidth, fColumnHeight);
      aElement.render (aItemCtx);

      // Update X-pos
      fCurX += fColumnWidth;
      ++nIndex;
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Columns", m_aColumns)
                            .append ("VertSplittable", m_bVertSplittable)
                            .appendIfNotNull ("PreparedColumnSize", m_aPreparedColumnSize)
                            .appendIfNotNull ("PreparedElementSize", m_aPreparedElementSize)
                            .toString ();
  }
}
