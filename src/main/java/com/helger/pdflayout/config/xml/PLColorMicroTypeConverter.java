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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringParser;
import com.helger.pdflayout.base.PLColor;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for class {@link PLColor}.
 *
 * @author Philip Helger
 * @since 7.2.0
 */
public final class PLColorMicroTypeConverter implements IMicroTypeConverter <PLColor>
{
  private static final String ATTR_RED = "red";
  private static final String ATTR_GREEN = "green";
  private static final String ATTR_BLUE = "blue";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final PLColor aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_RED, aObject.getRed ());
    aElement.setAttribute (ATTR_GREEN, aObject.getGreen ());
    aElement.setAttribute (ATTR_BLUE, aObject.getBlue ());

    return aElement;
  }

  @Nonnull
  public PLColor convertToNative (@Nonnull final IMicroElement aElement)
  {
    final int nRed = StringParser.parseInt (aElement.getAttributeValue (ATTR_RED), 0);
    final int nGreen = StringParser.parseInt (aElement.getAttributeValue (ATTR_GREEN), 0);
    final int nBlue = StringParser.parseInt (aElement.getAttributeValue (ATTR_BLUE), 0);
    return new PLColor (nRed, nGreen, nBlue);
  }
}
