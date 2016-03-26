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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.support.test.runner.AndroidJUnit4;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link FileWriterFetcherStreamReader}.
 * 
 * @author Niall Scott
 */
@RunWith(AndroidJUnit4.class)
public class FileWriterFetcherStreamReaderTests {
    
    /**
     * Test that the constructor throws an {@link IllegalArgumentException} when given an empty
     * {@code filePath}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyFilePath() {
        new FileWriterFetcherStreamReader("", false);
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    @Test
    public void testConstructorForFileWithAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader(file, true);
        
        assertEquals(file, reader.getFile());
        assertTrue(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    @Test
    public void testConstructorForFileWithNoAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader(file, false);
        
        assertEquals(file, reader.getFile());
        assertFalse(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    @Test
    public void testConstructorForFilePathWithAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader("test", true);
        
        assertEquals(file, reader.getFile());
        assertTrue(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    @Test
    public void testConstructorForFilePathWithNoAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader =
                new FileWriterFetcherStreamReader("test", false);
        
        assertEquals(file, reader.getFile());
        assertFalse(reader.doesAppend());
    }
}