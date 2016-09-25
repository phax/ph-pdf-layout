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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.hbox.AbstractPLHBox;
import com.helger.pdflayout.element.hbox.PLHBoxColumn;
import com.helger.pdflayout.element.hbox.PLHBoxSplittable;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.element.vbox.AbstractPLVBox;
import com.helger.pdflayout.element.vbox.PLVBoxRow;
import com.helger.pdflayout.spec.EValueUOMType;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * A special table with a repeating header
 *
 * @author Philip Helger
 */
public class PLTable extends AbstractPLVBox <PLTable> implements IPLSplittableObject <PLTable>
{
  private final ICommonsList <WidthSpec> m_aWidths;
  private int m_nHeaderRowCount = 0;

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
    m_aWidths = new CommonsArrayList<> (aWidths);
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLTable setBasicDataFrom (@Nonnull final PLTable aSource)
  {
    super.setBasicDataFrom (aSource);
    setHeaderRowCount (aSource.m_nHeaderRowCount);
    return this;
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
  public PLHBoxSplittable addTableRow (@Nullable final AbstractPLElement <?>... aElements)
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
  public PLHBoxSplittable addTableRow (@Nonnull final Collection <? extends IPLRenderableObject <?>> aElements)
  {
    ValueEnforcer.notNull (aElements, "Elements");
    if (aElements.size () > m_aWidths.size ())
      throw new IllegalArgumentException ("More elements in row (" +
                                          aElements.size () +
                                          ") than defined in the table (" +
                                          m_aWidths.size () +
                                          ")!");

    final PLHBoxSplittable aRowHBox = new PLHBoxSplittable ();
    int nWidthIndex = 0;
    for (IPLRenderableObject <?> aElement : aElements)
    {
      if (aElement == null)
      {
        // null elements end as a spacer
        aElement = new PLSpacerX ();
      }
      final WidthSpec aWidth = m_aWidths.get (nWidthIndex);
      aRowHBox.addColumn (aElement, aWidth);
      ++nWidthIndex;
    }
    super.addRow (aRowHBox);
    return aRowHBox;
  }

  @Nonnull
  public PLHBoxSplittable addTableRowExt (@Nonnull final PLTableCell... aCells)
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
  public PLHBoxSplittable addTableRowExt (@Nonnull final Iterable <? extends PLTableCell> aCells)
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

    final PLHBoxSplittable aHBox = new PLHBoxSplittable ();
    int nWidthIndex = 0;
    for (final PLTableCell aCell : aCells)
    {
      final int nCols = aCell.getColSpan ();
      if (nCols == 1)
      {
        aHBox.addAndReturnColumn (aCell.getElement (), m_aWidths.get (nWidthIndex));
      }
      else
      {
        final List <WidthSpec> aWidths = m_aWidths.subList (nWidthIndex, nWidthIndex + nCols);
        final EValueUOMType eWidthType = aWidths.get (0).getType ();
        WidthSpec aRealWidth;
        if (eWidthType == EValueUOMType.STAR)
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
          aRealWidth = new WidthSpec (eWidthType, fWidth);
        }
        aHBox.addAndReturnColumn (aCell.getElement (), aRealWidth);
      }
      nWidthIndex += nCols;
    }
    super.addRow (aHBox);
    return aHBox;
  }

  /**
   * @return The number of header rows. By default 0. Always &ge; 0.
   */
  @Nonnegative
  public int getHeaderRowCount ()
  {
    return m_nHeaderRowCount;
  }

  /**
   * Set the number of header rows in this table. Header rows get repeated on
   * every page upon rendering.
   *
   * @param nHeaderRowCount
   *        The number of header rows, to be repeated by page. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  public PLTable setHeaderRowCount (@Nonnegative final int nHeaderRowCount)
  {
    ValueEnforcer.isGE0 (nHeaderRowCount, "HeaderRowCount");

    m_nHeaderRowCount = nHeaderRowCount;
    return this;
  }

  /**
   * Get the cell at the specified row and column index
   *
   * @param nRowIndex
   *        row index
   * @param nColumnIndex
   *        column index
   * @return <code>null</code> if row and/or column index are out of bounds.
   * @since 3.0.4
   */
  @Nullable
  public IPLRenderableObject <?> getCellElement (@Nonnegative final int nRowIndex, @Nonnegative final int nColumnIndex)
  {
    final PLVBoxRow aRow = getRowAtIndex (nRowIndex);
    if (aRow != null)
    {
      final PLHBoxColumn aColumn = ((AbstractPLHBox <?>) aRow.getElement ()).getColumnAtIndex (nColumnIndex);
      if (aColumn != null)
        return aColumn.getElement ();
    }
    return null;
  }

  @Nonnull
  @ReturnsMutableCopy
  private static float [] _getAsArray (@Nonnull final List <Float> aList)
  {
    return TypeConverter.convertIfNecessary (aList, float [].class);
  }

  @Override
  @Nullable
  public PLSplitResult splitElements (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    final PLTable aTable1 = new PLTable (m_aWidths).setBasicDataFrom (this);
    final PLTable aTable2 = new PLTable (m_aWidths).setBasicDataFrom (this);

    final int nTotalRows = getRowCount ();

    final ICommonsList <SizeSpec> aTable1RowSize = new CommonsArrayList<> (nTotalRows);
    final ICommonsList <SizeSpec> aTable1ElementSize = new CommonsArrayList<> (nTotalRows);
    float fUsedTable1RowHeight = 0;

    // Copy all header rows
    for (int nRow = 0; nRow < m_nHeaderRowCount; ++nRow)
    {
      final IPLRenderableObject <?> aHeaderRowElement = getRowElementAtIndex (nRow);
      aTable1.addRow (aHeaderRowElement);
      aTable2.addRow (aHeaderRowElement);

      fUsedTable1RowHeight += m_aPreparedRowSize[nRow].getHeight ();
      aTable1RowSize.add (m_aPreparedRowSize[nRow]);
      aTable1ElementSize.add (m_aPreparedElementSize[nRow]);
    }

    // The height and width after header are identical
    final ICommonsList <SizeSpec> aTable2RowSize = new CommonsArrayList<> (aTable1RowSize);
    final ICommonsList <SizeSpec> aTable2ElementSize = new CommonsArrayList<> (aTable1ElementSize);
    float fUsedTable2RowHeight = fUsedTable1RowHeight;

    // Copy all content rows
    boolean bOnTable1 = true;

    for (int nRow = m_nHeaderRowCount; nRow < nTotalRows; ++nRow)
    {
      final IPLRenderableObject <?> aRowElement = getRowElementAtIndex (nRow);
      final float fRowHeight = m_aPreparedRowSize[nRow].getHeight ();

      if (bOnTable1)
      {
        if (fUsedTable1RowHeight + fRowHeight <= fAvailableHeight)
        {
          // Row fits in first table without a change
          aTable1.addRow (aRowElement);
          fUsedTable1RowHeight += fRowHeight;
          // Use data as is
          aTable1RowSize.add (m_aPreparedRowSize[nRow]);
          aTable1ElementSize.add (m_aPreparedElementSize[nRow]);
        }
        else
        {
          // Row does not fit - check if it can be splitted
          bOnTable1 = false;
          // try to split the row
          boolean bSplittedRow = false;
          if (aRowElement.isSplittable ())
          {
            final float fSplitWidth = m_aPreparedElementSize[nRow].getWidth ();
            final float fSplitHeight = fAvailableHeight - fUsedTable1RowHeight - aRowElement.getOutlineYSum ();
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Trying to split " +
                                        aRowElement.getDebugID () +
                                        " into pieces for split width " +
                                        fSplitWidth +
                                        " and height " +
                                        fSplitHeight);

            // Try to split the element contained in the row (without padding
            // and margin of the element)
            final PLSplitResult aSplitResult = aRowElement.getAsSplittable ().splitElements (fSplitWidth, fSplitHeight);

            if (aSplitResult != null)
            {
              final IPLRenderableObject <?> aTable1RowElement = aSplitResult.getFirstElement ().getElement ();
              aTable1.addRow (aTable1RowElement);
              fUsedTable1RowHeight += aSplitResult.getFirstElement ().getHeightFull ();
              aTable1RowSize.add (aSplitResult.getFirstElement ().getSizeFull ());
              aTable1ElementSize.add (aSplitResult.getFirstElement ().getSize ());

              final IPLRenderableObject <?> aTable2RowElement = aSplitResult.getSecondElement ().getElement ();
              aTable2.addRow (aTable2RowElement);
              fUsedTable2RowHeight += aSplitResult.getSecondElement ().getHeightFull ();
              aTable2RowSize.add (aSplitResult.getSecondElement ().getSizeFull ());
              aTable2ElementSize.add (aSplitResult.getSecondElement ().getSize ());

              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Split row element " +
                                          aRowElement.getDebugID () +
                                          " (Row " +
                                          nRow +
                                          ") into pieces: " +
                                          aTable1RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getFirstElement ().getWidth () +
                                          "+" +
                                          aTable1RowElement.getOutlineXSum () +
                                          " & " +
                                          aSplitResult.getFirstElement ().getHeight () +
                                          "+" +
                                          aTable1RowElement.getOutlineYSum () +
                                          ") and " +
                                          aTable2RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getSecondElement ().getWidth () +
                                          "+" +
                                          aTable2RowElement.getOutlineXSum () +
                                          " & " +
                                          aSplitResult.getSecondElement ().getHeight () +
                                          "+" +
                                          aTable2RowElement.getOutlineYSum () +
                                          ")");
              bSplittedRow = true;
            }
            else
            {
              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Failed to split row element " +
                                          aRowElement.getDebugID () +
                                          " (Row " +
                                          nRow +
                                          ") into pieces");
            }
          }

          if (!bSplittedRow)
          {
            // just add the full row to the second Table since the row does not
            // fit on first page
            aTable2.addRow (aRowElement);
            fUsedTable2RowHeight += fRowHeight;
            aTable2RowSize.add (m_aPreparedRowSize[nRow]);
            aTable2ElementSize.add (m_aPreparedElementSize[nRow]);
          }
        }
      }
      else
      {
        // We're already on Table 2 - add all elements, since Table2 may be
        // split again later!
        aTable2.addRow (aRowElement);
        fUsedTable2RowHeight += fRowHeight;
        aTable2RowSize.add (m_aPreparedRowSize[nRow]);
        aTable2ElementSize.add (m_aPreparedElementSize[nRow]);
      }
    }

    if (aTable1.getRowCount () == m_nHeaderRowCount)
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Splitting makes no sense, because only the header row would be in table 1");
      return null;
    }

    if (aTable2.getRowCount () == m_nHeaderRowCount)
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this,
                            "Splitting makes no sense, because only the header row would be in table 2 and this means the whole table 1 would match");
      return null;
    }

    // Excluding padding/margin
    aTable1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fUsedTable1RowHeight));
    aTable1.m_aPreparedRowSize = ArrayHelper.newArray (aTable1RowSize, SizeSpec.class);
    aTable1.m_aPreparedElementSize = ArrayHelper.newArray (aTable1ElementSize, SizeSpec.class);

    aTable2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fUsedTable2RowHeight));
    aTable2.m_aPreparedRowSize = ArrayHelper.newArray (aTable2RowSize, SizeSpec.class);
    aTable2.m_aPreparedElementSize = ArrayHelper.newArray (aTable2ElementSize, SizeSpec.class);

    return new PLSplitResult (new PLElementWithSize (aTable1, new SizeSpec (fAvailableWidth, fUsedTable1RowHeight)),
                              new PLElementWithSize (aTable2, new SizeSpec (fAvailableWidth, fUsedTable2RowHeight)));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Width", m_aWidths)
                            .append ("HeaderRowCount", m_nHeaderRowCount)
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
