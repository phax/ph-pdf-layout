package com.helger.pdflayout.element.link;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.helger.commons.string.StringHelper;
import com.helger.pdflayout.base.IPLRenderableObject;
import com.helger.pdflayout.element.box.AbstractPLInlineBox;
import com.helger.pdflayout.link.ELinkBorderStyle;
import com.helger.pdflayout.render.PageRenderContext;

public class PLExternalLink extends AbstractPLInlineBox <PLExternalLink>
{
  private String m_sURI;

  public PLExternalLink ()
  {
    super (null);
  }

  public PLExternalLink (@Nullable final IPLRenderableObject <?> aElement)
  {
    super (aElement);
  }

  @Nullable
  public final String getURI ()
  {
    return m_sURI;
  }

  @Nonnull
  public final PLExternalLink setURI (@Nullable final String sURI)
  {
    internalCheckNotPrepared ();
    m_sURI = sURI;
    return this;
  }

  @Override
  @Nonnull
  public PLExternalLink internalCreateNewVertSplitObject (@Nonnull final PLExternalLink aBase)
  {
    final PLExternalLink ret = new PLExternalLink ();
    ret.setBasicDataFrom (aBase);
    return ret;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    super.onRender (aCtx);

    final IPLRenderableObject <?> aElement = getElement ();
    if (aElement != null && StringHelper.hasText (m_sURI))
    {
      final PDAnnotationLink aLink = new PDAnnotationLink ();

      // No border on the link
      final PDBorderStyleDictionary aLinkBorder = new PDBorderStyleDictionary ();
      aLinkBorder.setStyle (ELinkBorderStyle.SOLID.getID ());
      aLinkBorder.setWidth (0);
      aLink.setBorderStyle (aLinkBorder);

      // Destination URI
      final PDActionURI aAction = new PDActionURI ();
      aAction.setURI (m_sURI);
      aLink.setAction (aAction);

      // Position
      final float fHeight = getRenderHeight () + getBorderYSumWidth () + getPaddingYSum ();
      final PDRectangle aLowerLeft = new PDRectangle (aCtx.getStartLeft () + getMarginLeft (),
                                                      aCtx.getStartTop () - getMarginTop () - fHeight,
                                                      getRenderWidth () + getBorderXSumWidth () + getPaddingXSum (),
                                                      fHeight);
      aLink.setRectangle (aLowerLeft);

      aCtx.getContentStream ().getPage ().getAnnotations ().add (aLink);
    }
  }
}
