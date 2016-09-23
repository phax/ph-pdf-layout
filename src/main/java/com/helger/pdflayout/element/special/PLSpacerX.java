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
package com.helger.pdflayout.element.special;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A horizontal spacer
 *
 * @author Philip Helger
 */
public class PLSpacerX extends AbstractPLRenderableObject <PLSpacerX>
{
  public PLSpacerX ()
  {}

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    // No height required
    return new SizeSpec (aCtx.getAvailableWidth (), 0);
  }

  @Override
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {}

  @Nonnull
  public static PLSpacerX createPrepared (final float fWidth)
  {
    final PLSpacerX ret = new PLSpacerX ();
    ret.internalMarkAsPrepared (new SizeSpec (fWidth, 0));
    return ret;
  }
}
