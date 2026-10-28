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
package com.helger.pdflayout.pdfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link PdfAssociatedFile}.
 *
 * @author Philip Helger
 */
public final class PdfAssociatedFileTest
{
  @Test
  public void testBasic ()
  {
    final byte [] aContent = { 1, 2, 3 };
    PdfAssociatedFile a = new PdfAssociatedFile (aContent, "a.xml", "text/xml", "desc", EPdfAFRelationship.SOURCE);
    // Not copied
    assertSame (aContent, a.getContent ());
    assertEquals ("a.xml", a.getFileName ());
    assertEquals ("text/xml", a.getMimeType ());
    assertEquals ("desc", a.getDescription ());
    assertSame (EPdfAFRelationship.SOURCE, a.getRelationship ());
    assertNotNull (a.toString ());

    // Without description
    a = new PdfAssociatedFile (aContent, "a.bin", "application/octet-stream", null, EPdfAFRelationship.DATA);
    assertNull (a.getDescription ());
    assertNotNull (a.toString ());

    // Empty content is allowed
    a = new PdfAssociatedFile (new byte [0], "empty.txt", "text/plain", null, EPdfAFRelationship.UNSPECIFIED);
    assertEquals (0, a.getContent ().length);
  }

  @Test
  public void testInvalid ()
  {
    final byte [] aContent = { 1 };
    try
    {
      new PdfAssociatedFile (null, "a.xml", "text/xml", null, EPdfAFRelationship.SOURCE);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
    try
    {
      new PdfAssociatedFile (aContent, "", "text/xml", null, EPdfAFRelationship.SOURCE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PdfAssociatedFile (aContent, "a.xml", "", null, EPdfAFRelationship.SOURCE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PdfAssociatedFile (aContent, "a.xml", "text/xml", null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }
}
