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
package com.helger.pdflayout.element.vbox;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Vertical box - groups several rows.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLVBoxSplittable <IMPLTYPE extends AbstractPLVBoxSplittable <IMPLTYPE>> extends
                                               AbstractPLVBox <IMPLTYPE> implements IPLSplittableObject <IMPLTYPE>
{
  public AbstractPLVBoxSplittable ()
  {}

  public boolean containsAnySplittableElement ()
  {
    return m_aRows.containsAny (r -> r.getElement ().isSplittable ());
  }

  @Nonnull
  @ReturnsMutableCopy
  private static float [] _getAsArray (@Nonnull final List <Float> aList)
  {
    return TypeConverter.convertIfNecessary (aList, float [].class);
  }

  @Nullable
  public PLSplitResult splitElements (final float fAvailableWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    if (!containsAnySplittableElement ())
    {
      // Splitting makes no sense
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Cannot split because no splittable elements are contained");
      return null;
    }

    // Create resulting VBoxes - the first one is not splittable again!
    final PLVBox aVBox1 = new PLVBox ().setBasicDataFrom (this);
    final PLVBoxSplittable aVBox2 = new PLVBoxSplittable ().setBasicDataFrom (this);

    final int nTotalRows = getRowCount ();
    final ICommonsList <SizeSpec> aVBox1RowSize = new CommonsArrayList<> (nTotalRows);
    final ICommonsList <SizeSpec> aVBox1ElementSize = new CommonsArrayList<> (nTotalRows);
    float fUsedVBox1RowHeight = 0;
    final ICommonsList <SizeSpec> aVBox2RowSize = new CommonsArrayList<> (nTotalRows);
    final ICommonsList <SizeSpec> aVBox2ElementSize = new CommonsArrayList<> (nTotalRows);
    float fUsedVBox2RowHeight = 0;

    // Copy all content rows
    boolean bOnVBox1 = true;

    for (int nRow = 0; nRow < nTotalRows; ++nRow)
    {
      final IPLRenderableObject <?> aRowElement = getRowElementAtIndex (nRow);
      final float fRowHeight = m_aPreparedRowSize[nRow].getHeight ();

      if (bOnVBox1)
      {
        if (fUsedVBox1RowHeight + fRowHeight <= fAvailableHeight)
        {
          // Row fits in first VBox without a change
          aVBox1.addRow (aRowElement);
          fUsedVBox1RowHeight += fRowHeight;
          // Use data as is
          aVBox1RowSize.add (m_aPreparedRowSize[nRow]);
          aVBox1ElementSize.add (m_aPreparedElementSize[nRow]);
        }
        else
        {
          // Row does not fit - check if it can be splitted
          bOnVBox1 = false;
          // try to split the row
          boolean bSplittedRow = false;
          if (aRowElement.isSplittable ())
          {
            final float fSplitWidth = m_aPreparedElementSize[nRow].getWidth ();
            final float fSplitHeight = fAvailableHeight - fUsedVBox1RowHeight - aRowElement.getOutlineYSum ();
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Trying to split " +
                                        aRowElement.getDebugID () +
                                        " into pieces for split size " +
                                        PLDebug.getWH (fSplitWidth, fSplitHeight));

            // Try to split the element contained in the row
            final PLSplitResult aSplitResult = aRowElement.getAsSplittable ().splitElements (fSplitWidth, fSplitHeight);
            if (aSplitResult != null)
            {
              final IPLRenderableObject <?> aVBox1RowElement = aSplitResult.getFirstElement ().getElement ();
              aVBox1.addRow (aVBox1RowElement);
              fUsedVBox1RowHeight += aSplitResult.getFirstElement ().getHeightFull ();
              aVBox1RowSize.add (aSplitResult.getFirstElement ().getSizeFull ());
              aVBox1ElementSize.add (aSplitResult.getFirstElement ().getSize ());

              final IPLRenderableObject <?> aVBox2RowElement = aSplitResult.getSecondElement ().getElement ();
              aVBox2.addRow (aVBox2RowElement);
              fUsedVBox2RowHeight += aSplitResult.getSecondElement ().getHeightFull ();
              aVBox2RowSize.add (aSplitResult.getSecondElement ().getSizeFull ());
              aVBox2ElementSize.add (aSplitResult.getSecondElement ().getSize ());

              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Split row element " +
                                          aRowElement.getDebugID () +
                                          " (Row " +
                                          nRow +
                                          ") into pieces: " +
                                          aVBox1RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getFirstElement ().getWidth () +
                                          "+" +
                                          aVBox1RowElement.getOutlineXSum () +
                                          " & " +
                                          aSplitResult.getFirstElement ().getHeight () +
                                          "+" +
                                          aVBox1RowElement.getOutlineYSum () +
                                          ") and " +
                                          aVBox2RowElement.getDebugID () +
                                          " (" +
                                          aSplitResult.getSecondElement ().getWidth () +
                                          "+" +
                                          aVBox2RowElement.getOutlineXSum () +
                                          " & " +
                                          aSplitResult.getSecondElement ().getHeight () +
                                          "+" +
                                          aVBox2RowElement.getOutlineYSum () +
                                          ")");
              bSplittedRow = true;
            }
            else
            {
              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Failed to split row element " +
                                          aRowElement.getDebugID () +
                                          " (Row " +
                                          nRow +
                                          ") into pieces");
            }
          }

          if (!bSplittedRow)
          {
            // just add the full row to the second VBox since the row does not
            // fit on first page
            aVBox2.addRow (aRowElement);
            fUsedVBox2RowHeight += fRowHeight;
            aVBox2RowSize.add (m_aPreparedRowSize[nRow]);
            aVBox2ElementSize.add (m_aPreparedElementSize[nRow]);
          }
        }
      }
      else
      {
        // We're already on VBox 2 - add all elements, since VBox2 may be split
        // again later!
        aVBox2.addRow (aRowElement);
        fUsedVBox2RowHeight += fRowHeight;
        aVBox2RowSize.add (m_aPreparedRowSize[nRow]);
        aVBox2ElementSize.add (m_aPreparedElementSize[nRow]);
      }
    }

    if (aVBox1.getRowCount () == 0)
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Splitting makes no sense, because VBox 1 would be empty");
      return null;
    }

    if (aVBox2.getRowCount () == 0)
    {
      // Splitting makes no sense!
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Splitting makes no sense, because VBox 2 would be empty");
      return null;
    }

    // Excluding padding/margin
    aVBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fUsedVBox1RowHeight));
    aVBox1.m_aPreparedRowSize = ArrayHelper.newArray (aVBox1RowSize, SizeSpec.class);
    aVBox1.m_aPreparedElementSize = ArrayHelper.newArray (aVBox1ElementSize, SizeSpec.class);

    aVBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fUsedVBox2RowHeight));
    aVBox2.m_aPreparedRowSize = ArrayHelper.newArray (aVBox2RowSize, SizeSpec.class);
    aVBox2.m_aPreparedElementSize = ArrayHelper.newArray (aVBox2ElementSize, SizeSpec.class);

    return new PLSplitResult (new PLElementWithSize (aVBox1, new SizeSpec (fAvailableWidth, fUsedVBox1RowHeight)),
                              new PLElementWithSize (aVBox2, new SizeSpec (fAvailableWidth, fUsedVBox2RowHeight)));
  }
}
