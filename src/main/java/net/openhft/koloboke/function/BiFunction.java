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
 * Represents a function that accepts two arguments and produces a result. This
 * is the two-arity specialization of {@link Function}.
 *
 * @param <T>
 *        the type of the first argument to the function
 * @param <U>
 *        the type of the second argument to the function
 * @param <R>
 *        the type of the result of the function
 * @see Function
 */
public interface BiFunction <T, U, R>
{
  /**
   * Applies this function to the given arguments.
   *
   * @param t
   *        the first function argument
   * @param u
   *        the second function argument
   * @return the function result
   */
  R apply (T t, U u);
}
