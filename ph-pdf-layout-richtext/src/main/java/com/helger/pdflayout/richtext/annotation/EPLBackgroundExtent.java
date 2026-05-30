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
package com.helger.pdflayout.richtext.annotation;

/**
 * Vertical extent of the background fill rectangle behind a rich-text segment.
 *
 * @author Philip Helger
 */
public enum EPLBackgroundExtent
{
  /**
   * Box sized to the segment's own font (its text height and descent). Sub-/superscript segments
   * follow their shifted baseline, so the highlight tracks the visible glyphs even when their
   * dimensions differ from the rest of the line. This is the HTML
   * <code>&lt;span style="background-color"&gt;</code> look.
   */
  TIGHT,
  /**
   * Box sized to the full line slot of the enclosing rich-text element. All segments on a line
   * share identical Y bounds — adjacent segments meet without seams and the background extends
   * into the line-spacing gap so consecutive lines of a multi-line highlight stay visually
   * contiguous (Word "highlight" look).
   */
  LINE_HEIGHT
}
