/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.element.box;

import com.plenigo.pdflayout.base.IPLRenderableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A box is a simple block element that encapsulates another element and has a
 * padding, border and margin etc. itself
 *
 * @author Philip Helger
 */
public class PLBox extends AbstractPLBox<PLBox> {
    public PLBox() {
        super(null);
    }

    public PLBox(@Nullable final IPLRenderableObject<?> aElement) {
        super(aElement);
    }

    @Override
    @Nonnull
    public PLBox internalCreateNewVertSplitObject(@Nonnull final PLBox aBase) {
        final PLBox ret = new PLBox();
        ret.setBasicDataFrom(aBase);
        return ret;
    }
}
