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

import com.helger.pdflayout.spec.FontSpec;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Render text
 *
 * @author Philip Helger
 */
public class PLText extends AbstractPLText <PLText>
{
  public PLText (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    super (sText, aFontSpec);
  }

  @Override
  @Nonnull
  public PLText internalCreateNewVertSplitObject (@Nonnull final PLText aBase)
  {
    final PLText ret = new PLText (null, aBase.getFontSpec ());
    ret.setBasicDataFrom (aBase);
    return ret;
  }
}
