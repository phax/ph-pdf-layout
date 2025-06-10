package com.helger.pdflayout.base;

/**
 * Define the split result type
 *
 * @author Philip Helger
 * @since 7.3.8
 */
public enum EPLSplitResultType
{
  /**
   * Splitting was performed. Some part is on the first page and some part is on the second page.
   */
  SPLIT_SUCCESS,
  /**
   * Splitting is not necessary, because the source object fits into the existing boundaries of the
   * first page
   */
  SPLIT_ALL_ON_FIRST,
  /**
   * Splitting is not possible, because no part would stay on the first page and everything ends up
   * on the second page
   */
  SPLIT_ALL_ON_SECOND;

  public boolean isSplit ()
  {
    return this == SPLIT_SUCCESS;
  }

  public boolean isAllOnFirst ()
  {
    return this == SPLIT_ALL_ON_FIRST;
  }

  public boolean isAllOnSecond ()
  {
    return this == SPLIT_ALL_ON_SECOND;
  }
}
