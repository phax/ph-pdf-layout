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
package com.helger.pdflayout.config.xml;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringParser;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for class {@link Color}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class ColorMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_RED = "red";
  private static final String ATTR_GREEN = "green";
  private static final String ATTR_BLUE = "blue";
  private static final String ATTR_ALPHA = "alpha";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final Color aValue = (Color) aObject;
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_RED, aValue.getRed ());
    aElement.setAttribute (ATTR_GREEN, aValue.getGreen ());
    aElement.setAttribute (ATTR_BLUE, aValue.getBlue ());
    aElement.setAttribute (ATTR_ALPHA, aValue.getAlpha ());

    return aElement;
  }

  @Nonnull
  public Color convertToNative (@Nonnull final IMicroElement aElement)
  {
    final int nRed = StringParser.parseInt (aElement.getAttributeValue (ATTR_RED), 0);
    final int nGreen = StringParser.parseInt (aElement.getAttributeValue (ATTR_GREEN), 0);
    final int nBlue = StringParser.parseInt (aElement.getAttributeValue (ATTR_BLUE), 0);
    final int nAlpha = StringParser.parseInt (aElement.getAttributeValue (ATTR_ALPHA), 0xff);
    return new Color (nRed, nGreen, nBlue, nAlpha);
  }
}
