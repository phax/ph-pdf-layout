/**
 * Copyright (C) 2012-2016 winenet GmbH - www.winenet.at
 * All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is
 * strictly prohibited.
 */
package com.helger.pdflayout4.config;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.pdflayout4.spec.PreloadFontManager;
import com.helger.xml.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.helger.xml.microdom.convert.IMicroTypeConverterRegistry;

/**
 * Mock SPI implementation
 * 
 * @author Philip Helger
 */
@IsSPIImplementation
public final class MockPDFLayoutMicroTypeConverterRegistrySPI implements IMicroTypeConverterRegistrarSPI
{
  public static final PreloadFontManager PRELOAD_FONT_MANAGER = new PreloadFontManager ();

  @Override
  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry)
  {
    PDFMicroTypeConverterRegistry.registerMicroTypeConverter (aRegistry, PRELOAD_FONT_MANAGER);
  }
}
