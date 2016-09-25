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
package com.helger.pdflayout.element.box;

import javax.annotation.Nullable;

import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLSplitResult;

/**
 * A splittable box is a simple element that encapsulates another element and
 * has a padding, border and margin etc. itself
 *
 * @author Philip Helger
 */
public class PLBoxSplittable extends AbstractPLBox <PLBoxSplittable> implements IPLSplittableObject <PLBoxSplittable>
{
  public PLBoxSplittable ()
  {
    super (null);
  }

  public PLBoxSplittable (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  public boolean isSplittable ()
  {
    // Empty boxes or boxes with a non-splittable element cannot be split
    return hasElement () && getElement ().isSplittable ();
  }

  @Nullable
  public PLSplitResult splitElements (final float fElementWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    // Create resulting VBoxes - the first one is not splittable again!
    final PLBox aBox1 = new PLBox ().setBasicDataFrom (this);
    final PLBoxSplittable aBox2 = new PLBoxSplittable ().setBasicDataFrom (this);

    return null;
  }
}
