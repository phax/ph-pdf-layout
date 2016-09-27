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
package com.helger.pdflayout.element.special;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A horizontal spacer
 *
 * @author Philip Helger
 */
public class PLSpacerX extends AbstractPLRenderableObject <PLSpacerX>
{
  private float m_fWidth;

  public PLSpacerX ()
  {}

  public PLSpacerX (final float fWidth)
  {
    setWidth (fWidth);
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLSpacerX setBasicDataFrom (@Nonnull final PLSpacerX aSource)
  {
    super.setBasicDataFrom (aSource);
    setWidth (aSource.m_fWidth);
    return this;
  }

  @Nonnull
  public final PLSpacerX setWidth (final float fWidth)
  {
    m_fWidth = fWidth;
    return this;
  }

  public float getWidth ()
  {
    return m_fWidth;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    // Use the fixed width
    return new SizeSpec (m_fWidth > 0 ? m_fWidth : aCtx.getAvailableWidth (), 0);
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {}

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Width", m_fWidth).toString ();
  }

  @Nonnull
  public static PLSpacerX createPrepared (final float fWidth, final float fHeight)
  {
    final PLSpacerX ret = new PLSpacerX ();
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }
}
