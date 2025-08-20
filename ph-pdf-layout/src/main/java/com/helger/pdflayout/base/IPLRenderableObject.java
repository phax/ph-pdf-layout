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
package com.helger.pdflayout.base;

import java.io.IOException;

import com.helger.annotation.Nonnegative;
import com.helger.base.state.EChange;
import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Base interface for a renderable PDF layout object.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLRenderableObject <IMPLTYPE extends IPLRenderableObject <IMPLTYPE>> extends IPLObject <IMPLTYPE>, IPLHasOutline
{
  /**
   * @return <code>true</code> if this object was already prepared,
   *         <code>false</code> otherwise.
   */
  boolean isPrepared ();

  /**
   * Get the prepared size of the object. This is the minimum space the content
   * of the object needs given the available size constraints. This does NOT
   * consider min- and max-size.
   *
   * @return The prepared size or <code>null</code> if this object was not yet
   *         prepared.
   * @see #isPrepared()
   */
  @Nullable
  SizeSpec getPreparedSize ();

  /**
   * @return The prepared width.
   * @see #getPreparedSize()
   */
  default float getPreparedWidth ()
  {
    return getPreparedSize ().getWidth ();
  }

  /**
   * @return The prepared height.
   * @see #getPreparedSize()
   */
  default float getPreparedHeight ()
  {
    return getPreparedSize ().getHeight ();
  }

  /**
   * @return The render size or <code>null</code> if this object was not yet
   *         prepared. The render size includes the min/max size.
   * @see #isPrepared()
   */
  @Nullable
  SizeSpec getRenderSize ();

  default float getRenderWidth ()
  {
    return getRenderSize ().getWidth ();
  }

  default float getRenderHeight ()
  {
    return getRenderSize ().getHeight ();
  }

  /**
   * Prepare this element once for rendering.
   *
   * @param aCtx
   *        The preparation context
   * @return The net size of the rendered object without margin, border and
   *         margin. May not be <code>null</code>.
   * @see #render(PageRenderContext)
   */
  @Nonnull
  SizeSpec prepare (@Nonnull final PreparationContext aCtx);

  /**
   * Called after the page was created but before the content stream is created.
   * This is e.g. used for images to create their XObjects upfront.
   *
   * @param aCtx
   *        The current page render context. Never <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed. May not be
   *         <code>null</code>.
   * @throws IOException
   *         In case of a PDFBox error
   */
  @Nonnull
  default EChange beforeRender (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {
    return EChange.UNCHANGED;
  }

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
