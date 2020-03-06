package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout4.spec.FontSpec;

/**
 * Abstract bullet point creator to create e.g. constant dots or numbers or
 * characters.
 *
 * @author Philip Helger
 */
public interface IBulletPointCreator
{
  /**
   * @return The font to be used to render the bullet point text. May not be
   *         <code>null</code>.
   */
  @Nonnull
  FontSpec getFontSpec ();

  /**
   * Get the bullet point text for the specified index.
   *
   * @param nBulletPointIndex
   *        The 0-based index of the bullet point to be created.
   * @return A non-<code>null</code> but maybe empty.
   */
  @Nonnull
  String getBulletPointText (@Nonnegative int nBulletPointIndex);
}
