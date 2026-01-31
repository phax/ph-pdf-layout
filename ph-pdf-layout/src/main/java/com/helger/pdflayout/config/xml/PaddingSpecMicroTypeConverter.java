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
package com.helger.pdflayout.config.xml;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.pdflayout.spec.PaddingSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Micro type converter for class {@link PaddingSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class PaddingSpecMicroTypeConverter extends AbstractRectSpecMicroTypeConverter <PaddingSpec>
{

  @NonNull
  public IMicroElement convertToMicroElement (@NonNull final PaddingSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
    fillMicroElement (aValue, aElement);
    return aElement;
  }

  @NonNull
  public PaddingSpec convertToNative (@NonNull final IMicroElement aElement)
  {
    return new PaddingSpec (convertToRectSpec (aElement));
  }
}
