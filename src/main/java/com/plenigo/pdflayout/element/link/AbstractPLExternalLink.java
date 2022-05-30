/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.plenigo.pdflayout.element.link;

import com.helger.commons.string.StringHelper;
import com.plenigo.pdflayout.base.IPLRenderableObject;
import com.plenigo.pdflayout.debug.PLDebugLog;
import com.plenigo.pdflayout.element.box.AbstractPLInlineBox;
import com.plenigo.pdflayout.link.ELinkBorderStyle;
import com.plenigo.pdflayout.render.PageRenderContext;
import com.plenigo.pdflayout.spec.LineDashPatternSpec;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.awt.*;
import java.io.IOException;

/**
 * An external link that references to an external URI. Use
 * {@link #setURI(String)} to define the link target.
 *
 * @param <IMPLTYPE> Implementation type
 *
 * @author Philip Helger
 * @since 6.0.1
 */
public abstract class AbstractPLExternalLink<IMPLTYPE extends AbstractPLExternalLink<IMPLTYPE>> extends AbstractPLInlineBox<IMPLTYPE> {
    private String m_sURI;
    // These are parameterized in preparation for eventual future actions. Until
    // then, always use the existing "border" functionality
    private final ELinkBorderStyle m_eLinkBorderStyle = ELinkBorderStyle.SOLID;
    private final LineDashPatternSpec m_aLinkDashPattern = null;
    private final float m_fLinkBorderWidth = 0;
    private final Color m_aLinkColor = null;

    public AbstractPLExternalLink(@Nullable final IPLRenderableObject<?> aElement) {
        super(aElement);
    }

    @Override
    @Nonnull
    @OverridingMethodsMustInvokeSuper
    public IMPLTYPE setBasicDataFrom(@Nonnull final IMPLTYPE aSource) {
        super.setBasicDataFrom(aSource);
        setURI(aSource.getURI());
        return thisAsT();
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
  @Nonnull
  public final IMPLTYPE setURI (@Nullable final String sURI)
  {
    internalCheckNotPrepared ();
    m_sURI = sURI;
    return thisAsT ();
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    super.onRender (aCtx);

    final IPLRenderableObject <?> aElement = getElement ();
    if (aElement != null)
    {
      if (StringHelper.hasText (m_sURI))
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
          final float [] components = new float [] { m_aLinkColor.getRed () / 255f,
                                                     m_aLinkColor.getGreen () / 255f,
                                                     m_aLinkColor.getBlue () / 255f };
          aLink.setColor (new PDColor (components, PDDeviceRGB.INSTANCE));
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
}
