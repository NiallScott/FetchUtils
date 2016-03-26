/*
 * Copyright (C) 2015 - 2016 Niall Scott
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.Proxy;
import java.util.Map;

/**
 * Tests for {@link HttpFetcher}.
 *
 * @author Niall Scott
 */
@RunWith(AndroidJUnit4.class)
public class HttpFetcherTests {

    /**
     * Test that attempting to build a {@link HttpFetcher} with a {@code null} URL throws an
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithNullUrl() {
        final HttpFetcher.Builder builder =
                new HttpFetcher.Builder(InstrumentationRegistry.getTargetContext());
        builder.setUrl(null);
        builder.build();
    }

    /**
     * Test that attempting to build a {@link HttpFetcher} with an empty URL throws an
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuilderWithEmptyUrl() {
        final HttpFetcher.Builder builder =
                new HttpFetcher.Builder(InstrumentationRegistry.getTargetContext());
        builder.setUrl("");
        builder.build();
    }

    /**
     * Test that the default values used when not set externally are as expected.
     */
    @Test
    public void testDefaultValues() {
        final HttpFetcher fetcher = new HttpFetcher.Builder(
                        InstrumentationRegistry.getTargetContext())
                .setUrl("http://example.com/")
                .build();

        assertFalse(fetcher.hasRun());
        assertEquals("http://example.com/", fetcher.getUrl());
        assertNull(fetcher.getProxy());
        assertTrue(fetcher.isAllowHostRedirects());
        assertTrue(fetcher.isFollowRedirects());
        assertEquals("GET", fetcher.getRequestMethod());
        assertEquals(0, fetcher.getConnectTimeout());
        assertEquals(0, fetcher.getModifiedSince());
        assertEquals(0, fetcher.getReadTimeout());
        assertTrue(fetcher.isUseCaches());
        assertNull(fetcher.getCustomHeaders());
        assertEquals("random", fetcher.getCustomHeader("any", "random"));
    }

    /**
     * Test that the builder behaves correctly when non-default values are used.
     */
    @Test
    public void testBuilderWithNonDefaultValues() {
        final HttpFetcher fetcher = new HttpFetcher.Builder(
                        InstrumentationRegistry.getTargetContext())
                .setUrl("http://example.com/")
                .setProxy(Proxy.NO_PROXY)
                .setAllowHostRedirects(false)
                .setFollowRedirects(false)
                .setRequestMethod("POST")
                .setConnectTimeout(1000)
                .setIfModifiedSince(123456789)
                .setReadTimeout(500)
                .setUseCaches(false)
                .setCustomHeader("key", "value")
                .setCustomHeader("key2", "value2")
                .build();

        assertFalse(fetcher.hasRun());
        assertEquals("http://example.com/", fetcher.getUrl());
        assertSame(Proxy.NO_PROXY, fetcher.getProxy());
        assertFalse(fetcher.isAllowHostRedirects());
        assertFalse(fetcher.isFollowRedirects());
        assertEquals("POST", fetcher.getRequestMethod());
        assertEquals(1000, fetcher.getConnectTimeout());
        assertEquals(123456789, fetcher.getModifiedSince());
        assertEquals(500, fetcher.getReadTimeout());
        assertFalse(fetcher.isUseCaches());

        // Test custom headers.
        final Map<String, String> customHeaders = fetcher.getCustomHeaders();
        assertNotNull(customHeaders);
        assertEquals(2, customHeaders.size());
        assertEquals("value", customHeaders.get("key"));
        assertEquals("value2", customHeaders.get("key2"));

        assertEquals("value", fetcher.getCustomHeader("key", "random"));
        assertEquals("value2", fetcher.getCustomHeader("key2", "random2"));
    }
}
