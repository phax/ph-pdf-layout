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
package com.helger.pdflayout.base;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;

/**
 * Contains a list of supported textual placeholders to be replaced when
 * creating a PDF.
 *
 * @author Philip Helger
 */
public enum EPLPlaceholder
{
  /** 0-based index of current pageset */
  PAGESET_INDEX ("${pageset-index}", 1),
  /** 1-based number of current pageset (same as pageset-index+1) */
  PAGESET_NUMBER ("${pageset-number}", 1),
  /** total number of pagesets */
  PAGESET_COUNT ("${pageset-count}", 1),
  /** 0-based index of page in current pageset */
  PAGESET_PAGE_INDEX ("${pageset-page-index}", 2),
  /** 1-based index of page in current pageset */
  PAGESET_PAGE_NUMBER ("${pageset-page-number}", 2),
  /** count of pages in current pageset */
  PAGESET_PAGE_COUNT ("${pageset-page-count}", 2),
  /** overall 0-based page index */
  TOTAL_PAGE_INDEX ("${total-page-index}", 2),
  /** overall 1-based page number */
  TOTAL_PAGE_NUMBER ("${total-page-number}", 2),
  /** total page count */
  TOTAL_PAGE_COUNT ("${total-page-count}", 2);

  private final String m_sVariable;
  private final int m_nEstimatedCharCount;
  private final String m_sEstimatedPrepareText;

  EPLPlaceholder (@NonNull @Nonempty final String sVariable, @Nonnegative final int nEstimatedCharCount)
  {
    m_sVariable = sVariable;
    m_nEstimatedCharCount = nEstimatedCharCount;
    // X is a good match for width of numbers 0..9
    m_sEstimatedPrepareText = StringHelper.getRepeated ('X', nEstimatedCharCount);
  }

  /**
   * @return The name of the variable, starting with "${" and ending with "}".
   *         Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getVariable ()
  {
    return m_sVariable;
  }

  /**
   * @return The number of estimated characters in the final document. Always
   *         &gt; 0.
   */
  @Nonnegative
  public int getEstimatedCharCount ()
  {
    return m_nEstimatedCharCount;
  }

  /**
   * @return The estimated replacement text, using
   *         {@link #getEstimatedCharCount()} as the basis.
   */
  @NonNull
  @Nonempty
  public String getEstimatedPrepareText ()
  {
    return m_sEstimatedPrepareText;
  }

  @Nullable
  public static EPLPlaceholder getFromVariableOrNull (@Nullable final String sVariable)
  {
    return ArrayHelper.findFirst (values (), x -> x.getVariable ().equals (sVariable));
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsMap <String, String> getEstimationReplacements ()
  {
    final ICommonsMap <String, String> ret = new CommonsHashMap <> ();
    for (final EPLPlaceholder e : values ())
      ret.put (e.m_sVariable, e.m_sEstimatedPrepareText);
    return ret;
  }
}
