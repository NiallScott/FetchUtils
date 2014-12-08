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

import java.io.IOException;

/**
 * This {@link IOException} is thrown when the remote path of the data to be fetched from the server
 * is not the same as the requested path. This happens when a redirect is in place.
 *
 * <p>
 *     For example, the device may be connected to a public Wi-Fi hotspot and all requests are
 *     redirected to the operator's landing page until the user accepts terms and conditions,
 *     provides payment details etc.
 * </p>
 * 
 * @author Niall Scott
 */
public class UrlMismatchException extends IOException {
    
    /**
     * Constructs a new {@code UrlMismatchException} with the default message filled in.
     */
    public UrlMismatchException() {
        super("The URL that was requested does not match the URL that was returned.");
    }
    
    /**
     * Constructs a new {@code UrlMismatchException}, specifying the message.
     * 
     * @param detailMessage The message.
     */
    public UrlMismatchException(final String detailMessage) {
        super(detailMessage);
    }
}