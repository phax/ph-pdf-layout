/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTConfig;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.serialize.SerializationHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.vendor.VendorInfo;
import com.helger.pdflayout4.base.IPLVisitable;
import com.helger.pdflayout4.base.IPLVisitor;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.base.PLPageSetPrepareResult;
import com.helger.pdflayout4.render.PreparationContextGlobal;

/**
 * Main class for creating layouted PDFs. This class contains the meta data as
 * well as a list of {@link PLPageSet} objects that represent a set of pages
 * with a consistent layouting scheme.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PageLayoutPDF implements IPLVisitable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PageLayoutPDF.class);

  private String m_sDocumentAuthor;
  private LocalDateTime m_aDocumentCreationDate;
  private String m_sDocumentCreator;
  private String m_sDocumentTitle;
  private String m_sDocumentKeywords;
  private String m_sDocumentSubject;
  private boolean m_bCompressPDF = true;
  private final ICommonsList <PLPageSet> m_aPageSets = new CommonsArrayList <> ();

  /**
   * Constructor. Initializes Author, CreationDate and Creator from class
   * {@link VendorInfo}.
   */
  public PageLayoutPDF ()
  {
    m_sDocumentAuthor = VendorInfo.getVendorName () + " " + VendorInfo.getVendorURLWithoutProtocol ();
    m_aDocumentCreationDate = PDTFactory.getCurrentLocalDateTime ();
    m_sDocumentCreator = VendorInfo.getVendorName ();
  }

  /**
   * @return if PDF content should be compressed or not.
   */
  public boolean isCompressPDF ()
  {
    return m_bCompressPDF;
  }

  /**
   * @param bCompressPDF
   *        <code>true</code> to enable creation of compressed PDFs,
   *        <code>false</code> to disable it.
   * @return this for chaining
   */
  @Nonnull
  public PageLayoutPDF setCompressPDF (final boolean bCompressPDF)
  {
    m_bCompressPDF = bCompressPDF;
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
  @Deprecated
  public Calendar getDocumentCreationDate ()
  {
    return TypeConverter.convert (getDocumentCreationDateTime (), Calendar.class);
  }

  @Deprecated
  public void setDocumentCreationDate (@Nullable final Calendar aDocumentCreationDate)
  {
    setDocumentCreationDateTime (TypeConverter.convert (aDocumentCreationDate, LocalDateTime.class));
  }

  @Nullable
  public LocalDateTime getDocumentCreationDateTime ()
  {
    return m_aDocumentCreationDate;
  }

  public void setDocumentCreationDateTime (@Nullable final LocalDateTime aDocumentCreationDate)
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
  @ReturnsMutableCopy
  public ICommonsList <? extends PLPageSet> getAllPageSets ()
  {
    return m_aPageSets.getClone ();
  }

  /**
   * Add a new page set
   *
   * @param aPageSet
   *        The page set to be added. May not be <code>null</code>.
   */
  public void addPageSet (@Nonnull final PLPageSet aPageSet)
  {
    ValueEnforcer.notNull (aPageSet, "PageSet");
    m_aPageSets.add (aPageSet);
  }

  @Nonnull
  public EChange removePageSet (@Nullable final PLPageSet aPageSet)
  {
    return m_aPageSets.removeObject (aPageSet);
  }

  @Nonnull
  public EChange visit (@Nonnull final IPLVisitor aVisitor) throws IOException
  {
    EChange ret = EChange.UNCHANGED;
    for (final PLPageSet aPageSet : m_aPageSets)
      ret = ret.or (aPageSet.visit (aVisitor));
    return ret;
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
  public void renderTo (@Nullable final IPDDocumentCustomizer aCustomizer,
                        @Nonnull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    if (false)
    {
      // For Serialization testing only
      SystemProperties.setPropertyValue ("sun.io.serialization.extendedDebugInfo", true);
      for (final PLPageSet aPageSet : m_aPageSets)
        SerializationHelper.getSerializedByteArray (aPageSet);
    }

    // create a new document
    // Use a buffered OS - approx 30% faster!
    try (final PDDocument aDoc = new PDDocument (); final OutputStream aBufferedOS = StreamHelper.getBuffered (aOS))
    {
      // Small consistency check to avoid creating empty, invalid PDFs
      int nTotalElements = 0;
      for (final PLPageSet aPageSet : m_aPageSets)
        nTotalElements += aPageSet.getElementCount ();
      if (nTotalElements == 0)
        throw new PDFCreationException ("All page sets are empty!");

      // Set document properties
      {
        final PDDocumentInformation aProperties = new PDDocumentInformation ();
        if (StringHelper.hasText (m_sDocumentAuthor))
          aProperties.setAuthor (m_sDocumentAuthor);
        if (m_aDocumentCreationDate != null)
          aProperties.setCreationDate (GregorianCalendar.from (m_aDocumentCreationDate.atZone (PDTConfig.getDefaultZoneId ())));
        if (StringHelper.hasText (m_sDocumentCreator))
          aProperties.setCreator (m_sDocumentCreator);
        if (StringHelper.hasText (m_sDocumentTitle))
          aProperties.setTitle (m_sDocumentTitle);
        if (StringHelper.hasText (m_sDocumentKeywords))
          aProperties.setKeywords (m_sDocumentKeywords);
        if (StringHelper.hasText (m_sDocumentSubject))
          aProperties.setSubject (m_sDocumentSubject);
        aProperties.setProducer (PLConfig.PROJECT_NAME + " " + PLConfig.PROJECT_VERSION + " - " + PLConfig.PROJECT_URL);

        // add the created properties
        aDoc.setDocumentInformation (aProperties);
      }

      // Prepare all page sets
      final PreparationContextGlobal aGlobalPrepareCtx = new PreparationContextGlobal (aDoc);
      final PLPageSetPrepareResult [] aPRs = new PLPageSetPrepareResult [m_aPageSets.size ()];
      int nPageSetIndex = 0;
      int nTotalPageCount = 0;
      for (final PLPageSet aPageSet : m_aPageSets)
      {
        final PLPageSetPrepareResult aPR = aPageSet.prepareAllPages (aGlobalPrepareCtx);
        aPRs[nPageSetIndex] = aPR;
        nTotalPageCount += aPR.getPageCount ();
        nPageSetIndex++;
      }

      // Start applying all page sets - real rendering
      nPageSetIndex = 0;
      final int nPageSetCount = m_aPageSets.size ();
      int nTotalPageIndex = 0;
      for (final PLPageSet aPageSet : m_aPageSets)
      {
        final PLPageSetPrepareResult aPR = aPRs[nPageSetIndex];
        aPageSet.renderAllPages (aPR,
                                 aDoc,
                                 m_bCompressPDF,
                                 nPageSetIndex,
                                 nPageSetCount,
                                 nTotalPageIndex,
                                 nTotalPageCount);
        // Inc afterwards
        nTotalPageIndex += aPR.getPageCount ();
        nPageSetIndex++;
      }

      // Customize the whole document (optional)
      if (aCustomizer != null)
        aCustomizer.customizeDocument (aDoc);

      // save document to output stream
      aDoc.save (aBufferedOS);

      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("PDF successfully created");
    }
    catch (final IOException ex)
    {
      throw new PDFCreationException ("IO Error", ex);
    }
    catch (final Exception ex)
    {
      throw new PDFCreationException ("Internal error", ex);
    }
  }
}
