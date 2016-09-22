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
package com.helger.pdflayout.base;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.PLDebug;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.ERenderingElementType;
import com.helger.pdflayout.render.IRenderingContextCustomizer;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.PreparationContextGlobal;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.MarginSpec;
import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Represents a single page layout as element. It consists of a page size, a
 * page header and footer as well as a set of page body elements.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PLPageSet extends AbstractPLObject <PLPageSet>
                       implements IPLHasMarginBorderPadding <PLPageSet>, IPLHasFillColor <PLPageSet>
{
  public static final class PageSetPrepareResult
  {
    private float m_fHeaderHeight = Float.NaN;
    private final ICommonsList <PLElementWithSize> m_aContentHeight = new CommonsArrayList<> ();
    private float m_fFooterHeight = Float.NaN;
    private final ICommonsList <ICommonsList <PLElementWithSize>> m_aPerPageElements = new CommonsArrayList<> ();

    PageSetPrepareResult ()
    {}

    /**
     * Set the page header height.
     *
     * @param fHeaderHeight
     *        Height without margin, border and padding.
     */
    void setHeaderHeight (final float fHeaderHeight)
    {
      m_fHeaderHeight = fHeaderHeight;
    }

    /**
     * @return Page header height without margin, border and padding.
     */
    public float getHeaderHeight ()
    {
      return m_fHeaderHeight;
    }

    /**
     * @param aElement
     *        The element to be added. May not be <code>null</code>. The element
     *        height must be without padding or margin.
     */
    void addElement (@Nonnull final PLElementWithSize aElement)
    {
      ValueEnforcer.notNull (aElement, "Element");
      m_aContentHeight.add (aElement);
    }

    /**
     * @return A list of all elements. Never <code>null</code>. The height of
     *         the contained elements is without padding or margin.
     */
    @Nonnull
    @ReturnsMutableCopy
    ICommonsList <PLElementWithSize> getAllElements ()
    {
      return m_aContentHeight.getClone ();
    }

    /**
     * Set the page footer height.
     *
     * @param fFooterHeight
     *        Height without padding or margin.
     */
    void setFooterHeight (final float fFooterHeight)
    {
      m_fFooterHeight = fFooterHeight;
    }

    /**
     * @return Page footer height without padding or margin.
     */
    public float getFooterHeight ()
    {
      return m_fFooterHeight;
    }

    /**
     * Add a list of elements for a single page. This implicitly creates a new
     * page.
     *
     * @param aCurPageElements
     *        The list to use. May neither be <code>null</code> nor empty.
     */
    void addPerPageElements (@Nonnull @Nonempty final ICommonsList <PLElementWithSize> aCurPageElements)
    {
      ValueEnforcer.notEmptyNoNullValue (aCurPageElements, "CurPageElements");
      m_aPerPageElements.add (aCurPageElements);
    }

    @Nonnegative
    public int getPageCount ()
    {
      return m_aPerPageElements.size ();
    }

    @Nonnegative
    public int getPageNumber ()
    {
      return getPageCount () + 1;
    }

    @Nonnull
    @ReturnsMutableObject ("speed")
    ICommonsList <ICommonsList <PLElementWithSize>> directGetPerPageElements ()
    {
      return m_aPerPageElements;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (PLPageSet.class);

  private final SizeSpec m_aPageSize;
  private MarginSpec m_aMargin = DEFAULT_MARGIN;
  private PaddingSpec m_aPadding = DEFAULT_PADDING;
  private BorderSpec m_aBorder = DEFAULT_BORDER;
  private Color m_aFillColor = DEFAULT_FILL_COLOR;
  private IPLRenderableObject <?> m_aPageHeader;
  private final ICommonsList <IPLRenderableObject <?>> m_aElements = new CommonsArrayList<> ();
  private IPLRenderableObject <?> m_aPageFooter;
  private IRenderingContextCustomizer m_aRCCustomizer;

  public PLPageSet (@Nonnull final PDRectangle aPageRect)
  {
    this (SizeSpec.create (aPageRect));
  }

  public PLPageSet (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    this (new SizeSpec (fWidth, fHeight));
  }

  public PLPageSet (@Nonnull final SizeSpec aPageSize)
  {
    m_aPageSize = ValueEnforcer.notNull (aPageSize, "PageSize");
  }

  @Nonnull
  public SizeSpec getPageSize ()
  {
    return m_aPageSize;
  }

  public float getPageWidth ()
  {
    return m_aPageSize.getWidth ();
  }

  public float getPageHeight ()
  {
    return m_aPageSize.getHeight ();
  }

  @Nonnull
  public final PLPageSet setMargin (@Nonnull final MarginSpec aMargin)
  {
    ValueEnforcer.notNull (aMargin, "Mergin");
    m_aMargin = aMargin;
    return this;
  }

  @Nonnull
  public final MarginSpec getMargin ()
  {
    return m_aMargin;
  }

  @Nonnull
  public final PLPageSet setPadding (@Nonnull final PaddingSpec aPadding)
  {
    ValueEnforcer.notNull (aPadding, "Padding");
    m_aPadding = aPadding;
    return this;
  }

  @Nonnull
  public final PaddingSpec getPadding ()
  {
    return m_aPadding;
  }

  @Nonnull
  public final PLPageSet setBorder (@Nonnull final BorderSpec aBorder)
  {
    ValueEnforcer.notNull (aBorder, "Border");
    m_aBorder = aBorder;
    return this;
  }

  @Nonnull
  public final BorderSpec getBorder ()
  {
    return m_aBorder;
  }

  @Nonnull
  public PLPageSet setFillColor (@Nullable final Color aFillColor)
  {
    m_aFillColor = aFillColor;
    return this;
  }

  @Nullable
  public Color getFillColor ()
  {
    return m_aFillColor;
  }

  @Nullable
  public IRenderingContextCustomizer getRenderingContextCustomizer ()
  {
    return m_aRCCustomizer;
  }

  @Nonnull
  public PLPageSet setRenderingContextCustomizer (@Nullable final IRenderingContextCustomizer aRCCustomizer)
  {
    m_aRCCustomizer = aRCCustomizer;
    return this;
  }

  /**
   * @return The usable page width without the x-paddings, x-borders and
   *         x-margins
   */
  @Nonnegative
  public float getAvailableWidth ()
  {
    return m_aPageSize.getWidth () - getFullXSum ();
  }

  /**
   * @return The usable page height without the y-paddings, y-borders and
   *         y-margins
   */
  @Nonnegative
  public float getAvailableHeight ()
  {
    return m_aPageSize.getHeight () - getFullYSum ();
  }

  /**
   * @return The global page header. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getPageHeader ()
  {
    return m_aPageHeader;
  }

  /**
   * @return <code>true</code> if a global page header is present,
   *         <code>false</code> if not.
   */
  public boolean hasPageHeader ()
  {
    return m_aPageHeader != null;
  }

  /**
   * Set the global page header
   *
   * @param aPageHeader
   *        The global page header. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageHeader (@Nullable final IPLRenderableObject <?> aPageHeader)
  {
    m_aPageHeader = aPageHeader;
    return this;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <? extends IPLRenderableObject <?>> getAllElements ()
  {
    return m_aElements.getClone ();
  }

  @Nonnegative
  public int getElementCount ()
  {
    return m_aElements.size ();
  }

  public void forEachElement (@Nonnull final Consumer <? super IPLRenderableObject <?>> aConsumer)
  {
    m_aElements.forEach (aConsumer);
  }

  @Nonnull
  public PLPageSet addElement (@Nonnull final IPLRenderableObject <?> aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");
    m_aElements.add (aElement);
    return this;
  }

  /**
   * @return The global page footer. May be <code>null</code>.
   */
  @Nullable
  public IPLRenderableObject <?> getPageFooter ()
  {
    return m_aPageFooter;
  }

  /**
   * @return <code>true</code> if a global page footer is present,
   *         <code>false</code> if not.
   */
  public boolean hasPageFooter ()
  {
    return m_aPageFooter != null;
  }

  /**
   * Set the global page footer
   *
   * @param aPageFooter
   *        The global page footer. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageFooter (@Nullable final IPLRenderableObject <?> aPageFooter)
  {
    m_aPageFooter = aPageFooter;
    return this;
  }

  /**
   * @return The y-top of the page excluding top padding, top-border and
   *         top-margin
   */
  public float getYTop ()
  {
    return m_aPageSize.getHeight () - getFullTop ();
  }

  public void visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    aVisitor.onPageSetStart (this);
    if (m_aPageHeader != null)
      m_aPageHeader.visit (aVisitor);
    if (m_aPageFooter != null)
      m_aPageFooter.visit (aVisitor);
    for (final IPLRenderableObject <?> aElement : m_aElements)
      aElement.visit (aVisitor);
    aVisitor.onPageSetEnd (this);
  }

  @Nonnull
  public PageSetPrepareResult prepareAllPages (@Nonnull final PreparationContextGlobal aGlobalCtx) throws IOException
  {
    // The result element
    final PageSetPrepareResult ret = new PageSetPrepareResult ();

    // Prepare page header
    if (m_aPageHeader != null)
    {
      // Page header does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx,
                                                              m_aPageSize.getWidth () - getMarginXSum (),
                                                              getMarginTop ());
      final SizeSpec aElementSize = m_aPageHeader.prepare (aRPC);
      ret.setHeaderHeight (aElementSize.getHeight ());

      final float fEffectiveHeaderHeight = aElementSize.getHeight () + m_aPageHeader.getFullYSum ();
      if (fEffectiveHeaderHeight > getMarginTop ())
      {
        // If the height of the header exceeds the available top-margin, modify
        // the margin so that the header fits!
        s_aLogger.info ("PageSet margin top was changed from " +
                        getMarginTop () +
                        " to " +
                        fEffectiveHeaderHeight +
                        " so that pageHeader fits!");
        setMarginTop (fEffectiveHeaderHeight);
      }
    }

    // Prepare footer
    if (m_aPageFooter != null)
    {
      // Page footer does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx,
                                                              m_aPageSize.getWidth () - getMarginXSum (),
                                                              getMarginBottom ());
      final SizeSpec aElementSize = m_aPageFooter.prepare (aRPC);
      ret.setFooterHeight (aElementSize.getHeight ());

      final float fEffectiveFooterHeight = aElementSize.getHeight () + m_aPageFooter.getFullYSum ();
      if (fEffectiveFooterHeight > getMarginBottom ())
      {
        // If the height of the footer exceeds the available bottom-margin,
        // modify the margin so that the footer fits!
        s_aLogger.info ("PageSet margin bottom was changed from " +
                        getMarginBottom () +
                        " to " +
                        fEffectiveFooterHeight +
                        " so that pageFooter fits!");
        setMarginBottom (fEffectiveFooterHeight);
      }
    }

    if (getMarginYSum () > m_aPageSize.getHeight ())
      throw new IllegalStateException ("Header and footer together (" +
                                       getMarginYSum () +
                                       ") take more height than available on the page (" +
                                       m_aPageSize.getHeight () +
                                       ")! Cannot render!");

    {
      final float fAvailWidth = getAvailableWidth ();
      final float fAvailHeight = getAvailableHeight ();

      if (PLDebug.isDebugPrepare ())
        PLDebug.debugPrepare (this,
                              "Start preparing elements on width=" +
                                    fAvailWidth +
                                    "+" +
                                    getFullXSum () +
                                    " and height=" +
                                    fAvailHeight +
                                    "+" +
                                    getFullYSum ());

      // Prepare content elements
      // Must be done after header and footer, because the margins may got
      // adopted!
      for (final IPLRenderableObject <?> aElement : m_aElements)
      {
        final PreparationContext aRPC = new PreparationContext (aGlobalCtx, fAvailWidth, fAvailHeight);
        final SizeSpec aElementSize = aElement.prepare (aRPC);
        ret.addElement (new PLElementWithSize (aElement, aElementSize));
      }

      if (PLDebug.isDebugPrepare ())
        PLDebug.debugPrepare (this, "Finished preparing elements");
    }

    // Split into pieces that fit onto a page
    final float fYTop = getYTop ();
    final float fYLeast = getFullBottom ();

    {
      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Start splitting elements");

      ICommonsList <PLElementWithSize> aCurPageElements = new CommonsArrayList<> ();

      // Start at the top
      float fCurY = fYTop;

      // Create a copy of the list, so that we can safely modify it
      final ICommonsList <PLElementWithSize> aElementsWithSize = ret.getAllElements ();
      while (!aElementsWithSize.isEmpty ())
      {
        // Use the first element
        PLElementWithSize aElementWithSize = aElementsWithSize.remove (0);
        final IPLRenderableObject <?> aElement = aElementWithSize.getElement ();

        boolean bIsPagebreakDesired = aElement instanceof PLPageBreak;
        if (bIsPagebreakDesired && aCurPageElements.isEmpty () && !((PLPageBreak) aElement).isForcePageBreak ())
        {
          // a new page was just started and no forced break is present, so no
          // page break is necessary
          bIsPagebreakDesired = false;
        }

        final float fElementWidth = aElementWithSize.getWidth ();
        final float fElementHeightFull = aElementWithSize.getHeightFull ();
        final float fAvailableHeight = fCurY - fYLeast;
        if (fCurY - fElementHeightFull < fYLeast || bIsPagebreakDesired)
        {
          // Element does not fit on page - try to split
          final boolean bIsSplittable = aElement.isSplittable ();
          if (bIsSplittable)
          {
            if (PLDebug.isDebugSplit ())
              PLDebug.debugSplit (this,
                                  "Trying to split " +
                                        aElement.getDebugID () +
                                        " into pieces for available width " +
                                        fElementWidth +
                                        " and height " +
                                        fAvailableHeight);

            // split elements
            final PLSplitResult aSplitResult = aElement.getAsSplittable ()
                                                       .splitElements (fElementWidth,
                                                                       fAvailableHeight - aElement.getFullYSum ());
            if (aSplitResult != null)
            {
              // Re-add them to the list and try again (they may be splitted
              // recursively)
              aElementsWithSize.add (0, aSplitResult.getFirstElement ());
              aElementsWithSize.add (1, aSplitResult.getSecondElement ());

              if (PLDebug.isDebugSplit ())
              {
                PLDebug.debugSplit (this,
                                    "Split " +
                                          aElement.getDebugID () +
                                          " into pieces: " +
                                          aSplitResult.getFirstElement ().getElement ().getDebugID () +
                                          " (" +
                                          aSplitResult.getFirstElement ().getWidth () +
                                          "+" +
                                          aSplitResult.getFirstElement ().getElement ().getFullXSum () +
                                          " & " +
                                          aSplitResult.getFirstElement ().getHeight () +
                                          "+" +
                                          aSplitResult.getFirstElement ().getElement ().getFullYSum () +
                                          ") and " +
                                          aSplitResult.getSecondElement ().getElement ().getDebugID () +
                                          " (" +
                                          aSplitResult.getSecondElement ().getWidth () +
                                          "+" +
                                          aSplitResult.getSecondElement ().getElement ().getFullXSum () +
                                          " & " +
                                          aSplitResult.getSecondElement ().getHeight () +
                                          "+" +
                                          aSplitResult.getSecondElement ().getElement ().getFullYSum () +
                                          ")");
              }
              continue;
            }
            if (PLDebug.isDebugSplit ())
            {
              PLDebug.debugSplit (this,
                                  "The single element " +
                                        aElement.getDebugID () +
                                        " does not fit onto a single page (" +
                                        fAvailableHeight +
                                        ") even though it is splittable!");
            }
          }

          // Next page
          if (aCurPageElements.isEmpty ())
          {
            if (!bIsPagebreakDesired)
            {
              // one element too large for a page
              s_aLogger.warn ("The single element " +
                              aElement.getDebugID () +
                              " does not fit onto a single page" +
                              (bIsSplittable ? " even though it is splittable!" : " and is not splittable!"));
            }
          }
          else
          {
            // We found elements fitting onto a page (at least one)
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Adding " + aCurPageElements.size () + " elements to page " + ret.getPageNumber ());

            if (PLDebug.isDebugPrepare ())
            {
              final ICommonsList <String> aLastPageContent = new CommonsArrayList<> (aCurPageElements,
                                                                                     x -> x.getElement ()
                                                                                           .getDebugID ());
              PLDebug.debugPrepare (this, "Finished page with: " + StringHelper.getImploded (aLastPageContent));
            }

            ret.addPerPageElements (aCurPageElements);
            aCurPageElements = new CommonsArrayList<> ();

            // Start new page
            fCurY = fYTop;

            // Re-add element and continue from start, so that splitting happens
            aElementsWithSize.add (0, aElementWithSize);

            // Continue with next element
            continue;
          }
        }

        // Handle vertical alignment of top-level elements
        if (aElement instanceof IPLHasVerticalAlignment <?>)
        {
          final EVertAlignment eVertAlignment = ((IPLHasVerticalAlignment <?>) aElement).getVertAlign ();
          float fPaddingTop;
          switch (eVertAlignment)
          {
            case TOP:
              fPaddingTop = 0f;
              break;
            case MIDDLE:
              fPaddingTop = (fAvailableHeight - fElementHeightFull) / 2;
              break;
            case BOTTOM:
              fPaddingTop = fAvailableHeight - fElementHeightFull;
              break;
            default:
              throw new IllegalStateException ("Unsupported vertical alignment: " + eVertAlignment);
          }
          if (fPaddingTop != 0f && aElement instanceof AbstractPLElement <?>)
          {
            final AbstractPLElement <?> aRealElement = (AbstractPLElement <?>) aElement;
            final SizeSpec aOldSize = aRealElement.getPreparedSize ();
            aRealElement.internalMarkAsNotPrepared ();
            aRealElement.setPaddingTop (aRealElement.getPaddingTop () + fPaddingTop);
            final SizeSpec aNewSize = new SizeSpec (aOldSize.getWidth (), aOldSize.getHeight () + fPaddingTop);
            aRealElement.internalMarkAsPrepared (aNewSize);
            aElementWithSize = new PLElementWithSize (aRealElement, aNewSize);
          }
        }

        // Add element to current page (may also be a page break)
        aCurPageElements.add (aElementWithSize);
        fCurY -= fElementHeightFull;
      }

      // Add elements to last page
      if (!aCurPageElements.isEmpty ())
      {
        if (PLDebug.isDebugSplit ())
          PLDebug.debugSplit (this,
                              "Finally adding " +
                                    aCurPageElements.size () +
                                    " elements to page " +
                                    ret.getPageNumber ());
        ret.addPerPageElements (aCurPageElements);

        if (PLDebug.isDebugSplit ())
        {
          final ICommonsList <String> aLastPageContent = new CommonsArrayList<> (aCurPageElements,
                                                                                 x -> x.getElement ().getDebugID ());
          PLDebug.debugSplit (this, "Finished last page with: " + StringHelper.getImploded (", ", aLastPageContent));
        }
      }

      if (PLDebug.isDebugSplit ())
        PLDebug.debugSplit (this, "Finished splitting elements");
    }

    return ret;
  }

  /**
   * Render all pages of this layout to the specified PDDocument
   *
   * @param aPrepareResult
   *        The preparation result. May not be <code>null</code>.
   * @param aDoc
   *        The PDDocument. May not be <code>null</code>.
   * @param bDebug
   *        <code>true</code> for debug output
   * @param nPageSetIndex
   *        Page set index. Always &ge; 0.
   * @param nTotalPageStartIndex
   *        Total page index. Always &ge; 0.
   * @param nTotalPageCount
   *        Total page count. Always &ge; 0.
   * @throws IOException
   *         In case of render errors
   */
  public void renderAllPages (@Nonnull final PageSetPrepareResult aPrepareResult,
                              @Nonnull final PDDocument aDoc,
                              final boolean bDebug,
                              @Nonnegative final int nPageSetIndex,
                              @Nonnegative final int nTotalPageStartIndex,
                              @Nonnegative final int nTotalPageCount) throws IOException
  {
    // Start at the left top
    final float fXLeft = getFullLeft ();
    final float fYTop = getYTop ();

    final boolean bCompressPDF = !bDebug;
    int nPageIndex = 0;
    final int nPageCount = aPrepareResult.getPageCount ();
    for (final ICommonsList <PLElementWithSize> aPerPage : aPrepareResult.directGetPerPageElements ())
    {
      if (PLDebug.isDebugRender ())
        PLDebug.debugRender (this,
                             "Start rendering page index " +
                                   nPageIndex +
                                   " (" +
                                   (nTotalPageStartIndex + nPageIndex) +
                                   ") with page size " +
                                   PLDebug.getWH (getPageWidth (), getPageHeight ()) +
                                   " and available size " +
                                   PLDebug.getWH (getAvailableWidth (), getAvailableHeight ()));

      // Layout in memory
      final PDPage aPage = new PDPage (m_aPageSize.getAsRectangle ());
      aDoc.addPage (aPage);

      final PageRenderContext aPageRenderCtx = new PageRenderContext (this,
                                                                      aDoc,
                                                                      aPage,
                                                                      nPageSetIndex,
                                                                      nPageIndex,
                                                                      nPageCount,
                                                                      nTotalPageStartIndex + nPageIndex,
                                                                      nTotalPageCount);
      visitElement (x -> x.doPageSetup (aPageRenderCtx));

      final PDPageContentStreamWithCache aContentStream = new PDPageContentStreamWithCache (aDoc,
                                                                                            aPage,
                                                                                            PDPageContentStream.AppendMode.OVERWRITE,
                                                                                            bCompressPDF);
      try
      {
        // Page rect before content - debug: red

        {
          final float fLeft = getMarginLeft ();
          final float fTop = m_aPageSize.getHeight () - getMarginTop ();
          final float fWidth = m_aPageSize.getWidth () - getMarginXSum ();
          final float fHeight = m_aPageSize.getHeight () - getMarginYSum ();

          // Fill before border
          if (getFillColor () != null)
          {
            aContentStream.setNonStrokingColor (getFillColor ());
            aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
          }

          BorderSpec aRealBorder = getBorder ();
          if (PLRenderHelper.shouldApplyDebugBorder (aRealBorder, bDebug))
            aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_PAGESET));
          if (aRealBorder.hasAnyBorder ())
            PLRenderHelper.renderBorder (this, aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
        }

        // Start with the page rectangle
        if (m_aPageHeader != null)
        {
          // Page header does not care about page padding
          // header top-left
          final float fStartLeft = getMarginLeft ();
          final float fStartTop = m_aPageSize.getHeight ();
          final float fWidth = m_aPageSize.getWidth () - getMarginXSum ();
          final float fHeight = aPrepareResult.getHeaderHeight ();
          final RenderingContext aRC = new RenderingContext (ERenderingElementType.PAGE_HEADER,
                                                             aContentStream,
                                                             bDebug,
                                                             fStartLeft,
                                                             fStartTop,
                                                             fWidth,
                                                             fHeight);
          aPageRenderCtx.setPlaceholdersInRenderingContext (aRC);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderingContext (aRC);
          m_aPageHeader.perform (aRC);
        }

        float fCurY = fYTop;
        for (final PLElementWithSize aElementWithHeight : aPerPage)
        {
          final IPLRenderableObject <?> aElement = aElementWithHeight.getElement ();
          // Get element extent
          float fStartLeft = fXLeft;
          float fStartTop = fCurY;
          float fWidth = getAvailableWidth ();
          float fHeight = aElementWithHeight.getHeight ();
          if (aElement instanceof IPLElement <?>)
          {
            final IPLElement <?> aRealElement = (IPLElement <?>) aElement;
            fStartLeft += aRealElement.getMarginAndBorderLeft ();
            fStartTop -= aRealElement.getMarginAndBorderTop ();
            fWidth -= aRealElement.getMarginAndBorderXSum ();
            fHeight += aRealElement.getBorderYSumWidth () + aRealElement.getPaddingYSum ();
          }

          final RenderingContext aRC = new RenderingContext (ERenderingElementType.CONTENT_ELEMENT,
                                                             aContentStream,
                                                             bDebug,
                                                             fStartLeft,
                                                             fStartTop,
                                                             fWidth,
                                                             fHeight);
          aPageRenderCtx.setPlaceholdersInRenderingContext (aRC);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderingContext (aRC);
          aElement.perform (aRC);

          fCurY -= fHeight + aElement.getFullYSum ();
        }

        if (m_aPageFooter != null)
        {
          // Page footer does not care about page padding
          // footer top-left
          final float fStartLeft = getMarginLeft ();
          final float fStartTop = getMarginBottom ();
          final float fWidth = m_aPageSize.getWidth () - getMarginXSum ();
          final float fHeight = aPrepareResult.getFooterHeight ();
          final RenderingContext aRC = new RenderingContext (ERenderingElementType.PAGE_FOOTER,
                                                             aContentStream,
                                                             bDebug,
                                                             fStartLeft,
                                                             fStartTop,
                                                             fWidth,
                                                             fHeight);
          aPageRenderCtx.setPlaceholdersInRenderingContext (aRC);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderingContext (aRC);
          m_aPageFooter.perform (aRC);
        }
      }
      finally
      {
        aContentStream.close ();
      }
      ++nPageIndex;
    }
    if (PLDebug.isDebugRender ())
      PLDebug.debugRender (this, "Finished rendering");
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("pageSize", m_aPageSize)
                            .appendIfNotNull ("pageHeader", m_aPageHeader)
                            .append ("elements", m_aElements)
                            .appendIfNotNull ("pageFooter", m_aPageFooter)
                            .appendIfNotNull ("RCCustomizer", m_aRCCustomizer)
                            .toString ();
  }
}
