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
package com.helger.pdflayout.element.link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * An external link that references to an external URI. Use {@link #setURI(String)} to define the
 * link target.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 6.0.1
 */
public abstract class AbstractPLExternalLink <IMPLTYPE extends AbstractPLExternalLink <IMPLTYPE>> extends
                                             AbstractPLLinkBase <IMPLTYPE>
{
  /**
   * The default set of URI schemes accepted by {@link #setURI(String)}. Dangerous schemes such as
   * <code>javascript:</code>, <code>file:</code>, <code>data:</code> and <code>vbscript:</code> are
   * intentionally not included. Callers that need a different policy can replace the active set via
   * {@link #setAllowedURISchemes(ICommonsSet)}.
   */
  public static final ICommonsSet <String> DEFAULT_ALLOWED_URI_SCHEMES;
  static
  {
    DEFAULT_ALLOWED_URI_SCHEMES = new CommonsHashSet <> ();
    DEFAULT_ALLOWED_URI_SCHEMES.add ("http");
    DEFAULT_ALLOWED_URI_SCHEMES.add ("https");
    DEFAULT_ALLOWED_URI_SCHEMES.add ("mailto");
    DEFAULT_ALLOWED_URI_SCHEMES.add ("tel");
    DEFAULT_ALLOWED_URI_SCHEMES.add ("ftp");
    DEFAULT_ALLOWED_URI_SCHEMES.add ("ftps");
  }

  private static ICommonsSet <String> s_aAllowedURISchemes = DEFAULT_ALLOWED_URI_SCHEMES.getClone ();

  private String m_sURI;

  public AbstractPLExternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  /**
   * @return A copy of the set of URI schemes currently accepted by {@link #setURI(String)}. An
   *         empty set disables scheme validation. Never <code>null</code>.
   * @since 8.1.2
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllowedURISchemes ()
  {
    return s_aAllowedURISchemes.getClone ();
  }

  /**
   * Replace the set of URI schemes accepted by {@link #setURI(String)}. Pass an empty set to
   * disable validation entirely (not recommended).
   *
   * @param aAllowedURISchemes
   *        The new set of accepted schemes, compared case-insensitively. May not be
   *        <code>null</code>.
   * @since 8.1.2
   */
  public static void setAllowedURISchemes (@NonNull final ICommonsSet <String> aAllowedURISchemes)
  {
    if (aAllowedURISchemes == null)
      throw new IllegalArgumentException ("AllowedURISchemes may not be null");
    final ICommonsSet <String> aLower = new CommonsHashSet <> ();
    for (final String s : aAllowedURISchemes)
      if (s != null)
        aLower.add (s.toLowerCase (Locale.ROOT));
    s_aAllowedURISchemes = aLower;
  }

  @Nullable
  private static String _extractScheme (@NonNull final String sURI) throws URISyntaxException
  {
    // RFC-compliant parsing via java.net.URI. Returns the scheme in lowercase, or null when the
    // URI is relative (no scheme).
    final String sScheme = new URI (sURI).getScheme ();
    return sScheme == null ? null : sScheme.toLowerCase (Locale.ROOT);
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setURI (aSource.getURI ());
    return thisAsT ();
  }

  /**
   * @return The URI to link to. May be <code>null</code>.
   */
  @Nullable
  public final String getURI ()
  {
    return m_sURI;
  }

  /**
   * Set the URI to link to. The URI scheme must be present in the active allowlist returned by
   * {@link #getAllowedURISchemes()} unless the allowlist is empty.
   *
   * @param sURI
   *        The URI to link to. May be <code>null</code>.
   * @return this for chaining.
   * @throws IllegalArgumentException
   *         if the URI uses a scheme that is not in the allowlist.
   */
  @NonNull
  public final IMPLTYPE setURI (@Nullable final String sURI)
  {
    internalCheckNotPrepared ();
    if (StringHelper.isNotEmpty (sURI) && s_aAllowedURISchemes.isNotEmpty ())
    {
      final String sScheme;
      try
      {
        sScheme = _extractScheme (sURI);
      }
      catch (final URISyntaxException ex)
      {
        throw new IllegalArgumentException ("Malformed URI: " + sURI, ex);
      }

      if (sScheme == null || !s_aAllowedURISchemes.contains (sScheme))
        throw new IllegalArgumentException ("The URI scheme '" +
                                            sScheme +
                                            "' is not in the allowlist " +
                                            s_aAllowedURISchemes +
                                            " - rejected URI '" +
                                            sURI +
                                            "'");
    }
    m_sURI = sURI;
    return thisAsT ();
  }

  @Override
  @Nullable
  protected PDAction createLinkAction ()
  {
    if (StringHelper.isEmpty (m_sURI))
      return null;
    final PDActionURI aAction = new PDActionURI ();
    aAction.setURI (m_sURI);
    return aAction;
  }

  @Override
  @NonNull
  protected String getMissingActionDebugReason ()
  {
    return "Not rendering an external link, because no URI is present";
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("URI", m_sURI).getToString ();
  }
}
