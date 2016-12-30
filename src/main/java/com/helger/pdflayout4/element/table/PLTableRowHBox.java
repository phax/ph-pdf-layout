package com.helger.pdflayout4.element.table;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.hbox.AbstractPLHBox;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.BorderSpec;
import com.helger.pdflayout4.spec.BorderStyleSpec;

/**
 * Special HBox for table rows, that creates table cells when split vertically.
 *
 * @author Philip Helger
 */
final class PLTableRowHBox extends AbstractPLHBox <PLTableRowHBox>
{
  @Override
  @Nonnull
  protected IPLRenderableObject <?> splitVertCreateEmptyElement (final float fWidth, final float fHeight)
  {
    if (false)
      return super.splitVertCreateEmptyElement (fWidth, fHeight);

    final PLTableCell ret = new PLTableCell (null);
    if (false)
      ret.setBorder (new BorderSpec (new BorderStyleSpec (Color.BLUE)));
    ret.prepare (new PreparationContext (null, fWidth + ret.getOutlineXSum (), fHeight + ret.getOutlineYSum ()));
    return ret;
  }

  @Nonnull
  public PLTableRowHBox internalCreateNewObject (@Nonnull final PLTableRowHBox aBase)
  {
    return new PLTableRowHBox ();
  }
}
