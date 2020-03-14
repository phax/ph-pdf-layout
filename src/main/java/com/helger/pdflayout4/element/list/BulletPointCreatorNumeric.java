package com.helger.pdflayout4.element.list;

import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that uses Arabic digits for
 * numbering and an optional suffix (e.g. a dot).
 *
 * @author Philip Helger
 * @since 5.0.10
 */
public class BulletPointCreatorNumeric implements IBulletPointCreator
{
  private final FontSpec m_aFontSpec;
  private final Function <String, String> m_aFormatter;

  public BulletPointCreatorNumeric (@Nonnull final Function <String, String> aFormatter,
                                    @Nonnull final FontSpec aFontSpec)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    ValueEnforcer.notNull (aFormatter, "Formatter");
    m_aFontSpec = aFontSpec;
    m_aFormatter = aFormatter;
  }

  @Nonnull
  public final Function <String, String> getFormatter ()
  {
    return m_aFormatter;
  }

  @Nonnull
  public String getBulletPointText (@Nonnegative final int nBulletPointIndex)
  {
    // Use 1-based index
    return m_aFormatter.apply (Integer.toString (nBulletPointIndex + 1));
  }

  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @Nonnull
  public PLText getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    return new PLText (getBulletPointText (nBulletPointIndex), m_aFontSpec);
  }
}
