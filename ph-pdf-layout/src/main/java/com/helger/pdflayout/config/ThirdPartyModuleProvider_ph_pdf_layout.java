/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.config;

import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.thirdparty.ELicense;
import com.helger.commons.thirdparty.IThirdPartyModule;
import com.helger.commons.thirdparty.IThirdPartyModuleProviderSPI;
import com.helger.commons.thirdparty.ThirdPartyModule;
import com.helger.commons.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party modules.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_ph_pdf_layout implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule PDFBOX = new ThirdPartyModule ("Apache PDFBox",
                                                                       "Apache",
                                                                       ELicense.APACHE2,
                                                                       new Version (3, 0, 5),
                                                                       "http://pdfbox.apache.org/");
  public static final IThirdPartyModule JBIG_IMAGEIO = new ThirdPartyModule ("Apache PDFBox ImageIO",
                                                                             "Apache",
                                                                             ELicense.APACHE2,
                                                                             new Version (3, 0, 4),
                                                                             "http://pdfbox.apache.org/");

  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { PDFBOX, JBIG_IMAGEIO };
  }
}
