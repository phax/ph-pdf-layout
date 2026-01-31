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
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.iface.IThrowingRunnable;
import com.helger.pdflayout.base.IPLBlockElement;
import com.helger.pdflayout.base.IPLInlineElement;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.spec.BorderStyleSpec;

/**
 * This class defines stuff for debug rendering.
 *
 * @author Philip Helger
 * @since 4.0.0-b3
 */
@NotThreadSafe
public final class PLDebugRender
{
  public static final boolean DEFAULT_DEBUG_RENDER = false;

  public static final PLColor DEFAULT_COLOR_OUTLINE_PAGESET = new PLColor (0x80, 0x80, 0x80);
  public static final PLColor DEFAULT_COLOR_OUTLINE_BLOCK_ELEMENT = new PLColor (0xa0, 0xa0, 0xa0);
  public static final PLColor DEFAULT_COLOR_OUTLINE_INLINE_ELEMENT = new PLColor (0xc0, 0xc0, 0xc0);

  /** red */
  public static final BorderStyleSpec DEFAULT_BORDER_PAGESET = new BorderStyleSpec (PLColor.RED);

  /** green */
  public static final BorderStyleSpec DEFAULT_BORDER_BLOCK_ELEMENT = new BorderStyleSpec (PLColor.BLUE);

  /** blue */
  public static final BorderStyleSpec DEFAULT_BORDER_INLINE_ELEMENT = new BorderStyleSpec (PLColor.GREEN);

  /**
   * Provide the debug color for elements.
   *
   * @author Philip Helger
   */
  @FunctionalInterface
  public interface IDebugColorProvider
  {
    @Nullable
    PLColor getDebugColor (@NonNull Object aObject);

    @NonNull
    static IDebugColorProvider getDefaultOutlineProvider ()
    {
      return aObj -> {
        if (aObj instanceof PLPageSet)
          return DEFAULT_COLOR_OUTLINE_PAGESET;
        if (aObj instanceof IPLBlockElement <?>)
          return DEFAULT_COLOR_OUTLINE_BLOCK_ELEMENT;
        if (aObj instanceof IPLInlineElement <?>)
          return DEFAULT_COLOR_OUTLINE_INLINE_ELEMENT;
        return null;
      };
    }
  }

  /**
   * Provide the debug borders for elements.
   *
   * @author Philip Helger
   */
  @FunctionalInterface
  public interface IDebugBorderProvider
  {
    @Nullable
    BorderStyleSpec getDebugBorder (@NonNull Object aObject);

    @NonNull
    static IDebugBorderProvider getDefaultBorderProvider ()
    {
      return aObj -> {
        if (aObj instanceof PLPageSet)
          return DEFAULT_BORDER_PAGESET;
        if (aObj instanceof IPLBlockElement <?>)
          return DEFAULT_BORDER_BLOCK_ELEMENT;
        if (aObj instanceof IPLInlineElement <?>)
          return DEFAULT_BORDER_INLINE_ELEMENT;
        return null;
      };
    }
  }

  private static boolean s_bDebugRender;
  private static IDebugColorProvider s_aDebugOutlineColorProvider;
  private static IDebugBorderProvider s_aDebugBorderProvider;

  static
  {
    resetToDefault ();
  }

  private PLDebugRender ()
  {}

  /**
   * Reset all debug rendering stuff to defaults. This includes disabling of
   * debug rendering.
   */
  public static void resetToDefault ()
  {
    s_bDebugRender = DEFAULT_DEBUG_RENDER;
    s_aDebugOutlineColorProvider = IDebugColorProvider.getDefaultOutlineProvider ();
    s_aDebugBorderProvider = IDebugBorderProvider.getDefaultBorderProvider ();
  }

  /**
   * @return <code>true</code> if debug rendering is enabled, <code>false</code>
   *         if not.
   */
  public static boolean isDebugRender ()
  {
    return s_bDebugRender;
  }

  /**
   * Enable or disable debug rendering globally.
   *
   * @param bDebugRender
   *        <code>true</code> to enable it, <code>false</code> to disable it.
   */
  public static void setDebugRender (final boolean bDebugRender)
  {
    s_bDebugRender = bDebugRender;
  }

  public static void setDebugOutlineColorProvider (@NonNull final IDebugColorProvider aDebugOutlineColorProvider)
  {
    ValueEnforcer.notNull (aDebugOutlineColorProvider, "DebugOutlineColorProvider");
    s_aDebugOutlineColorProvider = aDebugOutlineColorProvider;
  }

  @Nullable
  public static PLColor getDebugOutlineColor (@NonNull final Object aObject)
  {
    return s_aDebugOutlineColorProvider.getDebugColor (aObject);
  }

  public static void setDebugBorderProvider (@NonNull final IDebugBorderProvider aDebugBorderProvider)
  {
    ValueEnforcer.notNull (aDebugBorderProvider, "DebugBorderProvider");
    s_aDebugBorderProvider = aDebugBorderProvider;
  }

  @Nullable
  public static BorderStyleSpec getDebugBorder (@NonNull final Object aObject)
  {
    return s_aDebugBorderProvider.getDebugBorder (aObject);
  }

  public static <EXTYPE extends Throwable> void withDebugRender (@NonNull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    withDebugRender (true, aRunnable);
  }

  public static <EXTYPE extends Throwable> void withDebugRender (final boolean bDebug,
                                                                 @NonNull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    setDebugRender (bDebug);
    try
    {
      aRunnable.run ();
    }
    finally
    {
      resetToDefault ();
    }
  }
}
