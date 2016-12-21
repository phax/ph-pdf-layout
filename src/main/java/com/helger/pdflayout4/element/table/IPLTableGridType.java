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

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.id.IHasID;
import com.helger.pdflayout4.spec.BorderStyleSpec;

/**
 * Custom {@link PLTable} grid specification.
 *
 * @author Philip Helger
 */
public interface IPLTableGridType extends IHasID <String>, Serializable
{
  /**
   * Apply this grid to the whole provided table.
   *
   * @param aTable
   *        Table to modify. May not be <code>null</code>.
   * @param aBSS
   *        Border style specification to be used. May not be <code>null</code>.
   */
  default void applyGridToTable (@Nonnull final PLTable aTable, @Nonnull final BorderStyleSpec aBSS)
  {
    ValueEnforcer.notNull (aTable, "Table");
    applyGridToTable (aTable, 0, aTable.getRowCount () - 1, 0, aTable.getColumnCount () - 1, aBSS);
  }

  /**
   * Apply this grid to the passed cell range of the provided table.
   *
   * @param aTable
   *        Table to modify. May not be <code>null</code>.
   * @param aCellRange
   *        The cell range to which it should be applied. May not be
   *        <code>null</code>.
   * @param aBSS
   *        Border style specification to be used. May not be <code>null</code>.
   */
  default void applyGridToTable (@Nonnull final PLTable aTable,
                                 @Nonnull final IPLCellRange aCellRange,
                                 @Nonnull final BorderStyleSpec aBSS)
  {
    ValueEnforcer.notNull (aTable, "Table");
    ValueEnforcer.notNull (aCellRange, "CellRange");

    applyGridToTable (aTable,
                      aCellRange.getFirstRow (),
                      aCellRange.getLastRow (),
                      aCellRange.getFirstColumn (),
                      aCellRange.getLastColumn (),
                      aBSS);
  }

  /**
   * Apply this grid to the passed elements of the provided table.
   *
   * @param aTable
   *        Table to modify. May not be <code>null</code>.
   * @param nStartRowIncl
   *        Start row index (inclusive).
   * @param nEndRowIncl
   *        End row index (inclusive).
   * @param nStartColumnIncl
   *        Start column index (inclusive).
   * @param nEndColumnIncl
   *        End column index (inclusive).
   * @param aBSS
   *        Border style specification to be used. May not be <code>null</code>.
   */
  void applyGridToTable (@Nonnull PLTable aTable,
                         int nStartRowIncl,
                         int nEndRowIncl,
                         int nStartColumnIncl,
                         int nEndColumnIncl,
                         @Nonnull BorderStyleSpec aBSS);
}
