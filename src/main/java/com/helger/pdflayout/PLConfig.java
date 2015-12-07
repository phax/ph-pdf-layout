/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.ClassPathResource;

@Immutable
public final class PLConfig
{
  /** Project name */
  public static final String PROJECT_NAME = "ph-pdf-layout";
  /** Project URL */
  public static final String PROJECT_URL = "https://github.com/phax/ph-pdf-layout";
  /** Current version - from properties file */
  public static final String PROJECT_VERSION;

  private static final Logger s_aLogger = LoggerFactory.getLogger (PLConfig.class);

  static
  {
    String sProjectVersion = "undefined";
    try
    {
      final Properties p = new Properties ();
      final InputStream aIS = ClassPathResource.getInputStream ("ph-pdf-layout-version.properties");
      if (aIS != null)
      {
        p.load (aIS);
        sProjectVersion = p.getProperty ("version");
      }
    }
    catch (final IOException ex)
    {
      // Project version stays "undefined"
    }
    if (sProjectVersion == null)
      s_aLogger.warn ("Failed to load version number");
    PROJECT_VERSION = sProjectVersion;
  }

  private PLConfig ()
  {}
}
