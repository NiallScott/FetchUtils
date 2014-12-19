/*
 * Copyright (C) 2014 Niall Scott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.rivernile.android.fetchutils.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Loaders can only return a single object. The purpose of this class is to encapsulate a success
 * condition or a failure condition in to a single object that is returnable by a Loader.
 *
 * <p>
 *     This class uses generic types to define the types of the success and error objects. The error
 *     object has to be a subclass of {@link Exception} otherwise the two constructors of this class
 *     are invalid. This is because it is not possible to have more than one constructor that can
 *     accept similar generic types, otherwise the compiler is not able to determine which
 *     constructor should be called by a method. It's like having two constructors that both take an
 *     argument of a single {@link String} - how does the compiler know which one should be called?
 * </p>
 *
 * <p>
 *     Indeed, any object can be returned by a Loader. It does not have to be a {@code Result}. This
 *     class has been created to satisfy a common case scenario where a load of data produces either
 *     a success or error response, and it should be treated as a utility class. To cater for other
 *     scenarios, then another strategy should be considered.
 * </p>
 *
 * @author Niall Scott
 * @param <S> The type of the success object.
 * @param <E> The type of the error object. This must be a subclass of {@link Exception}.
 * @see uk.org.rivernile.android.fetchutils.loaders.SimpleAsyncTaskLoader
 * @see uk.org.rivernile.android.fetchutils.loaders.support.SimpleAsyncTaskLoader
 */
public class Result<S, E extends Exception> {

    private final S success;
    private final E error;

    /**
     * Create a {@code Result} that holds a success object.
     *
     * @param success The success object. This can be {@code null}.
     */
    public Result(@Nullable final S success) {
        this.success = success;
        error = null;
    }

    /**
     * Create a {@code Result} that holds an error.
     *
     * @param error The error {@link Exception}. This must not be {@code null}.
     */
    public Result(@NonNull final E error) {
        this.error = error;
        success = null;
    }

    /**
     * Get the success object.
     *
     * @return The success object, if {@link #isError()} returns {@code false}. Otherwise
     * {@code null} will be returned. It's possible for the success object to be {@code null} even
     * when {@link #isError()} returns {@code false}.
     */
    public S getSuccess() {
        return success;
    }

    /**
     * Get the error {@link Exception} object.
     *
     * @return The error {@link Exception} object. If {@link #isError()} returns {@code false}, then
     * {@code null} will be returned.
     */
    public E getError() {
        return error;
    }

    /**
     * Does this {@code Result} represent an error?
     *
     * @return {@code true} if this {@code Result} represents an error, {@code false} if it
     * represents a success.
     */
    public boolean isError() {
        return error != null;
    }
}
