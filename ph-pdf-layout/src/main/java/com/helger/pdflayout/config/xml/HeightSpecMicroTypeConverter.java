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

import com.helger.pdflayout.spec.EValueUOMType;
import com.helger.pdflayout.spec.HeightSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Micro type converter for class {@link HeightSpec}.
 *
 * @author Philip Helger
 */
public final class HeightSpecMicroTypeConverter implements IMicroTypeConverter <HeightSpec>
{
  private static final String ATTR_TYPE = "type";
  private static final String ATTR_VALUE = "value";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final HeightSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_TYPE, aValue.getTypeID ());
    if (aValue.isAbsolute ())
      aElement.setAttribute (ATTR_VALUE, aValue.getValue ());
    return aElement;
  }

  @Nonnull
  public HeightSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sTypeID = aElement.getAttributeValue (ATTR_TYPE);
    final EValueUOMType eHeightType = EValueUOMType.getFromIDOrNull (sTypeID);
    if (eHeightType == null)
      throw new IllegalStateException ("Failed to resolve height type with ID '" + sTypeID + "!");

    final float fValue = eHeightType.isValueRequired () ? aElement.getAttributeValueAsFloat (ATTR_VALUE, Float.NaN) : 0;
    return new HeightSpec (eHeightType, fValue);
  }
}
