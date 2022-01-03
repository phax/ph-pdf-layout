/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.pdflayout4.spec.BorderStyleSpec;

/**
 * Default table grids.
 *
 * @author Philip Helger
 */
public enum EPLTableGridType implements IPLTableGridType
{
  /** Create no grid lines at all */
  NONE ("none")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, aRow -> {
        aRow.forEachCell ( (aCell, nIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          aCell.setBorder (null, null, null, null);
        });
      });
    }
  },
  /**
   * Create all grid lines. The first row has the border also on top, the other
   * rows don't. The first column also has a border on the left, the others
   * don't.
   */
  FULL ("full")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
            if (nRowIndex == nStartRowIncl)
            {
              // First row
              if (nEffectiveCellStartIndex == nStartColumnIncl)
                aCell.setBorder (aBSS, aBSS, aBSS, aBSS);
              else
                aCell.setBorder (aBSS, aBSS, aBSS, null);
            }
            else
            {
              // Other rows - no top border
              if (nEffectiveCellStartIndex == nStartColumnIncl)
                aCell.setBorder (null, aBSS, aBSS, aBSS);
              else
                aCell.setBorder (null, aBSS, aBSS, null);
            }
        });
      });
    }
  },
  /**
   * Create all grid lines except for the border lines. The first row has the
   * border also on top, the other rows don't. The
   */
  FULL_NO_BORDER ("full_no_border")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstRow = nRowIndex == nStartRowIncl;
            final boolean bFirstCol = nEffectiveCellStartIndex == nStartColumnIncl;
            aCell.setBorder (bFirstRow ? null : aBSS, null, null, bFirstCol ? null : aBSS);
          }
        });
      });
    }
  },
  /**
   * Create all grid lines. The first row has the border also on top, the other
   * rows don't. The first column also has a border on the left, the others
   * don't.
   */
  OUTER ("outer")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstRow = nRowIndex == nStartRowIncl;
            final boolean bLastRow = nRowIndex == nEndRowIncl;
            final boolean bFirstCol = nEffectiveCellStartIndex == nStartColumnIncl;
            final boolean bLastCol = nEffectiveCellEndIndex - 1 == nEndColumnIncl;
            aCell.setBorder (bFirstRow ? aBSS : null, bLastCol ? aBSS : null, bLastRow ? aBSS : null, bFirstCol ? aBSS : null);
          }
        });
      });
    }
  },

  /**
   * Create all horizontal lines. The first row has a border on top and bottom,
   * all other rows only at the bottom
   */
  HORZ_ALL ("horz_all")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstRow = nRowIndex == nStartRowIncl;
            aCell.setBorder (bFirstRow ? aBSS : null, null, aBSS, null);
          }
        });
      });
    }
  },

  /**
   * Create all horizontal lines. The first row has a border on all sides all
   * other rows at outer left, outer right and every bottom
   */
  HORZ_OUTER_BORDER ("horz_outer_border")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstRow = nRowIndex == nStartRowIncl;
            final boolean bFirstCol = nEffectiveCellStartIndex == nStartColumnIncl;
            final boolean bLastCol = nEffectiveCellEndIndex - 1 == nEndColumnIncl;
            aCell.setBorder (bFirstRow ? aBSS : null, bLastCol ? aBSS : null, aBSS, bFirstCol ? aBSS : null);
          }
        });
      });
    }
  },

  /**
   * Create only horizontal lines but without the border lines on top and on
   * bottom. All rows have a border on bottom except for the last line which has
   * no border.
   */
  HORZ_NO_BORDER ("horz_no_border")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bLastRow = nRowIndex == nEndRowIncl;
            aCell.setBorder (null, null, bLastRow ? null : aBSS, null);
          }
        });
      });
    }
  },
  /**
   * Create all vertical lines. The first column has a border on left and right,
   * all other columns only at the right
   */
  VERT_ALL ("vert_all")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstCol = nEffectiveCellStartIndex == nStartColumnIncl;
            aCell.setBorder (null, aBSS, null, bFirstCol ? aBSS : null);
          }
        });
      });
    }
  },

  /**
   * Create all vertical lines. The first column has a border on left, right and
   * top, all other columns at the outer top, outer bottom and every right
   */
  VERT_OUTER_BORDER ("vert_outer_border")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bFirstRow = nRowIndex == nStartRowIncl;
            final boolean bLastRow = nRowIndex == nEndRowIncl;
            final boolean bFirstCol = nEffectiveCellStartIndex == nStartColumnIncl;
            aCell.setBorder (bFirstRow ? aBSS : null, aBSS, bLastRow ? aBSS : null, bFirstCol ? aBSS : null);
          }
        });
      });
    }
  },
  /**
   * Create only vertical lines but without the border lines left and right. The
   * first column has a border on left and right, all other columns only at the
   * right
   */
  VERT_NO_BORDER ("vert_no_border")
  {
    @Override
    public void applyGridToTable (@Nonnull final PLTable aTable,
                                  @Nonnegative final int nStartRowIncl,
                                  @Nonnegative final int nEndRowIncl,
                                  @Nonnegative final int nStartColumnIncl,
                                  @Nonnegative final int nEndColumnIncl,
                                  @Nonnull final BorderStyleSpec aBSS)
    {
      ValueEnforcer.notNull (aTable, "Table");
      ValueEnforcer.notNull (aBSS, "BorderStyleSpec");
      aTable.forEachRow (nStartRowIncl, nEndRowIncl, (aRow, nRowIndex) -> {
        aRow.forEachCell ( (aCell, nCellIndex, nEffectiveCellStartIndex, nEffectiveCellEndIndex) -> {
          if (nEffectiveCellStartIndex >= nStartColumnIncl && nEffectiveCellStartIndex <= nEndColumnIncl)
          {
            final boolean bLastCol = nEffectiveCellEndIndex - 1 == nEndColumnIncl;
            aCell.setBorder (null, bLastCol ? null : aBSS, null, null);
          }
        });
      });
    }
  };

  private final String m_sID;

  EPLTableGridType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public static EPLTableGridType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPLTableGridType.class, sID);
  }
}
