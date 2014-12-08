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

/**
 * Tests for {@link BitmapFetcherStreamReader}.
 * 
 * @author Niall Scott
 */
public class BitmapFetcherStreamReaderTests extends TestCase {
    
    private BitmapFetcherStreamReader reader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        reader = new BitmapFetcherStreamReader();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        reader = null;
    }
    
    /**
     * Test that the {@link android.graphics.Bitmap} is {@code null} by default.
     */
    public void testGetBitmapIsNullByDefault() {
        assertNull(reader.getBitmap());
    }
}