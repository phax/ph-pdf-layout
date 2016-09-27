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
package com.helger.pdflayout.element.table;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EValueUOMType;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * A special table with a repeating header
 *
 * @author Philip Helger
 */
public class PLTable extends AbstractPLRenderableObject <PLTable> implements IPLSplittableObject <PLTable>
{
  public static final IPLTableGridType DEFAULT_GRID_TYPE = EPLTableGridType.NONE;
  public static final BorderStyleSpec DEFAULT_GRID_BORDER_STYLE = new BorderStyleSpec (Color.BLACK,
                                                                                       LineDashPatternSpec.SOLID,
                                                                                       1f);

  private final PLVBox m_aVBox = new PLVBox ().setVertSplittable (true);
  private final ICommonsList <WidthSpec> m_aWidths;
  private final EValueUOMType m_eWidthType;
  private IPLTableGridType m_aGridType = DEFAULT_GRID_TYPE;
  private BorderStyleSpec m_aGridBSS = DEFAULT_GRID_BORDER_STYLE;

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
    m_aWidths = new CommonsArrayList<> (aWidths);
    m_eWidthType = eWidthType;
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

  public void setHeaderRowCount (final int nHeaderRowCount)
  {
    m_aVBox.setHeaderRowCount (nHeaderRowCount);
  }

  public int getHeaderRowCount ()
  {
    return m_aVBox.getHeaderRowCount ();
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aElements
   *        The elements to add. May be <code>null</code>.
   * @return The added row and never <code>null</code>.
   */
  @Nonnull
  public PLTableRow addAndReturnTableRow (@Nullable final AbstractPLElement <?>... aElements)
  {
    return addAndReturnTableRow (new CommonsArrayList<> (aElements));
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aElements
   *        The elements to add. May not be <code>null</code>.
   * @return the added row and never <code>null</code>.
   */
  @Nonnull
  public PLTableRow addAndReturnTableRow (@Nonnull final Collection <? extends IPLRenderableObject <?>> aElements)
  {
    ValueEnforcer.notNull (aElements, "Elements");
    if (aElements.size () > m_aWidths.size ())
      throw new IllegalArgumentException ("More elements in row (" +
                                          aElements.size () +
                                          ") than defined in the table (" +
                                          m_aWidths.size () +
                                          ")!");

    final PLTableRow aRow = new PLTableRow ();
    int nColumnIndex = 0;
    for (final IPLRenderableObject <?> aElement : aElements)
    {
      final WidthSpec aWidth = m_aWidths.get (nColumnIndex);
      final IPLRenderableObject <?> aRealElement = aElement != null ? aElement : new PLSpacerX ();
      aRow.addCell (new PLTableCell (aRealElement, PLTableCell.DEFAULT_COL_SPAN), aWidth);
      ++nColumnIndex;
    }
    m_aVBox.addRow (aRow);
    return aRow;
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aElements
   *        The elements to add. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLTable addTableRow (@Nullable final AbstractPLElement <?>... aElements)
  {
    return addTableRow (new CommonsArrayList<> (aElements));
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aElements
   *        The elements to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLTable addTableRow (@Nonnull final Collection <? extends IPLRenderableObject <?>> aElements)
  {
    addAndReturnTableRow (aElements);
    return this;
  }

  @Nonnull
  public PLTableRow addAndReturnTableRowExt (@Nonnull final PLTableCell... aCells)
  {
    return addAndReturnTableRowExt (new CommonsArrayList<> (aCells));
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return the added table row and never <code>null</code>.
   */
  @Nonnull
  public PLTableRow addAndReturnTableRowExt (@Nonnull final Iterable <? extends PLTableCell> aCells)
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
    m_aVBox.addRow (aRow);
    return aRow;
  }

  @Nonnull
  public PLTable addTableRowExt (@Nonnull final PLTableCell... aCells)
  {
    return addTableRowExt (new CommonsArrayList<> (aCells));
  }

  /**
   * Add a new table row. All contained elements are added with the specified
   * width in the constructor. <code>null</code> elements are represented as
   * empty cells.
   *
   * @param aCells
   *        The cells to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLTable addTableRowExt (@Nonnull final Iterable <? extends PLTableCell> aCells)
  {
    addAndReturnTableRowExt (aCells);
    return this;
  }

  public void forEachRow (@Nonnull final Consumer <? super PLTableRow> aConsumer)
  {
    m_aVBox.forEachRow (x -> aConsumer.accept ((PLTableRow) x.getElement ()));
  }

  public void forEachRow (@Nonnull final ObjIntConsumer <? super PLTableRow> aConsumer)
  {
    m_aVBox.forEachRow ( (x, idx) -> aConsumer.accept ((PLTableRow) x.getElement (), idx));
  }

  @Nonnegative
  public int getRowCount ()
  {
    return m_aVBox.getRowCount ();
  }

  @Nonnull
  public PLTable setGridType (@Nonnull final IPLTableGridType aGridType)
  {
    m_aGridType = ValueEnforcer.notNull (aGridType, "GridType");
    return this;
  }

  @Nonnull
  public IPLTableGridType getGridType ()
  {
    return m_aGridType;
  }

  @Nonnull
  public PLTable setGridBorderStyle (@Nonnull final BorderStyleSpec aBSS)
  {
    m_aGridBSS = ValueEnforcer.notNull (aBSS, "GridBorderStyle");
    return this;
  }

  @Nonnull
  public BorderStyleSpec getGridBorderStyle ()
  {
    return m_aGridBSS;
  }

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    super.visit (aVisitor);
    m_aVBox.visit (aVisitor);
  }

  @Override
  protected SizeSpec onPrepare (final PreparationContext aCtx) throws IOException
  {
    m_aGridType.applyGridToTable (this, m_aGridBSS);
    return m_aVBox.prepare (aCtx);
  }

  public boolean isVertSplittable ()
  {
    return m_aVBox.isVertSplittable ();
  }

  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    return m_aVBox.splitElementVert (fAvailableWidth, fAvailableHeight);
  }

  @Override
  protected void onRender (final PageRenderContext aCtx) throws IOException
  {
    m_aVBox.render (aCtx);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Width", m_aWidths).toString ();
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

    final ICommonsList <WidthSpec> aWidths = new CommonsArrayList<> (aPercentages.length);
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

    final ICommonsList <WidthSpec> aWidths = new CommonsArrayList<> (nColumnCount);
    for (int i = 0; i < nColumnCount; ++i)
      aWidths.add (WidthSpec.star ());
    return new PLTable (aWidths);
  }
}
