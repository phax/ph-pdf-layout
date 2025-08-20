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
package com.helger.pdflayout.element.special;

import java.io.IOException;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

import jakarta.annotation.Nonnull;

/**
 * A horizontal spacer
 *
 * @author Philip Helger
 */
public class PLSpacerX extends AbstractPLRenderableObject <PLSpacerX> implements
                       IPLSplittableObject <PLSpacerX, PLSpacerX>
{
  private static final float HEIGHT_ZERO = 0f;

  private float m_fWidth = -1;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  public PLSpacerX ()
  {}

  public PLSpacerX (final float fWidth)
  {
    setWidth (fWidth);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLSpacerX setBasicDataFrom (@Nonnull final PLSpacerX aSource)
  {
    super.setBasicDataFrom (aSource);
    setWidth (aSource.m_fWidth);
    setVertSplittable (aSource.m_bVertSplittable);
    return this;
  }

  public final float getWidth ()
  {
    return m_fWidth;
  }

  @Nonnull
  public final PLSpacerX setWidth (final float fWidth)
  {
    m_fWidth = fWidth;
    return this;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();

    // Use the fixed width
    return new SizeSpec (m_fWidth > 0 ? m_fWidth : fElementWidth, HEIGHT_ZERO);
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    // Nada
  }

  public final boolean isVertSplittable ()
  {
    return m_bVertSplittable;
  }

  @Nonnull
  public final PLSpacerX setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return this;
  }

  @Override
  @Nonnull
  public PLSpacerX internalCreateNewVertSplitObject (@Nonnull final PLSpacerX aBase)
  {
    final PLSpacerX ret = new PLSpacerX ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Nonnull
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    // Because height is 0, it always fits on the first page
    return PLSplitResult.allOnFirst ();
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Empty
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Width", m_fWidth)
                            .append ("VertSplittable", m_bVertSplittable)
                            .getToString ();
  }

  @Nonnull
  public static PLSpacerX createPrepared (final float fWidth)
  {
    final PLSpacerX ret = new PLSpacerX (fWidth);
    ret.prepare (new PreparationContext (null, fWidth, HEIGHT_ZERO));
    return ret;
  }
}
