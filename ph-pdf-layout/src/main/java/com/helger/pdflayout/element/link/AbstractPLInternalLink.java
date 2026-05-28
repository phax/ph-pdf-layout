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

import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.IPLHasAnchorName;
import com.helger.pdflayout.base.IPLRenderableObject;

/**
 * A clickable link that jumps to a named destination within the same PDF document. The target is
 * identified by an anchor name; a corresponding {@link PLAnchor} (or any block element with
 * {@link IPLHasAnchorName#setAnchorName} set) must exist somewhere in the document so the named
 * destination resolves at PDF read time. Forward references work too - the target may render
 * later than the link itself.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 8.2.0
 */
public abstract class AbstractPLInternalLink <IMPLTYPE extends AbstractPLInternalLink <IMPLTYPE>> extends
                                             AbstractPLLinkBase <IMPLTYPE>
{
  private String m_sTargetAnchorName;

  public AbstractPLInternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@NonNull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    setTargetAnchorName (aSource.getTargetAnchorName ());
    return thisAsT ();
  }

  /**
   * @return The name of the anchor this link jumps to, or <code>null</code> if no target was set.
   */
  @Nullable
  public final String getTargetAnchorName ()
  {
    return m_sTargetAnchorName;
  }

  /**
   * Set the name of the anchor this link should jump to. The link annotation resolves via a
   * {@link PDNamedDestination} so the target need not exist at the time this is called - it must
   * just exist in the final document.
   *
   * @param sTargetAnchorName
   *        The target anchor name. May be <code>null</code> to clear.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setTargetAnchorName (@Nullable final String sTargetAnchorName)
  {
    internalCheckNotPrepared ();
    m_sTargetAnchorName = sTargetAnchorName;
    return thisAsT ();
  }

  @Override
  @Nullable
  protected PDAction createLinkAction ()
  {
    if (StringHelper.isEmpty (m_sTargetAnchorName))
      return null;
    final PDActionGoTo aAction = new PDActionGoTo ();
    aAction.setDestination (new PDNamedDestination (m_sTargetAnchorName));
    return aAction;
  }

  @Override
  @NonNull
  protected String getMissingActionDebugReason ()
  {
    return "Not rendering an internal link, because no target anchor name is set";
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("TargetAnchorName", m_sTargetAnchorName)
                            .getToString ();
  }
}
