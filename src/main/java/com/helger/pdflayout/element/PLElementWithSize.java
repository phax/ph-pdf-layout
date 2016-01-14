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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Wraps an {@link AbstractPLElement} and stores the height.
 *
 * @author Philip Helger
 */
public final class PLElementWithSize
{
  private final AbstractPLElement <?> m_aElement;
  private final SizeSpec m_aSize;
  private final SizeSpec m_aSizeFull;

  /**
   * Constructor
   *
   * @param aElement
   *        Element itself.
   * @param aSize
   *        Size of the element without padding and margin
   */
  public PLElementWithSize (@Nonnull final AbstractPLElement <?> aElement, @Nonnull final SizeSpec aSize)
  {
    ValueEnforcer.notNull (aElement, "Element");
    ValueEnforcer.notNull (aSize, "Size");
    m_aElement = aElement;
    m_aSize = aSize;
    m_aSizeFull = new SizeSpec (aSize.getWidth () +
                                aElement.getMarginPlusPaddingXSum (),
                                aSize.getHeight () + aElement.getMarginPlusPaddingYSum ());
  }

  /**
   * @return The contained element.
   */
  @Nonnull
  public AbstractPLElement <?> getElement ()
  {
    return m_aElement;
  }

  /**
   * @return The size without padding or margin
   */
  @Nonnull
  public SizeSpec getSize ()
  {
    return m_aSize;
  }

  /**
   * @return The size with padding or margin
   */
  @Nonnull
  public SizeSpec getSizeFull ()
  {
    return m_aSizeFull;
  }

  /**
   * @return Width without padding or margin
   */
  public float getWidth ()
  {
    return m_aSize.getWidth ();
  }

  /**
   * @return Width with padding and margin
   */
  public float getWidthFull ()
  {
    return m_aSizeFull.getWidth ();
  }

  /**
   * @return Height without padding or margin
   */
  public float getHeight ()
  {
    return m_aSize.getHeight ();
  }

  /**
   * @return Height with padding and margin
   */
  public float getHeightFull ()
  {
    return m_aSizeFull.getHeight ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("element", m_aElement)
                                       .append ("size", m_aSize)
                                       .append ("sizeFull", m_aSizeFull)
                                       .toString ();
  }
}
