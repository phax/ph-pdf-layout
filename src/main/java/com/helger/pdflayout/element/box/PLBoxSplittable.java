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

import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.spec.SizeSpec;

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

  public boolean isHorzSplittable ()
  {
    // Empty boxes or boxes with a non-splittable element cannot be split
    return hasElement () && getElement ().isHorzSplittable ();
  }

  @Nullable
  public PLSplitResult splitElementHorz (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    final IPLRenderableObject <?> aElement = getElement ();

    // Create resulting VBoxes - the first one is not splittable again!
    final PLBox aBox1 = new PLBox ().setBasicDataFrom (this);
    final PLBoxSplittable aBox2 = new PLBoxSplittable ().setBasicDataFrom (this);

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
      final PLSplitResult aSplitResult = aElement.getAsSplittable ().splitElementHorz (fSplitWidth, fSplitHeight);
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
}
