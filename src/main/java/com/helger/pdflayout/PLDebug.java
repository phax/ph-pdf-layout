/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.pdflayout;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.lang.CGStringHelper;
import com.helger.pdflayout.element.AbstractPLBaseElement;

@NotThreadSafe
public final class PLDebug
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PLDebug.class);
  private static boolean s_bDebugText = GlobalDebug.isDebugMode ();
  private static boolean s_bDebugSplit = GlobalDebug.isDebugMode ();

  private PLDebug ()
  {}

  public static boolean isDebugText ()
  {
    return s_bDebugText;
  }

  public static void setDebugText (final boolean bDebugText)
  {
    s_bDebugText = bDebugText;
  }

  public static void debugText (final String sMsg)
  {
    s_aLogger.info ("Text: " + sMsg);
  }

  public static boolean isDebugSplit ()
  {
    return s_bDebugSplit;
  }

  public static void setDebugSplit (final boolean bDebugSplit)
  {
    s_bDebugSplit = bDebugSplit;
  }

  public static void debugSplit (@Nonnull final AbstractPLBaseElement <?> aElement, final String sMsg)
  {
    s_aLogger.info ("[Splitting] " +
                    CGStringHelper.getClassLocalName (aElement) +
                    "-" +
                    aElement.getID () +
                    ": " +
                    sMsg);
  }
}
