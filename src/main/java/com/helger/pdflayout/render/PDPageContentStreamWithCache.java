/**
 * Copyright (C) 2014 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
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
package com.helger.pdflayout.render;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import com.helger.commons.equals.EqualsUtils;
import com.helger.pdflayout.spec.FontSpec;
import com.helger.pdflayout.spec.LineDashPatternSpec;

/**
 * A special version of PDPageContentStream with an intergated "cache" to avoid
 * setting the same information over and over again.
 * 
 * @author Philip Helger
 */
public class PDPageContentStreamWithCache extends PDPageContentStream
{
  private final PDDocument m_aDocument;
  private final PDPage m_aPage;

  // Status cache
  private FontSpec m_aLastUsedFont;
  private Color m_aLastUsedStrokingColor = Color.BLACK;
  private Color m_aLastUsedNonStrokingColor = Color.BLACK;
  private LineDashPatternSpec m_aLastUsedLineDashPattern = LineDashPatternSpec.SOLID;

  public PDPageContentStreamWithCache (@Nonnull final PDDocument aDocument,
                                       @Nonnull final PDPage aSourcePage,
                                       final boolean bAppendContent,
                                       final boolean bCompress) throws IOException
  {
    super (aDocument, aSourcePage, bAppendContent, bCompress);
    m_aDocument = aDocument;
    m_aPage = aSourcePage;
  }

  @Nonnull
  public PDDocument getDocument ()
  {
    return m_aDocument;
  }

  @Nonnull
  public PDPage getPage ()
  {
    return m_aPage;
  }

  public void setFont (@Nonnull final FontSpec aFont) throws IOException
  {
    if (aFont == null)
      throw new NullPointerException ("font");

    if (m_aLastUsedFont == null ||
        !aFont.getFont ().equals (m_aLastUsedFont.getFont ()) ||
        !EqualsUtils.equals (aFont.getFontSize (), m_aLastUsedFont.getFontSize ()))
    {
      super.setFont (aFont.getFont ().getFont (), aFont.getFontSize ());
      m_aLastUsedFont = aFont;
    }
    setNonStrokingColor (aFont.getColor ());
  }

  @Override
  public void setStrokingColor (@Nonnull final Color aColor) throws IOException
  {
    if (aColor == null)
      throw new NullPointerException ("color");

    if (!m_aLastUsedStrokingColor.equals (aColor))
    {
      super.setStrokingColor (aColor);
      m_aLastUsedStrokingColor = aColor;
    }
  }

  @Nonnull
  public Color getLastUsedStrokingColor ()
  {
    return m_aLastUsedStrokingColor;
  }

  @Override
  public void setNonStrokingColor (@Nonnull final Color aColor) throws IOException
  {
    if (aColor == null)
      throw new NullPointerException ("color");

    if (!m_aLastUsedNonStrokingColor.equals (aColor))
    {
      super.setNonStrokingColor (aColor);
      m_aLastUsedNonStrokingColor = aColor;
    }
  }

  @Nonnull
  public Color getLastUsedNonStrokingColor ()
  {
    return m_aLastUsedNonStrokingColor;
  }

  public void setLineDashPattern (@Nonnull final LineDashPatternSpec aLineDashPattern) throws IOException
  {
    if (aLineDashPattern == null)
      throw new NullPointerException ("LineDashPattern");

    if (!m_aLastUsedLineDashPattern.equals (aLineDashPattern))
    {
      super.setLineDashPattern (aLineDashPattern.getPattern (), aLineDashPattern.getPhase ());
      m_aLastUsedLineDashPattern = aLineDashPattern;
    }
  }

  @Nonnull
  public LineDashPatternSpec getLastUsedLineDashPattern ()
  {
    return m_aLastUsedLineDashPattern;
  }
}
