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
package com.helger.pdflayout.element.text;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLHorzAlignedElement;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.TextAndWidthSpec;

/**
 * Render text
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLText <IMPLTYPE extends AbstractPLText <IMPLTYPE>>
                                     extends AbstractPLHorzAlignedElement <IMPLTYPE>
{
  public static final boolean DEFAULT_TOP_DOWN = true;
  public static final int DEFAULT_MAX_ROWS = CGlobal.ILLEGAL_UINT;

  private String m_sText;
  private final FontSpec m_aFontSpec;
  private boolean m_bTopDown = DEFAULT_TOP_DOWN;
  private int m_nMaxRows = DEFAULT_MAX_ROWS;

  // prepare result
  private LoadedFont m_aLoadedFont;
  protected int m_nPreparedLineCountUnmodified = CGlobal.ILLEGAL_UINT;
  protected ICommonsList <TextAndWidthSpec> m_aPreparedLinesUnmodified;
  protected ICommonsList <TextAndWidthSpec> m_aPreparedLines;
  protected float m_fLineHeight;

  public AbstractPLText (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    if (StringHelper.hasNoText (sText))
    {
      m_sText = "";
    }
    else
    {
      // Unify line endings so that all "\r" are removed and only "\n" is
      // contained
      String sCleaned = sText;
      sCleaned = StringHelper.replaceAll (sCleaned, "\r\n", "\n");
      sCleaned = StringHelper.replaceAll (sCleaned, '\r', '\n');
      m_sText = sCleaned;
    }
    m_aFontSpec = ValueEnforcer.notNull (aFontSpec, "FontSpec");
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLText <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setTopDown (aSource.m_bTopDown);
    setMaxRows (aSource.m_nMaxRows);
    return thisAsT ();
  }

  @Nonnull
  public String getText ()
  {
    return m_sText;
  }

  public boolean hasText ()
  {
    return m_sText.length () > 0;
  }

  public boolean hasNoText ()
  {
    return m_sText.length () == 0;
  }

  @Nonnull
  public FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  /**
   * @return <code>true</code> if the text is rendered from top to bottom, or
   *         <code>false</code> if the text is rendered from bottom to top. The
   *         default value is {@link #DEFAULT_TOP_DOWN}.
   */
  public boolean isTopDown ()
  {
    return m_bTopDown;
  }

  /**
   * Set the rendering direction: top-down or bottom-up.
   *
   * @param bTopDown
   *        <code>true</code> to render top-down, <code>false</code> to render
   *        bottom-up.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setTopDown (final boolean bTopDown)
  {
    m_bTopDown = bTopDown;
    return thisAsT ();
  }

  /**
   * @return The maximum number of rows to be rendered. If this value is &le; 0
   *         than all rows are rendered. The default value is
   *         {@link #DEFAULT_MAX_ROWS}.
   */
  @CheckForSigned
  public int getMaxRows ()
  {
    return m_nMaxRows;
  }

  /**
   * Set the maximum number of rows to render.
   *
   * @param nMaxRows
   *        Maximum number of rows. If &le; 0 than all lines are rendered.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setMaxRows (final int nMaxRows)
  {
    m_nMaxRows = nMaxRows;
    return thisAsT ();
  }

  final void internalSetPreparedLines (@Nonnull final ICommonsList <TextAndWidthSpec> aLines)
  {
    final int nLines = aLines.size ();
    m_nPreparedLineCountUnmodified = nLines;
    m_aPreparedLinesUnmodified = aLines;
    if (m_nMaxRows <= 0)
    {
      // Use all lines
      m_aPreparedLines = aLines;
    }
    else
    {
      // Use only a certain maximum number of rows
      if (nLines <= m_nMaxRows)
      {
        // We have less lines than the maximum
        m_aPreparedLines = aLines;
      }
      else
      {
        // Maximum number of lines exceeded - copy only the relevant lines
        m_aPreparedLines = new CommonsArrayList<> (m_nMaxRows);
        for (int i = 0; i < m_nMaxRows; ++i)
          m_aPreparedLines.add (aLines.get (i));
      }
    }

    if (!m_bTopDown)
    {
      // Reverse order only once
      Collections.reverse (m_aPreparedLines);
    }
  }

  final void internalSetPreparedFontData (@Nonnull final LoadedFont aLoadedFont, final float fLineHeight)
  {
    ValueEnforcer.notNull (aLoadedFont, "LoadedFont");
    m_aLoadedFont = aLoadedFont;
    m_fLineHeight = fLineHeight;
  }

  /**
   * This method can only be called after loadedFont member was set!
   *
   * @param fAvailableWidth
   *        Available with
   * @return The new preparation size
   * @throws IOException
   *         On PDFBox error
   */
  @Nonnull
  private SizeSpec _prepareText (final float fAvailableWidth) throws IOException
  {
    final float fFontSize = m_aFontSpec.getFontSize ();
    m_fLineHeight = m_aLoadedFont.getLineHeight (fFontSize);

    if (hasNoText ())
    {
      // Nothing to do - empty
      // But keep the height distance!
      return new SizeSpec (0, m_fLineHeight);
    }

    // Split text into rows
    internalSetPreparedLines (m_aLoadedFont.getFitToWidth (m_sText, fFontSize, fAvailableWidth));

    // Determine max width of all prepared lines
    float fMaxWidth = Float.MIN_VALUE;
    for (final TextAndWidthSpec aTWS : m_aPreparedLines)
      fMaxWidth = Math.max (fMaxWidth, aTWS.getWidth ());

    // Determine height by number of lines
    return new SizeSpec (fMaxWidth, m_aPreparedLines.size () * m_fLineHeight);
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    // Load font into document
    m_aLoadedFont = aCtx.getGlobalContext ().getLoadedFont (m_aFontSpec);
    return _prepareText (aCtx.getAvailableWidth ());
  }

  protected final void setNewTextAfterPrepare (@Nonnull final String sNewText,
                                               final float fAvailableWidth) throws IOException
  {
    internalMarkAsNotPrepared ();
    m_sText = sNewText;
    final SizeSpec aOnPrepareResult = _prepareText (fAvailableWidth);
    internalMarkAsPrepared (aOnPrepareResult);
  }

  /**
   * @return The total number of prepared lines, not taking the maxRows into
   *         consideration. Always &ge; 0.
   */
  @Nonnegative
  public int getPreparedLineCountUnmodified ()
  {
    if (m_nPreparedLineCountUnmodified == CGlobal.ILLEGAL_UINT)
      throw new IllegalStateException ("Preparation is not yet done");
    return m_nPreparedLineCountUnmodified;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <TextAndWidthSpec> getAllPreparedLinesUnmodified ()
  {
    if (m_aPreparedLinesUnmodified == null)
      throw new IllegalStateException ("Preparation is not yet done");
    return new CommonsArrayList<> (m_aPreparedLinesUnmodified);
  }

  /**
   * Get the text to draw, in case it is different from the stored text (e.g.
   * for page numbers in {@link PLTextWithPlaceholders})
   *
   * @param sText
   *        Original text. Never <code>null</code>.
   * @param aCtx
   *        The current rendering context. Never <code>null</code>.
   * @return The real text to draw. May not be <code>null</code>.
   */
  @Nonnull
  @OverrideOnDemand
  protected String getTextToDraw (@Nonnull final String sText, @Nonnull final PageRenderContext aCtx)
  {
    return sText;
  }

  @Override
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    if (hasNoText ())
    {
      // Nothing to do - empty text
      return;
    }

    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();

    aContentStream.beginText ();

    // Set font if changed
    aContentStream.setFont (m_aLoadedFont, m_aFontSpec);

    final float fFontSize = m_aFontSpec.getFontSize ();
    final float fLineHeight = m_fLineHeight;

    final float fTop = getMarginTop ();
    int nIndex = 0;
    final int nMax = m_aPreparedLines.size ();
    for (final TextAndWidthSpec aTW : m_aPreparedLines)
    {
      // Replace text (if any)
      float fTextWidth = aTW.getWidth ();
      final String sOrigText = aTW.getText ();

      // get the real text to draw
      final String sDrawText = getTextToDraw (sOrigText, aCtx);
      if (!sOrigText.equals (sDrawText))
      {
        // Text changed - recalculate width!
        fTextWidth = m_aLoadedFont.getStringWidth (sDrawText, fFontSize);
      }

      // Align text line by overall block width
      final float fIndentX = getIndentX (aCtx.getWidth (), fTextWidth);
      if (nIndex == 0)
      {
        // Initial move - only partial line height!
        aContentStream.moveTextPositionByAmount (aCtx.getStartLeft () +
                                                 fIndentX,
                                                 aCtx.getStartTop () - fTop - (fLineHeight * 0.75f));
      }
      else
        if (fIndentX != 0)
        {
          // Indent subsequent line
          aContentStream.moveTextPositionByAmount (fIndentX, 0);
        }

      // Main draw string
      aContentStream.drawString (sDrawText);
      ++nIndex;

      // Goto next line
      if (nIndex < nMax)
      {
        if (m_bTopDown)
        {
          // Outdent and one line down, except for last line
          aContentStream.moveTextPositionByAmount (-fIndentX, -fLineHeight);
        }
        else
        {
          // Outdent and one line up, except for last line
          aContentStream.moveTextPositionByAmount (-fIndentX, fLineHeight);
        }
      }
    }
    aContentStream.endText ();
  }

  protected final float getDisplayHeightOfLines (@Nonnegative final int nLineCount)
  {
    // Note: when drawing the text, only 0.75*lineHeight is subtracted so now we
    // need to add 0.25*lineHeight so that it looks good.
    return (nLineCount + 0.25f) * m_fLineHeight;
  }

  @Nonnull
  public PLElementWithSize getCopy (final float fElementWidth,
                                    @Nonnull @Nonempty final List <TextAndWidthSpec> aLines,
                                    final boolean bSplittableCopy)
  {
    ValueEnforcer.notEmpty (aLines, "Lines");

    // Create a copy to be independent!
    final ICommonsList <TextAndWidthSpec> aLineCopy = new CommonsArrayList<> (aLines);

    // Excluding padding/margin
    final SizeSpec aSize = new SizeSpec (fElementWidth, getDisplayHeightOfLines (aLineCopy.size ()));

    final String sTextContent = TextAndWidthSpec.getAsText (aLineCopy);
    final AbstractPLText <?> aNewText = bSplittableCopy ? new PLTextSplittable (sTextContent, getFontSpec ())
                                                        : new PLText (sTextContent, getFontSpec ());
    aNewText.setBasicDataFrom (this).internalMarkAsPrepared (aSize);
    aNewText.internalSetPreparedLines (aLineCopy);
    aNewText.internalSetPreparedFontData (m_aLoadedFont, m_fLineHeight);

    return new PLElementWithSize (aNewText, aSize);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Text", m_sText)
                            .append ("FontSpec", m_aFontSpec)
                            .append ("TopDown", m_bTopDown)
                            .append ("MaxRows", m_nMaxRows)
                            .toString ();
  }
}
