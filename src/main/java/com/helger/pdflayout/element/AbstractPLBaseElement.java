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

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;

/**
 * Abstract base class for a PDF layout (=PL) element that has margin, padding,
 * border and a fill color. It does not directly support rendering.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type of this class.
 */
public abstract class AbstractPLBaseElement <IMPLTYPE extends AbstractPLBaseElement <IMPLTYPE>>
                                            implements IPLHasFillColor <IMPLTYPE>, IPLHasMarginBorderPadding <IMPLTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPLBaseElement.class);

  private String m_sElementID;
  private transient String m_sDebugID;
  private MarginSpec m_aMargin = MarginSpec.MARGIN0;
  private PaddingSpec m_aPadding = PaddingSpec.PADDING0;
  private BorderSpec m_aBorder = BorderSpec.BORDER0;
  private Color m_aFillColor = null;

  public AbstractPLBaseElement ()
  {}

  /**
   * @return The unique element ID. Never <code>null</code>.
   */
  public final String getID ()
  {
    String ret = m_sElementID;
    if (ret == null)
      m_sElementID = ret = GlobalIDFactory.getNewStringID ();
    return ret;
  }

  @Nonnull
  public final IMPLTYPE setID (@Nonnull @Nonempty final String sID)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    if (StringHelper.hasText (m_sElementID))
    {
      s_aLogger.warn ("Overwriting ID '" + m_sElementID + "' with ID '" + sID + "'");
      // Disable caching
      m_sDebugID = null;
    }
    m_sElementID = sID;
    return thisAsT ();
  }

  @Nonnull
  @Nonempty
  public final String getDebugID ()
  {
    String ret = m_sDebugID;
    if (ret == null)
      m_sDebugID = ret = "<" + ClassHelper.getClassLocalName (this) + "-" + getID () + ">";
    return ret;
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLBaseElement <?> aSource)
  {
    setMargin (aSource.m_aMargin);
    setPadding (aSource.m_aPadding);
    setBorder (aSource.m_aBorder);
    setFillColor (aSource.m_aFillColor);
    return thisAsT ();
  }

  /**
   * Throw an exception, if this object is already prepared.
   *
   * @throws IllegalStateException
   *         if already prepared
   */
  @OverrideOnDemand
  protected void internlCheckNotPrepared ()
  {}

  /**
   * Set the margin values. This method may not be called after an element got
   * prepared!
   *
   * @param aMargin
   *        Margin to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    internlCheckNotPrepared ();
    m_aMargin = aMargin;
    return thisAsT ();
  }

  /**
   * @return The current margin. Never <code>null</code>.
   */
  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  /**
   * Set the padding values. This method may not be called after an element got
   * prepared!
   *
   * @param aPadding
   *        Padding to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    internlCheckNotPrepared ();
    m_aPadding = aPadding;
    return thisAsT ();
  }

  /**
   * @return The current padding. Never <code>null</code>.
   */
  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  /**
   * Set the border values. This method may not be called after an element got
   * prepared!
   *
   * @param aBorder
   *        Border to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    internlCheckNotPrepared ();
    m_aBorder = aBorder;
    return thisAsT ();
  }

  /**
   * @return The current border. Never <code>null</code>.
   */
  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  /**
   * Set the element fill color.
   *
   * @param aFillColor
   *        The fill color to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setFillColor (@Nullable final Color aFillColor)
  {
    m_aFillColor = aFillColor;
    return thisAsT ();
  }

  /**
   * @return The current fill color. May be <code>null</code>.
   */
  @Nullable
  public Color getFillColor ()
  {
    return m_aFillColor;
  }

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
  protected void renderBorder (@Nonnull final PDPageContentStreamWithCache aContentStream,
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
        PLDebug.debugRender (this,
                             "Border around " +
                                   PLDebug.getXYWH (fLeft, fTop, fWidth, fHeight) +
                                   " with line width " +
                                   fLineWidth);

      aContentStream.setStrokingColor (aAll.getColor ());
      aContentStream.setLineDashPattern (aAll.getLineDashPattern ());
      aContentStream.setLineWidth (fLineWidth);
      aContentStream.addRect (fLeft -
                              fHalfLineWidth,
                              fBottom - fHalfLineWidth,
                              fWidth + fLineWidth,
                              fHeight + fLineWidth);
      aContentStream.stroke ();
    }
    else
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
          PLDebug.debugRender (this,
                               "Border top " +
                                     PLDebug.getXYWH (fLeft, fTop, fWidth, 0) +
                                     " with line width " +
                                     fTopWidth);

        final float fDelta = fTopWidth / 2f;
        aContentStream.setStrokingColor (aTop.getColor ());
        aContentStream.setLineDashPattern (aTop.getLineDashPattern ());
        aContentStream.setLineWidth (fTopWidth);
        aContentStream.drawLine (fLeft, fTop + fDelta, fRight + fRightWidth, fTop + fDelta);
      }

      if (aRight != null)
      {
        if (PLDebug.isDebugRender ())
          PLDebug.debugRender (this,
                               "Border right " +
                                     PLDebug.getXYWH (fRight, fTop, 0, fHeight) +
                                     " with line width " +
                                     fRightWidth);

        final float fDelta = fRightWidth / 2f;
        aContentStream.setStrokingColor (aRight.getColor ());
        aContentStream.setLineDashPattern (aRight.getLineDashPattern ());
        aContentStream.setLineWidth (fRightWidth);
        aContentStream.drawLine (fRight + fDelta, fTop, fRight + fDelta, fBottom - fBottomWidth);
      }

      if (aBottom != null)
      {
        if (PLDebug.isDebugRender ())
          PLDebug.debugRender (this,
                               "Border bottom " +
                                     PLDebug.getXYWH (fLeft, fBottom, fWidth, 0) +
                                     " with line width " +
                                     fBottomWidth);

        final float fDelta = fBottomWidth / 2f;
        aContentStream.setStrokingColor (aBottom.getColor ());
        aContentStream.setLineDashPattern (aBottom.getLineDashPattern ());
        aContentStream.setLineWidth (fBottomWidth);
        aContentStream.drawLine (fLeft - fLeftWidth, fBottom - fDelta, fRight, fBottom - fDelta);
      }

      if (aLeft != null)
      {
        if (PLDebug.isDebugRender ())
          PLDebug.debugRender (this,
                               "Border left " +
                                     PLDebug.getXYWH (fLeft, fTop, 0, fHeight) +
                                     " with line width " +
                                     fLeftWidth);

        final float fDelta = fLeftWidth / 2f;
        aContentStream.setStrokingColor (aLeft.getColor ());
        aContentStream.setLineDashPattern (aLeft.getLineDashPattern ());
        aContentStream.setLineWidth (fLeftWidth);
        aContentStream.drawLine (fLeft - fDelta, fTop + fTopWidth, fLeft - fDelta, fBottom);
      }
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("id", m_sElementID)
                                       .append ("margin", m_aMargin)
                                       .append ("padding", m_aPadding)
                                       .append ("border", m_aBorder)
                                       .appendIfNotNull ("fillColor", m_aFillColor)
                                       .toString ();
  }
}
