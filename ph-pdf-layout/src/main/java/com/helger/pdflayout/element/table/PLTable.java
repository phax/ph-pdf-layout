/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.table;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.misc.DevelopersNote;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLHasMargin;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.element.vbox.PLVBoxRow;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.EValueUOMType;
import com.helger.pdflayout.spec.HeightSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * A special table with a repeating header
 *
 * @author Philip Helger
 */
public class PLTable extends AbstractPLRenderableObject <PLTable> implements
                     IPLSplittableObject <PLTable, PLTable>,
                     IPLHasMargin <PLTable>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PLTable.class);

  // All column widths
  private final ICommonsList <WidthSpec> m_aWidths;
  // With type to use - may be null
  private final EValueUOMType m_eCommonWidthType;
  // VBox with all the PLTableRow elements
  private PLVBox m_aRows = new PLVBox ().setVertSplittable (true).setFullWidth (true);
  // Margin around the table
  private MarginSpec m_aMargin = DEFAULT_MARGIN;

  /**
   * Don't use that constructor. Use {@link #PLTable(Iterable)} or {@link #PLTable(WidthSpec...)}!!!
   */
  @Deprecated
  @DevelopersNote ("This ctor is only present to indicate if the varargs ctor would be used without parameters")
  public PLTable ()
  {
    this (new CommonsArrayList <> ());
  }

  /**
   * @param aWidths
   *        Must all be of the same type! "auto" width is not allowed - only "star" may be used.
   */
  public PLTable (@NonNull @Nonempty final WidthSpec... aWidths)
  {
    this (new CommonsArrayList <> (aWidths));
  }

  /**
   * @param aWidths
   *        Must all be of the same type! "auto" width is not allowed - only "star" may be used.
   */
  public PLTable (@NonNull @Nonempty final Iterable <? extends WidthSpec> aWidths)
  {
    ValueEnforcer.notEmptyNoNullValue (aWidths, "Widths");

    // Check if all width are of the same type
    EValueUOMType eWidthType = null;
    boolean bDifferentWidthTypes = false;
    for (final WidthSpec aWidth : aWidths)
    {
      final EValueUOMType eCurWidth = aWidth.getType ();
      if (eCurWidth == EValueUOMType.AUTO)
        throw new IllegalArgumentException ("Width type auto is not allowed for tables! Use type star instead!");

      if (eWidthType == null)
        eWidthType = eCurWidth;
      else
        if (eCurWidth != eWidthType)
        {
          if (true)
          {
            // This means, colspan cannot be used
            bDifferentWidthTypes = true;
          }
          else
            throw new IllegalArgumentException ("All widths must be of the same type! Found " +
                                                eWidthType +
                                                " and " +
                                                eCurWidth);
        }
    }

    m_aWidths = new CommonsArrayList <> (aWidths);
    m_eCommonWidthType = bDifferentWidthTypes ? null : eWidthType;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onAfterSetID (@Nullable final String sOldElementID)
  {
    // Also change the derived ID
    m_aRows.setID (getID () + "-vbox");
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public PLTable setBasicDataFrom (@NonNull final PLTable aSource)
  {
    super.setBasicDataFrom (aSource);
    m_aRows.setBasicDataFrom (aSource.m_aRows);
    setMargin (aSource.m_aMargin);
    return this;
  }

  @NonNull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @NonNull
  public final PLTable setMargin (@NonNull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return this;
  }

  /**
   * @return A copy of the list with all widths as specified in the constructor. Neither
   *         <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsList <WidthSpec> getAllWidths ()
  {
    return m_aWidths.getClone ();
  }

  /**
   * @return The number of columns in the table. Always &ge; 0.
   */
  @Nonnegative
  public int getColumnCount ()
  {
    return m_aWidths.size ();
  }

  @NonNull
  public PLTable setHeaderRowCount (@Nonnegative final int nHeaderRowCount)
  {
    m_aRows.setHeaderRowCount (nHeaderRowCount);
    return this;
  }

  @Nonnegative
  public int getHeaderRowCount ()
  {
    return m_aRows.getHeaderRowCount ();
  }

  @NonNull
  public PLTableRow addAndReturnRow (@NonNull final PLTableCell... aCells)
  {
    return addAndReturnRow (new CommonsArrayList <> (aCells), m_aRows.getDefaultHeight ());
  }

  /**
   * Add a new table row with auto height. All contained elements are added with the specified width
   * in the constructor. <code>null</code> elements are represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return the added table row and never <code>null</code>.
   */
  @NonNull
  public PLTableRow addAndReturnRow (@NonNull final Iterable <? extends PLTableCell> aCells)
  {
    return addAndReturnRow (aCells, m_aRows.getDefaultHeight ());
  }

  /**
   * Add a new table row. All contained elements are added with the specified width in the
   * constructor. <code>null</code> elements are represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code> but may contain <code>null</code> values.
   * @param aHeight
   *        Row height to be used. May not be <code>null</code>.
   * @return the added table row and never <code>null</code>.
   */
  @NonNull
  public PLTableRow addAndReturnRow (@NonNull final Iterable <? extends PLTableCell> aCells,
                                     @NonNull final HeightSpec aHeight)
  {
    ValueEnforcer.notNull (aCells, "Cells");

    // Small consistency check
    {
      int nUsedCols = 0;
      for (final PLTableCell aCell : aCells)
        if (aCell == null)
          nUsedCols++;
        else
          nUsedCols += aCell.getColSpan ();
      if (nUsedCols > m_aWidths.size ())
        throw new IllegalArgumentException ("More cells in row (" +
                                            nUsedCols +
                                            ") than defined in the table (" +
                                            m_aWidths.size () +
                                            ")!");
    }

    final PLTableRow aRow = new PLTableRow ();
    int nWidthIndex = 0;
    for (final PLTableCell aCell : aCells)
    {
      // Make sure we don't have null cells
      final PLTableCell aRealCell = aCell != null ? aCell : new PLTableCell (new PLSpacerX ());

      final int nColSpan = aRealCell.getColSpan ();
      if (nColSpan == 1)
      {
        // Simple case - cell spanning 1 column
        aRow.addCell (aRealCell, m_aWidths.get (nWidthIndex));
      }
      else
      {
        // Apply column span
        if (m_eCommonWidthType == null)
          throw new IllegalArgumentException ("Since columns with different width types are used, 'colspan' must be 1");

        final List <WidthSpec> aWidths = m_aWidths.subList (nWidthIndex, nWidthIndex + nColSpan);
        final WidthSpec aRealWidth;
        if (m_eCommonWidthType == EValueUOMType.STAR)
        {
          // aggregate
          aRealWidth = WidthSpec.perc (nColSpan * 100f / m_aWidths.size ());
        }
        else
        {
          // aggregate values
          float fWidth = 0;
          for (final WidthSpec aWidth : aWidths)
            fWidth += aWidth.getValue ();
          aRealWidth = new WidthSpec (m_eCommonWidthType, fWidth);
        }
        aRow.addCell (aRealCell, aRealWidth);
      }
      nWidthIndex += nColSpan;
    }
    addRow (aRow, aHeight);
    return aRow;
  }

  @NonNull
  public PLTable addRow (@NonNull final PLTableRow aRow)
  {
    return addRow (aRow, m_aRows.getDefaultHeight ());
  }

  @NonNull
  public PLTable addRow (@NonNull final PLTableRow aRow, @NonNull final HeightSpec aHeight)
  {
    ValueEnforcer.notNull (aRow, "Row");
    m_aRows.addRow (aRow, aHeight);
    return this;
  }

  /**
   * Don't call this.
   *
   * @return this for chaining
   */
  @NonNull
  @Deprecated
  public PLTable addRow ()
  {
    LOGGER.warn ("You are calling the no-operation method 'PLTable.addRow()' - no row is added!!!");
    return this;
  }

  /**
   * Add a new table row with auto height. All contained elements are added with the specified width
   * in the constructor. <code>null</code> elements are represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public PLTable addRow (@NonNull final PLTableCell... aCells)
  {
    return addRow (new CommonsArrayList <> (aCells), m_aRows.getDefaultHeight ());
  }

  /**
   * Add a new table row with auto height. All contained elements are added with the specified width
   * in the constructor. <code>null</code> elements are represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public PLTable addRow (@NonNull final Iterable <? extends PLTableCell> aCells)
  {
    return addRow (aCells, m_aRows.getDefaultHeight ());
  }

  /**
   * Add a new table row. All contained elements are added with the specified width in the
   * constructor. <code>null</code> elements are represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @param aHeight
   *        Row height to be used.
   * @return this
   */
  @NonNull
  public PLTable addRow (@NonNull final Iterable <? extends PLTableCell> aCells, @NonNull final HeightSpec aHeight)
  {
    addAndReturnRow (aCells, aHeight);
    return this;
  }

  public void forEachRow (@NonNull final Consumer <? super PLTableRow> aConsumer)
  {
    m_aRows.forEachRow (x -> aConsumer.accept ((PLTableRow) x.getElement ()));
  }

  public void forEachRowByIndex (@NonNull final ObjIntConsumer <? super PLTableRow> aConsumer)
  {
    m_aRows.forEachRowByIndex ( (x, idx) -> aConsumer.accept ((PLTableRow) x.getElement (), idx));
  }

  public void forEachRow (final int nStartRowIncl,
                          final int nEndRowIncl,
                          @NonNull final Consumer <? super PLTableRow> aConsumer)
  {
    forEachRowByIndex ( (x, idx) -> {
      if (idx >= nStartRowIncl && idx <= nEndRowIncl)
        aConsumer.accept (x);
    });
  }

  public void forEachRow (final int nStartRowIncl,
                          final int nEndRowIncl,
                          @NonNull final ObjIntConsumer <? super PLTableRow> aConsumer)
  {
    forEachRowByIndex ( (x, idx) -> {
      if (idx >= nStartRowIncl && idx <= nEndRowIncl)
        aConsumer.accept (x, idx);
    });
  }

  @Nonnegative
  public int getRowCount ()
  {
    return m_aRows.getRowCount ();
  }

  public void forEachCell (@NonNull final Consumer <? super PLTableCell> aConsumer)
  {
    forEachRow (x -> x.forEachCell (aConsumer));
  }

  @Nullable
  public PLTableRow getRowAtIndex (@Nonnegative final int nIndex)
  {
    final PLVBoxRow aRow = m_aRows.getRowAtIndex (nIndex);
    return aRow == null ? null : (PLTableRow) aRow.getElement ();
  }

  @Nullable
  public PLTableCell getCellAtIndex (@Nonnegative final int nRowIndex, @Nonnegative final int nColIndex)
  {
    final PLTableRow aRow = getRowAtIndex (nRowIndex);
    return aRow == null ? null : aRow.getCellAtIndex (nColIndex);
  }

  @Override
  @NonNull
  public EChange visit (@NonNull final IPLVisitor aVisitor) throws IOException
  {
    final EChange ret = super.visit (aVisitor);
    return ret.or (m_aRows.visit (aVisitor));
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@NonNull final PreparationContext aCtx)
  {
    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();

    final PreparationContext aChildCtx = new PreparationContext (aCtx.getGlobalContext (),
                                                                 fElementWidth,
                                                                 fElementHeight);
    final SizeSpec aVBoxPreparedSize = m_aRows.prepare (aChildCtx);
    return aVBoxPreparedSize.plus (m_aRows.getOutlineXSum (), m_aRows.getOutlineYSum ());
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    m_aRows.internalMarkAsNotPrepared ();
  }

  public final boolean isVertSplittable ()
  {
    return m_aRows.isVertSplittable ();
  }

  @NonNull
  public final PLTable setVertSplittable (final boolean bVertSplittable)
  {
    m_aRows.setVertSplittable (bVertSplittable);
    return this;
  }

  @Override
  @NonNull
  public PLTable internalCreateNewVertSplitObject (@NonNull final PLTable aBase)
  {
    throw new UnsupportedOperationException ();
  }

  @NonNull
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    final float fSplitHeight = fAvailableHeight;
    if (PLDebugLog.isDebugSplit ())
      PLDebugLog.debugSplit (this,
                             "Trying to split " +
                                   m_aRows.getDebugID () +
                                   " into pieces for available width " +
                                   fAvailableWidth +
                                   " and height " +
                                   fSplitHeight);

    final PLSplitResult aSplitResult = m_aRows.splitElementVert (fAvailableWidth, fSplitHeight);
    if (!aSplitResult.getSplitResultType ().isSplit ())
      return aSplitResult;

    final PLTable aTable1 = new PLTable (m_aWidths);
    aTable1.setID (getID () + "-1");
    aTable1.setBasicDataFrom (this);
    aTable1.internalMarkAsPrepared (aSplitResult.getFirstElement ().getSize ());
    aTable1.m_aRows = (PLVBox) aSplitResult.getFirstElement ().getElement ();

    final PLTable aTable2 = new PLTable (m_aWidths);
    aTable2.setID (getID () + "-2");
    aTable2.setBasicDataFrom (this);
    aTable2.internalMarkAsPrepared (aSplitResult.getSecondElement ().getSize ());
    aTable2.m_aRows = (PLVBox) aSplitResult.getSecondElement ().getElement ();

    return PLSplitResult.createSplit (new PLElementWithSize (aTable1, aSplitResult.getFirstElement ().getSize ()),
                                      new PLElementWithSize (aTable2, aSplitResult.getSecondElement ().getSize ()));
  }

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    final PageRenderContext aChildCtx = new PageRenderContext (aCtx,
                                                               aCtx.getStartLeft () + getMarginLeft (),
                                                               aCtx.getStartTop () - getMarginTop (),
                                                               aCtx.getWidth () - getMarginXSum (),
                                                               aCtx.getHeight () - getMarginYSum ());
    m_aRows.render (aChildCtx);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Rows", m_aRows)
                            .append ("Width", m_aWidths)
                            .append ("WidthType", m_eCommonWidthType)
                            .append ("Margin", m_aMargin)
                            .getToString ();
  }

  /**
   * Create a new table with the specified percentages.
   *
   * @param aPercentages
   *        The array to use. The sum of all percentages should be &le; 100. May neither be
   *        <code>null</code> nor empty.
   * @return The created {@link PLTable} and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static PLTable createWithPercentage (@Nonempty final float @NonNull... aPercentages)
  {
    ValueEnforcer.notEmpty (aPercentages, "Percentages");

    final ICommonsList <WidthSpec> aWidths = new CommonsArrayList <> (aPercentages.length);
    for (final float fPercentage : aPercentages)
      aWidths.add (WidthSpec.perc (fPercentage));
    return new PLTable (aWidths);
  }

  /**
   * Create a new table with evenly sized columns.
   *
   * @param nColumnCount
   *        The number of columns to use. Must be &gt; 0.
   * @return The created {@link PLTable} and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static PLTable createWithEvenlySizedColumns (@Nonnegative final int nColumnCount)
  {
    ValueEnforcer.isGT0 (nColumnCount, "ColumnCount");

    final ICommonsList <WidthSpec> aWidths = new CommonsArrayList <> (nColumnCount);
    for (int i = 0; i < nColumnCount; ++i)
      aWidths.add (WidthSpec.star ());
    return new PLTable (aWidths);
  }
}
