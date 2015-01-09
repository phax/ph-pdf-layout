/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.vendor.VendorInfo;
import com.helger.pdflayout.element.PLPageSet;
import com.helger.pdflayout.element.PLPageSet.PageSetPrepareResult;

public class PageLayoutPDF
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PageLayoutPDF.class);

  private String m_sDocumentAuthor;
  private Calendar m_aDocumentCreationDate;
  private String m_sDocumentCreator;
  private String m_sDocumentTitle;
  private String m_sDocumentKeywords;
  private String m_sDocumentSubject;
  private boolean m_bDebug = false;
  private final List <PLPageSet> m_aPageSets = new ArrayList <PLPageSet> ();

  /**
   * Constructor. Initializes Author, CreationDate and Creator.
   */
  public PageLayoutPDF ()
  {
    m_sDocumentAuthor = VendorInfo.getVendorName () + " " + VendorInfo.getVendorURLWithoutProtocol ();
    m_aDocumentCreationDate = Calendar.getInstance ();
    m_sDocumentCreator = VendorInfo.getVendorName ();
  }

  public boolean isDebug ()
  {
    return m_bDebug;
  }

  @Nonnull
  public PageLayoutPDF setDebug (final boolean bDebug)
  {
    m_bDebug = bDebug;
    return this;
  }

  @Nullable
  public String getDocumentAuthor ()
  {
    return m_sDocumentAuthor;
  }

  public void setDocumentAuthor (@Nullable final String sDocumentAuthor)
  {
    m_sDocumentAuthor = sDocumentAuthor;
  }

  @Nullable
  public Calendar getDocumentCreationDate ()
  {
    return m_aDocumentCreationDate;
  }

  public void setDocumentCreationDate (@Nullable final Calendar aDocumentCreationDate)
  {
    m_aDocumentCreationDate = aDocumentCreationDate;
  }

  @Nullable
  public String getDocumentCreator ()
  {
    return m_sDocumentCreator;
  }

  public void setDocumentCreator (@Nullable final String sDocumentCreator)
  {
    m_sDocumentCreator = sDocumentCreator;
  }

  @Nullable
  public String getDocumentTitle ()
  {
    return m_sDocumentTitle;
  }

  public void setDocumentTitle (@Nullable final String sDocumentTitle)
  {
    m_sDocumentTitle = sDocumentTitle;
  }

  @Nullable
  public String getDocumentKeywords ()
  {
    return m_sDocumentKeywords;
  }

  public void setDocumentKeywords (@Nullable final String sDocumentKeywords)
  {
    m_sDocumentKeywords = sDocumentKeywords;
  }

  @Nullable
  public String getDocumentSubject ()
  {
    return m_sDocumentSubject;
  }

  public void setDocumentSubject (@Nullable final String sDocumentSubject)
  {
    m_sDocumentSubject = sDocumentSubject;
  }

  @Nonnull
  public List <? extends PLPageSet> getAllPageSets ()
  {
    return ContainerHelper.newList (m_aPageSets);
  }

  /**
   * Add a new page set
   *
   * @param aPageSet
   *        The page set to be added. May not be <code>null</code>.
   */
  public void addPageSet (@Nonnull final PLPageSet aPageSet)
  {
    if (aPageSet == null)
      throw new NullPointerException ("PageSet");
    m_aPageSets.add (aPageSet);
  }

  /**
   * Render this layout to an OutputStream.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed automatically.
   * @throws PDFCreationException
   *         In case of an error
   */
  public void renderTo (@Nonnull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    renderTo ((IPDDocumentCustomizer) null, aOS);
  }

  /**
   * Render this layout to an OutputStream.
   *
   * @param aCustomizer
   *        The customizer to be invoked before the document is written to the
   *        stream. May be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed automatically.
   * @throws PDFCreationException
   *         In case of an error
   */
  public void renderTo (@Nullable final IPDDocumentCustomizer aCustomizer, @Nonnull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    // create a new document
    PDDocument aDoc = null;

    try
    {
      aDoc = new PDDocument ();

      // Set document properties
      {
        final PDDocumentInformation aProperties = new PDDocumentInformation ();
        if (StringHelper.hasText (m_sDocumentAuthor))
          aProperties.setAuthor (m_sDocumentAuthor);
        if (m_aDocumentCreationDate != null)
          aProperties.setCreationDate (m_aDocumentCreationDate);
        if (StringHelper.hasText (m_sDocumentCreator))
          aProperties.setCreator (m_sDocumentCreator);
        if (StringHelper.hasText (m_sDocumentTitle))
          aProperties.setTitle (m_sDocumentTitle);
        if (StringHelper.hasText (m_sDocumentKeywords))
          aProperties.setKeywords (m_sDocumentKeywords);
        if (StringHelper.hasText (m_sDocumentSubject))
          aProperties.setSubject (m_sDocumentSubject);
        aProperties.setProducer ("ph-pdf-layout - https://github.com/phax/ph-pdf-layout");
        // add the created properties
        aDoc.setDocumentInformation (aProperties);
      }

      // Prepare all page sets
      final PageSetPrepareResult [] aPRs = new PageSetPrepareResult [m_aPageSets.size ()];
      int nPageSetIndex = 0;
      int nTotalPageCount = 0;
      for (final PLPageSet aPageSet : m_aPageSets)
      {
        final PageSetPrepareResult aPR = aPageSet.prepareAllPages ();
        aPRs[nPageSetIndex] = aPR;
        nTotalPageCount += aPR.getPageCount ();
        nPageSetIndex++;
      }

      // Start applying all page sets
      nPageSetIndex = 0;
      int nTotalPageIndex = 0;
      for (final PLPageSet aPageSet : m_aPageSets)
      {
        final PageSetPrepareResult aPR = aPRs[nPageSetIndex];
        aPageSet.renderAllPages (aPR, aDoc, m_bDebug, nPageSetIndex, nTotalPageIndex, nTotalPageCount);
        // Inc afterwards
        nTotalPageIndex += aPR.getPageCount ();
        nPageSetIndex++;
      }

      // Customize the whole document (optional)
      if (aCustomizer != null)
        aCustomizer.customizeDocument (aDoc);

      // save document to output stream
      aDoc.save (aOS);

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("PDF successfully created");
    }
    catch (final IOException ex)
    {
      throw new PDFCreationException ("IO Error", ex);
    }
    catch (final Throwable t)
    {
      throw new PDFCreationException ("Internal error", t);
    }
    finally
    {
      // close document
      if (aDoc != null)
      {
        try
        {
          aDoc.close ();
        }
        catch (final IOException ex)
        {
          s_aLogger.error ("Failed to close PDF document " + aDoc, ex);
        }
      }

      // Necessary in case of an exception
      StreamUtils.close (aOS);
    }
  }
}
