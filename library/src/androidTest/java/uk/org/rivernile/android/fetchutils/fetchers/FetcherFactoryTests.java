/*
 * Copyright (C) 2014 - 2016 Niall Scott
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link FetcherFactory}.
 * 
 * @author Niall Scott
 */
@RunWith(AndroidJUnit4.class)
public class FetcherFactoryTests {
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns {@code null} when the {@link Uri} is set to {@code null}.
     */
    @Test
    public void testGetFetcherWithNullUri() {
        assertNull(FetcherFactory.getFetcher(InstrumentationRegistry.getContext(), null));
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns an instance of {@link HttpFetcher} when the {@link Uri} scheme is "http".
     */
    @Test
    public void testGetFetcherWithHttp() {
        final Uri uri = Uri.parse("http://www.google.com/");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertSame(HttpFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns an instance of {@link HttpFetcher} when the {@link Uri} scheme is "https".
     */
    @Test
    public void testGetFetcherWithHttps() {
        final Uri uri = Uri.parse("https://www.google.com/");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertSame(HttpFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns {@code null} when a path isn't given for the "android.asset" scheme.
     */
    @Test
    public void testGetFetcherWithAndroidAssetAndNoPath() {
        final Uri uri = Uri.parse("android.asset://");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertNull(fetcher);
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns an instance of {@link AssetFileFetcher} when the {@link Uri} scheme is
     * "android.asset".
     */
    @Test
    public void testGetFetcherWithAndroidAsset() {
        final Uri uri = Uri.parse("android.asset://test/image.jpg");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertSame(AssetFileFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns {@code null} when a path isn't given for the "file" scheme.
     */
    @Test
    public void testGetFetcherWithFileAndNoPath() {
        final Uri uri = Uri.parse("file://");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertNull(fetcher);
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns an instance of {@link FileFetcher} when the {@link Uri} scheme is "file".
     */
    @Test
    public void testGetFetcherWithFile() {
        final Uri uri = Uri.parse("file:///usr/tmp/something.txt");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertSame(FileFetcher.class, fetcher.getClass());
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns {@code null} when the {@link Uri} scheme is not handled.
     */
    @Test
    public void testGetFetcherWithInvalidScheme() {
        final Uri uri = Uri.parse("invalid://test/image.jpg");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertNull(fetcher);
    }
    
    /**
     * Test that {@link FetcherFactory#getFetcher(android.content.Context, android.net.Uri)}
     * returns {@code null} when the {@link Uri} scheme is not handled and the {@link Uri} is not
     * hierarchical.
     */
    @Test
    public void testGetFetcherWithInvalidScheme2() {
        final Uri uri = Uri.parse("mailto:someone@mail.com");
        final Fetcher fetcher = FetcherFactory.getFetcher(InstrumentationRegistry.getContext(),
                uri);
        assertNull(fetcher);
    }
}