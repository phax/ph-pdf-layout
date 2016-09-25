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
package com.helger.pdflayout.base;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Base interface for a renderable PDF layout object.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLRenderableObject <IMPLTYPE extends IPLRenderableObject <IMPLTYPE>>
                                     extends IPLObject <IMPLTYPE>, IPLHasOutline
{
  /**
   * @return <code>true</code> if this object was already prepared,
   *         <code>false</code> otherwise.
   */
  boolean isPrepared ();

  /**
   * @return The prepared size or <code>null</code> if this object was not yet
   *         prepared.
   * @see #isPrepared()
   */
  @Nullable
  SizeSpec getPreparedSize ();

  default float getPreparedWidth ()
  {
    return getPreparedSize ().getWidth ();
  }

  default float getPreparedHeight ()
  {
    return getPreparedSize ().getHeight ();
  }

  /**
   * Prepare this element once for rendering.
   *
   * @param aCtx
   *        The preparation context
   * @return The net size of the rendered object without margin, border and
   *         margin. May not be <code>null</code>.
   * @throws IOException
   *         if already prepared
   * @see #render(PageRenderContext)
   */
  @Nonnull
  SizeSpec prepare (@Nonnull final PreparationContext aCtx) throws IOException;

  /**
   * Called after the page was created but before the content stream is created.
   * This is e.g. used for images to create their XObjects upfront.
   *
   * @param aCtx
   *        The current page render context. Never <code>null</code>.
   * @throws IOException
   *         In case of a PDFBox error
   */
  default void beforeRender (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {}

  /**
   * Second step: perform. This renders the previously prepared object to the
   * PDF content stream present in the rendering context.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   * @see #prepare(PreparationContext)
   */
  @Nonnegative
  void render (@Nonnull final PageRenderContext aCtx) throws IOException;
}
