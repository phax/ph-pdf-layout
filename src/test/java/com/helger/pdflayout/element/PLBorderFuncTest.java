/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import java.awt.Color;
import java.io.File;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.element.hbox.PLHBox;
import com.helger.pdflayout.element.special.PLSpacerY;
import com.helger.pdflayout.element.text.PLText;
import com.helger.pdflayout.element.vbox.PLVBox;
import com.helger.pdflayout.spec.BorderSpec;
import com.helger.pdflayout.spec.BorderStyleSpec;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;
import com.helger.pdflayout.spec.WidthSpec;

/**
 * TEst class for {@link PLImage}
 *
 * @author Philip Helger
 */
public final class PLBorderFuncTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testDifferentBordersText () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 20f;
    final float fSpacer = 10f;
    final BorderStyleSpec aTop = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aRight = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aBottom = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aLeft = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aAll = new BorderStyleSpec (Color.GRAY, fValue);

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setID ("pageset")
                                                         .setMargin (fValue)
                                                         .setFillColor (Color.GREEN)
                                                         .setBorder (new BorderSpec (aAll));
    aPS1.addElement (new PLText ("Border top", r10).setID ("top").setBorderTop (aTop));
    aPS1.addElement (new PLText ("Border right", r10).setID ("right").setBorderRight (aRight));
    aPS1.addElement (new PLText ("Border bottom", r10).setID ("bottom").setBorderBottom (aBottom));
    aPS1.addElement (new PLText ("Border left", r10).setID ("left").setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top left", r10).setID ("top left").setBorderTop (aTop).setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right", r10).setID ("top right")
                                                         .setBorderTop (aTop)
                                                         .setBorderRight (aRight));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom right", r10).setID ("bottom right")
                                                            .setBorderBottom (aBottom)
                                                            .setBorderRight (aRight));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border bottom left", r10).setID ("bottom left")
                                                           .setBorderBottom (aBottom)
                                                           .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right left", r10).setID ("top right left")
                                                              .setBorderTop (aTop)
                                                              .setBorderRight (aRight)
                                                              .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border right bottom left", r10).setID ("right bottom left")
                                                                 .setBorderRight (aRight)
                                                                 .setBorderBottom (aBottom)
                                                                 .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (10).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right bottom", r10).setID ("top right bottom")
                                                                .setBorderTop (aTop)
                                                                .setBorderRight (aRight)
                                                                .setBorderBottom (aBottom));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top bottom left", r10).setID ("top bottom left")
                                                               .setBorderTop (aTop)
                                                               .setBorderBottom (aBottom)
                                                               .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border top right bottom left", r10).setID ("top right bottom left")
                                                                     .setBorderTop (aTop)
                                                                     .setBorderRight (aRight)
                                                                     .setBorderBottom (aBottom)
                                                                     .setBorderLeft (aLeft));
    aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
    aPS1.addElement (new PLText ("Border all", r10).setID ("all").setBorder (aAll));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getBufferedOutputStream (new File ("pdf/test-border-text.pdf")));
  }

  @Test
  public void testDifferentBordersHBox () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 20f;
    final float fSpacer = 10f;
    final BorderStyleSpec aTop = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aRight = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aBottom = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aLeft = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aAll = new BorderStyleSpec (Color.GRAY, fValue);

    // Use simple numbers - approx. A4
    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (500,
                                                           800)).setID ("pageset")
                                                                .setMargin (fValue)
                                                                .setFillColor (Color.GREEN)
                                                                .setBorder (new BorderSpec (new BorderStyleSpec (Color.PINK,
                                                                                                                 fValue)));

    // Works!
    if (true)
    {
      // Border around the box
      aPS1.addElement (new PLText ("Border around the box:", r10).setID ("text")
                                                                 .setPadding (fValue, 0)
                                                                 .setFillColor (Color.WHITE));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox1a").setBorderTop (aTop);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox2a").setBorderRight (aRight);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox3a").setBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox4a").setBorderLeft (aLeft);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox5a").setBorder (aAll);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox6a").setBorderLeft (aLeft).setBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox7a").setBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aHBox.addColumn (new PLText ("Column" + i, r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
    }

    // Works!
    if (true)
    {
      // Using column border
      aPS1.addElement (new PLText ("Using column border:", r10).setID ("text")
                                                               .setPadding (fValue, 0)
                                                               .setFillColor (Color.WHITE));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox1b").setColumnBorderTop (aTop);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox2b").setColumnBorderRight (aRight);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox3b").setColumnBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox4b").setColumnBorderLeft (aLeft);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox5b").setColumnBorder (aAll);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox6b").setColumnBorderLeft (aLeft).setColumnBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox7b").setColumnBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aHBox.addColumn (new PLText ("Column" + i, r10), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
    }

    // Works!
    if (true)
    {
      // Using border around column elements
      aPS1.addElement (new PLText ("Using border around column elements:", r10).setID ("text")
                                                                               .setPadding (fValue, 0)
                                                                               .setFillColor (Color.WHITE));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox1c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorderTop (aTop), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderTop (aTop), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox2c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorderRight (aRight), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderRight (aRight), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox3c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorderBottom (aBottom), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderBottom (aBottom), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox4c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorderLeft (aLeft), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderLeft (aLeft), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox5c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorder (aAll), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorder (aAll), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox6c");
        aHBox.addColumn (new PLText ("Column1", r10).setBorderLeft (aLeft).setBorderBottom (aBottom),
                         WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderLeft (aLeft).setBorderBottom (aBottom),
                         WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox7c");
        for (int i = 1; i <= 4; ++i)
          aHBox.addColumn (new PLText ("Column" + i, r10).setBorder (aAll), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
    }

    // Works!
    if (true)
    {
      // Using border around column elements
      aPS1.addElement (new PLText ("Using column border and border around column elements:",
                                   r10).setID ("text").setPadding (fValue, 0).setFillColor (Color.WHITE));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox1d").setColumnBorderTop (aTop);
        aHBox.addColumn (new PLText ("Column1", r10).setBorderTop (aTop), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderTop (aTop), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox2d").setColumnBorderRight (aRight);
        aHBox.addColumn (new PLText ("Column1", r10).setBorderRight (aRight), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderRight (aRight), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox3d").setColumnBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10).setBorderBottom (aBottom), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderBottom (aBottom), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox4d").setColumnBorderLeft (aLeft);
        aHBox.addColumn (new PLText ("Column1", r10).setBorderLeft (aLeft), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderLeft (aLeft), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox5d").setColumnBorder (aAll);
        aHBox.addColumn (new PLText ("Column1", r10).setBorder (aAll), WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorder (aAll), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox6d").setColumnBorderLeft (aLeft).setColumnBorderBottom (aBottom);
        aHBox.addColumn (new PLText ("Column1", r10).setBorderLeft (aLeft).setBorderBottom (aBottom),
                         WidthSpec.star ());
        aHBox.addColumn (new PLText ("Column2", r10).setBorderLeft (aLeft).setBorderBottom (aBottom),
                         WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLHBox aHBox = new PLHBox ().setID ("hbox7d").setColumnBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aHBox.addColumn (new PLText ("Column" + i, r10).setBorder (aAll), WidthSpec.star ());
        aPS1.addElement (aHBox);
      }
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getBufferedOutputStream (new File ("pdf/test-border-hbox.pdf")));
  }

  @Test
  public void testDifferentBordersVBox () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 20f;
    final float fSpacer = 10f;
    final BorderStyleSpec aTop = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aRight = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aBottom = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aLeft = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aAll = new BorderStyleSpec (Color.GRAY, fValue);

    // Use simple numbers - approx. A4
    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (500,
                                                           800)).setID ("pageset")
                                                                .setMargin (fValue)
                                                                .setFillColor (Color.GREEN)
                                                                .setBorder (new BorderSpec (new BorderStyleSpec (Color.PINK,
                                                                                                                 fValue)));

    // Works!
    if (true)
    {
      // Border around the box
      aPS1.addElement (new PLText ("Border around the box:", r10).setID ("text")
                                                                 .setPadding (fValue, 0)
                                                                 .setFillColor (Color.WHITE));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox1a").setBorderTop (aTop);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox2a").setBorderRight (aRight);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox3a").setBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox4a").setBorderLeft (aLeft);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox5a").setBorder (aAll);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox6a").setBorderLeft (aLeft).setBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox7a").setBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aVBox.addRow (new PLText ("Row" + i, r10));
        aPS1.addElement (aVBox);
      }
    }

    // Works!
    if (true)
    {
      // Using column border
      aPS1.addElement (new PLText ("Using row border:", r10).setID ("text")
                                                            .setPadding (fValue, 0)
                                                            .setFillColor (Color.WHITE));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox1b").setRowBorderTop (aTop);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox2b").setRowBorderRight (aRight);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox3b").setRowBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox4b").setRowBorderLeft (aLeft);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox5b").setRowBorder (aAll);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox6b").setRowBorderLeft (aLeft).setRowBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10));
        aVBox.addRow (new PLText ("Row2", r10));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox7b").setRowBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aVBox.addRow (new PLText ("Row" + i, r10));
        aPS1.addElement (aVBox);
      }
    }

    // Works!
    if (true)
    {
      // Using border around column elements
      aPS1.addElement (new PLText ("Using border around row elements:", r10).setID ("text")
                                                                            .setPadding (fValue, 0)
                                                                            .setFillColor (Color.WHITE));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox1c");
        aVBox.addRow (new PLText ("Row1", r10).setBorderTop (aTop));
        aVBox.addRow (new PLText ("Row2", r10).setBorderTop (aTop));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox2c");
        aVBox.addRow (new PLText ("Row1", r10).setBorderRight (aRight));
        aVBox.addRow (new PLText ("Row2", r10).setBorderRight (aRight));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox3c");
        aVBox.addRow (new PLText ("Row1", r10).setBorderBottom (aBottom));
        aVBox.addRow (new PLText ("Row2", r10).setBorderBottom (aBottom));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox4c");
        aVBox.addRow (new PLText ("Row1", r10).setBorderLeft (aLeft));
        aVBox.addRow (new PLText ("Row2", r10).setBorderLeft (aLeft));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox5c");
        aVBox.addRow (new PLText ("Row1", r10).setBorder (aAll));
        aVBox.addRow (new PLText ("Row2", r10).setBorder (aAll));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox6c");
        aVBox.addRow (new PLText ("Row1", r10).setBorderLeft (aLeft).setBorderBottom (aBottom));
        aVBox.addRow (new PLText ("Row2", r10).setBorderLeft (aLeft).setBorderBottom (aBottom));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox7c");
        for (int i = 1; i <= 4; ++i)
          aVBox.addRow (new PLText ("Row" + i, r10).setBorder (aAll));
        aPS1.addElement (aVBox);
      }
    }

    // Works!
    if (true)
    {
      // Using border around column elements
      aPS1.addElement (new PLText ("Using row border and border around row elements:", r10).setID ("text")
                                                                                           .setPadding (fValue, 0)
                                                                                           .setFillColor (Color.WHITE));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox1d").setRowBorderTop (aTop);
        aVBox.addRow (new PLText ("Row1", r10).setBorderTop (aTop));
        aVBox.addRow (new PLText ("Row2", r10).setBorderTop (aTop));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox2d").setRowBorderRight (aRight);
        aVBox.addRow (new PLText ("Row1", r10).setBorderRight (aRight));
        aVBox.addRow (new PLText ("Row2", r10).setBorderRight (aRight));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox3d").setRowBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10).setBorderBottom (aBottom));
        aVBox.addRow (new PLText ("Row2", r10).setBorderBottom (aBottom));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox4d").setRowBorderLeft (aLeft);
        aVBox.addRow (new PLText ("Row1", r10).setBorderLeft (aLeft));
        aVBox.addRow (new PLText ("Row2", r10).setBorderLeft (aLeft));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox5d").setRowBorder (aAll);
        aVBox.addRow (new PLText ("Row1", r10).setBorder (aAll));
        aVBox.addRow (new PLText ("Row2", r10).setBorder (aAll));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox6d").setRowBorderLeft (aLeft).setRowBorderBottom (aBottom);
        aVBox.addRow (new PLText ("Row1", r10).setBorderLeft (aLeft).setBorderBottom (aBottom));
        aVBox.addRow (new PLText ("Row2", r10).setBorderLeft (aLeft).setBorderBottom (aBottom));
        aPS1.addElement (aVBox);
      }
      aPS1.addElement (new PLSpacerY (fSpacer).setID ("spacer"));
      {
        final PLVBox aVBox = new PLVBox ().setID ("vbox7d").setRowBorder (aAll);
        for (int i = 1; i <= 4; ++i)
          aVBox.addRow (new PLText ("Row" + i, r10).setBorder (aAll));
        aPS1.addElement (aVBox);
      }
    }

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getBufferedOutputStream (new File ("pdf/test-border-vbox.pdf")));
  }

  @Test
  public void testBorderWithHeaderAndFooter () throws PDFCreationException
  {
    final FontSpec r10 = new FontSpec (PreloadFont.REGULAR, 10);

    final float fValue = 10f;
    final BorderStyleSpec aRed = new BorderStyleSpec (Color.RED, fValue);
    final BorderStyleSpec aBlue = new BorderStyleSpec (Color.BLUE, fValue);
    final BorderStyleSpec aMagenta = new BorderStyleSpec (Color.MAGENTA, fValue);
    final BorderStyleSpec aCyan = new BorderStyleSpec (Color.CYAN, fValue);
    final BorderStyleSpec aGray = new BorderStyleSpec (Color.GRAY, fValue);

    final PLPageSet aPS1 = new PLPageSet (new PDRectangle (500, 800)).setID ("pageset")
                                                                     .setMargin (100, fValue)
                                                                     .setFillColor (Color.GREEN)
                                                                     .setBorder (new BorderSpec (aGray));
    {
      final PLHBox aPageHeader = new PLHBox ().setID ("pageheader");
      aPageHeader.setMargin (fValue);
      aPageHeader.setBorder (aRed);
      aPageHeader.setColumnBorder (aBlue);
      aPageHeader.addColumn (new PLText ("Header1", r10).setBorder (aMagenta), WidthSpec.perc (50));
      aPageHeader.addColumn (new PLText ("Header2", r10).setBorder (aCyan), WidthSpec.perc (50));
      aPS1.setPageHeader (aPageHeader);
    }
    {
      final PLHBox aPageFooter = new PLHBox ().setID ("pagefooter");
      aPageFooter.setMargin (fValue);
      aPageFooter.setBorder (aRed);
      aPageFooter.setColumnBorder (aBlue);
      aPageFooter.addColumn (new PLText ("Footer1", r10).setBorder (aMagenta), WidthSpec.perc (50));
      aPageFooter.addColumn (new PLText ("Footer2", r10).setBorder (aCyan), WidthSpec.perc (50));
      aPS1.setPageFooter (aPageFooter);
    }
    aPS1.addElement (new PLText ("Only content", r10).setID ("all").setBorder (aRed));

    final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (false);
    aPageLayout.addPageSet (aPS1);
    aPageLayout.renderTo (FileHelper.getBufferedOutputStream (new File ("pdf/test-border-header-footer.pdf")));
  }
}
