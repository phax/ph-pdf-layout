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
package com.plenigo.pdflayout.config;

import com.helger.xml.microdom.convert.IMicroTypeConverterRegistry;
import com.plenigo.pdflayout.config.xml.BorderSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.BorderStyleSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.FontSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.HeightSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.LineDashPatternSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.MarginSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.PLCellRangeMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.PaddingSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.SizeSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.TextAndWidthSpecMicroTypeConverter;
import com.plenigo.pdflayout.config.xml.WidthSpecMicroTypeConverter;
import com.plenigo.pdflayout.element.table.PLCellRange;
import com.plenigo.pdflayout.spec.BorderSpec;
import com.plenigo.pdflayout.spec.BorderStyleSpec;
import com.plenigo.pdflayout.spec.FontSpec;
import com.plenigo.pdflayout.spec.HeightSpec;
import com.plenigo.pdflayout.spec.IPreloadFontResolver;
import com.plenigo.pdflayout.spec.LineDashPatternSpec;
import com.plenigo.pdflayout.spec.MarginSpec;
import com.plenigo.pdflayout.spec.PaddingSpec;
import com.plenigo.pdflayout.spec.SizeSpec;
import com.plenigo.pdflayout.spec.TextAndWidthSpec;
import com.plenigo.pdflayout.spec.WidthSpec;

import javax.annotation.Nonnull;

/**
 * Micro type converter registration for all micro type converter contained in
 * this project. Must be called manually, because an
 * {@link IPreloadFontResolver} is required!
 *
 * @author Philip Helger
 */
public final class PDFMicroTypeConverterRegistry
{
  private PDFMicroTypeConverterRegistry ()
  {}

  public static void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry,
                                                 @Nonnull final IPreloadFontResolver aPreloadFontResolver)
  {
      // Details
      aRegistry.registerMicroElementTypeConverter(BorderSpec.class, new BorderSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(BorderStyleSpec.class, new BorderStyleSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(FontSpec.class, new FontSpecMicroTypeConverter(aPreloadFontResolver));
      aRegistry.registerMicroElementTypeConverter(HeightSpec.class, new HeightSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(LineDashPatternSpec.class, new LineDashPatternSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(MarginSpec.class, new MarginSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(PaddingSpec.class, new PaddingSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(PLCellRange.class, new PLCellRangeMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(SizeSpec.class, new SizeSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(TextAndWidthSpec.class, new TextAndWidthSpecMicroTypeConverter());
      aRegistry.registerMicroElementTypeConverter(WidthSpec.class, new WidthSpecMicroTypeConverter());
  }
}
