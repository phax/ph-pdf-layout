/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;

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
  }

  @Test
  public void testGetClontWith ()
  {
    final PaddingSpec p = new PaddingSpec (f1);

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
}
