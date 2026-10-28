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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.junit.Test;

/**
 * Test class for class {@link EPLStructureRole}.
 *
 * @author Philip Helger
 */
public final class EPLStructureRoleTest
{
  @Test
  public void testBasic ()
  {
    for (final EPLStructureRole e : EPLStructureRole.values ())
      assertSame (e, EPLStructureRole.getFromIDOrNull (e.getID ()));
    assertNull (EPLStructureRole.getFromIDOrNull (null));
    assertNull (EPLStructureRole.getFromIDOrNull ("p"));
  }

  @Test
  public void testStandardStructureTypes ()
  {
    // All IDs are standard structure types of PDFBox
    for (final EPLStructureRole e : EPLStructureRole.values ())
      assertEquals (e.getID (), StandardStructureTypes.types.stream ().filter (e.getID ()::equals).findFirst ().orElse (null));
  }
}
