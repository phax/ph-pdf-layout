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
package com.helger.pdflayout.richtext.annotation;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Hyperlink annotation for rich text. The URI may either be an absolute
 * external URI (e.g. {@code https://example.com}) or an internal anchor
 * reference starting with {@code #} (e.g. {@code #title1}).
 *
 * @author Philip Helger
 */
@Immutable
public final class PLHyperlinkAnnotation implements IPLRichTextAnnotation
{
  private final String m_sUri;
  private final EPLLinkStyle m_eLinkStyle;

  public PLHyperlinkAnnotation (@NonNull @Nonempty final String sUri, @NonNull final EPLLinkStyle eLinkStyle)
  {
    ValueEnforcer.notEmpty (sUri, "URI");
    ValueEnforcer.notNull (eLinkStyle, "LinkStyle");
    m_sUri = sUri;
    m_eLinkStyle = eLinkStyle;
  }

  @NonNull
  @Nonempty
  public String getUri ()
  {
    return m_sUri;
  }

  @NonNull
  public EPLLinkStyle getLinkStyle ()
  {
    return m_eLinkStyle;
  }

  /**
   * @return <code>true</code> if the URI is an internal anchor reference (starts
   *         with <code>#</code>), <code>false</code> for external URIs.
   */
  public boolean isInternalAnchorReference ()
  {
    return m_sUri.startsWith ("#");
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("URI", m_sUri).append ("LinkStyle", m_eLinkStyle).getToString ();
  }
}
