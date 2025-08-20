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

import java.io.IOException;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.element.box.AbstractPLBox;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.render.PageRenderContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class represents a single table cell within a table row.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLTableCell extends AbstractPLBox <PLTableCell>
{
  public static final int DEFAULT_COL_SPAN = 1;

  private int m_nColSpan;

  public PLTableCell (@Nullable final IPLRenderableObject <?> aElement)
  {
    this (aElement, DEFAULT_COL_SPAN);
  }

  public PLTableCell (@Nullable final IPLRenderableObject <?> aElement, @Nonnegative final int nColSpan)
  {
    super (aElement);
    _setColSpan (nColSpan);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLTableCell setBasicDataFrom (@Nonnull final PLTableCell aSource)
  {
    super.setBasicDataFrom (aSource);
    _setColSpan (aSource.getColSpan ());
    return this;
  }

  @Nonnegative
  public int getColSpan ()
  {
    return m_nColSpan;
  }

  private void _setColSpan (@Nonnegative final int nColSpan)
  {
    ValueEnforcer.isGT0 (nColSpan, "ColSpan");
    m_nColSpan = nColSpan;
  }

  @Override
  @Nonnull
  public PLTableCell internalCreateNewVertSplitObject (@Nonnull final PLTableCell aBase)
  {
    final PLTableCell ret = new PLTableCell (null, aBase.getColSpan ());
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // No further ado
    super.onRender (aCtx);
  }

  /**
   * Utility method to create an empty cell.
   *
   * @return The new empty table cell.
   * @since 5.1.3
   */
  @Nonnull
  public static PLTableCell createEmptyCell ()
  {
    return new PLTableCell (new PLSpacerX ());
  }

  /**
   * Utility method to create an empty cell with a colspan.
   *
   * @param nColSpan
   *        The column span to use. Must be &gt; 0.
   * @return The new empty table cell.
   * @since 7.0.1
   */
  @Nonnull
  public static PLTableCell createEmptyCell (@Nonnegative final int nColSpan)
  {
    return new PLTableCell (new PLSpacerX (), nColSpan);
  }

  /**
   * Utility method to create an array of empty cells.
   *
   * @param nCellCount
   *        The number of cells to create. Must be &ge; 0.
   * @return The new empty table cell.
   * @since 7.0.1
   */
  @Nonnull
  public static PLTableCell [] createEmptyCells (@Nonnegative final int nCellCount)
  {
    ValueEnforcer.isGE0 (nCellCount, "CellCount");
    final PLTableCell [] ret = new PLTableCell [nCellCount];
    for (int i = 0; i < nCellCount; ++i)
      ret[i] = createEmptyCell ();
    return ret;
  }
}
