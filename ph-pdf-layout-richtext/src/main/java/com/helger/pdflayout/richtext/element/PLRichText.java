/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.richtext.element;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.pdflayout.base.AbstractPLInlineElement;
import com.helger.pdflayout.base.IPLHasHorizontalAlignment;
import com.helger.pdflayout.base.IPLSplittableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLElementWithSize;
import com.helger.pdflayout.base.PLSplitResult;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PLAnchorRegistry;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.PreparationContextGlobal;
import com.helger.pdflayout.richtext.annotation.EPLBackgroundExtent;
import com.helger.pdflayout.richtext.annotation.IPLRichTextAnnotation;
import com.helger.pdflayout.richtext.annotation.PLAnchorAnnotation;
import com.helger.pdflayout.richtext.annotation.PLBackgroundAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.richtext.run.PLRichTextRun;
import com.helger.pdflayout.richtext.run.PLRichTextRunBuilder;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A multi-style rich-text inline element. Each {@link PLRichTextRun} carries its own
 * {@link FontSpec} and an arbitrary list of inline annotations (underline, hyperlink, anchor). The
 * text is word-wrapped to the available width during preparation and rendered segment by segment
 * during the render phase. Inline underlines are stroked after the baseline glyphs are emitted;
 * hyperlinks produce one PDF link annotation per visible segment; anchors register a named
 * destination at the segment's origin via {@link PLAnchorRegistry}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLRichText extends AbstractPLInlineElement <PLRichText> implements
                        IPLHasHorizontalAlignment <PLRichText>,
                        IPLSplittableObject <PLRichText, PLRichText>
{
  public static final float DEFAULT_LINE_SPACING = 1f;

  private final ICommonsList <PLRichTextRun> m_aRuns;
  private float m_fLineSpacing = DEFAULT_LINE_SPACING;
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private boolean m_bVertSplittable = DEFAULT_VERT_SPLITTABLE;

  // prepare result
  private transient ICommonsList <PLRichTextLine> m_aPreparedLines;
  private transient float m_fTextHeight;
  private transient float m_fDescent;

  public PLRichText (@NonNull final ICommonsList <PLRichTextRun> aRuns)
  {
    ValueEnforcer.notNull (aRuns, "Runs");
    m_aRuns = new CommonsArrayList <> (aRuns);
  }

  /**
   * Convenience: parse markup using a font family, default size and default color.
   *
   * @param sMarkup
   *        The markup string. May not be <code>null</code>.
   * @param aFontFamily
   *        The font family providing regular/bold/italic/bolditalic variants. May not be
   *        <code>null</code>.
   * @param fFontSize
   *        The base font size in points. Must be &gt; 0.
   * @param aDefaultColor
   *        The default color applied to runs without an explicit color marker. May not be
   *        <code>null</code>.
   * @return A new {@link PLRichText}, never <code>null</code>.
   */
  @NonNull
  public static PLRichText createFromMarkup (@NonNull final String sMarkup,
                                             @NonNull final PLFontFamily aFontFamily,
                                             @Nonnegative final float fFontSize,
                                             @NonNull final PLColor aDefaultColor)
  {
    final PLRichTextRunBuilder aBuilder = new PLRichTextRunBuilder (aFontFamily, fFontSize, aDefaultColor);
    return new PLRichText (aBuilder.buildFromMarkup (sMarkup));
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public PLRichText setBasicDataFrom (@NonNull final PLRichText aSource)
  {
    super.setBasicDataFrom (aSource);
    setLineSpacing (aSource.getLineSpacing ());
    setHorzAlign (aSource.getHorzAlign ());
    setVertSplittable (aSource.isVertSplittable ());
    return this;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <PLRichTextRun> getAllRuns ()
  {
    return m_aRuns.getClone ();
  }

  public float getLineSpacing ()
  {
    return m_fLineSpacing;
  }

  @NonNull
  public PLRichText setLineSpacing (@Nonnegative final float fLineSpacing)
  {
    ValueEnforcer.isGT0 (fLineSpacing, "LineSpacing");
    m_fLineSpacing = fLineSpacing;
    return this;
  }

  @Override
  @NonNull
  public EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Override
  @NonNull
  public PLRichText setHorzAlign (@NonNull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
    return this;
  }

  @Override
  public boolean isVertSplittable ()
  {
    return m_bVertSplittable;
  }

  @Override
  @NonNull
  public PLRichText setVertSplittable (final boolean bVertSplittable)
  {
    m_bVertSplittable = bVertSplittable;
    return this;
  }

  /**
   * Layout pass. Walks the run list, calls {@link LoadedFont#getStringWidth} on each word, and
   * emits {@link PLRichTextLine}s that fit within {@code fAvailableWidth}. Embedded {@code '\n'}
   * characters force a hard line break. When a run overflows the current line we split on the last
   * whitespace - the second half inherits the run's font and annotations.
   */
  @NonNull
  private ICommonsList <PLRichTextLine> _layout (final float fAvailableWidth,
                                                 @NonNull final ICommonsMap <FontSpec, LoadedFont> aLoadedFonts) throws IOException
  {
    final ICommonsList <PLRichTextLine> aLines = new CommonsArrayList <> ();
    ICommonsList <PLRichTextSegment> aCurrent = new CommonsArrayList <> ();
    float fCurrentWidth = 0f;

    for (final PLRichTextRun aRun : m_aRuns)
    {
      final FontSpec aFontSpec = aRun.getFontSpec ();
      final LoadedFont aLoadedFont = aLoadedFonts.get (aFontSpec);
      final float fFontSize = aFontSpec.getFontSize ();
      final ICommonsList <IPLRichTextAnnotation> aAnnotations = aRun.getAllAnnotations ();

      // Split run on hard newlines first.
      final String [] aHardLines = aRun.getText ().split ("\n", -1);
      for (int nHL = 0; nHL < aHardLines.length; ++nHL)
      {
        String sRemaining = aHardLines[nHL];
        while (sRemaining.length () > 0)
        {
          final float fAvail = fAvailableWidth - fCurrentWidth;
          final float fSegWidth = aLoadedFont.getStringWidth (sRemaining, fFontSize);
          if (fSegWidth <= fAvail)
          {
            // Fits as a whole.
            aCurrent.add (new PLRichTextSegment (sRemaining, aFontSpec, aLoadedFont, fSegWidth, aAnnotations, aRun.getBaselineOffsetScale ()));
            fCurrentWidth += fSegWidth;
            sRemaining = "";
          }
          else
          {
            // Need to break. Find the last space that still fits.
            final int nBreakAt = _findBreakPoint (sRemaining, aLoadedFont, fFontSize, fAvail);
            if (nBreakAt <= 0)
            {
              // Nothing of this run fits on the current line.
              if (aCurrent.isEmpty ())
              {
                // Cannot wrap - emit at least one char.
                final int nForce = Math.max (1, _findBreakPoint (sRemaining, aLoadedFont, fFontSize, fAvail, true));
                final String sPart = sRemaining.substring (0, nForce);
                final float fPartWidth = aLoadedFont.getStringWidth (sPart, fFontSize);
                aCurrent.add (new PLRichTextSegment (sPart, aFontSpec, aLoadedFont, fPartWidth, aAnnotations, aRun.getBaselineOffsetScale ()));
                fCurrentWidth += fPartWidth;
                sRemaining = sRemaining.substring (nForce);
              }
              // Flush current line and try again.
              aLines.add (new PLRichTextLine (aCurrent, fCurrentWidth, false));
              aCurrent = new CommonsArrayList <> ();
              fCurrentWidth = 0f;
            }
            else
            {
              // Break at last whitespace.
              String sPart = sRemaining.substring (0, nBreakAt);
              // Drop the trailing space the break sits on, if any.
              int nConsume = nBreakAt;
              if (sPart.endsWith (" "))
              {
                sPart = sPart.substring (0, sPart.length () - 1);
              }
              else
                if (nBreakAt < sRemaining.length () && sRemaining.charAt (nBreakAt) == ' ')
                {
                  nConsume = nBreakAt + 1;
                }
              final float fPartWidth = aLoadedFont.getStringWidth (sPart, fFontSize);
              aCurrent.add (new PLRichTextSegment (sPart, aFontSpec, aLoadedFont, fPartWidth, aAnnotations, aRun.getBaselineOffsetScale ()));
              fCurrentWidth += fPartWidth;
              aLines.add (new PLRichTextLine (aCurrent, fCurrentWidth, false));
              aCurrent = new CommonsArrayList <> ();
              fCurrentWidth = 0f;
              sRemaining = sRemaining.substring (nConsume);
            }
          }
        }

        if (nHL < aHardLines.length - 1)
        {
          // hard newline between split pieces of this run
          aLines.add (new PLRichTextLine (aCurrent, fCurrentWidth, true));
          aCurrent = new CommonsArrayList <> ();
          fCurrentWidth = 0f;
        }
      }
    }

    // Flush trailing partial line.
    if (!aCurrent.isEmpty ())
    {
      aLines.add (new PLRichTextLine (aCurrent, fCurrentWidth, true));
    }
    else
      if (aLines.isEmpty ())
      {
        // Empty input - keep a single empty line so the element still occupies a row.
        aLines.add (new PLRichTextLine (new CommonsArrayList <> (), 0f, true));
      }
    return aLines;
  }

  /**
   * Find the byte offset in {@code sText} at the last whitespace whose preceding substring fits in
   * {@code fAvail}. Returns -1 if no such whitespace exists.
   */
  private static int _findBreakPoint (@NonNull final String sText,
                                      @NonNull final LoadedFont aLoadedFont,
                                      final float fFontSize,
                                      final float fAvail) throws IOException
  {
    return _findBreakPoint (sText, aLoadedFont, fFontSize, fAvail, false);
  }

  private static int _findBreakPoint (@NonNull final String sText,
                                      @NonNull final LoadedFont aLoadedFont,
                                      final float fFontSize,
                                      final float fAvail,
                                      final boolean bByChar) throws IOException
  {
    float fSum = 0f;
    int nLastSpace = -1;
    for (int i = 0; i < sText.length (); ++i)
    {
      final char c = sText.charAt (i);
      final float fW = aLoadedFont.getStringWidth (String.valueOf (c), fFontSize);
      if (fSum + fW > fAvail)
      {
        if (bByChar)
          return i;
        return nLastSpace;
      }
      fSum += fW;
      if (c == ' ')
        nLastSpace = i;
    }
    // Whole string fits.
    return sText.length ();
  }

  @Override
  @NonNull
  protected SizeSpec onPrepare (@NonNull final PreparationContext aCtx)
  {
    final float fElementWidth = aCtx.getAvailableWidth () - getOutlineXSum ();

    try
    {
      final PreparationContextGlobal aGlobal = aCtx.getGlobalContext ();
      final ICommonsMap <FontSpec, LoadedFont> aLoadedFonts = new CommonsHashMap <> ();
      float fMaxTextHeight = 0f;
      float fMaxDescent = 0f;
      for (final PLRichTextRun aRun : m_aRuns)
      {
        final FontSpec aFontSpec = aRun.getFontSpec ();
        LoadedFont aLoaded = aLoadedFonts.get (aFontSpec);
        if (aLoaded == null)
        {
          aLoaded = aGlobal.getLoadedFont (aFontSpec);
          aLoadedFonts.put (aFontSpec, aLoaded);
        }
        final float fFontSize = aFontSpec.getFontSize ();
        fMaxTextHeight = Math.max (fMaxTextHeight, aLoaded.getTextHeight (fFontSize));
        fMaxDescent = Math.max (fMaxDescent, aLoaded.getDescent (fFontSize));
      }
      m_fTextHeight = fMaxTextHeight;
      m_fDescent = fMaxDescent;

      m_aPreparedLines = _layout (fElementWidth, aLoadedFonts);

      float fMaxLineWidth = 0f;
      for (final PLRichTextLine aLine : m_aPreparedLines)
        fMaxLineWidth = Math.max (fMaxLineWidth, aLine.getWidth ());

      final int nLineCount = m_aPreparedLines.size ();
      final float fTotalHeight = _getHeightOfLines (nLineCount);
      return new SizeSpec (fMaxLineWidth, fTotalHeight);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to prepare rich-text element: " + toString (), ex);
    }
  }

  private float _getHeightOfLines (final int nLineCount)
  {
    if (nLineCount <= 0)
      return 0f;
    if (nLineCount == 1)
      return m_fTextHeight;
    return (nLineCount - 1) * m_fTextHeight * m_fLineSpacing + m_fTextHeight;
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    m_aPreparedLines = null;
  }

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    if (m_aPreparedLines == null || m_aPreparedLines.isEmpty ())
      return;

    final float fRenderLeft = aCtx.getStartLeft () + getOutlineLeft ();
    final float fRenderTop = aCtx.getStartTop () - getOutlineTop ();
    final float fPreparedWidth = getPreparedWidth ();
    final PDPageContentStreamWithCache aCS = aCtx.getContentStream ();
    final boolean bJustify = m_eHorzAlign == EHorzAlignment.JUSTIFY;
    final boolean bBlock = m_eHorzAlign == EHorzAlignment.BLOCK;

    final int nLineCount = m_aPreparedLines.size ();
    for (int nLine = 0; nLine < nLineCount; ++nLine)
    {
      final PLRichTextLine aLine = m_aPreparedLines.get (nLine);
      final float fLineWidth = aLine.getWidth ();
      final float fIndentX = getIndentX (fPreparedWidth, fLineWidth);
      // PDF user-space baseline of this line (Y grows upward).
      final float fBaselineY = fRenderTop - m_fTextHeight - m_fDescent - nLine * m_fTextHeight * m_fLineSpacing;
      final boolean bIsLastLine = nLine == nLineCount - 1;
      // Deepest descent (most-negative value) across the segments of THIS line.
      // We can't reuse the element-wide m_fDescent because onPrepare folds it
      // with Math.max starting at 0, which clamps negative descents to 0 — the
      // layout tolerates that for baseline positioning but a LINE_HEIGHT
      // highlight needs the actual depth so descenders are covered.
      float fLineDescent = 0f;
      for (final PLRichTextSegment aSeg : aLine.segments ())
      {
        final float fSegDescent = aSeg.getLoadedFont ().getDescent (aSeg.getFontSpec ().getFontSize ());
        if (fSegDescent < fLineDescent)
          fLineDescent = fSegDescent;
      }

      // Compute character spacing for JUSTIFY/BLOCK. This matches how
      // AbstractPLText justifies — extra width is distributed across all
      // inter-character gaps on the line (Tc operator).
      float fCharSpacing = 0f;
      final boolean bLastLine = nLine == nLineCount - 1;
      final int nLineChars = _countCharsInLine (aLine);
      if ((bJustify && !bLastLine) || (bBlock && !aLine.isEndsWithHardBreak ()))
      {
        if (nLineChars > 1)
          fCharSpacing = (fPreparedWidth - fLineWidth) / (nLineChars - 1);
      }

      float fCursorX = fRenderLeft + fIndentX;
      for (final PLRichTextSegment aSeg : aLine.segments ())
      {
        final float fSegStartX = fCursorX;
        final float fSegBaseWidth = aSeg.getWidth ();
        final int nSegChars = aSeg.getText ().length ();
        // Width contribution from justify expansion. Tc adds spacing after every
        // glyph emitted; the last glyph of a segment is followed by another segment
        // (or end-of-line). We allocate (nSegChars) gaps per segment so the cursor
        // advances correctly across the segment boundary.
        final float fSegExtra = nSegChars * fCharSpacing;
        final float fSegFullWidth = fSegBaseWidth + fSegExtra;

        // Sub/superscript: shift the rendered baseline by fontSize *
        // baselineOffsetScale. Positive offset = downward (subscript), negative
        // offset = upward (superscript). The underline draws under the *shifted*
        // glyphs so it tracks the visible text.
        final float fFontSize = aSeg.getFontSpec ().getFontSize ();
        final float fSegBaselineY = fBaselineY - fFontSize * aSeg.getBaselineOffsetScale ();

        // Pre-text pass: backgrounds must paint BEFORE the glyphs so the text
        // stays on top. Width-zero or empty segments contribute nothing visible.
        if (nSegChars > 0 && fSegFullWidth > 0f)
        {
          for (final IPLRichTextAnnotation aAnn : aSeg.annotations ())
          {
            if (aAnn instanceof final PLBackgroundAnnotation aBg)
            {
              _drawBackground (aCS,
                               aSeg,
                               aBg,
                               fSegStartX,
                               fSegFullWidth,
                               fSegBaselineY,
                               fBaselineY,
                               fLineDescent,
                               bIsLastLine);
            }
          }
        }

        if (nSegChars > 0)
        {
          aCS.beginText ();
          aCS.setFont (aSeg.getLoadedFont (), aSeg.getFontSpec ());
          aCS.setCharacterSpacing (fCharSpacing);
          aCS.moveTextPositionByAmount (fSegStartX, fSegBaselineY);
          aCS.drawString (aSeg.getText ());
          aCS.endText ();
        }

        // Post-text pass: underline, hyperlink, anchor (drawn over the glyphs
        // where applicable, or registered with the page).
        for (final IPLRichTextAnnotation aAnn : aSeg.annotations ())
        {
          if (aAnn instanceof final PLUnderlineAnnotation aUnderline)
          {
            _drawUnderline (aCS, aSeg, aUnderline, fSegStartX, fSegBaselineY, fSegFullWidth, fFontSize);
          }
          else
            if (aAnn instanceof final PLHyperlinkAnnotation aHyperlink)
            {
              _addHyperlinkAnnotation (aCtx, aHyperlink, fSegStartX, fSegBaselineY, fSegFullWidth, fFontSize);
            }
            else
              if (aAnn instanceof final PLAnchorAnnotation aAnchor)
              {
                PLAnchorRegistry.registerNamedDestination (aCtx.getDocument (),
                                                           aAnchor.getName (),
                                                           aCS.getPage (),
                                                           fSegStartX,
                                                           fSegBaselineY + fFontSize);
              }
        }

        fCursorX += fSegFullWidth;
      }
    }
  }

  private static int _countCharsInLine (@NonNull final PLRichTextLine aLine)
  {
    int n = 0;
    for (final PLRichTextSegment aSeg : aLine.segments ())
      n += aSeg.getText ().length ();
    return n;
  }

  private void _drawBackground (@NonNull final PDPageContentStreamWithCache aCS,
                                @NonNull final PLRichTextSegment aSeg,
                                @NonNull final PLBackgroundAnnotation aBg,
                                final float fStartX,
                                final float fSegWidth,
                                final float fSegBaselineY,
                                final float fLineBaselineY,
                                final float fLineDescent,
                                final boolean bIsLastLine) throws IOException
  {
    // Note on the descent sign: PDFontDescriptor stores Descent as a NEGATIVE
    // value (the PDF spec convention — distance below the baseline). We follow
    // the same convention here: `baseline + descent` lands BELOW the baseline.
    final float fBottomY;
    final float fHeight;
    if (aBg.getExtent () == EPLBackgroundExtent.LINE_HEIGHT)
    {
      // Full line slot of the enclosing element. Anchor at the line's UNshifted
      // baseline (so sub/superscript segments don't perturb the box) and pull
      // the bottom down by the line's deepest descent so descenders are
      // covered. Height is the baseline-to-baseline distance for non-last
      // lines (which makes consecutive lines' boxes contiguous through the
      // leading gap) and the bare textHeight for the last line.
      fBottomY = fLineBaselineY + fLineDescent;
      fHeight = bIsLastLine ? m_fTextHeight : m_fTextHeight * m_fLineSpacing;
    }
    else
    {
      // Tight box around this segment's own font, anchored on the SHIFTED
      // baseline so sub/superscript highlights follow the visible glyphs.
      final float fFontSize = aSeg.getFontSpec ().getFontSize ();
      final float fSegDescent = aSeg.getLoadedFont ().getDescent (fFontSize);
      final float fSegTextHeight = aSeg.getLoadedFont ().getTextHeight (fFontSize);
      fBottomY = fSegBaselineY + fSegDescent;
      fHeight = fSegTextHeight;
    }
    aCS.setNonStrokingColor (aBg.getColor ());
    aCS.fillRect (fStartX, fBottomY, fSegWidth, fHeight);
  }

  private static void _drawUnderline (@NonNull final PDPageContentStreamWithCache aCS,
                                      @NonNull final PLRichTextSegment aSeg,
                                      @NonNull final PLUnderlineAnnotation aUnderline,
                                      final float fStartX,
                                      final float fBaselineY,
                                      final float fSegWidth,
                                      final float fFontSize) throws IOException
  {
    final float fLineY = fBaselineY + fFontSize * aUnderline.getBaselineOffsetScale ();
    final float fLineWeight = fFontSize * aUnderline.getLineWeight () / 10f;
    aCS.setStrokingColor (aSeg.getFontSpec ().getColor ());
    aCS.setLineWidth (fLineWeight);
    aCS.drawLine (fStartX, fLineY, fStartX + fSegWidth, fLineY);
    aCS.stroke ();
  }

  private static void _addHyperlinkAnnotation (@NonNull final PageRenderContext aCtx,
                                               @NonNull final PLHyperlinkAnnotation aHyperlink,
                                               final float fStartX,
                                               final float fBaselineY,
                                               final float fSegWidth,
                                               final float fFontSize) throws IOException
  {
    final PDAction aAction;
    if (aHyperlink.isInternalAnchorReference ())
    {
      // strip leading '#'
      final String sName = aHyperlink.getUri ().substring (1);
      if (sName.isEmpty ())
        return;
      final PDActionGoTo aGoTo = new PDActionGoTo ();
      aGoTo.setDestination (new PDNamedDestination (sName));
      aAction = aGoTo;
    }
    else
    {
      final PDActionURI aURI = new PDActionURI ();
      aURI.setURI (aHyperlink.getUri ());
      aAction = aURI;
    }

    final PDAnnotationLink aLink = new PDAnnotationLink ();
    // Invisible border so the link rectangle doesn't paint over the glyphs.
    final PDBorderStyleDictionary aBorder = new PDBorderStyleDictionary ();
    aBorder.setWidth (0f);
    aLink.setBorderStyle (aBorder);
    aLink.setAction (aAction);

    final PDRectangle aRect = new PDRectangle (fStartX, fBaselineY, fSegWidth, fFontSize);
    aLink.setRectangle (aRect);
    aCtx.getContentStream ().getPage ().getAnnotations ().add (aLink);
  }

  @Override
  @NonNull
  public PLRichText internalCreateNewVertSplitObject (@NonNull final PLRichText aBase)
  {
    final PLRichText ret = new PLRichText (aBase.m_aRuns);
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  /**
   * Split lines vertically. Builds two new {@link PLRichText} fragments by re-flattening the
   * prepared lines into run lists. Each fragment is marked as already prepared.
   */
  @Override
  @NonNull
  public PLSplitResult splitElementVert (final float fElementWidth, final float fAvailableHeight)
  {
    if (fAvailableHeight <= 0)
      return PLSplitResult.allOnSecond ();
    if (m_aPreparedLines == null)
      return PLSplitResult.allOnSecond ();

    final int nLineCount = m_aPreparedLines.size ();
    if (nLineCount <= 1)
      return PLSplitResult.allOnFirst ();

    // How many lines fit?
    int nFit = (int) ((fAvailableHeight + (m_fLineSpacing - 1f) * m_fTextHeight) / (m_fTextHeight * m_fLineSpacing));
    if (nFit <= 0)
      return PLSplitResult.allOnSecond ();
    // Honour the cap.
    if (_getHeightOfLines (nFit) > fAvailableHeight)
      --nFit;
    if (nFit <= 0)
      return PLSplitResult.allOnSecond ();
    if (nFit >= nLineCount)
      return PLSplitResult.allOnFirst ();

    final List <PLRichTextLine> aFirstLines = m_aPreparedLines.subList (0, nFit);
    final List <PLRichTextLine> aSecondLines = m_aPreparedLines.subList (nFit, nLineCount);

    final PLElementWithSize aPart1 = _splitGetCopy (aFirstLines, false, true, "-1");
    final PLElementWithSize aPart2 = _splitGetCopy (aSecondLines, true, false, "-2");
    return PLSplitResult.createSplit (aPart1, aPart2);
  }

  @NonNull
  private PLElementWithSize _splitGetCopy (@NonNull final List <PLRichTextLine> aLines,
                                           final boolean bSplittableCopy,
                                           final boolean bIsFirstHalf,
                                           @NonNull final String sIDSuffix)
  {
    final ICommonsList <PLRichTextLine> aLineCopy = new CommonsArrayList <> (aLines);
    final ICommonsList <PLRichTextRun> aFragmentRuns = _runsFromLines (aLineCopy);

    final PLRichText aNew = new PLRichText (aFragmentRuns);
    aNew.setBasicDataFrom (this);
    aNew.internalMarkAsSplitFragment (this, bIsFirstHalf, sIDSuffix);
    aNew.setVertSplittable (bSplittableCopy);

    float fMaxLineWidth = 0f;
    for (final PLRichTextLine aLine : aLineCopy)
      fMaxLineWidth = Math.max (fMaxLineWidth, aLine.getWidth ());

    aNew.m_fTextHeight = m_fTextHeight;
    aNew.m_fDescent = m_fDescent;
    aNew.m_aPreparedLines = aLineCopy;

    final SizeSpec aSize = new SizeSpec (fMaxLineWidth, aNew._getHeightOfLines (aLineCopy.size ()));
    aNew.internalMarkAsPrepared (aSize);

    return new PLElementWithSize (aNew, aSize);
  }

  /**
   * Reconstruct a run list from a sequence of prepared lines. Used by the split helper so the
   * fragment object's {@code getAllRuns()} reflects the lines it actually holds.
   */
  @NonNull
  private static ICommonsList <PLRichTextRun> _runsFromLines (@NonNull final ICommonsList <PLRichTextLine> aLines)
  {
    final ICommonsList <PLRichTextRun> aRuns = new CommonsArrayList <> ();
    final int nLineCount = aLines.size ();
    for (int nL = 0; nL < nLineCount; ++nL)
    {
      final PLRichTextLine aLine = aLines.get (nL);
      for (final PLRichTextSegment aSeg : aLine.segments ())
      {
        aRuns.add (new PLRichTextRun (aSeg.getText (), aSeg.getFontSpec (), aSeg.annotations ()));
      }
      if (nL < nLineCount - 1)
      {
        // Encode hard newline between lines so that re-preparation reproduces them.
        aRuns.add (new PLRichTextRun ("\n",
                                      aLines.get (nL).segments ().isEmpty () ? new FontSpec (PreloadFont.REGULAR,
                                                                                                   10f) : aLines.get (
                                                                                                                      nL)
                                                                                                                .segments ()
                                                                                                                .getFirstOrNull ()
                                                                                                                .getFontSpec ()));
      }
    }
    return aRuns;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Runs", m_aRuns)
                            .append ("LineSpacing", m_fLineSpacing)
                            .append ("HorzAlign", m_eHorzAlign)
                            .append ("VertSplittable", m_bVertSplittable)
                            .getToString ();
  }
}
