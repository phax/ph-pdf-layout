package com.helger.pdflayout4.element.list;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.base.AbstractPLRenderableObject;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.base.IPLSplittableObject;
import com.helger.pdflayout4.base.PLSplitResult;
import com.helger.pdflayout4.element.table.PLTable;
import com.helger.pdflayout4.element.table.PLTableCell;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.SizeSpec;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * A simple bullet point list.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class PLBulletPointList extends AbstractPLRenderableObject <PLBulletPointList> implements
                               IPLSplittableObject <PLBulletPointList, PLTable>
{
  private final PLTable m_aTable;
  private final IBulletPointCreator m_aBulletPointCreator;

  public PLBulletPointList (@Nonnull final WidthSpec aWidthSpec, @Nonnull final IBulletPointCreator aBulletPointCreator)
  {
    ValueEnforcer.notNull (aWidthSpec, "WidthSpec");
    ValueEnforcer.notNull (aBulletPointCreator, "BulletPointCreator");

    // Using different width types requires to NOT use a colspan
    m_aTable = new PLTable (aWidthSpec, WidthSpec.star ()).setID ("bulletpoint-list");
    m_aBulletPointCreator = aBulletPointCreator;
  }

  @Nonnull
  public final PLTable getUnderlyingTable ()
  {
    return m_aTable;
  }

  @Nonnull
  public final IBulletPointCreator getBulletPointCreator ()
  {
    return m_aBulletPointCreator;
  }

  public void addBulletPoint (@Nonnull final IPLRenderableObject <?> aElement)
  {
    final int nBulletPointIndex = m_aTable.getRowCount ();

    final PLTableCell aCellLeft = new PLTableCell (m_aBulletPointCreator.getBulletPointElement (nBulletPointIndex)).setID ("bulletpoint");
    final PLTableCell aCellRight = new PLTableCell (aElement).setID ("content");

    m_aTable.addRow (aCellLeft, aCellRight);
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
  public final PLSplitResult splitElementVert (@Nonnegative final float fAvailableWidth,
                                               @Nonnegative final float fAvailableHeight)
  {
    return m_aTable.splitElementVert (fAvailableWidth, fAvailableHeight);
  }
}
