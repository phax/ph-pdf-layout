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
package com.helger.pdflayout.richtext.markup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.PLAnchorAnnotation;
import com.helger.pdflayout.richtext.annotation.PLHyperlinkAnnotation;
import com.helger.pdflayout.richtext.annotation.PLUnderlineAnnotation;

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
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("hi *bold* world");
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
  public void testNewLine ()
  {
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("line 1\nline 2");
    assertEquals (3, aTokens.size ());
    assertEquals ("line 1", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
    assertTrue (aTokens.get (1) instanceof IPLMarkupToken.NewLine);
    assertEquals ("line 2", ((IPLMarkupToken.Text) aTokens.get (2)).getText ());
  }

  @Test
  public void testEscapedBold ()
  {
    // \* is a literal '*', the surrounding text must NOT be treated as bold.
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("not \\*bold\\* here");
    assertEquals (1, aTokens.size ());
    assertEquals ("not *bold* here", ((IPLMarkupToken.Text) aTokens.get (0)).getText ());
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
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("*B* _I_ {color:#00ff00}G");
    // *, B, *, " ", _, I, _, " ", {color}, G
    assertEquals (10, aTokens.size ());
    assertNotNull (aTokens.findFirst (t -> t instanceof IPLMarkupToken.BoldToggle));
    assertNotNull (aTokens.findFirst (t -> t instanceof IPLMarkupToken.ItalicToggle));
    assertNotNull (aTokens.findFirst (t -> t instanceof IPLMarkupToken.Color));
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
    // {^:0.5|-0.3}up{^} -> MetricsToggle(fontScale=0.5, baselineOffsetScale=-0.3), Text("up"), MetricsToggle(close)
    final ICommonsList <IPLMarkupToken> aTokens = new PLMarkupParser ().parse ("{^:0.5|-0.3}up{^}");
    assertEquals (3, aTokens.size ());
    assertTrue (aTokens.get (0) instanceof IPLMarkupToken.MetricsToggle);
    final IPLMarkupToken.MetricsToggle aOpen = (IPLMarkupToken.MetricsToggle) aTokens.get (0);
    assertEquals (0.5f, aOpen.getFontScale (), 0.0001f);
    assertEquals (-0.3f, aOpen.getBaselineOffsetScale (), 0.0001f);
    assertEquals ("up", ((IPLMarkupToken.Text) aTokens.get (1)).getText ());
    assertTrue (aTokens.get (2) instanceof IPLMarkupToken.MetricsToggle);
  }
}
