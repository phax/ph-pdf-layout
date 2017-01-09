/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.table;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.hbox.AbstractPLHBox;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;

/**
 * Special HBox for table rows, that creates table cells when split vertically.
 *
 * @author Philip Helger
 */
final class PLTableRowHBox extends AbstractPLHBox <PLTableRowHBox>
{
  @Override
  @Nonnull
  protected PLTableCell splitVertCreateEmptyElement (@Nonnull final IPLRenderableObject <?> aSrcObject,
                                                     final float fWidth,
                                                     final float fHeight)
  {
    final PLTableCell ret = new PLTableCell (null);
    ret.setBasicDataFrom ((PLTableCell) aSrcObject);
    ret.prepare (new PreparationContext (null, fWidth, fHeight));
    return ret;
  }

  @Nonnull
  public PLTableRowHBox internalCreateNewVertSplitObject (@Nonnull final PLTableRowHBox aBase)
  {
    final PLTableRowHBox ret = new PLTableRowHBox ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    super.onRender (aCtx);
  }
}
