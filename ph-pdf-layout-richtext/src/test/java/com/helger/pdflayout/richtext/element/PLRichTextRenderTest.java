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
package com.helger.pdflayout.richtext.element;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.PDFCreationException;
import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.richtext.run.PLFontFamily;
import com.helger.pdflayout.richtext.run.PLRichTextRun;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Render-level tests for {@link PLRichText}. These exercise the full prepare /
 * render pipeline by writing a real PDF to a temp file and asserting the file
 * is non-empty. Pixel-diff reference comparisons are deliberately not wired up
 * yet — that happens in a later step.
 *
 * @author Philip Helger
 */
public final class PLRichTextRenderTest
{
    private static final PLFontFamily FONT_FAMILY = new PLFontFamily (PreloadFont.REGULAR,
                                                                       PreloadFont.REGULAR_BOLD,
                                                                       PreloadFont.REGULAR_ITALIC,
                                                                       PreloadFont.REGULAR_BOLD_ITALIC);

    private static void _renderToTemp (final PageLayoutPDF aLayout, final String sName) throws IOException,
                                                                                         PDFCreationException
    {
        final File aFile = Files.createTempFile ("plrichtext-" + sName + "-", ".pdf").toFile ();
        aFile.deleteOnExit ();
        aLayout.renderTo (aFile);
        assertTrue ("Rendered PDF must exist: " + aFile.getAbsolutePath (), aFile.exists ());
        assertTrue ("Rendered PDF must be non-empty: " + aFile.getAbsolutePath (), aFile.length () > 0);
    }

    @Test
    public void testPlainTextSingleLine () throws IOException, PDFCreationException
    {
        final ICommonsList <PLRichTextRun> aRuns = new CommonsArrayList <> ();
        aRuns.add (new PLRichTextRun ("Hello", new FontSpec (PreloadFont.REGULAR, 12, PLColor.BLACK)));

        final PLRichText aRT = new PLRichText (aRuns);
        final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
        aPS.addElement (aRT);

        final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
        _renderToTemp (aLayout, "plain");
    }

    @Test
    public void testMultiStyleMarkup () throws IOException, PDFCreationException
    {
        final PLRichText aRT = PLRichText.createFromMarkup ("*bold* and _italic_ and __underline__",
                                                            FONT_FAMILY,
                                                            12f,
                                                            PLColor.BLACK);
        final PLPageSet aPS = new PLPageSet (PDRectangle.A4).setMargin (40);
        aPS.addElement (aRT);

        final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
        _renderToTemp (aLayout, "multistyle");
    }

    @Test
    public void testWrapping () throws IOException, PDFCreationException
    {
        final StringBuilder aSB = new StringBuilder ();
        for (int i = 0; i < 60; ++i)
            aSB.append ("word").append (i).append (' ');
        final PLRichText aRT = PLRichText.createFromMarkup (aSB.toString ().trim (),
                                                            FONT_FAMILY,
                                                            12f,
                                                            PLColor.BLACK);
        // Narrow page so wrapping is forced.
        final PLPageSet aPS = new PLPageSet (200, 400).setMargin (10);
        aPS.addElement (aRT);

        final PageLayoutPDF aLayout = new PageLayoutPDF ().addPageSet (aPS);
        _renderToTemp (aLayout, "wrap");

        // Sanity: prepared height grew beyond a single line.
        final float fSingleLineHeight = 12f * 1.2f; // rough upper bound on one line height
        assertTrue ("Expected wrapped block taller than a single line",
                    aRT.getPreparedSize ().getHeight () > fSingleLineHeight);
    }
}
