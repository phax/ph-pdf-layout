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
package com.helger.pdflayout.base;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.string.StringHelper;
import com.helger.base.trait.IGenericImplTrait;

/**
 * Base interface for objects that can act as a named link target ("anchor") inside the PDF. When an
 * anchor name is set, the rendering pipeline registers a PDF named destination at the element's
 * top-left position so that bookmarks, internal links (see
 * {@link com.helger.pdflayout.element.link.PLInternalLink}), or external URL fragments can jump
 * straight to this element. Analogous to an HTML <code>&lt;a name="..."&gt;</code> anchor.
 * <p>
 * For elements that are split across pages, the destination is registered only at the first
 * fragment - that is, on the page where the element first appears.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 8.2.0
 */
public interface IPLHasAnchorName <IMPLTYPE extends IPLHasAnchorName <IMPLTYPE>> extends IGenericImplTrait <IMPLTYPE>
{
  /**
   * By default no anchor name is set.
   */
  String DEFAULT_ANCHOR_NAME = null;

  /**
   * @return The anchor name, or <code>null</code> if no anchor is set.
   */
  @Nullable
  String getAnchorName ();

  /**
   * @return <code>true</code> if an anchor name is set, <code>false</code> otherwise.
   */
  default boolean hasAnchorName ()
  {
    return StringHelper.isNotEmpty (getAnchorName ());
  }

  /**
   * Set the anchor name. When set and non-<code>null</code>, the rendering pipeline registers a PDF
   * named destination pointing at the element's top-left position. Setting <code>null</code> (the
   * default) disables the anchor.
   * <p>
   * Anchor names must be unique within a document. Duplicates are logged as a warning and the first
   * registration wins.
   *
   * @param sAnchorName
   *        The name to register. May be <code>null</code> to disable. Must not be empty.
   * @return this for chaining
   */
  @NonNull
  IMPLTYPE setAnchorName (@Nullable String sAnchorName);
}
