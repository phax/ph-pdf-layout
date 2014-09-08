/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents a single row within a VBox. This is a pseudo element
 * and does not have a padding, margin or border!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class PLVBoxRow
{
  private AbstractPLElement <?> m_aElement;

  public PLVBoxRow (@Nonnull final AbstractPLElement <?> aElement)
  {
    setElement (aElement);
  }

  @Nonnull
  public AbstractPLElement <?> getElement ()
  {
    return m_aElement;
  }

  @Nonnull
  PLVBoxRow setElement (@Nonnull final AbstractPLElement <?> aElement)
  {
    m_aElement = ValueEnforcer.notNull (aElement, "Element");
    return this;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("element", m_aElement).toString ();
  }
}
