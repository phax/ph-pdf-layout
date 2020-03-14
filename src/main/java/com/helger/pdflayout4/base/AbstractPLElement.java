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
package com.helger.pdflayout4.base;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.spec.BorderSpec;
import com.helger.pdflayout4.spec.MarginSpec;
import com.helger.pdflayout4.spec.PaddingSpec;
import com.helger.pdflayout4.spec.SizeSpec;

/**
 * Abstract renderable PL element having a minimum size, a maximum size, margin,
 * border, padding and a fill color.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLElement <IMPLTYPE extends AbstractPLElement <IMPLTYPE>> extends
                                        AbstractPLRenderableObject <IMPLTYPE> implements
                                        IPLElement <IMPLTYPE>
{
  private SizeSpec m_aMinSize = DEFAULT_MIN_SIZE;
  private SizeSpec m_aMaxSize = DEFAULT_MAX_SIZE;
  private MarginSpec m_aMargin = DEFAULT_MARGIN;
  private BorderSpec m_aBorder = DEFAULT_BORDER;
  private PaddingSpec m_aPadding = DEFAULT_PADDING;
  private Color m_aFillColor = DEFAULT_FILL_COLOR;

  public AbstractPLElement ()
  {}

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setMargin (aSource.getMargin ());
    setBorder (aSource.getBorder ());
    setPadding (aSource.getPadding ());
    setFillColor (aSource.getFillColor ());
    return thisAsT ();
  }

  @Nonnull
  public final SizeSpec getMinSize ()
  {
    return m_aMinSize;
  }

  @Nonnull
  public final IMPLTYPE setMinSize (@Nonnull final SizeSpec aMinSize)
  {
    m_aMinSize = ValueEnforcer.notNull (aMinSize, "MinSize");
    onRenderSizeChange ();
    return thisAsT ();
  }

  @Nonnull
  public final SizeSpec getMaxSize ()
  {
    return m_aMaxSize;
  }

  @Nonnull
  public final IMPLTYPE setMaxSize (@Nonnull final SizeSpec aMaxSize)
  {
    m_aMaxSize = ValueEnforcer.notNull (aMaxSize, "MaxSize");
    onRenderSizeChange ();
    return thisAsT ();
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @Nonnull
  public final IMPLTYPE setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return thisAsT ();
  }

  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @Nonnull
  public final IMPLTYPE setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    m_aBorder = aBorder;
    return thisAsT ();
  }

  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public final IMPLTYPE setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return thisAsT ();
  }

  @Nullable
  public final Color getFillColor ()
  {
    return m_aFillColor;
  }

  @Nonnull
  public final IMPLTYPE setFillColor (@Nullable final Color aFillColor)
  {
    m_aFillColor = aFillColor;
    return thisAsT ();
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec getRenderSize (@Nonnull final SizeSpec aPreparedSize)
  {
    ValueEnforcer.notNull (aPreparedSize, "Size");

    // Consider min size here
    float fRealWidth = Math.max (m_aMinSize.getWidth (), aPreparedSize.getWidth ());
    float fRealHeight = Math.max (m_aMinSize.getHeight (), aPreparedSize.getHeight ());

    // Consider max size here
    fRealWidth = Math.min (m_aMaxSize.getWidth (), fRealWidth);
    fRealHeight = Math.min (m_aMaxSize.getHeight (), fRealHeight);

    return new SizeSpec (fRealWidth, fRealHeight);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("MinSize", m_aMinSize)
                            .append ("MaxSize", m_aMaxSize)
                            .append ("Margin", m_aMargin)
                            .append ("Border", m_aBorder)
                            .append ("Padding", m_aPadding)
                            .appendIfNotNull ("FillColor", m_aFillColor)
                            .getToString ();
  }
}
