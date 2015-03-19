/*
 * Copyright (C) 2014 - 2015 Niall Scott
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

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class contains a static method that allows an appropriate {@link Fetcher} to be created
 * depending on the supplied {@link Uri}.
 * 
 * @author Niall Scott
 * @see #getFetcher(android.content.Context, android.net.Uri)
 */
public final class FetcherFactory {
    
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private static final String SCHEME_ASSET = "android.asset";
    private static final String SCHEME_FILE = "file";
    
    /**
     * This private constructor exists to prevent instantiation of this class.
     */
    private FetcherFactory() {
        // Intentionally left blank.
    }
    
    /**
     * Get the most appropriate {@link Fetcher} for the given {@link Uri}. This
     * is based on the scheme in the {@link Uri} object.
     *
     * <p>
     *     The following are supported;
     * 
     *     <ul>
     *         <li>{@code http://<host>[:port]/[path]}</li>
     *         <li>{@code https://<host>[:port]/[path]}</li>
     *         <li>{@code android.asset://<path>}</li>
     *         <li>{@code file://<path>}</li>
     *     </ul>
     * </p>
     *
     * <p>
     *     Note: this will return the most simply configured {@link Fetcher}. If a {@link Fetcher}
     *     has more configuration options beyond setting a path or a URL, then you will need to
     *     write your own factory which deals with different schemes. It is possible to instantiate
     *     {@link Fetcher}s directly.
     * </p>
     * 
     * @param context A {@link Context} instance. Must not be {@code null}.
     * @param uri The {@link Uri} of the data to be fetched. If this is set as {@code null}, then
     *            this method will return {@code null}. If the {@link Uri} is incomplete, such as if
     *            it contains a {@code file://} scheme without a path, {@code null} will be
     *            returned.
     * @return An appropriate {@link Fetcher} for the given {@link Uri}, or {@code null} if there is
     *         no suitable {@link Fetcher}s or {@code uri} is
     *         set as {@code null}.
     */
    public static Fetcher getFetcher(@NonNull final Context context, @Nullable final Uri uri) {
        if (uri == null) {
            return null;
        }
        
        final String scheme = uri.getScheme();

        try {
            if (SCHEME_HTTP.equalsIgnoreCase(scheme) ||
                    SCHEME_HTTPS.equalsIgnoreCase(scheme)) {
                // Return the most simply configured HttpFetcher instance.
                return new HttpFetcher.Builder(context)
                        .setUrl(uri.toString())
                        .build();
            } else if (SCHEME_ASSET.equalsIgnoreCase(scheme)) {
                return new AssetFileFetcher(context, uri.getPath());
            } else if (SCHEME_FILE.equalsIgnoreCase(scheme)) {
                return new FileFetcher(uri.getPath());
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        
        return null;
    }
}