/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.koloboke.collect.map.hash;

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.function.Consumer;
import net.openhft.koloboke.collect.map.*;

import javax.annotation.Nonnull;

import java.util.Map;



/**
 * An immutable factory of {@code HashIntFloatMap}s.
 *
 * @see HashIntFloatMap
 * @see HashIntFloatMaps#getDefaultFactory()
 */
public interface HashIntFloatMapFactory
        extends IntFloatMapFactory<HashIntFloatMapFactory>
        , IntHashFactory<HashIntFloatMapFactory>
        {


    

    

    

    

    

    
    

    
    


    @Override
    @Nonnull
     HashIntFloatMap newMutableMap();

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(int expectedSize);

    


    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);

    

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);


    @Override
    @Nonnull
     HashIntFloatMap newMutableMapOf(int k1, float v1);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    @Override
    @Nonnull
     HashIntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);
    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap();

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(int expectedSize);

    


    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);

    

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);


    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMapOf(int k1, float v1);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    @Override
    @Nonnull
     HashIntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);

    


    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);

    

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);


    @Override
    @Nonnull
     HashIntFloatMap newImmutableMapOf(int k1, float v1);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    @Override
    @Nonnull
     HashIntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);
}

