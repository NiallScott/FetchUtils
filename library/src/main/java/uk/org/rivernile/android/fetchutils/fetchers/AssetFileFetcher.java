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

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@code AssetFileFetcher} fetches data from a given file path in the application assets. The
 * data is then given in to an instance of a {@link FetcherStreamReader}.
 *
 * <p>
 *     This class takes care of opening and closing the file.
 * </p>
 * 
 * @author Niall Scott
 */
public class AssetFileFetcher implements Fetcher {
    
    private final Context context;
    private final String filePath;

    /**
     * Create a new {@code AssetFileFetcher}.
     * 
     * @param context A {@link Context} instance. Cannot be {@code null}.
     * @param filePath The path of the file to load, relative to the assets directory. Must not be
     *                 {@code null} or empty. If the file is not readable at the time this fetcher
     *                 is executed, then the execution will throw an {@link IOException}.
     */
    public AssetFileFetcher(@NonNull final Context context, @NonNull final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("The filePath must not be null or empty.");
        }

        this.context = context;
        this.filePath = filePath;
    }

    @Override
    public void executeFetcher(@NonNull final FetcherStreamReader reader) throws IOException {
        InputStream in = null;
        
        try {
            in = context.getAssets().open(filePath);
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
     * Get the path to the file, as given in the constructor. This path will be relative to the
     * application's assets directory.
     * 
     * @return The path to the file that this instance is dealing with.
     */
    public String getFilePath() {
        return filePath;
    }
}