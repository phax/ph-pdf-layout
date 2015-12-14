package com.helger.pdflayout.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.random.VerySecureRandom;

public final class IntObjectMapTest
{
  private final static float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  protected IntObjectMap <String> makeMap (final int size, final float fillFactor)
  {
    return new IntObjectMap <String> (size, fillFactor);
  }

  @Nonnull
  @Nonempty
  private static String _make (final int i)
  {
    return "str" + i;
  }

  @Test
  public void testPut ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutHelper (ff);
  }

  private void _testPutHelper (final float fillFactor)
  {
    final IntObjectMap <String> map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      assertNull ("Inserting " + i, map.put (i, _make (i)));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (i), map.get (i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (_make (i), map.get (i));
  }

  @Test
  public void testPutNegative ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutNegative (ff);
  }

  private void _testPutNegative (final float fillFactor)
  {
    final IntObjectMap <String> map = makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      map.put (-i, _make (-i));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (-i), map.get (-i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (_make (-i), map.get (-i));
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

    final IntObjectMap <String> map = makeMap (100, fillFactor);
    for (i = 0; i < vals.length; ++i)
    {
      assertNull ("Inserting " + vals[i], map.put (vals[i], _make (vals[i])));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (vals[i]), map.get (vals[i]));
    }
    // now check the final state
    for (i = 0; i < vals.length; ++i)
      assertEquals (_make (vals[i]), map.get (vals[i]));
  }

  @Test
  public void testRemove ()
  {
    for (final float ff : FILL_FACTORS)
      _testRemoveHelper (ff);
  }

  private void _testRemoveHelper (final float fillFactor)
  {
    final IntObjectMap <String> map = makeMap (100, fillFactor);
    int addCnt = 0, removeCnt = 0;
    for (int i = 0; i < 100000; ++i)
    {
      assertNull (map.put (addCnt, _make (addCnt)));
      addCnt++;
      assertNull ("Failed for addCnt = " + addCnt + ", ff = " + fillFactor, map.put (addCnt, _make (addCnt)));
      addCnt++;
      assertEquals (_make (removeCnt), map.remove (removeCnt));
      removeCnt++;

      // map grows by one element on each iteration
      assertEquals (i + 1, map.size ());
    }
    for (int i = removeCnt; i < addCnt; ++i)
      assertEquals (_make (i), map.get (i));
  }
}
