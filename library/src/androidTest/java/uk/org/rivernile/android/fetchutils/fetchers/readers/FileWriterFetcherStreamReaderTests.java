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

import java.io.File;

import junit.framework.TestCase;

/**
 * Tests for {@link FileWriterFetcherStreamReader}.
 * 
 * @author Niall Scott
 */
public class FileWriterFetcherStreamReaderTests extends TestCase {
    
    /**
     * Test that the constructor throws an {@link IllegalArgumentException} when given an empty
     * {@code filePath}.
     */
    public void testConstructorWithEmptyFilePath() {
        try {
            new FileWriterFetcherStreamReader("", false);
        } catch (IllegalArgumentException e) {
            return;
        }
        
        fail("The filePath was set to empty, so IllegalArgumentException should be thrown.");
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    public void testConstructorForFileWithAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader(file, true);
        
        assertEquals(file, reader.getFile());
        assertTrue(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    public void testConstructorForFileWithNoAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader(file, false);
        
        assertEquals(file, reader.getFile());
        assertFalse(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    public void testConstructorForFilePathWithAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader = new FileWriterFetcherStreamReader("test", true);
        
        assertEquals(file, reader.getFile());
        assertTrue(reader.doesAppend());
    }
    
    /**
     * Test that the values passed in to the constructor match the getter methods.
     */
    public void testConstructorForFilePathWithNoAppend() {
        final File file = new File("test");
        final FileWriterFetcherStreamReader reader =
                new FileWriterFetcherStreamReader("test", false);
        
        assertEquals(file, reader.getFile());
        assertFalse(reader.doesAppend());
    }
}