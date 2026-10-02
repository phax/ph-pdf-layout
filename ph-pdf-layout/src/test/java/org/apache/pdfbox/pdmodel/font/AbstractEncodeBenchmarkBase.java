package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

abstract class AbstractEncodeBenchmarkBase
{
  protected static final int WARMUP_ROUNDS = 10;
  protected static final int MEASURE_ROUNDS = 15;
  protected static final long MIN_ROUND_NANOS = 200_000_000L;

  protected static final String [] WORDS = { "Lorem",
                                             "ipsum",
                                             "dolor",
                                             "sit",
                                             "amet,",
                                             "consectetur",
                                             "adipiscing",
                                             "elit.",
                                             "Invoice",
                                             "Rechnung",
                                             "Größe",
                                             "Übersicht",
                                             "Straße",
                                             "Bestätigung",
                                             "€",
                                             "1.234,56",
                                             "2026-10-02",
                                             "Quantity:",
                                             "(total)",
                                             "café",
                                             "naïve",
                                             "»quoted«",
                                             "PDF",
                                             "layout",
                                             "the",
                                             "and",
                                             "of",
                                             "a" };

  protected static volatile long s_nSink;

  @FunctionalInterface
  protected interface IWorkload
  {
    long run (String [] aTexts) throws IOException;
  }

  protected static String [] _createTexts (final int nCount, final int nMinLen, final int nMaxLen, final long nSeed)
  {
    final Random aRandom = new Random (nSeed);
    final String [] ret = new String [nCount];
    for (int i = 0; i < nCount; ++i)
    {
      final int nTargetLen = nMinLen + aRandom.nextInt (nMaxLen - nMinLen + 1);
      final StringBuilder aSB = new StringBuilder (nTargetLen + 16);
      while (aSB.length () < nTargetLen)
      {
        if (aSB.length () > 0)
          aSB.append (' ');
        aSB.append (WORDS[aRandom.nextInt (WORDS.length)]);
      }
      ret[i] = aSB.toString ();
    }
    return ret;
  }

  protected static long _charCount (final String [] aTexts)
  {
    long ret = 0;
    for (final String s : aTexts)
      ret += s.length ();
    return ret;
  }

  /**
   * @return nanoseconds per string of this round
   */
  protected static double _measureRound (final IWorkload aWorkload, final String [] aTexts) throws IOException
  {
    long nIterations = 0;
    long nSink = 0;
    final long nStart = System.nanoTime ();
    long nElapsed;
    do
    {
      nSink += aWorkload.run (aTexts);
      nIterations++;
      nElapsed = System.nanoTime () - nStart;
    } while (nElapsed < MIN_ROUND_NANOS);
    s_nSink += nSink;
    return (double) nElapsed / (nIterations * aTexts.length);
  }

  protected static double _median (final double [] a)
  {
    final double [] aSorted = a.clone ();
    Arrays.sort (aSorted);
    return aSorted[aSorted.length / 2];
  }
}
