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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsList;

/**
 * A single line of prepared text in a {@link PLRichText}. Holds the visible segments (each from a
 * single {@link com.helger.pdflayout.richtext.run.PLRichTextRun}) plus the total laid out width.
 * Lines are produced during the prepare phase once the available width is known.
 *
 * @author Philip Helger
 */
final class PLRichTextLine
{
  private final ICommonsList <PLRichTextSegment> m_aSegments;
  private final float m_fWidth;
  private final boolean m_bEndsWithHardBreak;

  PLRichTextLine (@NonNull final ICommonsList <PLRichTextSegment> aSegments,
                  final float fWidth,
                  final boolean bEndsWithHardBreak)
  {
    m_aSegments = aSegments;
    m_fWidth = fWidth;
    m_bEndsWithHardBreak = bEndsWithHardBreak;
  }

  @NonNull
  @ReturnsMutableObject
  ICommonsList <PLRichTextSegment> segments ()
  {
    return m_aSegments;
  }

  float getWidth ()
  {
    return m_fWidth;
  }

  /**
   * @return <code>true</code> if this line was terminated by an explicit <code>\n</code> in the
   *         source (not just word wrap). Last line of the block also returns <code>true</code>.
   */
  boolean isEndsWithHardBreak ()
  {
    return m_bEndsWithHardBreak;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Segments", m_aSegments)
                                       .append ("Width", m_fWidth)
                                       .append ("EndsWithHardBreak", m_bEndsWithHardBreak)
                                       .getToString ();
  }
}
