/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.io.IOException;

import org.apache.pdfbox.util.Matrix;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.element.text.AbstractPLText;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.EPLRotate;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Abstract layout object that supports rendering.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLRenderableObject <IMPLTYPE extends AbstractPLRenderableObject <IMPLTYPE>> extends
                                                 AbstractPLObject <IMPLTYPE> implements
                                                 IPLRenderableObject <IMPLTYPE>
{
  private boolean m_bPrepared = false;
  private SizeSpec m_aPrepareAvailableSize;
  private SizeSpec m_aPreparedSize;
  private SizeSpec m_aRenderSize;
  private EPLRotate m_eRotate = EPLRotate.DEFAULT;

  public AbstractPLRenderableObject ()
  {}

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    m_aPrepareAvailableSize = aSource.getPrepareAvailableSize ();
    setRotate (aSource.getRotate ());
    return thisAsT ();
  }

  @NonNull
  public final EPLRotate getRotate ()
  {
    return m_eRotate;
  }

  @NonNull
  public final IMPLTYPE setRotate (@NonNull final EPLRotate eRotate)
  {
    ValueEnforcer.notNull (eRotate, "Rotate");
    internalCheckNotPrepared ();
    m_eRotate = eRotate;
    return thisAsT ();
  }

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  @NonNull
  public EChange visit (@NonNull final IPLVisitor aVisitor) throws IOException
  {
    return aVisitor.onElement (this);
  }

  /**
   * Throw an exception, if this object was not yet prepared.
   *
   * @throws IllegalStateException
   *         if not yet prepared
   */
  protected final void internalCheckAlreadyPrepared ()
  {
    if (!isPrepared ())
      throw new IllegalStateException (getDebugID () + " was not yet prepared: " + toString ());
  }

  /**
   * Throw an exception, if this object is already prepared.
   *
   * @throws IllegalStateException
   *         if already prepared
   */
  protected final void internalCheckNotPrepared ()
  {
    if (isPrepared ())
      throw new IllegalStateException (getDebugID () +
                                       " was already prepared and can therefore not be modified: " +
                                       toString ());
  }

  /**
   * @return <code>true</code> if this object was already prepared, <code>false</code> otherwise.
   */
  public final boolean isPrepared ()
  {
    return m_bPrepared;
  }

  /**
   * @return The size used to perform the preparation. Is <code>null</code> if this object was not
   *         yet prepared. This is required, if a text needs to be prepared more than once (e.g.
   *         text with placeholders).
   */
  @Nullable
  protected final SizeSpec getPrepareAvailableSize ()
  {
    return m_aPrepareAvailableSize;
  }

  /**
   * @return The prepared size or <code>null</code> if this object was not yet prepared.
   * @see #isPrepared()
   */
  @Nullable
  public final SizeSpec getPreparedSize ()
  {
    return m_aPreparedSize;
  }

  @Nullable
  public final SizeSpec getRenderSize ()
  {
    return m_aRenderSize;
  }

  /**
   * The abstract method that must be implemented by all subclasses. It is ensured that this method
   * is called only once per instance!
   *
   * @param aCtx
   *        Preparation context. Never <code>null</code>.
   * @return The size of the rendered element without padding, border and margin. May not be
   *         <code>null</code>.
   */
  @NonNull
  protected abstract SizeSpec onPrepare (@NonNull final PreparationContext aCtx);

  /**
   * Overwrite this method to adopt prepared sizes (e.g. for min or max size) to get the render
   * size.
   *
   * @param aPreparedSize
   *        The originally prepared size.
   * @return The modified prepared size or the unchanged prepared size if no changes are necessary.
   *         May not be <code>null</code>.
   */
  @NonNull
  @OverrideOnDemand
  protected SizeSpec getRenderSize (@NonNull final SizeSpec aPreparedSize)
  {
    return aPreparedSize;
  }

  /**
   * Set the prepared size of this object. This method also handles min and max size
   *
   * @param aPreparedSize
   *        Prepared size without padding and margin.
   */
  private void _setPreparedSize (@NonNull final SizeSpec aPreparedSize)
  {
    ValueEnforcer.notNull (aPreparedSize, "PreparedSize");

    m_bPrepared = true;
    m_aPreparedSize = aPreparedSize;

    // Apply min/max size etc.
    SizeSpec aRenderSize = getRenderSize (aPreparedSize);
    if (m_eRotate.isVertical ())
    {
      // Swap width and height for rendering
      aRenderSize = new SizeSpec (aRenderSize.getHeight (), aRenderSize.getWidth ());
    }
    m_aRenderSize = aRenderSize;

    if (PLDebugLog.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof IPLHasMarginBorderPadding <?>)
      {
        sSuffix = " with " +
                  PLDebugLog.getXMBP ((IPLHasMarginBorderPadding <?>) this) +
                  " and " +
                  PLDebugLog.getYMBP ((IPLHasMarginBorderPadding <?>) this);
      }
      PLDebugLog.debugPrepare (this,
                               "Prepared object: " +
                                     PLDebugLog.getWH (aPreparedSize) +
                                     sSuffix +
                                     "; Render size: " +
                                     PLDebugLog.getWH (m_aRenderSize));
    }
  }

  protected final void onRenderSizeChange ()
  {
    if (m_bPrepared)
    {
      // Recalculate, e.g. for min-max size change
      final SizeSpec aOldRenderSize = m_aRenderSize;
      SizeSpec aNewRenderSize = getRenderSize (m_aPreparedSize);
      if (m_eRotate.isVertical ())
      {
        // Swap width and height for rendering
        aNewRenderSize = new SizeSpec (aNewRenderSize.getHeight (), aNewRenderSize.getWidth ());
      }
      m_aRenderSize = aNewRenderSize;
      if (PLDebugLog.isDebugPrepare () && !aOldRenderSize.equals (m_aRenderSize))
        PLDebugLog.debugPrepare (this,
                                 "RenderSize changed from " +
                                       PLDebugLog.getWH (aOldRenderSize) +
                                       " to " +
                                       PLDebugLog.getWH (m_aRenderSize));
    }
  }

  @NonNull
  public final SizeSpec prepare (@NonNull final PreparationContext aCtx)
  {
    // Prepare only once!
    internalCheckNotPrepared ();

    // Handle rotation on preparation
    final PreparationContext aPrepareCtx;
    if (m_eRotate.isVertical ())
    {
      // Swap available width and height for preparation
      aPrepareCtx = new PreparationContext (aCtx.getGlobalContext (),
                                            aCtx.getAvailableHeight (),
                                            aCtx.getAvailableWidth ());
    }
    else
      aPrepareCtx = aCtx;

    if (PLDebugLog.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof IPLHasMarginBorderPadding <?>)
      {
        sSuffix = " with " +
                  PLDebugLog.getXMBP ((IPLHasMarginBorderPadding <?>) this) +
                  " and " +
                  PLDebugLog.getYMBP ((IPLHasMarginBorderPadding <?>) this);
      }
      PLDebugLog.debugPrepare (this,
                               "Preparing object for available " +
                                     PLDebugLog.getWH (aPrepareCtx.getAvailableWidth (),
                                                       aPrepareCtx.getAvailableHeight ()) +
                                     sSuffix);
    }

    // Remember available size
    m_aPrepareAvailableSize = new SizeSpec (aPrepareCtx.getAvailableWidth (), aPrepareCtx.getAvailableHeight ());

    // Do prepare
    final SizeSpec aPrepResultSize = onPrepare (aPrepareCtx);
    _setPreparedSize (aPrepResultSize);

    // Return the render size
    return m_aRenderSize;
  }

  /**
   * PL objects need to overwrite this method to reset their preparation state. They also need to
   * propagate this to their children!
   */
  protected abstract void onMarkAsNotPrepared ();

  /**
   * INTERNAL method. Do not call from outside! This resets the preparation state.
   */
  protected final void internalMarkAsNotPreparedDontPropagate ()
  {
    internalCheckAlreadyPrepared ();
    m_bPrepared = false;
    m_aPreparedSize = null;
    m_aRenderSize = null;
  }

  /**
   * INTERNAL method. Do not call from outside! This resets the preparation state.
   */
  public final void internalMarkAsNotPrepared ()
  {
    internalMarkAsNotPreparedDontPropagate ();
    onMarkAsNotPrepared ();
  }

  /**
   * INTERNAL method. Do not call from outside!
   *
   * @param aPreparedSize
   *        The new prepared size without margin, border and padding.
   * @return this
   */
  @NonNull
  protected final IMPLTYPE internalMarkAsPrepared (@NonNull final SizeSpec aPreparedSize)
  {
    // Prepare only once!
    internalCheckNotPrepared ();

    _setPreparedSize (aPreparedSize);
    return thisAsT ();
  }

  /**
   * Abstract method to be implemented by subclasses.
   *
   * @param aCtx
   *        Rendering context. Never <code>null</code>.
   * @throws IOException
   *         In case of a PDFBox error
   */
  @OverrideOnDemand
  protected abstract void onRender (@NonNull PageRenderContext aCtx) throws IOException;

  @Nonnegative
  public final void render (@NonNull final PageRenderContext aCtx) throws IOException
  {
    internalCheckAlreadyPrepared ();

    if (PLDebugLog.isDebugRender ())
      PLDebugLog.debugRender (this,
                              "Rendering at " +
                                    PLDebugLog.getXYWH (aCtx.getStartLeft (),
                                                        aCtx.getStartTop (),
                                                        aCtx.getWidth (),
                                                        aCtx.getHeight ()) +
                                    " with " +
                                    m_eRotate);

    // Main perform after border
    if (m_eRotate.isRotate0 ())
    {
      // No rotation
      onRender (aCtx);
    }
    else
    {
      // Prepare rotation
      final float fCtxX = aCtx.getStartLeft ();
      final float fCtxY = aCtx.getStartTop ();
      final float fCtxW = aCtx.getWidth ();
      final float fCtxH = aCtx.getHeight ();

      final float fPreparedW = m_aPreparedSize.getWidth ();
      final float fPreparedH = m_aPreparedSize.getHeight ();

      final boolean bIsTextObject = this instanceof AbstractPLText <?>;

      {
        final float fExpectedWidth;
        final float fExpectedHeight;
        if (m_eRotate.isHorizontal ())
        {
          fExpectedWidth = fCtxW;
          fExpectedHeight = fCtxH;
        }
        else
        {
          // Swap width and height expectation
          fExpectedWidth = fCtxH;
          fExpectedHeight = fCtxW;
        }

        // Just a warning
        if (Math.abs (fExpectedWidth - fPreparedW) > 0.1 || Math.abs (fExpectedHeight - fPreparedH) > 0.1)
        {
          PLDebugLog.debugRender (this,
                                  "Rotation artifact: " +
                                        m_eRotate +
                                        " box " +
                                        PLDebugLog.getWH (fCtxW, fCtxH) +
                                        " vs content " +
                                        PLDebugLog.getWH (fPreparedW, fPreparedH));
        }
      }

      final float fTranslateX;
      final float fTranslateY;
      final float fRotate;

      if (m_eRotate.isRotate90 ())
      {
        // 90° CW: content BL(0,0) → allocated TL(fX,fY)
        // Effective transform: T(fX,fY) × R(-90°)
        fTranslateX = fCtxX;
        fTranslateY = fCtxY;
        fRotate = -90;
      }
      else
        if (m_eRotate.isRotate180 ())
        {
          // 180°: content BL(0,0) → allocated TR(fX+fW,fY)
          // Effective transform: T(fX+fW,fY) × R(180°)
          fTranslateX = fCtxX + fPreparedW;
          fTranslateY = fCtxY;
          fRotate = 180;
        }
        else
        {
          // 270° CW (= 90° CCW): content TR(fWc,fHc) → allocated TL(fX,fY)
          // Effective transform: T(fX+fHc,fY-fWc) × R(+90°)
          fTranslateX = fCtxX + fPreparedH;
          fTranslateY = fCtxY - fPreparedW;
          fRotate = 90;
        }

      // Rotate first, then translate — two transforms achieve T(tx,ty) × R(angle)
      // because PDFBox pre-multiplies: CTM = M × CTM_old, so call order is R then T
      final Matrix aRotateMatrix = Matrix.getRotateInstance (Math.toRadians (fRotate), 0, 0);
      final Matrix aTransformMatrix = Matrix.getTranslateInstance (fTranslateX, fTranslateY);

      final PDPageContentStreamWithCache aCS = aCtx.getContentStream ();
      final PageRenderContext aNewCtx = new PageRenderContext (aCtx.getElementType (),
                                                               aCS,
                                                               0,
                                                               fPreparedH,
                                                               fPreparedW,
                                                               fPreparedH,
                                                               aRotateMatrix,
                                                               aTransformMatrix);

      if (bIsTextObject)
      {
        // Render with new context
        onRender (aNewCtx);
      }
      else
      {
        aCS.saveGraphicsState ();
        try
        {
          aCS.getContentStream ().transform (aRotateMatrix);
          aCS.getContentStream ().transform (aTransformMatrix);

          // Render with new context
          onRender (aNewCtx);
        }
        finally
        {
          aCS.restoreGraphicsState ();
        }
      }
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Rotate", m_eRotate)
                            .append ("Prepared", m_bPrepared)
                            .appendIfNotNull ("PrepareAvailableSize", m_aPrepareAvailableSize)
                            .appendIfNotNull ("PreparedSize", m_aPreparedSize)
                            .appendIfNotNull ("RenderSize", m_aRenderSize)
                            .getToString ();
  }
}
