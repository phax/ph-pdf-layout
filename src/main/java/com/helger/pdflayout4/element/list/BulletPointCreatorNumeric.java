/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.list;

import java.util.function.IntFunction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that uses a custom
 * {@link IntFunction} to create a custom formatted text.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class BulletPointCreatorNumeric extends AbstractBulletPointCreatorFontBased <BulletPointCreatorNumeric>
{
  private final IntFunction <String> m_aFormatter;

  public BulletPointCreatorNumeric (@Nonnull final IntFunction <String> aFormatter, @Nonnull final FontSpec aFontSpec)
  {
    super (aFontSpec);
    ValueEnforcer.notNull (aFormatter, "Formatter");
    m_aFormatter = aFormatter;
  }

  @Nonnull
  public final IntFunction <String> getFormatter ()
  {
    return m_aFormatter;
  }

  @Nonnull
  public String getBulletPointText (@Nonnegative final int nBulletPointIndex)
  {
    // Use 0-based index
    return m_aFormatter.apply (nBulletPointIndex);
  }

  @Nonnull
  public IPLRenderableObject <?> getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    return new PLText (getBulletPointText (nBulletPointIndex), getFontSpec ()).setVertSplittable (false).setPadding (getPadding ());
  }
}
