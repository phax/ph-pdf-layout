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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Test class for class {@link EPdfAFRelationship}.
 *
 * @author Philip Helger
 */
public final class EPdfAFRelationshipTest
{
  @Test
  public void testBasic ()
  {
    for (final EPdfAFRelationship e : EPdfAFRelationship.values ())
      assertSame (e, EPdfAFRelationship.getFromIDOrNull (e.getID ()));
    assertNull (EPdfAFRelationship.getFromIDOrNull (null));
    // The IDs are case sensitive PDF names
    assertNull (EPdfAFRelationship.getFromIDOrNull ("source"));

    assertEquals ("Source", EPdfAFRelationship.SOURCE.getID ());
    assertEquals ("Data", EPdfAFRelationship.DATA.getID ());
    assertEquals ("Alternative", EPdfAFRelationship.ALTERNATIVE.getID ());
    assertEquals ("Supplement", EPdfAFRelationship.SUPPLEMENT.getID ());
    assertEquals ("Unspecified", EPdfAFRelationship.UNSPECIFIED.getID ());
  }
}
