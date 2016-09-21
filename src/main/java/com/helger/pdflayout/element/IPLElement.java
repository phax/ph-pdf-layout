package com.helger.pdflayout.element;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Base interface for a PDF layout element
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLElement <IMPLTYPE extends IPLElement <IMPLTYPE>>
                            extends IHasID <String>, IGenericImplTrait <IMPLTYPE>
{
  /**
   * @return The debug ID of this element. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getDebugID ();

  /**
   * @return <code>true</code> if this element is splittable, <code>false</code>
   *         otherwise.
   */
  default boolean isSplittable ()
  {
    return this instanceof IPLSplittableElement;
  }

  @Nonnull
  default IPLSplittableElement <?> getAsSplittable ()
  {
    return (IPLSplittableElement <?>) this;
  }
}
