/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.spec.WidthSpec;
import com.helger.pdflayout.spec.WidthSpec.EWidthType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for class {@link WidthSpec}.
 *
 * @author Philip Helger
 */
public final class WidthSpecMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_TYPE = "type";
  private static final String ATTR_VALUE = "value";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final WidthSpec aValue = (WidthSpec) aObject;
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_TYPE, aValue.getTypeID ());
    aElement.setAttribute (ATTR_VALUE, aValue.getValue ());
    return aElement;
  }

  @Nonnull
  public WidthSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sTypeID = aElement.getAttributeValue (ATTR_TYPE);
    final EWidthType eWidthType = EWidthType.getFromIDOrNull (sTypeID);
    if (eWidthType == null)
      throw new IllegalStateException ("Failed to resolve width type with ID '" + sTypeID + "!");

    final float fValue = aElement.getAttributeValueAsFloat (ATTR_VALUE, Float.NaN);
    return new WidthSpec (eWidthType, fValue);
  }
}
