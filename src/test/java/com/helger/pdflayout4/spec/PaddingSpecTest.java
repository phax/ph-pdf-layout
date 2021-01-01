/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.spec;

import static org.junit.Assert.assertSame;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link PaddingSpec}.
 *
 * @author Philip Helger
 */
public final class PaddingSpecTest
{
  private static final float f1 = 5.2f;
  private static final float f2 = 17.4f;

  @Test
  public void testBasic ()
  {
    PaddingSpec p = new PaddingSpec (f1);
    CommonsAssert.assertEquals (f1, p.getTop ());
    CommonsAssert.assertEquals (f1, p.getRight ());
    CommonsAssert.assertEquals (f1, p.getBottom ());
    CommonsAssert.assertEquals (f1, p.getLeft ());

    p = PaddingSpec.top (f1);
    CommonsAssert.assertEquals (f1, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.right (f1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (f1, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.bottom (f1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (f1, p.getBottom ());
    CommonsAssert.assertEquals (0, p.getLeft ());

    p = PaddingSpec.left (f1);
    CommonsAssert.assertEquals (0, p.getTop ());
    CommonsAssert.assertEquals (0, p.getRight ());
    CommonsAssert.assertEquals (0, p.getBottom ());
    CommonsAssert.assertEquals (f1, p.getLeft ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (p, new PaddingSpec (p));
    CommonsTestHelper.testDefaultSerialization (p);
  }

  @Test
  public void testGetCloneWith ()
  {
    final PaddingSpec p = new PaddingSpec (f1);
    assertSame (p, p.getCloneWithTop (f1));
    assertSame (p, p.getCloneWithRight (f1));
    assertSame (p, p.getCloneWithBottom (f1));
    assertSame (p, p.getCloneWithLeft (f1));

    PaddingSpec p2 = p.getCloneWithTop (f2);
    CommonsAssert.assertEquals (f2, p2.getTop ());
    CommonsAssert.assertEquals (f1, p2.getRight ());
    CommonsAssert.assertEquals (f1, p2.getBottom ());
    CommonsAssert.assertEquals (f1, p2.getLeft ());

    p2 = p.getCloneWithRight (f2);
    CommonsAssert.assertEquals (f1, p2.getTop ());
    CommonsAssert.assertEquals (f2, p2.getRight ());
    CommonsAssert.assertEquals (f1, p2.getBottom ());
    CommonsAssert.assertEquals (f1, p2.getLeft ());

    p2 = p.getCloneWithBottom (f2);
    CommonsAssert.assertEquals (f1, p2.getTop ());
    CommonsAssert.assertEquals (f1, p2.getRight ());
    CommonsAssert.assertEquals (f2, p2.getBottom ());
    CommonsAssert.assertEquals (f1, p2.getLeft ());

    p2 = p.getCloneWithLeft (f2);
    CommonsAssert.assertEquals (f1, p2.getTop ());
    CommonsAssert.assertEquals (f1, p2.getRight ());
    CommonsAssert.assertEquals (f1, p2.getBottom ());
    CommonsAssert.assertEquals (f2, p2.getLeft ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    PaddingSpec p = PaddingSpec.createMM (210f);
    CommonsAssert.assertEquals (f, p.getTop ());
    CommonsAssert.assertEquals (f, p.getRight ());
    CommonsAssert.assertEquals (f, p.getBottom ());
    CommonsAssert.assertEquals (f, p.getLeft ());

    p = PaddingSpec.createMM (210f, 297f);
    CommonsAssert.assertEquals (f, p.getTop ());
    CommonsAssert.assertEquals (g, p.getRight ());
    CommonsAssert.assertEquals (f, p.getBottom ());
    CommonsAssert.assertEquals (g, p.getLeft ());

    p = PaddingSpec.createMM (210f, 297f, 297f, 210f);
    CommonsAssert.assertEquals (f, p.getTop ());
    CommonsAssert.assertEquals (g, p.getRight ());
    CommonsAssert.assertEquals (g, p.getBottom ());
    CommonsAssert.assertEquals (f, p.getLeft ());
  }
}
