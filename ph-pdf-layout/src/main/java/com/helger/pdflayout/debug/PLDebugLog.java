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
package com.helger.pdflayout.debug;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.pdflayout.base.IPLHasMarginBorderPadding;
import com.helger.pdflayout.base.IPLObject;
import com.helger.pdflayout.spec.EPLRotate;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * This class allows for some debug logging on PDF creation.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PLDebugLog
{
  /**
   * Debug log output abstraction.
   *
   * @author Philip Helger
   * @since 5.1.0
   */
  public interface IPLDebugOutput
  {
    boolean isEnabled ();

    void log (@NonNull String sMsg);
  }

  /**
   * Default implementation of {@link IPLDebugOutput} using an SLF4J logger.
   *
   * @author Philip Helger
   * @since 5.1.0
   */
  public static class PLDebugOutputLogger implements IPLDebugOutput
  {
    public static final PLDebugOutputLogger INSTANCE = new PLDebugOutputLogger ();

    private static final Logger LOGGER = LoggerFactory.getLogger (PLDebugOutputLogger.class);

    public boolean isEnabled ()
    {
      return LOGGER.isInfoEnabled ();
    }

    public void log (final String sMsg)
    {
      LOGGER.info (sMsg);
    }
  }

  public static final boolean DEFAULT_DEBUG = false;

  private static IPLDebugOutput s_aDebugOutput = PLDebugOutputLogger.INSTANCE;
  private static boolean s_bDebugText = DEFAULT_DEBUG;
  private static boolean s_bDebugFont = DEFAULT_DEBUG;
  private static boolean s_bDebugSplit = DEFAULT_DEBUG;
  private static boolean s_bDebugPrepare = DEFAULT_DEBUG;
  private static boolean s_bDebugRender = DEFAULT_DEBUG;
  private static boolean s_bDebugConsistency = DEFAULT_DEBUG;

  private PLDebugLog ()
  {}

  @NonNull
  public static IPLDebugOutput getDebugOutput ()
  {
    return s_aDebugOutput;
  }

  public static void setDebugOutput (@NonNull final IPLDebugOutput aDebugOutput)
  {
    ValueEnforcer.notNull (aDebugOutput, "DebugOutput");
    s_aDebugOutput = aDebugOutput;
  }

  public static boolean isDebugText ()
  {
    return s_bDebugText;
  }

  public static void setDebugText (final boolean bDebugText)
  {
    s_bDebugText = bDebugText;
  }

  public static void debugText (@NonNull final IPLObject <?> aElement, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Text] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugFont ()
  {
    return s_bDebugFont;
  }

  public static void setDebugFont (final boolean bDebugFont)
  {
    s_bDebugFont = bDebugFont;
  }

  public static void debugFont (@NonNull final String sFontID, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Font] " + sFontID + " " + sMsg);
  }

  public static boolean isDebugSplit ()
  {
    return s_bDebugSplit;
  }

  public static void setDebugSplit (final boolean bDebugSplit)
  {
    s_bDebugSplit = bDebugSplit;
  }

  public static void debugSplit (@NonNull final IPLObject <?> aElement, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Splitting] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugPrepare ()
  {
    return s_bDebugPrepare;
  }

  public static void setDebugPrepare (final boolean bDebugPrepare)
  {
    s_bDebugPrepare = bDebugPrepare;
  }

  public static void debugPrepare (@NonNull final IPLObject <?> aElement, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Preparing] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugRender ()
  {
    return s_bDebugRender;
  }

  public static void setDebugRender (final boolean bDebugRender)
  {
    s_bDebugRender = bDebugRender;
  }

  public static void debugRender (@NonNull final IPLObject <?> aElement, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Rendering] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugConsistency ()
  {
    return s_bDebugConsistency;
  }

  public static void setDebugConsistency (final boolean bDebugConsistency)
  {
    s_bDebugConsistency = bDebugConsistency;
  }

  public static void debugConsistency (@NonNull final IPLObject <?> aElement, final String sMsg)
  {
    if (s_aDebugOutput.isEnabled ())
      s_aDebugOutput.log ("[Consistency] " + aElement.getDebugID () + " " + sMsg);
  }

  /**
   * Shortcut to globally en- or disable debugging
   *
   * @param bDebug
   *        <code>true</code> to enable debug output
   * @see #setDebugFont(boolean)
   * @see #setDebugPrepare(boolean)
   * @see #setDebugRender(boolean)
   * @see #setDebugRender(boolean)
   * @see #setDebugText(boolean)
   * @see #setDebugConsistency(boolean)
   */
  public static void setDebugAll (final boolean bDebug)
  {
    setDebugFont (bDebug);
    setDebugPrepare (bDebug);
    setDebugRender (bDebug);
    setDebugSplit (bDebug);
    setDebugText (bDebug);
    setDebugConsistency (bDebug);
  }

  @NonNull
  public static String getXY (final float fX, final float fY)
  {
    return "[" + fX + "/" + fY + "]";
  }

  @NonNull
  public static String getWH (@NonNull final SizeSpec aSize)
  {
    return getWH (aSize.getWidth (), aSize.getHeight ());
  }

  @NonNull
  public static String getWH (final float fWidth, final float fHeight)
  {
    return fWidth + "/" + fHeight;
  }

  @NonNull
  public static String getXYWH (final float fLeft, final float fTop, final float fWidth, final float fHeight)
  {
    final float fRight = fLeft + fWidth;
    final float fBottom = fTop - fHeight;
    return "LB" + getXY (fLeft, fBottom) + " - RT" + getXY (fRight, fTop) + " (=WH " + getWH (fWidth, fHeight) + ")";
  }

  @NonNull
  public static String getXMBP (@NonNull final IPLHasMarginBorderPadding <?> aElement)
  {
    return "[X-MBP: " +
           aElement.getMarginXSum () +
           "/" +
           aElement.getBorderXSumWidth () +
           "/" +
           aElement.getPaddingXSum () +
           "=" +
           aElement.getOutlineXSum () +
           "]";
  }

  @NonNull
  public static String getYMBP (@NonNull final IPLHasMarginBorderPadding <?> aElement)
  {
    return "[Y-MBP: " +
           aElement.getMarginYSum () +
           "/" +
           aElement.getBorderYSumWidth () +
           "/" +
           aElement.getPaddingYSum () +
           "=" +
           aElement.getOutlineYSum () +
           "]";
  }

  @NonNull
  public static String getRotationIfPresent (@NonNull final EPLRotate eRotate)
  {
    return eRotate.isRotate0 () ? "" : "; Rotation: [" + eRotate.getAngleDegrees () + "°]";
  }
}
