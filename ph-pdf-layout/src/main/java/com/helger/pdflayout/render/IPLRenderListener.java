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
package com.helger.pdflayout.render;

import org.jspecify.annotations.NonNull;

import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * Listener invoked after every PL element render. Use this to learn which page a particular
 * element ended up on - for example to build a table of contents or PDF bookmarks.
 * <p>
 * The listener fires once per render call, which includes the top-level page elements as well as
 * every nested child reached through their parent's render method. The listener also fires for
 * page headers and footers; filter on {@link PageRenderContext#getElementType()} if you only care
 * about content elements. The listener does NOT fire if {@code onRender} throws.
 * <p>
 * Elements that are split across pages produce one event per fragment. Use
 * {@link com.helger.pdflayout.base.IPLObject#getOriginalID()} to correlate fragments to their
 * unsplit ancestor and {@link com.helger.pdflayout.base.IPLObject#isFirstFragment()} to identify
 * the top-most slice (the natural target for a TOC entry).
 *
 * @author Philip Helger
 * @since 8.1.3
 */
@FunctionalInterface
public interface IPLRenderListener
{
  /**
   * Called after the element finishes rendering on a page.
   *
   * @param aElement
   *        The element that was just rendered. Never <code>null</code>.
   * @param aCtx
   *        The render context used for this render call. Carries page indices and the placement
   *        coordinates ({@link PageRenderContext#getStartLeft()},
   *        {@link PageRenderContext#getStartTop()}). Never <code>null</code>.
   */
  void onElementRendered (@NonNull IPLRenderableObject <?> aElement, @NonNull PageRenderContext aCtx);
}
