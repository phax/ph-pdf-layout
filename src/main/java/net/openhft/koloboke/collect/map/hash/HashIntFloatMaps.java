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

import net.openhft.koloboke.collect.map.IntFloatMap;
import net.openhft.koloboke.function.Consumer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * This class consists only of static factory methods to construct {@code HashIntFloatMap}s, and
 * the default {@link HashIntFloatMapFactory} static provider ({@link #getDefaultFactory()}).
 *
 * @see HashIntFloatMap
 */
public final class HashIntFloatMaps {
    
    private static class DefaultFactoryHolder {
        private static final HashIntFloatMapFactory defaultFactory =
                ServiceLoader.load(HashIntFloatMapFactory.class).iterator().next();
    }

    /**
     * Returns the default {@link HashIntFloatMapFactory} implementation, to which
     * all static methods in this class delegate.
     *
     
     
     * @return the default {@link HashIntFloatMapFactory} implementation
     * @throws RuntimeException if no implementations
     *         of {@link HashIntFloatMapFactory} are provided
     */
    @Nonnull
    public static  HashIntFloatMapFactory getDefaultFactory() {
        return (HashIntFloatMapFactory) DefaultFactoryHolder.defaultFactory;
    }

    

    

    

    


    /**
     * Constructs a new empty mutable map of the default expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap() newMutableMap()}.
     *
     
     
     * @return a new empty mutable map
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap() {
        return getDefaultFactory().newMutableMap();
    }

    /**
     * Constructs a new empty mutable map of the given expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(int) newMutableMap(expectedSize)}.
     *
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new empty mutable map of the given expected size
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(int expectedSize) {
        return getDefaultFactory().newMutableMap(expectedSize);
    }

    
    
    
    


    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map,
     * Map, int) newMutableMap(map1, map2, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize) {
        return getDefaultFactory().newMutableMap(map1, map2, expectedSize);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map, int) newMutableMap(map1, map2, map3, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize) {
        return getDefaultFactory().newMutableMap(map1, map2, map3, expectedSize);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map, Map, int) newMutableMap(map1, map2, map3, map4, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize) {
        return getDefaultFactory().newMutableMap(map1, map2, map3, map4, expectedSize);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map, Map, Map, int) newMutableMap(map1, map2, map3, map4, map5, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize) {
        return getDefaultFactory().newMutableMap(map1, map2, map3, map4, map5, expectedSize);
    }

    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(
     * Consumer, int) newMutableMap(entriesSupplier, expectedSize)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize) {
        return getDefaultFactory().newMutableMap(entriesSupplier, expectedSize);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(int[], float[], int
     * ) newMutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize) {
        return getDefaultFactory().newMutableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Integer[],
     * Float[], int) newMutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize) {
        return getDefaultFactory().newMutableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Iterable, Iterable, int
     * ) newMutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            , int expectedSize) {
        return getDefaultFactory().newMutableMap(keys, values, expectedSize);
    }
    
    
    
    

    /**
     * Constructs a new mutable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(
     * Map) newMutableMap(map)}.
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new mutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map) {
        return getDefaultFactory().newMutableMap(map);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map,
     * Map) newMutableMap(map1, map2)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2) {
        return getDefaultFactory().newMutableMap(map1, map2);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map) newMutableMap(map1, map2, map3)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3) {
        return getDefaultFactory().newMutableMap(map1, map2, map3);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map, Map) newMutableMap(map1, map2, map3, map4)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4) {
        return getDefaultFactory().newMutableMap(map1, map2, map3, map4);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Map, Map,
     * Map, Map, Map) newMutableMap(map1, map2, map3, map4, map5)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5) {
        return getDefaultFactory().newMutableMap(map1, map2, map3, map4, map5);
    }

    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(
     * Consumer) newMutableMap(entriesSupplier)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * 
    
     
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            ) {
        return getDefaultFactory().newMutableMap(entriesSupplier);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(int[], float[]
     * ) newMutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values) {
        return getDefaultFactory().newMutableMap(keys, values);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Integer[],
     * Float[]) newMutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values) {
        return getDefaultFactory().newMutableMap(keys, values);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMap(Iterable, Iterable
     * ) newMutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * 
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            ) {
        return getDefaultFactory().newMutableMap(keys, values);
    }

    /**
     * Constructs a new mutable map of the single specified mapping.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMapOf(int, float
     * ) newMutableMapOf(k1, v1)}.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new mutable map of the single specified mapping
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMapOf(
            int k1, float v1) {
        return getDefaultFactory().newMutableMapOf(k1, v1);
    }

    /**
     * Constructs a new mutable map of the two specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMapOf(int, float,
     * int, float) newMutableMapOf(k1, v1, k2, v2)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new mutable map of the two specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMapOf(
            int k1, float v1, int k2, float v2) {
        return getDefaultFactory().newMutableMapOf(k1, v1, k2, v2);
    }

    /**
     * Constructs a new mutable map of the three specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMapOf(int, float,
     * int, float, int, float) newMutableMapOf(k1, v1, k2, v2,
     * k3, v3)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
    
     
     * @return a new mutable map of the three specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3) {
        return getDefaultFactory().newMutableMapOf(k1, v1, k2, v2, k3, v3);
    }

    /**
     * Constructs a new mutable map of the four specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMapOf(int, float,
     * int, float, int, float, int, float
     * ) newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
    
     
     * @return a new mutable map of the four specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4) {
        return getDefaultFactory().newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /**
     * Constructs a new mutable map of the five specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newMutableMapOf(int, float,
     * int, float, int, float, int, float,
     * int, float) newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
     * @param k5 the key of the fifth mapping
     * @param v5 the value of the fifth mapping
    
     
     * @return a new mutable map of the five specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newMutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4,
            int k5, float v5) {
        return getDefaultFactory().newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    /**
     * Constructs a new empty updatable map of the default expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap() newUpdatableMap()}.
     *
     
     
     * @return a new empty updatable map
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap() {
        return getDefaultFactory().newUpdatableMap();
    }

    /**
     * Constructs a new empty updatable map of the given expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(int) newUpdatableMap(expectedSize)}.
     *
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new empty updatable map of the given expected size
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(int expectedSize) {
        return getDefaultFactory().newUpdatableMap(expectedSize);
    }

    
    
    
    


    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map,
     * Map, int) newUpdatableMap(map1, map2, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(map1, map2, expectedSize);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map, int) newUpdatableMap(map1, map2, map3, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3, expectedSize);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map, Map, int) newUpdatableMap(map1, map2, map3, map4, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3, map4, expectedSize);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map, Map, Map, int) newUpdatableMap(map1, map2, map3, map4, map5, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3, map4, map5, expectedSize);
    }

    /**
     * Constructs a new updatable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(
     * Consumer, int) newUpdatableMap(entriesSupplier, expectedSize)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize) {
        return getDefaultFactory().newUpdatableMap(entriesSupplier, expectedSize);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(int[], float[], int
     * ) newUpdatableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Integer[],
     * Float[], int) newUpdatableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize) {
        return getDefaultFactory().newUpdatableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Iterable, Iterable, int
     * ) newUpdatableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            , int expectedSize) {
        return getDefaultFactory().newUpdatableMap(keys, values, expectedSize);
    }
    
    
    
    

    /**
     * Constructs a new updatable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(
     * Map) newUpdatableMap(map)}.
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new updatable map with the same mappings as the specified {@code map}
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map) {
        return getDefaultFactory().newUpdatableMap(map);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map,
     * Map) newUpdatableMap(map1, map2)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2) {
        return getDefaultFactory().newUpdatableMap(map1, map2);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map) newUpdatableMap(map1, map2, map3)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map, Map) newUpdatableMap(map1, map2, map3, map4)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3, map4);
    }

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Map, Map,
     * Map, Map, Map) newUpdatableMap(map1, map2, map3, map4, map5)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5) {
        return getDefaultFactory().newUpdatableMap(map1, map2, map3, map4, map5);
    }

    /**
     * Constructs a new updatable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(
     * Consumer) newUpdatableMap(entriesSupplier)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * 
    
     
     * @return a new updatable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            ) {
        return getDefaultFactory().newUpdatableMap(entriesSupplier);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(int[], float[]
     * ) newUpdatableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values) {
        return getDefaultFactory().newUpdatableMap(keys, values);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Integer[],
     * Float[]) newUpdatableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values) {
        return getDefaultFactory().newUpdatableMap(keys, values);
    }

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMap(Iterable, Iterable
     * ) newUpdatableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * 
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            ) {
        return getDefaultFactory().newUpdatableMap(keys, values);
    }

    /**
     * Constructs a new updatable map of the single specified mapping.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMapOf(int, float
     * ) newUpdatableMapOf(k1, v1)}.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new updatable map of the single specified mapping
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMapOf(
            int k1, float v1) {
        return getDefaultFactory().newUpdatableMapOf(k1, v1);
    }

    /**
     * Constructs a new updatable map of the two specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMapOf(int, float,
     * int, float) newUpdatableMapOf(k1, v1, k2, v2)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new updatable map of the two specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMapOf(
            int k1, float v1, int k2, float v2) {
        return getDefaultFactory().newUpdatableMapOf(k1, v1, k2, v2);
    }

    /**
     * Constructs a new updatable map of the three specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMapOf(int, float,
     * int, float, int, float) newUpdatableMapOf(k1, v1, k2, v2,
     * k3, v3)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
    
     
     * @return a new updatable map of the three specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3) {
        return getDefaultFactory().newUpdatableMapOf(k1, v1, k2, v2, k3, v3);
    }

    /**
     * Constructs a new updatable map of the four specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMapOf(int, float,
     * int, float, int, float, int, float
     * ) newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
    
     
     * @return a new updatable map of the four specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4) {
        return getDefaultFactory().newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /**
     * Constructs a new updatable map of the five specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newUpdatableMapOf(int, float,
     * int, float, int, float, int, float,
     * int, float) newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
     * @param k5 the key of the fifth mapping
     * @param v5 the value of the fifth mapping
    
     
     * @return a new updatable map of the five specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newUpdatableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4,
            int k5, float v5) {
        return getDefaultFactory().newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }


    
    
    
    


    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map,
     * Map, int) newImmutableMap(map1, map2, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize) {
        return getDefaultFactory().newImmutableMap(map1, map2, expectedSize);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map, int) newImmutableMap(map1, map2, map3, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3, expectedSize);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map, Map, int) newImmutableMap(map1, map2, map3, map4, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3, map4, expectedSize);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map, Map, Map, int) newImmutableMap(map1, map2, map3, map4, map5, expectedSize)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3, map4, map5, expectedSize);
    }

    /**
     * Constructs a new immutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(
     * Consumer, int) newImmutableMap(entriesSupplier, expectedSize)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize) {
        return getDefaultFactory().newImmutableMap(entriesSupplier, expectedSize);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(int[], float[], int
     * ) newImmutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize) {
        return getDefaultFactory().newImmutableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Integer[],
     * Float[], int) newImmutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize) {
        return getDefaultFactory().newImmutableMap(keys, values, expectedSize);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Iterable, Iterable, int
     * ) newImmutableMap(keys, values, expectedSize)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            , int expectedSize) {
        return getDefaultFactory().newImmutableMap(keys, values, expectedSize);
    }
    
    
    
    

    /**
     * Constructs a new immutable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(
     * Map) newImmutableMap(map)}.
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new immutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map) {
        return getDefaultFactory().newImmutableMap(map);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map,
     * Map) newImmutableMap(map1, map2)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2) {
        return getDefaultFactory().newImmutableMap(map1, map2);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map) newImmutableMap(map1, map2, map3)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map, Map) newImmutableMap(map1, map2, map3, map4)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
    * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3, map4);
    }

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Map, Map,
     * Map, Map, Map) newImmutableMap(map1, map2, map3, map4, map5)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
    * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5) {
        return getDefaultFactory().newImmutableMap(map1, map2, map3, map4, map5);
    }

    /**
     * Constructs a new immutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(
     * Consumer) newImmutableMap(entriesSupplier)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    * 
    
     
     * @return a new immutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            ) {
        return getDefaultFactory().newImmutableMap(entriesSupplier);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(int[], float[]
     * ) newImmutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values) {
        return getDefaultFactory().newImmutableMap(keys, values);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Integer[],
     * Float[]) newImmutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
    * 
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values) {
        return getDefaultFactory().newImmutableMap(keys, values);
    }

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMap(Iterable, Iterable
     * ) newImmutableMap(keys, values)}.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
    * 
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMap(
            @Nonnull Iterable<Integer> keys, @Nonnull Iterable<Float> values
            ) {
        return getDefaultFactory().newImmutableMap(keys, values);
    }

    /**
     * Constructs a new immutable map of the single specified mapping.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMapOf(int, float
     * ) newImmutableMapOf(k1, v1)}.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new immutable map of the single specified mapping
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMapOf(
            int k1, float v1) {
        return getDefaultFactory().newImmutableMapOf(k1, v1);
    }

    /**
     * Constructs a new immutable map of the two specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMapOf(int, float,
     * int, float) newImmutableMapOf(k1, v1, k2, v2)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new immutable map of the two specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMapOf(
            int k1, float v1, int k2, float v2) {
        return getDefaultFactory().newImmutableMapOf(k1, v1, k2, v2);
    }

    /**
     * Constructs a new immutable map of the three specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMapOf(int, float,
     * int, float, int, float) newImmutableMapOf(k1, v1, k2, v2,
     * k3, v3)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
    
     
     * @return a new immutable map of the three specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3) {
        return getDefaultFactory().newImmutableMapOf(k1, v1, k2, v2, k3, v3);
    }

    /**
     * Constructs a new immutable map of the four specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMapOf(int, float,
     * int, float, int, float, int, float
     * ) newImmutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
    
     
     * @return a new immutable map of the four specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4) {
        return getDefaultFactory().newImmutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /**
     * Constructs a new immutable map of the five specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashIntFloatMapFactory#newImmutableMapOf(int, float,
     * int, float, int, float, int, float,
     * int, float) newImmutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
     * @param k5 the key of the fifth mapping
     * @param v5 the value of the fifth mapping
    
     
     * @return a new immutable map of the five specified mappings
     */
    @Nonnull
    public static  HashIntFloatMap newImmutableMapOf(
            int k1, float v1, int k2, float v2,
            int k3, float v3, int k4, float v4,
            int k5, float v5) {
        return getDefaultFactory().newImmutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    private HashIntFloatMaps() {}
}

