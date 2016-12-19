package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnull;

/**
 * PLTable helper class containing additional methods to deal with PLTable
 * special cases
 *
 * @author Saskia Reimerth
 */
public class PLTableHelper
{

  private PLTableHelper ()
  {}

  /**
   * If two joined rows both have borders at their connecting side, the doubles
   * width has to be removed
   *
   * @param ret
   *        the PLTable, whose doubled borders are to be removed
   */
  public static void avoidDoubleBorders (@Nonnull PLTable ret)
  {
    boolean bPreviousRowHasBottomBorder = false;
    for (int i = 0; i < ret.getRowCount (); i++)
    {
      boolean bRowHasBottomBorder = true;
      boolean bRowHasTopBorder = true;
      PLTableRow aRow = ret.getRowAtIndex (i);
      for (int j = 0; j < aRow.getCellCount (); j++)
      {
        PLTableCell aCell = aRow.getCellAtIndex (j);

        if (aCell.getBorderBottomWidth () == 0)
          bRowHasBottomBorder = false;
        if (aCell.getBorderTopWidth () == 0)
          bRowHasTopBorder = false;
      }
      if (bPreviousRowHasBottomBorder && bRowHasTopBorder)
        aRow.setBorderTop (null);
      bPreviousRowHasBottomBorder = bRowHasBottomBorder;
    }
  }
}
