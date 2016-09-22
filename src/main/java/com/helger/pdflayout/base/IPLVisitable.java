package com.helger.pdflayout.base;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Base interface for visitable objects.
 *
 * @author Philip Helger
 */
public interface IPLVisitable
{
  /**
   * Visit this object and all descendants
   *
   * @param aVisitor
   *        The visitor to use. May not be <code>null</code>.
   * @throws IOException
   *         on PDFBox error
   */
  void visit (@Nonnull IPLVisitor aVisitor) throws IOException;
}
