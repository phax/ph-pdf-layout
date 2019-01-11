/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.config.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout4.spec.TextAndWidthSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.util.MicroHelper;

/**
 * Micro type converter for class {@link TextAndWidthSpec}.
 *
 * @author Philip Helger
 */
public final class TextAndWidthSpecMicroTypeConverter implements IMicroTypeConverter <TextAndWidthSpec>
{
  private static final String ELEMENT_TEXT = "text";
  private static final String ATTR_WIDTH = "width";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final TextAndWidthSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_WIDTH, aValue.getWidth ());
    aElement.appendElement (sNamespaceURI, ELEMENT_TEXT).appendText (aValue.getText ());
    return aElement;
  }

  @Nonnull
  public TextAndWidthSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sText = MicroHelper.getChildTextContent (aElement, ELEMENT_TEXT);
    final float fWidth = aElement.getAttributeValueAsFloat (ATTR_WIDTH, Float.NaN);

    return new TextAndWidthSpec (sText, fWidth);
  }
}
