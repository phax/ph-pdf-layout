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

package net.openhft.koloboke.function;


/**
 * Represents a function that accepts
 * 
 * two {@code float}-valued arguments
 * 
 * and produces a {@code float}-valued result.
 * This is the {@code (float, float, float)}
 * specialization of {@link BiFunction}.
 *
 * 
 * <p>Unlike {@link FloatBinaryOperator}, this function is supposed to accept heterogeneous
 * arguments, e. g. key and value
 * in {@link net.openhft.koloboke.collect.map.FloatFloatMap#compute(float, FloatFloatToFloatFunction)}
 * method.
 * 
 *
 * 
 * @see BiFunction
 */

public interface FloatFloatToFloatFunction {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @return the function result
     */
    float applyAsFloat(float a, float b);
}

