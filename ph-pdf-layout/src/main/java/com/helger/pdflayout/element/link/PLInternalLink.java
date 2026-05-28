/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * A clickable in-document link that jumps to a named anchor. Configure the target with
 * {@link #setTargetAnchorName(String)}; that name must match the anchor name set on a
 * {@link PLAnchor} or any other element implementing
 * {@link com.helger.pdflayout.base.IPLHasAnchorName}.
 *
 * @author Philip Helger
 * @since 8.2.0
 */
public class PLInternalLink extends AbstractPLInternalLink <PLInternalLink>
{
  public PLInternalLink ()
  {
    super (null);
  }

  public PLInternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  @Override
  @NonNull
  public PLInternalLink internalCreateNewVertSplitObject (@NonNull final PLInternalLink aBase)
  {
    final PLInternalLink ret = new PLInternalLink ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }
}
