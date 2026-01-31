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

import com.helger.pdflayout.spec.TextAndWidthSpec;
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
  private static final String ATTR_DISPLAY_AS_NEWLINE = "newline";

  @NonNull
  public IMicroElement convertToMicroElement (@NonNull final TextAndWidthSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.addElementNS (sNamespaceURI, ELEMENT_TEXT).addText (aValue.getText ());
    aElement.setAttribute (ATTR_WIDTH, aValue.getWidth ());
    aElement.setAttribute (ATTR_DISPLAY_AS_NEWLINE, aValue.isDisplayAsNewline ());
    return aElement;
  }

  @NonNull
  public TextAndWidthSpec convertToNative (@NonNull final IMicroElement aElement)
  {
    final String sText = MicroHelper.getChildTextContent (aElement, ELEMENT_TEXT);
    final float fWidth = aElement.getAttributeValueAsFloat (ATTR_WIDTH, Float.NaN);
    final boolean bDisplayAsNewline = aElement.getAttributeValueAsBool (ATTR_DISPLAY_AS_NEWLINE, false);

    return new TextAndWidthSpec (sText, fWidth, bDisplayAsNewline);
  }
}
