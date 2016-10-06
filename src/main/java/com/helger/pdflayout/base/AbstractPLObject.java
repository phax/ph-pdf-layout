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

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;
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
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLObject.class);

  private String m_sElementID;
  private transient String m_sDebugID;

  public AbstractPLObject ()
  {
    // Assign a default ID
    m_sElementID = GlobalIDFactory.getNewStringID ();
  }

  /**
   * @return The unique element ID. Never <code>null</code>.
   */
  public final String getID ()
  {
    return m_sElementID;
  }

  /**
   * Override this method when interested in an ID change.
   */
  @OverrideOnDemand
  protected void onIDChange ()
  {}

  @Nonnull
  public final IMPLTYPE setID (@Nonnull @Nonempty final String sID)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    if (StringHelper.hasText (m_sElementID) && m_sDebugID != null)
    {
      s_aLogger.warn ("Overwriting ID '" + m_sElementID + "' with ID '" + sID + "'");
      // Disable caching
      m_sDebugID = null;
    }
    m_sElementID = sID;
    onIDChange ();
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

  /**
   * For copying stuff internally. Must always call super method
   *
   * @param aSource
   *        Source object to copy from. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLObject <?> aSource)
  {
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("id", m_sElementID).toString ();
  }
}
