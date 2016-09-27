package com.helger.pdflayout.element.table;

import javax.annotation.Nonnull;

import com.helger.commons.id.IHasID;
import com.helger.pdflayout.spec.BorderStyleSpec;

/**
 * Custom {@link PLTable} grid specification.
 * 
 * @author Philip Helger
 */
public interface IPLTableGridType extends IHasID <String>
{
  /**
   * Apply this grid to the provided table.
   * 
   * @param aTable
   *        Table to modify. May not be <code>null</code>.
   * @param aBSS
   *        Border style specification to be used. May not be <code>null</code>.
   */
  void applyGridToTable (@Nonnull PLTable aTable, @Nonnull BorderStyleSpec aBSS);
}
