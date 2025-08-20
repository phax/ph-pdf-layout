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
package com.helger.pdflayout;

import org.apache.xmpbox.XMPMetadata;

import jakarta.annotation.Nonnull;

/**
 * Callback interface to customize XMP Metadata.
 *
 * @author stmuecke
 * @author Philip Helger
 * @since 7.3.6
 */
@FunctionalInterface
public interface IXMPMetadataCustomizer
{
  /**
   * Customize the provided {@link XMPMetadata} object. It is called as the last
   * action before it gets serialized.
   *
   * @param aXmpMetadata
   *        The object to be customized. Never <code>null</code>.
   */
  void customizeMetadata (@Nonnull XMPMetadata aXmpMetadata);
}
