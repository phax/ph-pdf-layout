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
import com.helger.commons.id.IHasID;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.traits.IGenericImplTrait;
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
                                            implements IHasID <String>, IGenericImplTrait <IMPLTYPE>
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

  /**
   * @return The debug ID of this element. Neither <code>null</code> nor empty.
   */
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

  public final boolean isSplittable ()
  {
    return this instanceof IPLSplittableElement;
  }

  @Nonnull
  public final IPLSplittableElement getAsSplittable ()
  {
    return (IPLSplittableElement) this;
  }

  /**
   * Throw an exception, if this object is already prepared.
   *
   * @throws IllegalStateException
   *         if already prepared
   */
  @OverrideOnDemand
  protected void checkNotPrepared ()
  {}

  /**
   * Set all margin values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMargin (final float fMargin)
  {
    return setMargin (fMargin, fMargin);
  }

  /**
   * Set all margin values. This method may not be called after an element got
   * prepared!
   *
   * @param fMarginY
   *        The Y-value to use (for top and bottom).
   * @param fMarginX
   *        The X-value to use (for left and right).
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMargin (final float fMarginY, final float fMarginX)
  {
    return setMargin (fMarginY, fMarginX, fMarginY, fMarginX);
  }

  /**
   * Set all margin values to potentially different values. This method may not
   * be called after an element got prepared!
   *
   * @param fMarginTop
   *        Top
   * @param fMarginRight
   *        Right
   * @param fMarginBottom
   *        Bottom
   * @param fMarginLeft
   *        Left
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMargin (final float fMarginTop,
                                   final float fMarginRight,
                                   final float fMarginBottom,
                                   final float fMarginLeft)
  {
    return setMargin (new MarginSpec (fMarginTop, fMarginRight, fMarginBottom, fMarginLeft));
  }

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
    checkNotPrepared ();
    m_aMargin = aMargin;
    return thisAsT ();
  }

  /**
   * Set the top margin value. This method may not be called after an element
   * got prepared!
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMarginTop (final float fMargin)
  {
    return setMargin (m_aMargin.getCloneWithTop (fMargin));
  }

  /**
   * Set the right margin value. This method may not be called after an element
   * got prepared!
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMarginRight (final float fMargin)
  {
    return setMargin (m_aMargin.getCloneWithRight (fMargin));
  }

  /**
   * Set the bottom margin value. This method may not be called after an element
   * got prepared!
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMarginBottom (final float fMargin)
  {
    return setMargin (m_aMargin.getCloneWithBottom (fMargin));
  }

  /**
   * Set the left margin value. This method may not be called after an element
   * got prepared!
   *
   * @param fMargin
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMarginLeft (final float fMargin)
  {
    return setMargin (m_aMargin.getCloneWithLeft (fMargin));
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
   * @return The current top margin.
   */
  public final float getMarginTop ()
  {
    return m_aMargin.getTop ();
  }

  /**
   * @return The current right margin.
   */
  public final float getMarginRight ()
  {
    return m_aMargin.getRight ();
  }

  /**
   * @return The current bottom margin.
   */
  public final float getMarginBottom ()
  {
    return m_aMargin.getBottom ();
  }

  /**
   * @return The current left margin.
   */
  public final float getMarginLeft ()
  {
    return m_aMargin.getLeft ();
  }

  /**
   * @return The sum of left and right margin.
   */
  public final float getMarginXSum ()
  {
    return m_aMargin.getXSum ();
  }

  /**
   * @return The sum of top and bottom margin.
   */
  public final float getMarginYSum ()
  {
    return m_aMargin.getYSum ();
  }

  /**
   * Set all padding values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPadding (final float fPadding)
  {
    return setPadding (fPadding, fPadding);
  }

  /**
   * Set all padding values. This method may not be called after an element got
   * prepared!
   *
   * @param fPaddingY
   *        The Y-value to use (for top and bottom).
   * @param fPaddingX
   *        The X-value to use (for left and right).
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPadding (final float fPaddingY, final float fPaddingX)
  {
    return setPadding (fPaddingY, fPaddingX, fPaddingY, fPaddingX);
  }

  /**
   * Set all padding values to potentially different values. This method may not
   * be called after an element got prepared!
   *
   * @param fPaddingTop
   *        Top
   * @param fPaddingRight
   *        Right
   * @param fPaddingBottom
   *        Bottom
   * @param fPaddingLeft
   *        Left
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPadding (final float fPaddingTop,
                                    final float fPaddingRight,
                                    final float fPaddingBottom,
                                    final float fPaddingLeft)
  {
    return setPadding (new PaddingSpec (fPaddingTop, fPaddingRight, fPaddingBottom, fPaddingLeft));
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
    checkNotPrepared ();
    m_aPadding = aPadding;
    return thisAsT ();
  }

  /**
   * Set the top padding value. This method may not be called after an element
   * got prepared!
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPaddingTop (final float fPadding)
  {
    return setPadding (m_aPadding.getCloneWithTop (fPadding));
  }

  /**
   * Set the right padding value. This method may not be called after an element
   * got prepared!
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPaddingRight (final float fPadding)
  {
    return setPadding (m_aPadding.getCloneWithRight (fPadding));
  }

  /**
   * Set the bottom padding value. This method may not be called after an
   * element got prepared!
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPaddingBottom (final float fPadding)
  {
    return setPadding (m_aPadding.getCloneWithBottom (fPadding));
  }

  /**
   * Set the left padding value. This method may not be called after an element
   * got prepared!
   *
   * @param fPadding
   *        The value to use.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPaddingLeft (final float fPadding)
  {
    return setPadding (m_aPadding.getCloneWithLeft (fPadding));
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
   * @return The current top padding.
   */
  public final float getPaddingTop ()
  {
    return m_aPadding.getTop ();
  }

  /**
   * @return The current right padding.
   */
  public final float getPaddingRight ()
  {
    return m_aPadding.getRight ();
  }

  /**
   * @return The current bottom padding.
   */
  public final float getPaddingBottom ()
  {
    return m_aPadding.getBottom ();
  }

  /**
   * @return The current left padding.
   */
  public final float getPaddingLeft ()
  {
    return m_aPadding.getLeft ();
  }

  /**
   * @return The sum of left and right padding.
   */
  public final float getPaddingXSum ()
  {
    return m_aPadding.getXSum ();
  }

  /**
   * @return The sum of top and bottom padding.
   */
  public final float getPaddingYSum ()
  {
    return m_aPadding.getYSum ();
  }

  /**
   * Set all border values (left, top, right, bottom) to the same value. This
   * method may not be called after an element got prepared!
   *
   * @param aBorder
   *        The border style specification to use. May be <code>null</code> to
   *        indicate no border.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (new BorderSpec (aBorder));
  }

  /**
   * Set all border values. This method may not be called after an element got
   * prepared!
   *
   * @param aBorderY
   *        The Y-value to use (for top and bottom). May be <code>null</code> to
   *        indicate no border.
   * @param aBorderX
   *        The X-value to use (for left and right). May be <code>null</code> to
   *        indicate no border.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorderY, @Nullable final BorderStyleSpec aBorderX)
  {
    return setBorder (new BorderSpec (aBorderY, aBorderX));
  }

  /**
   * Set all border values to potentially different values. This method may not
   * be called after an element got prepared!
   *
   * @param aBorderTop
   *        Top. May be <code>null</code> to indicate no border.
   * @param aBorderRight
   *        Right. May be <code>null</code> to indicate no border.
   * @param aBorderBottom
   *        Bottom. May be <code>null</code> to indicate no border.
   * @param aBorderLeft
   *        Left. May be <code>null</code> to indicate no border.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorderTop,
                                   @Nullable final BorderStyleSpec aBorderRight,
                                   @Nullable final BorderStyleSpec aBorderBottom,
                                   @Nullable final BorderStyleSpec aBorderLeft)
  {
    return setBorder (new BorderSpec (aBorderTop, aBorderRight, aBorderBottom, aBorderLeft));
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
    checkNotPrepared ();
    m_aBorder = aBorder;
    return thisAsT ();
  }

  /**
   * Set the top border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorderTop (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (m_aBorder.getCloneWithTop (aBorder));
  }

  /**
   * Set the right border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorderRight (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (m_aBorder.getCloneWithRight (aBorder));
  }

  /**
   * Set the bottom border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorderBottom (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (m_aBorder.getCloneWithBottom (aBorder));
  }

  /**
   * Set the left border value. This method may not be called after an element
   * got prepared!
   *
   * @param aBorder
   *        The value to use. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorderLeft (@Nullable final BorderStyleSpec aBorder)
  {
    return setBorder (m_aBorder.getCloneWithLeft (aBorder));
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
   * @return The current top border.
   */
  public final float getBorderTopWidth ()
  {
    return m_aBorder.getTopWidth ();
  }

  /**
   * @return The current right border.
   */
  public final float getBorderRightWidth ()
  {
    return m_aBorder.getRightWidth ();
  }

  /**
   * @return The current bottom border.
   */
  public final float getBorderBottomWidth ()
  {
    return m_aBorder.getBottomWidth ();
  }

  /**
   * @return The current left border.
   */
  public final float getBorderLeftWidth ()
  {
    return m_aBorder.getLeftWidth ();
  }

  /**
   * @return The sum of left and right border.
   */
  public final float getBorderXSumWidth ()
  {
    return m_aBorder.getXSumWidth ();
  }

  /**
   * @return The sum of top and bottom border.
   */
  public final float getBorderYSumWidth ()
  {
    return m_aBorder.getYSumWidth ();
  }

  public float getMarginAndBorderTop ()
  {
    return m_aMargin.getTop () + m_aBorder.getTopWidth ();
  }

  public float getMarginAndBorderRight ()
  {
    return m_aMargin.getRight () + m_aBorder.getRightWidth ();
  }

  public float getMarginAndBorderBottom ()
  {
    return m_aMargin.getBottom () + m_aBorder.getBottomWidth ();
  }

  public float getMarginAndBorderLeft ()
  {
    return m_aMargin.getLeft () + m_aBorder.getLeftWidth ();
  }

  public float getMarginAndBorderXSum ()
  {
    return m_aMargin.getXSum () + m_aBorder.getXSumWidth ();
  }

  public float getMarginAndBorderYSum ()
  {
    return m_aMargin.getYSum () + m_aBorder.getYSumWidth ();
  }

  public float getFullTop ()
  {
    return m_aMargin.getTop () + m_aBorder.getTopWidth () + m_aPadding.getTop ();
  }

  public float getFullRight ()
  {
    return m_aMargin.getRight () + m_aBorder.getRightWidth () + m_aPadding.getRight ();
  }

  public float getFullBottom ()
  {
    return m_aMargin.getBottom () + m_aBorder.getBottomWidth () + m_aPadding.getBottom ();
  }

  public float getFullLeft ()
  {
    return m_aMargin.getLeft () + m_aBorder.getLeftWidth () + m_aPadding.getLeft ();
  }

  public float getFullXSum ()
  {
    return m_aMargin.getXSum () + m_aBorder.getXSumWidth () + m_aPadding.getXSum ();
  }

  public float getFullYSum ()
  {
    return m_aMargin.getYSum () + m_aBorder.getYSumWidth () + m_aPadding.getYSum ();
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
                             "Border around " + PLDebug.getXYWH (fLeft, fTop, fWidth, fHeight) + " with line width " +
                                   fLineWidth);

      aContentStream.setStrokingColor (aAll.getColor ());
      aContentStream.setLineDashPattern (aAll.getLineDashPattern ());
      aContentStream.setLineWidth (fLineWidth);
      aContentStream.addRect (fLeft - fHalfLineWidth,
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
                               "Border top " + PLDebug.getXYWH (fLeft, fTop, fWidth, 0) + " with line width " +
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
                               "Border right " + PLDebug.getXYWH (fRight, fTop, 0, fHeight) + " with line width " +
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
                               "Border bottom " + PLDebug.getXYWH (fLeft, fBottom, fWidth, 0) + " with line width " +
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
                               "Border left " + PLDebug.getXYWH (fLeft, fTop, 0, fHeight) + " with line width " +
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
