package com.helger.pdflayout.base;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.function.IThrowingConsumer;

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

  /**
   * Special visitor method that visits only elements of this objects and
   * ignores the others objects.
   *
   * @param aElementConsumer
   *        The consumer to use. May not be <code>null</code>.
   * @throws IOException
   *         on PDFBox error
   * @see #visit(IPLVisitor)
   */
  default void visitElement (@Nonnull final IThrowingConsumer <? super IPLRenderableObject <?>, IOException> aElementConsumer) throws IOException
  {
    ValueEnforcer.notNull (aElementConsumer, "ElementConsumer");
    visit (new IPLVisitor ()
    {
      public void onElement (@Nonnull final IPLRenderableObject <?> aElement) throws IOException
      {
        aElementConsumer.accept (aElement);
      }
    });
  }
}
