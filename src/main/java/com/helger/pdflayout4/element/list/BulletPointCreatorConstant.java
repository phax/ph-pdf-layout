package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that always uses the same
 * character (like in an unordered list).
 *
 * @author Philip Helger
 */
public class BulletPointCreatorConstant implements IBulletPointCreator
{
  private final FontSpec m_aFontSpec;
  private final String m_sText;

  public BulletPointCreatorConstant (@Nonnull final FontSpec aFontSpec, @Nonnull final String sText)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    ValueEnforcer.notNull (sText, "Text");
    m_aFontSpec = aFontSpec;
    m_sText = sText;
  }

  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @Nonnull
  public String getBulletPointText (@Nonnegative final int nBulletPointIndex)
  {
    return m_sText;
  }
}
