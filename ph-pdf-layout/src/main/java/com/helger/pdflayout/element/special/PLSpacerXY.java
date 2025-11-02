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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A two-dimensional spacer
 *
 * @author Philip Helger
 * @since 7.4.0
 */
public class PLSpacerXY extends AbstractPLRenderableObject <PLSpacerXY> implements
                        IPLSplittableObject <PLSpacerXY, PLSpacerXY>
{
  private float m_fWidth;
  private float m_fHeight;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  private PLSpacerXY ()
  {}

  public PLSpacerXY (final float fWidth, final float fHeight)
  {
    setWidth (fWidth);
    setHeight (fHeight);
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public PLSpacerXY setBasicDataFrom (@NonNull final PLSpacerXY aSource)
  {
    super.setBasicDataFrom (aSource);
    setWidth (aSource.m_fWidth);
    setHeight (aSource.m_fHeight);
    setVertSplittable (aSource.m_bVertSplittable);
    return this;
  }

  public final float getWidth ()
  {
    return m_fWidth;
  }

  @NonNull
  public final PLSpacerXY setWidth (final float fWidth)
  {
    m_fWidth = fWidth;
    return this;
  }

  public final float getHeight ()
  {
    return m_fHeight;
  }

  @NonNull
  public final PLSpacerXY setHeight (final float fHeight)
  {
    m_fHeight = fHeight;
    return this;
  }

  @Override
  protected SizeSpec onPrepare (@NonNull final PreparationContext aCtx)
  {
    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();

    // Use the fixed width
    return new SizeSpec (Math.min (m_fWidth, fElementWidth), Math.min (m_fHeight, fElementHeight));
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

  @NonNull
  public final PLSpacerXY setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return this;
  }

  @Override
  @NonNull
  public PLSpacerXY internalCreateNewVertSplitObject (@NonNull final PLSpacerXY aBase)
  {
    final PLSpacerXY ret = new PLSpacerXY ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @NonNull
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

    if (PLDebugLog.isDebugSplit ())
      PLDebugLog.debugSplit (this,
                             "Splitting " +
                                   getDebugID () +
                                   " into pieces " +
                                   fSpacer1Height +
                                   " and " +
                                   fSpacer2Height +
                                   " for available height " +
                                   fAvailableHeight);

    final PLSpacerXY aSpacer1 = new PLSpacerXY (m_fWidth, fSpacer1Height);
    aSpacer1.internalMarkAsPrepared (new SizeSpec (m_fWidth, fSpacer1Height));

    final PLSpacerXY aSpacer2 = new PLSpacerXY (m_fWidth, fSpacer2Height);
    aSpacer2.internalMarkAsPrepared (new SizeSpec (m_fWidth, fSpacer2Height));

    return PLSplitResult.createSplit (new PLElementWithSize (aSpacer1, new SizeSpec (m_fWidth, fSpacer1Height)),
                                      new PLElementWithSize (aSpacer2, new SizeSpec (m_fWidth, fSpacer2Height)));
  }

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    // Empty
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Width", m_fWidth)
                            .append ("Height", m_fHeight)
                            .append ("VertSplittable", m_bVertSplittable)
                            .getToString ();
  }

  @NonNull
  public static PLSpacerXY createPrepared (final float fWidth, final float fHeight, @Nullable String sID)
  {
    final PLSpacerXY ret = new PLSpacerXY (fWidth, fHeight);
    ret.setID (sID);
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }
}
