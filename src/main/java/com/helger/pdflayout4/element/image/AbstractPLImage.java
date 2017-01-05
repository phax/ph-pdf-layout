/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.pdflayout4.element.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.base.AbstractPLInlineElement;
import com.helger.pdflayout4.element.PLRenderHelper;
import com.helger.pdflayout4.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout4.render.PagePreRenderContext;
import com.helger.pdflayout4.render.PageRenderContext;
import com.helger.pdflayout4.render.PreparationContext;
import com.helger.pdflayout4.spec.SizeSpec;

/**
 * Base class for a static image based on {@link BufferedImage}.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractPLImage <IMPLTYPE extends AbstractPLImage <IMPLTYPE>>
                                      extends AbstractPLInlineElement <IMPLTYPE>
{
  private final float m_fImageWidth;
  private final float m_fImageHeight;

  // Status var
  private transient PDImageXObject m_aJpeg;

  public AbstractPLImage (@Nonnegative final float fImageWidth, @Nonnegative final float fImageHeight)
  {
    ValueEnforcer.isGT0 (fImageWidth, "ImageWidth");
    ValueEnforcer.isGT0 (fImageHeight, "ImageHeight");

    m_fImageWidth = fImageWidth;
    m_fImageHeight = fImageHeight;
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public IMPLTYPE setBasicDataFrom (@Nonnull final IMPLTYPE aSource)
  {
    super.setBasicDataFrom (aSource);
    return thisAsT ();
  }

  @Nonnegative
  public float getImageWidth ()
  {
    return m_fImageWidth;
  }

  @Nonnegative
  public float getImageHeight ()
  {
    return m_fImageHeight;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx)
  {
    return new SizeSpec (m_fImageWidth, m_fImageHeight);
  }

  @Override
  protected void onMarkAsNotPrepared ()
  {
    // Nada
  }

  @Nonnull
  protected abstract PDImageXObject getXObject (@Nonnull final PagePreRenderContext aCtx) throws IOException;

  @Override
  @Nonnull
  public EChange beforeRender (@Nonnull final PagePreRenderContext aCtx)
  {
    // It is very important that the PDJpeg is created BEFORE the page content
    // stream is created.
    // http://stackoverflow.com/questions/8521290/cant-add-an-image-to-a-pdf-using-pdfbox
    try
    {
      m_aJpeg = getXObject (aCtx);
      if (m_aJpeg == null)
        throw new IllegalStateException ("Failed to create PDImageXObject");
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Failed to create JPEG", ex);
    }
    return EChange.UNCHANGED;
  }

  @Override
  protected void onRender (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    aContentStream.drawXObject (m_aJpeg,
                                aCtx.getStartLeft () + getOutlineLeft (),
                                aCtx.getStartTop () - getOutlineTop () - m_fImageHeight,
                                m_fImageWidth,
                                m_fImageHeight);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ImageWidth", m_fImageWidth)
                            .append ("ImageHeight", m_fImageHeight)
                            .toString ();
  }
}
