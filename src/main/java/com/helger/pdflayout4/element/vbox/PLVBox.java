/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.vbox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.spec.HeightSpec;

/**
 * Vertical box - groups several rows without having layout information itself.
 *
 * @author Philip Helger
 */
public class PLVBox extends AbstractPLVBox <PLVBox>
{
  /**
   * Default constructor for an empty VBox.
   */
  public PLVBox ()
  {}

  /**
   * Constructor with elements so that each element constitutes a new row with
   * auto-height.
   *
   * @param aElements
   *        The elements for which rows should be created.
   */
  public PLVBox (@Nullable final IPLRenderableObject <?>... aElements)
  {
    if (aElements != null)
      for (final IPLRenderableObject <?> aElement : aElements)
        addRow (aElement, HeightSpec.auto ());
  }

  /**
   * Constructor with elements so that each element constitutes a new row with
   * auto-height.
   *
   * @param aElements
   *        The elements for which rows should be created.
   * @since 4.0.1
   */
  public PLVBox (@Nullable final Iterable <? extends IPLRenderableObject <?>> aElements)
  {
    if (aElements != null)
      for (final IPLRenderableObject <?> aElement : aElements)
        addRow (aElement, HeightSpec.auto ());
  }

  @Override
  @Nonnull
  public PLVBox internalCreateNewVertSplitObject (@Nonnull final PLVBox aBase)
  {
    final PLVBox ret = new PLVBox ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }
}
