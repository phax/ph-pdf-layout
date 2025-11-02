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
package com.helger.pdflayout.element.link;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.element.box.AbstractPLInlineBox;
import com.helger.pdflayout.link.ELinkBorderStyle;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.spec.LineDashPatternSpec;

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
                                             AbstractPLInlineBox <IMPLTYPE>
{
  public static final ELinkBorderStyle DEFAULT_LINK_BORDER_STYLE = ELinkBorderStyle.SOLID;
  public static final LineDashPatternSpec DEFAULT_LINK_DASH_PATTERN = null;
  public static final float DEFAULT_LINK_BORDER_WIDTH = 0f;
  public static final PLColor DEFAULT_LINK_COLOR = null;

  private String m_sURI;
  // These are parameterized in preparation for eventual future actions. Until
  // then, always use the existing "border" functionality
  private ELinkBorderStyle m_eLinkBorderStyle = DEFAULT_LINK_BORDER_STYLE;
  private LineDashPatternSpec m_aLinkDashPattern = DEFAULT_LINK_DASH_PATTERN;
  private float m_fLinkBorderWidth = DEFAULT_LINK_BORDER_WIDTH;
  private PLColor m_aLinkColor = DEFAULT_LINK_COLOR;

  public AbstractPLExternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
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
   * Set the URI to link to.
   *
   * @param sURI
   *        The URI to link to. May be <code>null</code>.
   * @return this for chaining.
   */
  @NonNull
  public final IMPLTYPE setURI (@Nullable final String sURI)
  {
    internalCheckNotPrepared ();
    m_sURI = sURI;
    return thisAsT ();
  }

  /**
   * @return The link border style to use. May be <code>null</code>.
   * @since v7.4.1
   */
  @Nullable
  public ELinkBorderStyle getLinkBorderStyle ()
  {
    return m_eLinkBorderStyle;
  }

  /**
   * Set the link border style to use.
   *
   * @param eLinkBorderStyle
   *        The border style to use. May be <code>null</code>.
   * @return this for chaining.
   * @since v7.4.1
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
   * @since v7.4.1
   */
  @Nullable
  public LineDashPatternSpec getLinkDashPattern ()
  {
    return m_aLinkDashPattern;
  }

  /**
   * Set the link dash pattern to use.
   *
   * @param aLinkDashPattern
   *        The link dash pattern to use. May be <code>null</code>.
   * @return this for chaining.
   * @since v7.4.1
   */
  @NonNull
  public final IMPLTYPE setLinkDashPattern (@Nullable final LineDashPatternSpec aLinkDashPattern)
  {
    internalCheckNotPrepared ();
    m_aLinkDashPattern = aLinkDashPattern;
    return thisAsT ();
  }

  /**
   * @return The link border width to use. May be <code>null</code>.
   * @since v7.4.1
   */
  public float getLinkBorderWidth ()
  {
    return m_fLinkBorderWidth;
  }

  /**
   * Set the link border width to use.
   *
   * @param fLinkBorderWidth
   *        The link border width to use. May be <code>null</code>.
   * @return this for chaining.
   * @since v7.4.1
   */
  @NonNull
  public final IMPLTYPE setLinkBorderWidth (final float fLinkBorderWidth)
  {
    internalCheckNotPrepared ();
    m_fLinkBorderWidth = fLinkBorderWidth;
    return thisAsT ();
  }

  /**
   * @return The link color to use. May be <code>null</code>.
   * @since v7.4.1
   */
  @Nullable
  public PLColor getLinkColor ()
  {
    return m_aLinkColor;
  }

  /**
   * Set the link color to use.
   *
   * @param aLinkColor
   *        The link color to use. May be <code>null</code>.
   * @return this for chaining.
   * @since v7.4.1
   */
  @NonNull
  public final IMPLTYPE setLinkDashPattern (@Nullable final PLColor aLinkColor)
  {
    internalCheckNotPrepared ();
    m_aLinkColor = aLinkColor;
    return thisAsT ();
  }

  @Override
  protected void onRender (@NonNull final PageRenderContext aCtx) throws IOException
  {
    super.onRender (aCtx);

    final IPLRenderableObject <?> aElement = getElement ();
    if (aElement != null)
    {
      if (StringHelper.isNotEmpty (m_sURI))
      {
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

        // Destination URI
        final PDActionURI aAction = new PDActionURI ();
        aAction.setURI (m_sURI);
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
      else
        PLDebugLog.debugRender (this, "Not rendering an external link, because no URI is present");
    }
    else
      PLDebugLog.debugRender (this, "Not rendering an external link, because no element is contained");
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("URI", m_sURI)
                            .appendIfNotNull ("LinkBorderStyle", m_eLinkBorderStyle)
                            .appendIfNotNull ("LinkDashPattern", m_aLinkDashPattern)
                            .append ("LinkBorderWidth", m_fLinkBorderWidth)
                            .appendIfNotNull ("LinkColor", m_aLinkColor)
                            .getToString ();
  }
}
