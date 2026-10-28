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
package com.helger.pdflayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.jspecify.annotations.NonNull;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.TestAssertion;
import org.verapdf.pdfa.results.ValidationResult;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Test helper that renders a {@link PageLayoutPDF} and checks the result with veraPDF.
 *
 * @author Philip Helger
 */
public final class PdfAValidatorHelper
{
  private static boolean s_bInitialised = false;

  private PdfAValidatorHelper ()
  {}

  private static synchronized void _ensureInitialised ()
  {
    if (!s_bInitialised)
    {
      VeraGreenfieldFoundryProvider.initialise ();
      s_bInitialised = true;
    }
  }

  @NonNull
  private static ICommonsList <String> _validate (@NonNull final PDFAParser aParser,
                                                 @NonNull final PDFAFlavour eFlavour) throws Exception
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    try (final PDFAValidator aValidator = Foundries.defaultInstance ().createValidator (eFlavour, false))
    {
      final ValidationResult aResult = aValidator.validate (aParser);
      for (final TestAssertion aTA : aResult.getTestAssertions ())
        if (aTA.getStatus () == TestAssertion.Status.FAILED)
          ret.add (aTA.getRuleId ().getClause () +
                   "-" +
                   aTA.getRuleId ().getTestNumber () +
                   ": " +
                   aTA.getMessage () +
                   (aTA.getLocation () != null ? " [" + aTA.getLocation ().getContext () + "]" : ""));
      if (ret.isEmpty () && !aResult.isCompliant ())
        ret.add ("Not compliant, but no failed rule was reported");
    }
    return ret;
  }

  /**
   * Validate the provided PDF with veraPDF against the PDF/A flavour that the PDF declares.
   *
   * @param aPdfBytes
   *        The PDF to validate. May not be <code>null</code>.
   * @param sExpectedFlavourID
   *        The expected veraPDF flavour ID, e.g. "3b" or "2b". May not be <code>null</code>.
   * @return The messages of all failed rules. Never <code>null</code> but maybe empty.
   * @throws Exception
   *         on error
   */
  @NonNull
  public static ICommonsList <String> validate (final byte @NonNull [] aPdfBytes,
                                                @NonNull final String sExpectedFlavourID) throws Exception
  {
    _ensureInitialised ();
    try (final InputStream aIS = new NonBlockingByteArrayInputStream (aPdfBytes);
         final PDFAParser aParser = Foundries.defaultInstance ().createParser (aIS, PDFAFlavour.NO_FLAVOUR))
    {
      final PDFAFlavour eFlavour = aParser.getFlavour ();
      assertEquals ("Declared PDF/A flavour", sExpectedFlavourID, eFlavour.getId ());
      return _validate (aParser, eFlavour);
    }
  }

  /**
   * Validate the provided PDF with veraPDF against an explicit flavour, e.g. PDF/UA-1.
   *
   * @param aPdfBytes
   *        The PDF to validate. May not be <code>null</code>.
   * @param eFlavour
   *        The flavour to validate against. May not be <code>null</code>.
   * @return The messages of all failed rules. Never <code>null</code> but maybe empty.
   * @throws Exception
   *         on error
   */
  @NonNull
  public static ICommonsList <String> validate (final byte @NonNull [] aPdfBytes,
                                                @NonNull final PDFAFlavour eFlavour) throws Exception
  {
    _ensureInitialised ();
    try (final InputStream aIS = new NonBlockingByteArrayInputStream (aPdfBytes);
         final PDFAParser aParser = Foundries.defaultInstance ().createParser (aIS, eFlavour))
    {
      return _validate (aParser, eFlavour);
    }
  }

  /**
   * Render the provided layout and assert that veraPDF finds no failed rule.
   *
   * @param aPageLayout
   *        The layout to render. May not be <code>null</code>.
   * @param sExpectedFlavourID
   *        The expected veraPDF flavour ID, e.g. "3b" or "2b". May not be <code>null</code>.
   * @return The bytes of the rendered PDF. Never <code>null</code>.
   * @throws Exception
   *         on error
   */
  public static byte @NonNull [] renderAndAssertValid (@NonNull final PageLayoutPDF aPageLayout,
                                                        @NonNull final String sExpectedFlavourID) throws Exception
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    aPageLayout.renderTo (aBAOS);
    final byte [] aBytes = aBAOS.toByteArray ();
    final ICommonsList <String> aErrors = validate (aBytes, sExpectedFlavourID);
    assertTrue ("veraPDF errors:\n" + String.join ("\n", aErrors), aErrors.isEmpty ());
    return aBytes;
  }
}
