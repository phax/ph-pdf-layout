/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MarginSpec}.
 *
 * @author Philip Helger
 */
public final class MarginSpecTest
{
  private static final float FLOAT_ALLOWED_ROUNDING_DIFFERENCE = 0.000_1f;
  private static final float F1 = 5.2f;
  private static final float F2 = 17.4f;

  @Test
  public void testBasic ()
  {
    MarginSpec m = new MarginSpec (F1);
    CommonsAssert.assertEquals (F1, m.getTop ());
    CommonsAssert.assertEquals (F1, m.getRight ());
    CommonsAssert.assertEquals (F1, m.getBottom ());
    CommonsAssert.assertEquals (F1, m.getLeft ());

    m = MarginSpec.top (F1);
    CommonsAssert.assertEquals (F1, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.right (F1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (F1, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.bottom (F1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (F1, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.left (F1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (F1, m.getLeft ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (m, new MarginSpec (m));
    CommonsTestHelper.testDefaultSerialization (m);
  }

  @Test
  public void testGetCloneWith ()
  {
    final MarginSpec m = new MarginSpec (F1);
    assertSame (m, m.getCloneWithTop (F1));
    assertSame (m, m.getCloneWithRight (F1));
    assertSame (m, m.getCloneWithBottom (F1));
    assertSame (m, m.getCloneWithLeft (F1));

    MarginSpec m2 = m.getCloneWithTop (F2);
    CommonsAssert.assertEquals (F2, m2.getTop ());
    CommonsAssert.assertEquals (F1, m2.getRight ());
    CommonsAssert.assertEquals (F1, m2.getBottom ());
    CommonsAssert.assertEquals (F1, m2.getLeft ());

    m2 = m.getCloneWithRight (F2);
    CommonsAssert.assertEquals (F1, m2.getTop ());
    CommonsAssert.assertEquals (F2, m2.getRight ());
    CommonsAssert.assertEquals (F1, m2.getBottom ());
    CommonsAssert.assertEquals (F1, m2.getLeft ());

    m2 = m.getCloneWithBottom (F2);
    CommonsAssert.assertEquals (F1, m2.getTop ());
    CommonsAssert.assertEquals (F1, m2.getRight ());
    CommonsAssert.assertEquals (F2, m2.getBottom ());
    CommonsAssert.assertEquals (F1, m2.getLeft ());

    m2 = m.getCloneWithLeft (F2);
    CommonsAssert.assertEquals (F1, m2.getTop ());
    CommonsAssert.assertEquals (F1, m2.getRight ());
    CommonsAssert.assertEquals (F1, m2.getBottom ());
    CommonsAssert.assertEquals (F2, m2.getLeft ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    MarginSpec m = MarginSpec.createMM (210f);
    assertEquals (f, m.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, m.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, m.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, m.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);

    m = MarginSpec.createMM (210f, 297f);
    assertEquals (f, m.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, m.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, m.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, m.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);

    m = MarginSpec.createMM (210f, 297f, 297f, 210f);
    assertEquals (f, m.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, m.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, m.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, m.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
  }
}
