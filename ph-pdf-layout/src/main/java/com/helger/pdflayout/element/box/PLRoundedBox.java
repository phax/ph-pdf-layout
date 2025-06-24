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
package com.helger.pdflayout.element.box;

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorderRounded;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageRenderContext;

/**
 * A rounded box is a simple block element that encapsulates another element and has a padding,
 * border and margin etc. itself, plus a rounded border radius.
 *
 * @author Philip Helger
 * @author Marco De Angelis
 * @since 7.4.1
 */
public class PLRoundedBox extends AbstractPLBox <PLRoundedBox>
{
  public static final float DEFAULT_BORDER_RADIUS = 8.0f;

  private float m_fBorderRadius = DEFAULT_BORDER_RADIUS;

  public PLRoundedBox ()
  {
    super (null);
  }

  public PLRoundedBox (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLRoundedBox setBasicDataFrom (@Nonnull final PLRoundedBox aSource)
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
  public PLRoundedBox setBorderRadius (@Nonnegative final float fBorderRadius)
  {
    ValueEnforcer.isGT0 (fBorderRadius, "BorderRadius");
    m_fBorderRadius = fBorderRadius;
    return this;
  }

  @Override
  @Nonnull
  public PLRoundedBox internalCreateNewVertSplitObject (@Nonnull final PLRoundedBox aBase)
  {
    final PLRoundedBox ret = new PLRoundedBox ();
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
  protected void clipShape (@Nonnull final PageRenderContext aCtx,
                            final float fLeft,
                            final float fBottom,
                            final float fWidth,
                            final float fHeight) throws IOException
  {
    final PDPageContentStreamWithCache aCSWC = aCtx.getContentStream ();
    aCSWC.saveGraphicsState ();
    aCSWC.drawRoundedRect (fLeft,
                           fBottom,
                           fWidth,
                           fHeight,
                           m_fBorderRadius,
                           m_fBorderRadius,
                           m_fBorderRadius,
                           m_fBorderRadius);
    aCSWC.clip ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("BorderRadius", m_fBorderRadius).getToString ();
  }
}
