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
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.helger.pdflayout.spec.LoadedFont;
import com.helger.pdflayout.spec.PreloadFont;

/**
 * Standalone micro benchmark for PDFBOX-6169: compares the default PDFBox text encoding
 * ({@link PDFont#encode(String)}, {@link PDFont#getStringWidth(String)}) with a per-code point
 * cache built on top of the protected {@link PDFont#encode(int)} - the approach used by
 * ph-pdf-layout.<br>
 * Only depends on PDFBox and the JDK. It is placed in the PDFBox font package solely to be able to
 * call the protected <code>encode(int)</code> method.<br>
 * Run with <code>java -cp pdfbox.jar:fontbox.jar:commons-logging.jar:. PDFontEncodeBenchmark</code>
 *
 * @author Philip Helger
 */
public final class PDFontEncodeBenchmarkLoadedFont extends AbstractEncodeBenchmarkBase
{

  private PDFontEncodeBenchmarkLoadedFont ()
  {}

  private static void _verifySameResult (final PDFont aFont,
                                         final LoadedFont aLF,
                                         final String [] aTexts) throws IOException
  {
    for (final String s : aTexts)
    {
      if (!Arrays.equals (aFont.encode (s), aLF.getEncodedForPageContentStream (s)))
        throw new IllegalStateException ("Encoding differs for '" + s + "' in " + aFont.getName ());

      final float f1 = aFont.getStringWidth (s);
      final float f2 = aLF.getStringWidth (s, 1000f);
      if ((f1 - f2) > 0.05f)
        throw new IllegalStateException ("Width differs for '" +
                                         s +
                                         "' in " +
                                         aFont.getName () +
                                         " - " +
                                         f1 +
                                         " vs. " +
                                         f2);
    }
  }

  private static void _compare (final String sFontName,
                                final String sTextsName,
                                final String [] aTexts,
                                final String sOperation,
                                final IWorkload aDefault,
                                final IWorkload aCached) throws IOException
  {
    // Warm up both, interleaved
    for (int i = 0; i < WARMUP_ROUNDS; ++i)
    {
      _measureRound (aDefault, aTexts);
      _measureRound (aCached, aTexts);
    }

    // Measure, interleaved to reduce drift effects
    final double [] aDefaultNs = new double [MEASURE_ROUNDS];
    final double [] aCachedNs = new double [MEASURE_ROUNDS];
    for (int i = 0; i < MEASURE_ROUNDS; ++i)
    {
      aDefaultNs[i] = _measureRound (aDefault, aTexts);
      aCachedNs[i] = _measureRound (aCached, aTexts);
    }

    final double dDefault = _median (aDefaultNs);
    final double dCached = _median (aCachedNs);
    final double dAvgLen = (double) _charCount (aTexts) / aTexts.length;
    System.out.println (String.format (Locale.ROOT,
                                       "| %-22s | %-15s | %-15s | %13.1f | %13.1f | %6.2fx | %14.2f | %14.2f |",
                                       sFontName,
                                       sTextsName,
                                       sOperation,
                                       Double.valueOf (dDefault),
                                       Double.valueOf (dCached),
                                       Double.valueOf (dDefault / dCached),
                                       Double.valueOf (dDefault / dAvgLen),
                                       Double.valueOf (dCached / dAvgLen)));
  }

  private static void _runFont (final String sFontName,
                                final PDFont aFont,
                                final List <String []> aTextSets,
                                final List <String> aTextSetNames) throws IOException
  {
    final LoadedFont aLF = new LoadedFont (aFont, PreloadFont.DEFAULT_FALLBACK_CODE_POINT, -1);
    for (int i = 0; i < aTextSets.size (); ++i)
    {
      final String [] aTexts = aTextSets.get (i);
      _verifySameResult (aFont, aLF, aTexts);

      _compare (sFontName, aTextSetNames.get (i), aTexts, "encode(String)", a -> {
        long n = 0;
        for (final String s : a)
          n += aFont.encode (s).length;
        return n;
      }, a -> {
        long n = 0;
        for (final String s : a)
          n += aLF.getEncodedForPageContentStream (s).length;
        return n;
      });

      _compare (sFontName, aTextSetNames.get (i), aTexts, "getStringWidth", a -> {
        float f = 0;
        for (final String s : a)
          f += aFont.getStringWidth (s);
        return (long) f;
      }, a -> {
        float f = 0;
        for (final String s : a)
          f += aLF.getStringWidth (s, 1000f);
        return (long) f;
      });
    }
  }

  public static void main (final String [] args) throws IOException
  {
    final List <String []> aTextSets = new ArrayList <> ();
    final List <String> aTextSetNames = new ArrayList <> ();
    // Typical table cells
    aTextSets.add (_createTexts (2_000, 3, 15, 4711));
    aTextSetNames.add ("short (3-15)");
    // Typical text lines after line breaking
    aTextSets.add (_createTexts (2_000, 60, 100, 4712));
    aTextSetNames.add ("lines (60-100)");
    // Whole paragraphs, as measured before line breaking
    aTextSets.add (_createTexts (200, 500, 1000, 4713));
    aTextSetNames.add ("para (500-1000)");

    System.out.println ("Java: " + System.getProperty ("java.vendor") + " " + System.getProperty ("java.version"));
    System.out.println ("OS: " +
                        System.getProperty ("os.name") +
                        " " +
                        System.getProperty ("os.version") +
                        " (" +
                        System.getProperty ("os.arch") +
                        "), " +
                        Runtime.getRuntime ().availableProcessors () +
                        " cores");
    System.out.println ("PDFBox: " + org.apache.pdfbox.util.Version.getVersion ());
    System.out.println ("Rounds: " +
                        WARMUP_ROUNDS +
                        " warmup + " +
                        MEASURE_ROUNDS +
                        " measured, >= " +
                        MIN_ROUND_NANOS / 1_000_000 +
                        " ms each; median reported");
    System.out.println ();
    System.out.println ("| Font                   | Texts           | Operation       | PDFBox ns/str | cached ns/str | speedup | PDFBox ns/char | cached ns/char |");
    System.out.println ("|------------------------|-----------------|-----------------|--------------:|--------------:|--------:|---------------:|---------------:|");

    try (final PDDocument aDoc = new PDDocument ())
    {
      _runFont ("Helvetica (Std14)", new PDType1Font (Standard14Fonts.FontName.HELVETICA), aTextSets, aTextSetNames);
      _runFont ("Times-Roman (Std14)",
                new PDType1Font (Standard14Fonts.FontName.TIMES_ROMAN),
                aTextSets,
                aTextSetNames);

      final String sPath = "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf";
      try (final InputStream aIS = PDFont.class.getResourceAsStream (sPath))
      {
        // Embedded and subset - the typical case for custom fonts
        _runFont ("LiberationSans (Type0)", PDType0Font.load (aDoc, aIS), aTextSets, aTextSetNames);
      }
    }
    System.out.println ();
    System.out.println ("(sink " + s_nSink + ")");
  }
}
