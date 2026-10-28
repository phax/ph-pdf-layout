/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.collection.commons.ICommonsSortedSet;
import com.helger.commons.vendor.VendorInfo;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.zone.PDTConfig;
import com.helger.io.file.FileHelper;
import com.helger.pdflayout.base.IPLVisitable;
import com.helger.pdflayout.base.IPLVisitor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.base.PLPageSetPrepareResult;
import com.helger.pdflayout.pdfa.EPdfALevel;
import com.helger.pdflayout.pdfa.PdfAXmpExtensionSchema;
import com.helger.pdflayout.pdfa.PdfAssociatedFile;
import com.helger.pdflayout.render.PreparationContextGlobal;

/**
 * Main class for creating layouted PDFs. This class contains the meta data as well as a list of
 * {@link PLPageSet} objects that represent a set of pages with a consistent layouting scheme.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PageLayoutPDF implements IPLVisitable
{
  public static final String DEFAULT_DOCUMENT_LANGUAGE = "en-US";
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
  private ZonedDateTime m_aDocumentCreationDate;
  private String m_sDocumentCreator;
  private String m_sDocumentTitle;
  private String m_sDocumentKeywords;
  private String m_sDocumentSubject;
  private String m_sDocumentLanguage = DEFAULT_DOCUMENT_LANGUAGE;
  private boolean m_bCompressPDF = DEFAULT_COMPRESS_PDF;
  private EPdfALevel m_ePdfALevel;
  private boolean m_bPdfAStrict = false;
  private final ICommonsList <PLPageSet> m_aPageSets = new CommonsArrayList <> ();
  private int m_nCustomLeadingPageCount = -1;
  private int m_nCustomTrailingPageCount = -1;
  private int m_nCustomTotalPageCount = -1;
  private IPDDocumentCustomizer m_aDocumentCustomizer;
  private IXMPMetadataCustomizer m_aMetadataCustomizer;
  private final ICommonsList <PdfAssociatedFile> m_aAssociatedFiles = new CommonsArrayList <> ();
  private final ICommonsList <PdfAXmpExtensionSchema> m_aXmpExtensionSchemas = new CommonsArrayList <> ();

  /**
   * Constructor. Initializes Author, CreationDate and Creator from class {@link VendorInfo}.
   */
  public PageLayoutPDF ()
  {
    m_sDocumentAuthor = VendorInfo.getVendorName () + " " + VendorInfo.getVendorURL ();
    m_aDocumentCreationDate = PDTFactory.getCurrentZonedDateTime ();
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
   *        <code>true</code> to enable creation of compressed PDFs, <code>false</code> to disable
   *        it.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setCompressPDF (final boolean bCompressPDF)
  {
    m_bCompressPDF = bCompressPDF;
    return this;
  }

  /**
   * @return if PDF/A conformant PDF should be created or not.
   * @since 6.0.3
   * @see #getPdfALevel()
   */
  public final boolean isCreatePDF_A ()
  {
    return m_ePdfALevel != null;
  }

  /**
   * Enable or disable the creation of PDF/A. When enabled, the PDF declares PDF/A-3a, as in
   * previous versions. Unlike {@link #setPdfALevel(EPdfALevel)}, this method does not reject fonts
   * that are not embedded.
   *
   * @param bCreatePDF_A
   *        <code>true</code> to enable creation of PDF/A, <code>false</code> to disable it.
   * @return this for chaining
   * @since 6.0.3
   * @see #setPdfALevel(EPdfALevel)
   */
  @NonNull
  public final PageLayoutPDF setCreatePDF_A (final boolean bCreatePDF_A)
  {
    m_ePdfALevel = bCreatePDF_A ? EPdfALevel.PDF_A_3A : null;
    m_bPdfAStrict = false;
    return this;
  }

  /**
   * @return The PDF/A level to be declared. <code>null</code> if no PDF/A is created.
   * @since 8.4.0
   */
  @Nullable
  public final EPdfALevel getPdfALevel ()
  {
    return m_ePdfALevel;
  }

  /**
   * Set the PDF/A level to be declared. When a level is set, rendering fails if the document uses
   * a font that is not embedded (e.g. one of the standard 14 fonts like {@link
   * com.helger.pdflayout.spec.PreloadFont#REGULAR}), because PDF/A requires all fonts to be
   * embedded.
   *
   * @param ePdfALevel
   *        The PDF/A level to use. May be <code>null</code> to disable the creation of PDF/A.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final PageLayoutPDF setPdfALevel (@Nullable final EPdfALevel ePdfALevel)
  {
    m_ePdfALevel = ePdfALevel;
    m_bPdfAStrict = ePdfALevel != null;
    return this;
  }

  /**
   * @return The document author for the metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentAuthor ()
  {
    return m_sDocumentAuthor;
  }

  /**
   * Set the document author for the metadata
   *
   * @param sDocumentAuthor
   *        The author to set. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentAuthor (@Nullable final String sDocumentAuthor)
  {
    m_sDocumentAuthor = sDocumentAuthor;
    return this;
  }

  /**
   * @return The document creation date time for the metadata. May be <code>null</code>.
   */
  @Nullable
  public final LocalDateTime getDocumentCreationDateTime ()
  {
    return m_aDocumentCreationDate == null ? null : m_aDocumentCreationDate.toLocalDateTime ();
  }

  /**
   * @return The document creation date time for the metadata. May be <code>null</code>.
   * @since v7.4.0
   */
  @Nullable
  public final ZonedDateTime getDocumentCreationZonedDateTime ()
  {
    return m_aDocumentCreationDate;
  }

  /**
   * @param aDocumentCreationDate
   *        The document creation date to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentCreationDateTime (@Nullable final LocalDateTime aDocumentCreationDate)
  {
    return setDocumentCreationDateTime (aDocumentCreationDate == null ? null
                                                                      : aDocumentCreationDate.atZone (PDTConfig.getDefaultZoneId ()));
  }

  /**
   * @param aDocumentCreationDate
   *        The document creation date to use. May be <code>null</code>.
   * @return this for chaining
   * @since v7.4.0
   */
  @NonNull
  public final PageLayoutPDF setDocumentCreationDateTime (@Nullable final ZonedDateTime aDocumentCreationDate)
  {
    m_aDocumentCreationDate = aDocumentCreationDate;
    return this;
  }

  /**
   * @return The document creator for the metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentCreator ()
  {
    return m_sDocumentCreator;
  }

  /**
   * Set the document creator metadata
   *
   * @param sDocumentCreator
   *        The document creator. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentCreator (@Nullable final String sDocumentCreator)
  {
    m_sDocumentCreator = sDocumentCreator;
    return this;
  }

  /**
   * @return The document title for metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentTitle ()
  {
    return m_sDocumentTitle;
  }

  /**
   * Set the document title for metadata
   *
   * @param sDocumentTitle
   *        The document title to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentTitle (@Nullable final String sDocumentTitle)
  {
    m_sDocumentTitle = sDocumentTitle;
    return this;
  }

  /**
   * @return The document keywords for metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentKeywords ()
  {
    return m_sDocumentKeywords;
  }

  /**
   * Set the document keywords for metadata
   *
   * @param sDocumentKeywords
   *        The document keywords to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentKeywords (@Nullable final String sDocumentKeywords)
  {
    m_sDocumentKeywords = sDocumentKeywords;
    return this;
  }

  /**
   * @return The document subject for metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentSubject ()
  {
    return m_sDocumentSubject;
  }

  /**
   * Set the document subject for metadata
   *
   * @param sDocumentSubject
   *        The document subject to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentSubject (@Nullable final String sDocumentSubject)
  {
    m_sDocumentSubject = sDocumentSubject;
    return this;
  }

  /**
   * @return The document language for metadata. May be <code>null</code>.
   */
  @Nullable
  public final String getDocumentLanguage ()
  {
    return m_sDocumentLanguage;
  }

  /**
   * Set the document language for metadata
   *
   * @param sDocumentLanguage
   *        The document language to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentLanguage (@Nullable final String sDocumentLanguage)
  {
    m_sDocumentLanguage = sDocumentLanguage;
    return this;
  }

  /**
   * @return A clone of all contained page sets. Never <code>null</code> but maybe empty.
   */
  @NonNull
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
  @NonNull
  public PageLayoutPDF addPageSet (@NonNull final PLPageSet aPageSet)
  {
    ValueEnforcer.notNull (aPageSet, "PageSet");
    m_aPageSets.add (aPageSet);
    return this;
  }

  /**
   * Remove a specific page set again.
   *
   * @param aPageSet
   *        The page set to remove. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if it was removed, {@link EChange#UNCHANGED} otherwise. Never
   *         <code>null</code>.
   */
  @NonNull
  public EChange removePageSet (@Nullable final PLPageSet aPageSet)
  {
    return m_aPageSets.removeObject (aPageSet);
  }

  /**
   * Get the custom leading page count to be used. This can be helpful if it is known, that pages
   * are prepended to the final PDF. The default is 0.
   *
   * @return The custom leading page count. Only values &ge; 0 are considered.
   * @see #getCustomTrailingPageCount()
   * @since 7.4.2
   */
  @CheckForSigned
  public final int getCustomLeadingPageCount ()
  {
    return m_nCustomLeadingPageCount;
  }

  /**
   * Set the custom leading page count to be used. This can be helpful if it is known, that pages
   * are prepended to the final PDF. The default is 0.
   *
   * @param nCustomLeadingPageCount
   *        The custom leading page count. Only values &ge; 0 are considered.
   * @return this for chaining
   * @see #setCustomTrailingPageCount(int)
   * @since 7.4.2
   */
  @NonNull
  public final PageLayoutPDF setCustomLeadingPageCount (final int nCustomLeadingPageCount)
  {
    if (m_nCustomTotalPageCount > 0)
      LOGGER.warn ("Don't mix 'Custom total page count' with 'Custom leading page count'");

    m_nCustomLeadingPageCount = nCustomLeadingPageCount;
    return this;
  }

  /**
   * Get the custom trailing page count to be used. This can be helpful if it is known, that pages
   * are appended to the final PDF. The default is 0.
   *
   * @return The custom trailing page count. Only values &ge; 0 are considered.
   * @see #getCustomLeadingPageCount()
   * @since 7.4.2
   */
  @CheckForSigned
  public final int getCustomTrailingPageCount ()
  {
    return m_nCustomTrailingPageCount;
  }

  /**
   * Set the custom trailing page count to be used. This can be helpful if it is known, that pages
   * are appended to the final PDF. The default is 0.
   *
   * @param nCustomTrailingPageCount
   *        The custom trailing page count. Only values &ge; 0 are considered.
   * @return this for chaining
   * @see #setCustomTrailingPageCount(int)
   * @since 7.4.2
   */
  @NonNull
  public final PageLayoutPDF setCustomTrailingPageCount (final int nCustomTrailingPageCount)
  {
    if (m_nCustomTotalPageCount > 0)
      LOGGER.warn ("Don't mix 'Custom total page count' with 'Custom trailing page count'");

    m_nCustomTrailingPageCount = nCustomTrailingPageCount;
    return this;
  }

  /**
   * Get the custom total page count to be used. This can be helpful if it is known, that pages are
   * prepended or appended to the final PDF. By default this value is calculated automatically.
   *
   * @return The custom page count offset. Only values &gt; 0 are considered.
   * @since 7.4.2
   */
  @CheckForSigned
  public final int getCustomTotalPageCount ()
  {
    return m_nCustomTotalPageCount;
  }

  /**
   * Set the custom total page count to be used. This can be helpful if it is known, that pages are
   * prepended or appended to the final PDF. By default this value is calculated automatically.
   * Don't mix this method with {@link #setCustomLeadingPageCount(int)} and
   * {@link #setCustomTrailingPageCount(int)} as they are contradicting - use either or.
   *
   * @param nCustomTotalPageCount
   *        The custom total page count. Only values &gt; 0 are considered.
   * @return this for chaining
   * @since 7.4.2
   */
  @NonNull
  public final PageLayoutPDF setCustomTotalPageCount (final int nCustomTotalPageCount)
  {
    if (m_nCustomLeadingPageCount > 0 || m_nCustomTrailingPageCount > 0)
      LOGGER.warn ("Don't mix 'Custom total page count' with 'Custom leading|trailing page count'");

    m_nCustomTotalPageCount = nCustomTotalPageCount;
    return this;
  }

  /**
   * @return The document customizer to use. May be <code>null</code>.
   */
  @Nullable
  public final IPDDocumentCustomizer getDocumentCustomizer ()
  {
    return m_aDocumentCustomizer;
  }

  /**
   * Set the overall document customizer to use.
   *
   * @param aDocumentCustomizer
   *        The customizer to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setDocumentCustomizer (@Nullable final IPDDocumentCustomizer aDocumentCustomizer)
  {
    m_aDocumentCustomizer = aDocumentCustomizer;
    return this;
  }

  /**
   * @return The metadata customizer to use. May be <code>null</code>.
   */
  @Nullable
  public final IXMPMetadataCustomizer getMetadataCustomizer ()
  {
    return m_aMetadataCustomizer;
  }

  /**
   * Set the overall metadata customizer to use.
   *
   * @param aMetadataCustomizer
   *        The customizer to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final PageLayoutPDF setMetadataCustomizer (@Nullable final IXMPMetadataCustomizer aMetadataCustomizer)
  {
    m_aMetadataCustomizer = aMetadataCustomizer;
    return this;
  }

  /**
   * @return A copy of all associated files to be embedded. Never <code>null</code> but maybe empty.
   * @since 8.4.0
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsList <PdfAssociatedFile> getAllAssociatedFiles ()
  {
    return m_aAssociatedFiles.getClone ();
  }

  /**
   * Add a file that is embedded into the PDF and associated with the whole document. Arbitrary
   * embedded files are allowed in PDF/A-3 but not in PDF/A-2 - rendering a PDF/A-2 document with
   * associated files fails.
   *
   * @param aAssociatedFile
   *        The file to embed. May not be <code>null</code>. The file name must be unique within this
   *        document.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final PageLayoutPDF addAssociatedFile (@NonNull final PdfAssociatedFile aAssociatedFile)
  {
    ValueEnforcer.notNull (aAssociatedFile, "AssociatedFile");
    if (m_aAssociatedFiles.containsAny (x -> x.getFileName ().equals (aAssociatedFile.getFileName ())))
      throw new IllegalArgumentException ("An associated file with the name '" +
                                          aAssociatedFile.getFileName () +
                                          "' is already contained");
    m_aAssociatedFiles.add (aAssociatedFile);
    return this;
  }

  /**
   * @return A copy of all XMP extension schemas. Never <code>null</code> but maybe empty.
   * @since 8.4.0
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsList <PdfAXmpExtensionSchema> getAllXmpExtensionSchemas ()
  {
    return m_aXmpExtensionSchemas.getClone ();
  }

  /**
   * Add a custom XMP schema together with its PDF/A extension schema description. The XMP metadata
   * is only written when PDF/A is created (see {@link #setPdfALevel(EPdfALevel)}). The schemas are
   * added before the {@link IXMPMetadataCustomizer} is invoked.
   *
   * @param aSchema
   *        The schema to add. May not be <code>null</code>.
   * @return this for chaining
   * @since 8.4.0
   */
  @NonNull
  public final PageLayoutPDF addXmpExtensionSchema (@NonNull final PdfAXmpExtensionSchema aSchema)
  {
    ValueEnforcer.notNull (aSchema, "Schema");
    m_aXmpExtensionSchemas.add (aSchema);
    return this;
  }

  @NonNull
  public EChange visit (@NonNull final IPLVisitor aVisitor) throws IOException
  {
    EChange ret = EChange.UNCHANGED;
    for (final PLPageSet aPageSet : m_aPageSets)
      ret = ret.or (aPageSet.visit (aVisitor));
    return ret;
  }

  /**
   * Explicitly prepare all available page sets. That means that the page sets cannot be modified
   * again, but the content sizes can be determined.
   *
   * @since 7.3.1
   */
  public void prepareAllPageSets ()
  {
    // Dummy document
    try (final PDDocument aDoc = new PDDocument ())
    {
      // Global context
      final PreparationContextGlobal aGlobalPrepareCtx = new PreparationContextGlobal (aDoc);
      // Through all page sets
      for (final PLPageSet aPageSet : m_aPageSets)
      {
        aPageSet.prepareAllPages (aGlobalPrepareCtx);
      }
    }
    catch (final IOException ex)
    {
      LOGGER.error ("Failed to prepare page sets", ex);
    }
  }

  /**
   * PDF/A requires all fonts that are used for rendering to be embedded. Fail early with a clear
   * message instead of creating a PDF that only claims to be PDF/A. Fonts that are only listed in
   * the page resources but never selected with the <code>Tf</code> operator are ignored, as in
   * PDF/A.
   */
  private static void _checkFontsEmbedded (@NonNull final PDDocument aDoc) throws IOException, PDFCreationException
  {
    final ICommonsSortedSet <String> aNotEmbedded = new CommonsTreeSet <> ();
    for (final PDPage aPage : aDoc.getPages ())
    {
      final PDResources aResources = aPage.getResources ();
      if (aResources != null && aPage.hasContents ())
      {
        final List <Object> aTokens = new PDFStreamParser (aPage).parse ();
        for (int i = 2; i < aTokens.size (); ++i)
          if (aTokens.get (i) instanceof final Operator aOperator &&
              OperatorName.SET_FONT_AND_SIZE.equals (aOperator.getName ()) &&
              aTokens.get (i - 2) instanceof final COSName aFontName)
          {
            final PDFont aFont = aResources.getFont (aFontName);
            if (aFont != null && !aFont.isEmbedded ())
              aNotEmbedded.add (aFont.getName ());
          }
      }
    }
    if (aNotEmbedded.isNotEmpty ())
      throw new PDFCreationException ("PDF/A requires all fonts to be embedded, but the following fonts are not embedded: " +
                                      aNotEmbedded +
                                      ". Use an embedded font (e.g. PreloadFont.createEmbedding) instead of the standard 14 fonts.");
  }

  /**
   * Embed all associated files and reference them from the <code>/AF</code> array of the document
   * catalogue.
   */
  private void _embedAssociatedFiles (@NonNull final PDDocument aDoc, @NonNull final Calendar aDate) throws IOException
  {
    final ICommonsSortedMap <String, PDComplexFileSpecification> aNames = new CommonsTreeMap <> ();
    final COSArray aAF = new COSArray ();
    for (final PdfAssociatedFile aFile : m_aAssociatedFiles)
    {
      final PDEmbeddedFile aEmbeddedFile;
      try (final InputStream aIS = new NonBlockingByteArrayInputStream (aFile.getContent ()))
      {
        aEmbeddedFile = new PDEmbeddedFile (aDoc, aIS, COSName.FLATE_DECODE);
      }
      aEmbeddedFile.setSubtype (aFile.getMimeType ());
      aEmbeddedFile.setSize (aFile.getContent ().length);
      aEmbeddedFile.setCreationDate (aDate);
      aEmbeddedFile.setModDate (aDate);

      final PDComplexFileSpecification aFileSpec = new PDComplexFileSpecification ();
      aFileSpec.setFile (aFile.getFileName ());
      aFileSpec.setFileUnicode (aFile.getFileName ());
      aFileSpec.setEmbeddedFile (aEmbeddedFile);
      aFileSpec.setEmbeddedFileUnicode (aEmbeddedFile);
      if (StringHelper.isNotEmpty (aFile.getDescription ()))
        aFileSpec.setFileDescription (aFile.getDescription ());
      aFileSpec.getCOSObject ().setName (COSName.AF_RELATIONSHIP, aFile.getRelationship ().getID ());

      aNames.put (aFile.getFileName (), aFileSpec);
      aAF.add (aFileSpec);
    }

    final PDEmbeddedFilesNameTreeNode aTree = new PDEmbeddedFilesNameTreeNode ();
    aTree.setNames (aNames);

    final PDDocumentCatalog aCatalogue = aDoc.getDocumentCatalog ();
    PDDocumentNameDictionary aNameDict = aCatalogue.getNames ();
    if (aNameDict == null)
      aNameDict = new PDDocumentNameDictionary (aCatalogue);
    aNameDict.setEmbeddedFiles (aTree);
    aCatalogue.setNames (aNameDict);
    aCatalogue.getCOSObject ().setItem (COSName.AF, aAF);
  }

  /**
   * PDF/A requires every annotation except popups to have the flags entry with the "print" flag set.
   */
  private static void _setAnnotationsPrinted (@NonNull final PDDocument aDoc) throws IOException
  {
    for (final PDPage aPage : aDoc.getPages ())
      for (final PDAnnotation aAnnotation : aPage.getAnnotations ())
        if (!(aAnnotation instanceof PDAnnotationPopup))
          aAnnotation.setPrinted (true);
  }

  /**
   * Render this layout to an OutputStream.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is closed automatically
   *        internally. To avoid closing the {@link OutputStream} you may consider wrapping it in a
   *        {@link com.helger.base.io.stream.NonClosingOutputStream} - just a hint.
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   */
  @NonNull
  public PageLayoutPDF renderTo (@NonNull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    if (m_ePdfALevel != null && !m_ePdfALevel.isArbitraryEmbeddedFilesAllowed () && m_aAssociatedFiles.isNotEmpty ())
    {
      StreamHelper.close (aOS);
      throw new PDFCreationException ("PDF/A level '" +
                                      m_ePdfALevel.getID () +
                                      "' does not allow arbitrary embedded files - use PDF/A-3 instead");
    }

    final Calendar aCreationDate = m_aDocumentCreationDate == null ? PDTFactory.createCalendar ()
                                                                   : GregorianCalendar.from (m_aDocumentCreationDate);

    try (final NonBlockingByteArrayOutputStream aTmpOS = new NonBlockingByteArrayOutputStream ())
    {
      // create a new document
      // Use a buffered OS - approx 30% faster!
      try (final PDDocument aDoc = new PDDocument ();
           final OutputStream aBufferedOS = StreamHelper.getBuffered (m_ePdfALevel != null ? aTmpOS : aOS))
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
          if (StringHelper.isNotEmpty (m_sDocumentAuthor))
            aProperties.setAuthor (m_sDocumentAuthor);
          if (m_aDocumentCreationDate != null)
            aProperties.setCreationDate (GregorianCalendar.from (m_aDocumentCreationDate));
          if (StringHelper.isNotEmpty (m_sDocumentCreator))
            aProperties.setCreator (m_sDocumentCreator);
          if (StringHelper.isNotEmpty (m_sDocumentTitle))
            aProperties.setTitle (m_sDocumentTitle);
          if (StringHelper.isNotEmpty (m_sDocumentKeywords))
            aProperties.setKeywords (m_sDocumentKeywords);
          if (StringHelper.isNotEmpty (m_sDocumentSubject))
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
        // Eventually start at the custom offset
        int nTotalPageCount = m_nCustomLeadingPageCount > 0 ? m_nCustomLeadingPageCount : 0;
        for (final PLPageSet aPageSet : m_aPageSets)
        {
          final PLPageSetPrepareResult aPR;

          // Handle pre prepared page sets
          if (aPageSet.isPrepared ())
            aPR = aPageSet.internalGetPrepareResult ();
          else
            aPR = aPageSet.prepareAllPages (aGlobalPrepareCtx);
          aPRs[nPageSetIndex] = aPR;
          nTotalPageCount += aPR.getPageCount ();
          nPageSetIndex++;
        }
        // Add the custom trailing page count to the total pages
        if (m_nCustomTrailingPageCount > 0)
          nTotalPageCount += m_nCustomTrailingPageCount;

        // Use the custom overall page count if applicable
        if (m_nCustomTotalPageCount > 0)
          nTotalPageCount = m_nCustomTotalPageCount;

        // Render all page sets
        nPageSetIndex = 0;
        final int nPageSetCount = m_aPageSets.size ();
        // Eventually start at the custom offset
        int nTotalPageIndex = m_nCustomLeadingPageCount > 0 ? m_nCustomLeadingPageCount : 0;
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
          // Increment afterwards
          nTotalPageIndex += aPR.getPageCount ();
          nPageSetIndex++;
        }

        // Embed the associated files (optional)
        if (m_aAssociatedFiles.isNotEmpty ())
          _embedAssociatedFiles (aDoc, aCreationDate);

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
        throw new PDFCreationException ("IO Error writing PDF", ex);
      }
      catch (final Exception ex)
      {
        throw new PDFCreationException ("Internal error rendering PDF", ex);
      }

      // Do specific PDF/A stuff if needed
      if (m_ePdfALevel != null)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Start adding PDF/A information");

        // Add metadata (needed by PDF/A)
        try (final PDDocument aDoc = Loader.loadPDF (aTmpOS.getBufferOrCopy ());
             final OutputStream aBufferedOS = StreamHelper.getBuffered (aOS))
        {
          final String sProducer = PLConfig.PROJECT_NAME + " " + PLConfig.PROJECT_VERSION;

          final XMPMetadata aXmpMetadata = XMPMetadata.createXMPMetadata ();
          final AdobePDFSchema aPDFSchema = aXmpMetadata.createAndAddAdobePDFSchema ();
          aPDFSchema.setProducer (sProducer);

          final XMPBasicSchema aXmpBasicSchema = aXmpMetadata.createAndAddXMPBasicSchema ();
          aXmpBasicSchema.setCreatorTool (sProducer);
          aXmpBasicSchema.setCreateDate (aCreationDate);
          aXmpBasicSchema.setModifyDate (aCreationDate);

          final PDDocumentCatalog aDocCatalogue = aDoc.getDocumentCatalog ();

          if (m_bPdfAStrict)
            _checkFontsEmbedded (aDoc);

          if (m_ePdfALevel.isTagged ())
          {
            final PDMarkInfo aMarkInfo = new PDMarkInfo ();
            final PDStructureTreeRoot aTreeRoot = new PDStructureTreeRoot ();
            aDocCatalogue.setMarkInfo (aMarkInfo);
            aDocCatalogue.setStructureTreeRoot (aTreeRoot);
            aDocCatalogue.getMarkInfo ().setMarked (true);
          }

          // PDF/A requires the annotation flags with the "print" flag set
          _setAnnotationsPrinted (aDoc);

          final PDDocumentInformation aDocInfo = aDoc.getDocumentInformation ();
          aDocInfo.setCreationDate (aCreationDate);
          aDocInfo.setModificationDate (aCreationDate);
          if (StringHelper.isNotEmpty (m_sDocumentAuthor))
            aDocInfo.setAuthor (m_sDocumentAuthor);
          aDocInfo.setProducer (sProducer);
          if (StringHelper.isNotEmpty (m_sDocumentCreator))
            aDocInfo.setCreator (m_sDocumentCreator);
          if (StringHelper.isNotEmpty (m_sDocumentTitle))
            aDocInfo.setTitle (m_sDocumentTitle);
          if (StringHelper.isNotEmpty (m_sDocumentSubject))
            aDocInfo.setSubject (m_sDocumentSubject);

          try
          {
            final DublinCoreSchema aDCSchema = aXmpMetadata.createAndAddDublinCoreSchema ();
            if (StringHelper.isNotEmpty (m_sDocumentTitle))
              aDCSchema.setTitle (m_sDocumentTitle);
            if (StringHelper.isNotEmpty (m_sDocumentCreator))
              aDCSchema.addCreator (m_sDocumentCreator);
            if (StringHelper.isNotEmpty (m_sDocumentKeywords))
              aDCSchema.addDescription ("", m_sDocumentKeywords);
            if (StringHelper.isNotEmpty (m_sDocumentSubject))
              aDCSchema.addSubject (m_sDocumentSubject);
            aDCSchema.addDate (aCreationDate);

            final PDFAIdentificationSchema aIdentificationSchema = aXmpMetadata.createAndAddPDFAIdentificationSchema ();
            aIdentificationSchema.setPart (Integer.valueOf (m_ePdfALevel.getPart ()));
            aIdentificationSchema.setConformance (m_ePdfALevel.getConformance ());

            for (final PdfAXmpExtensionSchema aSchema : m_aXmpExtensionSchemas)
              aSchema.addToMetadata (aXmpMetadata);

            if (m_aMetadataCustomizer != null)
              m_aMetadataCustomizer.customizeMetadata (aXmpMetadata);

            try (final NonBlockingByteArrayOutputStream aXmpOS = new NonBlockingByteArrayOutputStream ())
            {
              final XmpSerializer aSerializer = new XmpSerializer ();
              aSerializer.serialize (aXmpMetadata, aXmpOS, true);

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
          final ICC_Profile aRgbProfile = ICC_Profile.getInstance (ColorSpace.CS_sRGB);
          final byte [] aRgbBytes = aRgbProfile.getData ();

          try (final NonBlockingByteArrayInputStream aColorProfile = new NonBlockingByteArrayInputStream (aRgbBytes))
          {
            final PDOutputIntent aIntent = new PDOutputIntent (aDoc, aColorProfile);
            aIntent.setInfo ("sRGB IEC61966-2.1");
            aIntent.setOutputCondition ("sRGB IEC61966-2.1");
            aIntent.setOutputConditionIdentifier ("sRGB IEC61966-2.1");
            aIntent.setRegistryName ("http://www.color.org");

            aDocCatalogue.addOutputIntent (aIntent);
          }

          if (StringHelper.isNotEmpty (m_sDocumentLanguage))
            aDocCatalogue.setLanguage (m_sDocumentLanguage);

          final PDViewerPreferences aViewerPrefs = new PDViewerPreferences (new COSDictionary ());
          aViewerPrefs.setDisplayDocTitle (true);
          aDocCatalogue.setViewerPreferences (aViewerPrefs);

          // save document to final output stream
          aDoc.save (aBufferedOS);

          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("PDF with PDF/A successfully created");
        }
        catch (final PDFCreationException ex)
        {
          throw ex;
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
    finally
    {
      // Make sure the source is closed as well
      StreamHelper.close (aOS);
    }

    return this;
  }

  /**
   * Render this layout to an OutputStream.
   *
   * @param aCustomizer
   *        The customizer to be invoked before the document is written to the stream. May be
   *        <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is closed automatically.
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   * @deprecated Since 5.1.0; Call {@link #setDocumentCustomizer(IPDDocumentCustomizer)} and than
   *             {@link #renderTo(OutputStream)}
   */
  @NonNull
  @Deprecated
  public final PageLayoutPDF renderTo (@Nullable final IPDDocumentCustomizer aCustomizer,
                                       @NonNull @WillClose final OutputStream aOS) throws PDFCreationException
  {
    setDocumentCustomizer (aCustomizer);
    return renderTo (aOS);
  }

  /**
   * Render this layout to a {@link File}.
   *
   * @param aFile
   *        The output stream to write to. May not be <code>null</code>. Is closed automatically.
   * @return this for chaining
   * @throws PDFCreationException
   *         In case of an error
   * @throws IllegalArgumentException
   *         In case the file cannot be opened for writing
   * @since 5.1.0
   */
  @NonNull
  public PageLayoutPDF renderTo (@NonNull final File aFile) throws PDFCreationException
  {
    final OutputStream aOS = FileHelper.getOutputStream (aFile);
    if (aOS == null)
      throw new IllegalArgumentException ("Failed to open file '" + aFile.getAbsolutePath () + "' for writing");
    return renderTo (aOS);
  }
}
