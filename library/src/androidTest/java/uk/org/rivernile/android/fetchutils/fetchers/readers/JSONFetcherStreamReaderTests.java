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

import junit.framework.TestCase;

import org.json.JSONException;

/**
 * Tests for {@link JSONFetcherStreamReader}.
 * 
 * @author Niall Scott
 */
public class JSONFetcherStreamReaderTests extends TestCase {
    
    private JSONFetcherStreamReader reader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        reader = new JSONFetcherStreamReader();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        reader = null;
    }
    
    /**
     * Test that a {@link JSONException} is thrown when the data is {@code null}.
     */
    public void testGetJSONObjectWithNullData() {
        try {
            reader.getJSONObject();
        } catch (JSONException e) {
            return;
        }
        
        fail("The data is null, so attempting to get a JSONObject should yield a JSONException.");
    }
    
    /**
     * Test that a {@link JSONException} is thrown when the data is {@code null}.
     */
    public void testGetJSONArrayWithNullData() {
        try {
            reader.getJSONArray();
        } catch (JSONException e) {
            return;
        }
        
        fail("The data is null, so attempting to get a JSONArray should yield a JSONException.");
    }
}