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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLHasPadding;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Abstract renderable PL element having a padding only
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLElementWithPadding <IMPLTYPE extends AbstractPLElementWithPadding <IMPLTYPE>>
                                                   extends AbstractPLRenderableObject <IMPLTYPE>
                                                   implements IPLHasPadding <IMPLTYPE>
{
  private PaddingSpec m_aPadding = DEFAULT_PADDING;

  public AbstractPLElementWithPadding ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLElementWithPadding <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setPadding (aSource.m_aPadding);
    return thisAsT ();
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

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("padding", m_aPadding).toString ();
  }
}
