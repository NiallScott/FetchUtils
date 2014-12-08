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

import android.net.Uri;
import android.test.InstrumentationTestCase;

/**
 * Tests for {@link FetcherFactory}.
 * 
 * @author Niall Scott
 */
public class FetcherFactoryTests extends InstrumentationTestCase {
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns {@code null} when the {@link Uri} is set to {@code null}.
     */
    public void testGetFetcherWithNullUri() {
        assertNull(FetcherFactory.getFetcher(getInstrumentation().getContext(), null, true));
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns an instance of {@link HttpFetcher} when the {@link Uri} scheme is "http".
     */
    public void testGetFetcherWithHttp() {
        final Uri uri = Uri.parse("http://www.google.com/");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertSame(HttpFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns an instance of {@link HttpFetcher} when the {@link Uri} scheme is "https".
     */
    public void testGetFetcherWithHttps() {
        final Uri uri = Uri.parse("https://www.google.com/");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertSame(HttpFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns {@code null} when a path isn't given for the "android.asset" scheme.
     */
    public void testGetFetcherWithAndroidAssetAndNoPath() {
        final Uri uri = Uri.parse("android.asset://");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertNull(fetcher);
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns an instance of {@link AssetFileFetcher} when the {@link Uri} scheme is
     * "android.asset".
     */
    public void testGetFetcherWithAndroidAsset() {
        final Uri uri = Uri.parse("android.asset://test/image.jpg");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertSame(AssetFileFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns {@code null} when a path isn't given for the "file" scheme.
     */
    public void testGetFetcherWithFileAndNoPath() {
        final Uri uri = Uri.parse("file://");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertNull(fetcher);
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns an instance of {@link FileFetcher} when the {@link Uri} scheme is "file".
     */
    public void testGetFetcherWithFile() {
        final Uri uri = Uri.parse("file:///usr/tmp/something.txt");
        final Fetcher fetcher = FetcherFactory.getFetcher(getInstrumentation()
                .getContext(), uri, true);
        assertSame(FileFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns {@code null} when the {@link Uri} scheme is not handled.
     */
    public void testGetFetcherWithInvalidScheme() {
        final Uri uri = Uri.parse("invalid://test/image.jpg");
        final Fetcher fetcher = FetcherFactory
                .getFetcher(getInstrumentation().getContext(), uri, true);
        assertNull(fetcher);
    }
    
    /**
     * Test that
     * {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri, boolean)}
     * returns {@code null} when the {@link Uri} scheme is not handled and the {@link Uri} is not
     * hierarchical.
     */
    public void testGetFetcherWithInvalidScheme2() {
        final Uri uri = Uri.parse("mailto:someone@mail.com");
        final Fetcher fetcher = FetcherFactory
                .getFetcher(getInstrumentation().getContext(), uri, true);
        assertNull(fetcher);
    }
}