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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@code JSONFetcherStreamReader} takes an {@link java.io.InputStream} and produces a
 * {@link JSONObject} or {@link JSONArray} version of this data. It is up to the caller whether the
 * data is treated as a {@link JSONObject} or a {@link JSONArray} - the objects are created at the
 * time of calling this class' getters.
 * 
 * @author Niall Scott
 */
public class JSONFetcherStreamReader extends StringFetcherStreamReader {
    
    /**
     * Get a {@link JSONObject} version of this data.
     * 
     * @return A {@link JSONObject}, which is the root of the document tree.
     * @throws JSONException If there was an error parsing the JSON text, such as when the data does
     * not represent a {@link JSONObject}.
     */
    @NonNull
    public JSONObject getJSONObject() throws JSONException {
        final String data = getData();
        
        if (data == null) {
            throw new JSONException("The data is null.");
        }
        
        return new JSONObject(data);
    }
    
    /**
     * Get a {@link JSONArray} version of this data.
     * 
     * @return A {@link JSONArray}, which is the root of the document tree.
     * @throws JSONException If there was an error parsing the JSON text, such as when the data does
     * not represent a {@link JSONArray}.
     */
    @NonNull
    public JSONArray getJSONArray() throws JSONException {
        final String data = getData();
        
        if (data == null) {
            throw new JSONException("The data is null.");
        }
        
        return new JSONArray(data);
    }
}