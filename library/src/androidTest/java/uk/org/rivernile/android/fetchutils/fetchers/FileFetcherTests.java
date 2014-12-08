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

import java.io.File;

import junit.framework.TestCase;

/**
 * Tests for {@link FileFetcher}.
 * 
 * @author Niall Scott
 */
public class FileFetcherTests extends TestCase {
    
    /**
     * Test that an {@link IllegalArgumentException} is thrown if the {@code filePath} is set as an
     * empty {@link String}.
     */
    public void testConstructorWithEmptyFilePath() {
        try {
            new FileFetcher("");
        } catch (IllegalArgumentException e) {
            return;
        }
        
        fail("The filePath was set as empty, so an IllegalArgumentException should be thrown.");
    }
    
    /**
     * Test that the {@link File} instance that is returned by the getter is correct after passing
     * the {@code filePath} in the constructor.
     */
    public void testConstructorWithValidFilePath() {
        final File file = new File("test");
        final FileFetcher fetcher = new FileFetcher("test");
        
        assertEquals(file, fetcher.getFile());
    }
    
    /**
     * Test that the file instance this is returned by the getter is correct after passing the
     * {@link File} in the constructor.
     */
    public void testConstructorWithValidFile() {
        final File file = new File("test");
        final FileFetcher fetcher = new FileFetcher(file);
        
        assertEquals(file, fetcher.getFile());
    }
}