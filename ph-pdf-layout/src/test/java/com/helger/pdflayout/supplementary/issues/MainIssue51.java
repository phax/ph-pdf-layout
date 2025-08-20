/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.supplementary.issues;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.pdflayout.PageLayoutPDF;
import com.helger.pdflayout.base.AbstractPLRenderableObject;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.base.PLPageSet;
import com.helger.pdflayout.debug.PLDebugLog;
import com.helger.pdflayout.debug.PLDebugRender;
import com.helger.pdflayout.render.PageRenderContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.spec.SizeSpec;

import jakarta.annotation.Nonnull;

/**
 * Test for issue 49
 *
 * @author Philip Helger
 */
public final class MainIssue51
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainIssue51.class);

  private static final class PLRenderDirectly extends AbstractPLRenderableObject <PLRenderDirectly>
  {
    private final SizeSpec m_aPreparedSize;

    public PLRenderDirectly (@Nonnull final SizeSpec aPreparedSize)
    {
      m_aPreparedSize = aPreparedSize;
    }

    @Override
    protected SizeSpec onPrepare (final PreparationContext aCtx)
    {
      return m_aPreparedSize;
    }

    @Override
    protected void onMarkAsNotPrepared ()
    {
      // empty
    }

    @Override
    protected void onRender (final PageRenderContext aCtx) throws IOException
    {
      // Border starts after margin
      final float fLeft = aCtx.getStartLeft ();
      final float fTop = aCtx.getStartTop ();
      final float fWidth = m_aPreparedSize.getWidth ();
      final float fHeight = m_aPreparedSize.getHeight ();

      final PLColor aFillColor = PLColor.GREEN;
      aCtx.getContentStream ().setNonStrokingColor (aFillColor);
      aCtx.getContentStream ().fillRect (fLeft, fTop - fHeight, fWidth, fHeight);
    }
  }

  public static void main (final String [] args)
  {
    if (false)
    {
      PLDebugRender.setDebugRender (true);
      PLDebugLog.setDebugSplit (true);
    }

    final PLPageSet aPS1 = new PLPageSet (PDRectangle.A4).setMargin (30);
    final PageLayoutPDF aPageLayout = new PageLayoutPDF ();
    aPageLayout.addPageSet (aPS1);

    aPS1.addElement (new PLRenderDirectly (new SizeSpec (200, 200)));

    final String outFileString = "target/issue51.pdf";
    final File outFile = new File (outFileString);
    try
    {
      aPageLayout.renderTo (outFile);
      LOGGER.info ("Done, file written to: " + outFile.getAbsolutePath ());
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error", ex);
    }
  }
}
