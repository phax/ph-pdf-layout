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

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Cell range.
 *
 * @author Philip Helger
 */
public class PLCellRange implements IPLCellRange
{
  private int m_nFirstRow;
  private int m_nLastRow;
  private int m_nFirstCol;
  private int m_nLastCol;

  /**
   * @param nFirstRow
   *        First row index, inclusive.
   * @param nLastRow
   *        Last row index, inclusive.
   * @param nFirstCol
   *        First column index, inclusive.
   * @param nLastCol
   *        Last column index, inclusive.
   */
  public PLCellRange (final int nFirstRow, final int nLastRow, final int nFirstCol, final int nLastCol)
  {
    setFirstRow (nFirstRow);
    setLastRow (nLastRow);
    setFirstColumn (nFirstCol);
    setLastColumn (nLastCol);
  }

  /**
   * @return row number for the upper left hand corner
   */
  public int getFirstRow ()
  {
    return m_nFirstRow;
  }

  /**
   * @return row number for the lower right hand corner
   */
  public int getLastRow ()
  {
    return m_nLastRow;
  }

  /**
   * @return column number for the upper left hand corner
   */
  public int getFirstColumn ()
  {
    return m_nFirstCol;
  }

  /**
   * @return column number for the lower right hand corner
   */
  public int getLastColumn ()
  {
    return m_nLastCol;
  }

  /**
   * @param nFirstRow
   *        row number for the upper left hand corner
   */
  public final void setFirstRow (final int nFirstRow)
  {
    m_nFirstRow = nFirstRow;
  }

  /**
   * @param nLastRow
   *        row number for the lower right hand corner
   */
  public final void setLastRow (final int nLastRow)
  {
    m_nLastRow = nLastRow;
  }

  /**
   * @param nFirstCol
   *        column number for the upper left hand corner
   */
  public final void setFirstColumn (final int nFirstCol)
  {
    m_nFirstCol = nFirstCol;
  }

  /**
   * @param nLastCol
   *        column number for the lower right hand corner
   */
  public final void setLastColumn (final int nLastCol)
  {
    m_nLastCol = nLastCol;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("FirstRow", m_nFirstRow)
                                       .append ("LastRow", m_nLastRow)
                                       .append ("FirstColumn", m_nFirstCol)
                                       .append ("LastColumn", m_nLastCol)
                                       .toString ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PLCellRange rhs = (PLCellRange) o;
    return m_nFirstRow == rhs.m_nFirstRow &&
           m_nLastRow == rhs.m_nLastRow &&
           m_nFirstCol == rhs.m_nFirstCol &&
           m_nLastCol == rhs.m_nLastCol;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nFirstRow)
                                       .append (m_nLastRow)
                                       .append (m_nFirstCol)
                                       .append (m_nLastCol)
                                       .getHashCode ();
  }
}
