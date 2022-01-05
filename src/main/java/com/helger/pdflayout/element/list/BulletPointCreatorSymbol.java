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
package com.helger.pdflayout.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout.PLConvert;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

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
