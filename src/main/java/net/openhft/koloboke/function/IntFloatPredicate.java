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
 * Represents a predicate (boolean-valued function) that accepts
 * 
 * an {@code int}-valued and
 * a {@code float}-valued argument.
 * 
 * This is the {@code (int,
 * float)} specialization
 * of {@link BiPredicate}.
 *
 * 
 * 
 * @see BiPredicate
 */

public interface IntFloatPredicate {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @return {@code true} if the input arguments match the predicate,
     *         otherwise {@code false}
     */
    boolean test(int a, float b);
}

