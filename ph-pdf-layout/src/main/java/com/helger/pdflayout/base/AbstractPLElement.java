/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Abstract renderable PL element having a minimum size, a maximum size, margin, border, padding and
 * a fill color.
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
  private PLColor m_aFillColor = DEFAULT_FILL_COLOR;

  public AbstractPLElement ()
  {}

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    // Min size and max size is not set on purpose
    setMargin (aSource.getMargin ());
    setBorder (aSource.getBorder ());
    setPadding (aSource.getPadding ());
    setFillColor (aSource.getFillColor ());
    return thisAsT ();
  }

  @NonNull
  public final SizeSpec getMinSize ()
  {
    return m_aMinSize;
  }

  @NonNull
  public final IMPLTYPE setMinSize (@NonNull final SizeSpec aMinSize)
  {
    m_aMinSize = ValueEnforcer.notNull (aMinSize, "MinSize");
    onRenderSizeChange ();
    return thisAsT ();
  }

  @NonNull
  public final SizeSpec getMaxSize ()
  {
    return m_aMaxSize;
  }

  @NonNull
  public final IMPLTYPE setMaxSize (@NonNull final SizeSpec aMaxSize)
  {
    m_aMaxSize = ValueEnforcer.notNull (aMaxSize, "MaxSize");
    onRenderSizeChange ();
    return thisAsT ();
  }

  @NonNull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @NonNull
  public final IMPLTYPE setMargin (@NonNull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return thisAsT ();
  }

  @NonNull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @NonNull
  public final IMPLTYPE setBorder (@NonNull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    m_aBorder = aBorder;
    return thisAsT ();
  }

  @NonNull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @NonNull
  public final IMPLTYPE setPadding (@NonNull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return thisAsT ();
  }

  @Nullable
  public final PLColor getFillColor ()
  {
    return m_aFillColor;
  }

  @NonNull
  public final IMPLTYPE setFillColor (@Nullable final PLColor aFillColor)
  {
    m_aFillColor = aFillColor;
    return thisAsT ();
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  protected SizeSpec getRenderSize (@NonNull final SizeSpec aPreparedSize)
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
