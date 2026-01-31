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

/**
 * Define the split result type
 *
 * @author Philip Helger
 * @since 7.4.0
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
