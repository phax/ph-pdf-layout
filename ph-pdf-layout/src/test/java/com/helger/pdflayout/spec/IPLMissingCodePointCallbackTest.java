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
package com.helger.pdflayout.spec;

import static org.junit.Assert.assertEquals;
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
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;

/**
 * Test class for class {@link IPLMissingCodePointCallback}.
 *
 * @author Philip Helger
 */
public final class IPLMissingCodePointCallbackTest
{
  @Rule
  public final PLDebugTestRule m_aRule = new PLDebugTestRule ();

  private static final FontSpec REGULAR = new FontSpec (PreloadFont.REGULAR, 12);
  private static final FontSpec BOLD = new FontSpec (PreloadFont.REGULAR_BOLD, 12);

  private static void _render (final PageLayoutPDF aLayout) throws PDFCreationException
  {
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      aLayout.renderTo (aOS);
    }
  }

  @Test
  public void testCalledOncePerFontAndCodePoint () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("aЖbЖ", REGULAR));
    aPS.addElement (new PLText ("ЖΩ", REGULAR));
    aPS.addElement (new PLText ("Ж", BOLD));

    final ICommonsList <String> aCalls = new CommonsArrayList <> ();
    _render (new PageLayoutPDF ().addPageSet (aPS)
                                 .setMissingCodePointCallback ( (aFont, nCodePoint, nFallbackCodePoint) -> aCalls.add (aFont.getName () +
                                                                                                                       " " +
                                                                                                                       Integer.toHexString (nCodePoint) +
                                                                                                                       " " +
                                                                                                                       Integer.toHexString (nFallbackCodePoint))));

    assertEquals (aCalls.toString (), 3, aCalls.size ());
    assertEquals ("Helvetica 416 3f", aCalls.get (0));
    assertEquals ("Helvetica 3a9 3f", aCalls.get (1));
    assertEquals ("Helvetica-Bold 416 3f", aCalls.get (2));
  }

  @Test
  public void testNotCalledIfTheFontHasTheCharacters () throws PDFCreationException
  {
    final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
    aPS.addElement (new PLText ("Straße 10 €", REGULAR));

    final ICommonsList <Integer> aCalls = new CommonsArrayList <> ();
    _render (new PageLayoutPDF ().addPageSet (aPS)
                                 .setMissingCodePointCallback ( (aFont, nCodePoint, nFallbackCodePoint) -> aCalls.add (Integer.valueOf (nCodePoint))));
    assertTrue (aCalls.toString (), aCalls.isEmpty ());
  }
}
