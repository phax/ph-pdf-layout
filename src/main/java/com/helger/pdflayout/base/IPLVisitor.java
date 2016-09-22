package com.helger.pdflayout.base;

import javax.annotation.Nonnull;

/**
 * Visitor callback
 *
 * @author Philip Helger
 */
public interface IPLVisitor
{
  /**
   * Call on page set start
   *
   * @param aPageSet
   *        The current page set. Never <code>null</code>.
   */
  default void onPageSetStart (@Nonnull final PLPageSet aPageSet)
  {}

  /**
   * Call for each element in the current page set. This method is also called
   * for page set header and footer elements.
   *
   * @param aElement
   *        The current element. Never <code>null</code>.
   */
  default void onElement (@Nonnull final IPLRenderableObject <?> aElement)
  {}

  /**
   * Call on page set end
   *
   * @param aPageSet
   *        The current page set.
   */
  default void onPageSetEnd (@Nonnull final PLPageSet aPageSet)
  {}
}
