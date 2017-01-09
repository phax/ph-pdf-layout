/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.base;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.SizeSpec;

/**
 * Abstract layout object that supports rendering.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLRenderableObject <IMPLTYPE extends AbstractPLRenderableObject <IMPLTYPE>> extends
                                                 AbstractPLObject <IMPLTYPE> implements IPLRenderableObject <IMPLTYPE>
{
  private boolean m_bPrepared = false;
  private SizeSpec m_aPrepareAvailableSize;
  private SizeSpec m_aPreparedSize;
  private SizeSpec m_aRenderSize;

  public AbstractPLRenderableObject ()
  {}

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    return thisAsT ();
  }

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  @Nonnull
  public EChange visit (@Nonnull final IPLVisitor aVisitor) throws IOException
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
   * @return <code>true</code> if this object was already prepared,
   *         <code>false</code> otherwise.
   */
  public final boolean isPrepared ()
  {
    return m_bPrepared;
  }

  /**
   * @return The size used to perform the preparation. Is <code>null</code> if
   *         this object was not yet prepared. This is required, if a text needs
   *         to be prepared more than once (e.g. text with placeholders).
   */
  @Nullable
  protected final SizeSpec getPrepareAvailableSize ()
  {
    return m_aPrepareAvailableSize;
  }

  /**
   * @return The prepared size or <code>null</code> if this object was not yet
   *         prepared.
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
   * The abstract method that must be implemented by all subclasses. It is
   * ensured that this method is called only once per instance!
   *
   * @param aCtx
   *        Preparation context. Never <code>null</code>.
   * @return The size of the rendered element without padding, border and
   *         margin. May not be <code>null</code>.
   */
  @Nonnull
  protected abstract SizeSpec onPrepare (@Nonnull final PreparationContext aCtx);

  /**
   * Overwrite this method to adopt prepared sizes (e.g. for min or max size) to
   * get the render size.
   *
   * @param aPreparedSize
   *        The originally prepared size.
   * @return The modified prepared size or the unchanged prepared size if no
   *         changes are necessary. May not be <code>null</code>.
   */
  @Nonnull
  @OverrideOnDemand
  protected SizeSpec getRenderSize (@Nonnull final SizeSpec aPreparedSize)
  {
    return aPreparedSize;
  }

  /**
   * Set the prepared size of this object. This method also handles min and max
   * size
   *
   * @param aPreparedSize
   *        Prepared size without padding and margin.
   */
  private void _setPreparedSize (@Nonnull final SizeSpec aPreparedSize)
  {
    ValueEnforcer.notNull (aPreparedSize, "PreparedSize");

    m_bPrepared = true;
    m_aPreparedSize = aPreparedSize;
    m_aRenderSize = getRenderSize (aPreparedSize);

    if (PLDebug.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof IPLHasMarginBorderPadding <?>)
      {
        sSuffix = " with " +
                  PLDebug.getXMBP ((IPLHasMarginBorderPadding <?>) this) +
                  " and " +
                  PLDebug.getYMBP ((IPLHasMarginBorderPadding <?>) this);
      }
      PLDebug.debugPrepare (this,
                            "Prepared object: " +
                                  PLDebug.getWH (aPreparedSize) +
                                  sSuffix +
                                  "; Render size: " +
                                  PLDebug.getWH (m_aRenderSize));
    }
  }

  protected final void onRenderSizeChange ()
  {
    if (m_bPrepared)
    {
      // Recalculate, e.g. for min-max size change
      final SizeSpec aOldRenderSize = m_aRenderSize;
      m_aRenderSize = getRenderSize (m_aPreparedSize);
      if (PLDebug.isDebugPrepare ())
        PLDebug.debugPrepare (this,
                              "RenderSize changed from " +
                                    PLDebug.getWH (aOldRenderSize) +
                                    " to " +
                                    PLDebug.getWH (m_aRenderSize));
    }
  }

  @Nonnull
  public final SizeSpec prepare (@Nonnull final PreparationContext aCtx)
  {
    // Prepare only once!
    internalCheckNotPrepared ();

    if (PLDebug.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof IPLHasMarginBorderPadding <?>)
      {
        sSuffix = " with " +
                  PLDebug.getXMBP ((IPLHasMarginBorderPadding <?>) this) +
                  " and " +
                  PLDebug.getYMBP ((IPLHasMarginBorderPadding <?>) this);
      }
      PLDebug.debugPrepare (this,
                            "Preparing object for available " +
                                  PLDebug.getWH (aCtx.getAvailableWidth (), aCtx.getAvailableHeight ()) +
                                  sSuffix);
    }

    // Do prepare
    final SizeSpec aPrepResultSize = onPrepare (aCtx);
    _setPreparedSize (aPrepResultSize);

    // Remember original
    m_aPrepareAvailableSize = new SizeSpec (aCtx.getAvailableWidth (), aCtx.getAvailableHeight ());

    // Return the render size
    return m_aRenderSize;
  }

  /**
   * PL objects need to overwrite this method to reset their preparation state.
   * They also need to propagate this to their children!
   */
  protected abstract void onMarkAsNotPrepared ();

  /**
   * INTERNAL method. Do not call from outside! This resets the preparation
   * state.
   */
  protected final void internalMarkAsNotPreparedDontPropagate ()
  {
    internalCheckAlreadyPrepared ();
    m_bPrepared = false;
    m_aPreparedSize = null;
    m_aRenderSize = null;
  }

  /**
   * INTERNAL method. Do not call from outside! This resets the preparation
   * state.
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
  @Nonnull
  protected final IMPLTYPE internalMarkAsPrepared (@Nonnull final SizeSpec aPreparedSize)
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
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   */
  @OverrideOnDemand
  protected abstract void onRender (@Nonnull PageRenderContext aCtx) throws IOException;

  @Nonnegative
  public final void render (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    internalCheckAlreadyPrepared ();

    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this,
                           "Rendering at " +
                                 PLDebug.getXYWH (aCtx.getStartLeft (),
                                                  aCtx.getStartTop (),
                                                  aCtx.getWidth (),
                                                  aCtx.getHeight ()));

    // Main perform after border
    onRender (aCtx);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Prepared", m_bPrepared)
                            .appendIfNotNull ("PrepareAvailableSize", m_aPrepareAvailableSize)
                            .appendIfNotNull ("PreparedSize", m_aPreparedSize)
                            .appendIfNotNull ("RenderSize", m_aRenderSize)
                            .toString ();
  }
}
