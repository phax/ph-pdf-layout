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

import net.openhft.koloboke.collect.Container;
import net.openhft.koloboke.collect.Equivalence;
import net.openhft.koloboke.function.BiConsumer;
import net.openhft.koloboke.function.BiFunction;
import net.openhft.koloboke.function.IntFloatConsumer;
import net.openhft.koloboke.function.IntFloatPredicate;
import net.openhft.koloboke.function.IntFloatToFloatFunction;
import net.openhft.koloboke.function.IntToFloatFunction;
import net.openhft.koloboke.function.Function;
import net.openhft.koloboke.function.FloatBinaryOperator;
import net.openhft.koloboke.collect.FloatCollection;
import net.openhft.koloboke.collect.set.IntSet;
import net.openhft.koloboke.collect.set.ObjSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;


/**
 * 
 * A {@link Map} specialization with {@code int} keys and {@code float} values.
 * 
 *
 * @see IntFloatMapFactory
 */
public interface IntFloatMap extends Map<Integer, Float>, Container {



    /**
     * Returns the default value of this map, which is used instead of {@code null}
     * in primitive specialization methods, when the key is absent in the map.
     *
     * @return the default value of this map
     * @see IntFloatMapFactory#withDefaultValue(float)
     */
    float defaultValue();


    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #containsKey(int)} instead
     */
    @Override
    @Deprecated
    boolean containsKey(Object key);

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key the {@code int} key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    boolean containsKey(int key);

    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #containsValue(float)} instead
     */
    @Override
    @Deprecated
    boolean containsValue(Object value);

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value. This operation
     * will probably require time linear in the map size for most implementations
     * of the {@code IntFloatMap} interface.
     *
     * @param value the {@code float} value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    boolean containsValue(float value);

    

    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #get(int)} instead
     */
    @Nullable
    @Override
    @Deprecated
    Float get(Object key);

    

    /**
     * Returns the value to which the specified key is mapped, or {@linkplain #defaultValue() default
    value} if this map
     * contains no mapping for the key.
     *
     * 
     *
     * 
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@linkplain #defaultValue() default
    value} if this map
     *         contains no mapping for the key
     */
    
    float get(int key);


    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key

     * @deprecated Use specialization {@link #getOrDefault(int, float)} instead
     */
    
    @Deprecated
    Float getOrDefault(Object key, Float defaultValue);

    /**
     * Returns the value to which the specified key is mapped, or {@code defaultValue} if this map
     * contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the value to return if the specified {@code key} is absent in the map
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     */
    float getOrDefault(int key, float defaultValue);



    /**
     * Performs the given {@code action} on each entry in this map until all entries
     * have been processed or the action throws an {@code Exception}.
     * Exceptions thrown by the action are relayed to the caller. The entries
     * will be processed in the same order as the entry set iterator unless that
     * order is unspecified in which case implementations may use an order which
     * differs from the entry set iterator.
     *
     * @param action The action to be performed for each entry
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    void forEach(@Nonnull IntFloatConsumer action);


    /**
     * Checks the given {@code predicate} on each entry in this map until all entries
     * have been processed or the predicate returns {@code false} for some entry,
     * or throws an {@code Exception}. Exceptions thrown by the predicate are relayed to the caller.
     *
     * <p>The entries will be processed in the same order as the entry set iterator unless that
     * order is unspecified in which case implementations may use an order which differs from
     * the entry set iterator.
     *
     * <p>If the map is empty, this method returns {@code true} immediately.
     *
     * @return {@code true} if the predicate returned {@code true} for all entries of the map,
     *         {@code false} if it returned {@code false} for the entry
     * @param predicate the predicate to be checked for each entry
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean forEachWhile(@Nonnull IntFloatPredicate predicate);

    /**
     * Returns a new cursor over the entries of this map. It's order is always correspond to the
     * entry set iterator order.
     *
     * @return a new cursor over the entries of this map
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Nonnull
    IntFloatCursor cursor();


    @Override
    @Nonnull
    IntSet keySet();

    @Override
    @Nonnull
    FloatCollection values();

    @Override
    @Nonnull
    ObjSet<Entry<Integer, Float>> entrySet();

    

    

    

    

    

    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #put(int, float)} instead
     */
    @Override
    @Deprecated
    Float put(Integer key, Float value);

    /**
     * Associates the specified value with the specified key in this map (optional operation).
     * If the map previously contained a mapping for the key, the old value is replaced
     * by the specified value. (A map {@code m} is said to contain a mapping for a key {@code k}
     * if and only if {@link #containsKey(int) m.containsKey(k)} would return {@code true}.)
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or {@linkplain #defaultValue() default
    value} if there was
     *         no mapping for {@code key}. (A {@linkplain #defaultValue() default
    value} return can also indicate that the map
     *         previously associated {@linkplain #defaultValue() default
    value} with {@code key}.)
     * @throws UnsupportedOperationException if the {@code put} operation
    *         is not supported by this map
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this map
     */
    float put(int key, float value);

    

    

    /**
     * If the specified key is not already associated with a value, associates
     * it with the given value and returns {@code null}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     * @throws ClassCastException if the key or value is of an inappropriate type for this map
     * @throws NullPointerException if the specified key or value is null
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     * @deprecated Use specialization {@link #putIfAbsent(int, float)} instead
     */
    
    @Nullable
    @Deprecated
    Float putIfAbsent(Integer key, Float value);

    /**
     * If the specified key is not already associated with a value, associates
     * it with the given value and returns {@linkplain #defaultValue() default
    value}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@linkplain #defaultValue() default
    value}
     *         if there was no mapping for the key. (A {@linkplain #defaultValue() default
    value} return
     *         can also indicate that the map previously associated {@linkplain #defaultValue() default
    value}
     *         with the key, if the implementation supports such values.)
     * @throws UnsupportedOperationException if the {@code put} operation
    *         is not supported by this map
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this map
     */
    
    float putIfAbsent(int key, float value);


    

    

    

    


    /**
     * Attempts to compute a mapping for the specified key and its current mapped value
     * (or {@linkplain #defaultValue() default
    value} if there is no current mapping).
     *
     * <p>If the function itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float compute(int key, @Nonnull IntFloatToFloatFunction remappingFunction);



    /**
     * If the specified key is not already associated with a value, attempts
     * to compute its value using the given mapping function and enters it into this map
     * . The most common usage is to construct
     * a new object serving as an initial mapped value or memoized result.
     *
     * <p>If the function itself throws an (unchecked) exception, the exception is rethrown,
     * and no mapping is recorded.
     *
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     *         the specified key
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float computeIfAbsent(int key, @Nonnull IntToFloatFunction mappingFunction);



    /**
     * If the value for the specified key is present,
     * attempts to compute a new mapping given the key and its current mapped value.
     *
     * <p>If the function itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key,
     *         or "no entry" value
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float computeIfPresent(int key, @Nonnull IntFloatToFloatFunction remappingFunction);



    /**
     * If the specified key is not already associated with a value, associates
     * it with the given value, otherwise, replaces the value with the results of the given
     * remapping function.
     *
     * This method may be of use when combining multiple mapped values for a key.
     *
     * <p>If the remappingFunction itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param value the value to use if absent
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key
     * @throws NullPointerException if the {@code remappingFunction} is null
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float merge(int key, float value, @Nonnull FloatBinaryOperator remappingFunction);


    /**
     * Adds the given {@code addition} value to the value associated with the specified key,
     * or {@linkplain #defaultValue() default
    value} if this map contains no mapping for the key, and associates the resulting
     * value with the key.
     *
     * @param key the key to which value add the given value
     * @param addition the value to add
     * @return the new value associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float addValue(int key, float addition);

    /**
     * Adds the given {@code addition} value to the value associated with the specified key,
     * or {@code defaultValue} if this map contains no mapping for the key, and associates
     * the resulting value with the key.
     *
     * <p>This version of {@link #addValue(int, float)} is useful if you want to count
     * values from the different initial value, than the {@linkplain #defaultValue() default value}
     * of this map.
     *
     * @param key the key to which value add the given value
     * @param addition the value to add
     * @param defaultValue the value to be used if the map contains no mapping for the given key
     * @return the new value associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    float addValue(int key, float addition, float defaultValue);

    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was
     *         no mapping for the key. 
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     * @throws NullPointerException if the specified key or value is null
     * @throws IllegalArgumentException if some property of the specified value
     *         prevents it from being stored in this map
     * @deprecated Use specialization {@link #replace(int, float)} instead
     */
    
    @Nullable
    @Deprecated
    Float replace(Integer key, Float value);

    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key,
     *         or {@linkplain #defaultValue() default
    value} if there was no mapping for the key.
     *         (A {@linkplain #defaultValue() default
    value} return can also indicate that the map
     *         previously associated {@linkplain #defaultValue() default
    value} with the key,
     *         if the implementation supports such values.)
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this map
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    
    float replace(int key, float value);


    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     * @throws NullPointerException if the specified key or value is null
     * @throws IllegalArgumentException if some property of the specified value
     *         prevents it from being stored in this map
     * @deprecated Use specialization
     *             {@link #replace(int, float, float)} instead
     */
    
    @Deprecated
    boolean replace(Integer key, Float oldValue, Float newValue);

    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this map
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     */
    boolean replace(int key, float oldValue, float newValue);



    /**
     * Replaces each entry's value with the result of invoking the given function on that entry,
     * in the order entries are returned by an entry set iterator, until all entries have been
     * processed or the function throws an exception.
     *
     * @param function the function to apply to each entry
     * @throws UnsupportedOperationException if the {@code set} operation
     *         is not supported by this map's entry set iterator
     * @throws IllegalArgumentException if some property of a replacement value
     *         prevents it from being stored in this map (optional restriction)
     */
    void replaceAll(@Nonnull IntFloatToFloatFunction function);


    

    /**
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #remove(int)} instead
     */
    @Override
    @Nullable
    @Deprecated
    Float remove(Object key);

    /**
     * Removes the mapping for a key from this map if it is present (optional operation).
     *
     *
     * <p>Returns the value to which this map previously associated the key, or {@linkplain #defaultValue() default
    value}
     * if the map contained no mapping for the key.
     *
     * <p>A return value of {@linkplain #defaultValue() default
    value} does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to {@linkplain #defaultValue() default
    value}.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or {@linkplain #defaultValue() default
    value} if there was
     *         no mapping for {@code key}
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this map
     */
    
    float remove(int key);


    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws NullPointerException if the specified key or value is null
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this map
     * @deprecated Use specialization {@link #remove(int, float)} instead
     */
    
    @Deprecated
    boolean remove(Object key, Object value);

    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this map
     */
    boolean remove(int key, float value);

    /**
     * Removes all of the entries of this collection that satisfy the given predicate.
     * Errors or runtime exceptions thrown during iteration or by the predicate are relayed
     * to the caller.
     *
     * @param filter a predicate which returns {@code true} for elements to be removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed from this collection.
     *         Implementations may throw this exception if a matching element cannot be removed
     *         or if, in general, removal is not supported.
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean removeIf(@Nonnull IntFloatPredicate filter);
}

