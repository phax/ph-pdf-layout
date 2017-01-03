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
package com.helger.pdflayout4.element.table;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.base.PLElementWithSize;
import com.helger.pdflayout4.base.PLSplitResult;
import com.helger.pdflayout4.element.box.AbstractPLBox;
import com.helger.pdflayout4.spec.SizeSpec;

/**
 * This class represents a single table cell within a table row.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLTableCell extends AbstractPLBox <PLTableCell>
{
  public static final int DEFAULT_COL_SPAN = 1;

  private int m_nColSpan;

  public PLTableCell (@Nullable final IPLRenderableObject <?> aElement)
  {
    this (aElement, DEFAULT_COL_SPAN);
  }

  public PLTableCell (@Nullable final IPLRenderableObject <?> aElement, @Nonnegative final int nColSpan)
  {
    super (aElement);
    _setColSpan (nColSpan);
    setVertSplittable (true);
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLTableCell setBasicDataFrom (@Nonnull final PLTableCell aSource)
  {
    super.setBasicDataFrom (aSource);
    _setColSpan (aSource.getColSpan ());
    return this;
  }

  @Nonnegative
  public int getColSpan ()
  {
    return m_nColSpan;
  }

  private void _setColSpan (@Nonnegative final int nColSpan)
  {
    ValueEnforcer.isGT0 (nColSpan, "ColSpan");
    m_nColSpan = nColSpan;
  }

  @Override
  @Nonnull
  public PLTableCell internalCreateNewObject (@Nonnull final PLTableCell aBase)
  {
    return new PLTableCell (null, aBase.getColSpan ());
  }

  @Override
  @Nullable
  public PLSplitResult splitElementVert (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (true)
      return super.splitElementVert (fAvailableWidth, fAvailableHeight);

    final IPLRenderableObject <?> aElement = getElement ();

    // Create resulting VBoxes - the first one is not splittable again!
    final PLTableCell aBox1 = internalCreateNewObject (thisAsT ()).setBasicDataFrom (this)
                                                                  .setID (getID () + "-1")
                                                                  .setVertSplittable (false);
    final PLTableCell aBox2 = internalCreateNewObject (thisAsT ()).setBasicDataFrom (this)
                                                                  .setID (getID () + "-2")
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

    SizeSpec aBox1ElementPreparedSize = null;
    SizeSpec aBox2ElementPreparedSize = null;

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
    final PLSplitResult aSplitResult = fSplitHeight <= 0 ? null : aElement.getAsSplittable ()
                                                                          .splitElementVert (fSplitWidth, fSplitHeight);
    if (aSplitResult == null)
    {
      // No splitting - so create and empty second box
      aBox1.setElement (aElement);
      aBox1.setBasicDataFrom (this);
      fBox1UsedHeight += aElement.getPreparedHeight () + aElement.getOutlineYSum ();
      aBox1ElementPreparedSize = aElement.getPreparedSize ();

      aBox2.setElement (null);
      aBox2.setBasicDataFrom (this);
      fBox2UsedHeight += 0;
      aBox2ElementPreparedSize = SizeSpec.SIZE0;
    }
    else
    {
      // Splitting succeeded
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

    // Excluding padding/margin
    aBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox1UsedHeight));
    aBox1.internalSetElementPreparedSize (aBox1ElementPreparedSize);

    aBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fBox2UsedHeight));
    aBox2.internalSetElementPreparedSize (aBox2ElementPreparedSize);

    return new PLSplitResult (new PLElementWithSize (aBox1, new SizeSpec (fAvailableWidth, fBox1UsedHeight)),
                              new PLElementWithSize (aBox2, new SizeSpec (fAvailableWidth, fBox2UsedHeight)));
  }
}
