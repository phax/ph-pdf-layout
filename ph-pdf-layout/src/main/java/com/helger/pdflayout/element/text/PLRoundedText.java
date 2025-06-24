/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.text;

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorderRounded;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.spec.FontSpec;

/**
 * Render text in a rounded rectangle.
 *
 * @author Philip Helger
 * @author Marco De Angelis
 * @since 7.4.1
 */
public class PLRoundedText extends AbstractPLText <PLRoundedText>
{
  public static final float DEFAULT_BORDER_RADIUS = 8.0f;

  private float m_fBorderRadius = DEFAULT_BORDER_RADIUS;

  public PLRoundedText (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    super (sText, aFontSpec);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLRoundedText setBasicDataFrom (@Nonnull final PLRoundedText aSource)
  {
    super.setBasicDataFrom (aSource);
    setBorderRadius (aSource.getBorderRadius ());
    return thisAsT ();
  }

  public float getBorderRadius ()
  {
    return m_fBorderRadius;
  }

  @Nonnull
  public PLRoundedText setBorderRadius (@Nonnegative final float fBorderRadius)
  {
    ValueEnforcer.isGT0 (fBorderRadius, "BorderRadius");
    m_fBorderRadius = fBorderRadius;
    return this;
  }

  @Override
  @Nonnull
  public PLRoundedText internalCreateNewVertSplitObject (@Nonnull final PLRoundedText aBase)
  {
    final PLRoundedText ret = new PLRoundedText (null, aBase.getFontSpec ());
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void renderShape (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    fillAndRenderBorderRounded (thisAsT (),
                                aCtx,
                                0f,
                                0f,
                                m_fBorderRadius,
                                m_fBorderRadius,
                                m_fBorderRadius,
                                m_fBorderRadius);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("BorderRadius", m_fBorderRadius).getToString ();
  }
}
