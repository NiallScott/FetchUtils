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

import junit.framework.TestCase;

/**
 * Tests for {@link HttpFetcher}.
 * 
 * @author Niall Scott
 */
public class HttpFetcherTests extends TestCase {
    
    /**
     * Test that the constructor throws an {@link IllegalArgumentException} when the url is set to
     * an empty {@link String}.
     */
    public void testConstructorWithEmptyUrl() {
        try {
            new HttpFetcher("", false);
        } catch (IllegalArgumentException e) {
            return;
        }
        
        fail("The url was set as empty, so an IllegalArgumentException should be thrown.");
    }
    
    /**
     * Test the getters return correct data after passing valid arguments to the constructor.
     */
    public void testValidConstructor() {
        final HttpFetcher fetcher = new HttpFetcher("http://example.com/test", true);
        assertEquals("http://example.com/test", fetcher.getUrl());
        assertTrue(fetcher.getAllowRedirects());
    }
}