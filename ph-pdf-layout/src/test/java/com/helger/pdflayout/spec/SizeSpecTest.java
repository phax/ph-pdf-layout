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
  private static final float FLOAT_ALLOWED_ROUNDING_DIFFERENCE = 0.000_1f;
  private static final float W = 5.2f;
  private static final float H = 17.4f;

  @Test
  public void testBasic ()
  {
    SizeSpec s = new SizeSpec (W, H);
    CommonsAssert.assertEquals (W, s.getWidth ());
    CommonsAssert.assertEquals (H, s.getHeight ());

    s = SizeSpec.width (W);
    CommonsAssert.assertEquals (W, s.getWidth ());
    CommonsAssert.assertEquals (0, s.getHeight ());

    s = SizeSpec.height (H);
    CommonsAssert.assertEquals (0, s.getWidth ());
    CommonsAssert.assertEquals (H, s.getHeight ());

    s = s.withWidth (H);
    CommonsAssert.assertEquals (H, s.getWidth ());
    CommonsAssert.assertEquals (H, s.getHeight ());

    s = s.withHeight (W);
    CommonsAssert.assertEquals (H, s.getWidth ());
    CommonsAssert.assertEquals (W, s.getHeight ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (s, SizeSpec.create (s.getAsRectangle ()));
    CommonsTestHelper.testDefaultSerialization (s);
  }

  @Test
  public void testPlusMinus ()
  {
    final SizeSpec s = new SizeSpec (W, H);

    SizeSpec s2 = s.plus (s);
    CommonsAssert.assertEquals (W * 2, s2.getWidth ());
    CommonsAssert.assertEquals (H * 2, s2.getHeight ());

    s2 = s.plus (W, H);
    CommonsAssert.assertEquals (W * 2, s2.getWidth ());
    CommonsAssert.assertEquals (H * 2, s2.getHeight ());

    s2 = s.minus (s);
    CommonsAssert.assertEquals (0, s2.getWidth ());
    CommonsAssert.assertEquals (0, s2.getHeight ());

    s2 = s.minus (W, H);
    CommonsAssert.assertEquals (0, s2.getWidth ());
    CommonsAssert.assertEquals (0, s2.getHeight ());
  }

  @Test
  public void testConvert ()
  {
    final float f = PDRectangle.A4.getWidth ();
    final float g = PDRectangle.A4.getHeight ();

    final SizeSpec s = SizeSpec.createMM (210f, 297f);
    assertEquals (f, s.getWidth (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, s.getHeight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);

    final PDRectangle r = s.getAsRectangle ();
    assertEquals (f, r.getWidth (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    assertEquals (g, r.getHeight (), FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
  }
}
