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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A box is a simple element that encapsulates another element and has a
 * padding, border and margin itself
 *
 * @author Philip Helger
 */
public class PLBox extends AbstractPLElement <PLBox>
{
  private final IPLRenderableObject <?> m_aElement;
  private SizeSpec m_aElementPreparedSize;

  public PLBox ()
  {
    this (null);
  }

  public PLBox (@Nullable final IPLRenderableObject <?> aElement)
  {
    m_aElement = aElement;
  }

  /**
   * @return The element passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    super.visit (aVisitor);
    if (m_aElement != null)
      m_aElement.visit (aVisitor);
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    if (m_aElement == null)
      return SizeSpec.SIZE0;

    final float fElementWidth = aCtx.getAvailableWidth () - getFullXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getFullYSum ();

    final PreparationContext aElementCtx = new PreparationContext (aCtx.getGlobalContext (),
                                                                   fElementWidth,
                                                                   fElementHeight);
    m_aElementPreparedSize = m_aElement.prepare (aElementCtx);
    return m_aElementPreparedSize.plus (m_aElement.getFullXSum (), m_aElement.getFullYSum ());
  }

  @Override
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    if (m_aElement != null)
    {
      final PageRenderContext aElementCtx = new PageRenderContext (aCtx,
                                                                   aCtx.getStartLeft () + getFullLeft (),
                                                                   aCtx.getStartTop () - getFullTop (),
                                                                   getPreparedWidth (),
                                                                   getPreparedHeight ());
      m_aElement.perform (aElementCtx);
    }
  }
}
