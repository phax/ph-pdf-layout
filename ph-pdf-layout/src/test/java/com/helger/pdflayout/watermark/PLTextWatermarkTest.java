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
package com.helger.pdflayout.watermark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.base.mock.CommonsAssert;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Test class for class {@link PLTextWatermark}.
 *
 * @author Philip Helger
 */
public final class PLTextWatermarkTest
{
  @Test
  public void testBasic ()
  {
    final PLTextWatermark aWM = new PLTextWatermark ("DRAFT", PreloadFont.REGULAR_BOLD);
    assertEquals ("DRAFT", aWM.getText ());
    assertSame (PreloadFont.REGULAR_BOLD, aWM.getFont ());
    CommonsAssert.assertEquals (PLTextWatermark.DEFAULT_FONT_SIZE, aWM.getFontSize ());
    assertEquals (PLTextWatermark.DEFAULT_COLOR, aWM.getColor ());
    CommonsAssert.assertEquals (PLTextWatermark.DEFAULT_OPACITY, aWM.getOpacity ());
    CommonsAssert.assertEquals (PLTextWatermark.DEFAULT_ROTATION, aWM.getRotation ());
    assertSame (PLTextWatermark.DEFAULT_POSITION, aWM.getPosition ());
    CommonsAssert.assertEquals (PLTextWatermark.DEFAULT_EDGE_DISTANCE, aWM.getEdgeDistance ());

    aWM.setFontSize (12)
       .setColor (PLColor.RED)
       .setOpacity (0.5f)
       .setRotation (-30)
       .setPosition (EPLWatermarkPosition.TOP)
       .setEdgeDistance (0);
    CommonsAssert.assertEquals (12f, aWM.getFontSize ());
    assertEquals (PLColor.RED, aWM.getColor ());
    CommonsAssert.assertEquals (0.5f, aWM.getOpacity ());
    CommonsAssert.assertEquals (-30f, aWM.getRotation ());
    assertSame (EPLWatermarkPosition.TOP, aWM.getPosition ());
    CommonsAssert.assertEquals (0f, aWM.getEdgeDistance ());
    assertNotNull (aWM.toString ());
  }

  @Test
  public void testInvalid ()
  {
    try
    {
      new PLTextWatermark ("", PreloadFont.REGULAR);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    final PLTextWatermark aWM = new PLTextWatermark ("x", PreloadFont.REGULAR);
    try
    {
      aWM.setFontSize (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aWM.setOpacity (1.1f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aWM.setEdgeDistance (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testDrawOnAllPages () throws Exception
  {
    try (final PDDocument aDoc = new PDDocument ())
    {
      aDoc.addPage (new PDPage (PDRectangle.A4));
      aDoc.addPage (new PDPage (PDRectangle.A4));
      new PLTextWatermark ("DRAFT", PreloadFont.REGULAR_BOLD).setOpacity (0.5f).drawOnAllPages (aDoc, false);

      for (final PDPage aPage : aDoc.getPages ())
      {
        final List <Object> aTokens = new PDFStreamParser (aPage).parse ();
        // Starts with the artifact marked content
        assertTrue (aTokens.toString (), aTokens.get (0) instanceof COSName);
        assertEquals (COSName.ARTIFACT, aTokens.get (0));
        boolean bBDC = false;
        boolean bEMC = false;
        boolean bGS = false;
        for (final Object aToken : aTokens)
          if (aToken instanceof final Operator aOp)
          {
            if (OperatorName.BEGIN_MARKED_CONTENT_SEQ.equals (aOp.getName ()))
              bBDC = true;
            if (OperatorName.END_MARKED_CONTENT.equals (aOp.getName ()))
              bEMC = true;
            if (OperatorName.SET_GRAPHICS_STATE_PARAMS.equals (aOp.getName ()))
              bGS = true;
          }
        assertTrue (bBDC);
        assertTrue (bEMC);
        // Opacity < 1 uses a graphics state
        assertTrue (bGS);
      }
    }
  }
}
