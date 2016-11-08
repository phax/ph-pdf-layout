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
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.AbstractPLBlockElement;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

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

  private final ICommonsList <PLHBoxColumn> m_aColumns = new CommonsArrayList <> ();
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
    final PLHBoxColumn aColumn = new PLHBoxColumn (aElement, aWidth);
    _addAndReturnColumn (-1, aColumn);
    return aColumn;
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
    final PLHBoxColumn aColumn = new PLHBoxColumn (aElement, aWidth);
    _addAndReturnColumn (nIndex, aColumn);
    return aColumn;
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
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    for (final PLHBoxColumn aColumn : m_aColumns)
      aColumn.getElement ().visit (aVisitor);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    m_aPreparedColumnSize = new SizeSpec [m_aColumns.size ()];
    m_aPreparedElementSize = new SizeSpec [m_aColumns.size ()];

    final float fAvailableWidth = aCtx.getAvailableWidth ();
    final float fAvailableHeight = aCtx.getAvailableHeight ();
    float fUsedWidthFull = 0;
    float fMaxColumnHeight = 0;
    float fMaxContentHeight = 0;

    int nStarColumns = 0;
    int nAutoColumns = 0;
    // FIXME unnecessary for-loop (do it one loop underneath?)
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
    float fRestWidth = fAvailableWidth;
    // 1. prepare all non-star width items
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (aColumn.getWidth ().isAbsolute ())
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fColumnWidth = aColumn.getWidth ().getEffectiveValue (fAvailableWidth);

        // Prepare child element
        final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                        fColumnWidth,
                                                                                        fAvailableHeight));

        // Update used width
        fUsedWidthFull += fColumnWidth;
        fRestWidth -= fColumnWidth;

        // Update used height
        fMaxContentHeight = Math.max (fMaxContentHeight, aElementPreparedSize.getHeight ());
        final float fColumnHeight = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
        fMaxColumnHeight = Math.max (fMaxColumnHeight, fColumnHeight);

        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeight);
        m_aPreparedElementSize[nIndex] = aElementPreparedSize;
      }
      ++nIndex;
    }

    // 2. prepare all auto widths items
    nIndex = 0;
    if (false)
    {
      float fRestWidthAuto = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        if (aColumn.getWidth ().isAuto ())
        {
          final IPLRenderableObject <?> aElement = aColumn.getElement ();
          // Full width of this element
          final float fAvailableColumnWidth = fRestWidth / (nAutoColumns + nStarColumns);
          final float fAvailableWidthAuto = fAvailableColumnWidth * nAutoColumns;

          // Prepare child element
          final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                          fAvailableWidthAuto,
                                                                                          fAvailableHeight));
          if (aElementPreparedSize.getWidth () <= fAvailableColumnWidth)
          {
            fRestWidthAuto += fAvailableColumnWidth - aElementPreparedSize.getWidth ();
            // Use the used size of the element as the column width
            final float fColumnWidth = aElementPreparedSize.getWidth () + aElement.getOutlineXSum ();
            fRestWidthAuto += fColumnWidth;
            // Update used width
            fUsedWidthFull += fColumnWidth;

            // Update used height
            fMaxContentHeight = Math.max (fMaxContentHeight, aElementPreparedSize.getHeight ());
            final float fColumnHeight = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
            fMaxColumnHeight = Math.max (fMaxColumnHeight, fColumnHeight);

            // Remember width and height for element (without padding and
            // margin)
            m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeight);
            m_aPreparedElementSize[nIndex] = aElementPreparedSize;
          }
        }
      }
    }

    nIndex = 0;
    for (final PLHBoxColumn aColumn : m_aColumns)
    {
      if (aColumn.getWidth ().isAuto ())
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        // Full width of this element
        final float fAvailableColumnWidth = fRestWidth / (nAutoColumns + nStarColumns);

        // Prepare child element
        final SizeSpec aElementPreparedSize = aElement.prepare (new PreparationContext (aCtx.getGlobalContext (),
                                                                                        fAvailableColumnWidth,
                                                                                        fAvailableHeight));

        // Use the used size of the element as the column width
        final float fColumnWidth = aElementPreparedSize.getWidth () + aElement.getOutlineXSum ();
        // Update used width
        fUsedWidthFull += fColumnWidth;

        // Update used height
        fMaxContentHeight = Math.max (fMaxContentHeight, aElementPreparedSize.getHeight ());
        final float fColumnHeight = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
        fMaxColumnHeight = Math.max (fMaxColumnHeight, fColumnHeight);

        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeight);
        m_aPreparedElementSize[nIndex] = aElementPreparedSize;

      }
      ++nIndex;
    }

    // 3. prepare all star widths items
    fRestWidth = fAvailableWidth - fUsedWidthFull;
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
                                                                                        fAvailableHeight));

        // Update used width
        fUsedWidthFull += fColumnWidth;
        // Don't change rest-width!

        // Update used height
        fMaxContentHeight = Math.max (fMaxContentHeight, aElementPreparedSize.getHeight ());
        final float fColumnHeight = aElementPreparedSize.getHeight () + aElement.getOutlineYSum ();
        fMaxColumnHeight = Math.max (fMaxColumnHeight, fColumnHeight);

        // Remember width and height for element (without padding and margin)
        m_aPreparedColumnSize[nIndex] = new SizeSpec (fColumnWidth, fColumnHeight);
        m_aPreparedElementSize[nIndex] = aElementPreparedSize;
      }
      ++nIndex;
    }

    // Set min size for block elements
    {
      nIndex = 0;
      for (final PLHBoxColumn aColumn : m_aColumns)
      {
        final IPLRenderableObject <?> aElement = aColumn.getElement ();
        if (aElement instanceof AbstractPLBlockElement <?>)
        {
          final AbstractPLBlockElement <?> aRealElement = (AbstractPLBlockElement <?>) aElement;
          // Set minimum column width and height as prepared width
          aRealElement.setMinSize (m_aPreparedColumnSize[nIndex].getWidth () -
                                   aElement.getOutlineXSum (),
                                   fMaxContentHeight);
        }
        ++nIndex;
      }
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
      if (fMaxColumnHeight - fAvailableHeight > 0.01)
        if (!isVertSplittable ())
          s_aLogger.warn (getDebugID () +
                          " uses more height (" +
                          fMaxColumnHeight +
                          ") than available (" +
                          fAvailableHeight +
                          ")!");
    }

    return new SizeSpec (fUsedWidthFull, fMaxColumnHeight);
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
      boolean bAnySplittingPossible = false;
      for (int i = 0; i < nCols; ++i)
      {
        // Is the current element higher and splittable?
        final IPLRenderableObject <?> aColumnElement = getColumnElementAtIndex (i);
        if (aColumnElement.isVertSplittable ())
        {
          final float fColumnHeightFull = m_aPreparedColumnSize[i].getHeight ();
          if (fColumnHeightFull > fAvailableHeight)
          {
            bAnySplittingPossible = true;
            break;
          }
        }
      }

      if (!bAnySplittingPossible)
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

    final AbstractPLHBox <?> aHBox1 = new PLHBox ().setBasicDataFrom (this)
                                                   .setID (getID () + "-1")
                                                   .setVertSplittable (false);
    final AbstractPLHBox <?> aHBox2 = new PLHBox ().setBasicDataFrom (this)
                                                   .setID (getID () + "-2")
                                                   .setVertSplittable (true);

    // Fill all columns with empty content
    for (int i = 0; i < nCols; ++i)
    {
      final PLHBoxColumn aColumn = getColumnAtIndex (i);
      final WidthSpec aColumnWidth = aColumn.getWidth ();

      // Create empty element with the same width as the original element
      PLSpacerX aEmptyElement = PLSpacerX.createPrepared (m_aPreparedColumnSize[i].getWidth (), 0);
      aHBox1.addColumn (aEmptyElement, aColumnWidth);

      aEmptyElement = PLSpacerX.createPrepared (m_aPreparedColumnSize[i].getWidth (), 0);
      aHBox2.addColumn (aEmptyElement, aColumnWidth);
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
                                    " into pieces for remaining size " +
                                    PLDebug.getWH (fSplitWidth, fSplitHeight));

        // Use width and height without padding and margin!
        final PLSplitResult aSplitResult = aColumnElement.getAsSplittable ().splitElementVert (fSplitWidth,
                                                                                               fSplitHeight);
        if (aSplitResult != null)
        {
          final IPLRenderableObject <?> aHBox1Element = aSplitResult.getFirstElement ().getElement ();
          aHBox1.getColumnAtIndex (nCol).setElement (aHBox1Element);

          final IPLRenderableObject <?> aHBox2Element = aSplitResult.getSecondElement ().getElement ();
          aHBox2.getColumnAtIndex (nCol).setElement (aHBox2Element);

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
                                      ") into pieces for available height " +
                                      fAvailableHeight);
        }
      }

      if (!bDidSplitColumn)
      {
        if (fColumnHeight > fAvailableHeight)
        {
          // We should have split but did not
          if (bIsSplittable)
          {
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Column " +
                                        nCol +
                                        " contains splittable element " +
                                        aColumnElement.getDebugID () +
                                        " which creates an overflow by " +
                                        (fColumnHeight - fAvailableHeight) +
                                        " for available height " +
                                        fAvailableHeight +
                                        "!");
          }
          else
          {
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Column " +
                                        nCol +
                                        " contains non splittable element " +
                                        aColumnElement.getDebugID () +
                                        " which creates an overflow by " +
                                        (fColumnHeight - fAvailableHeight) +
                                        " for max height " +
                                        fAvailableHeight +
                                        "!");
          }

          // One column of the row is too large and cannot be split -> the whole
          // row cannot be split!
          return null;
        }

        // No splitting and cell fits totally in available height
        aHBox1.getColumnAtIndex (nCol).setElement (aColumnElement);

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
