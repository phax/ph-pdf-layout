/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.element.special;

import com.helger.commons.string.ToStringGenerator;
import com.plenigo.pdflayout.base.AbstractPLRenderableObject;
import com.plenigo.pdflayout.render.PageRenderContext;
import com.plenigo.pdflayout.render.PreparationContext;
import com.plenigo.pdflayout.spec.SizeSpec;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

/**
 * A vertical spacer
 *
 * @author Philip Helger
 */
public class PLSpacerY extends AbstractPLRenderableObject<PLSpacerY> {
    private float m_fHeight = -1;

    public PLSpacerY() {
        this(0f);
    }

    public PLSpacerY(final float fHeight) {
        setHeight(fHeight);
    }

    @Override
    @Nonnull
    @OverridingMethodsMustInvokeSuper
    public PLSpacerY setBasicDataFrom(@Nonnull final PLSpacerY aSource)
  {
    super.setBasicDataFrom (aSource);
    setHeight (aSource.m_fHeight);
    return this;
  }

  public final float getHeight ()
  {
    return m_fHeight;
  }

  @Nonnull
  public final PLSpacerY setHeight (final float fHeight)
  {
    m_fHeight = fHeight;
    return this;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();

    // Use the fixed height
    return new SizeSpec (0, m_fHeight > 0 ? m_fHeight : fElementHeight);
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    // Nada
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {}

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Height", m_fHeight).getToString ();
  }

  @Nonnull
  public static PLSpacerY createPrepared (final float fWidth, final float fHeight)
  {
    final PLSpacerY ret = new PLSpacerY (fHeight);
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }
}
