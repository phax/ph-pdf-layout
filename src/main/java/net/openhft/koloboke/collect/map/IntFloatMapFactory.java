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

package net.openhft.koloboke.collect.map;

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.function.Consumer;

import javax.annotation.Nonnull;

import java.util.Map;



/**
 * An immutable factory of {@code IntFloatMap}s.
 *
 * 
 * 
 * @param <F> the concrete factory type which extends this interface
 * @see IntFloatMap
 */
public interface IntFloatMapFactory<F extends IntFloatMapFactory<F>>
        extends ContainerFactory<F> {

    

    

    

    

    

    
    

    
    




    /**
     * Returns the value to which {@linkplain IntFloatMap#defaultValue() default value} of the maps
     * constructed by this factory is set. Default value is {@code
     * 0.0f}.
     *
     * @return the default value of the maps constructed by this factory
     */
    float getDefaultValue();

    /**
     * Returns a copy of this factory, with exception that it constructs maps with
     * {@linkplain IntFloatMap#defaultValue() default value} set to the given {@code float} value.
     *
     * @param defaultValue the new default {@code float} value
     * @return a copy of this factory, which constructs maps with the given {@code defaultValue}
     */
    @Nonnull
    F withDefaultValue(float defaultValue);

    

    /**
     * Constructs a new empty mutable map of the {@linkplain #getDefaultExpectedSize()
     * default expected size}.
     *
    
     
     * @return a new empty mutable map
     */
    @Nonnull
     IntFloatMap newMutableMap();

    /**
     * Constructs a new empty mutable map of the given expected size.
     *
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new empty mutable map of the given expected size
     */
    @Nonnull
     IntFloatMap newMutableMap(int expectedSize);

    

    


    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);
    

    

    /**
     * Constructs a new mutable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new mutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * 
    
     
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newMutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * 
    
     
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newMutableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * 
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newMutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * 
    
     
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newMutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);

    /**
     * Constructs a new mutable map of the single specified mapping.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new mutable map of the single specified mapping
     */
    @Nonnull
     IntFloatMap newMutableMapOf(int k1, float v1);

    /**
     * Constructs a new mutable map of the two specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new mutable map of the two specified mappings
     */
    @Nonnull
     IntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2);

    /**
     * Constructs a new mutable map of the three specified mappings.
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
     IntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    /**
     * Constructs a new mutable map of the four specified mappings.
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
     IntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    /**
     * Constructs a new mutable map of the five specified mappings.
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
     IntFloatMap newMutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);
    /**
     * Constructs a new empty updatable map of the {@linkplain #getDefaultExpectedSize()
     * default expected size}.
     *
    
     
     * @return a new empty updatable map
     */
    @Nonnull
     IntFloatMap newUpdatableMap();

    /**
     * Constructs a new empty updatable map of the given expected size.
     *
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new empty updatable map of the given expected size
     */
    @Nonnull
     IntFloatMap newUpdatableMap(int expectedSize);

    

    


    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    /**
     * Constructs a new updatable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);
    

    

    /**
     * Constructs a new updatable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new updatable map with the same mappings as the specified {@code map}
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * 
    
     
     * @return a new updatable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    /**
     * Constructs a new updatable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newUpdatableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    /**
     * Constructs a new updatable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * 
    
     
     * @return a new updatable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newUpdatableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * 
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newUpdatableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    /**
     * Constructs a new updatable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * 
    
     
     * @return a new updatable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newUpdatableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);

    /**
     * Constructs a new updatable map of the single specified mapping.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new updatable map of the single specified mapping
     */
    @Nonnull
     IntFloatMap newUpdatableMapOf(int k1, float v1);

    /**
     * Constructs a new updatable map of the two specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new updatable map of the two specified mappings
     */
    @Nonnull
     IntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2);

    /**
     * Constructs a new updatable map of the three specified mappings.
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
     IntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    /**
     * Constructs a new updatable map of the four specified mappings.
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
     IntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    /**
     * Constructs a new updatable map of the five specified mappings.
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
     IntFloatMap newUpdatableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);

    

    


    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2, int expectedSize);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3, int expectedSize);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4, int expectedSize);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5, int expectedSize);



    /**
     * Constructs a new immutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            , int expectedSize);

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values, int expectedSize);

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values, int expectedSize);

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * @param expectedSize the expected size of the returned map
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values, int expectedSize);
    

    

    /**
     * Constructs a new immutable map with the same mappings as the specified {@code map}.
     *
     * 
     *
     * @param map the map whose mappings are to be placed in the returned map
     * 
    
     
     * @return a new immutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * 
    
     
     * @return a new immutable map which merge the mappings of the specified maps
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4);

    /**
     * Constructs a new immutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
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
     IntFloatMap newImmutableMap(@Nonnull Map<Integer, Float> map1,
            @Nonnull Map<Integer, Float> map2,
            @Nonnull Map<Integer, Float> map3,
            @Nonnull Map<Integer, Float> map4,
            @Nonnull Map<Integer, Float> map5);



    /**
     * Constructs a new immutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     * 
    
     
     * @return a new immutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function.IntFloatConsumer> entriesSupplier
            );

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
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
     IntFloatMap newImmutableMap(
            @Nonnull int[] keys, @Nonnull float[] values);

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     * 
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if {@code keys}
     *         or {@code
     *         values} contain {@code null} elements
     */
    @Nonnull
     IntFloatMap newImmutableMap(
            @Nonnull Integer[] keys, @Nonnull Float[] values);

    /**
     * Constructs a new immutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     * 
    
     
     * @return a new immutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
     IntFloatMap newImmutableMap(@Nonnull Iterable<Integer> keys,
            @Nonnull Iterable<Float> values);

    /**
     * Constructs a new immutable map of the single specified mapping.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    
     
     * @return a new immutable map of the single specified mapping
     */
    @Nonnull
     IntFloatMap newImmutableMapOf(int k1, float v1);

    /**
     * Constructs a new immutable map of the two specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    
     
     * @return a new immutable map of the two specified mappings
     */
    @Nonnull
     IntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2);

    /**
     * Constructs a new immutable map of the three specified mappings.
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
     IntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3);

    /**
     * Constructs a new immutable map of the four specified mappings.
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
     IntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4);

    /**
     * Constructs a new immutable map of the five specified mappings.
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
     IntFloatMap newImmutableMapOf(int k1, float v1,
            int k2, float v2, int k3, float v3,
            int k4, float v4, int k5, float v5);
}

