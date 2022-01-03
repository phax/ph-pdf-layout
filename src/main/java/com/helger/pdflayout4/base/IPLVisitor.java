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

import java.io.IOException;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.functional.IThrowingFunction;
import com.helger.commons.state.EChange;

/**
 * Visitor callback
 *
 * @author Philip Helger
 */
public interface IPLVisitor
{
  /**
   * Call on page set start
   *
   * @param aPageSet
   *        The current page set. Never <code>null</code>.
   * @throws IOException
   *         on PDFBox error
   */
  default void onPageSetStart (@Nonnull final PLPageSet aPageSet) throws IOException
  {}

  /**
   * Call for each element in the current page set. This method is also called
   * for page set header and footer elements.
   *
   * @param aElement
   *        The current element. Never <code>null</code>.
   * @return {@link EChange#CHANGED} if the object was modified.
   * @throws IOException
   *         on PDFBox error
   */
  @Nonnull
  default EChange onElement (@Nonnull final IPLRenderableObject <?> aElement) throws IOException
  {
    return EChange.UNCHANGED;
  }

  /**
   * Call on page set end
   *
   * @param aPageSet
   *        The current page set.
   * @throws IOException
   *         on PDFBox error
   */
  default void onPageSetEnd (@Nonnull final PLPageSet aPageSet) throws IOException
  {}

  /**
   * Special visitor method that visits only elements of this objects and
   * ignores the others objects.
   *
   * @param aElementConsumer
   *        The consumer to use. May not be <code>null</code>.
   * @return The new element visitor to use
   */
  @Nonnull
  static IPLVisitor createElementVisitor (@Nonnull final IThrowingFunction <? super IPLRenderableObject <?>, EChange, IOException> aElementConsumer)
  {
    ValueEnforcer.notNull (aElementConsumer, "ElementConsumer");
    return new IPLVisitor ()
    {
      @Override
      @Nonnull
      public EChange onElement (@Nonnull final IPLRenderableObject <?> aElement) throws IOException
      {
        return aElementConsumer.apply (aElement);
      }
    };
  }
}
