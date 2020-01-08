/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.render.PagePreRenderContext;

/**
 * Represent a static image based on {@link BufferedImage}. This image type is
 * supported for all {@link EPLImageType}s!
 * 
 * @see PLStreamImage
 * @author Philip Helger
 */
public class PLImage extends AbstractPLImage <PLImage>
{
  private final BufferedImage m_aImage;

  public PLImage (@Nonnull final BufferedImage aImage)
  {
    this (aImage, aImage.getWidth (), aImage.getHeight ());
  }

  public PLImage (@Nonnull final BufferedImage aImage,
                  @Nonnegative final float fImageWidth,
                  @Nonnegative final float fImageHeight)
  {
    super (fImageWidth, fImageHeight);
    ValueEnforcer.notNull (aImage, "Image");

    m_aImage = aImage;
  }

  @Override
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

  @Override
  @Nonnull
  protected PDImageXObject getXObject (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {
    switch (getImageType ())
    {
      case CCITT:
        return CCITTFactory.createFromImage (aCtx.getDocument (), m_aImage);
      case JPEG:
        return JPEGFactory.createFromImage (aCtx.getDocument (), m_aImage);
      case LOSSLESS:
        return LosslessFactory.createFromImage (aCtx.getDocument (), m_aImage);
      default:
        throw new IllegalStateException ("Unsupported image type: " + toString ());
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Image", m_aImage).getToString ();
  }
}
