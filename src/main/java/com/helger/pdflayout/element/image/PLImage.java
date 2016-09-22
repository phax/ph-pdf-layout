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
package com.helger.pdflayout.element.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout.base.AbstractPLElement;
import com.helger.pdflayout.element.PLRenderHelper;
import com.helger.pdflayout.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PagePreRenderContext;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Render an image
 *
 * @author Philip Helger
 */
public class PLImage extends AbstractPLElement <PLImage>
{
  private final BufferedImage m_aImage;
  private final IHasInputStream m_aIIS;
  private final float m_fWidth;
  private final float m_fHeight;

  // Status var
  private PDImageXObject m_aJpeg;

  public PLImage (@Nonnull final BufferedImage aImage)
  {
    this (aImage, aImage.getWidth (), aImage.getHeight ());
  }

  public PLImage (@Nonnull final BufferedImage aImage,
                  @Nonnegative final float fWidth,
                  @Nonnegative final float fHeight)
  {
    ValueEnforcer.notNull (aImage, "Image");
    ValueEnforcer.isGT0 (fWidth, "Width");
    ValueEnforcer.isGT0 (fHeight, "Height");

    m_aImage = aImage;
    m_aIIS = null;
    m_fWidth = fWidth;
    m_fHeight = fHeight;
  }

  public PLImage (@Nonnull final IHasInputStream aImage,
                  @Nonnegative final float fWidth,
                  @Nonnegative final float fHeight)
  {
    ValueEnforcer.notNull (aImage, "Image");
    ValueEnforcer.isGT0 (fWidth, "Width");
    ValueEnforcer.isGT0 (fHeight, "Height");

    m_aImage = null;
    m_aIIS = aImage;
    m_fWidth = fWidth;
    m_fHeight = fHeight;
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLImage setBasicDataFrom (@Nonnull final PLImage aSource)
  {
    super.setBasicDataFrom (aSource);
    return this;
  }

  @Nullable
  public BufferedImage getImage ()
  {
    return m_aImage;
  }

  @Nullable
  public IHasInputStream getIIS ()
  {
    return m_aIIS;
  }

  public float getWidth ()
  {
    return m_fWidth;
  }

  public float getHeight ()
  {
    return m_fHeight;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    return new SizeSpec (m_fWidth, m_fHeight);
  }

  @Override
  public void beforeRender (@Nonnull final PagePreRenderContext aCtx)
  {
    // It is very important that the PDJpeg is created BEFORE the page content
    // stream is created.
    // http://stackoverflow.com/questions/8521290/cant-add-an-image-to-a-pdf-using-pdfbox
    try
    {
      if (m_aIIS != null)
      {
        // The input stream is closed automatically
        m_aJpeg = JPEGFactory.createFromStream (aCtx.getDocument (), m_aIIS.getInputStream ());
      }
      else
      {
        m_aJpeg = JPEGFactory.createFromImage (aCtx.getDocument (), m_aImage);
      }
    }
    catch (final IOException ex)
    {
      throw new IllegalArgumentException ("Failed to create JPEG", ex);
    }
  }

  @Override
  protected void onPerform (@Nonnull final PageRenderContext aCtx) throws IOException
  {
    // Fill and border
    PLRenderHelper.fillAndRenderBorder (this, aCtx, 0f, 0f);

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    aContentStream.drawXObject (m_aJpeg,
                                aCtx.getStartLeft () + getMarginLeft (),
                                aCtx.getStartTop () - getMarginTop () - m_fHeight,
                                m_fWidth,
                                m_fHeight);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Image", m_aImage)
                            .append ("IIS", m_aIIS)
                            .append ("Width", m_fWidth)
                            .append ("Height", m_fHeight)
                            .toString ();
  }
}
