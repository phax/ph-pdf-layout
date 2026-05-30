/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.richtext.markup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.PLAnchorAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;
import com.helger.pdflayout.richtext.color.PLCMYKColor;
import com.helger.pdflayout.richtext.markup.IPLMarkupToken.BoldToggle;
import com.helger.pdflayout.richtext.markup.IPLMarkupToken.Color;
import com.helger.pdflayout.richtext.markup.IPLMarkupToken.ItalicToggle;

/**
 * Tests {@link PLMarkupParser}.
 *
 * @author Philip Helger
 */
public final class PLMarkupParserTest
{
  @Test
  public void testPlainText ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("Hello world");
    assertEquals (1, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.Text);
    assertEquals ("Hello world", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
  }

  @Test
  public void testBold ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi **bold** world");
    // Text, BoldToggle, Text(bold), BoldToggle, Text
    assertEquals (5, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.Text);
    assertEquals ("hi ", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.BoldToggle);
    assertEquals ("bold", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
    assertTrue (aTokens.get (3) instanceof IPLMarkupToken.BoldToggle);
    assertEquals (" world", ((IPLMarkupToken.Text) aTokens.get (4)).getText ());
  }

  @Test
  public void testItalic ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi *italic* world");
    // Text, ItalicToggle, Text(italic), ItalicToggle, Text
    assertEquals (5, aTokens.size ());
    assertEquals ("hi ", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals ("italic", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
    assertTrue (aTokens.get (3) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals (" world", ((IPLMarkupToken.Text) aTokens.get (4)).getText ());
  }

  @Test
  public void testItalicNotMatchedInsideBold ()
  {
    // The text "**bold**" must produce 1 bold-open + Text + 1 bold-close,
    // NOT four italic markers.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("**bold**");
    assertEquals (3, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.BoldToggle);
    assertEquals ("bold", ((IPLMarkupToken.Text) aTokens.get (1)).getText ());
    assertTrue (aTokens.get (2) instanceof IPLMarkupToken.BoldToggle);
  }

  @Test
  public void testBoldContainingItalic ()
  {
    // "**bold *and italic* bold**" — italic nested inside bold.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("**bold *and italic* bold**");
    // BoldToggle, Text("bold "), ItalicToggle, Text("and italic"), ItalicToggle, Text(" bold"), BoldToggle
    assertEquals (7, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.BoldToggle);
    assertEquals ("bold ", ((IPLMarkupToken.Text) aTokens.get (1)).getText ());
    assertTrue (aTokens.get (2) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals ("and italic", ((IPLMarkupToken.Text) aTokens.get (3)).getText ());
    assertTrue (aTokens.get (4) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals (" bold", ((IPLMarkupToken.Text) aTokens.get (5)).getText ());
    assertTrue (aTokens.get (6) instanceof IPLMarkupToken.BoldToggle);
  }

  @Test
  public void testItalicNotMatchedInsideUnderline ()
  {
    // The text "__under__" should produce 1 underline-open + Text + 1 underline-close,
    // NOT four italic markers.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("__under__");
    assertEquals (3, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.AnnotationToggle);
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.Text);
    assertEquals ("under", ((IPLMarkupToken.Text) aTokens.get (1)).getText ());
    assertTrue (aTokens.get (2) instanceof IPLMarkupToken.AnnotationToggle);

    final IPLMarkupToken.AnnotationToggle aOpen = (IPLMarkupToken.AnnotationToggle) aTokens.get (0);
    assertTrue (aOpen.getAnnotation () instanceof PLUnderlineAnnotation);
  }

  @Test
  public void testColor ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi {color:#ff0000}red{color:#000000} done");
    assertEquals (5, aTokens.size ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.Color);
    final IPLMarkupToken.Color aRed = (IPLMarkupToken.Color) aTokens.get (1);
    assertEquals (new PLColor (255, 0, 0), aRed.getColor ());
  }

  @Test
  public void testSoftBreak ()
  {
    // Bare \n is a CommonMark soft break — rendered later as a single space.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("line 1\nline 2");
    assertEquals (3, aTokens.size ());
    assertEquals ("line 1", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.SoftBreak);
    assertEquals ("line 2", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testHardBreakSpaces ()
  {
    // Two-or-more trailing spaces before the line ending = CommonMark hard break.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("line 1  \nline 2");
    assertEquals (3, aTokens.size ());
    assertEquals ("line 1", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.NewLine);
    assertEquals ("line 2", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testHardBreakBackslash ()
  {
    // Backslash before the line ending = CommonMark hard break.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("line 1\\\nline 2");
    assertEquals (3, aTokens.size ());
    assertEquals ("line 1", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.NewLine);
    assertEquals ("line 2", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testEscapedBackslashThenSoftBreak ()
  {
    // \\\n in the source = literal backslash + bare newline = soft break (NOT hard).
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("line 1\\\\\nline 2");
    assertEquals (3, aTokens.size ());
    assertEquals ("line 1\\", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.SoftBreak);
    assertEquals ("line 2", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testItalicUnderscoreAlias ()
  {
    // CommonMark allows _italic_ as an alias for *italic*.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi _italic_ world");
    assertEquals (5, aTokens.size ());
    assertEquals ("hi ", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals ("italic", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
    assertTrue (aTokens.get (3) instanceof IPLMarkupToken.ItalicToggle);
    assertEquals (" world", ((IPLMarkupToken.Text) aTokens.get (4)).getText ());
  }

  @Test
  public void testBoldItalicCombined ()
  {
    // ***foo*** is bold + italic around "foo" (CommonMark style).
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("***foo***");
    // ItalicToggle, BoldToggle, Text("foo"), ItalicToggle, BoldToggle
    assertEquals (5, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.ItalicToggle);
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.BoldToggle);
    assertEquals ("foo", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
    assertTrue (aTokens.get (3) instanceof IPLMarkupToken.ItalicToggle);
    assertTrue (aTokens.get (4) instanceof IPLMarkupToken.BoldToggle);
  }

  @Test
  public void testEscapedBold ()
  {
    // \** is a literal '**', the surrounding text must NOT be treated as bold.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("not \\**bold\\** here");
    assertEquals (1, aTokens.size ());
    assertEquals ("not **bold** here", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
  }

  @Test
  public void testEscapedItalic ()
  {
    // \* is a literal '*', the surrounding text must NOT be treated as italic.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("not \\*italic\\* here");
    assertEquals (1, aTokens.size ());
    assertEquals ("not *italic* here", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
  }

  @Test
  public void testHyperlink ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("see {link[https://example.com]}here{link} for more");
    // Text, AnnotationToggle(open), Text, AnnotationToggle(close), Text
    assertEquals (5, aTokens.size ());
    final IPLMarkupToken.AnnotationToggle aOpen = (IPLMarkupToken.AnnotationToggle) aTokens.get (1);
    final PLHyperlinkAnnotation aLink = (PLHyperlinkAnnotation) aOpen.getAnnotation ();
    assertEquals ("https://example.com", aLink.getUri ());
  }

  @Test
  public void testAnchor ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("see {anchor:title1}Title{anchor} below");
    assertEquals (5, aTokens.size ());
    final IPLMarkupToken.AnnotationToggle aOpen = (IPLMarkupToken.AnnotationToggle) aTokens.get (1);
    final PLAnchorAnnotation aAnchor = (PLAnchorAnnotation) aOpen.getAnnotation ();
    assertEquals ("title1", aAnchor.getName ());
  }

  @Test
  public void testCombined ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("**B** *I* {color:#00ff00}G");
    // **, B, **, " ", *, I, *, " ", {color}, G
    assertEquals (10, aTokens.size ());
    assertNotNull (aTokens.findFirst (BoldToggle.class::isInstance));
    assertNotNull (aTokens.findFirst (ItalicToggle.class::isInstance));
    assertNotNull (aTokens.findFirst (Color.class::isInstance));
  }

  @Test
  public void testSubscriptDefault ()
  {
    // x{_}foo{_}y -> Text("x"), MetricsToggle(open), Text("foo"), MetricsToggle(close), Text("y")
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("x{_}foo{_}y");
    assertEquals (5, aTokens.size ());
    assertEquals ("x", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.MetricsToggle);
    final IPLMarkupToken.MetricsToggle aOpen = (IPLMarkupToken.MetricsToggle) aTokens.get (1);
    assertEquals (0.61f, aOpen.getFontScale (), 0.0001f);
    assertEquals (0.15f, aOpen.getBaselineOffsetScale (), 0.0001f);
    assertEquals ("foo", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
    assertTrue (aTokens.get (3) instanceof IPLMarkupToken.MetricsToggle);
    final IPLMarkupToken.MetricsToggle aClose = (IPLMarkupToken.MetricsToggle) aTokens.get (3);
    // Open and close share the same key so the run-builder can match them.
    assertEquals (aOpen.getKey (), aClose.getKey ());
    assertEquals ("y", ((IPLMarkupToken.Text) aTokens.get (4)).getText ());
  }

  @Test
  public void testSuperscriptCustomParams ()
  {
    // {^:0.5|-0.3}up{^} -> MetricsToggle(fontScale=0.5, baselineOffsetScale=-0.3), Text("up"),
    // MetricsToggle(close)
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("{^:0.5|-0.3}up{^}");
    assertEquals (3, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.MetricsToggle);
    final IPLMarkupToken.MetricsToggle aOpen = (IPLMarkupToken.MetricsToggle) aTokens.get (0);
    assertEquals (0.5f, aOpen.getFontScale (), 0.0001f);
    assertEquals (-0.3f, aOpen.getBaselineOffsetScale (), 0.0001f);
    assertEquals ("up", ((IPLMarkupToken.Text) aTokens.get (1)).getText ());
    assertTrue (aTokens.get (2) instanceof IPLMarkupToken.MetricsToggle);
  }

  @Test
  public void testColorCmyk ()
  {
    // The CMYK marker {color_cmyk:75,15,0,20} sets a CMYK colour without touching
    // the existing RGB-style {color:#rrggbb}. See ralfstuckert/pdfbox-layout#94.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi {color_cmyk:75,15,0,20}cyan-ish");
    assertEquals (3, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.Text);
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.Color);
    final IPLMarkupToken.Color aColorToken = (IPLMarkupToken.Color) aTokens.get (1);
    assertTrue ("Expected a PLCMYKColor", aColorToken.getColor () instanceof PLCMYKColor);
    final PLCMYKColor aCmyk = (PLCMYKColor) aColorToken.getColor ();
    assertEquals (0.75f, aCmyk.getC (), 0.0001f);
    assertEquals (0.15f, aCmyk.getM (), 0.0001f);
    assertEquals (0f, aCmyk.getY (), 0.0001f);
    assertEquals (0.20f, aCmyk.getK (), 0.0001f);
    assertEquals ("cyan-ish", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testRgbColorMarkerUnchanged ()
  {
    // The original RGB marker MUST still work, identically — adding COLOR_CMYK
    // must not regress {color:#rrggbb}. See ralfstuckert/pdfbox-layout#94.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("{color:#ff0000}red");
    assertEquals (2, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.Color);
    final IPLMarkupToken.Color aColorToken = (IPLMarkupToken.Color) aTokens.get (0);
    // Must NOT be a CMYK colour
    assertFalse (aColorToken.getColor () instanceof PLCMYKColor);
    assertEquals (255, aColorToken.getColor ().getRed ());
    assertEquals (0, aColorToken.getColor ().getGreen ());
    assertEquals (0, aColorToken.getColor ().getBlue ());
  }

  @Test
  public void testColorCmykFloatValues ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("{color_cmyk:12.5,0.0,50,99.9}x");
    assertEquals (2, aTokens.size ());
    final PLCMYKColor aCmyk = (PLCMYKColor) ((IPLMarkupToken.Color) aTokens.get (0)).getColor ();
    assertEquals (0.125f, aCmyk.getC (), 0.0001f);
    assertEquals (0f, aCmyk.getM (), 0.0001f);
    assertEquals (0.5f, aCmyk.getY (), 0.0001f);
    assertEquals (0.999f, aCmyk.getK (), 0.0001f);
  }
}
