/*
 * Copyright (C) 2016 Niall Scott
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

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * An {@code OkHttp3Fetcher} fetches data from a given {@link ResponseBody} which has come from an
 * OkHttp3 response. The data stream is then passed in to the provided {@link FetcherStreamReader}.
 *
 * <p>
 *     Unlike {@link HttpFetcher}, which provides an entire proxy implementation between itself and
 *     {@link java.net.HttpURLConnection}, this class takes a step back and only implements what is
 *     required to receive and handle the {@code byte}s from the network. The onus is on you to
 *     implement anything else that is required, such as checking the HTTP status code and dealing
 *     with connectivity problems. This class will simply just read the {@code byte}s from the
 *     {@link ResponseBody}. This class will close the stream when it is finished with it.
 * </p>
 *
 * <p>
 *     Before using this class, make sure that OkHttp3 is included in your project. To use OkHttp3,
 *     refer to the documentation at the
 *     <a href="http://square.github.io/okhttp/">OkHttp3 website</a>.
 * </p>
 *
 * @author Niall Scott
 */
public class OkHttp3Fetcher implements Fetcher {

    private final ResponseBody responseBody;

    /**
     * Create a new {@code OkHttp3Fetcher}.
     *
     * @param responseBody A {@link ResponseBody} from a OkHttp3 response. Must not be {@code null}.
     */
    public OkHttp3Fetcher(@NonNull final ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public void executeFetcher(@NonNull final FetcherStreamReader reader) throws IOException {
        try {
            reader.readInputStream(responseBody.byteStream());
        } finally {
            responseBody.close();
        }
    }
}
