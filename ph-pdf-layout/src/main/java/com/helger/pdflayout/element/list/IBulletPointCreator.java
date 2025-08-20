/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.list;

import com.helger.annotation.Nonnegative;
import com.helger.pdflayout.base.IPLRenderableObject;

import jakarta.annotation.Nonnull;

/**
 * Abstract bullet point creator to create e.g. constant dots or numbers or characters.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public interface IBulletPointCreator
{
  /**
   * Get the bullet point text for the specified index.
   *
   * @param nBulletPointIndex
   *        The 0-based index of the bullet point to be created.
   * @return A non-<code>null</code> bullet point element.
   */
  @Nonnull
  IPLRenderableObject <?> getBulletPointElement (@Nonnegative int nBulletPointIndex);
}
