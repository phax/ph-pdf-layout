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

import java.io.IOException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.element.box.AbstractPLInlineBox;
import com.helger.pdflayout.link.ELinkBorderStyle;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.spec.LineDashPatternSpec;

/**
 * Common base class for PDF link annotations wrapping another PL element. Provides the link
 * annotation rectangle math plus link styling (border, dash pattern, width, color). Subclasses
 * supply the {@link PDAction} that determines where the link points - a URI for
 * {@link AbstractPLExternalLink}, an in-document destination for {@link AbstractPLInternalLink}.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 8.2.0
 */
public abstract class AbstractPLLinkBase <IMPLTYPE extends AbstractPLLinkBase <IMPLTYPE>> extends
                                         AbstractPLInlineBox <IMPLTYPE>
{
  public static final ELinkBorderStyle DEFAULT_LINK_BORDER_STYLE = ELinkBorderStyle.SOLID;
  public static final LineDashPatternSpec DEFAULT_LINK_DASH_PATTERN = null;
  public static final float DEFAULT_LINK_BORDER_WIDTH = 0f;
  public static final PLColor DEFAULT_LINK_COLOR = null;

  private ELinkBorderStyle m_eLinkBorderStyle = DEFAULT_LINK_BORDER_STYLE;
  private LineDashPatternSpec m_aLinkDashPattern = DEFAULT_LINK_DASH_PATTERN;
  private float m_fLinkBorderWidth = DEFAULT_LINK_BORDER_WIDTH;
  private PLColor m_aLinkColor = DEFAULT_LINK_COLOR;

  public AbstractPLLinkBase (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  /**
   * @return The link border style to use. May be <code>null</code>.
   */
  @Nullable
  public final ELinkBorderStyle getLinkBorderStyle ()
  {
    return m_eLinkBorderStyle;
  }

  /**
   * Set the link border style to use.
   *
   * @param eLinkBorderStyle
   *        The border style to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setLinkBorderStyle (@Nullable final ELinkBorderStyle eLinkBorderStyle)
  {
    internalCheckNotPrepared ();
    m_eLinkBorderStyle = eLinkBorderStyle;
    return thisAsT ();
  }

  /**
   * @return The link dash pattern to use. May be <code>null</code>.
   */
  @Nullable
  public final LineDashPatternSpec getLinkDashPattern ()
  {
    return m_aLinkDashPattern;
  }

  /**
   * Set the link dash pattern to use.
   *
   * @param aLinkDashPattern
   *        The link dash pattern to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setLinkDashPattern (@Nullable final LineDashPatternSpec aLinkDashPattern)
  {
    internalCheckNotPrepared ();
    m_aLinkDashPattern = aLinkDashPattern;
    return thisAsT ();
  }

  /**
   * @return The link border width.
   */
  public final float getLinkBorderWidth ()
  {
    return m_fLinkBorderWidth;
  }

  /**
   * Set the link border width.
   *
   * @param fLinkBorderWidth
   *        The link border width.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setLinkBorderWidth (final float fLinkBorderWidth)
  {
    internalCheckNotPrepared ();
    m_fLinkBorderWidth = fLinkBorderWidth;
    return thisAsT ();
  }

  /**
   * @return The link color. May be <code>null</code>.
   */
  @Nullable
  public final PLColor getLinkColor ()
  {
    return m_aLinkColor;
  }

  /**
   * Set the link color.
   *
   * @param aLinkColor
   *        The link color. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE setLinkColor (@Nullable final PLColor aLinkColor)
  {
    internalCheckNotPrepared ();
    m_aLinkColor = aLinkColor;
    return thisAsT ();
  }

  /**
   * @return The PDF action describing where this link points, or <code>null</code> if no action can
   *         be constructed (for example, when no target has been set yet). If <code>null</code>,
   *         the link annotation is not added to the page.
   */
  @Nullable
  protected abstract PDAction createLinkAction ();

  /**
   * Hook for the human-readable description used in debug logging when no action can be created.
   *
   * @return A short description of why the link could not be created, used for debug output.
   */
  @NonNull
  protected abstract String getMissingActionDebugReason ();

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    super.onRender (aCtx);

    final IPLRenderableObject <?> aElement = getElement ();
    if (aElement == null)
    {
      PLDebugLog.debugRender (this, "Not rendering a link, because no element is contained");
      return;
    }

    final PDAction aAction = createLinkAction ();
    if (aAction == null)
    {
      PLDebugLog.debugRender (this, getMissingActionDebugReason ());
      return;
    }

    final PDAnnotationLink aLink = new PDAnnotationLink ();

    // border of the link
    final PDBorderStyleDictionary aLinkBorder = new PDBorderStyleDictionary ();
    if (m_eLinkBorderStyle != null)
      aLinkBorder.setStyle (m_eLinkBorderStyle.getID ());
    if (m_aLinkDashPattern != null)
      aLinkBorder.setDashStyle (m_aLinkDashPattern.getPatternCOSArray ());
    aLinkBorder.setWidth (m_fLinkBorderWidth);
    aLink.setBorderStyle (aLinkBorder);

    if (m_aLinkColor != null)
    {
      // Border color
      aLink.setColor (m_aLinkColor.getAsPDColor ());
    }

    aLink.setAction (aAction);

    // Position as lower left based coordinates (not top left)
    final float fHeight = getRenderHeight () + getBorderYSumWidth () + getPaddingYSum ();
    final PDRectangle aLowerLeft = new PDRectangle (aCtx.getStartLeft () + getMarginLeft (),
                                                    aCtx.getStartTop () - getMarginTop () - fHeight,
                                                    getRenderWidth () + getBorderXSumWidth () + getPaddingXSum (),
                                                    fHeight);
    aLink.setRectangle (aLowerLeft);

    aCtx.getContentStream ().getPage ().getAnnotations ().add (aLink);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("LinkBorderStyle", m_eLinkBorderStyle)
                            .appendIfNotNull ("LinkDashPattern", m_aLinkDashPattern)
                            .append ("LinkBorderWidth", m_fLinkBorderWidth)
                            .appendIfNotNull ("LinkColor", m_aLinkColor)
                            .getToString ();
  }
}
