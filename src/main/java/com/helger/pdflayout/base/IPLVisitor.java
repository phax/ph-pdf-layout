package com.helger.pdflayout.base;

import java.io.IOException;

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
   * @throws IOException
   *         on PDFBox error
   */
  default void onPageSetStart (@Nonnull final PLPageSet aPageSet) throws IOException
  {}

  /**
   * Call for each element in the current page set. This method is also called
   * for page set header and footer elements.
   *
   * @param aElement
   *        The current element. Never <code>null</code>.
   * @throws IOException
   *         on PDFBox error
   */
  default void onElement (@Nonnull final IPLRenderableObject <?> aElement) throws IOException
  {}

  /**
   * Call on page set end
   *
   * @param aPageSet
   *        The current page set.
   * @throws IOException
   *         on PDFBox error
   */
  default void onPageSetEnd (@Nonnull final PLPageSet aPageSet) throws IOException
  {}
}
