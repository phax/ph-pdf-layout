/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.richtext.run;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.richtext.annotation.PLStrikethroughAnnotation;

/**
 * Tests {@link PLRichTextRunBuilder}.
 *
 * @author Philip Helger
 */
public final class PLRichTextRunBuilderTest
{
  @Test
  public void testStrikethroughOnlyCoversTheMarkedText ()
  {
    final ICommonsList <PLRichTextRun> aRuns = new PLRichTextRunBuilder (PLFontFamily.regular (), 12f).buildFromMarkup ("keep ~~gone~~ keep");
    assertEquals (3, aRuns.size ());
    assertNull (aRuns.get (0).getStrikethrough ());
    assertNotNull (aRuns.get (1).getStrikethrough ());
    assertEquals (PLStrikethroughAnnotation.DEFAULT_BASELINE_OFFSET_SCALE,
                  aRuns.get (1).getStrikethrough ().getBaselineOffsetScale (),
                  0.0001f);
    assertNull (aRuns.get (2).getStrikethrough ());
  }

  @Test
  public void testStrikethroughCombinesWithOtherStyles ()
  {
    final ICommonsList <PLRichTextRun> aRuns = new PLRichTextRunBuilder (PLFontFamily.regular (), 12f).buildFromMarkup ("~~**bold** and __under__~~");
    assertEquals (3, aRuns.size ());
    for (final PLRichTextRun aRun : aRuns)
      assertNotNull (aRun.getStrikethrough ());
    assertNotNull (aRuns.get (2).getUnderline ());
    assertNull (aRuns.get (0).getUnderline ());
  }
}
