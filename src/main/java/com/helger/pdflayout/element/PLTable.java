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

import java.util.Collection;
import java.util.List;

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
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;
import com.helger.pdflayout.spec.WidthSpec.EWidthType;

/**
 * A special table with a repeating header
 *
 * @author Philip Helger
 */
public class PLTable extends AbstractPLVBox <PLTable> implements IPLSplittableElement
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
    EWidthType eWidthType = null;
    for (final WidthSpec aWidth : aWidths)
      if (eWidthType == null)
        eWidthType = aWidth.getType ();
      else
        if (aWidth.getType () != eWidthType)
          throw new IllegalArgumentException ("All widths must be of the same type! Found " +
                                              eWidthType +
                                              " and " +
                                              aWidth.getType ());
    m_aWidths = new CommonsArrayList <> (aWidths);
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
  public ICommonsList <WidthSpec> getWidths ()
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
    return addTableRow (new CommonsArrayList <> (aElements));
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
  public PLHBoxSplittable addTableRow (@Nonnull final Collection <? extends AbstractPLElement <?>> aElements)
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
    for (AbstractPLElement <?> aElement : aElements)
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
    return addTableRowExt (new CommonsArrayList <> (aCells));
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
        aHBox.addColumn (aCell.getElement (), m_aWidths.get (nWidthIndex));
      }
      else
      {
        final List <WidthSpec> aWidths = m_aWidths.subList (nWidthIndex, nWidthIndex + nCols);
        final EWidthType eWidthType = aWidths.get (0).getType ();
        WidthSpec aRealWidth;
        if (eWidthType == EWidthType.STAR)
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
        aHBox.addColumn (aCell.getElement (), aRealWidth);
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
   * Set the number of header rows in this table.
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

  @Nonnull
  @ReturnsMutableCopy
  private static float [] _getAsArray (@Nonnull final List <Float> aList)
  {
    return TypeConverter.convertIfNecessary (aList, float [].class);
  }

  @SuppressWarnings ("unused")
  @Override
  @Nullable
  public PLSplitResult splitElements (final float fElementWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    final PLTable aTable1 = new PLTable (m_aWidths).setBasicDataFrom (this);
    final PLTable aTable2 = new PLTable (m_aWidths).setBasicDataFrom (this);

    final int nTotalRows = getRowCount ();
    final ICommonsList <Float> aTable1RowWidth = new CommonsArrayList <> (nTotalRows);
    final ICommonsList <Float> aTable1RowHeight = new CommonsArrayList <> (nTotalRows);

    // Copy all header rows
    float fUsedTable1Width = 0;
    float fUsedTable1WidthFull = 0;
    float fUsedTable1Height = 0;
    float fUsedTable1HeightFull = 0;
    for (int nRow = 0; nRow < m_nHeaderRowCount; ++nRow)
    {
      final AbstractPLElement <?> aHeaderRowElement = getRowElementAtIndex (nRow);
      aTable1.addRow (aHeaderRowElement);
      aTable2.addRow (aHeaderRowElement);

      final float fRowWidth = m_aPreparedRowElementWidth[nRow];
      final float fRowWidthFull = fRowWidth + aHeaderRowElement.getFullXSum ();
      final float fRowHeight = m_aPreparedRowElementHeight[nRow];
      final float fRowHeightFull = fRowHeight + aHeaderRowElement.getFullYSum ();

      fUsedTable1Width = Math.max (fUsedTable1Width, fRowWidth);
      fUsedTable1WidthFull = Math.max (fUsedTable1WidthFull, fRowWidthFull);
      fUsedTable1Height += fRowHeight;
      fUsedTable1HeightFull += fRowHeightFull;
      aTable1RowWidth.add (Float.valueOf (fRowWidth));
      aTable1RowHeight.add (Float.valueOf (fRowHeight));
    }

    // The height and width after header are identical
    float fUsedTable2Width = fUsedTable1Width;
    float fUsedTable2Height = fUsedTable1Height;
    float fUsedTable2HeightFull = fUsedTable1HeightFull;
    final ICommonsList <Float> aTable2RowWidth = new CommonsArrayList <> (aTable1RowWidth);
    final ICommonsList <Float> aTable2RowHeight = new CommonsArrayList <> (aTable1RowHeight);

    // Copy all content rows
    boolean bOnTable1 = true;

    for (int nRow = m_nHeaderRowCount; nRow < nTotalRows; ++nRow)
    {
      final AbstractPLElement <?> aRowElement = getRowElementAtIndex (nRow);
      final float fRowWidth = m_aPreparedRowElementWidth[nRow];
      final float fRowWidthFull = fRowWidth + aRowElement.getFullXSum ();
      final float fRowHeight = m_aPreparedRowElementHeight[nRow];
      final float fRowHeightFull = fRowHeight + aRowElement.getFullYSum ();

      if (bOnTable1)
      {
        if (fUsedTable1HeightFull + fRowHeightFull <= fAvailableHeight)
        {
          // Row fits in first table without a change
          aTable1.addRow (aRowElement);
          fUsedTable1Width = Math.max (fUsedTable1Width, fRowWidth);
          fUsedTable1WidthFull = Math.max (fUsedTable1WidthFull, fRowWidthFull);
          fUsedTable1Height += fRowHeight;
          fUsedTable1HeightFull += fRowHeightFull;
          aTable1RowWidth.add (Float.valueOf (fRowWidth));
          aTable1RowHeight.add (Float.valueOf (fRowHeight));
        }
        else
        {
          // Row does not fit - check if it can be splitted
          bOnTable1 = false;
          // try to split the row
          boolean bSplittedRow = false;
          if (aRowElement.isSplittable ())
          {
            // don't override fTable1Width
            final float fWidth = Math.max (fUsedTable1Width, fRowWidth);
            final float fWidthFull = Math.max (fUsedTable1WidthFull, fRowWidthFull);

            final float fAvailableSplitWidth = fWidth;
            final float fAvailableSplitHeight = fAvailableHeight -
                                                fUsedTable1HeightFull -
                                                aRowElement.getFullYSum ();

            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Trying to split " +
                                        aRowElement.getDebugID () +
                                        " into pieces for split width " +
                                        fAvailableSplitWidth +
                                        " and height " +
                                        fAvailableSplitHeight);

            // Try to split the element contained in the row (without padding
            // and margin of the element)
            final PLSplitResult aSplitResult = aRowElement.getAsSplittable ().splitElements (fAvailableSplitWidth,
                                                                                             fAvailableSplitHeight);

            if (aSplitResult != null)
            {
              final AbstractPLElement <?> aTable1RowElement = aSplitResult.getFirstElement ().getElement ();
              aTable1.addRow (aTable1RowElement);
              fUsedTable1Width = fWidth;
              fUsedTable1WidthFull = fWidthFull;
              final float fTable1RowHeight = aSplitResult.getFirstElement ().getHeight ();
              fUsedTable1Height += fTable1RowHeight;
              fUsedTable1HeightFull += fTable1RowHeight + aTable1RowElement.getFullYSum ();
              aTable1RowWidth.add (Float.valueOf (fWidth));
              aTable1RowHeight.add (Float.valueOf (fTable1RowHeight));

              final AbstractPLElement <?> aTable2RowElement = aSplitResult.getSecondElement ().getElement ();
              aTable2.addRow (aTable2RowElement);
              fUsedTable2Width = fWidth;
              final float fTable2RowHeight = aSplitResult.getSecondElement ().getHeight ();
              final float fTable2RowHeightFull = fTable2RowHeight + aTable2RowElement.getFullYSum ();
              fUsedTable2Height += fTable2RowHeight;
              fUsedTable2HeightFull += fTable2RowHeightFull;
              aTable2RowWidth.add (Float.valueOf (fWidth));
              aTable2RowHeight.add (Float.valueOf (fTable2RowHeight));

              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Split " +
                                          aRowElement.getDebugID () +
                                          " into pieces: " +
                                          aTable1RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getFirstElement ().getHeight () +
                                          ") and " +
                                          aTable2RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getSecondElement ().getHeight () +
                                          ")");
              bSplittedRow = true;
            }
            else
            {
              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this, "Failed to split " + aRowElement.getDebugID () + " into pieces");
            }
          }

          if (!bSplittedRow)
          {
            // just add the full row to the second table
            aTable2.addRow (aRowElement);
            fUsedTable2Width = Math.max (fUsedTable2Width, fRowWidth);
            fUsedTable2Height += fRowHeight;
            fUsedTable2HeightFull += fRowHeightFull;
            aTable2RowWidth.add (Float.valueOf (fRowWidth));
            aTable2RowHeight.add (Float.valueOf (fRowHeight));
          }
        }
      }
      else
      {
        // We're already on table 2
        aTable2.addRow (aRowElement);
        fUsedTable2Width = Math.max (fUsedTable2Width, fRowWidth);
        fUsedTable2Height += fRowHeight;
        fUsedTable2HeightFull += fRowHeightFull;
        aTable2RowWidth.add (Float.valueOf (fRowWidth));
        aTable2RowHeight.add (Float.valueOf (fRowHeight));
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
    aTable1.internalMarkAsPrepared (new SizeSpec (fElementWidth, fUsedTable1HeightFull));
    aTable1.m_aPreparedRowElementWidth = _getAsArray (aTable1RowWidth);
    aTable1.m_aPreparedRowElementHeight = _getAsArray (aTable1RowHeight);

    aTable2.internalMarkAsPrepared (new SizeSpec (fElementWidth, fUsedTable2HeightFull));
    aTable2.m_aPreparedRowElementWidth = _getAsArray (aTable2RowWidth);
    aTable2.m_aPreparedRowElementHeight = _getAsArray (aTable2RowHeight);

    return new PLSplitResult (new PLElementWithSize (aTable1, new SizeSpec (fElementWidth, fUsedTable1HeightFull)),
                              new PLElementWithSize (aTable2, new SizeSpec (fElementWidth, fUsedTable2HeightFull)));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("width", m_aWidths)
                            .append ("headerRowCount", m_nHeaderRowCount)
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
