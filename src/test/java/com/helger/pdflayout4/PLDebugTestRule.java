/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4;

import org.junit.rules.ExternalResource;

import com.helger.pdflayout4.debug.PLDebugLog;
import com.helger.pdflayout4.debug.PLDebugLog.IPLDebugOutput;

public class PLDebugTestRule extends ExternalResource
{
  /** When this rule is used, global debug is enabled. */
  public static final boolean ENABLE_DEBUG = true;

  private static final IPLDebugOutput NO_OP = new IPLDebugOutput ()
  {
    public boolean isEnabled ()
    {
      return true;
    }

    public void log (final String sMsg)
    {
      // Ignore
    }
  };

  private final boolean m_bDebug;
  private boolean m_bOldDebug;

  public PLDebugTestRule ()
  {
    this (ENABLE_DEBUG);
  }

  public PLDebugTestRule (final boolean bDebug)
  {
    m_bDebug = bDebug;
  }

  @Override
  public void before () throws Throwable
  {
    // Remember old states
    m_bOldDebug = PLDebugLog.isDebugFont ();

    // Init debug stuff to state specified in ctor
    PLDebugLog.setDebugAll (m_bDebug);
    PLDebugLog.setDebugOutput (NO_OP);
  }

  @Override
  public void after ()
  {
    // Reset debug stuff to previous state
    PLDebugLog.setDebugAll (m_bOldDebug);
    PLDebugLog.setDebugOutput (new PLDebugLog.PLDebugOutputLogger ());
  }
}
