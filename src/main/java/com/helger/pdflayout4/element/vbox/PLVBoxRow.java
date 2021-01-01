/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.vbox;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.spec.HeightSpec;

/**
 * This class represents a single row within a VBox. This is a pseudo element
 * and does not have a padding, margin or border!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PLVBoxRow
{
  private IPLRenderableObject <?> m_aElement;
  private final HeightSpec m_aHeight;

  public PLVBoxRow (@Nonnull final IPLRenderableObject <?> aElement, @Nonnull final HeightSpec aHeight)
  {
    internalSetElement (aElement);
    m_aHeight = ValueEnforcer.notNull (aHeight, "Height");
  }

  @Nonnull
  public IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  @Nonnull
  PLVBoxRow internalSetElement (@Nonnull final IPLRenderableObject <?> aElement)
  {
    m_aElement = ValueEnforcer.notNull (aElement, "Element");
    return this;
  }

  @Nonnull
  public HeightSpec getHeight ()
  {
    return m_aHeight;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Element", m_aElement).append ("Height", m_aHeight).getToString ();
  }
}
