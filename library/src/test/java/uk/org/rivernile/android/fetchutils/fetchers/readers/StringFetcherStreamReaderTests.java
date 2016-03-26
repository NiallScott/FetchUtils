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

package uk.org.rivernile.android.fetchutils.fetchers.readers;

import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link StringFetcherStreamReader}.
 * 
 * @author Niall Scott
 */
public class StringFetcherStreamReaderTests {
    
    private StringFetcherStreamReader reader;

    @Before
    public void setUp() throws Exception {
        reader = new StringFetcherStreamReader();
    }

    @After
    public void tearDown() throws Exception {
        reader = null;
    }
    
    /**
     * Test that {@link StringFetcherStreamReader#getData()} returns {@code null} by default.
     */
    @Test
    public void testGetDataReturnsNullByDefault() {
        assertNull(reader.getData());
    }
    
    /**
     * Test that {@link StringFetcherStreamReader#toString()} returns {@code null} by default.
     */
    @Test
    public void testToStringReturnsNullByDefault() {
        assertNull(reader.toString());
    }
}