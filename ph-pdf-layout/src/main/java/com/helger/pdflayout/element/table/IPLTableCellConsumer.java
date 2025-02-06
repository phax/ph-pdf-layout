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
