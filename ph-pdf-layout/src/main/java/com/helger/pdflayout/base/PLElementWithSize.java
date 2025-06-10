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
package com.helger.pdflayout.base;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Wraps an {@link IPLRenderableObject} together with a size.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLElementWithSize
{
  private final IPLRenderableObject <?> m_aElement;
  private final SizeSpec m_aSize;
  private final SizeSpec m_aSizeFull;

  /**
   * Constructor
   *
   * @param aElement
   *        Element itself.
   * @param aSize
   *        Size of the element without padding, border and margin
   */
  public PLElementWithSize (@Nonnull final IPLRenderableObject <?> aElement, @Nonnull final SizeSpec aSize)
  {
    this (aElement, aSize, aSize.plus (aElement.getOutlineXSum (), aElement.getOutlineYSum ()));
  }

  /**
   * Constructor. This constructor is only present for the unlikely case that the full size differs
   * from the raw size with all the outlines added.
   *
   * @param aElement
   *        Element itself.
   * @param aSize
   *        Size of the element without padding, border and margin
   * @param aSizeFull
   *        Size of the element with padding, border and margin
   */
  public PLElementWithSize (@Nonnull final IPLRenderableObject <?> aElement,
                            @Nonnull final SizeSpec aSize,
                            @Nonnull final SizeSpec aSizeFull)
  {
    ValueEnforcer.notNull (aElement, "Element");
    ValueEnforcer.notNull (aSize, "Size");
    ValueEnforcer.notNull (aSizeFull, "SizeFull");
    m_aElement = aElement;
    m_aSize = aSize;
    m_aSizeFull = aSizeFull;
  }

  /**
   * @return The contained element.
   */
  @Nonnull
  public IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  /**
   * @return The size without padding, border or margin
   */
  @Nonnull
  public SizeSpec getSize ()
  {
    return m_aSize;
  }

  /**
   * @return The size with padding, border or margin
   */
  @Nonnull
  public SizeSpec getSizeFull ()
  {
    return m_aSizeFull;
  }

  /**
   * @return Width without padding, border or margin
   */
  public float getWidth ()
  {
    return m_aSize.getWidth ();
  }

  /**
   * @return Width with padding, border and margin
   */
  public float getWidthFull ()
  {
    return m_aSizeFull.getWidth ();
  }

  /**
   * @return Height without padding, border or margin
   */
  public float getHeight ()
  {
    return m_aSize.getHeight ();
  }

  /**
   * @return Height with padding, border and margin
   */
  public float getHeightFull ()
  {
    return m_aSizeFull.getHeight ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Element", m_aElement)
                                       .append ("Size", m_aSize)
                                       .append ("SizeFull", m_aSizeFull)
                                       .getToString ();
  }
}
