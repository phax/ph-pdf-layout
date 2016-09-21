/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * This class represents a single column within a HBox. This is a pseudo element
 * and does not have a padding, margin or border!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PLHBoxColumn
{
  private AbstractPLElement <?> m_aElement;
  private final WidthSpec m_aWidth;
  private Color m_aFillColor;

  public PLHBoxColumn (@Nonnull final AbstractPLElement <?> aElement, @Nonnull final WidthSpec aWidth)
  {
    setElement (aElement);
    m_aWidth = ValueEnforcer.notNull (aWidth, "Width");
  }

  @Nonnull
  public AbstractPLElement <?> getElement ()
  {
    return m_aElement;
  }

  @Nonnull
  PLHBoxColumn setElement (@Nonnull final AbstractPLElement <?> aElement)
  {
    m_aElement = ValueEnforcer.notNull (aElement, "Element");
    return this;
  }

  @Nonnull
  public WidthSpec getWidth ()
  {
    return m_aWidth;
  }

  /**
   * Set the cell fill color.
   *
   * @param aFillColor
   *        The fill color to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLHBoxColumn setFillColor (@Nullable final Color aFillColor)
  {
    m_aFillColor = aFillColor;
    return this;
  }

  /**
   * @return The current fill color. May be <code>null</code>.
   */
  @Nullable
  public Color getFillColor ()
  {
    return m_aFillColor;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("element", m_aElement)
                                       .append ("width", m_aWidth)
                                       .append ("fillColor", m_aFillColor)
                                       .toString ();
  }
}
