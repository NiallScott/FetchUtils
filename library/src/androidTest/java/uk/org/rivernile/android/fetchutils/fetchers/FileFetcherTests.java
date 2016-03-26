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

import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Tests for {@link FileFetcher}.
 * 
 * @author Niall Scott
 */
@RunWith(AndroidJUnit4.class)
public class FileFetcherTests {
    
    /**
     * Test that an {@link IllegalArgumentException} is thrown if the {@code filePath} is set as an
     * empty {@link String}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyFilePath() {
        new FileFetcher("");
    }
    
    /**
     * Test that the {@link File} instance that is returned by the getter is correct after passing
     * the {@code filePath} in the constructor.
     */
    @Test
    public void testConstructorWithValidFilePath() {
        final File file = new File("test");
        final FileFetcher fetcher = new FileFetcher("test");
        
        assertEquals(file, fetcher.getFile());
    }
    
    /**
     * Test that the file instance this is returned by the getter is correct after passing the
     * {@link File} in the constructor.
     */
    @Test
    public void testConstructorWithValidFile() {
        final File file = new File("test");
        final FileFetcher fetcher = new FileFetcher(file);
        
        assertEquals(file, fetcher.getFile());
    }
}