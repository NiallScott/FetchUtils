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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import uk.org.rivernile.android.fetchutils.fetchers.FetcherStreamReader;

/**
 * A {@code BitmapFetcherStreamReader} takes an {@link InputStream} and produces a {@link Bitmap},
 * should the data be formatted appropriately. {@link #getBitmap()} will return the {@link Bitmap}
 * if it is available. This implementation uses the
 * {@link BitmapFactory#decodeStream(java.io.InputStream)} method found inside the Android
 * framework to decode the stream.
 * 
 * @author Niall Scott
 */
public class BitmapFetcherStreamReader implements FetcherStreamReader {
    
    private Bitmap bitmap;

    @Override
    public void readInputStream(@NonNull final InputStream stream) throws IOException {
        bitmap = BitmapFactory.decodeStream(stream);
    }
    
    /**
     * Get the {@link Bitmap} that was returned from the stream. May be {@code null} if no data has
     * been fed in to this class yet, or if there was an error fetching or parsing the data.
     *
     * @return An instance of {@link Bitmap} which contains the image data, or {@code null} if the
     * data has yet to be fetched, or if there was an error.
     */
    @Nullable
    public Bitmap getBitmap() {
        return bitmap;
    }
}