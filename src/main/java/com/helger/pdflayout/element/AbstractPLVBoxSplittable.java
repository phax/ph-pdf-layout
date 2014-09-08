/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.lang.CGStringHelper;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Vertical box - groups several rows.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLVBoxSplittable <IMPLTYPE extends AbstractPLVBoxSplittable <IMPLTYPE>> extends AbstractPLVBox <IMPLTYPE> implements IPLSplittableElement
{
  public AbstractPLVBoxSplittable ()
  {}

  public boolean containsAnySplittableElement ()
  {
    for (final Row aRow : m_aRows)
      if (aRow.getElement ().isSplittable ())
        return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  private static float [] _getAsArray (@Nonnull final List <Float> aList)
  {
    return TypeConverter.convertIfNecessary (aList, float [].class);
  }

  @Nullable
  public PLSplitResult splitElements (final float fElementWidth, final float fAvailableHeight)
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
    final List <Float> aVBox1RowWidth = new ArrayList <Float> (nTotalRows);
    final List <Float> aVBox1RowHeight = new ArrayList <Float> (nTotalRows);

    // What we need to know
    float fVBox1Width = 0;
    float fVBox1WidthFull = 0;
    float fVBox1Height = 0;
    float fVBox1HeightFull = 0;
    float fVBox2Width = 0;
    float fVBox2Height = 0;
    final List <Float> aVBox2RowWidth = new ArrayList <Float> (aVBox1RowWidth);
    final List <Float> aVBox2RowHeight = new ArrayList <Float> (aVBox1RowHeight);

    // Copy all content rows
    boolean bOnTable1 = true;

    for (int nRow = 0; nRow < nTotalRows; ++nRow)
    {
      final AbstractPLElement <?> aRowElement = getRowElementAtIndex (nRow);
      final float fRowWidth = m_aPreparedWidth[nRow];
      final float fRowWidthFull = fRowWidth + aRowElement.getMarginPlusPaddingXSum ();
      final float fRowHeight = m_aPreparedHeight[nRow];
      final float fRowHeightFull = fRowHeight + aRowElement.getMarginPlusPaddingYSum ();

      if (bOnTable1)
      {
        if (fVBox1HeightFull + fRowHeightFull <= fAvailableHeight)
        {
          // Row fits in first VBox without a change
          aVBox1.addRow (aRowElement);
          fVBox1Width = Math.max (fVBox1Width, fRowWidth);
          fVBox1WidthFull = Math.max (fVBox1WidthFull, fRowWidthFull);
          fVBox1Height += fRowHeight;
          fVBox1HeightFull += fRowHeightFull;
          aVBox1RowWidth.add (Float.valueOf (fRowWidth));
          aVBox1RowHeight.add (Float.valueOf (fRowHeight));
        }
        else
        {
          // Row does not fit - check if it can be splitted
          bOnTable1 = false;
          // try to split the row
          boolean bSplittedRow = false;
          if (aRowElement.isSplittable ())
          {
            // don't override fVBox1Width
            final float fWidth = Math.max (fVBox1Width, fRowWidth);
            final float fWidthFull = Math.max (fVBox1WidthFull, fRowWidthFull);
            final float fRemainingHeight = fAvailableHeight - fVBox1HeightFull;

            // Try to split the element contained in the row
            final PLSplitResult aSplitResult = aRowElement.getAsSplittable ().splitElements (fWidth, fRemainingHeight);

            if (aSplitResult != null)
            {
              final AbstractPLElement <?> aVBox1RowElement = aSplitResult.getFirstElement ().getElement ();
              aVBox1.addRow (aVBox1RowElement);
              fVBox1Width = fWidth;
              fVBox1WidthFull = fWidthFull;
              final float fVBox1RowHeight = aSplitResult.getFirstElement ().getHeight ();
              fVBox1Height += fVBox1RowHeight;
              fVBox1HeightFull += fVBox1RowHeight + aVBox1RowElement.getMarginPlusPaddingYSum ();
              aVBox1RowWidth.add (Float.valueOf (fWidth));
              aVBox1RowHeight.add (Float.valueOf (fVBox1RowHeight));

              final AbstractPLElement <?> aVBox2RowElement = aSplitResult.getSecondElement ().getElement ();
              aVBox2.addRow (aVBox2RowElement);
              fVBox2Width = fWidth;
              final float fVBox2RowHeight = aSplitResult.getSecondElement ().getHeight ();
              fVBox2Height += fVBox2RowHeight;
              aVBox2RowWidth.add (Float.valueOf (fWidth));
              aVBox2RowHeight.add (Float.valueOf (fVBox2RowHeight));

              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this, "Split row element " +
                                          CGStringHelper.getClassLocalName (aRowElement) +
                                          "-" +
                                          aRowElement.getID () +
                                          " (Row " +
                                          nRow +
                                          ") into pieces: " +
                                          aVBox1RowElement.getID () +
                                          " (" +
                                          aSplitResult.getFirstElement ().getHeight () +
                                          ") and " +
                                          aVBox2RowElement.getID () +
                                          " (" +
                                          aSplitResult.getSecondElement ().getHeight () +
                                          ") for remaining height " +
                                          fRemainingHeight);
              bSplittedRow = true;
            }
            else
            {
              if (PLDebug.isDebugSplit ())
                PLDebug.debugSplit (this,
                                    "Failed to split row element " +
                                        CGStringHelper.getClassLocalName (aRowElement) +
                                        "-" +
                                        aRowElement.getID () +
                                        " (Row " +
                                        nRow +
                                        ") into pieces for remaining height " +
                                        fRemainingHeight);
            }
          }

          if (!bSplittedRow)
          {
            // just add the full row to the second VBox since the row does not
            // fit on first page
            aVBox2.addRow (aRowElement);
            fVBox2Width = Math.max (fVBox2Width, fRowWidth);
            fVBox2Height += fRowHeight;
            aVBox2RowWidth.add (Float.valueOf (fRowWidth));
            aVBox2RowHeight.add (Float.valueOf (fRowHeight));
          }
        }
      }
      else
      {
        // We're already on VBox 2 - add all elements, since VBox2 may be split
        // again!
        aVBox2.addRow (aRowElement);
        fVBox2Width = Math.max (fVBox2Width, fRowWidth);
        fVBox2Height += fRowHeight;
        aVBox2RowWidth.add (Float.valueOf (fRowWidth));
        aVBox2RowHeight.add (Float.valueOf (fRowHeight));
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
    aVBox1.markAsPrepared (new SizeSpec (fElementWidth, fVBox1Height));
    aVBox1.m_aPreparedWidth = _getAsArray (aVBox1RowWidth);
    aVBox1.m_aPreparedHeight = _getAsArray (aVBox1RowHeight);

    aVBox2.markAsPrepared (new SizeSpec (fElementWidth, fVBox2Height));
    aVBox2.m_aPreparedWidth = _getAsArray (aVBox2RowWidth);
    aVBox2.m_aPreparedHeight = _getAsArray (aVBox2RowHeight);

    return new PLSplitResult (new PLElementWithSize (aVBox1, new SizeSpec (fElementWidth, fVBox1Height)),
                              new PLElementWithSize (aVBox2, new SizeSpec (fElementWidth, fVBox2Height)));
  }
}
