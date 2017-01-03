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
package com.helger.pdflayout4.element.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.render.PagePreRenderContext;

/**
 * Represent a static image based on {@link BufferedImage} read from an
 * {@link InputStream}.
 *
 * @author Philip Helger
 */
public class PLStreamImage extends AbstractPLImage <PLStreamImage>
{
  private final IHasInputStream m_aIIS;

  public PLStreamImage (@Nonnull final IHasInputStream aImage,
                        @Nonnegative final float fImageWidth,
                        @Nonnegative final float fImageHeight)
  {
    super (fImageWidth, fImageHeight);
    ValueEnforcer.notNull (aImage, "Image");

    m_aIIS = aImage;
  }

  @Override
  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLStreamImage setBasicDataFrom (@Nonnull final PLStreamImage aSource)
  {
    super.setBasicDataFrom (aSource);
    return this;
  }

  @Nullable
  public IHasInputStream getIIS ()
  {
    return m_aIIS;
  }

  @Override
  @Nonnull
  protected PDImageXObject getXObject (@Nonnull final PagePreRenderContext aCtx) throws IOException
  {
    // The input stream is closed automatically
    final InputStream aIS = m_aIIS.getInputStream ();
    return JPEGFactory.createFromStream (aCtx.getDocument (), aIS);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("IIS", m_aIIS).toString ();
  }
}
