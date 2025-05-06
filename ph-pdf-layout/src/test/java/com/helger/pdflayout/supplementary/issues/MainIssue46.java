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
package com.helger.pdflayout.supplementary.issues;

import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.helger.font.noto_sans_sc.EFontResourceNotoSansSC;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

public class MainIssue46
{
  public static void main (final String [] args) throws PDFCreationException
  {
    final var scFont = PreloadFont.createEmbedding (EFontResourceNotoSansSC.NOTO_SANS_SC_REGULAR.getFontResource ());
    scFont.setUseFontLineHeightFromHHEA ();

    final var pageSet = new PLPageSet (PDRectangle.A4);
    pageSet.addElement (new PLText ("sc font", new FontSpec (scFont, 10)).setBorder (PLColor.RED));
    pageSet.addElement (new PLText ("built-in font", new FontSpec (PreloadFont.REGULAR, 10)).setBorder (PLColor.RED));
    pageSet.addElement (new PLText ("sc font", new FontSpec (scFont, 10)).setBorder (PLColor.RED));
    pageSet.addElement (new PLText ("built-in font", new FontSpec (PreloadFont.REGULAR, 10)).setBorder (PLColor.RED));

    final var pageLayout = new PageLayoutPDF ();
    pageLayout.addPageSet (pageSet);
    pageLayout.renderTo (new File ("target/issue46.pdf"));
  }
}
