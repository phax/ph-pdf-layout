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
package com.helger.pdflayout4.element.table;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.base.AbstractPLRenderableObject;
import com.helger.pdflayout4.base.IPLHasMargin;
import com.helger.pdflayout4.base.IPLSplittableObject;
import com.helger.pdflayout4.base.IPLVisitor;
import com.helger.pdflayout4.base.PLElementWithSize;
import com.helger.pdflayout4.base.PLSplitResult;
import com.helger.pdflayout4.element.vbox.PLVBox;
import com.helger.pdflayout4.element.vbox.PLVBoxRow;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.EValueUOMType;
import com.helger.pdflayout4.spec.HeightSpec;
import com.helger.pdflayout4.spec.MarginSpec;
import com.helger.pdflayout4.spec.SizeSpec;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * A special table with a repeating header
 *
 * @author Philip Helger
 */
public class PLTable extends AbstractPLRenderableObject <PLTable>
                     implements IPLSplittableObject <PLTable>, IPLHasMargin <PLTable>
{
  // All column widths
  private final ICommonsList <WidthSpec> m_aWidths;
  // With type to use
  private final EValueUOMType m_eWidthType;
  // VBox with all the PLTableRow elements
  private PLVBox m_aRows = new PLVBox ().setVertSplittable (true).setFullWidth (true);
  // Margin around the table
  private MarginSpec m_aMargin = DEFAULT_MARGIN;

  /**
   * @param aWidths
   *        Must all be of the same type!
   */
  public PLTable (@Nonnull @Nonempty final Iterable <? extends WidthSpec> aWidths)
  {
    ValueEnforcer.notEmptyNoNullValue (aWidths, "Widths");

    // Check that all width are of the same type
    EValueUOMType eWidthType = null;
    for (final WidthSpec aWidth : aWidths)
      if (eWidthType == null)
        eWidthType = aWidth.getType ();
      else
        if (aWidth.getType () != eWidthType)
          throw new IllegalArgumentException ("All widths must be of the same type! Found " +
                                              eWidthType +
                                              " and " +
                                              aWidth.getType ());
    if (eWidthType == EValueUOMType.AUTO)
      throw new IllegalArgumentException ("Width type auto is not allowed for tables!");
    m_aWidths = new CommonsArrayList <> (aWidths);
    m_eWidthType = eWidthType;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onAfterSetID ()
  {
    m_aRows.setID (getID () + "-vbox");
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLTable setBasicDataFrom (@Nonnull final PLTable aSource)
  {
    super.setBasicDataFrom (aSource);
    m_aRows.setBasicDataFrom (aSource.m_aRows);
    setMargin (aSource.m_aMargin);
    return this;
  }

  @Nonnull
  public final PLTable setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return this;
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  /**
   * @return A copy of the list with all widths as specified in the constructor.
   *         Neither <code>null</code> nor empty.
   */
  @Nonnull
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

  @Nonnull
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

  @Nonnull
  public PLTableRow addAndReturnRow (@Nonnull final PLTableCell... aCells)
  {
    return addAndReturnRow (new CommonsArrayList <> (aCells), HeightSpec.auto ());
  }

  /**
   * Add a new table row with auto height. All contained elements are added with
   * the specified width in the constructor. <code>null</code> elements are
   * represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return the added table row and never <code>null</code>.
   */
  @Nonnull
  public PLTableRow addAndReturnRow (@Nonnull final Iterable <? extends PLTableCell> aCells)
  {
    return addAndReturnRow (aCells, HeightSpec.auto ());
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @param aHeight
   *        Row height to be used. May not be <code>null</code>.
   * @return the added table row and never <code>null</code>.
   */
  @Nonnull
  public PLTableRow addAndReturnRow (@Nonnull final Iterable <? extends PLTableCell> aCells,
                                     @Nonnull final HeightSpec aHeight)
  {
    ValueEnforcer.notNull (aCells, "Cells");

    // Small consistency check
    int nUsedCols = 0;
    for (final PLTableCell aCell : aCells)
      nUsedCols += aCell.getColSpan ();
    if (nUsedCols > m_aWidths.size ())
      throw new IllegalArgumentException ("More cells in row (" +
                                          nUsedCols +
                                          ") than defined in the table (" +
                                          m_aWidths.size () +
                                          ")!");

    final PLTableRow aRow = new PLTableRow ();
    int nWidthIndex = 0;
    for (final PLTableCell aCell : aCells)
    {
      final int nCols = aCell.getColSpan ();
      if (nCols == 1)
      {
        aRow.addCell (aCell, m_aWidths.get (nWidthIndex));
      }
      else
      {
        final List <WidthSpec> aWidths = m_aWidths.subList (nWidthIndex, nWidthIndex + nCols);
        WidthSpec aRealWidth;
        if (m_eWidthType == EValueUOMType.STAR)
        {
          // aggregate
          aRealWidth = WidthSpec.perc (nCols * 100f / m_aWidths.size ());
        }
        else
        {
          // aggregate values
          float fWidth = 0;
          for (final WidthSpec aWidth : aWidths)
            fWidth += aWidth.getValue ();
          aRealWidth = new WidthSpec (m_eWidthType, fWidth);
        }
        aRow.addCell (aCell, aRealWidth);
      }
      nWidthIndex += nCols;
    }
    addRow (aRow, aHeight);
    return aRow;
  }

  @Nonnull
  public PLTable addRow (@Nonnull final PLTableRow aRow, @Nonnull final HeightSpec aHeight)
  {
    ValueEnforcer.notNull (aRow, "Row");
    m_aRows.addRow (aRow, aHeight);
    return this;
  }

  @Nonnull
  @Deprecated
  public PLTable addRow ()
  {
    return this;
  }

  @Nonnull
  public PLTable addRow (@Nonnull final PLTableCell... aCells)
  {
    return addRow (new CommonsArrayList <> (aCells), HeightSpec.auto ());
  }

  /**
   * Add a new table row with auto height. All contained elements are added with
   * the specified width in the constructor. <code>null</code> elements are
   * represented as empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLTable addRow (@Nonnull final Iterable <? extends PLTableCell> aCells)
  {
    return addRow (aCells, HeightSpec.auto ());
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @param aHeight
   *        Row height to be used.
   * @return this
   */
  @Nonnull
  public PLTable addRow (@Nonnull final Iterable <? extends PLTableCell> aCells, @Nonnull final HeightSpec aHeight)
  {
    addAndReturnRow (aCells, aHeight);
    return this;
  }

  public void forEachRow (@Nonnull final Consumer <? super PLTableRow> aConsumer)
  {
    m_aRows.forEachRow (x -> aConsumer.accept ((PLTableRow) x.getElement ()));
  }

  public void forEachRow (@Nonnull final ObjIntConsumer <? super PLTableRow> aConsumer)
  {
    m_aRows.forEachRow ( (x, idx) -> aConsumer.accept ((PLTableRow) x.getElement (), idx));
  }

  public void forEachRow (final int nStartRowIncl,
                          final int nEndRowIncl,
                          @Nonnull final Consumer <? super PLTableRow> aConsumer)
  {
    forEachRow ( (x, idx) -> {
      if (idx >= nStartRowIncl && idx <= nEndRowIncl)
        aConsumer.accept (x);
    });
  }

  public void forEachRow (final int nStartRowIncl,
                          final int nEndRowIncl,
                          @Nonnull final ObjIntConsumer <? super PLTableRow> aConsumer)
  {
    forEachRow ( (x, idx) -> {
      if (idx >= nStartRowIncl && idx <= nEndRowIncl)
        aConsumer.accept (x, idx);
    });
  }

  @Nonnegative
  public int getRowCount ()
  {
    return m_aRows.getRowCount ();
  }

  public void forEachCell (@Nonnull final Consumer <? super PLTableCell> aConsumer)
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
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    super.visit (aVisitor);
    m_aRows.visit (aVisitor);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
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

  public boolean isVertSplittable ()
  {
    return m_aRows.isVertSplittable ();
  }

  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    final float fSplitHeight = fAvailableHeight;
    if (PLDebug.isDebugSplit ())
      PLDebug.debugSplit (this,
                          "Trying to split " +
                                m_aRows.getDebugID () +
                                " into pieces for available width " +
                                fAvailableWidth +
                                " and height " +
                                fSplitHeight);

    final PLSplitResult ret = m_aRows.splitElementVert (fAvailableWidth, fSplitHeight);
    if (ret == null)
      return ret;

    final PLTable aTable1 = new PLTable (m_aWidths);
    aTable1.setID (getID () + "-1");
    aTable1.setBasicDataFrom (this);
    aTable1.internalMarkAsPrepared (ret.getFirstElement ().getSize ());
    aTable1.m_aRows = (PLVBox) ret.getFirstElement ().getElement ();

    final PLTable aTable2 = new PLTable (m_aWidths);
    aTable2.setID (getID () + "-2");
    aTable2.setBasicDataFrom (this);
    aTable2.internalMarkAsPrepared (ret.getSecondElement ().getSize ());
    aTable2.m_aRows = (PLVBox) ret.getSecondElement ().getElement ();

    return new PLSplitResult (new PLElementWithSize (aTable1, ret.getFirstElement ().getSize ()),
                              new PLElementWithSize (aTable2, ret.getSecondElement ().getSize ()));
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
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
                            .append ("WidthType", m_eWidthType)
                            .append ("Margin", m_aMargin)
                            .toString ();
  }

  /**
   * Create a new table with the specified percentages.
   *
   * @param aPercentages
   *        The array to use. The sum of all percentages should be &le; 100. May
   *        neither be <code>null</code> nor empty.
   * @return The created {@link PLTable} and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static PLTable createWithPercentage (@Nonnull @Nonempty final float... aPercentages)
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
  @Nonnull
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
