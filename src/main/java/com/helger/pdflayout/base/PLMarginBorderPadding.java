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
package com.helger.pdflayout.base;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Represents a single page layout as element. It consists of a page size, a
 * page header and footer as well as a set of page body elements.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLMarginBorderPadding implements IPLHasMarginBorderPadding <PLMarginBorderPadding>
{
  private MarginSpec m_aMargin;
  private PaddingSpec m_aPadding;
  private BorderSpec m_aBorder;

  public PLMarginBorderPadding (@Nonnull final MarginSpec aMargin, @Nonnull final PaddingSpec aPadding, @Nonnull final BorderSpec aBorder)
  {
    setMargin (aMargin);
    setPadding (aPadding);
    setBorder (aBorder);
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @Nonnull
  public final PLMarginBorderPadding setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return this;
  }

  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public final PLMarginBorderPadding setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return this;
  }

  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @Nonnull
  public final PLMarginBorderPadding setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    m_aBorder = aBorder;
    return this;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Margin", m_aMargin)
                            .append ("Padding", m_aPadding)
                            .append ("Border", m_aBorder)
                            .getToString ();
  }
}
