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

package net.openhft.koloboke.collect.set;

import net.openhft.koloboke.collect.ObjCollection;
import net.openhft.koloboke.collect.ObjIterator;

import javax.annotation.Nonnull;
import java.util.Set;


/**
 * 
 * A set of objects, the library's extension of the classic {@link Set} interface.
 * 
 *
 * <p>Methods, declared in this interface (i. e. not inherited from the superinterfaces),
 * are present only to remove some compile-time ambiguities, they don't have any additional meaning
 * over the specifications from superinterfaces.
 *
 * @see ObjSetFactory
 */
public interface ObjSet<E> extends ObjCollection<E>, Set<E> {


    /**
     * {@inheritDoc}
     * @deprecated Instead of explicit {@code iterator()} calls, use {@link #cursor()};
     * {@code iterator()} is still sensible only as a backing mechanism for Java 5's for-each
     * statements.
     */
    @Deprecated
    @Nonnull
    @Override
    ObjIterator<E> iterator();
}

