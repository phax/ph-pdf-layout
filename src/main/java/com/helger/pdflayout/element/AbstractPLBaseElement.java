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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.id.IHasID;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.lang.ClassHelper;
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
  private String m_sElementID;
  private MarginSpec m_aMargin = MarginSpec.MARGIN0;
  private PaddingSpec m_aPadding = PaddingSpec.PADDING0;
  private BorderSpec m_aBorder = BorderSpec.BORDER0;
  private Color m_aFillColor = null;

  public AbstractPLBaseElement ()
  {
    m_sElementID = ClassHelper.getClassLocalName (this) + "-" + GlobalIDFactory.getNewIntID ();
  }

  /**
   * @return The unique element ID.
   */
  public final String getID ()
  {
    return m_sElementID;
  }

  @Nonnull
  public final IMPLTYPE setID (@Nonnull @Nonempty final String sID)
  {
    m_sElementID = ValueEnforcer.notEmpty (sID, "ID");
    return thisAsT ();
  }

  /**
   * @return The debug ID of this element. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public final String getDebugID ()
  {
    return getID ();
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
   * @param fMarginX
   *        The X-value to use (for left and right).
   * @param fMarginY
   *        The Y-value to use (for top and bottom).
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setMargin (final float fMarginX, final float fMarginY)
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
   * @param fPaddingX
   *        The X-value to use (for left and right).
   * @param fPaddingY
   *        The Y-value to use (for top and bottom).
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setPadding (final float fPaddingX, final float fPaddingY)
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
   * @param aBorderX
   *        The X-value to use (for left and right). May be <code>null</code> to
   *        indicate no border.
   * @param aBorderY
   *        The Y-value to use (for top and bottom). May be <code>null</code> to
   *        indicate no border.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setBorder (@Nullable final BorderStyleSpec aBorderX, @Nullable final BorderStyleSpec aBorderY)
  {
    return setBorder (new BorderSpec (aBorderX, aBorderY));
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

  public float getMarginAndBorderFullTop ()
  {
    return m_aMargin.getTop () + m_aBorder.getTopWidth ();
  }

  public float getMarginAndBorderFullRight ()
  {
    return m_aMargin.getRight () + m_aBorder.getRightWidth ();
  }

  public float getMarginAndBorderFullBottom ()
  {
    return m_aMargin.getBottom () + m_aBorder.getBottomWidth ();
  }

  public float getMarginAndBorderFullLeft ()
  {
    return m_aMargin.getLeft () + m_aBorder.getLeftWidth ();
  }

  public float getMarginAndBorderFullXSum ()
  {
    return m_aMargin.getXSum () + m_aBorder.getXSumWidth ();
  }

  public float getMarginAndBorderFullYSum ()
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
   *        Left position
   * @param fTop
   *        Top position
   * @param fWidth
   *        Width
   * @param fHeight
   *        Height
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

    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this,
                           "Border: " +
                                 fLeft +
                                 "/" +
                                 fBottom +
                                 " - " +
                                 fRight +
                                 "/" +
                                 fTop +
                                 " (= " +
                                 fWidth +
                                 "/" +
                                 fHeight +
                                 ")");

    if (aBorder.hasAllBorders () && aBorder.areAllBordersEqual ())
    {
      // draw full rect
      final BorderStyleSpec aAll = aBorder.getLeft ();
      aContentStream.setStrokingColor (aAll.getColor ());
      aContentStream.setLineDashPattern (aAll.getLineDashPattern ());
      aContentStream.setLineWidth (aAll.getLineWidth ());
      aContentStream.addRect (fLeft, fBottom, fWidth, fHeight);
      aContentStream.stroke ();
    }
    else
    {
      // partially
      final BorderStyleSpec aTop = aBorder.getTop ();
      if (aTop != null)
      {
        aContentStream.setStrokingColor (aTop.getColor ());
        aContentStream.setLineDashPattern (aTop.getLineDashPattern ());
        aContentStream.setLineWidth (aTop.getLineWidth ());
        aContentStream.drawLine (fLeft, fTop, fRight, fTop);
      }

      final BorderStyleSpec aRight = aBorder.getRight ();
      if (aRight != null)
      {
        aContentStream.setStrokingColor (aRight.getColor ());
        aContentStream.setLineDashPattern (aRight.getLineDashPattern ());
        aContentStream.setLineWidth (aRight.getLineWidth ());
        aContentStream.drawLine (fRight, fTop, fRight, fBottom);
      }

      final BorderStyleSpec aBottom = aBorder.getBottom ();
      if (aBottom != null)
      {
        aContentStream.setStrokingColor (aBottom.getColor ());
        aContentStream.setLineDashPattern (aBottom.getLineDashPattern ());
        aContentStream.setLineWidth (aBottom.getLineWidth ());
        aContentStream.drawLine (fLeft, fBottom, fRight, fBottom);
      }

      final BorderStyleSpec aLeft = aBorder.getLeft ();
      if (aLeft != null)
      {
        aContentStream.setStrokingColor (aLeft.getColor ());
        aContentStream.setLineDashPattern (aLeft.getLineDashPattern ());
        aContentStream.setLineWidth (aLeft.getLineWidth ());
        aContentStream.drawLine (fLeft, fTop, fLeft, fBottom);
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
