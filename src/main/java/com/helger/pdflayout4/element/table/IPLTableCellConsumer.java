package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Special consumer for table cells.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IPLTableCellConsumer
{
  /**
   * Performs this operation on the given arguments.
   *
   * @param aCell
   *        the current table cell. Never <code>null</code>.
   * @param nCellIndex
   *        The cell index. Always &ge; 0.
   * @param nEffectiveCellStartIndex
   *        The effective cell start index including colspan. Always &ge; 0.
   * @param nEffectiveCellEndIndex
   *        The effective cell end index including colspan. Always &ge; 0.
   */
  void accept (@Nonnull PLTableCell aCell,
               @Nonnegative int nCellIndex,
               @Nonnegative int nEffectiveCellStartIndex,
               @Nonnegative int nEffectiveCellEndIndex);
}
