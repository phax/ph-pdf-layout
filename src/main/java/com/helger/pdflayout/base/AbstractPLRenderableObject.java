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
package com.helger.pdflayout.base;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

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

  public AbstractPLRenderableObject ()
  {}

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    aVisitor.onElement (this);
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
   * @return The prepared size or <code>null</code> if this object was not yet
   *         prepared.
   * @see #isPrepared()
   */
  @Nullable
  public final SizeSpec getPreparedSize ()
  {
    return m_aPreparedSize;
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
   * The abstract method that must be implemented by all subclasses. It is
   * ensured that this method is called only once per instance!
   *
   * @param aCtx
   *        Preparation context. Never <code>null</code>.
   * @return The size of the rendered element without padding, border and
   *         margin. May not be <code>null</code>.
   * @throws IOException
   *         on PDFBox error
   */
  @Nonnull
  protected abstract SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException;

  @Nonnull
  protected SizeSpec adoptPreparedSize (@Nonnull final SizeSpec aSize)
  {
    return aSize;
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
    m_aPreparedSize = adoptPreparedSize (aPreparedSize);

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
                                  PLDebug.getWH (aPreparedSize.getWidth (), aPreparedSize.getHeight ()) +
                                  sSuffix);
    }
  }

  @Nonnull
  public final SizeSpec prepare (@Nonnull final PreparationContext aCtx) throws IOException
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
    _setPreparedSize (onPrepare (aCtx));

    // Remember original
    m_aPrepareAvailableSize = new SizeSpec (aCtx.getAvailableWidth (), aCtx.getAvailableHeight ());

    // Return the prepared size
    return m_aPreparedSize;
  }

  // TODO should be protected only
  public final void internalMarkAsNotPrepared ()
  {
    internalCheckAlreadyPrepared ();
    m_aPreparedSize = null;
    m_bPrepared = false;
  }

  /**
   * @param aPreparedSize
   *        The new prepared size without padding or margin.
   * @return this
   */
  // TODO should be protected only
  @Nonnull
  public final IMPLTYPE internalMarkAsPrepared (@Nonnull final SizeSpec aPreparedSize)
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
                            .toString ();
  }
}
