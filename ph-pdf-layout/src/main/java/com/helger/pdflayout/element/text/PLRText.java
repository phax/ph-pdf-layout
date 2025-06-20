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
package com.helger.pdflayout.element.text;

import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.spec.FontSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorder;

/**
 * Render text in a rounded rectangle.
 *
 * @author Philip Helger
 * @author Marco De Angelis
 */
public class PLRText extends AbstractPLText <PLRText>
{
  private float borderRadius = 4.0f;

  public PLRText(@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    super (sText, aFontSpec);
  }

  public float getBorderRadius() {
    return borderRadius;
  }

  public PLRText setBorderRadius(float borderRadius) {
    this.borderRadius = borderRadius;
    return this;
  }

  @Override
  @Nonnull
  public PLRText internalCreateNewVertSplitObject (@Nonnull final PLRText aBase)
  {
    final PLRText ret = new PLRText(null, aBase.getFontSpec ());
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void renderShape(@Nonnull final PageRenderContext aCtx) throws IOException {
    fillAndRenderBorder (thisAsT (), aCtx, 0f, 0f, borderRadius);
  }

}
