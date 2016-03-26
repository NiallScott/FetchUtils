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

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.org.rivernile.android.fetchutils.fetchers.FetcherStreamReader;

/**
 * A {@code FileWriterFetcherStreamReader} will take an {@link InputStream} and pipe the data out
 * to the given file. This could be useful, for example, if data was being transferred over HTTP and
 * needed to be piped out to a file on disk.
 * 
 * @author Niall Scott
 */
public class FileWriterFetcherStreamReader implements FetcherStreamReader {
    
    private final File file;
    private final boolean append;
    
    /**
     * Create a new {@code FileWriterFetcherStreamReader}.
     * 
     * @param file The {@link File} that the data will be written out to. Must not be {@code null}.
     * @param append {@code true} if the data should be appended to the end of the file,
     *               {@code false} if the file should be overwritten.
     */
    public FileWriterFetcherStreamReader(@NonNull final File file, final boolean append) {
        this.file = file;
        this.append = append;
    }
    
    /**
     * Create a new {@code FileWriterFetcherStreamReader}.
     * 
     * @param filePath The path to the file that data will be written to. Must not be {@code null}
     *                 or empty.
     * @param append {@code true} if the data should be appended to the end of the file,
     *               {@code false} if the file should be overwritten.
     */
    public FileWriterFetcherStreamReader(@NonNull final String filePath, final boolean append) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be null or empty.");
        }
        
        file = new File(filePath);
        this.append = append;
    }

    @Override
    public void readInputStream(@NonNull final InputStream stream) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(stream);
        final BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(file, append));
        final byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            out.flush();
        }

        out.close();
    }
    
    /**
     * Get a {@link File} object, describing the file that the data will be written out to.
     * 
     * @return A {@link File} object, describing the file that the data will be written out to.
     */
    @NonNull
    public File getFile() {
        return file;
    }
    
    /**
     * Does output to the file append or overwrite?
     * 
     * @return {@code true} if the data is appended to the file, {@code false} if it is overwritten.
     */
    public boolean doesAppend() {
        return append;
    }
}