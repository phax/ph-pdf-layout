/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.spec;

import java.io.Serializable;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class wraps a text with a specified rendering width.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class TextAndWidthSpec implements Serializable
{
  private final String m_sText;
  private final float m_fWidth;
  private final boolean m_bDisplayAsNewline;

  public TextAndWidthSpec (@NonNull final String sText,
                           @Nonnegative final float fWidth,
                           final boolean bDisplayAsNewline)
  {
    ValueEnforcer.notNull (sText, "Text");
    ValueEnforcer.isGE0 (fWidth, "Width");
    m_sText = sText;
    m_fWidth = fWidth;
    m_bDisplayAsNewline = bDisplayAsNewline;
  }

  @NonNull
  public final String getText ()
  {
    return m_sText;
  }

  @Nonnegative
  public final float getWidth ()
  {
    return m_fWidth;
  }

  public boolean isDisplayAsNewline ()
  {
    return m_bDisplayAsNewline;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TextAndWidthSpec rhs = (TextAndWidthSpec) o;
    return m_sText.equals (rhs.m_sText) &&
           EqualsHelper.equals (m_fWidth, rhs.m_fWidth) &&
           m_bDisplayAsNewline == rhs.m_bDisplayAsNewline;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sText).append (m_fWidth).append (m_bDisplayAsNewline).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Text", m_sText)
                                       .append ("Width", m_fWidth)
                                       .append ("DisplayAsNewline", m_bDisplayAsNewline)
                                       .getToString ();
  }
}
