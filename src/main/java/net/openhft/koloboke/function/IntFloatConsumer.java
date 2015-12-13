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
 * Represents an operation that accepts
 * 
 * an {@code int}-valued and
 * a {@code float}-valued argument,
 * 
 * and returns no result.  This is
 * the {@code (int,
 * float)} specialization of {@link BiConsumer}.
 * Unlike most other functional interfaces, {@code IntFloatConsumer} is
 * expected to operate via side-effects.
 *
 * 
 * 
 * @see BiConsumer
 * 
 */


public interface IntFloatConsumer {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     */
    void accept(int a, float b);
}

