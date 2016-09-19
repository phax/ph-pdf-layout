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

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.ERenderingElementType;
import com.helger.pdflayout.render.IRenderingContextCustomizer;
import com.helger.pdflayout.render.PageSetupContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.PreparationContextGlobal;
import com.helger.pdflayout.render.RenderPageIndex;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Represents a single page layout as element. It consists of a page size, a
 * page header and footer as well as a set of page body elements.
 *
 * @author Philip Helger
 */
public class PLPageSet extends AbstractPLBaseElement <PLPageSet>
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
  private AbstractPLElement <?> m_aPageHeader;
  private final ICommonsList <AbstractPLElement <?>> m_aElements = new CommonsArrayList<> ();
  private AbstractPLElement <?> m_aPageFooter;
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
   * @return The usable page width without the x-paddings and x-margins
   */
  @Nonnegative
  public float getAvailableWidth ()
  {
    return m_aPageSize.getWidth () - getFullXSum ();
  }

  /**
   * @return The usable page height without the y-paddings and y-margins
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
  public AbstractPLElement <?> getPageHeader ()
  {
    return m_aPageHeader;
  }

  /**
   * Set the global page header
   *
   * @param aPageHeader
   *        The global page header. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageHeader (@Nullable final AbstractPLElement <?> aPageHeader)
  {
    m_aPageHeader = aPageHeader;
    return this;
  }

  @Nonnull
  public ICommonsList <? extends AbstractPLElement <?>> getAllElements ()
  {
    return m_aElements.getClone ();
  }

  @Nonnull
  public PLPageSet addElement (@Nonnull final AbstractPLElement <?> aElement)
  {
    ValueEnforcer.notNull (aElement, "Element");
    m_aElements.add (aElement);
    return this;
  }

  /**
   * @return The global page footer. May be <code>null</code>.
   */
  @Nullable
  public AbstractPLElement <?> getPageFooter ()
  {
    return m_aPageFooter;
  }

  /**
   * Set the global page footer
   *
   * @param aPageFooter
   *        The global page footer. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public PLPageSet setPageFooter (@Nullable final AbstractPLElement <?> aPageFooter)
  {
    m_aPageFooter = aPageFooter;
    return this;
  }

  /**
   * @return The y-top of the page
   */
  public float getYTop ()
  {
    return m_aPageSize.getHeight () - getFullTop ();
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
                                                              m_aPageSize.getWidth () -
                                                                          getMarginXSum () -
                                                                          m_aPageHeader.getFullXSum (),
                                                              getMarginTop () - m_aPageHeader.getFullYSum ());
      final SizeSpec aElementSize = m_aPageHeader.prepare (aRPC);
      ret.setHeaderHeight (aElementSize.getHeight ());

      if (aElementSize.getHeight () > getMarginTop ())
      {
        // If the height of the header exceeds the available top-margin, modify
        // the margin so that the header fits!
        setMarginTop (aElementSize.getHeight () + m_aPageHeader.getFullYSum ());
      }
    }

    // Prepare footer
    if (m_aPageFooter != null)
    {
      // Page footer does not care about page padding
      final PreparationContext aRPC = new PreparationContext (aGlobalCtx,
                                                              m_aPageSize.getWidth () -
                                                                          getMarginXSum () -
                                                                          m_aPageFooter.getFullXSum (),
                                                              getMarginBottom () - m_aPageFooter.getFullYSum ());
      final SizeSpec aElementSize = m_aPageFooter.prepare (aRPC);
      ret.setFooterHeight (aElementSize.getHeight ());

      if (aElementSize.getHeight () > getMarginBottom ())
      {
        // If the height of the footer exceeds the available bottom-margin,
        // modify the margin so that the footer fits!
        setMarginBottom (aElementSize.getHeight () + m_aPageFooter.getFullYSum ());
      }
    }

    if (getMarginYSum () > m_aPageSize.getHeight ())
      throw new IllegalStateException ("Header and footer together (" +
                                       getMarginYSum () +
                                       ") take more height than available on the page (" +
                                       m_aPageSize.getHeight () +
                                       ")!");

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
      for (final AbstractPLElement <?> aElement : m_aElements)
      {
        final float fAvailableWidth = fAvailWidth - aElement.getFullXSum ();
        final float fAvailableHeight = fAvailHeight - aElement.getFullYSum ();
        final PreparationContext aRPC = new PreparationContext (aGlobalCtx, fAvailableWidth, fAvailableHeight);
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
        final AbstractPLElement <?> aElement = aElementWithSize.getElement ();

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
              PLDebug.debugSplit (this,
                                  "The single element " +
                                        aElement.getDebugID () +
                                        " does not fit onto a single page (" +
                                        fAvailableHeight +
                                        ") even though it is splittable!");
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
          if (fPaddingTop != 0f)
          {
            final SizeSpec aOldSize = aElement.getPreparedSize ();
            aElement.internalMarkAsNotPrepared ();
            aElement.setPaddingTop (aElement.getPaddingTop () + fPaddingTop);
            final SizeSpec aNewSize = new SizeSpec (aOldSize.getWidth (), aOldSize.getHeight () + fPaddingTop);
            aElement.internalMarkAsPrepared (aNewSize);
            aElementWithSize = new PLElementWithSize (aElement, aNewSize);
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
    // Start at the left
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

      final RenderPageIndex aPageIndex = new RenderPageIndex (nPageSetIndex,
                                                              nPageIndex,
                                                              nPageCount,
                                                              nTotalPageStartIndex + nPageIndex,
                                                              nTotalPageCount);

      // Layout in memory
      final PDPage aPage = new PDPage (m_aPageSize.getAsRectangle ());
      aDoc.addPage (aPage);

      {
        final PageSetupContext aCtx = new PageSetupContext (aDoc, aPage);
        if (m_aPageHeader != null)
          m_aPageHeader.doPageSetup (aCtx);
        for (final PLElementWithSize aElement : aPerPage)
          aElement.getElement ().doPageSetup (aCtx);
        if (m_aPageFooter != null)
          m_aPageFooter.doPageSetup (aCtx);
      }

      final PDPageContentStreamWithCache aContentStream = new PDPageContentStreamWithCache (aDoc,
                                                                                            aPage,
                                                                                            PDPageContentStream.AppendMode.OVERWRITE,
                                                                                            bCompressPDF);
      try
      {
        // Page rect before content - debug: red
        {
          final float fLeft = getMarginAndBorderLeft ();
          final float fTop = m_aPageSize.getHeight () - getMarginAndBorderTop ();
          final float fWidth = m_aPageSize.getWidth () - getMarginAndBorderXSum ();
          final float fHeight = m_aPageSize.getHeight () - getMarginAndBorderYSum ();

          // Fill before border
          if (getFillColor () != null)
          {
            aContentStream.setNonStrokingColor (getFillColor ());
            aContentStream.fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
          }

          BorderSpec aRealBorder = getBorder ();
          if (shouldApplyDebugBorder (aRealBorder, bDebug))
            aRealBorder = new BorderSpec (new BorderStyleSpec (PLDebug.BORDER_COLOR_PAGESET));
          if (aRealBorder.hasAnyBorder ())
            renderBorder (aContentStream, fLeft, fTop, fWidth, fHeight, aRealBorder);
        }

        // Start with the page rectangle
        if (m_aPageHeader != null)
        {
          // Page header does not care about page padding
          // header top-left
          final RenderingContext aRC = new RenderingContext (ERenderingElementType.PAGE_HEADER,
                                                             aContentStream,
                                                             bDebug,
                                                             getMarginLeft () + m_aPageHeader.getMarginAndBorderLeft (),
                                                             m_aPageSize.getHeight () -
                                                                                                                         m_aPageHeader.getMarginAndBorderTop (),
                                                             m_aPageSize.getWidth () -
                                                                                                                                                                 getMarginXSum () -
                                                                                                                                                                 m_aPageHeader.getMarginAndBorderXSum (),
                                                             aPrepareResult.getHeaderHeight () +
                                                                                                                                                                                                          m_aPageHeader.getPaddingYSum ());
          aPageIndex.setPlaceholdersInRenderingContext (aRC);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderingContext (aRC);
          m_aPageHeader.perform (aRC);
        }

        float fCurY = fYTop;
        for (final PLElementWithSize aElementWithHeight : aPerPage)
        {
          final AbstractPLElement <?> aElement = aElementWithHeight.getElement ();
          // Get element height
          final float fThisHeight = aElementWithHeight.getHeight ();
          final float fThisHeightWithPadding = fThisHeight +
                                               aElement.getBorderYSumWidth () +
                                               aElement.getPaddingYSum ();

          final RenderingContext aRC = new RenderingContext (ERenderingElementType.CONTENT_ELEMENT,
                                                             aContentStream,
                                                             bDebug,
                                                             fXLeft + aElement.getMarginAndBorderLeft (),
                                                             fCurY - aElement.getMarginAndBorderTop (),
                                                             getAvailableWidth () - aElement.getMarginAndBorderXSum (),
                                                             fThisHeightWithPadding);
          aPageIndex.setPlaceholdersInRenderingContext (aRC);
          if (m_aRCCustomizer != null)
            m_aRCCustomizer.customizeRenderingContext (aRC);
          aElement.perform (aRC);

          fCurY -= fThisHeightWithPadding + aElement.getMarginYSum ();
        }

        if (m_aPageFooter != null)
        {
          // Page footer does not care about page padding
          // footer top-left
          final float fStartLeft = getMarginLeft () + m_aPageFooter.getMarginAndBorderLeft ();
          final float fStartTop = getMarginBottom () - m_aPageFooter.getMarginAndBorderTop ();
          final float fWidth = m_aPageSize.getWidth () - getMarginXSum () - m_aPageFooter.getMarginAndBorderXSum ();
          final float fHeight = aPrepareResult.getFooterHeight () + m_aPageFooter.getPaddingYSum ();
          final RenderingContext aRC = new RenderingContext (ERenderingElementType.PAGE_FOOTER,
                                                             aContentStream,
                                                             bDebug,
                                                             fStartLeft,
                                                             fStartTop,
                                                             fWidth,
                                                             fHeight);
          aPageIndex.setPlaceholdersInRenderingContext (aRC);
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
