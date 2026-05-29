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
package com.helger.pdflayout.richtext.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.IPLElement;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.richtext.run.PLRichTextRun;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Unit tests for {@link PLRichTextBlocks}. These don't render PDFs — they only
 * verify that the markup-to-blocks pipeline produces the expected sequence of
 * elements with the expected prefixes / margins.
 *
 * @author Philip Helger
 */
public final class PLRichTextBlocksTest
{
  private static final PLFontFamily FONT_FAMILY = new PLFontFamily (PreloadFont.REGULAR,
                                                                     PreloadFont.REGULAR_BOLD,
                                                                     PreloadFont.REGULAR_ITALIC,
                                                                     PreloadFont.REGULAR_BOLD_ITALIC);

  private static String _firstRunText (final IPLElement <?> aElement)
  {
    assertTrue ("Expected PLRichText, got " + aElement.getClass ().getName (), aElement instanceof PLRichText);
    final PLRichText aRT = (PLRichText) aElement;
    final ICommonsList <PLRichTextRun> aRuns = aRT.getAllRuns ();
    assertTrue ("Expected at least one run", aRuns.isNotEmpty ());
    return aRuns.get (0).getText ();
  }

  @Test
  public void testPlainMarkupYieldsSinglePLRichText ()
  {
    final ICommonsList <IPLElement <?>> aBlocks = PLRichTextBlocks.parseMarkup ("Hello world",
                                                                                 FONT_FAMILY,
                                                                                 11f,
                                                                                 PLColor.BLACK);
    assertEquals (1, aBlocks.size ());
    assertTrue (aBlocks.get (0) instanceof PLRichText);
    assertEquals ("Hello world", _firstRunText (aBlocks.get (0)));
  }

  @Test
  public void testBulletListYieldsMultipleBlocks ()
  {
    // intro paragraph + 2 bullets + after paragraph = 4 blocks
    final ICommonsList <IPLElement <?>> aBlocks = PLRichTextBlocks.parseMarkup ("intro\n-+one\n-+two\n-!\nafter",
                                                                                 FONT_FAMILY,
                                                                                 11f,
                                                                                 PLColor.BLACK);
    assertEquals (4, aBlocks.size ());
    assertEquals ("intro", _firstRunText (aBlocks.get (0)));
    // bullets prepend "• "
    assertEquals ("• one", _firstRunText (aBlocks.get (1)));
    assertEquals ("• two", _firstRunText (aBlocks.get (2)));
    // after paragraph; the leading blank line is preserved in the buffer so first
    // run starts with the newline.
    assertNotNull (_firstRunText (aBlocks.get (3)));
    assertTrue (_firstRunText (aBlocks.get (3)).endsWith ("after"));
    // Bullet items have a left margin > 0; the intro paragraph does not.
    assertEquals (0f, ((PLRichText) aBlocks.get (0)).getMarginLeft (), 0.0001f);
    assertTrue (((PLRichText) aBlocks.get (1)).getMarginLeft () > 0f);
  }

  @Test
  public void testNumberedRestartAfterReset ()
  {
    final ICommonsList <IPLElement <?>> aBlocks = PLRichTextBlocks.parseMarkup ("-#a\n-#b\n-!\n-#c",
                                                                                 FONT_FAMILY,
                                                                                 11f,
                                                                                 PLColor.BLACK);
    assertEquals (3, aBlocks.size ());
    assertEquals ("1. a", _firstRunText (aBlocks.get (0)));
    assertEquals ("2. b", _firstRunText (aBlocks.get (1)));
    // After -! the counter restarts at 1.
    assertEquals ("1. c", _firstRunText (aBlocks.get (2)));
  }
}
