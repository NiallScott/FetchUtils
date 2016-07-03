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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import uk.org.rivernile.android.fetchutils.fetchers.readers.StringFetcherStreamReader;

/**
 * Tests for {@link OkHttp3Fetcher}.
 *
 * @author Niall Scott
 */
public class OkHttp3FetcherTests {

    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=utf-8");

    /**
     * Test that fetching data from a {@link ResponseBody} gets read correctly.
     *
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the test fail.
     */
    @Test
    public void testReadDataSuccessful() throws IOException {
        final StringFetcherStreamReader reader = new StringFetcherStreamReader();
        assertNull(reader.getData());
        final OkHttp3Fetcher fetcher = new OkHttp3Fetcher(
                ResponseBody.create(MEDIA_TYPE, "This is some example text"));
        fetcher.executeFetcher(reader);
        assertEquals("This is some example text", reader.getData());
    }
}