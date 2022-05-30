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
import com.plenigo.pdflayout.base.IPLHasPadding;
import com.plenigo.pdflayout.spec.FontSpec;
import com.plenigo.pdflayout.spec.PaddingSpec;

import javax.annotation.Nonnull;

/**
 * An abstract implementation of {@link IBulletPointCreator} that uses a Font
 * based character.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 5.1.0
 */
public abstract class AbstractBulletPointCreatorFontBased <IMPLTYPE extends AbstractBulletPointCreatorFontBased <IMPLTYPE>> implements
                                                          IBulletPointCreator,
        IPLHasPadding<IMPLTYPE>
{
  private final FontSpec m_aFontSpec;
  private PaddingSpec m_aPadding = PaddingSpec.PADDING0;

  public AbstractBulletPointCreatorFontBased (@Nonnull final FontSpec aFontSpec)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    m_aFontSpec = aFontSpec;
  }

  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public final IMPLTYPE setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return thisAsT ();
  }
}
