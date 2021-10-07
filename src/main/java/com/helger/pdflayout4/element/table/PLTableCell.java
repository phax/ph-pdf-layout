/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.box.AbstractPLBox;
import com.helger.pdflayout4.element.special.PLSpacerX;
import com.helger.pdflayout4.render.PageRenderContext;

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
    setVertSplittable (true);
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
}
