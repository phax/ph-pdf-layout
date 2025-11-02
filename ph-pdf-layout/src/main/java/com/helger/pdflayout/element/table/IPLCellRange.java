/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.numeric.MathHelper;

/**
 * Read-only version of the cell range.
 *
 * @author Philip Helger
 */
public interface IPLCellRange
{
  /**
   * @return row number for the upper left hand corner
   */
  int getFirstRow ();

  /**
   * @return row number for the lower right hand corner
   */
  int getLastRow ();

  /**
   * @return column number for the upper left hand corner
   */
  int getFirstColumn ();

  /**
   * @return column number for the lower right hand corner
   */
  int getLastColumn ();

  /**
   * Determines if the given coordinates lie within the bounds of this range.
   *
   * @param nRowIndex
   *        The row, 0-based.
   * @param nColumnIndex
   *        The column, 0-based.
   * @return <code>true</code> if the coordinates lie within the bounds,
   *         <code>false</code> otherwise.
   * @see #intersects(IPLCellRange) for checking if two ranges overlap
   */
  default boolean isInRange (final int nRowIndex, final int nColumnIndex)
  {
    return containsRow (nRowIndex) && containsColumn (nColumnIndex);
  }

  /**
   * Check if the row is in the specified cell range
   *
   * @param nRowIndex
   *        the row to check
   * @return <code>true</code> if the range contains the row at the passed index
   */
  default boolean containsRow (final int nRowIndex)
  {
    return getFirstRow () <= nRowIndex && nRowIndex <= getLastRow ();
  }

  /**
   * Check if the column is in the specified cell range
   *
   * @param nColumnIndex
   *        the column to check
   * @return <code>true</code> if the range contains the column at the passed
   *         index
   */
  default boolean containsColumn (final int nColumnIndex)
  {
    return getFirstColumn () <= nColumnIndex && nColumnIndex <= getLastColumn ();
  }

  /**
   * Determines whether or not this {@link IPLCellRange} and the specified
   * {@link IPLCellRange} intersect.
   *
   * @param aOther
   *        a candidate cell range address to check for intersection with this
   *        range. May not be <code>null</code>.
   * @return <code>true</code> if this range and other range have at least 1
   *         cell in common
   * @see #isInRange(int, int) for checking if a single cell intersects
   */
  default boolean intersects (@NonNull final IPLCellRange aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    return getFirstRow () <= aOther.getLastRow () &&
           getFirstColumn () <= aOther.getLastColumn () &&
           aOther.getFirstRow () <= getLastRow () &&
           aOther.getFirstColumn () <= getLastColumn ();
  }

  /**
   * @return The number of effected rows. Always &ge; 0.
   */
  @Nonnegative
  default int getRowCount ()
  {
    return Math.max (getLastRow () - getFirstRow () + 1, 0);
  }

  /**
   * @return The number of effected columns. Always &ge; 0.
   */
  @Nonnegative
  default int getColumnCount ()
  {
    return Math.max (getLastColumn () - getFirstColumn () + 1, 0);
  }

  /**
   * @return the size of the range (number of cells in the area).
   */
  default long getNumberOfCells ()
  {
    return MathHelper.abs ((long) getRowCount () * getColumnCount ());
  }

  default int getMinRow ()
  {
    return Math.min (getFirstRow (), getLastRow ());
  }

  default int getMaxRow ()
  {
    return Math.max (getFirstRow (), getLastRow ());
  }

  default int getMinColumn ()
  {
    return Math.min (getFirstColumn (), getLastColumn ());
  }

  default int getMaxColumn ()
  {
    return Math.max (getFirstColumn (), getLastColumn ());
  }
}
