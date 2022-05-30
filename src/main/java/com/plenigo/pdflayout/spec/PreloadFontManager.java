/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.spec;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.string.ToStringGenerator;
import com.helger.font.api.IFontResource;
import com.helger.font.api.IHasFontResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Predicate;

/**
 * A manager for maintaining {@link PreloadFont}s.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class PreloadFontManager implements IPreloadFontResolver {
    private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock();
    private final ICommonsMap<String, PreloadFont> m_aMap = new CommonsHashMap<>();

    /**
     * Default constructor which registers the standard 14 fonts.
     */
    public PreloadFontManager() {
        this(true);
    }

  /**
   * Constructor.
   *
   * @param bRegisterStandardFonts
   *        <code>true</code> to register the standard 14 fonts,
   *        <code>false</code> to not do it.
   */
  public PreloadFontManager (final boolean bRegisterStandardFonts)
  {
    if (bRegisterStandardFonts)
    {
      // Register all default fonts
      addPreloadFont (PreloadFont.REGULAR);
      addPreloadFont (PreloadFont.REGULAR_BOLD);
      addPreloadFont (PreloadFont.REGULAR_ITALIC);
      addPreloadFont (PreloadFont.REGULAR_BOLD_ITALIC);
      addPreloadFont (PreloadFont.MONOSPACE);
      addPreloadFont (PreloadFont.MONOSPACE_BOLD);
      addPreloadFont (PreloadFont.MONOSPACE_ITALIC);
      addPreloadFont (PreloadFont.MONOSPACE_BOLD_ITALIC);
      addPreloadFont (PreloadFont.TIMES);
      addPreloadFont (PreloadFont.TIMES_BOLD);
      addPreloadFont (PreloadFont.TIMES_ITALIC);
      addPreloadFont (PreloadFont.TIMES_BOLD_ITALIC);
      addPreloadFont (PreloadFont.SYMBOL);
      addPreloadFont (PreloadFont.ZAPF_DINGBATS);
    }
  }

  /**
   * Add a pre-created {@link PreloadFont}.
   *
   * @param aPreloadFont
   *        The font to be added. May not be <code>null</code>.
   */
  public void addPreloadFont (@Nonnull final PreloadFont aPreloadFont)
  {
    ValueEnforcer.notNull (aPreloadFont, "PreloadFont");
    final String sKey = aPreloadFont.getID ();

    m_aRWLock.writeLocked ( () -> {
      if (m_aMap.containsKey (sKey))
        throw new IllegalArgumentException ("The PreloadFont  " + aPreloadFont + " is already contained!");
      m_aMap.put (sKey, aPreloadFont);
    });
  }

  /**
   * Create and add a new embedding {@link PreloadFont} if it is not yet
   * contained.
   *
   * @param aFontResProvider
   *        The font resource provider to be added for embedding. May not be
   *        <code>null</code>.
   * @return The created {@link PreloadFont}. Never <code>null</code>.
   */
  @Nonnull
  public PreloadFont getOrAddEmbeddingPreloadFont (@Nonnull final IHasFontResource aFontResProvider)
  {
    ValueEnforcer.notNull (aFontResProvider, "FontResProvider");
    return getOrAddEmbeddingPreloadFont (aFontResProvider.getFontResource ());
  }

  /**
   * Create and add a new embedding {@link PreloadFont} if it is not yet
   * contained.
   *
   * @param aFontRes
   *        The font resource to be added for embedding. May not be
   *        <code>null</code>.
   * @return The created {@link PreloadFont}. Never <code>null</code>.
   */
  @Nonnull
  public PreloadFont getOrAddEmbeddingPreloadFont (@Nonnull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    PreloadFont aPreloadFont = getPreloadFontOfID (aFontRes);
    if (aPreloadFont == null)
    {
      aPreloadFont = PreloadFont.createEmbedding (aFontRes);
      addPreloadFont (aPreloadFont);
    }
    return aPreloadFont;
  }

  @Nullable
  public PreloadFont getPreloadFontOfID (@Nullable final String sID)
  {
    if (sID == null)
      return null;
    return m_aRWLock.readLockedGet ( () -> m_aMap.get (sID));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <PreloadFont> getAllPreloadFonts ()
  {
    return m_aRWLock.readLockedGet (m_aMap::copyOfValues);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <PreloadFont> getAllPreloadFonts (@Nullable final Predicate <? super PreloadFont> aFilter)
  {
    return m_aRWLock.readLockedGet ( () -> m_aMap.copyOfValues (aFilter));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Map", m_aMap).getToString ();
  }
}
