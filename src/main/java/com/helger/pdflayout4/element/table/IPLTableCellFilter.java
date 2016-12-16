package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Special consumer for table cells.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IPLTableCellFilter
{
  /**
   * Check if the passed cell properties are valid for handling.
   *
   * @param aCell
   *        the current table cell. Never <code>null</code>.
   * @param nCellIndex
   *        The cell index. Always &ge; 0.
   * @param nEffectiveCellIndex
   *        The effective cell index including colspan. Always &ge; 0.
   * @return <code>true</code> if the cell should be handled, <code>false</code>
   *         if not.
   */
  boolean test (@Nonnull PLTableCell aCell, @Nonnegative int nCellIndex, @Nonnegative int nEffectiveCellIndex);
}
