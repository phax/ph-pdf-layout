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
 * Test class for class {@link MarginSpec}.
 *
 * @author Philip Helger
 */
public final class MarginSpecTest
{
  private static final float f1 = 5.2f;
  private static final float f2 = 17.4f;

  @Test
  public void testBasic ()
  {
    MarginSpec m = new MarginSpec (f1);
    CommonsAssert.assertEquals (f1, m.getTop ());
    CommonsAssert.assertEquals (f1, m.getRight ());
    CommonsAssert.assertEquals (f1, m.getBottom ());
    CommonsAssert.assertEquals (f1, m.getLeft ());

    m = MarginSpec.top (f1);
    CommonsAssert.assertEquals (f1, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.right (f1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (f1, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.bottom (f1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (f1, m.getBottom ());
    CommonsAssert.assertEquals (0, m.getLeft ());

    m = MarginSpec.left (f1);
    CommonsAssert.assertEquals (0, m.getTop ());
    CommonsAssert.assertEquals (0, m.getRight ());
    CommonsAssert.assertEquals (0, m.getBottom ());
    CommonsAssert.assertEquals (f1, m.getLeft ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (m, new MarginSpec (m));
    CommonsTestHelper.testDefaultSerialization (m);
  }

  @Test
  public void testGetCloneWith ()
  {
    final MarginSpec m = new MarginSpec (f1);
    assertSame (m, m.getCloneWithTop (f1));
    assertSame (m, m.getCloneWithRight (f1));
    assertSame (m, m.getCloneWithBottom (f1));
    assertSame (m, m.getCloneWithLeft (f1));

    MarginSpec m2 = m.getCloneWithTop (f2);
    CommonsAssert.assertEquals (f2, m2.getTop ());
    CommonsAssert.assertEquals (f1, m2.getRight ());
    CommonsAssert.assertEquals (f1, m2.getBottom ());
    CommonsAssert.assertEquals (f1, m2.getLeft ());

    m2 = m.getCloneWithRight (f2);
    CommonsAssert.assertEquals (f1, m2.getTop ());
    CommonsAssert.assertEquals (f2, m2.getRight ());
    CommonsAssert.assertEquals (f1, m2.getBottom ());
    CommonsAssert.assertEquals (f1, m2.getLeft ());

    m2 = m.getCloneWithBottom (f2);
    CommonsAssert.assertEquals (f1, m2.getTop ());
    CommonsAssert.assertEquals (f1, m2.getRight ());
    CommonsAssert.assertEquals (f2, m2.getBottom ());
    CommonsAssert.assertEquals (f1, m2.getLeft ());

    m2 = m.getCloneWithLeft (f2);
    CommonsAssert.assertEquals (f1, m2.getTop ());
    CommonsAssert.assertEquals (f1, m2.getRight ());
    CommonsAssert.assertEquals (f1, m2.getBottom ());
    CommonsAssert.assertEquals (f2, m2.getLeft ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    MarginSpec m = MarginSpec.createMM (210f);
    CommonsAssert.assertEquals (f, m.getTop ());
    CommonsAssert.assertEquals (f, m.getRight ());
    CommonsAssert.assertEquals (f, m.getBottom ());
    CommonsAssert.assertEquals (f, m.getLeft ());

    m = MarginSpec.createMM (210f, 297f);
    CommonsAssert.assertEquals (f, m.getTop ());
    CommonsAssert.assertEquals (g, m.getRight ());
    CommonsAssert.assertEquals (f, m.getBottom ());
    CommonsAssert.assertEquals (g, m.getLeft ());

    m = MarginSpec.createMM (210f, 297f, 297f, 210f);
    CommonsAssert.assertEquals (f, m.getTop ());
    CommonsAssert.assertEquals (g, m.getRight ());
    CommonsAssert.assertEquals (g, m.getBottom ());
    CommonsAssert.assertEquals (f, m.getLeft ());
  }
}
