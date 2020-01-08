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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.font.api.IFontResource;
import com.helger.font.lato2.EFontResourceLato2;

/**
 * Test class for class {@link PreloadFontManager}.
 *
 * @author Philip Helger
 */
public final class PreloadFontManagerTest
{
  @Test
  public void testInit ()
  {
    final PreloadFontManager aMgr = new PreloadFontManager ();
    assertNull (aMgr.getPreloadFontOfID ((IFontResource) null));
    assertNull (aMgr.getPreloadFontOfID ((String) null));
    assertNull (aMgr.getPreloadFontOfID ("abc"));
    assertSame (PreloadFont.MONOSPACE, aMgr.getPreloadFontOfID (PreloadFont.MONOSPACE.getID ()));
    assertNotNull (aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK));
    assertSame (aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK),
                aMgr.getOrAddEmbeddingPreloadFont (EFontResourceLato2.LATO2_BLACK));
  }
}
