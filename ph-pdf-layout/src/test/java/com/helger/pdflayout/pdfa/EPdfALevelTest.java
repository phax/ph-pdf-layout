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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link EPdfALevel}.
 *
 * @author Philip Helger
 */
public final class EPdfALevelTest
{
  @Test
  public void testBasic ()
  {
    for (final EPdfALevel e : EPdfALevel.values ())
    {
      assertSame (e, EPdfALevel.getFromIDOrNull (e.getID ()));
      // The ID is the part followed by the lower case conformance
      assertEquals (e.getPart () + e.getConformance ().toLowerCase (Locale.ROOT), e.getID ());
      assertTrue (e.getPart () == 2 || e.getPart () == 3);
      assertEquals (e.getPart () >= 3, e.isArbitraryEmbeddedFilesAllowed ());
      assertEquals ("A".equals (e.getConformance ()), e.isTagged ());
    }
    assertNull (EPdfALevel.getFromIDOrNull (null));
    assertNull (EPdfALevel.getFromIDOrNull ("1b"));
    assertNull (EPdfALevel.getFromIDOrNull ("3B"));
  }

  @Test
  public void testSpecificValues ()
  {
    assertEquals (3, EPdfALevel.PDF_A_3B.getPart ());
    assertEquals ("B", EPdfALevel.PDF_A_3B.getConformance ());
    assertFalse (EPdfALevel.PDF_A_3B.isTagged ());
    assertTrue (EPdfALevel.PDF_A_3B.isArbitraryEmbeddedFilesAllowed ());

    assertEquals (2, EPdfALevel.PDF_A_2U.getPart ());
    assertEquals ("U", EPdfALevel.PDF_A_2U.getConformance ());
    assertFalse (EPdfALevel.PDF_A_2U.isArbitraryEmbeddedFilesAllowed ());

    assertTrue (EPdfALevel.PDF_A_3A.isTagged ());
  }
}
