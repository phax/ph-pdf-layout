/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import static com.helger.pdflayout.render.PLRenderHelper.fillAndRenderBorderRounded;

import java.io.IOException;
import java.util.List;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.string.StringReplace;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.pdflayout.base.AbstractPLInlineElement;
import com.helger.pdflayout.base.EPLPlaceholder;
import com.helger.pdflayout.base.IPLHasHorizontalAlignment;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PLRenderHelper;
import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.SizeSpec;
import com.helger.pdflayout.spec.TextAndWidthSpec;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Render text
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLText <IMPLTYPE extends AbstractPLText <IMPLTYPE>> extends
                                     AbstractPLInlineElement <IMPLTYPE> implements
                                     IPLHasHorizontalAlignment <IMPLTYPE>,
                                     IPLSplittableObject <IMPLTYPE, IMPLTYPE>
{
  public static final float DEFAULT_LINE_SPACING = 1f;
  public static final int DEFAULT_MAX_ROWS = CGlobal.ILLEGAL_UINT;
  public static final boolean DEFAULT_REPLACE_PLACEHOLDERS = false;
  public static final float DEFAULT_BORDER_RADIUS = 0f;

  private String m_sOriginalText;
  private String m_sTextWithPlaceholdersReplaced;
  private FontSpec m_aFontSpec;
  private float m_fLineSpacing = DEFAULT_LINE_SPACING;

  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private int m_nMaxRows = DEFAULT_MAX_ROWS;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;
  private boolean m_bReplacePlaceholder = DEFAULT_REPLACE_PLACEHOLDERS;
  private float m_fBorderRadius = DEFAULT_BORDER_RADIUS;

  // prepare result
  private transient LoadedFont m_aLoadedFont;
  protected float m_fTextHeight;
  protected float m_fDescent;
  private float m_fCustomAscentFirstLine = 0f;
  protected int m_nPreparedLineCountUnmodified = CGlobal.ILLEGAL_UINT;
  protected ICommonsList <TextAndWidthSpec> m_aPreparedLinesUnmodified;
  protected ICommonsList <TextAndWidthSpec> m_aPreparedLines;

  @Nonnull
  public static String getCleanedPLText (@Nullable final String sText)
  {
    if (StringHelper.isEmpty (sText))
    {
      return "";
    }
    // Unify line endings so that all "\r" are removed and only "\n" is
    // contained
    // Multiple \n after each other remain
    String sCleaned = sText;
    sCleaned = StringReplace.replaceAll (sCleaned, "\r\n", "\n");
    sCleaned = StringReplace.replaceAll (sCleaned, '\r', '\n');
    return sCleaned;
  }

  public AbstractPLText (@Nullable final String sText, @Nonnull final FontSpec aFontSpec)
  {
    _setText (sText);
    m_aFontSpec = ValueEnforcer.notNull (aFontSpec, "FontSpec");
  }

  /**
   * Set the internal text fields
   *
   * @param sText
   *        Text to use. May be <code>null</code>.
   */
  private void _setText (@Nullable final String sText)
  {
    m_sOriginalText = getCleanedPLText (sText);
    m_sTextWithPlaceholdersReplaced = m_sOriginalText;
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setLineSpacing (aSource.getLineSpacing ());
    setHorzAlign (aSource.getHorzAlign ());
    setMaxRows (aSource.getMaxRows ());
    setVertSplittable (aSource.isVertSplittable ());
    setReplacePlaceholder (aSource.isReplacePlaceholder ());
    setBorderRadius (aSource.getBorderRadius ());
    setCustomAscentFirstLine (aSource.getCustomAscentFirstLine ());
    return thisAsT ();
  }

  /**
   * @return The original text provided in the constructor, with newlines unified. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final String getText ()
  {
    return m_sOriginalText;
  }

  /**
   * @return <code>true</code> if the contained text has at least one character, <code>false</code>
   *         if it is empty.
   */
  public final boolean hasText ()
  {
    return m_sOriginalText.length () > 0;
  }

  /**
   * @return <code>true</code> if the text provided in the constructor contains no character,
   *         <code>false</code> otherwise.
   */
  public final boolean hasNoText ()
  {
    return m_sOriginalText.length () == 0;
  }

  /**
   * @return The font specification to be used as provided in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  /**
   * Set the font spec after the constructor. This can only be done before the text is prepared.
   *
   * @param aFontSpec
   *        The new font spec. Must not be <code>null</code>.
   * @return this for chaining
   * @since 7.3.5
   */
  @Nonnull
  public final IMPLTYPE setFontSpec (@Nonnull final FontSpec aFontSpec)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");

    if (isPrepared ())
      throw new IllegalStateException ("You cannot change the FontSpec after the object was already prepared");

    m_aFontSpec = aFontSpec;
    return thisAsT ();
  }

  /**
   * @return The line height factor. Defaults to {@link #DEFAULT_LINE_SPACING} which means 100%.
   */
  public final float getLineSpacing ()
  {
    return m_fLineSpacing;
  }

  /**
   * Set the line spacing to use. The line spacing is the distance between 2 consecutive lines. The
   * line spacing is not considered if there is a single line of text.
   *
   * @param fLineSpacing
   *        A value of 1 means 100%, 1.05 means 105% etc. Must be &gt; 0.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setLineSpacing (@Nonnegative final float fLineSpacing)
  {
    ValueEnforcer.isGT0 (fLineSpacing, "LineSpacing");
    m_fLineSpacing = fLineSpacing;
    return thisAsT ();
  }

  @Nonnull
  public final EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Nonnull
  public final IMPLTYPE setHorzAlign (@Nonnull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
    return thisAsT ();
  }

  /**
   * @return The maximum number of rows to be rendered. If this value is &le; 0 than all rows are
   *         rendered. The default value is {@link #DEFAULT_MAX_ROWS} meaning all rows are rendered.
   */
  @CheckForSigned
  public final int getMaxRows ()
  {
    return m_nMaxRows;
  }

  /**
   * Set the maximum number of rows to render.
   *
   * @param nMaxRows
   *        Maximum number of rows. If &le; 0 than all lines are rendered.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setMaxRows (final int nMaxRows)
  {
    m_nMaxRows = nMaxRows;
    return thisAsT ();
  }

  @Override
  public final boolean isVertSplittable ()
  {
    return m_bVertSplittable;
  }

  @Nonnull
  public final IMPLTYPE setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return thisAsT ();
  }

  /**
   * @return <code>true</code> if placeholders should be replaced, <code>false</code> otherwise. The
   *         default value is {@link #DEFAULT_REPLACE_PLACEHOLDERS} so
   *         {@value #DEFAULT_REPLACE_PLACEHOLDERS}.
   */
  public final boolean isReplacePlaceholder ()
  {
    return m_bReplacePlaceholder;
  }

  /**
   * Change whether placeholders should be replaced or not. Enabling this slows down the execution
   * of rendering. Enable this only if absolutely necessary.
   *
   * @param bReplacePlaceholder
   *        <code>true</code> if placeholders should be replaced, <code>false</code> otherwise.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setReplacePlaceholder (final boolean bReplacePlaceholder)
  {
    m_bReplacePlaceholder = bReplacePlaceholder;
    return thisAsT ();
  }

  /**
   * @return The border radius to use. Only values &gt; 0 will draw a radius.
   * @since v7.4.1
   */
  public final float getBorderRadius ()
  {
    return m_fBorderRadius;
  }

  /**
   * @return <code>true</code> if a border radius is defined, <code>false</code> if not.
   * @since v7.4.1
   */
  public final boolean hasBorderRadius ()
  {
    return m_fBorderRadius > 0f;
  }

  /**
   * Set the border radius to be used.
   *
   * @param fBorderRadius
   *        The actual border radius. Only values &gt; 0 will draw a radius.
   * @return this for chaining
   * @since v7.4.1
   */
  @Nonnull
  public final IMPLTYPE setBorderRadius (final float fBorderRadius)
  {
    m_fBorderRadius = fBorderRadius;
    return thisAsT ();
  }

  /**
   * @return A custom ascent to the first line. Defaults to 0.
   * @since 5.1.0
   */
  public final float getCustomAscentFirstLine ()
  {
    return m_fCustomAscentFirstLine;
  }

  /**
   * Set a custom ascent to the first line
   *
   * @param fCustomAscentFirstLine
   *        The value to use. 0 means no change.
   * @return this for chaining
   * @since 5.1.0
   */
  @Nonnull
  public final IMPLTYPE setCustomAscentFirstLine (final float fCustomAscentFirstLine)
  {
    m_fCustomAscentFirstLine = fCustomAscentFirstLine;
    return thisAsT ();
  }

  final void internalSetPreparedLines (@Nonnull final ICommonsList <TextAndWidthSpec> aLines)
  {
    final int nLineCount = aLines.size ();
    m_nPreparedLineCountUnmodified = nLineCount;
    m_aPreparedLinesUnmodified = aLines;
    if (m_nMaxRows <= 0)
    {
      // Use all lines
      m_aPreparedLines = aLines;
    }
    else
    {
      // Use only a certain maximum number of rows
      if (nLineCount <= m_nMaxRows)
      {
        // We have less lines than the maximum
        m_aPreparedLines = aLines;
      }
      else
      {
        // Maximum number of lines exceeded - copy only the relevant lines
        m_aPreparedLines = new CommonsArrayList <> (m_nMaxRows);
        for (int i = 0; i < m_nMaxRows; ++i)
          m_aPreparedLines.add (aLines.get (i));
      }
    }
  }

  final void internalSetPreparedFontData (@Nonnull final LoadedFont aLoadedFont,
                                          final float fTextHeight,
                                          final float fDescent)
  {
    ValueEnforcer.notNull (aLoadedFont, "LoadedFont");
    m_aLoadedFont = aLoadedFont;
    m_fTextHeight = fTextHeight;
    m_fDescent = fDescent;
  }

  // Call only once here - used read-only!
  private static final ICommonsMap <String, String> ESTIMATION_REPLACEMENTS = EPLPlaceholder.getEstimationReplacements ();

  /**
   * This method can only be called after loadedFont member was set!
   *
   * @param fAvailableWidth
   *        Available with
   * @param bAlreadyReplaced
   *        <code>true</code> if the text was already replaced
   * @return The new preparation size
   * @throws IOException
   *         On PDFBox error
   */
  @Nonnull
  private SizeSpec _prepareText (final float fAvailableWidth, final boolean bAlreadyReplaced) throws IOException
  {
    final float fFontSize = m_aFontSpec.getFontSize ();
    m_fTextHeight = m_aLoadedFont.getTextHeight (fFontSize);
    m_fDescent = m_aLoadedFont.getDescent (fFontSize);

    if (hasNoText ())
    {
      // Nothing to do - empty
      // But keep the height distance!
      return new SizeSpec (0, m_fTextHeight);
    }

    // Split text into rows
    final String sTextToFit;
    if (bAlreadyReplaced)
    {
      sTextToFit = m_sTextWithPlaceholdersReplaced;
    }
    else
    {
      // Use the approximations from the placeholders
      sTextToFit = StringReplace.replaceMultiple (m_sOriginalText, ESTIMATION_REPLACEMENTS);
    }
    internalSetPreparedLines (m_aLoadedFont.getFitToWidth (sTextToFit, fFontSize, fAvailableWidth));

    // Determine max width of all prepared lines
    float fMaxWidth = Float.MIN_VALUE;
    for (final TextAndWidthSpec aTWS : m_aPreparedLines)
      fMaxWidth = Math.max (fMaxWidth, aTWS.getWidth ());

    // Determine height by number of lines
    // No line spacing for the last line
    return new SizeSpec (fMaxWidth, getDisplayHeightOfLineCount (m_aPreparedLines.size (), false));
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();

    // Load font into document
    try
    {
      m_aLoadedFont = aCtx.getGlobalContext ().getLoadedFont (m_aFontSpec);
      return _prepareText (fElementWidth, false);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to prepare text element: " + toString (), ex);
    }
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    m_nPreparedLineCountUnmodified = CGlobal.ILLEGAL_UINT;
    m_aPreparedLinesUnmodified = null;
    m_aPreparedLines = null;
  }

  private void _setDisplayTextAfterPrepare (@Nonnull final String sNewTextWithPlaceholdersReplaced,
                                            final float fAvailableWidth) throws IOException
  {
    internalMarkAsNotPrepared ();
    m_sTextWithPlaceholdersReplaced = sNewTextWithPlaceholdersReplaced;
    final SizeSpec aOnPrepareResult = _prepareText (fAvailableWidth, true);
    internalMarkAsPrepared (aOnPrepareResult);
  }

  /**
   * @return The total number of prepared lines, not taking the maxRows into consideration. Always
   *         &ge; 0.
   */
  @Nonnegative
  public int getPreparedLineCountUnmodified ()
  {
    internalCheckAlreadyPrepared ();
    return m_nPreparedLineCountUnmodified;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <TextAndWidthSpec> getAllPreparedLinesUnmodified ()
  {
    internalCheckAlreadyPrepared ();
    return new CommonsArrayList <> (m_aPreparedLinesUnmodified);
  }

  protected final float getDisplayHeightOfLineCount (@Nonnegative final int nLineCount,
                                                     final boolean bLineSpacingAlsoOnLastLine)
  {
    if (nLineCount == 0)
      return 0f;
    if (nLineCount == 1)
      return m_fTextHeight;

    if (bLineSpacingAlsoOnLastLine)
    {
      // Use text height and line spacing for each line separately
      return nLineCount * m_fTextHeight * m_fLineSpacing;
    }

    // The line spacing factor counts only between lines!
    return (nLineCount - 1) * m_fTextHeight * m_fLineSpacing + 1 * m_fTextHeight;
  }

  @Nonnull
  private PLElementWithSize _splitGetCopy (final float fElementWidth,
                                           @Nonnull @Nonempty final List <TextAndWidthSpec> aLines,
                                           final boolean bSplittableCopy,
                                           @Nonnull final String sIDSuffix)
  {
    ValueEnforcer.notEmpty (aLines, "Lines");

    // Create a copy to be independent!
    final ICommonsList <TextAndWidthSpec> aLineCopy = new CommonsArrayList <> (aLines);

    // Excluding padding/margin
    final SizeSpec aSize = new SizeSpec (fElementWidth, getDisplayHeightOfLineCount (aLineCopy.size (), true));

    final String sTextContent = StringImplode.getImplodedMapped ('\n', aLineCopy, TextAndWidthSpec::getText);
    final AbstractPLText <?> aNewText = internalCreateNewVertSplitObject (thisAsT ()).setID (getID () + sIDSuffix);
    aNewText._setText (sTextContent);
    // Set this explicitly after setBasicDataFrom!
    aNewText.setVertSplittable (bSplittableCopy);

    // Set min width/max width from source
    // Don't use the height, because on vertically split elements, the height is
    // dynamic
    aNewText.setMinWidth (getMinWidth ());
    aNewText.setMaxWidth (getMaxWidth ());

    aNewText.internalMarkAsPrepared (aSize);
    aNewText.internalSetPreparedLines (aLineCopy);
    aNewText.internalSetPreparedFontData (m_aLoadedFont, m_fTextHeight, m_fDescent);

    return new PLElementWithSize (aNewText, aSize);
  }

  @Nonnull
  public final PLSplitResult splitElementVert (final float fElementWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return PLSplitResult.allOnSecond ();

    // Get the lines in the correct order from top to bottom
    final ICommonsList <TextAndWidthSpec> aLines = m_aPreparedLines;

    int nSplitLineCount = (int) ((fAvailableHeight + (m_fLineSpacing - 1f) * m_fTextHeight) /
                                 (m_fTextHeight * m_fLineSpacing));
    if (false)
      nSplitLineCount = (int) (fAvailableHeight / (m_fTextHeight * m_fLineSpacing));

    if (nSplitLineCount <= 0)
    {
      // Splitting makes no sense because the resulting text 1 would be empty
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this,
                               "Failed to split because the result would be " +
                                     nSplitLineCount +
                                     " lines for available height " +
                                     fAvailableHeight +
                                     " and line height " +
                                     (m_fTextHeight * m_fLineSpacing));
      return PLSplitResult.allOnSecond ();
    }

    if (nSplitLineCount >= aLines.size ())
    {
      // Splitting makes no sense because the resulting text 2 would be empty
      if (PLDebugLog.isDebugSplit ())
        PLDebugLog.debugSplit (this,
                               "Failed to split because the result of " +
                                     nSplitLineCount +
                                     " lines fits into the available height " +
                                     fAvailableHeight +
                                     " and line height " +
                                     (m_fTextHeight * m_fLineSpacing) +
                                     " (=" +
                                     getDisplayHeightOfLineCount (nSplitLineCount, true) +
                                     ")");
      return PLSplitResult.allOnFirst ();
    }

    // Calculate estimated height of the split lines
    final float fExpectedHeight = getDisplayHeightOfLineCount (nSplitLineCount, true);
    if (fExpectedHeight > fAvailableHeight)
    {
      // Show one line less
      --nSplitLineCount;
      if (nSplitLineCount <= 0)
      {
        // Splitting makes no sense
        if (PLDebugLog.isDebugSplit ())
          PLDebugLog.debugSplit (this,
                                 "Failed to split because the result would be " +
                                       nSplitLineCount +
                                       " lines for available height " +
                                       fAvailableHeight +
                                       " and expected height " +
                                       fExpectedHeight);
        return PLSplitResult.allOnSecond ();
      }
    }

    // First elements does not need to be splittable anymore
    final PLElementWithSize aText1 = _splitGetCopy (fElementWidth, aLines.subList (0, nSplitLineCount), false, "-1");
    // Second element may need additional splitting
    final PLElementWithSize aText2 = _splitGetCopy (fElementWidth,
                                                    aLines.subList (nSplitLineCount, aLines.size ()),
                                                    true,
                                                    "-2");

    return PLSplitResult.createSplit (aText1, aText2);
  }

  @Override
  @Nonnull
  public EChange beforeRender (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {
    if (m_bReplacePlaceholder)
    {
      final String sOrigText = m_sOriginalText;
      final String sDisplayText = StringReplace.replaceMultiple (sOrigText, aCtx.getAllPlaceholders ());
      if (!sOrigText.equals (sDisplayText))
      {
        // Something changed
        _setDisplayTextAfterPrepare (sDisplayText, getPrepareAvailableSize ().getWidth ());
        return EChange.CHANGED;
      }
    }
    return EChange.UNCHANGED;
  }

  @OverrideOnDemand
  protected void renderShape (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    if (hasBorderRadius ())
    {
      fillAndRenderBorderRounded (thisAsT (),
                                  aCtx,
                                  0f,
                                  0f,
                                  m_fBorderRadius,
                                  m_fBorderRadius,
                                  m_fBorderRadius,
                                  m_fBorderRadius);
    }
    else
      PLRenderHelper.fillAndRenderBorder (thisAsT (), aCtx, 0f, 0f);
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    if (hasNoText ())
    {
      // Nothing to do - empty text
      return;
    }

    renderShape (aCtx);

    final float fRenderLeft = aCtx.getStartLeft () + getOutlineLeft ();
    final float fRenderTop = aCtx.getStartTop () - getOutlineTop ();

    if (PLDebugLog.isDebugRender ())
      PLDebugLog.debugRender (this,
                              "Display at " +
                                    PLDebugLog.getXYWH (fRenderLeft,
                                                        fRenderTop,
                                                        getRenderWidth (),
                                                        getRenderHeight ()) +
                                    " with " +
                                    m_aPreparedLines.size () +
                                    " lines");

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();

    aContentStream.beginText ();

    // Set font if changed
    aContentStream.setFont (m_aLoadedFont, m_aFontSpec);

    final float fTextHeight = m_fTextHeight;
    final float fPreparedWidth = getPreparedWidth ();
    final boolean bDoTextJustify = m_eHorzAlign == EHorzAlignment.JUSTIFY;
    final boolean bDoTextBlock = m_eHorzAlign == EHorzAlignment.BLOCK;

    int nIndex = 0;
    final int nMax = m_aPreparedLines.size ();
    for (final TextAndWidthSpec aTW : m_aPreparedLines)
    {
      final boolean bBeforeLastLine = nIndex < (nMax - 1);

      // Replace text (if any)
      final float fTextWidth = aTW.getWidth ();
      final String sDrawText = aTW.getText ();

      // Align text line by overall block width
      final float fIndentX = getIndentX (fPreparedWidth, fTextWidth);
      if (nIndex == 0)
      {
        // Initial move - only partial line height!
        aContentStream.moveTextPositionByAmount (fRenderLeft + fIndentX,
                                                 fRenderTop - fTextHeight - m_fDescent + m_fCustomAscentFirstLine);
      }
      else
        if (fIndentX != 0)
        {
          // Indent subsequent line
          aContentStream.moveTextPositionByAmount (fIndentX, 0);
        }

      if ((bDoTextJustify && bBeforeLastLine) || (bDoTextBlock && !aTW.isDisplayAsNewline ()))
      {
        // Justify the content of this line
        // Avoid division by zero
        float fCharSpacing = 0;
        if (sDrawText.length () > 1)
        {
          // Calculate width of space between each character (therefore -1)
          fCharSpacing = (fPreparedWidth - fTextWidth) / (sDrawText.length () - 1);
        }

        // Set for each line separately,
        aContentStream.setCharacterSpacing (fCharSpacing);
      }
      else
        if ((bDoTextJustify && !bBeforeLastLine) || (bDoTextBlock && aTW.isDisplayAsNewline ()))
        {
          // No justification for that line
          // Important to reset back to default after all (if any was set)
          if (nIndex > 0)
            aContentStream.setCharacterSpacing (0);
        }

      // Main draw string
      aContentStream.drawString (sDrawText);
      ++nIndex;

      // Goto next line
      // Handle indent per-line as when right alignment is used, the indentX may
      // differ from line to line
      if (bBeforeLastLine)
      {
        // Outdent and one line down, except for last line
        aContentStream.moveTextPositionByAmount (-fIndentX, -fTextHeight * m_fLineSpacing);
      }
    }

    aContentStream.endText ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("OriginalText", m_sOriginalText)
                            .append ("TextWithPlaceholdersReplaced", m_sTextWithPlaceholdersReplaced)
                            .append ("FontSpec", m_aFontSpec)
                            .append ("LineSpacing", m_fLineSpacing)
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("MaxRows", m_nMaxRows)
                            .append ("VertSplittable", m_bVertSplittable)
                            .append ("ReplacePlaceholder", m_bReplacePlaceholder)
                            .append ("BorderRadius", m_fBorderRadius)
                            .append ("CustomAscentFirstLine", m_fCustomAscentFirstLine)
                            .getToString ();
  }
}
