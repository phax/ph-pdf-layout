/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.config.xml;

import com.helger.commons.ValueEnforcer;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.plenigo.pdflayout.spec.FontSpec;
import com.plenigo.pdflayout.spec.IPreloadFontResolver;
import com.plenigo.pdflayout.spec.PreloadFont;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Micro type converter for class {@link FontSpec}.
 *
 * @author Saskia Reimerth
 * @author Philip Helger
 */
public final class FontSpecMicroTypeConverter implements IMicroTypeConverter <FontSpec>
{
  private static final String ATTR_PRELOAD_FONT_ID = "preloadfontid";
  private static final String ATTR_FONT_SIZE = "fontsize";
  private static final String ELEMENT_COLOR = "color";

  private final IPreloadFontResolver m_aPreloadFontResolver;

  public FontSpecMicroTypeConverter (@Nonnull final IPreloadFontResolver aPreloadFontResolver)
  {
    m_aPreloadFontResolver = ValueEnforcer.notNull (aPreloadFontResolver, "PreloadFontResolver");
  }

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final FontSpec aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_PRELOAD_FONT_ID, aValue.getPreloadFontID ());
    aElement.setAttribute (ATTR_FONT_SIZE, aValue.getFontSize ());

    final Color aColor = aValue.getColor ();
    if (aColor != FontSpec.DEFAULT_COLOR)
      aElement.appendChild (MicroTypeConverter.convertToMicroElement (aColor, sNamespaceURI, ELEMENT_COLOR));
    return aElement;
  }

  @Nonnull
  public FontSpec convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sPreloadFontID = aElement.getAttributeValue (ATTR_PRELOAD_FONT_ID);
    final PreloadFont aPreloadFont = m_aPreloadFontResolver.getPreloadFontOfID (sPreloadFontID);
    if (aPreloadFont == null)
      throw new IllegalStateException ("Failed to resolve preloadfont with ID '" + sPreloadFontID + "!");

    final float fFontSize = aElement.getAttributeValueAsFloat (ATTR_FONT_SIZE, Float.NaN);

    final Color aColor = MicroTypeConverter.convertToNative (aElement.getFirstChildElement (ELEMENT_COLOR),
                                                             Color.class,
                                                             FontSpec.DEFAULT_COLOR);
    return new FontSpec (aPreloadFont, fFontSize, aColor);
  }
}
