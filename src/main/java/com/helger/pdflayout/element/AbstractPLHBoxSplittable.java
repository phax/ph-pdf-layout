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
package com.helger.pdflayout.element;

import javax.annotation.Nullable;

import com.helger.commons.collection.ArrayHelper;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * Horizontal box - groups several columns.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLHBoxSplittable <IMPLTYPE extends AbstractPLHBoxSplittable <IMPLTYPE>>
                                               extends AbstractPLHBox <IMPLTYPE> implements IPLSplittableElement
{
  public AbstractPLHBoxSplittable ()
  {}

  public boolean containsAnySplittableElement ()
  {
    for (final PLHBoxColumn aColumn : m_aColumns)
      if (aColumn.getElement ().isSplittable ())
        return true;
    return false;
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
        PLDebug.debugSplit (this, "cannot split because no splittable elements are contained");
      return null;
    }

    final int nCols = m_aColumns.size ();

    boolean bAnySplittingPossible = false;
    for (int i = 0; i < nCols; ++i)
    {
      // Is the current element higher and splittable?
      final AbstractPLElement <?> aColumnElement = getColumnElementAtIndex (i);
      if (aColumnElement.isSplittable ())
      {
        final float fColumnHeightFull = m_aPreparedColumnHeight[i] + aColumnElement.getFullYSum ();
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

    final PLHBoxSplittable aHBox1 = new PLHBoxSplittable ();
    aHBox1.setBasicDataFrom (this);
    final PLHBoxSplittable aHBox2 = new PLHBoxSplittable ();
    aHBox2.setBasicDataFrom (this);

    // Fill all columns with empty content
    for (int i = 0; i < nCols; ++i)
    {
      final PLHBoxColumn aColumn = getColumnAtIndex (i);
      final WidthSpec aColumnWidth = aColumn.getWidth ();
      final AbstractPLElement <?> aColumnElement = aColumn.getElement ();

      // Create empty element with the same padding and margin as the original
      // element
      final PLSpacerX aEmptyElement = new PLSpacerX ();
      aEmptyElement.setPadding (aColumnElement.getPadding ());
      aEmptyElement.setMargin (aColumnElement.getMargin ());
      aEmptyElement.markAsPrepared (new SizeSpec (m_aPreparedColumnWidth[i], 0));

      aHBox1.addColumn (aEmptyElement, aColumnWidth);
      aHBox2.addColumn (aEmptyElement, aColumnWidth);
    }

    float fHBox1MaxHeight = 0;
    float fHBox2MaxHeight = 0;
    final float [] fHBox1Heights = new float [m_aPreparedColumnHeight.length];
    final float [] fHBox2Heights = new float [m_aPreparedColumnHeight.length];

    // Start splitting columns
    boolean bDidSplitAnyColumn = false;
    for (int nCol = 0; nCol < nCols; nCol++)
    {
      final AbstractPLElement <?> aColumnElement = getColumnElementAtIndex (nCol);
      final boolean bIsSplittable = aColumnElement.isSplittable ();
      final float fColumnWidth = m_aPreparedColumnWidth[nCol];
      @SuppressWarnings ("unused")
      final float fColumnWidthFull = fColumnWidth + aColumnElement.getFullXSum ();
      final float fColumnHeight = m_aPreparedColumnHeight[nCol];
      final float fColumnHeightFull = fColumnHeight + aColumnElement.getFullYSum ();

      boolean bDidSplitColumn = false;
      if (fColumnHeightFull > fAvailableHeight && bIsSplittable)
      {
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Trying to split " +
                                    aColumnElement.getDebugID () +
                                    " into pieces for remaining width " +
                                    fColumnWidth +
                                    " and height " +
                                    fAvailableHeight);

        // Use width and height without padding and margin!
        final PLSplitResult aSplitResult = aColumnElement.getAsSplittable ()
                                                         .splitElements (fColumnWidth,
                                                                         fAvailableHeight -
                                                                                       aColumnElement.getFullYSum ());

        if (aSplitResult != null)
        {
          final AbstractPLElement <?> aHBox1Element = aSplitResult.getFirstElement ().getElement ();
          aHBox1.getColumnAtIndex (nCol).setElement (aHBox1Element);

          final AbstractPLElement <?> aHBox2Element = aSplitResult.getSecondElement ().getElement ();
          aHBox2.getColumnAtIndex (nCol).setElement (aHBox2Element);

          // Use the full height, because the column itself has no padding or
          // margin!
          fHBox1Heights[nCol] = aSplitResult.getFirstElement ().getHeightFull ();
          fHBox2Heights[nCol] = aSplitResult.getSecondElement ().getHeightFull ();
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
        if (fColumnHeightFull > fAvailableHeight)
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
                                        (fColumnHeightFull - fAvailableHeight) +
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
                                        (fColumnHeightFull - fAvailableHeight) +
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

        // Use the full height, because the column itself has no padding or
        // margin!
        fHBox1Heights[nCol] = Math.min (fColumnHeightFull, fAvailableHeight);
        fHBox2Heights[nCol] = 0;
      }

      // calculate max column height
      fHBox1MaxHeight = Math.max (fHBox1MaxHeight, fHBox1Heights[nCol]);
      fHBox2MaxHeight = Math.max (fHBox2MaxHeight, fHBox2Heights[nCol]);
    }

    if (!bDidSplitAnyColumn)
    {
      // Nothing was splitted
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Weird: No column was split and the height is OK!");
      return null;
    }

    // mark new hboxes as prepared
    aHBox1.markAsPrepared (new SizeSpec (fElementWidth, fHBox1MaxHeight));
    aHBox2.markAsPrepared (new SizeSpec (fElementWidth, fHBox2MaxHeight));
    // reuse prepared widths - nothing changed here
    aHBox1.m_aPreparedColumnWidth = ArrayHelper.getCopy (m_aPreparedColumnWidth);
    aHBox2.m_aPreparedColumnWidth = ArrayHelper.getCopy (m_aPreparedColumnWidth);
    // set all column heights
    aHBox1.m_aPreparedColumnHeight = fHBox1Heights;
    aHBox2.m_aPreparedColumnHeight = fHBox2Heights;

    return new PLSplitResult (new PLElementWithSize (aHBox1, new SizeSpec (fElementWidth, fHBox1MaxHeight)),
                              new PLElementWithSize (aHBox2, new SizeSpec (fElementWidth, fHBox2MaxHeight)));
  }
}
