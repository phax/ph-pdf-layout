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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.random.VerySecureRandom;

public final class IntFloatMapTest
{
  private static final float DELTA = 0.0001f;
  private final static float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  protected IntFloatMap makeMap (final int size, final float fillFactor)
  {
    return new IntFloatMap (size, fillFactor);
  }

  @Test
  public void testPut ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutHelper (ff);
  }

  private void _testPutHelper (final float fillFactor)
  {
    final IntFloatMap map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals ("Inserting " + i, IntFloatMap.NO_VALUE, map.put (i, i), DELTA);
      assertEquals (i + 1, map.size ());
      assertEquals (i, map.get (i), DELTA);
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (i, map.get (i), DELTA);
  }

  @Test
  public void testPutNegative ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutNegative (ff);
  }

  private void _testPutNegative (final float fillFactor)
  {
    final IntFloatMap map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      map.put (-i, -i);
      assertEquals (i + 1, map.size ());
      assertEquals (-i, map.get (-i), DELTA);
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (-i, map.get (-i), DELTA);
  }

  @Test
  public void testPutRandom ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutRandom (ff);
  }

  private void _testPutRandom (final float fillFactor)
  {
    final Random aRandom = VerySecureRandom.getInstance ();
    final int SIZE = 100 * 1000;
    final Set <Integer> set = new HashSet <Integer> (SIZE);
    final int [] vals = new int [SIZE];
    while (set.size () < SIZE)
      set.add (Integer.valueOf (aRandom.nextInt ()));
    int i = 0;
    for (final Integer v : set)
      vals[i++] = v.intValue ();

    final IntFloatMap map = makeMap (100, fillFactor);
    for (i = 0; i < vals.length; ++i)
    {
      assertEquals ("Inserting " + vals[i], IntFloatMap.NO_VALUE, map.put (vals[i], vals[i]), DELTA);
      assertEquals (i + 1, map.size ());
      assertEquals (vals[i], map.get (vals[i]), DELTA);
    }
    // now check the final state
    for (i = 0; i < vals.length; ++i)
      assertEquals (vals[i], map.get (vals[i]), DELTA);
  }

  @Test
  public void testRemove ()
  {
    for (final float ff : FILL_FACTORS)
      _testRemoveHelper (ff);
  }

  private void _testRemoveHelper (final float fillFactor)
  {
    final IntFloatMap map = makeMap (100, fillFactor);
    int addCnt = 0, removeCnt = 0;
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals (IntFloatMap.NO_VALUE, map.put (addCnt, addCnt), DELTA);
      addCnt++;
      assertEquals ("Failed for addCnt = " +
                    addCnt +
                    ", ff = " +
                    fillFactor,
                    IntFloatMap.NO_VALUE,
                    map.put (addCnt, addCnt),
                    DELTA);
      addCnt++;
      assertEquals (removeCnt, map.remove (removeCnt), DELTA);
      removeCnt++;

      // map grows by one element on each iteration
      assertEquals (i + 1, map.size ());
    }
    for (int i = removeCnt; i < addCnt; ++i)
      assertEquals (i, map.get (i), DELTA);
  }
}
