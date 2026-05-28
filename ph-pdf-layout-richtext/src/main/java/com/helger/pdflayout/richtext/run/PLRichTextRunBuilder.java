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
package com.helger.pdflayout.richtext.run;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.pdflayout.base.PLColor;
import com.helger.pdflayout.richtext.annotation.IPLRichTextAnnotation;
import com.helger.pdflayout.richtext.markup.IPLMarkupToken;
import com.helger.pdflayout.richtext.markup.PLMarkupParser;
import com.helger.pdflayout.spec.FontSpec;

/**
 * Walks a token stream produced by {@link PLMarkupParser} and emits the
 * corresponding list of {@link PLRichTextRun}s. State is kept locally (bold
 * flag, italic flag, current color, active annotations) and folded into the
 * font spec / annotations of each emitted run.
 * <p>
 * The builder intentionally emits {@link PLRichTextRun}s that may contain
 * embedded {@code '\n'} characters — line wrapping is the layout engine's job,
 * not the parser's.
 *
 * @author Philip Helger
 */
public final class PLRichTextRunBuilder
{
  private final PLFontFamily m_aFontFamily;
  private final float m_fFontSize;
  private final PLColor m_aDefaultColor;

  public PLRichTextRunBuilder (@NonNull final PLFontFamily aFontFamily, final float fFontSize)
  {
    this (aFontFamily, fFontSize, FontSpec.DEFAULT_COLOR);
  }

  public PLRichTextRunBuilder (@NonNull final PLFontFamily aFontFamily,
                               final float fFontSize,
                               @NonNull final PLColor aDefaultColor)
  {
    ValueEnforcer.notNull (aFontFamily, "FontFamily");
    ValueEnforcer.isGT0 (fFontSize, "FontSize");
    ValueEnforcer.notNull (aDefaultColor, "DefaultColor");
    m_aFontFamily = aFontFamily;
    m_fFontSize = fFontSize;
    m_aDefaultColor = aDefaultColor;
  }

  /**
   * Walks the given token stream and produces runs.
   *
   * @param aTokens
   *        the parsed markup tokens.
   * @return the list of runs.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <PLRichTextRun> build (@NonNull final ICommonsList <IPLMarkupToken> aTokens)
  {
    final ICommonsList <PLRichTextRun> aResult = new CommonsArrayList <> ();
    boolean bBold = false;
    boolean bItalic = false;
    PLColor aColor = m_aDefaultColor;
    // Active annotations keyed by type so a closing toggle of the same type pops.
    final Map <Class <? extends IPLRichTextAnnotation>, IPLRichTextAnnotation> aActiveAnnotations = new HashMap <> ();

    for (final IPLMarkupToken aToken : aTokens)
    {
      if (aToken instanceof final IPLMarkupToken.Text aText)
      {
        aResult.add (_makeRun (aText.getText (), bBold, bItalic, aColor, aActiveAnnotations));
      }
      else
        if (aToken instanceof IPLMarkupToken.NewLine)
        {
          aResult.add (_makeRun ("\n", bBold, bItalic, aColor, aActiveAnnotations));
        }
        else
          if (aToken instanceof IPLMarkupToken.BoldToggle)
          {
            bBold = !bBold;
          }
          else
            if (aToken instanceof IPLMarkupToken.ItalicToggle)
            {
              bItalic = !bItalic;
            }
            else
              if (aToken instanceof final IPLMarkupToken.Color aColorToken)
              {
                aColor = aColorToken.getColor ();
              }
              else
                if (aToken instanceof final IPLMarkupToken.AnnotationToggle aToggle)
                {
                  final Class <? extends IPLRichTextAnnotation> aType = aToggle.getAnnotationType ();
                  if (aActiveAnnotations.containsKey (aType))
                    aActiveAnnotations.remove (aType);
                  else
                    aActiveAnnotations.put (aType, aToggle.getAnnotation ());
                }
    }
    return aResult;
  }

  /**
   * Convenience: parse the markup and build the runs in one step.
   *
   * @param sMarkup
   *        the markup string.
   * @return the list of runs.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <PLRichTextRun> buildFromMarkup (@NonNull final String sMarkup)
  {
    return build (new PLMarkupParser ().parse (sMarkup));
  }

  @NonNull
  private PLRichTextRun _makeRun (@NonNull final String sText,
                                  final boolean bBold,
                                  final boolean bItalic,
                                  @NonNull final PLColor aColor,
                                  @NonNull final Map <Class <? extends IPLRichTextAnnotation>, IPLRichTextAnnotation> aActiveAnnotations)
  {
    final FontSpec aFontSpec = new FontSpec (m_aFontFamily.resolve (bBold, bItalic), m_fFontSize, aColor);
    final ICommonsList <IPLRichTextAnnotation> aAnnotations = aActiveAnnotations.isEmpty () ? null
                                                                                            : new CommonsArrayList <> (aActiveAnnotations.values ());
    return new PLRichTextRun (sText, aFontSpec, aAnnotations);
  }
}
