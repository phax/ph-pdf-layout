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
package com.helger.pdflayout4.element.vbox;

import javax.annotation.Nullable;

import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.spec.HeightSpec;

/**
 * Vertical box - groups several rows.
 *
 * @author Philip Helger
 */
public class PLVBox extends AbstractPLVBox <PLVBox>
{
  public PLVBox ()
  {}

  public PLVBox (@Nullable final IPLRenderableObject <?>... aElements)
  {
    if (aElements != null)
      for (final IPLRenderableObject <?> aElement : aElements)
        addRow (aElement, HeightSpec.auto ());
  }
}
