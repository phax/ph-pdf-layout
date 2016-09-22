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
import com.helger.pdflayout.spec.EHorzAlignment;
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
                                               extends AbstractPLElement <IMPLTYPE> implements
                                               IPLHasHorizontalAlignment <IMPLTYPE>,
                                               IPLHasVerticalAlignment <IMPLTYPE>
{
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private EVertAlignment m_eVertAlign = DEFAULT_VERT_ALIGNMENT;

  public AbstractPLAlignedElement ()
  {}

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLAlignedElement <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setHorzAlign (aSource.m_eHorzAlign);
    setVertAlign (aSource.m_eVertAlign);
    return thisAsT ();
  }

  @Nonnull
  public EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Nonnull
  public IMPLTYPE setHorzAlign (@Nonnull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
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
   * Get the indentation for a certain horizontal alignment. This method uses
   * the prepared width as the basis for alignment.
   *
   * @param eHorzAlign
   *        Horizontal alignment. May not be <code>null</code>.
   * @param fAvailableWidth
   *        The available width of the surrounding element.
   * @return The left margin of this object plus the indentation offset
   */
  protected float getIndentX (@Nonnull final EHorzAlignment eHorzAlign, final float fAvailableWidth)
  {
    return getIndentX (eHorzAlign, fAvailableWidth, getPreparedSize ().getWidth ());
  }

  /**
   * Get the indentation for a certain horizontal alignment. This method uses
   * the provided element width as the basis for alignment.
   *
   * @param eHorzAlign
   *        Horizontal alignment. May not be <code>null</code>.
   * @param fAvailableWidth
   *        The available width of the surrounding element.
   * @param fElementWidth
   *        The width of the element to align
   * @return The left margin of this object plus the indentation offset
   */
  protected float getIndentX (@Nonnull final EHorzAlignment eHorzAlign,
                              final float fAvailableWidth,
                              final float fElementWidth)
  {
    final float fLeft = getMarginLeft ();
    final float fUsableWidth = fAvailableWidth - getMarginXSum ();

    switch (eHorzAlign)
    {
      case LEFT:
        return fLeft;
      case CENTER:
        return fLeft + (fUsableWidth - fElementWidth) / 2;
      case RIGHT:
        return fLeft + fUsableWidth - fElementWidth;
      default:
        throw new IllegalStateException ("Unsupported horizontal alignment " + eHorzAlign);
    }
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * prepared height as the basis for alignment.
   *
   * @param eVertAlign
   *        Vertical alignment. May not be <code>null</code>.
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @return The top margin of this object plus the indentation offset
   */
  protected float getIndentY (@Nonnull final EVertAlignment eVertAlign, final float fAvailableHeight)
  {
    return getIndentY (eVertAlign, fAvailableHeight, getPreparedSize ().getHeight ());
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * provided element height as the basis for alignment.
   *
   * @param eVertAlign
   *        Vertical alignment. May not be <code>null</code>.
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @param fElementHeight
   *        The height of the element to align
   * @return The top margin of this object plus the indentation offset
   */
  protected float getIndentY (@Nonnull final EVertAlignment eVertAlign,
                              final float fAvailableHeight,
                              final float fElementHeight)
  {
    final float fTop = getMarginTop ();
    final float fUsableHeight = fAvailableHeight - getMarginYSum ();

    switch (eVertAlign)
    {
      case TOP:
        return fTop;
      case MIDDLE:
        return fTop + (fUsableHeight - fElementHeight) / 2;
      case BOTTOM:
        return fTop + fUsableHeight - fElementHeight;
      default:
        throw new IllegalStateException ("Unsupported vertical alignment " + eVertAlign);
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("VertAlign", m_eVertAlign)
                            .toString ();
  }
}
