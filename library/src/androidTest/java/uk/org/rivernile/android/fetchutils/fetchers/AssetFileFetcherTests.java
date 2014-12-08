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

import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.org.rivernile.android.fetchutils.fetchers.readers.BitmapFetcherStreamReader;
import uk.org.rivernile.android.fetchutils.fetchers.readers.FileWriterFetcherStreamReader;
import uk.org.rivernile.android.fetchutils.fetchers.readers.JSONFetcherStreamReader;
import uk.org.rivernile.android.fetchutils.fetchers.readers.StringFetcherStreamReader;

/**
 * Tests for {@link AssetFileFetcher}. This {@link InstrumentationTestCase} is also used to test the
 * execution of the {@link FetcherStreamReader} classes as storing test data in the assets is
 * easier.
 * 
 * @author Niall Scott
 */
public class AssetFileFetcherTests extends InstrumentationTestCase {
    
    /**
     * Test that an {@link IllegalArgumentException} is thrown when the file name is set to blank.
     */
    public void testConstructorWithEmptyFilename() {
        try {
            new AssetFileFetcher(getInstrumentation().getContext(), "");
        } catch (IllegalArgumentException e) {
            return;
        }
        
        fail("The context is set to null, so an IllegalArgumentException should be thrown.");
    }
    
    /**
     * Test that the file name that is passed in the constructor matches what is returned in the
     * getter.
     */
    public void testGetFilename() {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "test");
        assertEquals("test", fetcher.getFilePath());
    }
    
    /**
     * Test that an {@link IOException} is thrown when an invalid file name is passed to the
     * {@link AssetFileFetcher} and is executed.
     */
    public void testInvalidFilename() {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "invalid");
        
        try {
            fetcher.executeFetcher(new StringFetcherStreamReader());
        } catch (IOException e) {
            return;
        }
        
        fail("An invalid filename was set so an IOException should be thrown.");
    }
    
    /**
     * Test that a {@link Bitmap} can be successfully read.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     */
    public void testBitmap() throws IOException {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "fetchers/redsquare.png");
        final BitmapFetcherStreamReader reader = new BitmapFetcherStreamReader();
        
        fetcher.executeFetcher(reader);
        
        final Bitmap bitmap = reader.getBitmap();
        assertNotNull(bitmap);
        assertEquals(6, bitmap.getWidth());
        assertEquals(4, bitmap.getHeight());
    }
    
    /**
     * Test that no {@link Bitmap} is set when an invalid/corrupt {@link Bitmap} file is read from.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     */
    public void testInvalidBitmap() throws IOException {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(),
                        "fetchers/not_an_image.png");
        final BitmapFetcherStreamReader reader = new BitmapFetcherStreamReader();
        fetcher.executeFetcher(reader);
        
        assertNull(reader.getBitmap());
    }
    
    /**
     * Test that data can be passed from an {@link AssetFileFetcher} to a
     * {@link FileWriterFetcherStreamReader} and read the data back in to make sure it is correct.
     * This makes sure that data is overwritten.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     */
    public void testFileWriterWithoutAppend() throws IOException {
        final AssetFileFetcher assetFetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "fetchers/example.txt");
        final File outFile =
                new File(getInstrumentation().getTargetContext().getFilesDir(), "out.txt");
        final FileWriterFetcherStreamReader assetReader =
                new FileWriterFetcherStreamReader(outFile, false);
        
        // Executed twice to make sure the data is not appended.
        assetFetcher.executeFetcher(assetReader);
        assetFetcher.executeFetcher(assetReader);
        
        final FileFetcher fileFetcher = new FileFetcher(outFile);
        final StringFetcherStreamReader fileReader = new StringFetcherStreamReader();
        fileFetcher.executeFetcher(fileReader);
        
        assertEquals("This is example text.", fileReader.toString().trim());

        if (!outFile.delete()) {
            fail("Unable to delete test file.");
        }
    }
    
    /**
     * Test that data can be passed from an {@link AssetFileFetcher} to a
     * {@link FileWriterFetcherStreamReader} and read the data back in to make sure it is correct.
     * This makes sure that data is appended.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     */
    public void testFileWriterWithAppend() throws IOException {
        final AssetFileFetcher assetFetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "fetchers/example.txt");
        final File outFile =
                new File(getInstrumentation().getTargetContext().getFilesDir(), "out.txt");
        final FileWriterFetcherStreamReader assetReader =
                new FileWriterFetcherStreamReader(outFile, true);
        
        // Executed twice to make sure the data is appended.
        assetFetcher.executeFetcher(assetReader);
        assetFetcher.executeFetcher(assetReader);
        
        final FileFetcher fileFetcher = new FileFetcher(outFile);
        final StringFetcherStreamReader fileReader = new StringFetcherStreamReader();
        fileFetcher.executeFetcher(fileReader);
        
        assertEquals("This is example text.\nThis is example text.", fileReader.toString().trim());

        if (!outFile.delete()) {
            fail("Unable to delete test file.");
        }
    }
    
    /**
     * Test that a file containing {@link String} data can be read from.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     */
    public void testStringReader() throws IOException {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(), "fetchers/example.txt");
        final StringFetcherStreamReader reader = new StringFetcherStreamReader();
        fetcher.executeFetcher(reader);
        
        assertEquals("This is example text.", reader.toString().trim());
    }
    
    /**
     * Test that a {@link JSONObject} can be read from.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     * @throws JSONException This test is not expected to be thrown, so if it is, let the
     * {@link InstrumentationTestCase} fail the test.
     */
    public void testJSONObject() throws IOException, JSONException {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(),
                        "fetchers/example_object.json");
        final JSONFetcherStreamReader reader = new JSONFetcherStreamReader();
        fetcher.executeFetcher(reader);
        
        final JSONObject jo = reader.getJSONObject();
        assertEquals("A JSON String.", jo.getString("example"));
    }
    
    /**
     * Test that a {@link JSONArray} can be read from.
     * 
     * @throws IOException This test is not expected to throw an {@link IOException}, so if it is
     * thrown, let the {@link InstrumentationTestCase} cause a failure.
     * @throws JSONException This test is not expected to be thrown, so if it is, let the
     * {@link InstrumentationTestCase} fail the test.
     */
    public void testJSONArray() throws IOException, JSONException {
        final AssetFileFetcher fetcher =
                new AssetFileFetcher(getInstrumentation().getContext(),
                        "fetchers/example_array.json");
        final JSONFetcherStreamReader reader = new JSONFetcherStreamReader();
        fetcher.executeFetcher(reader);
        
        final JSONArray ja = reader.getJSONArray();
        assertEquals("One", ja.getString(0));
        assertEquals("Two", ja.getString(1));
        assertEquals("Three", ja.getString(2));
        assertEquals("Four", ja.getString(3));
    }
}