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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PLDebugTestRule;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.special.PLPageBreak;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for {@link IPLRenderListener} and {@link PLRenderedElementCollector}.
 *
 * @author Philip Helger
 */
public final class PLRenderListenerTest
{
  @Rule
  public final PLDebugTestRule m_aRule = new PLDebugTestRule ();

  private static final FontSpec FONT = new FontSpec (PreloadFont.REGULAR, 12);

  /**
   * Render and discard. We only care about the listener events.
   */
  private static void _render (final PageLayoutPDF aLayout) throws PDFCreationException
  {
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      aLayout.renderTo (aOS);
    }
  }

  @Test
  public void testCollectorRecordsEachContentElementOnce () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Chapter 1", FONT).setID ("ch1"));
    aPS.addElement (new PLText ("Chapter 2", FONT).setID ("ch2"));
    aPS.addElement (new PLText ("Chapter 3", FONT).setID ("ch3"));

    final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ();
    aPS.setRenderListener (aCollector);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    // All three on page 0 (small content, big page).
    assertNotNull (aCollector.getLocation ("ch1"));
    assertNotNull (aCollector.getLocation ("ch2"));
    assertNotNull (aCollector.getLocation ("ch3"));
    assertEquals (0, aCollector.getLocation ("ch1").getTotalPageIndex ());
    assertEquals (0, aCollector.getLocation ("ch2").getTotalPageIndex ());
    assertEquals (0, aCollector.getLocation ("ch3").getTotalPageIndex ());

    // Insertion order matches render order.
    final ICommonsList <String> aKeys = new CommonsArrayList <> (aCollector.getAll ().keySet ());
    assertEquals ("ch1", aKeys.get (0));
    assertEquals ("ch2", aKeys.get (1));
    assertEquals ("ch3", aKeys.get (2));
  }

  @Test
  public void testCollectorTracksPageBreaks () throws PDFCreationException
  {
    // Explicit page breaks: deterministic regardless of font metrics.
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("First", FONT).setID ("first"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLText ("Second", FONT).setID ("second"));
    aPS.addElement (new PLPageBreak (true));
    aPS.addElement (new PLText ("Third", FONT).setID ("third"));

    final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ();
    aPS.setRenderListener (aCollector);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    assertEquals (0, aCollector.getLocation ("first").getTotalPageIndex ());
    assertEquals (1, aCollector.getLocation ("second").getTotalPageIndex ());
    assertEquals (2, aCollector.getLocation ("third").getTotalPageIndex ());
  }

  @Test
  public void testCollectorIgnoresSecondFragmentOfSplitElement () throws PDFCreationException
  {
    // Forces a vertical split: lots of lines into a small page.
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < 25; i++)
      aSB.append ("Long content line ").append (i).append ('\n');

    final PLPageSet aPS = new PLPageSet (300, 120).setMargin (10);
    aPS.addElement (new PLText (aSB.toString (), FONT).setID ("longtext").setVertSplittable (true));

    // Raw listener captures every event (including the second fragment).
    final ICommonsList <String> aRawIDs = new CommonsArrayList <> ();
    final ICommonsList <String> aRawOriginalIDs = new CommonsArrayList <> ();
    final ICommonsList <Boolean> aRawFirstFragmentFlags = new CommonsArrayList <> ();
    aPS.setRenderListener ( (aElement, aCtx) -> {
      if (aCtx.getElementType () != ERenderingElementType.CONTENT_ELEMENT)
        return;
      // Only the top-level text is interesting; PLText has no nested children.
      if (aElement instanceof PLText)
      {
        aRawIDs.add (aElement.getID ());
        aRawOriginalIDs.add (aElement.getOriginalID ());
        aRawFirstFragmentFlags.add (Boolean.valueOf (aElement.isFirstFragment ()));
      }
    });

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    // At least one split must have happened.
    assertTrue ("Expected the text to split across pages, but only " + aRawIDs.size () + " fragment(s) rendered",
                aRawIDs.size () >= 2);

    // Every fragment has the original ID "longtext".
    for (final String sOrig : aRawOriginalIDs)
      assertEquals ("longtext", sOrig);

    // Exactly one fragment is a first fragment.
    int nFirstCount = 0;
    for (final Boolean b : aRawFirstFragmentFlags)
      if (b.booleanValue ())
        nFirstCount++;
    assertEquals ("Exactly one fragment of a single root element should be first",
                  1,
                  nFirstCount);

    // The actual IDs include "longtext-1" and "longtext-2..." patterns.
    boolean bFoundSuffixed = false;
    for (final String sID : aRawIDs)
      if (!sID.equals ("longtext") && sID.startsWith ("longtext-"))
      {
        bFoundSuffixed = true;
        break;
      }
    assertTrue ("Expected at least one fragment with the 'longtext-N' suffix pattern, got: " + aRawIDs,
                bFoundSuffixed);
  }

  @Test
  public void testCollectorDedupesSplitFragmentsToFirstAppearance () throws PDFCreationException
  {
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < 25; i++)
      aSB.append ("Long content line ").append (i).append ('\n');

    final PLPageSet aPS = new PLPageSet (300, 120).setMargin (10);
    aPS.addElement (new PLText (aSB.toString (), FONT).setID ("longtext").setVertSplittable (true));

    final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ();
    aPS.setRenderListener (aCollector);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    // Exactly one entry keyed by the original ID, on the first page.
    assertEquals (1, aCollector.getAll ().size ());
    final PLRenderedElementCollector.Location aLoc = aCollector.getLocation ("longtext");
    assertNotNull (aLoc);
    assertEquals (0, aLoc.getTotalPageIndex ());
  }

  @Test
  public void testCollectorSkipsHeaderFooterByDefault () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (60);
    aPS.setPageHeader (new PLText ("Header", FONT).setID ("hdr"));
    aPS.setPageFooter (new PLText ("Footer", FONT).setID ("ftr"));
    aPS.addElement (new PLText ("Body", FONT).setID ("body"));

    final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ();
    aPS.setRenderListener (aCollector);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    assertNotNull (aCollector.getLocation ("body"));
    assertNull ("Header should be skipped by default", aCollector.getLocation ("hdr"));
    assertNull ("Footer should be skipped by default", aCollector.getLocation ("ftr"));
  }

  @Test
  public void testCollectorIncludesHeaderFooterWhenAsked () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (60);
    aPS.setPageHeader (new PLText ("Header", FONT).setID ("hdr"));
    aPS.setPageFooter (new PLText ("Footer", FONT).setID ("ftr"));
    aPS.addElement (new PLText ("Body", FONT).setID ("body"));

    final PLRenderedElementCollector aCollector = new PLRenderedElementCollector ().setIncludeHeaderFooter (true);
    aPS.setRenderListener (aCollector);

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    assertNotNull (aCollector.getLocation ("hdr"));
    assertNotNull (aCollector.getLocation ("ftr"));
    assertNotNull (aCollector.getLocation ("body"));
  }

  @Test
  public void testListenerFiresOnNestedChildren () throws PDFCreationException
  {
    // A PLText inside a PLBox: the listener should observe BOTH because every
    // render call flows through AbstractPLRenderableObject.render.
    final com.helger.pdflayout.element.box.PLBox aBox = new com.helger.pdflayout.element.box.PLBox (new PLText ("inner",
                                                                                                                FONT)
                                                                                                                     .setID ("inner")).setID ("outer");

    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (aBox);

    final ICommonsList <String> aObservedIDs = new CommonsArrayList <> ();
    aPS.setRenderListener ( (final IPLRenderableObject <?> aElement, final PageRenderContext aCtx) -> {
      if (aCtx.getElementType () == ERenderingElementType.CONTENT_ELEMENT)
        aObservedIDs.add (aElement.getID ());
    });

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);

    assertTrue ("Expected listener to see outer box, got " + aObservedIDs, aObservedIDs.contains ("outer"));
    assertTrue ("Expected listener to see inner text, got " + aObservedIDs, aObservedIDs.contains ("inner"));
  }

  @Test
  public void testNullListenerDoesNotBreakRendering () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("just one line", FONT));
    // No listener set.
    assertNull (aPS.getRenderListener ());

    final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
    _render (aLayout);
    // No assertion needed - render must just not blow up.
    assertFalse (false);
  }
}
