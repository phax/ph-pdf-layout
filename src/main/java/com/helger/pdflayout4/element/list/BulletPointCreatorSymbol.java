package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * An implementation of {@link IBulletPointCreator} that always uses the Bullet
 * point character from symbol font.
 *
 * @author Philip Helger
 * @since 5.0.10
 */
public class BulletPointCreatorSymbol extends BulletPointCreatorConstant
{
  public BulletPointCreatorSymbol (@Nonnegative final float fFontSize)
  {
    super (" \u00b7", new FontSpec (PreloadFont.SYMBOL, fFontSize));
  }

  @Override
  @Nonnull
  public PLText getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    final PLText ret = super.getBulletPointElement (nBulletPointIndex);
    ret.addMarginTop (-getFontSpec ().getFontSize ());
    return ret;
  }
}
