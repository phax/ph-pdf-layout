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
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.pdflayout4.base.AbstractPLInlineElement;
import com.helger.pdflayout4.pdfbox.PDPageContentStreamWithCache;
import com.helger.pdflayout4.render.PLRenderHelper;
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
public abstract class AbstractPLImage <IMPLTYPE extends AbstractPLImage <IMPLTYPE>> extends
                                      AbstractPLInlineElement <IMPLTYPE>
{
  public static final EPLImageType DEFAULT_IMAGE_TYPE = EPLImageType.JPEG;

  private final float m_fImageWidth;
  private final float m_fImageHeight;
  private EPLImageType m_eImageType = DEFAULT_IMAGE_TYPE;

  // Status var
  private transient PDImageXObject m_aXObject;

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
  public final float getImageWidth ()
  {
    return m_fImageWidth;
  }

  @Nonnegative
  public final float getImageHeight ()
  {
    return m_fImageHeight;
  }

  /**
   * @return The image type to use. Never <code>null</code>. The default is
   *         {@link #DEFAULT_IMAGE_TYPE}.
   * @see #setImageType(EPLImageType)
   * @since 5.0.1
   */
  @Nonnull
  public final EPLImageType getImageType ()
  {
    return m_eImageType;
  }

  /**
   * Set the image type to be used. <br>
   * Note: not all image types may be supported by all subclasses of this class.
   * Please check the respective documentation!
   *
   * @param eImageType
   *        The image type to be used. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setImageType (@Nonnull final EPLImageType eImageType)
  {
    ValueEnforcer.notNull (eImageType, "ImageType");
    m_eImageType = eImageType;
    return thisAsT ();
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

  /**
   * Resolve the {@link PDImageXObject} for rendering. Must consider the image
   * type according to {@link #getImageType()}.
   *
   * @param aCtx
   *        Render context
   * @return Never <code>null</code>.
   * @throws IOException
   *         In case of error.
   */
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
      m_aXObject = getXObject (aCtx);
      if (m_aXObject == null)
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
    PLRenderHelper.fillAndRenderBorder (thisAsT (), aCtx, 0f, 0f);

    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();
    aContentStream.drawXObject (m_aXObject,
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
                            .append ("ImageType", m_eImageType)
                            .getToString ();
  }
}
