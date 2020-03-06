package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that uses Arabic digits for
 * numbering and an optional suffix (e.g. a dot).
 *
 * @author Philip Helger
 */
public class BulletPointCreatorNumeric implements IBulletPointCreator
{
  private final FontSpec m_aFontSpec;
  private final String m_sSuffix;

  public BulletPointCreatorNumeric (@Nonnull final FontSpec aFontSpec, @Nonnull final String sSuffix)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    ValueEnforcer.notNull (sSuffix, "Suffix");
    m_aFontSpec = aFontSpec;
    m_sSuffix = sSuffix;
  }

  @Nonnull
  public final String getSuffix ()
  {
    return m_sSuffix;
  }

  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @Nonnull
  public String getBulletPointText (@Nonnegative final int nBulletPointIndex)
  {
    // Use 1-based index
    return Integer.toString (nBulletPointIndex + 1) + m_sSuffix;
  }
}
