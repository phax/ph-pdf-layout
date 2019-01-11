/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;

import com.helger.pdflayout4.element.table.PLCellRange;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for class {@link PLCellRange}.
 *
 * @author Philip Helger
 */
public final class PLCellRangeMicroTypeConverter implements IMicroTypeConverter <PLCellRange>
{
  private static final String ATTR_FIRST_ROW = "firstrow";
  private static final String ATTR_LAST_ROW = "lastrow";
  private static final String ATTR_FIRST_COLUMN = "firstcol";
  private static final String ATTR_LAST_COLUMN = "lastcol";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final PLCellRange aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);

    aElement.setAttribute (ATTR_FIRST_ROW, aValue.getFirstRow ());
    aElement.setAttribute (ATTR_LAST_ROW, aValue.getLastRow ());
    aElement.setAttribute (ATTR_FIRST_COLUMN, aValue.getFirstColumn ());
    aElement.setAttribute (ATTR_LAST_COLUMN, aValue.getLastColumn ());
    return aElement;
  }

  @Nonnull
  public PLCellRange convertToNative (@Nonnull final IMicroElement aElement)
  {
    final int nFirstRow = aElement.getAttributeValueAsInt (ATTR_FIRST_ROW, 0);
    final int nLastRow = aElement.getAttributeValueAsInt (ATTR_LAST_ROW, 0);
    final int nFirstColumn = aElement.getAttributeValueAsInt (ATTR_FIRST_COLUMN, 0);
    final int nLastColumn = aElement.getAttributeValueAsInt (ATTR_LAST_COLUMN, 0);
    return new PLCellRange (nFirstRow, nLastRow, nFirstColumn, nLastColumn);
  }
}
