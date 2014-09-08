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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.ContainerHelper;
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
  public static class PLTableCell
  {
    private final AbstractPLElement <?> m_aElement;
    private final int m_nColSpan;

    public PLTableCell (@Nonnull final AbstractPLElement <?> aElement, @Nonnegative final int nColSpan)
    {
      ValueEnforcer.notNull (aElement, "Element");
      ValueEnforcer.isGT0 (nColSpan, "ColSpan");
      m_aElement = aElement;
      m_nColSpan = nColSpan;
    }

    @Nonnull
    public AbstractPLElement <?> getElement ()
    {
      return m_aElement;
    }

    @Nonnegative
    public int getColSpan ()
    {
      return m_nColSpan;
    }
  }

  private final List <WidthSpec> m_aWidths;
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
    m_aWidths = ContainerHelper.newList (aWidths);
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
  public List <WidthSpec> getWidths ()
  {
    return ContainerHelper.newList (m_aWidths);
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
    return addTableRow (ContainerHelper.newList (aElements));
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

    final PLHBoxSplittable aHBox = new PLHBoxSplittable ();
    int nWidthIndex = 0;
    for (AbstractPLElement <?> aElement : aElements)
    {
      if (aElement == null)
      {
        // null elements end as a spacer
        aElement = new PLSpacerX ();
      }
      final WidthSpec aWidth = m_aWidths.get (nWidthIndex);
      aHBox.addColumn (aElement, aWidth);
      ++nWidthIndex;
    }
    super.addRow (aHBox);
    return aHBox;
  }

  @Nonnull
  public PLHBoxSplittable addTableRowExt (@Nonnull final PLTableCell... aCells)
  {
    return addTableRowExt (ContainerHelper.newList (aCells));
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
  public PLHBoxSplittable addTableRowExt (@Nonnull final Collection <? extends PLTableCell> aCells)
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
    final List <Float> aTable1RowWidth = new ArrayList <Float> (nTotalRows);
    final List <Float> aTable1RowHeight = new ArrayList <Float> (nTotalRows);

    // Copy all header rows
    float fTable1Width = 0;
    float fTable1WidthFull = 0;
    float fTable1Height = 0;
    float fTable1HeightFull = 0;
    for (int i = 0; i < m_nHeaderRowCount; ++i)
    {
      final AbstractPLElement <?> aHeaderRowElement = getRowElementAtIndex (i);
      aTable1.addRow (aHeaderRowElement);
      aTable2.addRow (aHeaderRowElement);

      final float fRowWidth = m_aPreparedRowElementWidth[i];
      final float fRowWidthFull = fRowWidth + aHeaderRowElement.getMarginPlusPaddingXSum ();
      final float fRowHeight = m_aPreparedRowElementHeight[i];
      final float fRowHeightFull = fRowHeight + aHeaderRowElement.getMarginPlusPaddingYSum ();

      fTable1Width = Math.max (fTable1Width, fRowWidth);
      fTable1WidthFull = Math.max (fTable1WidthFull, fRowWidthFull);
      fTable1Height += fRowHeight;
      fTable1HeightFull += fRowHeightFull;
      aTable1RowWidth.add (Float.valueOf (fRowWidth));
      aTable1RowHeight.add (Float.valueOf (fRowHeight));
    }

    // The height and width after header are identical
    float fTable2Width = fTable1Width;
    float fTable2Height = fTable1Height;
    float fTable2HeightFull = fTable1HeightFull;
    final List <Float> aTable2RowWidth = new ArrayList <Float> (aTable1RowWidth);
    final List <Float> aTable2RowHeight = new ArrayList <Float> (aTable1RowHeight);

    // Copy all content rows
    boolean bOnTable1 = true;

    for (int i = m_nHeaderRowCount; i < nTotalRows; ++i)
    {
      final AbstractPLElement <?> aRowElement = getRowElementAtIndex (i);
      final float fRowWidth = m_aPreparedRowElementWidth[i];
      final float fRowWidthFull = fRowWidth + aRowElement.getMarginPlusPaddingXSum ();
      final float fRowHeight = m_aPreparedRowElementHeight[i];
      final float fRowHeightFull = fRowHeight + aRowElement.getMarginPlusPaddingYSum ();

      if (bOnTable1)
      {
        if (fTable1HeightFull + fRowHeightFull <= fAvailableHeight)
        {
          // Row fits in first table without a change
          aTable1.addRow (aRowElement);
          fTable1Width = Math.max (fTable1Width, fRowWidth);
          fTable1WidthFull = Math.max (fTable1WidthFull, fRowWidthFull);
          fTable1Height += fRowHeight;
          fTable1HeightFull += fRowHeightFull;
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
            final float fWidth = Math.max (fTable1Width, fRowWidth);
            final float fWidthFull = Math.max (fTable1WidthFull, fRowWidthFull);
            final float fRemainingHeight = fAvailableHeight - fTable1HeightFull;

            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this, "Trying to split " +
                                        aRowElement.getDebugID () +
                                        " into pieces for remaining width " +
                                        fWidth +
                                        " and height " +
                                        fRemainingHeight);

            // Try to split the element contained in the row (without padding
            // and margin of the element)
            final PLSplitResult aSplitResult = aRowElement.getAsSplittable ()
                                                          .splitElements (fWidth -
                                                                              aRowElement.getMarginPlusPaddingXSum (),
                                                                          fRemainingHeight -
                                                                              aRowElement.getMarginPlusPaddingYSum ());

            if (aSplitResult != null)
            {
              final AbstractPLElement <?> aTable1RowElement = aSplitResult.getFirstElement ().getElement ();
              aTable1.addRow (aTable1RowElement);
              fTable1Width = fWidth;
              fTable1WidthFull = fWidthFull;
              final float fTable1RowHeight = aSplitResult.getFirstElement ().getHeight ();
              fTable1Height += fTable1RowHeight;
              fTable1HeightFull += fTable1RowHeight + aTable1RowElement.getMarginPlusPaddingYSum ();
              aTable1RowWidth.add (Float.valueOf (fWidth));
              aTable1RowHeight.add (Float.valueOf (fTable1RowHeight));

              final AbstractPLElement <?> aTable2RowElement = aSplitResult.getSecondElement ().getElement ();
              aTable2.addRow (aTable2RowElement);
              fTable2Width = fWidth;
              final float fTable2RowHeight = aSplitResult.getSecondElement ().getHeight ();
              final float fTable2RowHeightFull = fTable2RowHeight + aTable2RowElement.getMarginPlusPaddingYSum ();
              fTable2Height += fTable2RowHeight;
              fTable2HeightFull += fTable2RowHeightFull;
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
                                        ") for remaining height of " +
                                        fRemainingHeight);
              bSplittedRow = true;
            }
            else
            {
              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this, "Failed to split " +
                                          aRowElement.getDebugID () +
                                          " into pieces for remaining height " +
                                          fRemainingHeight);
            }
          }

          if (!bSplittedRow)
          {
            // just add the full row to the second table
            aTable2.addRow (aRowElement);
            fTable2Width = Math.max (fTable2Width, fRowWidth);
            fTable2Height += fRowHeight;
            fTable2HeightFull += fRowHeightFull;
            aTable2RowWidth.add (Float.valueOf (fRowWidth));
            aTable2RowHeight.add (Float.valueOf (fRowHeight));
          }
        }
      }
      else
      {
        // We're already on table 2
        aTable2.addRow (aRowElement);
        fTable2Width = Math.max (fTable2Width, fRowWidth);
        fTable2Height += fRowHeight;
        fTable2HeightFull += fRowHeightFull;
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
    aTable1.markAsPrepared (new SizeSpec (fElementWidth, fTable1HeightFull));
    aTable1.m_aPreparedRowElementWidth = _getAsArray (aTable1RowWidth);
    aTable1.m_aPreparedRowElementHeight = _getAsArray (aTable1RowHeight);

    aTable2.markAsPrepared (new SizeSpec (fElementWidth, fTable2HeightFull));
    aTable2.m_aPreparedRowElementWidth = _getAsArray (aTable2RowWidth);
    aTable2.m_aPreparedRowElementHeight = _getAsArray (aTable2RowHeight);

    return new PLSplitResult (new PLElementWithSize (aTable1, new SizeSpec (fElementWidth, fTable1HeightFull)),
                              new PLElementWithSize (aTable2, new SizeSpec (fElementWidth, fTable2HeightFull)));
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
    if (ArrayHelper.isEmpty (aPercentages))
      throw new IllegalArgumentException ("Percentages");
    final List <WidthSpec> aWidths = new ArrayList <WidthSpec> (aPercentages.length);
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
    if (nColumnCount < 1)
      throw new IllegalArgumentException ("Columns must be >= 1: " + nColumnCount);
    final List <WidthSpec> aWidths = new ArrayList <WidthSpec> (nColumnCount);
    for (int i = 0; i < nColumnCount; ++i)
      aWidths.add (WidthSpec.star ());
    return new PLTable (aWidths);
  }
}
