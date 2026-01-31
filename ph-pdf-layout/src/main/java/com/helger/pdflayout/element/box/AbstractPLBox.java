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
package com.helger.pdflayout.element.box;

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorderRounded;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLBlockElement;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PLRenderHelper;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A box is a simple element that encapsulates another element and has a padding, border and margin
 * itself as well as it can align the contained element.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLBox <IMPLTYPE extends AbstractPLBox <IMPLTYPE>> extends
                                    AbstractPLBlockElement <IMPLTYPE> implements
                                    IPLSplittableObject <IMPLTYPE, IMPLTYPE>
{
  public static final float DEFAULT_BORDER_RADIUS = 0f;

  private IPLRenderableObject <?> m_aElement;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;
  private float m_fBorderRadius = DEFAULT_BORDER_RADIUS;

  // Status vars
  private SizeSpec m_aElementPreparedSize;
  private SizeSpec m_aRenderOffset = SizeSpec.SIZE0;

  public AbstractPLBox (@Nullable final IPLRenderableObject <?> aElement)
  {
    setElement (aElement);
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setVertSplittable (aSource.isVertSplittable ());
    setBorderRadius (aSource.getBorderRadius ());
    return thisAsT ();
  }

  /**
   * @return The element passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public final IPLRenderableObject <?> getElement ()
  {
    return m_aElement;
  }

  /**
   * @return <code>true</code> if an element is contained, <code>false</code> if not.
   */
  public final boolean hasElement ()
  {
    return m_aElement != null;
  }

  @NonNull
  public final IMPLTYPE setElement (@Nullable final IPLRenderableObject <?> aElement)
  {
    internalCheckNotPrepared ();
    m_aElement = aElement;
    return thisAsT ();
  }

  public final boolean isVertSplittable ()
  {
    if (!m_bVertSplittable)
      return false;
    // Empty boxes or boxes with a non-splittable element cannot be split
    return hasElement () && getElement ().isVertSplittable ();
  }

  @NonNull
  public final IMPLTYPE setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return thisAsT ();
  }

  /**
   * @return The border radius to use. Only values &gt; 0 will draw a radius.
   * @since v7.4.1
   */
  public final float getBorderRadius ()
  {
    return m_fBorderRadius;
  }

  /**
   * @return <code>true</code> if a border radius is defined, <code>false</code> if not.
   * @since v7.4.1
   */
  public final boolean hasBorderRadius ()
  {
    return m_fBorderRadius > 0f;
  }

  /**
   * Set the border radius to be used.
   *
   * @param fBorderRadius
   *        The actual border radius. Only values &gt; 0 will draw a radius.
   * @return this for chaining
   * @since v7.4.1
   */
  @NonNull
  public final IMPLTYPE setBorderRadius (final float fBorderRadius)
  {
    m_fBorderRadius = fBorderRadius;
    return thisAsT ();
  }

  @Override
  @NonNull
  public EChange visit (@NonNull final IPLVisitor aVisitor) throws IOException
  {
    EChange ret = super.visit (aVisitor);
    if (m_aElement != null)
    {
      if (m_aElement.visit (aVisitor).isChanged ())
      {
        ret = EChange.CHANGED;

        // Something changed in the contained element
        // E.g. in onBeforeRender for text elements with placeholder texts
        // replaced
        final SizeSpec aElementPreparedSize = m_aElement.getPreparedSize ();
        internalMarkAsNotPreparedDontPropagate ();
        internalSetElementPreparedSize (aElementPreparedSize);
        internalMarkAsPrepared (aElementPreparedSize.plus (m_aElement.getOutlineXSum (), m_aElement.getOutlineYSum ()));
      }
    }
    return ret;
  }

  /**
   * @return The prepared size of the contained element. May be <code>null</code> if this box was
   *         not yet prepared or if no element is contained.
   */
  @Nullable
  protected final SizeSpec getElementPreparedSize ()
  {
    return m_aElementPreparedSize;
  }

  protected final void internalSetElementPreparedSize (@Nullable final SizeSpec aSize)
  {
    m_aElementPreparedSize = aSize;
  }

  @Override
  @NonNull
  protected SizeSpec getRenderSize (@NonNull final SizeSpec aPreparedSize)
  {
    SizeSpec aRenderSize = super.getRenderSize (aPreparedSize);

    if (isFullWidth ())
    {
      // Change render size before render offset, so that internal alignment
      // works
      aRenderSize = aRenderSize.withWidth (getPrepareAvailableSize ().getWidth () - getOutlineXSum ());
    }

    // Handle horizontal and vertical alignment here
    m_aRenderOffset = new SizeSpec (getIndentX (aRenderSize.getWidth (), aPreparedSize.getWidth ()),
                                    getIndentY (aRenderSize.getHeight (), aPreparedSize.getHeight ()));

    return aRenderSize;
  }

  @Override
  protected SizeSpec onPrepare (@NonNull final PreparationContext aCtx)
  {
    if (m_aElement == null)
    {
      // No content - no size
      return SizeSpec.SIZE0;
    }

    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();
    final float fElementHeight = aCtx.getAvailableHeight () - getOutlineYSum ();

    final PreparationContext aElementCtx = new PreparationContext (aCtx.getGlobalContext (),
                                                                   fElementWidth,
                                                                   fElementHeight);
    internalSetElementPreparedSize (m_aElement.prepare (aElementCtx));

    // Add the outer stuff of the contained element as this elements prepared
    // size
    return new SizeSpec (m_aElementPreparedSize.getWidth () + m_aElement.getOutlineXSum (),
                         m_aElementPreparedSize.getHeight () + m_aElement.getOutlineYSum ());
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    internalSetElementPreparedSize (null);
    if (m_aElement instanceof AbstractPLRenderableObject <?>)
      ((AbstractPLRenderableObject <?>) m_aElement).internalMarkAsNotPrepared ();
  }

  @NonNull
  public final PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return PLSplitResult.allOnSecond ();

    final float fBoxHeight = getPreparedHeight ();
    if (fBoxHeight <= fAvailableHeight)
    {
      // Splitting makes no sense!
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this, "Splitting makes no sense, because Box 2 would be empty");
      return PLSplitResult.allOnFirst ();
    }

    final IPLRenderableObject <?> aElement = getElement ();

    // Create resulting VBoxes - the first one is not splittable again!
    final AbstractPLBox <?> aBox1 = internalCreateNewVertSplitObject (thisAsT ()).setID (getID () + "-1")
                                                                                 .setVertSplittable (false);
    final AbstractPLBox <?> aBox2 = internalCreateNewVertSplitObject (thisAsT ()).setID (getID () + "-2")
                                                                                 .setVertSplittable (true);

    // Set min width/max width from source
    // Don't use the height, because on vertically split elements, the height is
    // dynamic
    aBox1.setMinWidth (getMinWidth ());
    aBox1.setMaxWidth (getMaxWidth ());
    aBox2.setMinWidth (getMinWidth ());
    aBox2.setMaxWidth (getMaxWidth ());

    float fBox1UsedHeight = 0;
    float fBox2UsedHeight = 0;

    // Try split
    final float fSplitWidth = getElementPreparedSize ().getWidth ();
    final float fSplitHeight = fAvailableHeight - aElement.getOutlineYSum ();
    if (PLDebugLog.isDebugSplit ())
      PLDebugLog.debugSplit (this,
                             "Trying to split " +
                                   aElement.getDebugID () +
                                   " into pieces for split size " +
                                   PLDebugLog.getWH (fSplitWidth, fSplitHeight));

    // Try to split the element contained in the row
    final PLSplitResult aSplitResult = aElement.getAsSplittable ().splitElementVert (fSplitWidth, fSplitHeight);
    if (!aSplitResult.getSplitResultType ().isSplit ())
    {
      // Splitting makes no sense!
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this,
                               "Splitting makes no sense based on contained element (" +
                                     aSplitResult.getSplitResultType () +
                                     ")");
      return aSplitResult;
    }

    // Splitting succeeded
    final IPLRenderableObject <?> aBox1Element = aSplitResult.getFirstElement ().getElement ();
    aBox1.setElement (aBox1Element);
    fBox1UsedHeight += aSplitResult.getFirstElement ().getHeightFull ();
    final SizeSpec aBox1ElementPreparedSize = aSplitResult.getFirstElement ().getSize ();

    final IPLRenderableObject <?> aBox2Element = aSplitResult.getSecondElement ().getElement ();
    aBox2.setElement (aBox2Element);
    fBox2UsedHeight += aSplitResult.getSecondElement ().getHeightFull ();
    final SizeSpec aBox2ElementPreparedSize = aSplitResult.getSecondElement ().getSize ();

    if (PLDebugLog.isDebugSplit ())
      PLDebugLog.debugSplit (this,
                             "Split box element " +
                                   aElement.getDebugID () +
                                   " into pieces: " +
                                   aBox1Element.getDebugID () +
                                   " (" +
                                   aSplitResult.getFirstElement ().getWidth () +
                                   " + " +
                                   aBox1Element.getOutlineXSum () +
                                   " & " +
                                   aSplitResult.getFirstElement ().getHeight () +
                                   " + " +
                                   aBox1Element.getOutlineYSum () +
                                   ") and " +
                                   aBox2Element.getDebugID () +
                                   " (" +
                                   aSplitResult.getSecondElement ().getWidth () +
                                   " + " +
                                   aBox2Element.getOutlineXSum () +
                                   " & " +
                                   aSplitResult.getSecondElement ().getHeight () +
                                   " + " +
                                   aBox2Element.getOutlineYSum () +
                                   ")");

    // Excluding padding/margin
    aBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox1UsedHeight));
    aBox1.internalSetElementPreparedSize (aBox1ElementPreparedSize);

    aBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox2UsedHeight));
    aBox2.internalSetElementPreparedSize (aBox2ElementPreparedSize);

    return PLSplitResult.createSplit (new PLElementWithSize (aBox1, new SizeSpec (fAvailableWidth, fBox1UsedHeight)),
                                      new PLElementWithSize (aBox2, new SizeSpec (fAvailableWidth, fBox2UsedHeight)));
  }

  @OverrideOnDemand
  protected void renderShape (@NonNull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    if (hasBorderRadius ())
    {
      fillAndRenderBorderRounded (thisAsT (),
                                  aCtx,
                                  0f,
                                  0f,
                                  m_fBorderRadius,
                                  m_fBorderRadius,
                                  m_fBorderRadius,
                                  m_fBorderRadius);
    }
    else
      PLRenderHelper.fillAndRenderBorder (thisAsT (), aCtx, 0f, 0f);
  }

  @OverrideOnDemand
  protected void clipShape (@NonNull final PageRenderContext aCtx,
                            final float fLeft,
                            final float fBottom,
                            final float fWidth,
                            final float fHeight) throws IOException
  {
    final PDPageContentStreamWithCache aCSWC = aCtx.getContentStream ();
    aCSWC.saveGraphicsState ();
    if (hasBorderRadius ())
    {
      aCSWC.drawRoundedRect (fLeft,
                             fBottom,
                             fWidth,
                             fHeight,
                             m_fBorderRadius,
                             m_fBorderRadius,
                             m_fBorderRadius,
                             m_fBorderRadius);
    }
    else
    {
      aCSWC.addRect (fLeft, fBottom, fWidth, fHeight);
    }
    aCSWC.clip ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    renderShape (aCtx);

    if (m_aElement != null)
    {
      final float fStartLeft = aCtx.getStartLeft () + getOutlineLeft () + m_aRenderOffset.getWidth ();
      final float fStartTop = aCtx.getStartTop () - getOutlineTop () - m_aRenderOffset.getHeight ();
      final float fRenderWidth = getRenderWidth ();
      final float fRenderHeight = getRenderHeight ();

      final PDPageContentStreamWithCache aCSWC = aCtx.getContentStream ();
      final boolean bClipContent = isClipContent ();
      if (bClipContent)
      {
        clipShape (aCtx, fStartLeft, fStartTop - fRenderHeight, fRenderWidth, fRenderHeight);
      }

      final PageRenderContext aElementCtx = new PageRenderContext (aCtx,
                                                                   fStartLeft,
                                                                   fStartTop,
                                                                   fRenderWidth,
                                                                   fRenderHeight);
      m_aElement.render (aElementCtx);

      if (bClipContent)
      {
        aCSWC.restoreGraphicsState ();
      }
    }
    else
      PLDebugLog.debugRender (this, "Not rendering the box, because no element is contained");
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("Element", m_aElement)
                            .append ("VertSplittable", m_bVertSplittable)
                            .append ("BorderRadius", m_fBorderRadius)
                            .appendIfNotNull ("ElementPreparedSize", m_aElementPreparedSize)
                            .appendIfNotNull ("RenderOffset", m_aRenderOffset)
                            .getToString ();
  }
}
