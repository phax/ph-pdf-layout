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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.AbstractPLBlockElement;
import com.helger.pdflayout.base.IPLHasMargin;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
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
                                    extends AbstractPLBlockElement <IMPLTYPE> implements IPLSplittableObject <IMPLTYPE>
{
  private IPLRenderableObject <?> m_aElement;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  // Status vars
  private SizeSpec m_aElementPreparedSize;

  public AbstractPLBox (@Nullable final IPLRenderableObject <?> aElement)
  {
    setElement (aElement);
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLBox <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setVertSplittable (aSource.m_bVertSplittable);
    return thisAsT ();
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

  public boolean isVertSplittable ()
  {
    if (!m_bVertSplittable)
      return false;
    // Empty boxes or boxes with a non-splittable element cannot be split
    return hasElement () && getElement ().isVertSplittable ();
  }

  @Nonnull
  public IMPLTYPE setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
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

  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    final IPLRenderableObject <?> aElement = getElement ();

    // Create resulting VBoxes - the first one is not splittable again!
    final AbstractPLBox <?> aBox1 = new PLBox ().setBasicDataFrom (this);
    final AbstractPLBox <?> aBox2 = new PLBox ().setBasicDataFrom (this);

    float fBox1UsedHeight = 0;
    float fBox2UsedHeight = 0;

    SizeSpec aBox1ElementPreparedSize = null;
    SizeSpec aBox2ElementPreparedSize = null;

    final float fBoxHeight = getPreparedHeight ();
    if (fBoxHeight <= fAvailableHeight)
    {
      // Row fits in first Box without a change
      aBox1.setElement (aElement);
      fBox1UsedHeight += fBoxHeight;
      aBox1ElementPreparedSize = m_aElementPreparedSize;
    }
    else
    {
      // Try split
      final float fSplitWidth = getElementPreparedSize ().getWidth ();
      final float fSplitHeight = fAvailableHeight - aElement.getOutlineYSum ();
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this,
                            "Trying to split " +
                                  aElement.getDebugID () +
                                  " into pieces for split size " +
                                  PLDebug.getWH (fSplitWidth, fSplitHeight));

      // Try to split the element contained in the row
      final PLSplitResult aSplitResult = aElement.getAsSplittable ().splitElementVert (fSplitWidth, fSplitHeight);
      if (aSplitResult != null)
      {
        final IPLRenderableObject <?> aBox1Element = aSplitResult.getFirstElement ().getElement ();
        aBox1.setElement (aBox1Element);
        fBox1UsedHeight += aSplitResult.getFirstElement ().getHeightFull ();
        aBox1ElementPreparedSize = aSplitResult.getFirstElement ().getSize ();

        final IPLRenderableObject <?> aBox2Element = aSplitResult.getSecondElement ().getElement ();
        aBox2.setElement (aBox2Element);
        fBox2UsedHeight += aSplitResult.getSecondElement ().getHeightFull ();
        aBox2ElementPreparedSize = aSplitResult.getSecondElement ().getSize ();

        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Split box element " +
                                    aElement.getDebugID () +
                                    " into pieces: " +
                                    aBox1Element.getDebugID () +
                                    " (" +
                                    aSplitResult.getFirstElement ().getWidth () +
                                    "+" +
                                    aBox1Element.getOutlineXSum () +
                                    " & " +
                                    aSplitResult.getFirstElement ().getHeight () +
                                    "+" +
                                    aBox1Element.getOutlineYSum () +
                                    ") and " +
                                    aBox2Element.getDebugID () +
                                    " (" +
                                    aSplitResult.getSecondElement ().getWidth () +
                                    "+" +
                                    aBox2Element.getOutlineXSum () +
                                    " & " +
                                    aSplitResult.getSecondElement ().getHeight () +
                                    "+" +
                                    aBox2Element.getOutlineYSum () +
                                    ")");
      }
      else
      {
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this, "Failed to split row element " + aElement.getDebugID () + " into pieces");

        // just add the full row to the second VBox since the row does not
        // fit on first page
        aBox2.setElement (aElement);
        fBox2UsedHeight += fBoxHeight;
        aBox2ElementPreparedSize = m_aElementPreparedSize;
      }
    }

    if (!aBox1.hasElement ())
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Splitting makes no sense, because Box 1 would be empty");
      return null;
    }

    if (!aBox2.hasElement ())
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Splitting makes no sense, because Box 2 would be empty");
      return null;
    }

    // Excluding padding/margin
    aBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox1UsedHeight));
    aBox1.m_aElementPreparedSize = aBox1ElementPreparedSize;

    aBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox2UsedHeight));
    aBox2.m_aElementPreparedSize = aBox2ElementPreparedSize;

    return new PLSplitResult (new PLElementWithSize (aBox1, new SizeSpec (fAvailableWidth, fBox1UsedHeight)),
                              new PLElementWithSize (aBox2, new SizeSpec (fAvailableWidth, fBox2UsedHeight)));
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
                            .append ("VertSplittable", m_bVertSplittable)
                            .appendIfNotNull ("ElementPreparedSize", m_aElementPreparedSize)
                            .toString ();
  }
}
