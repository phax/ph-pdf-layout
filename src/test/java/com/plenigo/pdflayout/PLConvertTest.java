/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for class {@link PLConvert}.
 *
 * @author Philip Helger
 */
public final class PLConvertTest {
    private static final float DELTA = 0.001f;

    @Test
    public void testBasic() {
        assertEquals(PDRectangle.A4.getWidth(), PLConvert.mm2units(210), DELTA);
        assertEquals(PDRectangle.A4.getWidth(), PLConvert.cm2units(21), DELTA);

        assertEquals(210f, PLConvert.units2mm(PDRectangle.A4.getWidth()), DELTA);
        assertEquals(297f, PLConvert.units2mm(PDRectangle.A4.getHeight()), DELTA);
    assertEquals (21.0f, PLConvert.units2cm (PDRectangle.A4.getWidth ()), DELTA);
    assertEquals (29.7f, PLConvert.units2cm (PDRectangle.A4.getHeight ()), DELTA);
  }
}
