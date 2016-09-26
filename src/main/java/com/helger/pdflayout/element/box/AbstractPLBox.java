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

import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLBlockElement;
import com.helger.pdflayout.base.IPLHasMargin;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A box is a simple element that encapsulates another element and has a
 * padding, border and margin itself as well as it can align the contained
 * element.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLBox <IMPLTYPE extends AbstractPLBox <IMPLTYPE>>
                                    extends AbstractPLBlockElement <IMPLTYPE>
{
  private IPLRenderableObject <?> m_aElement;
  protected SizeSpec m_aElementPreparedSize;

  public AbstractPLBox (@Nullable final IPLRenderableObject <?> aElement)
  {
    setElement (aElement);
  }

  /**
   * @return The element passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  /**
   * @return <code>true</code> if an element is contained, <code>false</code> if
   *         not.
   */
  public boolean hasElement ()
  {
    return m_aElement != null;
  }

  @Nonnull
  public final IMPLTYPE setElement (@Nullable final IPLRenderableObject <?> aElement)
  {
    internalCheckNotPrepared ();
    m_aElement = aElement;
    return thisAsT ();
  }

  @Override
  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    super.visit (aVisitor);
    if (m_aElement != null)
      m_aElement.visit (aVisitor);
  }

  /**
   * @return The prepared size of the contained element. May be
   *         <code>null</code> if this box was not yet prepared or if no element
   *         is contained.
   */
  @Nullable
  protected final SizeSpec getElementPreparedSize ()
  {
    return m_aElementPreparedSize;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    if (m_aElement == null)
      return SizeSpec.SIZE0;

    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();

    final PreparationContext aElementCtx = new PreparationContext (aCtx.getGlobalContext (),
                                                                   fElementWidth,
                                                                   fElementHeight);
    m_aElementPreparedSize = m_aElement.prepare (aElementCtx);

    // Add the outer stuff of the contained element as this elements prepared
    // size
    final SizeSpec ret = m_aElementPreparedSize.plus (m_aElement.getOutlineXSum (), m_aElement.getOutlineYSum ());

    if (m_aElement instanceof IPLHasMargin <?>)
    {
      // Add margin to the child element for alignment
      final IPLHasMargin <?> aEl = (IPLHasMargin <?>) m_aElement;

      // Calculate how big this box would be with min/max size
      final SizeSpec aRealSize = adoptPreparedSize (ret);
      aEl.addMarginLeft (getIndentX (aRealSize.getWidth (), ret.getWidth ()));
      aEl.addMarginTop (getIndentY (aRealSize.getHeight (), ret.getHeight ()));
    }

    return ret;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    if (m_aElement != null)
    {
      final PageRenderContext aElementCtx = new PageRenderContext (aCtx,
                                                                   aCtx.getStartLeft () + getOutlineLeft (),
                                                                   aCtx.getStartTop () - getOutlineTop (),
                                                                   getPreparedWidth (),
                                                                   getPreparedHeight ());
      m_aElement.render (aElementCtx);
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("Element", m_aElement)
                            .appendIfNotNull ("ElementPreparedSize", m_aElementPreparedSize)
                            .toString ();
  }
}
