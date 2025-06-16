/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that always uses the same character (like in an
 * unordered list).
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class BulletPointCreatorConstant extends AbstractBulletPointCreatorFontBased <BulletPointCreatorConstant>
{
  private final String m_sText;

  public BulletPointCreatorConstant (@Nonnull final String sText, @Nonnull final FontSpec aFontSpec)
  {
    super (aFontSpec);
    ValueEnforcer.notNull (sText, "Text");
    m_sText = sText;
  }

  @Nonnull
  public final String getText ()
  {
    return m_sText;
  }

  @Nonnull
  public PLText getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    return new PLText (m_sText, getFontSpec ()).setVertSplittable (false).setPadding (getPadding ());
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Text", m_sText).getToString ();
  }
}
