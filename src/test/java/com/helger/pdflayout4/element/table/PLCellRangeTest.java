/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.xml.mock.XMLTestHelper;

/**
 * Test class for class {@link PLCellRange}.
 *
 * @author Philip Helger
 */
public final class PLCellRangeTest
{
  @Test
  public void testBasic ()
  {
    final PLCellRange aCR = new PLCellRange (0, 1, 2, 3);
    assertEquals (0, aCR.getFirstRow ());
    assertEquals (1, aCR.getLastRow ());
    assertEquals (2, aCR.getRowCount ());
    assertEquals (0, aCR.getMinRow ());
    assertEquals (1, aCR.getMaxRow ());

    assertEquals (2, aCR.getFirstColumn ());
    assertEquals (3, aCR.getLastColumn ());
    assertEquals (2, aCR.getColumnCount ());
    assertEquals (2, aCR.getMinColumn ());
    assertEquals (3, aCR.getMaxColumn ());

    assertEquals (4, aCR.getNumberOfCells ());
    assertEquals (aCR, aCR.getClone ());
    XMLTestHelper.testMicroTypeConversion (aCR);
  }
}
