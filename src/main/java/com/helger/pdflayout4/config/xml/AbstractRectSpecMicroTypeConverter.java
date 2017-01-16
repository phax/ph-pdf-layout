/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import com.helger.pdflayout4.spec.AbstractRectSpec;
import com.helger.pdflayout4.spec.MarginSpec;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for classes based on {@link AbstractRectSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public abstract class AbstractRectSpecMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_TOP = "top";
  private static final String ATTR_RIGHT = "right";
  private static final String ATTR_BOTTOM = "bottom";
  private static final String ATTR_LEFT = "left";

  public final void fillMicroElement (@Nonnull final AbstractRectSpec aValue, @Nonnull final IMicroElement aElement)
  {
    aElement.setAttribute (ATTR_TOP, aValue.getTop ());
    aElement.setAttribute (ATTR_RIGHT, aValue.getRight ());
    aElement.setAttribute (ATTR_BOTTOM, aValue.getBottom ());
    aElement.setAttribute (ATTR_LEFT, aValue.getLeft ());
  }

  @Nonnull
  public AbstractRectSpec convertToRectSpec (@Nonnull final IMicroElement aElement)
  {
    final float fTop = aElement.getAttributeValueAsFloat (ATTR_TOP, Float.NaN);
    final float fRight = aElement.getAttributeValueAsFloat (ATTR_RIGHT, Float.NaN);
    final float fBottom = aElement.getAttributeValueAsFloat (ATTR_BOTTOM, Float.NaN);
    final float fLeft = aElement.getAttributeValueAsFloat (ATTR_LEFT, Float.NaN);
    return new MarginSpec (fTop, fRight, fBottom, fLeft);
  }
}
