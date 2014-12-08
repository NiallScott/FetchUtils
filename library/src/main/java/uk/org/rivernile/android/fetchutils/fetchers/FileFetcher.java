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

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A {@code FileFetcher} fetches data from a given {@link File} or path. The data is then passed in
 * to an instance of a {@link FetcherStreamReader}.
 *
 * <p>
 *     This class takes care of opening and closing the file.
 * </p>
 * 
 * @author Niall Scott
 */
public class FileFetcher implements Fetcher {
    
    private final File file;
    
    /**
     * Create a new instance of {@code FileFetcher}, specifying the path of the file.
     * 
     * @param filePath The path to the file that is to be read from. Must not be {@code null} or
     *                 empty.
     */
    public FileFetcher(@NonNull final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be null or empty.");
        }
        
        file = new File(filePath);
    }
    
    /**
     * Create a new instance of {@code FileFetcher}, specifying the {@link File} to read from.
     * 
     * @param file The {@link File} to read from. Must not be {@code null}.
     */
    public FileFetcher(@NonNull final File file) {
        this.file = file;
    }

    @Override
    public void executeFetcher(@NonNull final FetcherStreamReader reader) throws IOException {
        FileInputStream in = null;
        
        try {
            in = new FileInputStream(file);
            reader.readInputStream(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Nothing to do here.
                }
            }
        }
    }
    
    /**
     * Get the {@link File} that this instance uses to read data from.
     * 
     * @return A {@link File} object describing the file that this instance points at.
     */
    public File getFile() {
        return file;
    }
}