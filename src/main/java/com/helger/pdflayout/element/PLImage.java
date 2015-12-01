/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

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
import com.helger.pdflayout.render.PDPageContentStreamWithCache;
import com.helger.pdflayout.render.PageSetupContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.EHorzAlignment;
import com.helger.pdflayout.spec.EVertAlignment;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Render an image
 *
 * @author Philip Helger
 */
public class PLImage extends AbstractPLElement <PLImage>implements IPLHasHorizontalAlignment <PLImage>, IPLHasVerticalAlignment <PLImage>
{
  public static final EHorzAlignment DEFAULT_HORZ_ALIGNMENT = EHorzAlignment.DEFAULT;
  public static final EVertAlignment DEFAULT_VERT_ALIGNMENT = EVertAlignment.DEFAULT;

  private final BufferedImage m_aImage;
  private final IHasInputStream m_aIIS;
  private final float m_fWidth;
  private final float m_fHeight;
  private EHorzAlignment m_eHorzAlign = DEFAULT_HORZ_ALIGNMENT;
  private EVertAlignment m_eVertAlign = DEFAULT_VERT_ALIGNMENT;

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

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public PLImage setBasicDataFrom (@Nonnull final PLImage aSource)
  {
    super.setBasicDataFrom (aSource);
    setHorzAlign (aSource.m_eHorzAlign);
    setVertAlign (aSource.m_eVertAlign);
    return this;
  }

  @Nonnull
  public EHorzAlignment getHorzAlign ()
  {
    return m_eHorzAlign;
  }

  @Nonnull
  public PLImage setHorzAlign (@Nonnull final EHorzAlignment eHorzAlign)
  {
    m_eHorzAlign = ValueEnforcer.notNull (eHorzAlign, "HorzAlign");
    return this;
  }

  @Nonnull
  public EVertAlignment getVertAlign ()
  {
    return m_eVertAlign;
  }

  @Nonnull
  public PLImage setVertAlign (@Nonnull final EVertAlignment eVertAlign)
  {
    m_eVertAlign = ValueEnforcer.notNull (eVertAlign, "VertAlign");
    return this;
  }

  @Override
  protected SizeSpec onPrepare (@Nonnull final PreparationContext aCtx) throws IOException
  {
    return new SizeSpec (aCtx.getAvailableWidth (), m_fHeight);
  }

  @Override
  public void doPageSetup (@Nonnull final PageSetupContext aCtx)
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
  protected void onPerform (@Nonnull final RenderingContext aCtx) throws IOException
  {
    final PDPageContentStreamWithCache aContentStream = aCtx.getContentStream ();

    final float fLeft = getPaddingLeft ();
    final float fUsableWidth = aCtx.getWidth () - getPaddingXSum ();
    float fIndentX;
    switch (m_eHorzAlign)
    {
      case LEFT:
        fIndentX = fLeft;
        break;
      case CENTER:
        fIndentX = fLeft + (fUsableWidth - m_fWidth) / 2;
        break;
      case RIGHT:
        fIndentX = fLeft + fUsableWidth - m_fWidth;
        break;
      default:
        throw new IllegalStateException ("Unsupported horizontal alignment " + m_eHorzAlign);
    }

    aContentStream.drawXObject (m_aJpeg,
                                aCtx.getStartLeft () + fIndentX,
                                aCtx.getStartTop () - getPaddingTop () - m_fHeight,
                                m_fWidth,
                                m_fHeight);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("image", m_aImage)
                            .append ("width", m_fWidth)
                            .append ("height", m_fHeight)
                            .append ("horzAlign", m_eHorzAlign)
                            .append ("vertAlign", m_eVertAlign)
                            .toString ();
  }
}
