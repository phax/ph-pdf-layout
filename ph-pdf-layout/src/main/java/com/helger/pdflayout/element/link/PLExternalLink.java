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
package com.helger.pdflayout.element.link;

import com.helger.pdflayout.base.IPLRenderableObject;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An external link that references to an external URI. Use
 * {@link #setURI(String)} to define the link target.
 *
 * @author Philip Helger
 * @since 6.0.1
 */
public class PLExternalLink extends AbstractPLExternalLink <PLExternalLink>
{
  public PLExternalLink ()
  {
    super (null);
  }

  public PLExternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  @Override
  @Nonnull
  public PLExternalLink internalCreateNewVertSplitObject (@Nonnull final PLExternalLink aBase)
  {
    final PLExternalLink ret = new PLExternalLink ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }
}
