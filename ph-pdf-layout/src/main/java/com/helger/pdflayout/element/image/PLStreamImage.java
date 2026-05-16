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
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.pdflayout.render.PagePreRenderContext;

/**
 * Represent a static image based on {@link BufferedImage} read from an {@link InputStream}. This is
 * not supported for the image type {@link EPLImageType#LOSSLESS}!
 *
 * @see PLImage
 * @author Philip Helger
 */
public class PLStreamImage extends AbstractPLImage <PLStreamImage>
{
  /**
   * The default maximum number of bytes read from the input stream before the read is aborted.
   * Defaults to 64 MiB. Override globally via {@link #setDefaultMaxImageSize(int)} or per instance
   * via {@link #setMaxImageSize(int)}.
   */
  public static final int DEFAULT_MAX_IMAGE_SIZE = 64 * CGlobal.BYTES_PER_MEGABYTE;

  private static int s_nDefaultMaxImageSize = DEFAULT_MAX_IMAGE_SIZE;

  /**
   * @return The currently active global default for the maximum image byte size in
   *         {@link PLStreamImage}.
   * @since 8.1.2
   */
  @Nonnegative
  public static int getDefaultMaxImageSize ()
  {
    return s_nDefaultMaxImageSize;
  }

  /**
   * Change the global default for the maximum image byte size used by new {@link PLStreamImage}
   * instances. Existing instances are not affected.
   *
   * @param nMaxImageSize
   *        The new maximum size in bytes. Must be &gt; 0.
   * @since 8.1.2
   */
  public static void setDefaultMaxImageSize (@Nonnegative final int nMaxImageSize)
  {
    ValueEnforcer.isGT0 (nMaxImageSize, "MaxImageSize");
    s_nDefaultMaxImageSize = nMaxImageSize;
  }

  private final IHasInputStream m_aIIS;
  private int m_nMaxImageSize = s_nDefaultMaxImageSize;

  public PLStreamImage (@NonNull final IHasInputStream aImage,
                        @Nonnegative final float fImageWidth,
                        @Nonnegative final float fImageHeight)
  {
    super (fImageWidth, fImageHeight);
    ValueEnforcer.notNull (aImage, "Image");

    m_aIIS = aImage;
  }

  /**
   * @return The maximum number of bytes read from the input stream when loading this image.
   * @since 8.1.2
   */
  @Nonnegative
  public int getMaxImageSize ()
  {
    return m_nMaxImageSize;
  }

  /**
   * Override the maximum image size used for this instance. Reading is aborted with an
   * {@link IOException} if the stream supplies more than this many bytes.
   *
   * @param nMaxImageSize
   *        The new maximum size in bytes. Must be &gt; 0.
   * @return this for chaining
   * @since 8.1.2
   */
  @NonNull
  public PLStreamImage setMaxImageSize (@Nonnegative final int nMaxImageSize)
  {
    ValueEnforcer.isGT0 (nMaxImageSize, "MaxImageSize");
    m_nMaxImageSize = nMaxImageSize;
    return this;
  }

  private static byte @NonNull [] _readBounded (@NonNull final InputStream aIS, @Nonnegative final int nMaxBytes)
                                                                                                                  throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      final byte [] aBuf = new byte [8192];
      long nTotal = 0;
      int nRead;
      while ((nRead = aIS.read (aBuf)) > 0)
      {
        nTotal += nRead;
        if (nTotal > nMaxBytes)
          throw new IOException ("Image data exceeds the configured maximum of " + nMaxBytes + " bytes");
        aBAOS.write (aBuf, 0, nRead);
      }
      return aBAOS.toByteArray ();
    }
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
      final byte [] aBytes = _readBounded (aRealIS, m_nMaxImageSize);
      return switch (getImageType ())
      {
        case CCITT -> CCITTFactory.createFromByteArray (aCtx.getDocument (), aBytes);
        case JPEG -> JPEGFactory.createFromByteArray (aCtx.getDocument (), aBytes);
        // API does not support it
        case LOSSLESS -> throw new IllegalStateException ("Lossless images cannot be read from Stream - use the version with BufferedImage!");
        default -> throw new IllegalStateException ("Unsupported image type: " + toString ());
      };
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("IIS", m_aIIS).getToString ();
  }
}
