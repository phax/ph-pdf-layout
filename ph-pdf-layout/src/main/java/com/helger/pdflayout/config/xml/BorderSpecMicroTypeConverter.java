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
package com.helger.pdflayout.config.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.convert.MicroTypeConverter;

/**
 * Micro type converter for class {@link BorderSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class BorderSpecMicroTypeConverter implements IMicroTypeConverter <BorderSpec>
{
  private static final String ELEMENT_TOP = "top";
  private static final String ELEMENT_RIGHT = "right";
  private static final String ELEMENT_BOTTOM = "bottom";
  private static final String ELEMENT_LEFT = "left";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final BorderSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.appendChild (MicroTypeConverter.convertToMicroElement (aValue.getTop (), sNamespaceURI, ELEMENT_TOP));
    aElement.appendChild (MicroTypeConverter.convertToMicroElement (aValue.getRight (), sNamespaceURI, ELEMENT_RIGHT));
    aElement.appendChild (MicroTypeConverter.convertToMicroElement (aValue.getBottom (), sNamespaceURI, ELEMENT_BOTTOM));
    aElement.appendChild (MicroTypeConverter.convertToMicroElement (aValue.getLeft (), sNamespaceURI, ELEMENT_LEFT));
    return aElement;
  }

  @Nonnull
  public BorderSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final BorderStyleSpec aTop = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_TOP), BorderStyleSpec.class);
    final BorderStyleSpec aRight = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_RIGHT),
                                                                       BorderStyleSpec.class);
    final BorderStyleSpec aBottom = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_BOTTOM),
                                                                        BorderStyleSpec.class);
    final BorderStyleSpec aLeft = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_LEFT), BorderStyleSpec.class);
    return new BorderSpec (aTop, aRight, aBottom, aLeft);
  }
}
