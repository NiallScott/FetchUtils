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
import java.io.InputStream;

/**
 * A {@code FetcherSteamReader} is an interface that should be implemented by classes capturing a
 * stream of data from a {@link Fetcher}. {@link Fetcher}s require an instance of a class
 * implementing this interface before fetching data.
 * 
 * @author Niall Scott
 */
public interface FetcherStreamReader {
    
    /**
     * This method is called when an {@link InputStream} is available to read from. Do not close the
     * stream inside this method, this will be done inside the calling {@link Fetcher} classes.
     * Simply use this method to get bytes from the stream.
     * 
     * @param stream The {@link InputStream}.
     * @throws IOException When an {@link IOException} occurs.
     */
    public void readInputStream(@NonNull InputStream stream) throws IOException;
}