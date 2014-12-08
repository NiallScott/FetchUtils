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

package uk.org.rivernile.android.fetchutils.fetchers;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * A {@code Fetcher} is a component which collects data from a source, such as a remote server or a
 * local file.
 *
 * <p>
 *     To use a {@code Fetcher}, then instantiate one of the included {@code Fetcher}s
 *     (or your own custom one), as well as instantiating an appropriate
 *     {@link FetcherStreamReader}. When the fetch is to happen, then
 *     {@link #executeFetcher(FetcherStreamReader)} should be called. The fetch process is
 *     <b>blocking</b>. Threading is entirely the responsibility of the caller.
 * </p>
 *
 * <p>All {@code Fetcher}s should implement this interface.</p>
 * 
 * @author Niall Scott
 */
public interface Fetcher {
    
    /**
     * Start this {@code Fetcher}. This will execute the fetcher and feed the stream of data in to
     * the supplied {@link FetcherStreamReader}.
     * 
     * @param reader Where the stream of data should be sent to.
     * @throws IOException When there was a problem during the fetching process. This may be
     *                     because, for example, of a network or a missing file.
     */
    public void executeFetcher(@NonNull FetcherStreamReader reader) throws IOException;
}