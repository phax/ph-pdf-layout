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
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.element.hbox.AbstractPLHBox;
import com.helger.pdflayout.element.hbox.PLHBoxColumn;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.WidthSpec;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A special HBox representing a table row.
 *
 * @author Philip Helger
 */
public class PLTableRow extends AbstractPLHBox <PLTableRow>
{
  public PLTableRow ()
  {}

  @Override
  @Nonnull
  protected AbstractPLRenderableObject <?> internalCreateVertSplitEmptyElement (@Nonnull final IPLRenderableObject <?> aSrcObject,
                                                                                final float fWidth,
                                                                                final float fHeight,
                                                                                @Nullable final String sID)
  {
    final PLTableCell ret = new PLTableCell (null);
    ret.setID (sID);
    ret.setBasicDataFrom ((PLTableCell) aSrcObject);
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }

  @Nonnull
  public PLTableRow internalCreateNewVertSplitObject (@Nonnull final PLTableRow aBase)
  {
    final PLTableRow ret = new PLTableRow ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // No further ado
    super.onRender (aCtx);
  }

  public void addCell (@Nonnull final PLTableCell aCell, @Nonnull final WidthSpec aWidth)
  {
    addColumn (aCell, aWidth);
  }

  @Nullable
  public PLTableCell getCellAtIndex (final int nIndex)
  {
    final PLHBoxColumn aColumn = getColumnAtIndex (nIndex);
    return aColumn == null ? null : (PLTableCell) aColumn.getElement ();
  }

  @Nullable
  public PLTableCell getFirstCell ()
  {
    return getCellAtIndex (0);
  }

  @Nullable
  public PLTableCell getLastCell ()
  {
    return getCellAtIndex (getColumnCount () - 1);
  }

  public void forEachCell (@Nonnull final Consumer <? super PLTableCell> aConsumer)
  {
    forEachColumn (x -> aConsumer.accept ((PLTableCell) x.getElement ()));
  }

  public void forEachCell (@Nonnull final ObjIntConsumer <? super PLTableCell> aConsumer)
  {
    forEachColumnByIndex ( (x, idx) -> aConsumer.accept ((PLTableCell) x.getElement (), idx));
  }

  public void forEachCell (@Nonnull final IPLTableCellConsumer aConsumer)
  {
    final MutableInt aEffectiveIndex = new MutableInt (0);
    forEachColumnByIndex ( (x, idx) -> {
      final PLTableCell aCell = (PLTableCell) x.getElement ();
      final int nColSpan = aCell.getColSpan ();
      aConsumer.accept (aCell, idx, aEffectiveIndex.intValue (), aEffectiveIndex.intValue () + nColSpan);
      aEffectiveIndex.inc (nColSpan);
    });
  }

  public void forEachCell (final int nStartIncl,
                           final int nEndIncl,
                           @Nonnull final Consumer <? super PLTableCell> aConsumer)
  {
    forEachCell ( (x, idx) -> {
      if (idx >= nStartIncl && idx <= nEndIncl)
        aConsumer.accept (x);
    });
  }

  public void forEachCell (final int nStartIncl,
                           final int nEndIncl,
                           @Nonnull final ObjIntConsumer <? super PLTableCell> aConsumer)
  {
    forEachCell ( (x, idx) -> {
      if (idx >= nStartIncl && idx <= nEndIncl)
        aConsumer.accept (x, idx);
    });
  }

  public void forEachCell (@Nonnull final IPLTableCellFilter aFilter, @Nonnull final IPLTableCellConsumer aConsumer)
  {
    forEachCell ( (x, idx, esidx, eeidx) -> {
      if (aFilter.test (x, idx, esidx, eeidx))
        aConsumer.accept (x, idx, esidx, eeidx);
    });
  }

  @Nonnull
  public PLTableRow setFillColor (@Nullable final PLColor aFillColor)
  {
    forEachCell (x -> x.setFillColor (aFillColor));
    return this;
  }

  @Nonnull
  public PLTableRow setBorder (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorder (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderTop (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderRight (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderBottom (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderLeft (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderX (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderX (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setBorderY (@Nullable final BorderStyleSpec aBorder)
  {
    forEachCell (x -> x.setBorderY (aBorder));
    return this;
  }

  @Nonnull
  public PLTableRow setMargin (@Nonnull final MarginSpec aMargin)
  {
    forEachCell (x -> x.setMargin (aMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMargin (final float fMargin)
  {
    forEachCell (x -> x.setMargin (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginTop (final float fMargin)
  {
    forEachCell (x -> x.setMarginTop (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginRight (final float fMargin)
  {
    forEachCell (x -> x.setMarginRight (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginBottom (final float fMargin)
  {
    forEachCell (x -> x.setMarginBottom (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginLeft (final float fMargin)
  {
    forEachCell (x -> x.setMarginLeft (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginX (final float fMargin)
  {
    forEachCell (x -> x.setMarginX (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setMarginY (final float fMargin)
  {
    forEachCell (x -> x.setMarginY (fMargin));
    return this;
  }

  @Nonnull
  public PLTableRow setPadding (@Nonnull final PaddingSpec aPadding)
  {
    forEachCell (x -> x.setPadding (aPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPadding (final float fPadding)
  {
    forEachCell (x -> x.setPadding (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingTop (final float fPadding)
  {
    forEachCell (x -> x.setPaddingTop (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingRight (final float fPadding)
  {
    forEachCell (x -> x.setPaddingRight (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingBottom (final float fPadding)
  {
    forEachCell (x -> x.setPaddingBottom (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingLeft (final float fPadding)
  {
    forEachCell (x -> x.setPaddingLeft (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingX (final float fPadding)
  {
    forEachCell (x -> x.setPaddingX (fPadding));
    return this;
  }

  @Nonnull
  public PLTableRow setPaddingY (final float fPadding)
  {
    forEachCell (x -> x.setPaddingY (fPadding));
    return this;
  }
}
