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
package com.helger.pdflayout.tagging;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDParentTreeValue;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDTableAttributeObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSortedMap;

/**
 * The state of a tagged PDF while it is rendered: the structure tree, the structure element that is
 * currently open, the marked content IDs of the current page and the parent tree. One instance is
 * used for the whole document. It is created by {@link com.helger.pdflayout.PageLayoutPDF} when a
 * tagged PDF is created - elements reach it via
 * {@link com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache#getTagging()}.
 * <p>
 * Content is tagged at the drawing level: text objects and images become marked content of the
 * innermost open structure element. Everything else (fills, borders, lines) and everything drawn
 * while an artifact is open is an artifact.
 * </p>
 *
 * @author Philip Helger
 * @since 8.4.0
 */
@NotThreadSafe
public final class PLTaggingContext
{
  private final PDStructureTreeRoot m_aRoot;
  private final PDStructureElement m_aDocumentElement;
  // The structure elements by the original ID of the element, so that the fragments of a split
  // element continue the same structure element
  private final ICommonsMap <String, PDStructureElement> m_aElements = new CommonsHashMap <> ();
  private final ICommonsList <PDStructureElement> m_aStack = new CommonsArrayList <> ();
  private final ICommonsSortedMap <Integer, PDParentTreeValue> m_aParentTree = new CommonsTreeMap <> ();
  private int m_nNextParentTreeKey = 0;
  private int m_nArtifactDepth = 0;
  private int m_nHeaderRowDepth = 0;
  private PDPage m_aCurrentPage;
  private COSArray m_aCurrentPageMCIDs;

  /**
   * Constructor
   *
   * @param sLanguage
   *        The natural language of the document, e.g. <code>en-US</code>. Set on the document
   *        structure element. May be <code>null</code>.
   */
  public PLTaggingContext (@Nullable final String sLanguage)
  {
    m_aRoot = new PDStructureTreeRoot ();
    m_aDocumentElement = new PDStructureElement (EPLStructureRole.DOCUMENT.getID (), m_aRoot);
    if (StringHelper.isNotEmpty (sLanguage))
      m_aDocumentElement.setLanguage (sLanguage);
    m_aRoot.appendKid (m_aDocumentElement);
  }

  @NonNull
  private PDStructureElement _getCurrentElement ()
  {
    return m_aStack.isEmpty () ? m_aDocumentElement : m_aStack.getLastOrNull ();
  }

  /**
   * @return The structure tree root. Never <code>null</code>.
   */
  @NonNull
  public PDStructureTreeRoot getStructureTreeRoot ()
  {
    return m_aRoot;
  }

  /**
   * @return The document structure element - the only child of the root. Never <code>null</code>.
   */
  @NonNull
  public PDStructureElement getDocumentElement ()
  {
    return m_aDocumentElement;
  }

  /**
   * Start a new page. All marked content until the next call belongs to this page.
   *
   * @param aPage
   *        The new page. May not be <code>null</code>.
   */
  public void startPage (@NonNull final PDPage aPage)
  {
    ValueEnforcer.notNull (aPage, "Page");
    final int nKey = m_nNextParentTreeKey++;
    aPage.setStructParents (nKey);
    // Tab order follows the structure
    aPage.getCOSObject ().setName (COSName.getPDFName ("Tabs"), "S");
    m_aCurrentPage = aPage;
    m_aCurrentPageMCIDs = new COSArray ();
    m_aParentTree.put (Integer.valueOf (nKey), new PDParentTreeValue (m_aCurrentPageMCIDs));
  }

  /**
   * @return <code>true</code> if an artifact is open, so that all content is an artifact and no
   *         structure elements are created.
   */
  public boolean isInArtifact ()
  {
    return m_nArtifactDepth > 0;
  }

  /**
   * Open an artifact: all content until the matching {@link #endArtifact()} is an artifact. Calls
   * may be nested.
   */
  public void beginArtifact ()
  {
    m_nArtifactDepth++;
  }

  /**
   * Close an artifact opened with {@link #beginArtifact()}.
   */
  public void endArtifact ()
  {
    if (m_nArtifactDepth <= 0)
      throw new IllegalStateException ("No artifact is open");
    m_nArtifactDepth--;
  }

  /**
   * Open a table header row: until the matching {@link #endHeaderRow()}, cells with the role
   * {@link EPLStructureRole#TD} become {@link EPLStructureRole#TH}.
   */
  public void beginHeaderRow ()
  {
    m_nHeaderRowDepth++;
  }

  /**
   * Close a table header row opened with {@link #beginHeaderRow()}.
   */
  public void endHeaderRow ()
  {
    if (m_nHeaderRowDepth <= 0)
      throw new IllegalStateException ("No header row is open");
    m_nHeaderRowDepth--;
  }

  /**
   * Open a structure element as the child of the innermost open one. Nothing happens while an
   * artifact is open.
   *
   * @param sKey
   *        The key of the element - the original ID of the layout element, which is the same for
   *        all fragments of a split element. May neither be <code>null</code> nor empty.
   * @param bContinue
   *        <code>true</code> to continue the structure element of an earlier fragment with the same
   *        key, if there is one. <code>false</code> to always create a new structure element.
   * @param eRole
   *        The role of the element. May not be <code>null</code>.
   * @param sAlternateText
   *        The alternate text, e.g. of a figure. May be <code>null</code>.
   * @return <code>true</code> if the element was opened and {@link #endElement()} must be called,
   *         <code>false</code> if not.
   */
  public boolean beginElement (@NonNull @Nonempty final String sKey,
                               final boolean bContinue,
                               @NonNull final EPLStructureRole eRole,
                               @Nullable final String sAlternateText)
  {
    ValueEnforcer.notEmpty (sKey, "Key");
    ValueEnforcer.notNull (eRole, "Role");
    if (isInArtifact ())
      return false;

    PDStructureElement aElement = bContinue ? m_aElements.get (sKey) : null;
    if (aElement == null)
    {
      final EPLStructureRole eRealRole = eRole == EPLStructureRole.TD && m_nHeaderRowDepth > 0 ? EPLStructureRole.TH
                                                                                                : eRole;
      final PDStructureElement aParent = _getCurrentElement ();
      aElement = new PDStructureElement (eRealRole.getID (), aParent);
      if (StringHelper.isNotEmpty (sAlternateText))
        aElement.setAlternateDescription (sAlternateText);
      if (eRealRole == EPLStructureRole.TH)
      {
        // A header cell of a header row describes its column
        final PDTableAttributeObject aTableAttrs = new PDTableAttributeObject ();
        aTableAttrs.setScope (PDTableAttributeObject.SCOPE_COLUMN);
        aElement.addAttribute (aTableAttrs);
      }
      aParent.appendKid (aElement);
      m_aElements.put (sKey, aElement);
    }
    m_aStack.add (aElement);
    return true;
  }

  /**
   * Close the structure element opened last with {@link #beginElement(String, boolean,
   * EPLStructureRole, String)}.
   */
  public void endElement ()
  {
    if (m_aStack.isEmpty ())
      throw new IllegalStateException ("No structure element is open");
    m_aStack.removeLastOrNull ();
  }

  /**
   * @return The structure type of the innermost open structure element, which is the tag of the
   *         marked content. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  public String getCurrentStructureType ()
  {
    return _getCurrentElement ().getStructureType ();
  }

  /**
   * Create the next marked content ID of the current page and assign it to the innermost open
   * structure element.
   *
   * @return The marked content ID. Always &ge; 0.
   */
  @Nonnegative
  public int createMarkedContent ()
  {
    if (m_aCurrentPage == null)
      throw new IllegalStateException ("No page was started");
    final PDStructureElement aOwner = _getCurrentElement ();
    final int nMCID = m_aCurrentPageMCIDs.size ();
    m_aCurrentPageMCIDs.add (aOwner);

    final PDMarkedContentReference aMCR = new PDMarkedContentReference ();
    aMCR.setPage (m_aCurrentPage);
    aMCR.setMCID (nMCID);
    aOwner.appendKid (aMCR);
    return nMCID;
  }

  /**
   * Assign an annotation (e.g. a link) to the innermost open structure element. While an artifact
   * is open, the annotation gets a link structure element of its own in the document element.
   *
   * @param aAnnotation
   *        The annotation, which must be on the current page. May not be <code>null</code>.
   */
  public void addAnnotation (@NonNull final PDAnnotation aAnnotation)
  {
    ValueEnforcer.notNull (aAnnotation, "Annotation");

    // Annotations need an alternate description, e.g. the target of a link
    if (StringHelper.isEmpty (aAnnotation.getContents ()) && aAnnotation instanceof final PDAnnotationLink aLink)
    {
      if (aLink.getAction () instanceof final PDActionURI aURIAction)
        aLink.setContents (aURIAction.getURI ());
      else
        if (aLink.getAction () instanceof PDActionGoTo)
          aLink.setContents ("Link within the document");
    }

    final PDStructureElement aOwner;
    if (isInArtifact ())
    {
      // The content around it is an artifact (e.g. a link in a page footer), but every annotation
      // must be in the structure - it gets a link element of its own in the document element
      aOwner = new PDStructureElement (EPLStructureRole.LINK.getID (), m_aDocumentElement);
      m_aDocumentElement.appendKid (aOwner);
    }
    else
      aOwner = _getCurrentElement ();

    final int nKey = m_nNextParentTreeKey++;
    aAnnotation.setStructParent (nKey);
    m_aParentTree.put (Integer.valueOf (nKey), new PDParentTreeValue (aOwner.getCOSObject ()));

    final PDObjectReference aObjRef = new PDObjectReference ();
    aObjRef.setReferencedObject (aAnnotation);
    aObjRef.setPage (m_aCurrentPage);
    aOwner.appendKid (aObjRef);
  }

  /**
   * Write the structure tree into the document: the structure tree root with the parent tree and
   * the mark info. Called once after all pages were rendered.
   *
   * @param aDoc
   *        The document. May not be <code>null</code>.
   */
  public void finish (@NonNull final PDDocument aDoc)
  {
    ValueEnforcer.notNull (aDoc, "Document");
    if (m_aStack.isNotEmpty ())
      throw new IllegalStateException ("There are still " + m_aStack.size () + " open structure elements");

    final PDNumberTreeNode aParentTree = new PDNumberTreeNode (PDParentTreeValue.class);
    aParentTree.setNumbers (m_aParentTree);
    m_aRoot.setParentTree (aParentTree);
    m_aRoot.setParentTreeNextKey (m_nNextParentTreeKey);

    final PDDocumentCatalog aCatalog = aDoc.getDocumentCatalog ();
    aCatalog.setStructureTreeRoot (m_aRoot);
    final PDMarkInfo aMarkInfo = new PDMarkInfo ();
    aMarkInfo.setMarked (true);
    aCatalog.setMarkInfo (aMarkInfo);
  }
}
