package com.helger.pdflayout.spec;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.font.api.IFontResource;

public final class PreloadFont
{
  public static final PreloadFont REGULAR = PreloadFont.createPredefined (PDType1Font.HELVETICA);
  public static final PreloadFont REGULAR_BOLD = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD);
  public static final PreloadFont REGULAR_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_OBLIQUE);
  public static final PreloadFont REGULAR_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.HELVETICA_BOLD_OBLIQUE);
  public static final PreloadFont MONOSPACE = PreloadFont.createPredefined (PDType1Font.COURIER);
  public static final PreloadFont MONOSPACE_BOLD = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD);
  public static final PreloadFont MONOSPACE_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_OBLIQUE);
  public static final PreloadFont MONOSPACE_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.COURIER_BOLD_OBLIQUE);
  public static final PreloadFont TIMES = PreloadFont.createPredefined (PDType1Font.TIMES_ROMAN);
  public static final PreloadFont TIMES_BOLD = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD);
  public static final PreloadFont TIMES_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_ITALIC);
  public static final PreloadFont TIMES_BOLD_ITALIC = PreloadFont.createPredefined (PDType1Font.TIMES_BOLD_ITALIC);
  public static final PreloadFont SYMBOL = PreloadFont.createPredefined (PDType1Font.SYMBOL);
  public static final PreloadFont ZAPF_DINGBATS = PreloadFont.createPredefined (PDType1Font.ZAPF_DINGBATS);

  private static final Logger s_aLogger = LoggerFactory.getLogger (PreloadFont.class);

  private PDFont m_aFont;
  private final IFontResource m_aFontRes;

  private PreloadFont (@Nullable final PDFont aFont, @Nullable final IFontResource aFontRes)
  {
    m_aFont = aFont;
    m_aFontRes = aFontRes;
  }

  @Nonnull
  public PDFont getAsPDFont (@Nonnull final PDDocument aDoc) throws IOException
  {
    if (m_aFont == null)
    {
      final boolean bEmbed = true;
      switch (m_aFontRes.getFontType ())
      {
        case TTF:
          s_aLogger.info ("Loading TTF font " + m_aFontRes);
          m_aFont = PDType0Font.load (aDoc, m_aFontRes.getInputStream (), bEmbed);
          break;
        case OTF:
          s_aLogger.info ("Loading OTF font " + m_aFontRes);
          m_aFont = PDType0Font.load (aDoc, new OTFParser ().parse (m_aFontRes.getInputStream ()), bEmbed);
          break;
        default:
          throw new IllegalArgumentException ("Cannot load font resources of type " + m_aFontRes.getFontType ());
      }
    }
    return m_aFont;
  }

  @Nonnull
  public static PreloadFont createPredefined (@Nonnull final PDFont aFont)
  {
    ValueEnforcer.notNull (aFont, "Font");
    return new PreloadFont (aFont, null);
  }

  @Nonnull
  public static PreloadFont createEmbedding (@Nonnull final IFontResource aFontRes)
  {
    ValueEnforcer.notNull (aFontRes, "FontRes");
    return new PreloadFont (null, aFontRes);
  }
}
