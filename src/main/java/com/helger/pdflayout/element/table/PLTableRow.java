package com.helger.pdflayout.element.table;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.hbox.PLHBoxColumn;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

public class PLTableRow extends AbstractPLRenderableObject <PLTableRow> implements IPLSplittableObject <PLTableRow>
{
  private final PLHBox m_aRow = new PLHBox ().setVertSplittable (true);

  public PLTableRow ()
  {}

  public void addCell (@Nonnull final PLTableCell aCell, @Nonnull final WidthSpec aWidth)
  {
    m_aRow.addColumn (aCell, aWidth);
  }

  @Nullable
  public PLTableCell getCellAtIndex (final int nIndex)
  {
    final PLHBoxColumn aColumn = m_aRow.getColumnAtIndex (nIndex);
    return aColumn == null ? null : (PLTableCell) aColumn.getElement ();
  }

  public void forEachCell (@Nonnull final Consumer <? super PLTableCell> aConsumer)
  {
    m_aRow.forEachColumn (x -> aConsumer.accept ((PLTableCell) x.getElement ()));
  }

  public void forEachCell (@Nonnull final ObjIntConsumer <? super PLTableCell> aConsumer)
  {
    m_aRow.forEachColumn ( (x, idx) -> aConsumer.accept ((PLTableCell) x.getElement (), idx));
  }

  @Override
  protected SizeSpec onPrepare (final PreparationContext aCtx) throws IOException
  {
    return m_aRow.prepare (aCtx);
  }

  public boolean isVertSplittable ()
  {
    return m_aRow.isVertSplittable ();
  }

  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    return m_aRow.splitElementVert (fAvailableWidth, fAvailableHeight);
  }

  @Override
  protected void onRender (final PageRenderContext aCtx) throws IOException
  {
    m_aRow.render (aCtx);
  }
}
