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
package com.plenigo.pdflayout.element.list;

import com.helger.commons.ValueEnforcer;
import com.plenigo.pdflayout.base.AbstractPLRenderableObject;
import com.plenigo.pdflayout.base.IPLRenderableObject;
import com.plenigo.pdflayout.base.IPLSplittableObject;
import com.plenigo.pdflayout.base.PLSplitResult;
import com.plenigo.pdflayout.element.table.PLTable;
import com.plenigo.pdflayout.element.table.PLTableCell;
import com.plenigo.pdflayout.render.PageRenderContext;
import com.plenigo.pdflayout.render.PreparationContext;
import com.plenigo.pdflayout.spec.SizeSpec;
import com.plenigo.pdflayout.spec.WidthSpec;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * A simple bullet point list.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class PLBulletPointList extends AbstractPLRenderableObject<PLBulletPointList> implements
        IPLSplittableObject<PLBulletPointList, PLTable> {
    private final PLTable m_aTable;
    private final IBulletPointCreator m_aBulletPointCreator;

    public PLBulletPointList(@Nonnull final WidthSpec aWidthSpec, @Nonnull final IBulletPointCreator aBulletPointCreator) {
        ValueEnforcer.notNull(aWidthSpec, "WidthSpec");
        ValueEnforcer.notNull(aBulletPointCreator, "BulletPointCreator");

        // Using different width types requires to NOT use a colspan
        m_aTable = new PLTable(aWidthSpec, WidthSpec.star()).setID("bulletpoint-list");
        m_aBulletPointCreator = aBulletPointCreator;
    }

    @Nonnull
    public final PLTable getUnderlyingTable() {
        return m_aTable;
  }

  @Nonnull
  public final IBulletPointCreator getBulletPointCreator ()
  {
    return m_aBulletPointCreator;
  }

    @Nonnull
    public PLBulletPointList addBulletPoint(@Nonnull final IPLRenderableObject<?> aElement) {
        final int nBulletPointIndex = m_aTable.getRowCount();

        final PLTableCell aCellLeft = new PLTableCell(m_aBulletPointCreator.getBulletPointElement(nBulletPointIndex)).setID("bulletpoint");
        final PLTableCell aCellRight = new PLTableCell(aElement).setID("content");

        m_aTable.addRow(aCellLeft, aCellRight);
        return this;
    }

  @Override
  protected SizeSpec onPrepare (final PreparationContext aCtx)
  {
    return m_aTable.prepare (aCtx);
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    m_aTable.internalMarkAsNotPrepared ();
  }

  @Override
  protected void onRender (final PageRenderContext aCtx) throws IOException
  {
    m_aTable.render (aCtx);
  }

  @Nonnull
  public PLTable internalCreateNewVertSplitObject (@Nonnull final PLTable aBase)
  {
    return m_aTable.internalCreateNewVertSplitObject (aBase);
  }

  public final boolean isVertSplittable ()
  {
    return m_aTable.isVertSplittable ();
  }

  @Nonnull
  public final PLBulletPointList setVertSplittable (final boolean bVertSplittable)
  {
    m_aTable.setVertSplittable (bVertSplittable);
    return this;
  }

  @Nullable
  public final PLSplitResult splitElementVert (@Nonnegative final float fAvailableWidth, @Nonnegative final float fAvailableHeight)
  {
    return m_aTable.splitElementVert (fAvailableWidth, fAvailableHeight);
  }
}
