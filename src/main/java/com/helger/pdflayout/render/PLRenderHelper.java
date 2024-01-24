/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.render;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.pdflayout.base.IPLElement;
import com.helger.pdflayout.base.IPLHasFillColor;
import com.helger.pdflayout.base.IPLHasMarginBorderPadding;
import com.helger.pdflayout.base.IPLObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.debug.PLDebugRender;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;

@Immutable
public final class PLRenderHelper
{
  private PLRenderHelper ()
  {}

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

      if (PLDebugLog.isDebugRender ())
        PLDebugLog.debugRender (aElement,
                                "Border around " +
                                          PLDebugLog.getXYWH (fLeft, fTop, fWidth, fHeight) +
                                          " with line width " +
                                          fLineWidth);

      aContentStream.setStrokingColor (aAll.getColor ());
      aContentStream.setLineDashPattern (aAll.getLineDashPattern ());
      aContentStream.setLineWidth (fLineWidth);
      aContentStream.addRect (fLeft + fHalfLineWidth,
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
          if (PLDebugLog.isDebugRender ())
            PLDebugLog.debugRender (aElement,
                                    "Border top " +
                                              PLDebugLog.getXYWH (fLeft, fTop, fWidth, 0) +
                                              " with line width " +
                                              fTopWidth);

          final float fDelta = fTopWidth / 2f;
          aContentStream.setStrokingColor (aTop.getColor ());
          aContentStream.setLineDashPattern (aTop.getLineDashPattern ());
          aContentStream.setLineWidth (fTopWidth);
          aContentStream.drawLine (fLeft, fTop - fDelta, fRight - fRightWidth, fTop - fDelta);
          aContentStream.stroke ();
        }

        if (aRight != null)
        {
          if (PLDebugLog.isDebugRender ())
            PLDebugLog.debugRender (aElement,
                                    "Border right " +
                                              PLDebugLog.getXYWH (fRight, fTop, 0, fHeight) +
                                              " with line width " +
                                              fRightWidth);

          final float fDelta = fRightWidth / 2f;
          aContentStream.setStrokingColor (aRight.getColor ());
          aContentStream.setLineDashPattern (aRight.getLineDashPattern ());
          aContentStream.setLineWidth (fRightWidth);
          aContentStream.drawLine (fRight - fDelta, fTop, fRight - fDelta, fBottom + fBottomWidth);
          aContentStream.stroke ();
        }

        if (aBottom != null)
        {
          if (PLDebugLog.isDebugRender ())
            PLDebugLog.debugRender (aElement,
                                    "Border bottom " +
                                              PLDebugLog.getXYWH (fLeft, fBottom, fWidth, 0) +
                                              " with line width " +
                                              fBottomWidth);

          final float fDelta = fBottomWidth / 2f;
          aContentStream.setStrokingColor (aBottom.getColor ());
          aContentStream.setLineDashPattern (aBottom.getLineDashPattern ());
          aContentStream.setLineWidth (fBottomWidth);
          aContentStream.drawLine (fLeft + fLeftWidth, fBottom + fDelta, fRight, fBottom + fDelta);
          aContentStream.stroke ();
        }

        if (aLeft != null)
        {
          if (PLDebugLog.isDebugRender ())
            PLDebugLog.debugRender (aElement,
                                    "Border left " +
                                              PLDebugLog.getXYWH (fLeft, fTop, 0, fHeight) +
                                              " with line width " +
                                              fLeftWidth);

          final float fDelta = fLeftWidth / 2f;
          aContentStream.setStrokingColor (aLeft.getColor ());
          aContentStream.setLineDashPattern (aLeft.getLineDashPattern ());
          aContentStream.setLineWidth (fLeftWidth);
          aContentStream.drawLine (fLeft + fDelta, fTop - fTopWidth, fLeft + fDelta, fBottom);
          aContentStream.stroke ();
        }
      }
  }

  /**
   * Create the background fill (debug and real) and draw the border (debug and
   * real) of an element.
   *
   * @param aElement
   *        The element to be rendered. May not be <code>null</code>.
   * @param aCtx
   *        The render context incl. the content stream.
   * @param fIndentX
   *        Additional x-indentation
   * @param fIndentY
   *        Additional y-indentation
   * @throws IOException
   *         in case writing fails
   * @param <T>
   *        element type to render
   */
  public static <T extends IPLElement <T>> void fillAndRenderBorder (@Nonnull final T aElement,
                                                                     @Nonnull final PageRenderContext aCtx,
                                                                     final float fIndentX,
                                                                     final float fIndentY) throws IOException
  {
    // Border starts after margin
    final float fLeft = aCtx.getStartLeft () + aElement.getMarginLeft () + fIndentX;
    final float fTop = aCtx.getStartTop () - aElement.getMarginTop () - fIndentY;
    final float fWidth = aElement.getRenderWidth () + aElement.getBorderXSumWidth () + aElement.getPaddingXSum ();
    final float fHeight = aElement.getRenderHeight () + aElement.getBorderYSumWidth () + aElement.getPaddingYSum ();

    fillAndRenderBorder (aElement, fLeft, fTop, fWidth, fHeight, aCtx.getContentStream ());
  }

  /**
   * Create the background fill (debug and real) and draw the border (debug and
   * real) of an element.
   *
   * @param aElement
   *        The element to be rendered. May not be <code>null</code>.
   * @param fLeft
   *        left
   * @param fTop
   *        top
   * @param fWidth
   *        width
   * @param fHeight
   *        height
   * @param aContentStream
   *        Content stream to act on. May not be <code>null</code>.
   * @throws IOException
   *         in case writing fails
   * @param <T>
   *        Type that implements {@link IPLHasFillColor} and
   *        {@link IPLHasMarginBorderPadding}
   */
  public static <T extends IPLObject <T> & IPLHasFillColor <T> & IPLHasMarginBorderPadding <T>> void fillAndRenderBorder (@Nonnull final T aElement,
                                                                                                                          final float fLeft,
                                                                                                                          final float fTop,
                                                                                                                          final float fWidth,
                                                                                                                          final float fHeight,
                                                                                                                          @Nonnull final PDPageContentStreamWithCache aContentStream) throws IOException
  {
    final boolean bDebugRender = PLDebugRender.isDebugRender ();
    if (bDebugRender)
    {
      // Debug margin with a filled rectangle
      final PLColor aOutlineColor = PLDebugRender.getDebugOutlineColor (aElement);
      if (aOutlineColor != null)
      {
        aContentStream.setNonStrokingColor (aOutlineColor);
        aContentStream.fillRect (fLeft - aElement.getMarginLeft (),
                                 fTop - fHeight - aElement.getMarginBottom (),
                                 fWidth + aElement.getMarginXSum (),
                                 fHeight + aElement.getMarginYSum ());
      }
    }

    // Fill before border
    final PLColor aFillColor = aElement.getFillColor ();
    if (aFillColor != null)
    {
      aContentStream.setNonStrokingColor (aFillColor);
      aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
    }

    // Draw debug border first anyway, in case only partial borders are present
    if (bDebugRender)
    {
      final BorderSpec aDebugBorder = new BorderSpec (PLDebugRender.getDebugBorder (aElement));
      if (aDebugBorder.hasAnyBorder ())
        renderBorder (aElement, aContentStream, fLeft, fTop, fWidth, fHeight, aDebugBorder);
    }

    // Border draws over fill, to avoid nasty display problems if the background
    // is visible between then
    final BorderSpec aBorder = aElement.getBorder ();
    if (aBorder.hasAnyBorder ())
      renderBorder (aElement, aContentStream, fLeft, fTop, fWidth, fHeight, aBorder);
  }
}
