package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnull;

import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.hbox.AbstractPLHBox;
import com.helger.pdflayout4.render.PreparationContext;

/**
 * Special HBox for table rows, that creates table cells when split vertically.
 *
 * @author Philip Helger
 */
final class PLTableRowHBox extends AbstractPLHBox <PLTableRowHBox>
{
  @Override
  @Nonnull
  protected PLTableCell splitVertCreateEmptyElement (@Nonnull final IPLRenderableObject <?> aSrcObject,
                                                     final float fWidth,
                                                     final float fHeight)
  {
    final PLTableCell ret = new PLTableCell (null);
    ret.setBasicDataFrom ((PLTableCell) aSrcObject);
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }

  @Nonnull
  public PLTableRowHBox internalCreateNewVertSplitObject (@Nonnull final PLTableRowHBox aBase)
  {
    final PLTableRowHBox ret = new PLTableRowHBox ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }
}
