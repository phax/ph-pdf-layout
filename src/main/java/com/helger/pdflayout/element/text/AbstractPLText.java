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
import java.util.List;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.base.IPLHasHorizontalAlignment;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.EHorzAlignment;
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
public abstract class AbstractPLText <IMPLTYPE extends AbstractPLText <IMPLTYPE>> extends AbstractPLElement <IMPLTYPE>
                                     implements IPLHasHorizontalAlignment <IMPLTYPE>, IPLSplittableObject <IMPLTYPE>
{
  public static final int DEFAULT_MAX_ROWS = CGlobal.ILLEGAL_UINT;
  public static final boolean DEFAULT_REPLACE_PLACEHOLDERS = false;

  private String m_sText;
  private String m_sDisplayText;
  private final FontSpec m_aFontSpec;
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private int m_nMaxRows = DEFAULT_MAX_ROWS;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;
  private boolean m_bReplacePlaceholder = DEFAULT_REPLACE_PLACEHOLDERS;

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
    m_sDisplayText = sText;
    m_aFontSpec = ValueEnforcer.notNull (aFontSpec, "FontSpec");
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final AbstractPLText <?> aSource)
  {
    super.setBasicDataFrom (aSource);
    setHorzAlign (aSource.m_eHorzAlign);
    setMaxRows (aSource.m_nMaxRows);
    setVertSplittable (aSource.m_bVertSplittable);
    setReplacePlaceholder (aSource.m_bReplacePlaceholder);
    return thisAsT ();
  }

  /**
   * @return The original text provided in the constructor.
   */
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

  @Nonnull
  public EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Nonnull
  public IMPLTYPE setHorzAlign (@Nonnull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
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

  public boolean isVertSplittable ()
  {
    return m_bVertSplittable;
  }

  @Nonnull
  public IMPLTYPE setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return thisAsT ();
  }

  public boolean isReplacePlaceholder ()
  {
    return m_bReplacePlaceholder;
  }

  @Nonnull
  public IMPLTYPE setReplacePlaceholder (final boolean bReplacePlaceholder)
  {
    m_bReplacePlaceholder = bReplacePlaceholder;
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
    internalSetPreparedLines (m_aLoadedFont.getFitToWidth (m_sDisplayText, fFontSize, fAvailableWidth));

    // Determine max width of all prepared lines
    float fMaxWidth = Float.MIN_VALUE;
    for (final TextAndWidthSpec aTWS : m_aPreparedLines)
      fMaxWidth = Math.max (fMaxWidth, aTWS.getWidth ());

    // Determine height by number of lines
    return new SizeSpec (fMaxWidth, m_aPreparedLines.size () * m_fLineHeight);
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    // Load font into document
    try
    {
      m_aLoadedFont = aCtx.getGlobalContext ().getLoadedFont (m_aFontSpec);
      return _prepareText (aCtx.getAvailableWidth () - getOutlineXSum ());
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to prepare text element: " + toString (), ex);
    }
  }

  protected final void setDisplayTextAfterPrepare (@Nonnull final String sNewText,
                                                   final float fAvailableWidth) throws IOException
  {
    internalMarkAsNotPrepared ();
    m_sDisplayText = sNewText;
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

  @Override
  public void beforeRender (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {
    if (m_bReplacePlaceholder)
    {
      final String sOrigText = getText ();
      final String sDisplayText = StringHelper.replaceMultiple (sOrigText, aCtx.getAllPlaceholders ());
      if (!sOrigText.equals (sDisplayText))
      {
        setDisplayTextAfterPrepare (sDisplayText, getPrepareAvailableSize ().getWidth ());
      }
    }
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    if (hasNoText ())
    {
      // Nothing to do - empty text
      return;
    }

    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    final float fRenderLeft = aCtx.getStartLeft () + getOutlineLeft ();
    final float fRenderTop = aCtx.getStartTop () - getOutlineTop ();

    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this,
                           "Display at " +
                                 PLDebug.getXYWH (fRenderLeft, fRenderTop, getPreparedWidth (), getPreparedHeight ()) +
                                 " with " +
                                 m_aPreparedLines.size () +
                                 " lines");

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();

    aContentStream.beginText ();

    // Set font if changed
    aContentStream.setFont (m_aLoadedFont, m_aFontSpec);

    final float fLineHeight = m_fLineHeight;
    final float fPreparedWidth = getPreparedWidth ();

    int nIndex = 0;
    final int nMax = m_aPreparedLines.size ();
    for (final TextAndWidthSpec aTW : m_aPreparedLines)
    {
      // Replace text (if any)
      final float fTextWidth = aTW.getWidth ();
      final String sDrawText = aTW.getText ();

      // Align text line by overall block width
      final float fIndentX = getIndentX (fPreparedWidth, fTextWidth);
      if (nIndex == 0)
      {
        // Initial move - only partial line height!
        aContentStream.moveTextPositionByAmount (fRenderLeft + fIndentX, fRenderTop - (fLineHeight * 0.75f));
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
      // Handle indent per-line as when right alignment is used, the indentX may
      // differ from line to line
      if (nIndex < nMax)
      {
        // Outdent and one line down, except for last line
        aContentStream.moveTextPositionByAmount (-fIndentX, -fLineHeight);
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
  private PLElementWithSize _splitGetCopy (final float fElementWidth,
                                           @Nonnull @Nonempty final List <TextAndWidthSpec> aLines,
                                           final boolean bSplittableCopy,
                                           @Nonnull final String sIDSuffix)
  {
    ValueEnforcer.notEmpty (aLines, "Lines");

    // Create a copy to be independent!
    final ICommonsList <TextAndWidthSpec> aLineCopy = new CommonsArrayList<> (aLines);

    // Excluding padding/margin
    final SizeSpec aSize = new SizeSpec (fElementWidth, getDisplayHeightOfLines (aLineCopy.size ()));

    final String sTextContent = StringHelper.getImploded ('\n', aLineCopy, x -> x.getText ());
    final PLText aNewText = new PLText (sTextContent, getFontSpec ());
    aNewText.setBasicDataFrom (this).setID (getID () + sIDSuffix);
    // Set this explicitly after setBasicDataFrom!
    aNewText.setVertSplittable (bSplittableCopy);

    aNewText.internalMarkAsPrepared (aSize);
    aNewText.internalSetPreparedLines (aLineCopy);
    aNewText.internalSetPreparedFontData (m_aLoadedFont, m_fLineHeight);

    return new PLElementWithSize (aNewText, aSize);
  }

  @Nullable
  public PLSplitResult splitElementVert (final float fElementWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return null;

    final float fLineHeight = m_fLineHeight;

    // Get the lines in the correct order from top to bottom
    final ICommonsList <TextAndWidthSpec> aLines = m_aPreparedLines;

    int nLines = (int) (fAvailableHeight / fLineHeight);
    if (nLines <= 0)
    {
      // Splitting makes no sense because the resulting text 1 would be empty
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this,
                            "Failed to split because the result would be " +
                                  nLines +
                                  " lines for available height " +
                                  fAvailableHeight +
                                  " and line height " +
                                  fLineHeight);
      return null;
    }

    if (nLines >= aLines.size ())
    {
      // Splitting makes no sense because the resulting text 2 would be empty
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this,
                            "Failed to split because the result of " +
                                  nLines +
                                  " lines fits into the available height " +
                                  fAvailableHeight +
                                  " and line height " +
                                  fLineHeight +
                                  " (=" +
                                  (fAvailableHeight * fLineHeight) +
                                  ")");
      return null;
    }

    // Calc estimated height (required because an offset is added)
    final float fExpectedHeight = getDisplayHeightOfLines (nLines);
    if (fExpectedHeight > fAvailableHeight)
    {
      // Show one line less
      --nLines;
      if (nLines <= 0)
      {
        // Splitting makes no sense
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Failed to split because the result would be " +
                                    nLines +
                                    " lines for available height " +
                                    fAvailableHeight +
                                    " and expected height " +
                                    fExpectedHeight);
        return null;
      }
    }

    // First elements does not need to be splittable anymore
    final PLElementWithSize aText1 = _splitGetCopy (fElementWidth, aLines.subList (0, nLines), false, "-1");
    // Second element may need additional splitting
    final PLElementWithSize aText2 = _splitGetCopy (fElementWidth, aLines.subList (nLines, aLines.size ()), true, "-2");

    return new PLSplitResult (aText1, aText2);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Text", m_sText)
                            .append ("DisplayText", m_sDisplayText)
                            .append ("FontSpec", m_aFontSpec)
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("MaxRows", m_nMaxRows)
                            .append ("VertSplittable", m_bVertSplittable)
                            .append ("ReplacePlaceholder", m_bReplacePlaceholder)
                            .toString ();
  }
}
