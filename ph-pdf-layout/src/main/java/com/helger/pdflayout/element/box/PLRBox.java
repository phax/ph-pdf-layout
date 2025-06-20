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
package com.helger.pdflayout.element.box;

import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageRenderContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorder;

/**
 * A rounded box is a simple block element that encapsulates another element and has a
 * padding, border and margin etc. itself, plus a rounded border radius.
 *
 * @author Philip Helger
 * @author Marco De Angelis
 */
public class PLRBox extends AbstractPLBox <PLRBox>
{

  private float borderRadius = 8.0f;

  public PLRBox()
  {
    super (null);
  }

  public PLRBox(@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }


  public float getBorderRadius() {
    return borderRadius;
  }

  public PLRBox setBorderRadius(float borderRadius) {
    this.borderRadius = borderRadius;
    return this;
  }

  @Override
  @Nonnull
  public PLRBox internalCreateNewVertSplitObject (@Nonnull final PLRBox aBase)
  {
    final PLRBox ret = new PLRBox();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void renderShape(@Nonnull final PageRenderContext aCtx) throws IOException {
    fillAndRenderBorder (thisAsT (), aCtx, 0f, 0f, borderRadius);
  }

  @Override
  protected void clipShape(@Nonnull PageRenderContext aCtx, float fLeft, float fBottom, float fWidth, float fHeight) throws IOException {
    final PDPageContentStreamWithCache aCSWC = aCtx.getContentStream ();
    aCSWC.saveGraphicsState ();
    aCSWC.drawRoundedRect (fLeft, fBottom, fWidth, fHeight, borderRadius, borderRadius, borderRadius, borderRadius);
    aCSWC.clip ();
  }
}
