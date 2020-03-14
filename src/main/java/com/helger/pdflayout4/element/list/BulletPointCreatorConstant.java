package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.pdflayout4.base.IPLRenderableObject;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * An implementation of {@link IBulletPointCreator} that always uses the same
 * character (like in an unordered list).
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public class BulletPointCreatorConstant implements IBulletPointCreator
{
  private final String m_sText;
  private final FontSpec m_aFontSpec;

  public BulletPointCreatorConstant (@Nonnull final String sText, @Nonnull final FontSpec aFontSpec)
  {
    ValueEnforcer.notNull (aFontSpec, "FontSpec");
    ValueEnforcer.notNull (sText, "Text");
    m_aFontSpec = aFontSpec;
    m_sText = sText;
  }

  @Nonnull
  public final String getText ()
  {
    return m_sText;
  }

  @Nonnull
  public final FontSpec getFontSpec ()
  {
    return m_aFontSpec;
  }

  @Nonnull
  public IPLRenderableObject <?> getBulletPointElement (@Nonnegative final int nBulletPointIndex)
  {
    return new PLText (m_sText, m_aFontSpec);
  }
}
