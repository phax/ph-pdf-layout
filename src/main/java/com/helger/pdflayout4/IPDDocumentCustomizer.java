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
package com.helger.pdflayout4;

import java.io.IOException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Callback interface for PDF customization
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IPDDocumentCustomizer
{
  /**
   * Customize the passed {@link PDDocument}.
   *
   * @param aDoc
   *        The document to be customized. Never <code>null</code>.
   * @throws IOException
   *         in case something goes wrong
   */
  void customizeDocument (@Nonnull PDDocument aDoc) throws IOException;

  /**
   * Invoke this customizer and afterwards the provided customizer.
   *
   * @param aNextCustomizer
   *        The customizer to be invoked after this customizer. May be
   *        <code>null</code>.
   * @return A new, non-<code>null</code> customizer.
   */
  @Nonnull
  @CheckReturnValue
  default IPDDocumentCustomizer and (@Nullable final IPDDocumentCustomizer aNextCustomizer)
  {
    return and (this, aNextCustomizer);
  }

  /**
   * Create a customizer that invokes both customizers if they are
   * non-<code>null</code>.
   *
   * @param aCustomizer1
   *        The first customizer to be invoked. May be <code>null</code>.
   * @param aCustomizer2
   *        The second customizer to be invoked after the first customizer (if
   *        present). May be <code>null</code>.
   * @return <code>null</code> if both parameters are <code>null</code>.
   */
  @Nullable
  @CheckReturnValue
  static IPDDocumentCustomizer and (@Nullable final IPDDocumentCustomizer aCustomizer1, @Nullable final IPDDocumentCustomizer aCustomizer2)
  {
    if (aCustomizer1 != null)
    {
      if (aCustomizer2 != null)
      {
        return aDoc -> {
          aCustomizer1.customizeDocument (aDoc);
          aCustomizer2.customizeDocument (aDoc);
        };
      }
      return aCustomizer1;
    }

    // May be null
    return aCustomizer2;
  }
}
