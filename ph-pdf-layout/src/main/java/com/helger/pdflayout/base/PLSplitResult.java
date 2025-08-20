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

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class represents the result of splitting as defined in {@link IPLSplittableObject}.
 *
 * @author Philip Helger
 */
@Immutable
public class PLSplitResult
{
  private final EPLSplitResultType m_eSplitResultType;
  private final PLElementWithSize m_aFirstElement;
  private final PLElementWithSize m_aSecondElement;

  private PLSplitResult (@Nonnull final EPLSplitResultType eSplitResultType,
                         @Nullable final PLElementWithSize aFirstElement,
                         @Nullable final PLElementWithSize aSecondElement)
  {
    ValueEnforcer.notNull (eSplitResultType, "SplitResultType");
    m_eSplitResultType = eSplitResultType;
    m_aFirstElement = aFirstElement;
    m_aSecondElement = aSecondElement;
  }

  @Nonnull
  public EPLSplitResultType getSplitResultType ()
  {
    return m_eSplitResultType;
  }

  @Nullable
  public PLElementWithSize getFirstElement ()
  {
    return m_aFirstElement;
  }

  @Nullable
  public PLElementWithSize getSecondElement ()
  {
    return m_aSecondElement;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("FirstElement", m_aFirstElement)
                                       .append ("SecondElement", m_aSecondElement)
                                       .getToString ();
  }

  @Nonnull
  public static PLSplitResult createSplit (@Nonnull final PLElementWithSize aFirstElement,
                                      @Nonnull final PLElementWithSize aSecondElement)
  {
    ValueEnforcer.notNull (aFirstElement, "FirstElement");
    ValueEnforcer.notNull (aSecondElement, "SecondElement");
    return new PLSplitResult (EPLSplitResultType.SPLIT_SUCCESS, aFirstElement, aSecondElement);
  }

  @Nonnull
  public static PLSplitResult allOnFirst ()
  {
    return new PLSplitResult (EPLSplitResultType.SPLIT_ALL_ON_FIRST, null, null);
  }

  @Nonnull
  public static PLSplitResult allOnSecond ()
  {
    return new PLSplitResult (EPLSplitResultType.SPLIT_ALL_ON_SECOND, null, null);
  }
}
