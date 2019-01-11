/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.spec;

import javax.annotation.Nullable;

import com.helger.font.api.IFontResource;
import com.helger.font.api.IHasFontResource;

@FunctionalInterface
public interface IPreloadFontResolver
{
  /**
   * Get the {@link PreloadFont} with the provided ID.
   *
   * @param sID
   *        The ID to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  PreloadFont getPreloadFontOfID (@Nullable String sID);

  /**
   * Get the {@link PreloadFont} from the provided font resource.
   *
   * @param aFontRes
   *        The font resource to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  default PreloadFont getPreloadFontOfID (@Nullable final IFontResource aFontRes)
  {
    if (aFontRes == null)
      return null;
    return getPreloadFontOfID (aFontRes.getID ());
  }

  /**
   * Get the {@link PreloadFont} from the provided font resource provider.
   *
   * @param aFontResProvider
   *        The font resource provided from which to be resolved. May be
   *        <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  default PreloadFont getPreloadFontOfID (@Nullable final IHasFontResource aFontResProvider)
  {
    if (aFontResProvider == null)
      return null;
    return getPreloadFontOfID (aFontResProvider.getFontResource ());
  }
}
