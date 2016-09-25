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
package com.helger.pdflayout.element.hbox;

import javax.annotation.Nullable;

import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.special.PLSpacerX;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Horizontal box - groups several columns.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLHBoxSplittable <IMPLTYPE extends AbstractPLHBoxSplittable <IMPLTYPE>> extends
                                               AbstractPLHBox <IMPLTYPE> implements IPLSplittableObject <IMPLTYPE>
{
  public AbstractPLHBoxSplittable ()
  {}

  public boolean containsAnySplittableElement ()
  {
    return m_aColumns.containsAny (x -> x.getElement ().isSplittable ());
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
        PLDebug.debugSplit (this, "cannot split because no splittable elements are contained");
      return null;
    }

    final int nCols = m_aColumns.size ();

    // Check if height is exceeded
    {
      boolean bAnySplittingPossible = false;
      for (int i = 0; i < nCols; ++i)
      {
        // Is the current element higher and splittable?
        final IPLRenderableObject <?> aColumnElement = getColumnElementAtIndex (i);
        if (aColumnElement.isSplittable ())
        {
          final float fColumnHeightFull = m_aPreparedColumnSize[i].getHeight ();
          if (fColumnHeightFull > fAvailableHeight)
          {
            bAnySplittingPossible = true;
            break;
          }
        }
      }

      if (!bAnySplittingPossible)
      {
        // Splitting makes no sense
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "no need to split because all splittable elements easily fit into the available height (" +
                                    fAvailableHeight +
                                    ")");
        return null;
      }
    }

    final PLHBoxSplittable aHBox1 = new PLHBoxSplittable ();
    aHBox1.setBasicDataFrom (this);
    final PLHBoxSplittable aHBox2 = new PLHBoxSplittable ();
    aHBox2.setBasicDataFrom (this);

    // Fill all columns with empty content
    for (int i = 0; i < nCols; ++i)
    {
      final PLHBoxColumn aColumn = getColumnAtIndex (i);
      final WidthSpec aColumnWidth = aColumn.getWidth ();

      // Create empty element with the same width as the original element
      final PLSpacerX aEmptyElement = new PLSpacerX ();
      aEmptyElement.internalMarkAsPrepared (new SizeSpec (m_aPreparedColumnSize[i].getWidth (), 0));

      aHBox1.addColumn (aEmptyElement, aColumnWidth);
      aHBox2.addColumn (aEmptyElement, aColumnWidth);
    }

    float fHBox1MaxHeight = 0;
    float fHBox2MaxHeight = 0;
    final SizeSpec [] fHBox1ColumnSizes = new SizeSpec [m_aPreparedColumnSize.length];
    final SizeSpec [] fHBox2ColumnSizes = new SizeSpec [m_aPreparedColumnSize.length];
    final SizeSpec [] fHBox1ElementSizes = new SizeSpec [m_aPreparedElementSize.length];
    final SizeSpec [] fHBox2ElementSizes = new SizeSpec [m_aPreparedElementSize.length];

    // Start splitting columns
    boolean bDidSplitAnyColumn = false;
    for (int nCol = 0; nCol < nCols; nCol++)
    {
      final IPLRenderableObject <?> aColumnElement = getColumnElementAtIndex (nCol);
      final boolean bIsSplittable = aColumnElement.isSplittable ();
      final float fColumnWidth = m_aPreparedColumnSize[nCol].getWidth ();
      final float fColumnHeight = m_aPreparedColumnSize[nCol].getHeight ();
      final float fElementWidth = m_aPreparedElementSize[nCol].getWidth ();

      boolean bDidSplitColumn = false;
      if (fColumnHeight > fAvailableHeight && bIsSplittable)
      {
        final float fSplitWidth = fElementWidth;
        final float fSplitHeight = fAvailableHeight - aColumnElement.getFullYSum ();
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Trying to split " +
                                    aColumnElement.getDebugID () +
                                    " into pieces for remaining size " +
                                    PLDebug.getWH (fSplitWidth, fSplitHeight));

        // Use width and height without padding and margin!
        final PLSplitResult aSplitResult = aColumnElement.getAsSplittable ().splitElements (fSplitWidth, fSplitHeight);
        if (aSplitResult != null)
        {
          final IPLRenderableObject <?> aHBox1Element = aSplitResult.getFirstElement ().getElement ();
          aHBox1.getColumnAtIndex (nCol).setElement (aHBox1Element);

          final IPLRenderableObject <?> aHBox2Element = aSplitResult.getSecondElement ().getElement ();
          aHBox2.getColumnAtIndex (nCol).setElement (aHBox2Element);

          // Use the full height, because the column itself has no padding or
          // margin!
          fHBox1ColumnSizes[nCol] = new SizeSpec (fColumnWidth, aSplitResult.getFirstElement ().getHeightFull ());
          fHBox2ColumnSizes[nCol] = new SizeSpec (fColumnWidth, aSplitResult.getSecondElement ().getHeightFull ());
          fHBox1ElementSizes[nCol] = new SizeSpec (fElementWidth, aSplitResult.getFirstElement ().getHeight ());
          fHBox2ElementSizes[nCol] = new SizeSpec (fElementWidth, aSplitResult.getSecondElement ().getHeight ());
          bDidSplitColumn = true;
          bDidSplitAnyColumn = true;

          if (PLDebug.isDebugSplit ())
            PLDebug.debugSplit (this,
                                "Split column element " +
                                      aColumnElement.getDebugID () +
                                      " (Column " +
                                      nCol +
                                      ") into pieces: " +
                                      aHBox1Element.getDebugID () +
                                      " (" +
                                      aSplitResult.getFirstElement ().getWidth () +
                                      "+" +
                                      aHBox1Element.getFullXSum () +
                                      " & " +
                                      aSplitResult.getFirstElement ().getHeight () +
                                      "+" +
                                      aHBox1Element.getFullYSum () +
                                      ") and " +
                                      aHBox2Element.getDebugID () +
                                      " (" +
                                      aSplitResult.getSecondElement ().getWidth () +
                                      "+" +
                                      aHBox2Element.getFullXSum () +
                                      " & " +
                                      aSplitResult.getSecondElement ().getHeight () +
                                      "+" +
                                      aHBox2Element.getFullYSum () +
                                      ") for available height " +
                                      fAvailableHeight);
        }
        else
        {
          if (PLDebug.isDebugSplit ())
            PLDebug.debugSplit (this,
                                "Failed to split column element " +
                                      aColumnElement.getDebugID () +
                                      " (Column " +
                                      nCol +
                                      ") into pieces for available height " +
                                      fAvailableHeight);
        }
      }

      if (!bDidSplitColumn)
      {
        if (fColumnHeight > fAvailableHeight)
        {
          // We should have split but did not
          if (bIsSplittable)
          {
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Column " +
                                        nCol +
                                        " contains splittable element " +
                                        aColumnElement.getDebugID () +
                                        " which creates an overflow by " +
                                        (fColumnHeight - fAvailableHeight) +
                                        " for available height " +
                                        fAvailableHeight +
                                        "!");
          }
          else
          {
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Column " +
                                        nCol +
                                        " contains non splittable element " +
                                        aColumnElement.getDebugID () +
                                        " which creates an overflow by " +
                                        (fColumnHeight - fAvailableHeight) +
                                        " for max height " +
                                        fAvailableHeight +
                                        "!");
          }

          // One column of the row is too large and cannot be split -> the whole
          // row cannot be split!
          return null;
        }

        // No splitting and cell fits totally in available height
        aHBox1.getColumnAtIndex (nCol).setElement (aColumnElement);

        fHBox1ColumnSizes[nCol] = new SizeSpec (fColumnWidth, Math.min (fColumnHeight, fAvailableHeight));
        fHBox2ColumnSizes[nCol] = new SizeSpec (fColumnWidth, 0);
        fHBox1ElementSizes[nCol] = m_aPreparedElementSize[nCol];
        fHBox2ElementSizes[nCol] = new SizeSpec (fElementWidth, 0);
      }

      // calculate max column height
      fHBox1MaxHeight = Math.max (fHBox1MaxHeight, fHBox1ColumnSizes[nCol].getHeight ());
      fHBox2MaxHeight = Math.max (fHBox2MaxHeight, fHBox2ColumnSizes[nCol].getHeight ());
    }

    if (!bDidSplitAnyColumn)
    {
      // Nothing was splitted
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Weird: No column was split and the height is OK!");
      return null;
    }

    // mark new hboxes as prepared
    aHBox1.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fHBox1MaxHeight));
    aHBox2.internalMarkAsPrepared (new SizeSpec (fAvailableWidth, fHBox2MaxHeight));
    // set prepared column sizes
    aHBox1.m_aPreparedColumnSize = fHBox1ColumnSizes;
    aHBox2.m_aPreparedColumnSize = fHBox2ColumnSizes;
    // set prepared element sizes
    aHBox1.m_aPreparedElementSize = fHBox1ElementSizes;
    aHBox2.m_aPreparedElementSize = fHBox2ElementSizes;

    return new PLSplitResult (new PLElementWithSize (aHBox1, new SizeSpec (fAvailableWidth, fHBox1MaxHeight)),
                              new PLElementWithSize (aHBox2, new SizeSpec (fAvailableWidth, fHBox2MaxHeight)));
  }
}
