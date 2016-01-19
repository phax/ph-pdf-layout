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
package com.helger.pdflayout.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * The current context for preparing an element.
 *
 * @author Philip Helger
 */
@Immutable
public final class PreparationContext
{
  private final PDDocument m_aDoc;
  private final float m_fAvailableWidth;
  private final float m_fAvailableHeight;
  private final Map <PreloadFont, LoadedFont> m_aFontCache = new HashMap <PreloadFont, LoadedFont> ();

  /**
   * Constructor
   *
   * @param aDoc
   *        The {@link PDDocument} worked upon
   * @param fAvailableWidth
   *        The available width for an element, without the element's margin and
   *        padding. Should be &gt; 0.
   * @param fAvailableHeight
   *        The available height for an element, without the element's margin
   *        and padding. Should be &gt; 0.
   */
  public PreparationContext (@Nonnull final PDDocument aDoc,
                             @Nonnegative final float fAvailableWidth,
                             @Nonnegative final float fAvailableHeight)
  {
    ValueEnforcer.notNull (aDoc, "PDDocument");
    ValueEnforcer.isGE0 (fAvailableWidth, "AvailableWidth");
    ValueEnforcer.isGE0 (fAvailableHeight, "AvailableHeight");
    m_aDoc = aDoc;
    m_fAvailableWidth = fAvailableWidth;
    m_fAvailableHeight = fAvailableHeight;
  }

  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDoc;
  }

  /**
   * @return The available width for an element, without the element's margin
   *         and padding. Should be &gt; 0.
   */
  public float getAvailableWidth ()
  {
    return m_fAvailableWidth;
  }

  /**
   * @return The available height for an element, without the element's margin
   *         and padding. Should be &gt; 0.
   */
  public float getAvailableHeight ()
  {
    return m_fAvailableHeight;
  }

  @Nonnull
  public LoadedFont getLoadedFont (@Nonnull final FontSpec aFontSpec) throws IOException
  {
    final PreloadFont aPreloadFont = aFontSpec.getPreloadFont ();
    LoadedFont aLoadedFont = m_aFontCache.get (aPreloadFont);
    if (aLoadedFont == null)
    {
      aLoadedFont = new LoadedFont (aPreloadFont.loadPDFont (m_aDoc));
      m_aFontCache.put (aPreloadFont, aLoadedFont);
    }
    return aLoadedFont;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("availableWidth", m_fAvailableWidth)
                                       .append ("availableHeight", m_fAvailableHeight)
                                       .toString ();
  }
}
