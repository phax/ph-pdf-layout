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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.render.IPLRenderListener;
import com.helger.pdflayout.render.PLAnchorRegistry;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.tagging.EPLStructureRole;
import com.helger.pdflayout.tagging.PLTaggingContext;

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
  private String m_sAnchorName = DEFAULT_ANCHOR_NAME;
  // Tagged PDF
  private boolean m_bStructureRoleSet = false;
  private EPLStructureRole m_eStructureRole;
  private String m_sAlternateText;
  private boolean m_bArtifact = false;

  public AbstractPLRenderableObject ()
  {}

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    m_aPrepareAvailableSize = aSource.getPrepareAvailableSize ();
    // Carry the anchor name forward to split fragments so the anchor is still
    // available on the fragment that actually renders. Duplicate registration
    // is suppressed via isFirstFragment() in the render hook.
    m_sAnchorName = aSource.getAnchorName ();
    // Access the private fields via the base type
    final AbstractPLRenderableObject <?> aRealSource = aSource;
    m_bStructureRoleSet = aRealSource.m_bStructureRoleSet;
    m_eStructureRole = aRealSource.m_eStructureRole;
    m_sAlternateText = aRealSource.m_sAlternateText;
    m_bArtifact = aRealSource.m_bArtifact;
    return thisAsT ();
  }

  /**
   * Get the structure role of this element in a tagged PDF, if none was set explicitly. Override
   * this in element types that have a natural role, e.g. a text is a paragraph.
   *
   * @return The default structure role. May be <code>null</code> for an element that only groups
   *         other elements.
   * @since 8.4.0
   */
  @OverrideOnDemand
  @Nullable
  protected EPLStructureRole getDefaultStructureRole ()
  {
    return null;
  }

  /**
   * Get the structure role of this element in a tagged PDF. Only used when a tagged PDF is created.
   *
   * @return The explicitly set role, or the default role of the element type. May be
   *         <code>null</code> if this element creates no structure element - its content then
   *         belongs to the structure element of the parent.
   * @since 8.4.0
   */
  @Nullable
  public final EPLStructureRole getStructureRole ()
  {
    return m_bStructureRoleSet ? m_eStructureRole : getDefaultStructureRole ();
  }

  /**
   * Set the structure role of this element in a tagged PDF, e.g. {@link EPLStructureRole#H1} for a
   * heading.
   *
   * @param eStructureRole
   *        The role. May be <code>null</code> so that this element creates no structure element.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final IMPLTYPE setStructureRole (@Nullable final EPLStructureRole eStructureRole)
  {
    m_bStructureRoleSet = true;
    m_eStructureRole = eStructureRole;
    return thisAsT ();
  }

  /**
   * Go back to the default structure role of the element type.
   *
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final IMPLTYPE resetStructureRole ()
  {
    m_bStructureRoleSet = false;
    m_eStructureRole = null;
    return thisAsT ();
  }

  /**
   * @return The alternate text of this element in a tagged PDF, e.g. the description of an image.
   *         May be <code>null</code>.
   * @since 8.4.0
   */
  @Nullable
  public final String getAlternateText ()
  {
    return m_sAlternateText;
  }

  /**
   * Set the alternate text of this element in a tagged PDF. Figures and formulas should have one.
   *
   * @param sAlternateText
   *        The alternate text. May be <code>null</code>.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final IMPLTYPE setAlternateText (@Nullable final String sAlternateText)
  {
    m_sAlternateText = sAlternateText;
    return thisAsT ();
  }

  /**
   * @return <code>true</code> if this element and all its children are an artifact in a tagged PDF
   *         - decoration that is not part of the logical structure.
   * @since 8.4.0
   */
  public final boolean isArtifact ()
  {
    return m_bArtifact;
  }

  /**
   * Mark this element and all its children as artifact in a tagged PDF - decoration that is not
   * part of the logical structure.
   *
   * @param bArtifact
   *        <code>true</code> to mark it as artifact.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final IMPLTYPE setArtifact (final boolean bArtifact)
  {
    m_bArtifact = bArtifact;
    return thisAsT ();
  }

  /**
   * {@inheritDoc}
   *
   * @since 8.2.0
   */
  @Override
  @Nullable
  public final String getAnchorName ()
  {
    return m_sAnchorName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 8.2.0
   */
  @Override
  @NonNull
  public final IMPLTYPE setAnchorName (@Nullable final String sAnchorName)
  {
    if (sAnchorName != null)
      ValueEnforcer.notEmpty (sAnchorName, "AnchorName");
    m_sAnchorName = sAnchorName;
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
    m_aRenderSize = getRenderSize (aPreparedSize);

    if (PLDebugLog.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof final IPLHasMarginBorderPadding <?> aMBP)
      {
        sSuffix = " with " + PLDebugLog.getXMBP (aMBP) + " and " + PLDebugLog.getYMBP (aMBP);
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
      m_aRenderSize = getRenderSize (m_aPreparedSize);
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

    if (PLDebugLog.isDebugPrepare ())
    {
      String sSuffix = "";
      if (this instanceof final IPLHasMarginBorderPadding <?> aMBP)
      {
        sSuffix = " with " + PLDebugLog.getXMBP (aMBP) + " and " + PLDebugLog.getYMBP (aMBP);
      }
      PLDebugLog.debugPrepare (this,
                               "Preparing object for available " +
                                     PLDebugLog.getWH (aCtx.getAvailableWidth (), aCtx.getAvailableHeight ()) +
                                     sSuffix);
    }

    // Remember available size
    m_aPrepareAvailableSize = new SizeSpec (aCtx.getAvailableWidth (), aCtx.getAvailableHeight ());

    // Do prepare
    final SizeSpec aPrepResultSize = onPrepare (aCtx);
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
    {
      PLDebugLog.debugRender (this,
                              "Rendering at " +
                                    PLDebugLog.getXYWH (aCtx.getStartLeft (),
                                                        aCtx.getStartTop (),
                                                        aCtx.getWidth (),
                                                        aCtx.getHeight ()));
    }

    // Tagged PDF: open the structure element or artifact of this element
    final PLTaggingContext aTagging = aCtx.getContentStream ().getTagging ();
    boolean bArtifact = false;
    boolean bElement = false;
    if (aTagging != null)
    {
      if (m_bArtifact)
      {
        aTagging.beginArtifact ();
        bArtifact = true;
      }
      else
      {
        final EPLStructureRole eRole = getStructureRole ();
        if (eRole != null)
        {
          // Later fragments of a split element continue its structure element
          bElement = aTagging.beginElement (getOriginalID (), !isFirstFragment (), eRole, m_sAlternateText);
        }
      }
    }

    try
    {
      // Main perform after border
      onRender (aCtx);
    }
    finally
    {
      if (bElement)
        aTagging.endElement ();
      if (bArtifact)
        aTagging.endArtifact ();
    }

    // Notify a registered listener (if any) that this element just rendered.
    // Fires after onRender so an exception suppresses the event. Reaches every
    // nested child too, because they all flow through this same render method.
    final IPLRenderListener aListener = aCtx.getRenderListener ();
    if (aListener != null)
      aListener.onElementRendered (this, aCtx);

    // Register a PDF named destination when this element carries an anchor
    // name. Only the first fragment of a split element registers, so each
    // anchor maps to exactly one location even when the labeled element ends
    // up on multiple pages.
    if (m_sAnchorName != null && isFirstFragment ())
    {
      PLAnchorRegistry.registerNamedDestination (aCtx.getDocument (),
                                                 m_sAnchorName,
                                                 aCtx.getContentStream ().getPage (),
                                                 aCtx.getStartLeft (),
                                                 aCtx.getStartTop ());
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Prepared", m_bPrepared)
                            .appendIfNotNull ("PrepareAvailableSize", m_aPrepareAvailableSize)
                            .appendIfNotNull ("PreparedSize", m_aPreparedSize)
                            .appendIfNotNull ("RenderSize", m_aRenderSize)
                            .appendIfNotNull ("AnchorName", m_sAnchorName)
                            .getToString ();
  }
}
