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
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
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
  private SizeSpec m_aMinSize = DEFAULT_MIN_SIZE;
  private SizeSpec m_aMaxSize = DEFAULT_MAX_SIZE;
  private boolean m_bPrepared = false;
  private SizeSpec m_aPreparedSize;

  public AbstractPLRenderableObject ()
  {}

  @Nonnull
  public SizeSpec getMinSize ()
  {
    return m_aMinSize;
  }

  @Nonnull
  public IMPLTYPE setMinSize (@Nonnegative final float fMinWidth, @Nonnegative final float fMinHeight)
  {
    m_aMinSize = new SizeSpec (fMinWidth, fMinHeight);
    return thisAsT ();
  }

  @Nonnull
  public SizeSpec getMaxSize ()
  {
    return m_aMaxSize;
  }

  @Nonnull
  public IMPLTYPE setMaxSize (@Nonnegative final float fMaxWidth, @Nonnegative final float fMaxHeight)
  {
    m_aMaxSize = new SizeSpec (fMaxWidth, fMaxHeight);
    return thisAsT ();
  }

  /**
   * Throw an exception, if this object is already prepared.
   *
   * @throws IllegalStateException
   *         if already prepared
   */
  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  protected void internalCheckNotPrepared ()
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
   */
  @Nullable
  public final SizeSpec getPreparedSize ()
  {
    return m_aPreparedSize;
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
   *         on error
   */
  @Nonnull
  protected abstract SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException;

  /**
   * @param aPreparedSize
   *        Prepared size without padding and margin.
   */
  private final void _setPreparedSize (@Nonnull final SizeSpec aPreparedSize)
  {
    ValueEnforcer.notNull (aPreparedSize, "PreparedSize");

    // Consider min size here
    float fRealWidth = Math.max (m_aMinSize.getWidth (), aPreparedSize.getWidth ());
    float fRealHeight = Math.max (m_aMinSize.getHeight (), aPreparedSize.getHeight ());

    // Consider max size here
    fRealWidth = Math.min (m_aMaxSize.getWidth (), fRealWidth);
    fRealHeight = Math.min (m_aMaxSize.getHeight (), fRealHeight);

    m_bPrepared = true;
    m_aPreparedSize = new SizeSpec (fRealWidth, fRealHeight);

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
    final SizeSpec aOnPrepareResult = onPrepare (aCtx);
    _setPreparedSize (aOnPrepareResult);

    return m_aPreparedSize;
  }

  public final void internalMarkAsNotPrepared ()
  {
    m_aPreparedSize = null;
    m_bPrepared = false;
  }

  /**
   * @param aPreparedSize
   *        The new prepared size without padding or margin.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE internalMarkAsPrepared (@Nonnull final SizeSpec aPreparedSize)
  {
    // Prepare only once!
    internalCheckNotPrepared ();

    _setPreparedSize (aPreparedSize);
    return thisAsT ();
  }

  /**
   * method to be implemented by subclasses. Should fill the surrounding and
   * create the border.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   */
  @OverrideOnDemand
  protected void onPerformFillAndBorder (@Nonnull final RenderingContext aCtx) throws IOException
  {}

  /**
   * Abstract method to be implemented by subclasses.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   */
  @OverrideOnDemand
  protected abstract void onPerform (@Nonnull RenderingContext aCtx) throws IOException;

  @Nonnegative
  public final void perform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    if (!m_bPrepared)
      throw new IllegalStateException ("Element " + ClassHelper.getClassLocalName (this) + " was never prepared!");

    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this,
                           "Rendering at " +
                                 PLDebug.getXYWH (aCtx.getStartLeft (),
                                                  aCtx.getStartTop (),
                                                  m_aPreparedSize.getWidth (),
                                                  m_aPreparedSize.getHeight ()));

    // Fill and render border
    onPerformFillAndBorder (aCtx);

    // Main perform after border
    onPerform (aCtx);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("minSize", m_aMinSize)
                            .append ("maxSize", m_aMaxSize)
                            .append ("prepared", m_bPrepared)
                            .appendIfNotNull ("preparedSize", m_aPreparedSize)
                            .toString ();
  }
}
