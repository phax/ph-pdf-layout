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

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A vertical spacer
 *
 * @author Philip Helger
 */
public class PLSpacerY extends AbstractPLRenderableObject <PLSpacerY> implements
                       IPLSplittableObject <PLSpacerY, PLSpacerY>
{
  private static final float WIDTH_ZERO = 0f;

  private float m_fHeight = -1;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  private PLSpacerY ()
  {}

  public PLSpacerY (final float fHeight)
  {
    setHeight (fHeight);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLSpacerY setBasicDataFrom (@Nonnull final PLSpacerY aSource)
  {
    super.setBasicDataFrom (aSource);
    setHeight (aSource.m_fHeight);
    setVertSplittable (aSource.m_bVertSplittable);
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
    return new SizeSpec (WIDTH_ZERO, m_fHeight > 0 ? m_fHeight : fElementHeight);
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
  public final PLSpacerY setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return this;
  }

  @Override
  @Nonnull
  public PLSpacerY internalCreateNewVertSplitObject (@Nonnull final PLSpacerY aBase)
  {
    final PLSpacerY ret = new PLSpacerY ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Nonnull
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    // Prepared height may be 0 as well
    final float fPreparedHeight = getPreparedHeight ();
    if (fPreparedHeight <= fAvailableHeight)
    {
      // Splitting makes no sense!
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this, "Splitting makes no sense, because part 2 would be empty");
      return PLSplitResult.allOnFirst ();
    }

    if (fAvailableHeight <= 0)
      return PLSplitResult.allOnSecond ();

    // Splitting should take place, but the second part is always 0 height
    final float fSpacer1Height = fAvailableHeight;
    final float fSpacer2Height = 0;

    final PLSpacerY aSpacer1 = new PLSpacerY (fSpacer1Height);
    aSpacer1.internalMarkAsPrepared (new SizeSpec (WIDTH_ZERO, fSpacer1Height));

    final PLSpacerY aSpacer2 = new PLSpacerY (fSpacer2Height);
    aSpacer2.internalMarkAsPrepared (new SizeSpec (WIDTH_ZERO, fSpacer2Height));

    return PLSplitResult.createSplit (new PLElementWithSize (aSpacer1, new SizeSpec (WIDTH_ZERO, fSpacer1Height)),
                                      new PLElementWithSize (aSpacer2, new SizeSpec (WIDTH_ZERO, fSpacer2Height)));
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
                            .append ("Height", m_fHeight)
                            .append ("VertSplittable", m_bVertSplittable)
                            .getToString ();
  }

  @Nonnull
  public static PLSpacerY createPrepared (final float fHeight)
  {
    final PLSpacerY ret = new PLSpacerY (fHeight);
    ret.prepare (new PreparationContext (null, WIDTH_ZERO, fHeight));
    return ret;
  }
}
