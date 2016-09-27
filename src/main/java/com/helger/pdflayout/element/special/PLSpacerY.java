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
 * A vertical spacer
 *
 * @author Philip Helger
 */
public class PLSpacerY extends AbstractPLRenderableObject <PLSpacerY>
{
  private float m_fHeight;

  public PLSpacerY (final float fHeight)
  {
    setHeight (fHeight);
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLSpacerY setBasicDataFrom (@Nonnull final PLSpacerY aSource)
  {
    super.setBasicDataFrom (aSource);
    setHeight (aSource.m_fHeight);
    return thisAsT ();
  }

  @Nonnull
  public PLSpacerY setHeight (final float fHeight)
  {
    m_fHeight = fHeight;
    return this;
  }

  public float getHeight ()
  {
    return m_fHeight;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    // Use the fixed height
    return new SizeSpec (aCtx.getAvailableWidth (), m_fHeight);
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {}

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("height", m_fHeight).toString ();
  }

  @Nonnull
  public static PLSpacerY createPrepared (final float fWidth, final float fHeight)
  {
    final PLSpacerY ret = new PLSpacerY (fHeight);
    ret.internalMarkAsPrepared (new SizeSpec (fWidth, fHeight));
    return ret;
  }
}
