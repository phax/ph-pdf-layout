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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout.base.AbstractPLElement;

/**
 * This class represents a single table cell within a table row.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLTableCell
{
  public static final int DEFAULT_COL_SPAN = 1;

  private final AbstractPLElement <?> m_aElement;
  private final int m_nColSpan;
  private Color m_aFillColor;

  public PLTableCell (@Nonnull final AbstractPLElement <?> aElement)
  {
    this (aElement, DEFAULT_COL_SPAN);
  }

  public PLTableCell (@Nonnull final AbstractPLElement <?> aElement, @Nonnegative final int nColSpan)
  {
    ValueEnforcer.notNull (aElement, "Element");
    ValueEnforcer.isGT0 (nColSpan, "ColSpan");
    m_aElement = aElement;
    m_nColSpan = nColSpan;
  }

  @Nonnull
  public AbstractPLElement <?> getElement ()
  {
    return m_aElement;
  }

  @Nonnegative
  public int getColSpan ()
  {
    return m_nColSpan;
  }

  /**
   * Set the cell fill color.
   *
   * @param aFillColor
   *        The fill color to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLTableCell setFillColor (@Nullable final Color aFillColor)
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
}
