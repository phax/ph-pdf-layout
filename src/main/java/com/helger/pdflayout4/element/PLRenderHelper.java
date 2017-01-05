/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.pdflayout4.PLDebug;
import com.helger.pdflayout4.base.IPLElement;
import com.helger.pdflayout4.base.IPLObject;
import com.helger.pdflayout4.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.spec.BorderSpec;
import com.helger.pdflayout4.spec.BorderStyleSpec;

@Immutable
public final class PLRenderHelper
{
  private PLRenderHelper ()
  {}

  /**
   * Should a debug border be drawn? Only if no other border is present.
   *
   * @param aBorder
   *        The element border. May not be <code>null</code>.
   * @param bDebug
   *        <code>true</code> if debug mode is enabled
   * @return <code>true</code> if a debug border should be drawn
   */
  public static boolean shouldApplyDebugBorder (@Nonnull final BorderSpec aBorder, final boolean bDebug)
  {
    return !aBorder.hasAnyBorder () && bDebug;
  }

  /**
   * Render a single border
   *
   * @param aElement
   *        The element currently rendered. May not be <code>null</code>.
   * @param aContentStream
   *        Content stream
   * @param fLeft
   *        Left position (including left border width)
   * @param fTop
   *        Top position (including top border width)
   * @param fWidth
   *        Width (excluding left and right border width)
   * @param fHeight
   *        Height (excluding top and bottom border width)
   * @param aBorder
   *        Border to use. May not be <code>null</code>.
   * @throws IOException
   *         In case of a PDFBox error
   */
  public static void renderBorder (@Nonnull final IPLObject <?> aElement,
                                   @Nonnull final PDPageContentStreamWithCache aContentStream,
                                   final float fLeft,
                                   final float fTop,
                                   final float fWidth,
                                   final float fHeight,
                                   @Nonnull final BorderSpec aBorder) throws IOException
  {
    final float fRight = fLeft + fWidth;
    final float fBottom = fTop - fHeight;

    if (aBorder.hasAllBorders () && aBorder.areAllBordersEqual ())
    {
      // draw full rect
      final BorderStyleSpec aAll = aBorder.getLeft ();
      // The border position must be in the middle of the line
      final float fLineWidth = aAll.getLineWidth ();
      final float fHalfLineWidth = fLineWidth / 2f;

      if (PLDebug.isDebugRender ())
        PLDebug.debugRender (aElement,
                             "Border around " +
                                       PLDebug.getXYWH (fLeft, fTop, fWidth, fHeight) +
                                       " with line width " +
                                       fLineWidth);

      aContentStream.setStrokingColor (aAll.getColor ());
      aContentStream.setLineDashPattern (aAll.getLineDashPattern ());
      aContentStream.setLineWidth (fLineWidth);
      aContentStream.addRect (fLeft +
                              fHalfLineWidth,
                              fBottom + fHalfLineWidth,
                              fWidth - fLineWidth,
                              fHeight - fLineWidth);
      aContentStream.stroke ();
    }
    else
      if (aBorder.hasAnyBorder ())
      {
        // partially
        final BorderStyleSpec aTop = aBorder.getTop ();
        final BorderStyleSpec aRight = aBorder.getRight ();
        final BorderStyleSpec aBottom = aBorder.getBottom ();
        final BorderStyleSpec aLeft = aBorder.getLeft ();
        final float fTopWidth = aTop == null ? 0 : aTop.getLineWidth ();
        final float fRightWidth = aRight == null ? 0 : aRight.getLineWidth ();
        final float fBottomWidth = aBottom == null ? 0 : aBottom.getLineWidth ();
        final float fLeftWidth = aLeft == null ? 0 : aLeft.getLineWidth ();

        if (aTop != null)
        {
          if (PLDebug.isDebugRender ())
            PLDebug.debugRender (aElement,
                                 "Border top " +
                                           PLDebug.getXYWH (fLeft, fTop, fWidth, 0) +
                                           " with line width " +
                                           fTopWidth);

          final float fDelta = fTopWidth / 2f;
          aContentStream.setStrokingColor (aTop.getColor ());
          aContentStream.setLineDashPattern (aTop.getLineDashPattern ());
          aContentStream.setLineWidth (fTopWidth);
          aContentStream.drawLine (fLeft, fTop - fDelta, fRight - fRightWidth, fTop - fDelta, false);
        }

        if (aRight != null)
        {
          if (PLDebug.isDebugRender ())
            PLDebug.debugRender (aElement,
                                 "Border right " +
                                           PLDebug.getXYWH (fRight, fTop, 0, fHeight) +
                                           " with line width " +
                                           fRightWidth);

          final float fDelta = fRightWidth / 2f;
          aContentStream.setStrokingColor (aRight.getColor ());
          aContentStream.setLineDashPattern (aRight.getLineDashPattern ());
          aContentStream.setLineWidth (fRightWidth);
          aContentStream.drawLine (fRight - fDelta, fTop, fRight - fDelta, fBottom + fBottomWidth, false);
        }

        if (aBottom != null)
        {
          if (PLDebug.isDebugRender ())
            PLDebug.debugRender (aElement,
                                 "Border bottom " +
                                           PLDebug.getXYWH (fLeft, fBottom, fWidth, 0) +
                                           " with line width " +
                                           fBottomWidth);

          final float fDelta = fBottomWidth / 2f;
          aContentStream.setStrokingColor (aBottom.getColor ());
          aContentStream.setLineDashPattern (aBottom.getLineDashPattern ());
          aContentStream.setLineWidth (fBottomWidth);
          aContentStream.drawLine (fLeft + fLeftWidth, fBottom + fDelta, fRight, fBottom + fDelta, false);
        }

        if (aLeft != null)
        {
          if (PLDebug.isDebugRender ())
            PLDebug.debugRender (aElement,
                                 "Border left " +
                                           PLDebug.getXYWH (fLeft, fTop, 0, fHeight) +
                                           " with line width " +
                                           fLeftWidth);

          final float fDelta = fLeftWidth / 2f;
          aContentStream.setStrokingColor (aLeft.getColor ());
          aContentStream.setLineDashPattern (aLeft.getLineDashPattern ());
          aContentStream.setLineWidth (fLeftWidth);
          aContentStream.drawLine (fLeft + fDelta, fTop - fTopWidth, fLeft + fDelta, fBottom, false);
        }
        aContentStream.stroke ();
      }
  }

  public static void fillAndRenderBorder (@Nonnull final IPLElement <?> aElement,
                                          @Nonnull final PageRenderContext aCtx,
                                          final float fIndentX,
                                          final float fIndentY) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();

    // Border starts after margin
    final float fLeft = aCtx.getStartLeft () + aElement.getMarginLeft () + fIndentX;
    final float fTop = aCtx.getStartTop () - aElement.getMarginTop () - fIndentY;
    final float fWidth = aElement.getRenderWidth () + aElement.getBorderXSumWidth () + aElement.getPaddingXSum ();
    final float fHeight = aElement.getRenderHeight () + aElement.getBorderYSumWidth () + aElement.getPaddingYSum ();

    // Fill before border
    final Color aFillColor = aElement.getFillColor ();
    if (aFillColor != null)
    {
      aContentStream.setNonStrokingColor (aFillColor);
      aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
    }

    // Border draws over fill, to avoid nasty display problems if the background
    // is visible between them
    BorderSpec aRealBorder = aElement.getBorder ();
    if (shouldApplyDebugBorder (aRealBorder, aCtx.isDebugMode ()))
      aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_ELEMENT));
    if (aRealBorder.hasAnyBorder ())
      renderBorder (aElement, aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
  }
}
