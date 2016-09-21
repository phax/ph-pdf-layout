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
package com.helger.pdflayout;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.pdflayout.element.IPLHasMarginBorderPadding;
import com.helger.pdflayout.element.IPLObject;

@NotThreadSafe
public final class PLDebug
{
  /** red */
  public static final Color BORDER_COLOR_PAGESET = new Color (255, 0, 0);

  /** green */
  public static final Color BORDER_COLOR_ELEMENT = new Color (0, 255, 0);

  /** blue */
  public static final Color BORDER_COLOR_HBOX = new Color (0, 0, 255);

  /** pink */
  public static final Color BORDER_COLOR_VBOX = new Color (255, 0, 255);

  private static final Logger s_aLogger = LoggerFactory.getLogger (PLDebug.class);
  private static boolean s_bDebugText = false;
  private static boolean s_bDebugFont = false;
  private static boolean s_bDebugSplit = false;
  private static boolean s_bDebugPrepare = false;
  private static boolean s_bDebugRender = false;

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

  public static void debugText (@Nonnull final IPLObject <?> aElement, final String sMsg)
  {
    s_aLogger.info ("[Text] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugFont ()
  {
    return s_bDebugFont;
  }

  public static void setDebugFont (final boolean bDebugFont)
  {
    s_bDebugFont = bDebugFont;
  }

  public static void debugFont (@Nonnull final String sFontID, final String sMsg)
  {
    s_aLogger.info ("[Font] " + sFontID + " " + sMsg);
  }

  public static boolean isDebugSplit ()
  {
    return s_bDebugSplit;
  }

  public static void setDebugSplit (final boolean bDebugSplit)
  {
    s_bDebugSplit = bDebugSplit;
  }

  public static void debugSplit (@Nonnull final IPLObject <?> aElement, final String sMsg)
  {
    s_aLogger.info ("[Splitting] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugPrepare ()
  {
    return s_bDebugPrepare;
  }

  public static void setDebugPrepare (final boolean bDebugPrepare)
  {
    s_bDebugPrepare = bDebugPrepare;
  }

  public static void debugPrepare (@Nonnull final IPLObject <?> aElement, final String sMsg)
  {
    s_aLogger.info ("[Preparing] " + aElement.getDebugID () + " " + sMsg);
  }

  public static boolean isDebugRender ()
  {
    return s_bDebugRender;
  }

  public static void setDebugRender (final boolean bDebugRender)
  {
    s_bDebugRender = bDebugRender;
  }

  public static void debugRender (@Nonnull final IPLObject <?> aElement, final String sMsg)
  {
    s_aLogger.info ("[Rendering] " + aElement.getDebugID () + " " + sMsg);
  }

  /**
   * Shortcut to globally en- or disable debugging
   *
   * @param bDebug
   *        <code>true</code> to enable debug output
   */
  public static void setDebugAll (final boolean bDebug)
  {
    setDebugText (bDebug);
    setDebugFont (bDebug);
    setDebugSplit (bDebug);
    setDebugPrepare (bDebug);
    setDebugRender (bDebug);
  }

  @Nonnull
  public static String getXY (final float fX, final float fY)
  {
    return "[" + fX + "/" + fY + "]";
  }

  @Nonnull
  public static String getWH (final float fWidth, final float fHeight)
  {
    return fWidth + "/" + fHeight;
  }

  @Nonnull
  public static String getXYWH (final float fLeft, final float fTop, final float fWidth, final float fHeight)
  {
    final float fRight = fLeft + fWidth;
    final float fBottom = fTop - fHeight;
    return getXY (fLeft, fBottom) + "-" + getXY (fRight, fTop) + " (=" + getWH (fWidth, fHeight) + ")";
  }

  @Nonnull
  public static String getXMBP (@Nonnull final IPLHasMarginBorderPadding <?> aElement)
  {
    return "[X-MBP: " +
           aElement.getMarginXSum () +
           "/" +
           aElement.getBorderXSumWidth () +
           "/" +
           aElement.getPaddingXSum () +
           "=" +
           aElement.getFullXSum () +
           "]";
  }

  @Nonnull
  public static String getYMBP (@Nonnull final IPLHasMarginBorderPadding <?> aElement)
  {
    return "[Y-MBP: " +
           aElement.getMarginYSum () +
           "/" +
           aElement.getBorderYSumWidth () +
           "/" +
           aElement.getPaddingYSum () +
           "=" +
           aElement.getFullYSum () +
           "]";
  }
}
