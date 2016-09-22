package com.helger.pdflayout.base;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Base interface for a PDF layout object
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLObject <IMPLTYPE extends IPLObject <IMPLTYPE>>
                           extends IHasID <String>, IGenericImplTrait <IMPLTYPE>, IPLVisitable
{
  /**
   * @return The debug ID of this element. Neither <code>null</code> nor empty.
   *         The debug ID is usually automatically created automatically from
   *         the ID.
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
    return this instanceof IPLSplittableObject <?>;
  }

  @Nonnull
  default IPLSplittableObject <?> getAsSplittable ()
  {
    return (IPLSplittableObject <?>) this;
  }
}
