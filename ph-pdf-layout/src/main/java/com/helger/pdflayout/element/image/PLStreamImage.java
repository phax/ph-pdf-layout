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
package com.helger.pdflayout.element.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.render.PagePreRenderContext;

/**
 * Represent a static image based on {@link BufferedImage} read from an
 * {@link InputStream}. This is not supported for the image type
 * {@link EPLImageType#LOSSLESS}!
 *
 * @see PLImage
 * @author Philip Helger
 */
public class PLStreamImage extends AbstractPLImage <PLStreamImage>
{
  private final IHasInputStream m_aIIS;

  public PLStreamImage (@NonNull final IHasInputStream aImage, @Nonnegative final float fImageWidth, @Nonnegative final float fImageHeight)
  {
    super (fImageWidth, fImageHeight);
    ValueEnforcer.notNull (aImage, "Image");

    m_aIIS = aImage;
  }

  @Override
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public PLStreamImage setBasicDataFrom (@NonNull final PLStreamImage aSource)
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
  @NonNull
  protected PDImageXObject getXObject (@NonNull final PagePreRenderContext aCtx) throws IOException
  {
    // The input stream is only sometimes closed automatically
    final InputStream aIS = m_aIIS.getInputStream ();
    if (aIS == null)
      throw new IOException ("Failed to open InputStream from " + m_aIIS);

    try (final InputStream aRealIS = aIS)
    {
      final byte [] aBytes = StreamHelper.getAllBytes (aRealIS);
      switch (getImageType ())
      {
        case CCITT:
          return CCITTFactory.createFromByteArray (aCtx.getDocument (), aBytes);
        case JPEG:
          return JPEGFactory.createFromByteArray (aCtx.getDocument (), aBytes);
        case LOSSLESS:
          // API does not support it
          throw new IllegalStateException ("Lossless images cannot be read from Stream - use the version with BufferedImage!");
        default:
          throw new IllegalStateException ("Unsupported image type: " + toString ());
      }
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("IIS", m_aIIS).getToString ();
  }
}
