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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.id.factory.GlobalIDFactory;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.debug.PLDebugLog;

/**
 * Abstract PL object
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLObject <IMPLTYPE extends AbstractPLObject <IMPLTYPE>> implements IPLObject <IMPLTYPE>
{
  private String m_sElementID;
  // The ID of the original, unsplit ancestor. <code>null</code> means this
  // object was never produced by a vertical split.
  private String m_sOriginalID;
  // <code>true</code> if this object is reachable from the original ancestor
  // by always picking the first (top) fragment. Unsplit objects are first
  // fragments of themselves.
  private boolean m_bFirstFragment = true;

  // Status variable
  private transient String m_sDebugID;

  public AbstractPLObject ()
  {
    // Assign a default ID
    m_sElementID = GlobalIDFactory.getNewStringID ();
  }

  /**
   * @return The unique element ID. Never <code>null</code>. By default this ID is automatically
   *         generated, by it might be overridden by {@link #setID(String)}.
   */
  public final String getID ()
  {
    return m_sElementID;
  }

  /**
   * Callback invoked after an ID change. Overwrite this method to do local actions (if needed)
   *
   * @param sOldElementID
   *        the previous element ID. May be <code>null</code>.
   */
  @OverrideOnDemand
  protected void onAfterSetID (@Nullable final String sOldElementID)
  {}

  /**
   * Set the ID of this element. This methods calls <code>onAfterSetID</code> after any change, even
   * if the values were the same.
   *
   * @param sID
   *        The new ID to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setID (@NonNull @Nonempty final String sID)
  {
    ValueEnforcer.notEmpty (sID, "ID");

    final String sOldElementID = m_sElementID;

    // If debug ID is set, it means that the ID was most likely already "used"
    // or "displayed", so we're using this as a usage indicator
    if (m_sDebugID != null)
    {
      if (PLDebugLog.isDebugConsistency ())
        PLDebugLog.debugConsistency (this, "Overwriting ID '" + m_sElementID + "' with ID '" + sID + "'");

      // Clear cached value
      m_sDebugID = null;
    }
    m_sElementID = sID;

    // Call callback afterwards
    onAfterSetID (sOldElementID);
    return thisAsT ();
  }

  @NonNull
  @Nonempty
  public final String getDebugID ()
  {
    String ret = m_sDebugID;
    if (ret == null)
      m_sDebugID = ret = "<" + ClassHelper.getClassLocalName (this) + "-" + getID () + ">";
    return ret;
  }

  /**
   * {@inheritDoc} For unsplit instances this returns the same value as {@link #getID()}; for
   * fragments produced via {@link #internalMarkAsSplitFragment(IPLObject, boolean, String)} it
   * returns the ID of the original ancestor.
   *
   * @return The original (unsplit) ID. Never <code>null</code> and never empty.
   * @since 8.2.0
   */
  @Override
  @NonNull
  @Nonempty
  public final String getOriginalID ()
  {
    return m_sOriginalID != null ? m_sOriginalID : m_sElementID;
  }

  /**
   * {@inheritDoc}
   *
   * @return <code>true</code> if this object was produced by a call to
   *         {@link #internalMarkAsSplitFragment(IPLObject, boolean, String)}, <code>false</code>
   *         otherwise.
   * @since 8.2.0
   */
  @Override
  public final boolean isSplitFragment ()
  {
    return m_sOriginalID != null;
  }

  /**
   * {@inheritDoc}
   *
   * @return <code>true</code> if this object is the top-most slice of its original ancestor (or an
   *         unsplit original, which is always the first fragment of itself), <code>false</code>
   *         otherwise.
   * @since 8.2.0
   */
  @Override
  public final boolean isFirstFragment ()
  {
    return m_bFirstFragment;
  }

  /**
   * Mark this object as a fragment produced by vertically splitting another
   * object. Carries the original (unsplit) ancestor's ID forward and tracks
   * whether this fragment is the top-most slice of the original. Must be called
   * once per fragment, immediately after creation, at every split site.
   *
   * @param aSplitSource
   *        The object that was just split to produce this fragment. May not be
   *        <code>null</code>.
   * @param bThisIsFirstHalf
   *        <code>true</code> if this is the first (top) half of the split,
   *        <code>false</code> if it is the second (bottom) half.
   * @param sIDSuffix
   *        Suffix appended to the split source's ID to form this fragment's
   *        ID. Must not be empty.
   * @return this for chaining
   * @since 8.2.0
   */
  @NonNull
  protected final IMPLTYPE internalMarkAsSplitFragment (@NonNull final IPLObject <?> aSplitSource,
                                                        final boolean bThisIsFirstHalf,
                                                        @NonNull @Nonempty final String sIDSuffix)
  {
    ValueEnforcer.notNull (aSplitSource, "SplitSource");
    ValueEnforcer.notEmpty (sIDSuffix, "IDSuffix");

    setID (aSplitSource.getID () + sIDSuffix);
    m_sOriginalID = aSplitSource.getOriginalID ();
    m_bFirstFragment = aSplitSource.isFirstFragment () && bThisIsFirstHalf;
    return thisAsT ();
  }

  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    // Nothing to do here
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ElementID", m_sElementID)
                                       .appendIfNotNull ("OriginalID", m_sOriginalID)
                                       .append ("FirstFragment", m_bFirstFragment)
                                       .getToString ();
  }
}
