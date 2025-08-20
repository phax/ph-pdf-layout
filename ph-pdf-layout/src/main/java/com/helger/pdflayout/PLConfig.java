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
package com.helger.pdflayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.rt.NonBlockingProperties;
import com.helger.base.rt.PropertiesHelper;
import com.helger.io.resource.ClassPathResource;

/**
 * Contains global configuration constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class PLConfig
{
  /** Project name */
  public static final String PROJECT_NAME = "ph-pdf-layout";
  /** Project URL */
  public static final String PROJECT_URL = "https://github.com/phax/ph-pdf-layout";
  /** Current version - from properties file */
  public static final String PROJECT_VERSION;

  private static final Logger LOGGER = LoggerFactory.getLogger (PLConfig.class);

  static
  {
    String sProjectVersion = null;
    final NonBlockingProperties p = PropertiesHelper.loadProperties (ClassPathResource.getInputStream ("ph-pdf-layout-version.properties",
                                                                                                       PLConfig.class.getClassLoader ()));
    if (p != null)
      sProjectVersion = p.get ("version");
    if (sProjectVersion == null)
    {
      sProjectVersion = "undefined";
      LOGGER.warn ("Failed to load ph-pdf-layout version number");
    }
    PROJECT_VERSION = sProjectVersion;
  }

  private PLConfig ()
  {}
}
