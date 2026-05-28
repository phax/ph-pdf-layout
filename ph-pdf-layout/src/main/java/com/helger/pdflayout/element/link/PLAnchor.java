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

import java.io.IOException;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLHasAnchorName;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A zero-size anchor marker. Place this element in the flow at the position you want a PDF named
 * destination registered. When the document is rendered, a {@code PDPageXYZDestination} is added
 * to the document's name dictionary under the anchor name; bookmarks, internal links, or external
 * URL fragments (<code>mypdf.pdf#section1</code>) can then jump straight to that point.
 * <p>
 * The element occupies no space (zero width, zero height), takes no part in layout calculations
 * beyond consuming a row slot in containers that iterate row-by-row, and renders nothing visible.
 * It cannot be split vertically.
 * <p>
 * Equivalent to an HTML <code>&lt;a name="section1"&gt;</code> anchor. For block elements that
 * should be their own anchor without a separate marker, use {@link IPLHasAnchorName#setAnchorName}
 * on the block element itself.
 *
 * @author Philip Helger
 * @since 8.2.0
 */
public class PLAnchor extends AbstractPLRenderableObject <PLAnchor>
{
  /**
   * Constructor.
   *
   * @param sAnchorName
   *        The anchor name to register. Must not be empty.
   */
  public PLAnchor (@NonNull @Nonempty final String sAnchorName)
  {
    setAnchorName (sAnchorName);
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public PLAnchor setBasicDataFrom (@NonNull final PLAnchor aSource)
  {
    super.setBasicDataFrom (aSource);
    return this;
  }

  @Override
  protected SizeSpec onPrepare (@NonNull final PreparationContext aCtx)
  {
    return SizeSpec.SIZE0;
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    // Nothing to reset
  }

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    // The anchor's destination is registered by the base render method via
    // PLAnchorRegistry, which fires for any element with an anchor name.
    // Nothing visible is drawn here.
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).getToString ();
  }
}
