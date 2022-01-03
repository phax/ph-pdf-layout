/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.base;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents the result of splitting as defined in
 * {@link IPLSplittableObject}.
 *
 * @author Philip Helger
 */
@Immutable
public class PLSplitResult
{
  private final PLElementWithSize m_aFirstElement;
  private final PLElementWithSize m_aSecondElement;

  public PLSplitResult (@Nonnull final PLElementWithSize aFirstElement, @Nonnull final PLElementWithSize aSecondElement)
  {
    ValueEnforcer.notNull (aFirstElement, "FirstElement");
    ValueEnforcer.notNull (aSecondElement, "SecondElement");
    m_aFirstElement = aFirstElement;
    m_aSecondElement = aSecondElement;
  }

  @Nonnull
  public PLElementWithSize getFirstElement ()
  {
    return m_aFirstElement;
  }

  @Nonnull
  public PLElementWithSize getSecondElement ()
  {
    return m_aSecondElement;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("firstElement", m_aFirstElement).append ("secondElement", m_aSecondElement).getToString ();
  }
}
