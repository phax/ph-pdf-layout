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
package com.helger.pdflayout.render;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;

/**
 * Internal helper for registering named destinations on a {@link PDDocument}. Used by the render
 * pipeline whenever an element implementing {@link com.helger.pdflayout.base.IPLHasAnchorName} is
 * rendered as a first fragment.
 * <p>
 * Stores destinations in the document's <code>/Names/Dests</code> name tree (the modern PDF
 * mechanism). On duplicate names a warning is logged and the first registration wins.
 *
 * @author Philip Helger
 * @since 8.2.0
 */
public final class PLAnchorRegistry
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PLAnchorRegistry.class);

  private PLAnchorRegistry ()
  {}

  /**
   * Register a named destination pointing at the given (page, left, top) location. Coordinates are
   * in PDF user space, where the origin is the lower-left corner of the page.
   *
   * @param aDoc
   *        The document to attach the destination to. May not be <code>null</code>.
   * @param sName
   *        The anchor name. May not be empty.
   * @param aPage
   *        The page the destination points at. May not be <code>null</code>.
   * @param fLeft
   *        Absolute page x coordinate in PDF user space.
   * @param fTop
   *        Absolute page y coordinate in PDF user space (origin at page lower-left).
   * @throws IOException
   *         If the name tree could not be read or written.
   */
  public static void registerNamedDestination (@NonNull final PDDocument aDoc,
                                               @NonNull @Nonempty final String sName,
                                               @NonNull final PDPage aPage,
                                               final float fLeft,
                                               final float fTop) throws IOException
  {
    ValueEnforcer.notNull (aDoc, "Doc");
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (aPage, "Page");

    final PDDocumentCatalog aCatalog = aDoc.getDocumentCatalog ();

    PDDocumentNameDictionary aNameDict = aCatalog.getNames ();
    if (aNameDict == null)
    {
      aNameDict = new PDDocumentNameDictionary (aCatalog);
      aCatalog.setNames (aNameDict);
    }

    PDDestinationNameTreeNode aDestsTree = aNameDict.getDests ();
    final ICommonsMap <String, PDPageDestination> aExisting = new CommonsHashMap <> ();
    if (aDestsTree != null)
    {
      final Map <String, PDPageDestination> aCurrent = aDestsTree.getNames ();
      if (aCurrent != null)
        aExisting.putAll (aCurrent);
    }
    else
    {
      aDestsTree = new PDDestinationNameTreeNode ();
    }

    if (aExisting.containsKey (sName))
    {
      // Warn-and-keep-first style. Matches AbstractPLObject.setID's tolerance
      // for repeated IDs (see AbstractPLObject.java:89).
      LOGGER.warn ("Anchor name '" + sName + "' is already registered; keeping the first registration");
    }
    else
    {
      final PDPageXYZDestination aDest = new PDPageXYZDestination ();
      aDest.setPage (aPage);
      aDest.setLeft ((int) fLeft);
      aDest.setTop ((int) fTop);
      // Leave zoom unset (null) = preserve reader's current zoom level when followed.

      aExisting.put (sName, aDest);
      aDestsTree.setNames (aExisting);
      aNameDict.setDests (aDestsTree);
    }
  }
}
