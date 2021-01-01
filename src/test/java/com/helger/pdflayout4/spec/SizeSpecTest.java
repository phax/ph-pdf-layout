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

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SizeSpec}.
 *
 * @author Philip Helger
 */
public final class SizeSpecTest
{
  private static final float w = 5.2f;
  private static final float h = 17.4f;

  @Test
  public void testBasic ()
  {
    SizeSpec s = new SizeSpec (w, h);
    CommonsAssert.assertEquals (w, s.getWidth ());
    CommonsAssert.assertEquals (h, s.getHeight ());

    s = SizeSpec.width (w);
    CommonsAssert.assertEquals (w, s.getWidth ());
    CommonsAssert.assertEquals (0, s.getHeight ());

    s = SizeSpec.height (h);
    CommonsAssert.assertEquals (0, s.getWidth ());
    CommonsAssert.assertEquals (h, s.getHeight ());

    s = s.withWidth (h);
    CommonsAssert.assertEquals (h, s.getWidth ());
    CommonsAssert.assertEquals (h, s.getHeight ());

    s = s.withHeight (w);
    CommonsAssert.assertEquals (h, s.getWidth ());
    CommonsAssert.assertEquals (w, s.getHeight ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (s, SizeSpec.create (s.getAsRectangle ()));
    CommonsTestHelper.testDefaultSerialization (s);
  }

  @Test
  public void testPlusMinus ()
  {
    final SizeSpec s = new SizeSpec (w, h);

    SizeSpec s2 = s.plus (s);
    CommonsAssert.assertEquals (w * 2, s2.getWidth ());
    CommonsAssert.assertEquals (h * 2, s2.getHeight ());

    s2 = s.plus (w, h);
    CommonsAssert.assertEquals (w * 2, s2.getWidth ());
    CommonsAssert.assertEquals (h * 2, s2.getHeight ());

    s2 = s.minus (s);
    CommonsAssert.assertEquals (0, s2.getWidth ());
    CommonsAssert.assertEquals (0, s2.getHeight ());

    s2 = s.minus (w, h);
    CommonsAssert.assertEquals (0, s2.getWidth ());
    CommonsAssert.assertEquals (0, s2.getHeight ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    final SizeSpec s = SizeSpec.createMM (210f, 297f);
    CommonsAssert.assertEquals (f, s.getWidth ());
    CommonsAssert.assertEquals (g, s.getHeight ());

    final PDRectangle r = s.getAsRectangle ();
    CommonsAssert.assertEquals (f, r.getWidth ());
    CommonsAssert.assertEquals (g, r.getHeight ());
  }
}
