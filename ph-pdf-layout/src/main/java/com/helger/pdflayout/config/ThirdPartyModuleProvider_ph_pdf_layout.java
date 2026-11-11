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
package com.helger.pdflayout.config;

import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.base.thirdparty.ELicense;
import com.helger.base.thirdparty.IThirdPartyModule;
import com.helger.base.thirdparty.IThirdPartyModuleProviderSPI;
import com.helger.base.thirdparty.ThirdPartyModule;
import com.helger.base.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party modules.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_ph_pdf_layout implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule PDFBOX = ThirdPartyModule.builder ()
                                                                 .displayName ("Apache PDFBox")
                                                                 .copyrightOwner ("Apache")
                                                                 .license (ELicense.APACHE2)
                                                                 .version (new Version (3, 0, 8))
                                                                 .webSiteUrl ("http://pdfbox.apache.org/")
                                                                 .build ();
  public static final IThirdPartyModule JBIG_IMAGEIO = ThirdPartyModule.builder ()
                                                                       .displayName ("Apache PDFBox ImageIO")
                                                                       .copyrightOwner ("Apache")
                                                                       .license (ELicense.APACHE2)
                                                                       .version (new Version (3, 0, 5))
                                                                       .webSiteUrl ("http://pdfbox.apache.org/")
                                                                       .build ();

  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { PDFBOX, JBIG_IMAGEIO };
  }
}
