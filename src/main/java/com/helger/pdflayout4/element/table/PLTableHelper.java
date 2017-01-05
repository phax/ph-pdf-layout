/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
  public static void avoidDoubleBorders (@Nonnull final PLTable ret)
  {
    boolean bPreviousRowHasBottomBorder = false;
    for (int i = 0; i < ret.getRowCount (); i++)
    {
      boolean bRowHasBottomBorder = true;
      boolean bRowHasTopBorder = true;
      final PLTableRow aRow = ret.getRowAtIndex (i);
      for (int j = 0; j < aRow.getCellCount (); j++)
      {
        final PLTableCell aCell = aRow.getCellAtIndex (j);

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
