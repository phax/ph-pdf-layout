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
package com.helger.pdflayout4.element.hbox;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.spec.WidthSpec;

/**
 * This class represents a single column within an {@link PLHBox}. This is a
 * pseudo element and does not have a padding, margin or border!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PLHBoxColumn implements Serializable
{
  private IPLRenderableObject <?> m_aElement;
  private WidthSpec m_aWidth;

  public PLHBoxColumn (@Nonnull final IPLRenderableObject <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    internalSetElement (aElement);
    internalSetWidth (aWidth);
  }

  @Nonnull
  public IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  @Nonnull
  PLHBoxColumn internalSetElement (@Nonnull final IPLRenderableObject <?> aElement)
  {
    m_aElement = ValueEnforcer.notNull (aElement, "Element");
    return this;
  }

  @Nonnull
  public WidthSpec getWidth ()
  {
    return m_aWidth;
  }

  @Nonnull
  PLHBoxColumn internalSetWidth (@Nonnull final WidthSpec aWidth)
  {
    m_aWidth = ValueEnforcer.notNull (aWidth, "Width");
    return this;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Element", m_aElement).append ("Width", m_aWidth).getToString ();
  }
}
