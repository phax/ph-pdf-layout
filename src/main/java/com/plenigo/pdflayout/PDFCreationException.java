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
package com.plenigo.pdflayout;

import javax.annotation.Nonnull;

/**
 * For all exceptions occurring during PDF creation.
 *
 * @author Philip Helger
 */
public class PDFCreationException extends Exception
{
  public PDFCreationException (@Nonnull final String sMsg)
  {
    super (sMsg);
  }

  public PDFCreationException (@Nonnull final String sMsg, @Nonnull final Throwable aCause)
  {
    super (sMsg, aCause);
  }
}
