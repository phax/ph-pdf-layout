/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.base.mock.CommonsAssert;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link PaddingSpec}.
 *
 * @author Philip Helger
 */
public final class PaddingSpecTest
{
  private static final float FLOAT_ALLOWED_ROUNDING_DIFFERENCE = 0.000_1f;

  private static final float F1 = 5.2f;
  private static final float F2 = 17.4f;

  @Test
  public void testBasic ()
  {
    PaddingSpec p = new PaddingSpec (F1);
    CommonsAssert.assertEquals (F1, p.getTop ());
    CommonsAssert.assertEquals (F1, p.getRight ());
    CommonsAssert.assertEquals (F1, p.getBottom ());
    CommonsAssert.assertEquals (F1, p.getLeft ());

    p = PaddingSpec.top (F1);
    CommonsAssert.assertEquals (F1, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.right (F1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (F1, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.bottom (F1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (F1, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.left (F1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (F1, p.getLeft ());

    TestHelper.testDefaultImplementationWithEqualContentObject (p, new PaddingSpec (p));
    TestHelper.testDefaultSerialization (p);
  }

  @Test
  public void testGetCloneWith ()
  {
    final PaddingSpec p = new PaddingSpec (F1);
    assertSame (p, p.getCloneWithTop (F1));
    assertSame (p, p.getCloneWithRight (F1));
    assertSame (p, p.getCloneWithBottom (F1));
    assertSame (p, p.getCloneWithLeft (F1));

    PaddingSpec p2 = p.getCloneWithTop (F2);
    CommonsAssert.assertEquals (F2, p2.getTop ());
    CommonsAssert.assertEquals (F1, p2.getRight ());
    CommonsAssert.assertEquals (F1, p2.getBottom ());
    CommonsAssert.assertEquals (F1, p2.getLeft ());

    p2 = p.getCloneWithRight (F2);
    CommonsAssert.assertEquals (F1, p2.getTop ());
    CommonsAssert.assertEquals (F2, p2.getRight ());
    CommonsAssert.assertEquals (F1, p2.getBottom ());
    CommonsAssert.assertEquals (F1, p2.getLeft ());

    p2 = p.getCloneWithBottom (F2);
    CommonsAssert.assertEquals (F1, p2.getTop ());
    CommonsAssert.assertEquals (F1, p2.getRight ());
    CommonsAssert.assertEquals (F2, p2.getBottom ());
    CommonsAssert.assertEquals (F1, p2.getLeft ());

    p2 = p.getCloneWithLeft (F2);
    CommonsAssert.assertEquals (F1, p2.getTop ());
    CommonsAssert.assertEquals (F1, p2.getRight ());
    CommonsAssert.assertEquals (F1, p2.getBottom ());
    CommonsAssert.assertEquals (F2, p2.getLeft ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    PaddingSpec p = PaddingSpec.createMM (210f);
    assertEquals (f, p.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, p.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, p.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, p.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);

    p = PaddingSpec.createMM (210f, 297f);
    assertEquals (f, p.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, p.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, p.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, p.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);

    p = PaddingSpec.createMM (210f, 297f, 297f, 210f);
    assertEquals (f, p.getTop (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, p.getRight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, p.getBottom (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (f, p.getLeft (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
  }
}
