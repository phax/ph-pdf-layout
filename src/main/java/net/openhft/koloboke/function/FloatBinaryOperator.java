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
 * 
 * Represents an operation upon two {@code float}-valued operands and producing a
 * {@code float}-valued result.   This is the primitive type specialization of
 * {@link BinaryOperator} for {@code float}.
 *
 * <p>Unlike {@link FloatFloatToFloatFunction}, this operator is supposed to accept two homogeneous
 * arguments and produce a result homogeneous to the arguments, e. g. value merging
 * in {@link net.openhft.koloboke.collect.map.ObjFloatMap#merge(Object, float, FloatBinaryOperator)}
 * method.
 *
 * @see BinaryOperator
 * @see FloatUnaryOperator
 * 
 */


public interface FloatBinaryOperator extends FloatFloatToFloatFunction
         {

    /**
     * Applies this operator to the given operands.
     *
     * @param left the first operand
     * @param right the second operand
     * @return the operator result
     */
    @Override
    float applyAsFloat(float left, float right);
}

