package com.helger.pdflayout.util;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.random.VerySecureRandom;

public final class IntIntMapTest
{
  private final static float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  protected IntIntMap makeMap (final int size, final float fillFactor)
  {
    return new IntIntMap (size, fillFactor);
  }

  @Test
  public void testPut ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutHelper (ff);
  }

  private void _testPutHelper (final float fillFactor)
  {
    final IntIntMap map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals (0, map.put (i, i));
      assertEquals (i + 1, map.size ());
      assertEquals (i, map.get (i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (i, map.get (i));
  }

  @Test
  public void testPutNegative ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutNegative (ff);
  }

  private void _testPutNegative (final float fillFactor)
  {
    final IntIntMap map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      map.put (-i, -i);
      assertEquals (i + 1, map.size ());
      assertEquals (-i, map.get (-i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (-i, map.get (-i));
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

    final IntIntMap map = makeMap (100, fillFactor);
    for (i = 0; i < vals.length; ++i)
    {
      assertEquals (0, map.put (vals[i], vals[i]));
      assertEquals (i + 1, map.size ());
      assertEquals (vals[i], map.get (vals[i]));
    }
    // now check the final state
    for (i = 0; i < vals.length; ++i)
      assertEquals (vals[i], map.get (vals[i]));
  }

  @Test
  public void testRemove ()
  {
    for (final float ff : FILL_FACTORS)
      _testRemoveHelper (ff);
  }

  private void _testRemoveHelper (final float fillFactor)
  {
    final IntIntMap map = makeMap (100, fillFactor);
    int addCnt = 0, removeCnt = 0;
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals (0, map.put (addCnt, addCnt));
      addCnt++;
      assertEquals ("Failed for addCnt = " +
                    addCnt +
                    ", ff = " +
                    fillFactor,
                    IntIntMap.NO_VALUE,
                    map.put (addCnt, addCnt));
      addCnt++;
      assertEquals (removeCnt, map.remove (removeCnt));
      removeCnt++;

      // map grows by one element on each iteration
      assertEquals (i + 1, map.size ());
    }
    for (int i = removeCnt; i < addCnt; ++i)
      assertEquals (i, map.get (i));
  }
}
