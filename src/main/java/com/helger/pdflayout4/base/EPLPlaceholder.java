package com.helger.pdflayout4.base;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.string.StringHelper;

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
  private final String m_sPrepareText;

  private EPLPlaceholder (@Nonnull @Nonempty final String sVariable, @Nonnegative final int nEstimatedCharCount)
  {
    m_sVariable = sVariable;
    m_nEstimatedCharCount = nEstimatedCharCount;
    m_sPrepareText = StringHelper.getRepeated ('X', nEstimatedCharCount);
  }

  @Nonnull
  @Nonempty
  public String getVariable ()
  {
    return m_sVariable;
  }

  @Nonnegative
  public int getEstimatedCharCount ()
  {
    return m_nEstimatedCharCount;
  }

  @Nonnull
  @Nonempty
  public String getPrepareText ()
  {
    return m_sPrepareText;
  }

  @Nullable
  public static EPLPlaceholder getFromVariableOrNull (@Nullable final String sVariable)
  {
    return ArrayHelper.findFirst (values (), x -> x.getVariable ().equals (sVariable));
  }
}
