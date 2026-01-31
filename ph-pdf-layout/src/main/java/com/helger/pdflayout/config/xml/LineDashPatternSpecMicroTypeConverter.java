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

import com.helger.base.string.StringParser;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.spec.LineDashPatternSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for class {@link LineDashPatternSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class LineDashPatternSpecMicroTypeConverter implements IMicroTypeConverter <LineDashPatternSpec>
{
  private static final String ATTR_PHASE = "phase";
  private static final String ELEMENT_PATTERN = "pattern";
  private static final String ATTR_ITEM = "item";

  @NonNull
  public IMicroElement convertToMicroElement (@NonNull final LineDashPatternSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_PHASE, aValue.getPhase ());
    for (final float fPattern : aValue.getPattern ())
      aElement.addElementNS (sNamespaceURI, ELEMENT_PATTERN).setAttribute (ATTR_ITEM, fPattern);

    return aElement;
  }

  @NonNull
  public LineDashPatternSpec convertToNative (@NonNull final IMicroElement aElement)
  {
    final float fPhase = StringParser.parseFloat (aElement.getAttributeValue (ATTR_PHASE), Float.NaN);
    final ICommonsList <IMicroElement> aChildren = aElement.getAllChildElements (ELEMENT_PATTERN);
    final float [] aPattern = new float [aChildren.size ()];
    int nIndex = 0;
    for (final IMicroElement ePattern : aChildren)
    {
      aPattern[nIndex] = ePattern.getAttributeValueAsFloat (ATTR_ITEM, Float.NaN);
      if (Float.isNaN (aPattern[nIndex]))
        aPattern[nIndex] = ePattern.getAttributeValueAsFloat ("patternitem", Float.NaN);
      nIndex++;
    }

    return new LineDashPatternSpec (aPattern, fPhase);
  }
}
