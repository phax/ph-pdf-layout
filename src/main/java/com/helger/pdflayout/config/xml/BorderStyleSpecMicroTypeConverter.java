/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.convert.MicroTypeConverter;

/**
 * Micro type converter for class {@link BorderStyleSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class BorderStyleSpecMicroTypeConverter implements IMicroTypeConverter <BorderStyleSpec>
{
  private static final String ELEMENT_COLOR = "color";
  private static final String ELEMENT_LINE_DASH_PATTERN = "linedashpattern";
  private static final String ATTR_LINE_WIDTH = "linewidth";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final BorderStyleSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    final Color aColor = aValue.getColor ();
    if (aColor != BorderStyleSpec.DEFAULT_COLOR)
      aElement.appendChild (MicroTypeConverter.convertToMicroElement (aColor, sNamespaceURI, ELEMENT_COLOR));

    final LineDashPatternSpec aLDPSpec = aValue.getLineDashPattern ();
    if (aLDPSpec != BorderStyleSpec.DEFAULT_LINE_DASH_PATTERN)
      aElement.appendChild (MicroTypeConverter.convertToMicroElement (aLDPSpec, sNamespaceURI, ELEMENT_LINE_DASH_PATTERN));

    aElement.setAttribute (ATTR_LINE_WIDTH, aValue.getLineWidth ());

    return aElement;
  }

  @Nonnull
  public BorderStyleSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final Color aColor = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_COLOR),
                                                             Color.class,
                                                             BorderStyleSpec.DEFAULT_COLOR);
    final LineDashPatternSpec aLDPSpec = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_LINE_DASH_PATTERN),
                                                                             LineDashPatternSpec.class,
                                                                             BorderStyleSpec.DEFAULT_LINE_DASH_PATTERN);
    final float fLineWidth = aElement.getAttributeValueAsFloat (ATTR_LINE_WIDTH, BorderStyleSpec.DEFAULT_LINE_WIDTH);
    return new BorderStyleSpec (aColor, aLDPSpec, fLineWidth);
  }
}
