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
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A {@code HttpFetcher} fetches data from a HTTP server, specified by the given URL. The data is
 * then passed in to an instance of a {@link FetcherStreamReader}.
 *
 * <p>
 *     This class takes care of opening and closing the stream.
 * </p>
 * 
 * @author Niall Scott
 */
public class HttpFetcher implements Fetcher {
    
    private final String url;
    private final boolean allowRedirects;
    
    /**
     * Create a new {@code HttpFetcher}.
     * 
     * @param url The URL to fetch from. Must not be {@code null} or an empty {@link String}.
     * @param allowRedirects {@code true} if redirects are allowed, {@code false} if not.
     */
    public HttpFetcher(@NonNull final String url, final boolean allowRedirects) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("The url must not be null or empty.");
        }
        
        this.url = url;
        this.allowRedirects = allowRedirects;
    }

    @Override
    public void executeFetcher(@NonNull final FetcherStreamReader reader) throws IOException {
        HttpURLConnection conn = null;
        
        try {
            final URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            final InputStream in = conn.getInputStream();
            
            if (!allowRedirects && !u.getHost().equals(conn.getURL().getHost())) {
                conn.disconnect();
                throw new UrlMismatchException();
            }
            
            reader.readInputStream(in);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Get the URL used by this instance.
     * 
     * @return The URL used by this instance.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Get whether this instance allows redirects or not.
     * 
     * @return {@code true} if redirects are allowed, {@code false} if not.
     */
    public boolean getAllowRedirects() {
        return allowRedirects;
    }
}