package com.helger.pdflayout4.element.list;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout4.base.IPLRenderableObject;

/**
 * Abstract bullet point creator to create e.g. constant dots or numbers or
 * characters.
 *
 * @author Philip Helger
 * @since 5.1.0
 */
public interface IBulletPointCreator
{
  /**
   * Get the bullet point text for the specified index.
   *
   * @param nBulletPointIndex
   *        The 0-based index of the bullet point to be created.
   * @return A non-<code>null</code> but maybe empty.
   */
  @Nonnull
  IPLRenderableObject <?> getBulletPointElement (@Nonnegative int nBulletPointIndex);
}
