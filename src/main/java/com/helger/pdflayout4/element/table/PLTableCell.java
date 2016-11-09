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
package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.box.AbstractPLBox;

/**
 * This class represents a single table cell within a table row.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLTableCell extends AbstractPLBox <PLTableCell>
{
  public static final int DEFAULT_COL_SPAN = 1;

  private final int m_nColSpan;

  public PLTableCell (@Nonnull final IPLRenderableObject <?> aElement)
  {
    this (aElement, DEFAULT_COL_SPAN);
  }

  public PLTableCell (@Nonnull final IPLRenderableObject <?> aElement, @Nonnegative final int nColSpan)
  {
    super (aElement);
    ValueEnforcer.isGT0 (nColSpan, "ColSpan");
    m_nColSpan = nColSpan;
    setVertSplittable (true);
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLTableCell setBasicDataFrom (@Nonnull final PLTableCell aSource)
  {
    super.setBasicDataFrom (aSource);
    return this;
  }

  @Nonnegative
  public int getColSpan ()
  {
    return m_nColSpan;
  }
}
