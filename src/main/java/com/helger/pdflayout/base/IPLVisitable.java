package com.helger.pdflayout.base;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

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
   */
  void visit (@Nonnull IPLVisitor aVisitor);

  /**
   * Special visitor method that visits only elements of this objects and
   * ignores the others objects.
   *
   * @param aElementConsumer
   *        The consumer to use. May not be <code>null</code>.
   * @see #visit(IPLVisitor)
   */
  default void visitElement (@Nonnull final Consumer <? super IPLRenderableObject <?>> aElementConsumer)
  {
    ValueEnforcer.notNull (aElementConsumer, "ElementConsumer");
    visit (new IPLVisitor ()
    {
      public void onElement (@Nonnull final IPLRenderableObject <?> aElement)
      {
        aElementConsumer.accept (aElement);
      }
    });
  }
}
