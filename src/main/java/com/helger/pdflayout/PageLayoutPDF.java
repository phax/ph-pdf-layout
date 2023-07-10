/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
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
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTConfig;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.vendor.VendorInfo;
import com.helger.pdflayout.base.IPLVisitable;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.base.PLPageSetPrepareResult;
import com.helger.pdflayout.render.PreparationContextGlobal;

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
  /**
   * By default certain parts of the created PDFs are compressed, to safe space.
   */
  public static final boolean DEFAULT_COMPRESS_PDF = true;
  /**
   * By default no PDF/A compliant PDF is created.
   */
  public static final boolean DEFAULT_CREATE_PDF_A = false;

  private static final Logger LOGGER = LoggerFactory.getLogger (PageLayoutPDF.class);

  private String m_sDocumentAuthor;
  private LocalDateTime m_aDocumentCreationDate;
  private String m_sDocumentCreator;
  private String m_sDocumentTitle;
  private String m_sDocumentKeywords;
  private String m_sDocumentSubject;
  private boolean m_bCompressPDF = DEFAULT_COMPRESS_PDF;
  private boolean m_bCreatePDF_A = DEFAULT_CREATE_PDF_A;
  private final ICommonsList <PLPageSet> m_aPageSets = new CommonsArrayList <> ();
  private IPDDocumentCustomizer m_aDocumentCustomizer;

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
  public final boolean isCompressPDF ()
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
  public final PageLayoutPDF setCompressPDF (final boolean bCompressPDF)
  {
    m_bCompressPDF = bCompressPDF;
    return this;
  }

  /**
   * @return if PDF/A conformant PDF should be created or not.
   * @since 6.0.3
   */
  public final boolean isCreatePDF_A ()
  {
    return m_bCreatePDF_A;
  }

  /**
   * @param bCreatePDF_A
   *        <code>true</code> to enable creation of PDF/A, <code>false</code> to
   *        disable it.
   * @return this for chaining
   * @since 6.0.3
   */
  @Nonnull
  public final PageLayoutPDF setCreatePDF_A (final boolean bCreatePDF_A)
  {
    m_bCreatePDF_A = bCreatePDF_A;
    return this;
  }

  @Nullable
  public final String getDocumentAuthor ()
  {
    return m_sDocumentAuthor;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentAuthor (@Nullable final String sDocumentAuthor)
  {
    m_sDocumentAuthor = sDocumentAuthor;
    return this;
  }

  @Nullable
  public final LocalDateTime getDocumentCreationDateTime ()
  {
    return m_aDocumentCreationDate;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentCreationDateTime (@Nullable final LocalDateTime aDocumentCreationDate)
  {
    m_aDocumentCreationDate = aDocumentCreationDate;
    return this;
  }

  @Nullable
  public final String getDocumentCreator ()
  {
    return m_sDocumentCreator;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentCreator (@Nullable final String sDocumentCreator)
  {
    m_sDocumentCreator = sDocumentCreator;
    return this;
  }

  @Nullable
  public final String getDocumentTitle ()
  {
    return m_sDocumentTitle;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentTitle (@Nullable final String sDocumentTitle)
  {
    m_sDocumentTitle = sDocumentTitle;
    return this;
  }

  @Nullable
  public final String getDocumentKeywords ()
  {
    return m_sDocumentKeywords;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentKeywords (@Nullable final String sDocumentKeywords)
  {
    m_sDocumentKeywords = sDocumentKeywords;
    return this;
  }

  @Nullable
  public final String getDocumentSubject ()
  {
    return m_sDocumentSubject;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentSubject (@Nullable final String sDocumentSubject)
  {
    m_sDocumentSubject = sDocumentSubject;
    return this;
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
   * @return this for chaining
   */
  @Nonnull
  public PageLayoutPDF addPageSet (@Nonnull final PLPageSet aPageSet)
  {
    ValueEnforcer.notNull (aPageSet, "PageSet");
    m_aPageSets.add (aPageSet);
    return this;
  }

  @Nonnull
  public EChange removePageSet (@Nullable final PLPageSet aPageSet)
  {
    return m_aPageSets.removeObject (aPageSet);
  }

  @Nullable
  public final IPDDocumentCustomizer getDocumentCustomizer ()
  {
    return m_aDocumentCustomizer;
  }

  @Nonnull
  public final PageLayoutPDF setDocumentCustomizer (@Nullable final IPDDocumentCustomizer aDocumentCustomizer)
  {
    m_aDocumentCustomizer = aDocumentCustomizer;
    return this;
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
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   */
  @Nonnull
  public PageLayoutPDF renderTo (@Nonnull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    try (final NonBlockingByteArrayOutputStream aTmpOS = new NonBlockingByteArrayOutputStream ())
    {
      // create a new document
      // Use a buffered OS - approx 30% faster!
      try (final PDDocument aDoc = new PDDocument ();
          final OutputStream aBufferedOS = StreamHelper.getBuffered (m_bCreatePDF_A ? aTmpOS : aOS))
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
          aProperties.setProducer (PLConfig.PROJECT_NAME +
                                   " " +
                                   PLConfig.PROJECT_VERSION +
                                   " - " +
                                   PLConfig.PROJECT_URL);

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
        if (m_aDocumentCustomizer != null)
          m_aDocumentCustomizer.customizeDocument (aDoc);

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

      if (m_bCreatePDF_A)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Start adding PDF/A information");

        // Add metadata (needed by PDF/A)
        try (final NonBlockingByteArrayInputStream pdfInputStream = aTmpOS.getAsInputStream ();
            final PDDocument aDoc = PDDocument.load (pdfInputStream);
            final OutputStream aBufferedOS = StreamHelper.getBuffered (aOS))
        {

          final Calendar aCreationDate = m_aDocumentCreationDate == null ? PDTFactory.createCalendar ()
                                                                         : GregorianCalendar.from (m_aDocumentCreationDate.atZone (PDTConfig.getDefaultZoneId ()));
          final String sProducer = PLConfig.PROJECT_NAME + " " + PLConfig.PROJECT_VERSION;

          final XMPMetadata xmpMetadata = XMPMetadata.createXMPMetadata ();
          final AdobePDFSchema pdfSchema = xmpMetadata.createAndAddAdobePDFSchema ();
          pdfSchema.setProducer (sProducer);

          final XMPBasicSchema xmpBasicSchema = xmpMetadata.createAndAddXMPBasicSchema ();
          xmpBasicSchema.setCreatorTool (sProducer);
          xmpBasicSchema.setCreateDate (aCreationDate);
          xmpBasicSchema.setModifyDate (aCreationDate);

          final PDDocumentCatalog aDocCatalogue = aDoc.getDocumentCatalog ();

          final PDMarkInfo markInfo = new PDMarkInfo ();
          final PDStructureTreeRoot treeRoot = new PDStructureTreeRoot ();
          aDocCatalogue.setMarkInfo (markInfo);
          aDocCatalogue.setStructureTreeRoot (treeRoot);
          aDocCatalogue.getMarkInfo ().setMarked (true);

          final PDDocumentInformation aDocInfo = aDoc.getDocumentInformation ();
          aDocInfo.setCreationDate (aCreationDate);
          aDocInfo.setModificationDate (aCreationDate);
          if (StringHelper.hasText (m_sDocumentAuthor))
            aDocInfo.setAuthor (m_sDocumentAuthor);
          aDocInfo.setProducer (sProducer);
          if (StringHelper.hasText (m_sDocumentCreator))
            aDocInfo.setCreator (m_sDocumentCreator);
          if (StringHelper.hasText (m_sDocumentTitle))
            aDocInfo.setTitle (m_sDocumentTitle);
          if (StringHelper.hasText (m_sDocumentSubject))
            aDocInfo.setSubject (m_sDocumentSubject);

          try
          {
            final DublinCoreSchema dublinCoreSchema = xmpMetadata.createAndAddDublinCoreSchema ();
            if (StringHelper.hasText (m_sDocumentTitle))
              dublinCoreSchema.setTitle (m_sDocumentTitle);
            if (StringHelper.hasText (m_sDocumentCreator))
              dublinCoreSchema.addCreator (m_sDocumentCreator);
            if (StringHelper.hasText (m_sDocumentKeywords))
              dublinCoreSchema.addDescription ("", m_sDocumentKeywords);
            if (StringHelper.hasText (m_sDocumentSubject))
              dublinCoreSchema.addSubject (m_sDocumentSubject);
            dublinCoreSchema.addDate (aCreationDate);

            final PDFAIdentificationSchema aIdentificationSchema = xmpMetadata.createAndAddPDFAIdentificationSchema ();
            aIdentificationSchema.setPart (Integer.valueOf (3));
            aIdentificationSchema.setConformance ("A");

            try (final NonBlockingByteArrayOutputStream aXmpOS = new NonBlockingByteArrayOutputStream ())
            {
              final XmpSerializer aSerializer = new XmpSerializer ();
              aSerializer.serialize (xmpMetadata, aXmpOS, true);

              final PDMetadata aMetadata = new PDMetadata (aDoc);
              aMetadata.importXMPMetadata (aXmpOS.toByteArray ());
              aDocCatalogue.setMetadata (aMetadata);
            }
          }
          catch (final BadFieldValueException ex)
          {
            throw new IllegalArgumentException ("Failed to set PDF Metadata", ex);
          }

          // Set color profile (needed by PDF/A)
          final ICC_Profile rgbProfile = ICC_Profile.getInstance (ColorSpace.CS_sRGB);
          final byte [] aRGBBytes = rgbProfile.getData ();

          try (final NonBlockingByteArrayInputStream colorProfile = new NonBlockingByteArrayInputStream (aRGBBytes))
          {
            final PDOutputIntent intent = new PDOutputIntent (aDoc, colorProfile);
            intent.setInfo ("sRGB IEC61966-2.1");
            intent.setOutputCondition ("sRGB IEC61966-2.1");
            intent.setOutputConditionIdentifier ("sRGB IEC61966-2.1");
            intent.setRegistryName ("http://www.color.org");

            aDocCatalogue.addOutputIntent (intent);
            aDocCatalogue.setLanguage ("de-DE");
          }

          for (final PDPage aPage : aDoc.getPages ())
          {
            final PDViewerPreferences aViewerPrefs = new PDViewerPreferences (aPage.getCOSObject ());
            aViewerPrefs.setDisplayDocTitle (true);
            aDocCatalogue.setViewerPreferences (aViewerPrefs);
          }

          // save document to final output stream
          aDoc.save (aBufferedOS);

          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("PDF with PDF/A successfully created");
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
    } // close aTmpOS

    return this;

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
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   * @deprecated Since 5.1.0; Call
   *             {@link #setDocumentCustomizer(IPDDocumentCustomizer)} and than
   *             {@link #renderTo(OutputStream)}
   */
  @Nonnull
  @Deprecated
  public final PageLayoutPDF renderTo (@Nullable final IPDDocumentCustomizer aCustomizer,
                                       @Nonnull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    setDocumentCustomizer (aCustomizer);
    return renderTo (aOS);
  }

  /**
   * Render this layout to a {@link File}.
   *
   * @param aFile
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed automatically.
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   * @throws IllegalArgumentException
   *         In case the file cannot be opened for writing
   * @since 5.1.0
   */
  @Nonnull
  public PageLayoutPDF renderTo (@Nonnull final File aFile) throws PDFCreationException
  {
    final OutputStream aOS = FileHelper.getOutputStream (aFile);
    if (aOS == null)
      throw new IllegalArgumentException ("Failed to open file '" + aFile.getAbsolutePath () + "' for writing");
    return renderTo (aOS);
  }
}
