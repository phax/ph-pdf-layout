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
package com.helger.pdflayout.supplementary.issues;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.box.PLBox;
import com.helger.pdflayout.element.table.EPLTableGridType;
import com.helger.pdflayout.element.table.PLTable;
import com.helger.pdflayout.element.table.PLTableCell;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

public class MainIssue34
{
  public static void main (final String [] args) throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);
    final FontSpec r13 = new FontSpec (PreloadFont.REGULAR, 13);
    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4);

    final PLTable aTable = PLTable.createWithEvenlySizedColumns (4);
    // header
    aTable.addRow (new PLTableCell (new PLText ("Col1", r13)),
                   new PLTableCell (new PLText ("Col2", r13)),
                   new PLTableCell (new PLText ("Col3", r13)),
                   new PLTableCell (new PLText ("Col4", r13)));

    // body
    final String s = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.";

    aTable.addRow (new PLTableCell (new PLText (s, r10)),
                   new PLTableCell (new PLText (s, r10)).setMaxHeight (65),
                   new PLTableCell (new PLText (s, r10)).setMaxHeight (75).setClipContent (true),
                   new PLTableCell (new PLText (s, r10)));

    EPLTableGridType.FULL.applyGridToTable (aTable, new BorderStyleSpec (PLColor.RED));

    aPS1.addElement (aTable);

    aPS1.addElement (new PLBox (new PLText (s, r10)).setMaxHeight (17)
                                                    .setBorder (new BorderStyleSpec (PLColor.GREEN))
                                                    .setClipContent (true));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setCompressPDF (false);
    aPageLayout.addPageSet (aPS1);

    aPageLayout.renderTo (new File ("target/issue34.pdf"));
  }

}
