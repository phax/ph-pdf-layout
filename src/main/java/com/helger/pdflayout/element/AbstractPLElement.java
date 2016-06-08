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
package com.helger.pdflayout.element;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageSetupContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Abstract layout element that supports rendering.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLElement <IMPLTYPE extends AbstractPLElement <IMPLTYPE>>
                                        extends AbstractPLBaseElement <IMPLTYPE>
{
  private SizeSpec m_aMinSize = SizeSpec.SIZE0;
  private SizeSpec m_aMaxSize = new SizeSpec (Float.MAX_VALUE, Float.MAX_VALUE);
  private boolean m_bPrepared = false;
  private SizeSpec m_aPreparedSize;

  public AbstractPLElement ()
  {}

  /**
   * @return The minimum size to be used. Excluding padding and margin. Never
   *         <code>null</code>.
   */
  @Nonnull
  public SizeSpec getMinSize ()
  {
    return m_aMinSize;
  }

  /**
   * Set the minimum size to be used. Excluding padding and margin.
   *
   * @param fMinWidth
   *        Minimum width. Must be &ge; 0.
   * @param fMinHeight
   *        Minimum height. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setMinSize (@Nonnegative final float fMinWidth, @Nonnegative final float fMinHeight)
  {
    m_aMinSize = new SizeSpec (fMinWidth, fMinHeight);
    return thisAsT ();
  }

  /**
   * @return The maximum size to be used. Excluding padding and margin. Never
   *         <code>null</code>.
   */
  @Nonnull
  public SizeSpec getMaxSize ()
  {
    return m_aMaxSize;
  }

  /**
   * Set the maximum size to be used. Excluding padding and margin.
   *
   * @param fMaxWidth
   *        Maximum width. Must be &ge; 0.
   * @param fMaxHeight
   *        Maximum height. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setMaxSize (@Nonnegative final float fMaxWidth, @Nonnegative final float fMaxHeight)
  {
    m_aMaxSize = new SizeSpec (fMaxWidth, fMaxHeight);
    return thisAsT ();
  }

  /**
   * Set the exact size to be used. Excluding padding and margin. This is a
   * shortcut for setting minimum and maximum size to the same values.
   *
   * @param fWidth
   *        Width to use. Must be &ge; 0.
   * @param fHeight
   *        Height to use. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setExactSize (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    setMinSize (fWidth, fHeight);
    return setMaxSize (fWidth, fHeight);
  }

  /**
   * Throw an exception, if this object is already prepared.
   *
   * @throws IllegalStateException
   *         if already prepared
   */
  @Override
  protected final void checkNotPrepared ()
  {
    if (isPrepared ())
      throw new IllegalStateException ("This object was already prepared and can therefore not be modified: " +
                                       toString ());
  }

  /**
   * @return <code>true</code> if this object was already prepared,
   *         <code>false</code> otherwise.
   */
  protected final boolean isPrepared ()
  {
    return m_bPrepared;
  }

  /**
   * @return The prepared size or <code>null</code> if this object was not yet
   *         prepared.
   */
  @Nullable
  protected final SizeSpec getPreparedSize ()
  {
    return m_aPreparedSize;
  }

  /**
   * The abstract method that must be implemented by all subclasses. It is
   * ensured that this method is called only once per instance!
   *
   * @param aCtx
   *        Preparation context. Never <code>null</code>.
   * @return The size of the rendered element without padding or margin. May not
   *         be <code>null</code>.
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
      PLDebug.debugPrepare (this,
                            "Prepared object: width=" +
                                  aPreparedSize.getWidth () +
                                  "+" +
                                  getFullXSum () +
                                  " & height=" +
                                  aPreparedSize.getHeight () +
                                  "+" +
                                  getFullYSum ());
  }

  /**
   * Prepare this element once for rendering.
   *
   * @param aCtx
   *        The preparation context
   * @return The net size of the rendered object without padding or margin. May
   *         not be <code>null</code>.
   * @throws IOException
   *         if already prepared
   */
  @Nonnull
  public final SizeSpec prepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    // Prepare only once!
    checkNotPrepared ();

    if (PLDebug.isDebugPrepare ())
      PLDebug.debugPrepare (this,
                            "Preparing object for available width " +
                                  aCtx.getAvailableWidth () +
                                  "+" +
                                  getFullXSum () +
                                  " and available height " +
                                  aCtx.getAvailableHeight () +
                                  "+" +
                                  getFullYSum ());

    // Do prepare
    final SizeSpec aOnPrepareResult = onPrepare (aCtx);
    _setPreparedSize (aOnPrepareResult);

    return m_aPreparedSize;
  }

  protected final void markAsNotPrepared ()
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
  protected final IMPLTYPE markAsPrepared (@Nonnull final SizeSpec aPreparedSize)
  {
    // Prepare only once!
    checkNotPrepared ();

    _setPreparedSize (aPreparedSize);
    return thisAsT ();
  }

  /**
   * Called after the page was created but before the content stream is created.
   *
   * @param aCtx
   *        The current page setup context. Never <code>null</code>.
   */
  @OverrideOnDemand
  public void doPageSetup (@Nonnull final PageSetupContext aCtx)
  {}

  /**
   * Abstract method to be implemented by subclasses.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   */
  @Nonnegative
  protected abstract void onPerform (@Nonnull RenderingContext aCtx) throws IOException;

  /**
   * Second step: perform.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   */
  @Nonnegative
  public final void perform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    if (!m_bPrepared)
      throw new IllegalStateException ("Element " + ClassHelper.getClassLocalName (this) + " was never prepared!");

    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this, "rendering at " + aCtx.getStartLeft () + " & " + aCtx.getStartTop ());

    // Render border - debug: green
    {
      final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
      final float fLeft = aCtx.getStartLeft ();
      final float fTop = aCtx.getStartTop ();
      final float fWidth = m_aPreparedSize.getWidth () + getPaddingXSum ();
      final float fHeight = m_aPreparedSize.getHeight () + getPaddingYSum ();

      // Fill before border
      if (getFillColor () != null)
      {
        aContentStream.setNonStrokingColor (getFillColor ());
        aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
      }

      BorderSpec aRealBorder = getBorder ();
      if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
        aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_ELEMENT));
      if (aRealBorder.hasAnyBorder ())
        renderBorder (aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
    }

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
