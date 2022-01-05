/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.font.api.IFontResource;
import com.helger.font.lato2.EFontResourceLato2;

/**
 * Test class for class {@link PreloadFontManager}.
 *
 * @author Philip Helger
 */
public final class PreloadFontManagerTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PreloadFontManagerTest.class);

  @Test
  public void testInit ()
  {
    final PreloadFontManager aMgr = new PreloadFontManager ();
    assertNull (aMgr.getPreloadFontOfID ((IFontResource) null));
    assertNull (aMgr.getPreloadFontOfID ((String) null));
    assertNull (aMgr.getPreloadFontOfID ("abc"));
    assertSame (PreloadFont.MONOSPACE, aMgr.getPreloadFontOfID (PreloadFont.MONOSPACE.getID ()));
    assertNotNull (aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK));
    assertSame (aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK),
                aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK));

    for (final PreloadFont x : aMgr.getAllPreloadFonts ())
      CommonsTestHelper.testDefaultSerialization (x);
  }

  public void _test (final PDType1Font f, final int nCP) throws IOException
  {
    LOGGER.info ("Character: " + (char) nCP);
    LOGGER.info ("  Height = " + f.getHeight (nCP));
    LOGGER.info ("  Width = " + f.getWidth (nCP));
    LOGGER.info ("  Displacement-X = " + f.getDisplacement (nCP).getX ());
    LOGGER.info ("  Font-Ascent = " + f.getFontDescriptor ().getAscent ());
    LOGGER.info ("  Font-CapHeight = " + f.getFontDescriptor ().getCapHeight ());
    LOGGER.info ("  Font-Descent = " + f.getFontDescriptor ().getDescent ());
  }

  @Test
  public void testGetDifferences () throws IOException
  {
    final PDType1Font f = (PDType1Font) PreloadFont.SYMBOL.loadPDFont (null);
    _test (f, "\u00b0".codePointAt (0));
    _test (f, "\u00b7".codePointAt (0));

    final PDType1Font f2 = (PDType1Font) PreloadFont.REGULAR.loadPDFont (null);
    _test (f2, "B".codePointAt (0));
    _test (f2, "C".codePointAt (0));
    _test (f2, "b".codePointAt (0));
    _test (f2, "c".codePointAt (0));
  }
}
