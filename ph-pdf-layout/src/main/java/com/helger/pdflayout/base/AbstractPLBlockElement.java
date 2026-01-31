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

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;

/**
 * Abstract implementation of {@link IPLBlockElement}.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLBlockElement <IMPLTYPE extends AbstractPLBlockElement <IMPLTYPE>> extends
                                             AbstractPLElement <IMPLTYPE> implements
                                             IPLBlockElement <IMPLTYPE>
{
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private EVertAlignment m_eVertAlign = DEFAULT_VERT_ALIGNMENT;
  // Always use the full width?
  private boolean m_bFullWidth = DEFAULT_FULL_WIDTH;
  private boolean m_bClipContent = DEFAULT_CLIP_CONTENT;

  public AbstractPLBlockElement ()
  {}

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setHorzAlign (aSource.getHorzAlign ());
    setVertAlign (aSource.getVertAlign ());
    return thisAsT ();
  }

  @NonNull
  public final EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @NonNull
  public final IMPLTYPE setHorzAlign (@NonNull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
    return thisAsT ();
  }

  @NonNull
  public final EVertAlignment getVertAlign ()
  {
    return m_eVertAlign;
  }

  @NonNull
  public final IMPLTYPE setVertAlign (@NonNull final EVertAlignment eVertAlign)
  {
    m_eVertAlign = ValueEnforcer.notNull (eVertAlign, "VertAlign");
    return thisAsT ();
  }

  public final boolean isFullWidth ()
  {
    return m_bFullWidth;
  }

  @NonNull
  public final IMPLTYPE setFullWidth (final boolean bFullWidth)
  {
    m_bFullWidth = bFullWidth;
    return thisAsT ();
  }

  public final boolean isClipContent ()
  {
    return m_bClipContent;
  }

  @NonNull
  public final IMPLTYPE setClipContent (final boolean bClipContent)
  {
    m_bClipContent = bClipContent;
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("VertAlign", m_eVertAlign)
                            .append ("FullWidth", m_bFullWidth)
                            .append ("ClipContent", m_bClipContent)
                            .getToString ();
  }
}
