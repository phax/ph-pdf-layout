/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;

/**
 * Abstract implementation of {@link IPLBlockElement}.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLBlockElement <IMPLTYPE extends AbstractPLBlockElement <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
                                             implements
                                             IPLBlockElement <IMPLTYPE>
{
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private EVertAlignment m_eVertAlign = DEFAULT_VERT_ALIGNMENT;
  // Always use the full width?
  private boolean m_bFullWidth = DEFAULT_FULL_WIDTH;

  public AbstractPLBlockElement ()
  {}

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setHorzAlign (aSource.getHorzAlign ());
    setVertAlign (aSource.getVertAlign ());
    return thisAsT ();
  }

  @Nonnull
  public final EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Nonnull
  public final IMPLTYPE setHorzAlign (@Nonnull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
    return thisAsT ();
  }

  @Nonnull
  public final EVertAlignment getVertAlign ()
  {
    return m_eVertAlign;
  }

  @Nonnull
  public final IMPLTYPE setVertAlign (@Nonnull final EVertAlignment eVertAlign)
  {
    m_eVertAlign = ValueEnforcer.notNull (eVertAlign, "VertAlign");
    return thisAsT ();
  }

  public final boolean isFullWidth ()
  {
    return m_bFullWidth;
  }

  @Nonnull
  public final IMPLTYPE setFullWidth (final boolean bFullWidth)
  {
    m_bFullWidth = bFullWidth;
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("VertAlign", m_eVertAlign)
                            .append ("FullWidth", m_bFullWidth)
                            .getToString ();
  }
}
