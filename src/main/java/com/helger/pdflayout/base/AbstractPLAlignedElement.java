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

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.EVertAlignment;

/**
 * Base class for text and image elements - so elements only having a padding
 * but no border themselves.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLAlignedElement <IMPLTYPE extends AbstractPLAlignedElement <IMPLTYPE>>
                                               extends AbstractPLHorzAlignedElement <IMPLTYPE>
                                               implements IPLHasVerticalAlignment <IMPLTYPE>
{
  private EVertAlignment m_eVertAlign = DEFAULT_VERT_ALIGNMENT;

  public AbstractPLAlignedElement ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLAlignedElement <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setVertAlign (aSource.m_eVertAlign);
    return thisAsT ();
  }

  @Nonnull
  public EVertAlignment getVertAlign ()
  {
    return m_eVertAlign;
  }

  @Nonnull
  public IMPLTYPE setVertAlign (@Nonnull final EVertAlignment eVertAlign)
  {
    m_eVertAlign = ValueEnforcer.notNull (eVertAlign, "VertAlign");
    return thisAsT ();
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * prepared height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @return The indentation offset
   */
  protected float getIndentY (final float fAvailableHeight)
  {
    return getIndentY (fAvailableHeight, getPreparedHeight ());
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * provided element height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @param fElementHeight
   *        The height of the element to align
   * @return The indentation offset
   */
  protected float getIndentY (final float fAvailableHeight, final float fElementHeight)
  {
    switch (m_eVertAlign)
    {
      case TOP:
        return 0f;
      case MIDDLE:
        return (fAvailableHeight - fElementHeight) / 2f;
      case BOTTOM:
        return fAvailableHeight - fElementHeight;
      default:
        throw new IllegalStateException ("Unsupported vertical alignment " + m_eVertAlign);
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("VertAlign", m_eVertAlign).toString ();
  }
}
