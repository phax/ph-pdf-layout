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
package com.helger.pdflayout.base;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Abstract renderable PL element having a padding only
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLElement <IMPLTYPE extends AbstractPLElement <IMPLTYPE>>
                                        extends AbstractPLRenderableObject <IMPLTYPE> implements IPLElement <IMPLTYPE>
{
  private MarginSpec m_aMargin = DEFAULT_MARGIN;
  private BorderSpec m_aBorder = DEFAULT_BORDER;
  private PaddingSpec m_aPadding = DEFAULT_PADDING;
  private Color m_aFillColor = DEFAULT_FILL_COLOR;

  public AbstractPLElement ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLElement <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setMargin (aSource.m_aMargin);
    setBorder (aSource.m_aBorder);
    setPadding (aSource.m_aPadding);
    setFillColor (aSource.m_aFillColor);
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    internalCheckNotPrepared ();
    m_aMargin = aMargin;
    return thisAsT ();
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @Nonnull
  public final IMPLTYPE setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    internalCheckNotPrepared ();
    m_aBorder = aBorder;
    return thisAsT ();
  }

  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @Nonnull
  public final IMPLTYPE setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    internalCheckNotPrepared ();
    m_aPadding = aPadding;
    return thisAsT ();
  }

  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public IMPLTYPE setFillColor (@Nullable final Color aFillColor)
  {
    internalCheckNotPrepared ();
    m_aFillColor = aFillColor;
    return thisAsT ();
  }

  @Nullable
  public Color getFillColor ()
  {
    return m_aFillColor;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("margin", m_aMargin)
                            .append ("border", m_aBorder)
                            .append ("padding", m_aPadding)
                            .appendIfNotNull ("fillColor", m_aFillColor)
                            .toString ();
  }
}
