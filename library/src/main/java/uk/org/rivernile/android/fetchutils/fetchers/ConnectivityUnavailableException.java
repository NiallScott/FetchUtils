/*
 * Copyright (C) 2015 Niall Scott
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

package uk.org.rivernile.android.fetchutils.fetchers;

import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * This {@link Exception} is thrown when it has been determined that there is no connectivity to a
 * network which enables a {@link Fetcher} to complete its request. This check may be done before it
 * attempts to establish a connection.
 *
 * @author Niall Scott
 */
public class ConnectivityUnavailableException extends IOException {

    /**
     * Constructs a new {@code ConnectivityUnavailableException} with the default message filled in.
     */
    public ConnectivityUnavailableException() {
        super("Connectivity is not available.");
    }

    /**
     * Constructs a new {@code ConnectivityUnavailableException}, specifying the message.
     *
     * @param detailMessage The {@link Exception} message.
     */
    public ConnectivityUnavailableException(@Nullable final String detailMessage) {
        super(detailMessage);
    }
}