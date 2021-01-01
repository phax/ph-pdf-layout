/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.spec;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

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

  public TextAndWidthSpec (@Nonnull final String sText, @Nonnegative final float fWidth)
  {
    ValueEnforcer.notNull (sText, "Text");
    ValueEnforcer.isGE0 (fWidth, "Width");
    m_sText = sText;
    m_fWidth = fWidth;
  }

  @Nonnull
  public final String getText ()
  {
    return m_sText;
  }

  @Nonnegative
  public final float getWidth ()
  {
    return m_fWidth;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TextAndWidthSpec rhs = (TextAndWidthSpec) o;
    return m_sText.equals (rhs.m_sText) && EqualsHelper.equals (m_fWidth, rhs.m_fWidth);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sText).append (m_fWidth).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("text", m_sText).append ("width", m_fWidth).getToString ();
  }
}
