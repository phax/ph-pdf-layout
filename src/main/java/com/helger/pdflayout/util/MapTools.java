/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.pdflayout.util;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;

/**
 * Primitive map common methods
 * 
 * @author Mikhail Vorontsov
 * @author Philip Helger
 */
@Immutable
public final class MapTools
{
  private MapTools ()
  {}

  /**
   * Return the least power of two greater than or equal to the specified value.
   * <p>
   * Note that this function will return 1 when the argument is 0.
   *
   * @param nValue
   *        a long integer smaller than or equal to 2<sup>62</sup>.
   * @return the least power of two greater than or equal to the specified
   *         value.
   */
  public static long nextPowerOfTwo (final long nValue)
  {
    if (nValue == 0)
      return 1;
    long x = nValue - 1;
    x |= x >> 1;
    x |= x >> 2;
    x |= x >> 4;
    x |= x >> 8;
    x |= x >> 16;
    return (x | x >> 32) + 1;
  }

  /**
   * Returns the least power of two smaller than or equal to 2<sup>30</sup> and
   * larger than or equal to <code>Math.ceil( expected / f )</code>.
   *
   * @param expected
   *        the expected number of elements in a hash table.
   * @param f
   *        the load factor.
   * @return the minimum possible size for a backing array.
   * @throws IllegalArgumentException
   *         if the necessary size is larger than 2<sup>30</sup>.
   */
  @Nonnegative
  public static int arraySize (final int expected, final float f)
  {
    final long s = Math.max (2, nextPowerOfTwo ((long) Math.ceil (expected / f)));
    if (s > (1 << 30))
      throw new IllegalArgumentException ("Too large (" + expected + " expected elements with load factor " + f + ")");
    return (int) s;
  }

  // taken from FastUtil
  private static final int INT_PHI = 0x9E3779B9;

  public static int phiMix (final int x)
  {
    final int h = x * INT_PHI;
    return h ^ (h >> 16);
  }
}
