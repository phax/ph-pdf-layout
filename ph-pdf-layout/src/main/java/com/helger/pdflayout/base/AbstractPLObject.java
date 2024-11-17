/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract PL object
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLObject <IMPLTYPE extends AbstractPLObject <IMPLTYPE>> implements IPLObject <IMPLTYPE>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractPLObject.class);

  private String m_sElementID;

  // Status variable
  private transient String m_sDebugID;

  public AbstractPLObject ()
  {
    // Assign a default ID
    m_sElementID = GlobalIDFactory.getNewStringID ();
  }

  /**
   * @return The unique element ID. Never <code>null</code>. By default this ID
   *         is automatically generated, by it might be overridden by
   *         {@link #setID(String)}.
   */
  public final String getID ()
  {
    return m_sElementID;
  }

  /**
   * Callback invoked after an ID change. Overwrite this method to do local
   * actions (if needed)
   */
  @OverrideOnDemand
  protected void onAfterSetID ()
  {}

  /**
   * Set the ID of this element. This methods calls <code>onAfterSetID</code>
   * after any change, even if the values were the same.
   *
   * @param sID
   *        The new ID to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setID (@Nonnull @Nonempty final String sID)
  {
    ValueEnforcer.notEmpty (sID, "ID");

    // String sOldElementID = m_sElementID;

    // If debug ID is set, it means that the ID was most likely already "used"
    // or "displayed", so we're using this as a usage indicator
    if (m_sDebugID != null)
    {
      LOGGER.warn ("Overwriting ID '" + m_sElementID + "' with ID '" + sID + "'");
      // Clear cached value
      m_sDebugID = null;
    }
    m_sElementID = sID;

    // Call callback afterwards
    onAfterSetID ();
    return thisAsT ();
  }

  @Nonnull
  @Nonempty
  public final String getDebugID ()
  {
    String ret = m_sDebugID;
    if (ret == null)
      m_sDebugID = ret = "<" + ClassHelper.getClassLocalName (this) + "-" + getID () + ">";
    return ret;
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    // Nothing to do here
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ElementID", m_sElementID).getToString ();
  }
}
