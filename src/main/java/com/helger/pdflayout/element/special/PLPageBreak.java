/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element.special;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * A page break that ensures a new page is started afterwards. The difference
 * between force pages breaks and normal page breaks is as follows: forced page
 * breaks are always executed, whereas normal page breaks are not executed if a
 * new page just started.<br>
 * Important note: page breaks are only handled if they are directly contained
 * in a page set. Page breaks are not handled when nested in VBoxes or HBoxes.
 *
 * @author Philip Helger
 */
public class PLPageBreak extends AbstractPLRenderableObject <PLPageBreak>
{
  private final boolean m_bForcePageBreak;

  /**
   * Constructor
   *
   * @param bForcePageBreak
   *        <code>true</code> if this is a forced page break, <code>false</code>
   *        if it is a normal page break.
   */
  public PLPageBreak (final boolean bForcePageBreak)
  {
    m_bForcePageBreak = bForcePageBreak;
  }

  /**
   * @return <code>true</code> if this is a forced page break,
   *         <code>false</code> if it is a normal page break.
   */
  public boolean isForcePageBreak ()
  {
    return m_bForcePageBreak;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    // Use the fixed size
    return SizeSpec.SIZE0;
  }

  @Override
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {}

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("forcePageBreak", m_bForcePageBreak).getToString ();
  }

  @Nonnull
  public static PLPageBreak createPreparedPageBreak ()
  {
    final PLPageBreak ret = new PLPageBreak (false);
    ret.internalMarkAsPrepared (SizeSpec.SIZE0);
    return ret;
  }
}
