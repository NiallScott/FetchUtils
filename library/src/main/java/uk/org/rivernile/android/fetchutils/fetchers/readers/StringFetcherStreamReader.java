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

package uk.org.rivernile.android.fetchutils.fetchers.readers;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.org.rivernile.android.fetchutils.fetchers.FetcherStreamReader;

/**
 * A {@code StringFetcherStreamReader} takes an {@link InputStream} and creates a {@link String}
 * version of this data.
 * 
 * @author Niall Scott
 */
public class StringFetcherStreamReader implements FetcherStreamReader {
    
    private String data;

    @Override
    public void readInputStream(@NonNull final InputStream stream) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final char[] buf = new char[1024];
        int len;
        
        // Use an array buffer rather than reading in to String, otherwise we create lots of String
        // objects that need garbage collected.
        while ((len = reader.read(buf, 0, 1024)) != -1) {
            sb.append(buf, 0, len);
        }
        
        data = sb.toString();
    }
    
    /**
     * Get the data that was read from the stream as a {@link String}. This may be {@code null} if
     * the stream has yet to be read from, or if there was an error while reading the stream.
     * 
     * @return The data read from the stream as a {@link String}, or {@code null} if it has not yet
     * been read from or there was an error while reading it.
     */
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return getData();
    }
}