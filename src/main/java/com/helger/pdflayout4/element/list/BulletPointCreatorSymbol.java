package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout4.PLConvert;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * An implementation of {@link IBulletPointCreator} that always uses the Bullet
 * point character from symbol font.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class BulletPointCreatorSymbol extends BulletPointCreatorConstant
{
  public BulletPointCreatorSymbol (final char cSymbol, @Nonnegative final float fFontSize)
  {
    super (Character.toString (cSymbol), new FontSpec (PreloadFont.SYMBOL, fFontSize));
  }

  @Override
  @Nonnull
  public IPLRenderableObject <?> getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    final PLText ret = (PLText) super.getBulletPointElement (nBulletPointIndex);
    // Vertical align
    ret.setCustomAscentFirstLine (PLConvert.getWidthForFontSize (450, getFontSpec ().getFontSize ()));
    return ret;
  }

  @Nonnull
  public static BulletPointCreatorSymbol createFilledDot (@Nonnegative final float fFontSize)
  {
    // 183
    return new BulletPointCreatorSymbol ('\u00b7', fFontSize);
  }

  @Nonnull
  public static BulletPointCreatorSymbol createEmptyDot (@Nonnegative final float fFontSize)
  {
    // 176
    return new BulletPointCreatorSymbol ('\u00b0', fFontSize);
  }
}
