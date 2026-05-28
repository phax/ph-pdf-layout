/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for split fragment metadata on {@link AbstractPLObject}
 * ({@link IPLObject#getOriginalID()}, {@link IPLObject#isFirstFragment()},
 * {@link IPLObject#isSplitFragment()}).
 *
 * @author Philip Helger
 */
public final class SplitFragmentMetadataTest
{
  private static final FontSpec FONT = new FontSpec (PreloadFont.REGULAR, 10);

  @Test
  public void testUnsplitDefaults ()
  {
    final PLText aOriginal = new PLText ("hello", FONT).setID ("intro");
    assertEquals ("intro", aOriginal.getID ());
    assertEquals ("intro", aOriginal.getOriginalID ());
    assertFalse (aOriginal.isSplitFragment ());
    assertTrue (aOriginal.isFirstFragment ());
  }

  @Test
  public void testSingleSplit ()
  {
    final PLText aOriginal = new PLText ("hello", FONT).setID ("intro");

    final PLText aFrag1 = new PLText ("hello", FONT);
    aFrag1.internalMarkAsSplitFragment (aOriginal, true, "-1");

    final PLText aFrag2 = new PLText ("hello", FONT);
    aFrag2.internalMarkAsSplitFragment (aOriginal, false, "-2");

    assertEquals ("intro-1", aFrag1.getID ());
    assertEquals ("intro", aFrag1.getOriginalID ());
    assertTrue (aFrag1.isSplitFragment ());
    assertTrue (aFrag1.isFirstFragment ());

    assertEquals ("intro-2", aFrag2.getID ());
    assertEquals ("intro", aFrag2.getOriginalID ());
    assertTrue (aFrag2.isSplitFragment ());
    assertFalse (aFrag2.isFirstFragment ());
  }

  @Test
  public void testRecursiveSplitPropagatesOriginalAndFirstFragment ()
  {
    final PLText aOriginal = new PLText ("hello", FONT).setID ("body");

    // First split: body -> body-1, body-2
    final PLText aFrag1 = new PLText ("hello", FONT);
    aFrag1.internalMarkAsSplitFragment (aOriginal, true, "-1");
    final PLText aFrag2 = new PLText ("hello", FONT);
    aFrag2.internalMarkAsSplitFragment (aOriginal, false, "-2");

    // Second split of the second fragment: body-2 -> body-2-1, body-2-2
    final PLText aFrag2a = new PLText ("hello", FONT);
    aFrag2a.internalMarkAsSplitFragment (aFrag2, true, "-1");
    final PLText aFrag2b = new PLText ("hello", FONT);
    aFrag2b.internalMarkAsSplitFragment (aFrag2, false, "-2");

    // IDs cascade
    assertEquals ("body-2-1", aFrag2a.getID ());
    assertEquals ("body-2-2", aFrag2b.getID ());

    // OriginalID walks back to the root in one hop, not just to the parent
    assertEquals ("body", aFrag2a.getOriginalID ());
    assertEquals ("body", aFrag2b.getOriginalID ());

    // body-2-1 has suffix -1 but is NOT the first fragment of the original,
    // because its parent body-2 was already a second fragment.
    assertFalse ("body-2-1 ends in -1 but is not the first fragment of 'body'", aFrag2a.isFirstFragment ());
    assertFalse (aFrag2b.isFirstFragment ());

    // Third split, this time of the first-fragment-of-first-fragment.
    final PLText aFrag1a = new PLText ("hello", FONT);
    aFrag1a.internalMarkAsSplitFragment (aFrag1, true, "-1");
    final PLText aFrag1b = new PLText ("hello", FONT);
    aFrag1b.internalMarkAsSplitFragment (aFrag1, false, "-2");

    assertEquals ("body-1-1", aFrag1a.getID ());
    assertEquals ("body", aFrag1a.getOriginalID ());
    assertTrue ("body-1-1 remains a first fragment because every ancestor was first", aFrag1a.isFirstFragment ());

    assertEquals ("body-1-2", aFrag1b.getID ());
    assertEquals ("body", aFrag1b.getOriginalID ());
    assertFalse (aFrag1b.isFirstFragment ());
  }

  @Test
  public void testOriginalIdentityUnchangedAfterChildrenSplit ()
  {
    final PLText aOriginal = new PLText ("hello", FONT).setID ("body");
    final String sOriginalIDBefore = aOriginal.getID ();
    final boolean bIsSplitBefore = aOriginal.isSplitFragment ();
    final boolean bIsFirstBefore = aOriginal.isFirstFragment ();

    final PLText aFrag = new PLText ("hello", FONT);
    aFrag.internalMarkAsSplitFragment (aOriginal, true, "-1");

    // Marking a fragment must not mutate the source.
    assertSame (sOriginalIDBefore, aOriginal.getID ());
    assertTrue (bIsSplitBefore == aOriginal.isSplitFragment ());
    assertTrue (bIsFirstBefore == aOriginal.isFirstFragment ());
  }
}
