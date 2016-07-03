/*
 * Copyright (C) 2014 - 2015 Niall Scott
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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code HttpFetcher} fetches data from a HTTP server, specified by the given URL. The stream is
 * then passed in to an instance of a {@link FetcherStreamReader}.
 *
 * <p>
 *     This class is backed by the implementations of {@link java.net.URLConnection},
 *     {@link HttpURLConnection} and {@link javax.net.ssl.HttpsURLConnection}. Most of the fields in
 *     this class are just simply proxies through to those classes. Mostly, the documentation for
 *     those classes apply here - see the individual methods in this class for any possible
 *     exceptions. This class makes the assumption that you are familiar with those classes.
 * </p>
 *
 * <p>
 *     This class takes care of opening and closing the stream. Each instance of this class can only
 *     be run once. To perform another HTTP request, a new instance of this class must be created -
 *     it is not reusable.
 * </p>
 *
 * <p>
 *     If this class is being used on an Android platform prior to Android Froyo (API level 8), then
 *     the system property "http.keepAlive" has been set to {@code false}. This is to workaround a
 *     known issue whereby the connection pool can be poisoned. Please see the documentation in
 *     {@link HttpURLConnection} for more details. This is set during static initialisation of this
 *     class, so if for some reason you want to turn it back on while using a platform version prior
 *     to Froyo, then you'll need to set it after the first use of this class.
 * </p>
 *
 * <h3>Usage</h3>
 * Usage is straight forward. Firstly, a {@link Builder} must be instantiated. At a minimum,
 * {@link Builder#setUrl(String)} must be called - all other setters are optional. If a setter isn't
 * called, its default value will be used. Once configuration has taken place, call
 * {@link HttpFetcher.Builder#build()} to obtain a {@link HttpFetcher} instance. For example;
 *
 * <pre>
 * <code>
 * HttpFetcher.Builder builder = new HttpFetcher.Builder(getContext());
 * builder.setUrl("http://www.android.com/")
 *         .setAllowHostRedirects(false)
 *         .setConnectTimeout(30000) // 30 seconds - just an example.
 *         .setReadTimeout(15000); // 15 seconds - just an example.
 * HttpFetcher fetcher = builder.build();
 * </code>
 * </pre>
 *
 * <p>
 *     Now that an instance of {@code HttpFetcher} has been created, it can now be used to perform
 *     the HTTP request. For example;
 * </p>
 *
 * <pre>
 * <code>
 * FetcherStreamReader reader = new StringFetcherStreamReader();
 *
 * try {
 *     fetcher.executeFetcher(reader);
 *     int responseCode = fetcher.getResponseCode(); // Example of getting a property post-request.
 *     String data = reader.getData(); // Example of getting the data from the HTTP server.
 * } catch (ConnectivityUnavailableException e) {
 *     // There's no connectivity - unable to perform request. Handle this gracefully.
 * } catch (UrlMismatchException e) {
 *     // The instance is configured to not allow host redirects. This exception is thrown when this
 *     // happens. Maybe inform the user, asking them to sign in to the network?
 * } catch (IOException e) {
 *     // IOException catch-all handler. Handle these gracefully.
 * }
 * </code>
 * </pre>
 *
 * <h3>Changes</h3>
 * Where appropriate, some changes have been made;
 *
 * <p>
 *     <ul>
 *         <li>If the hosting application has requested the permission
 *             {@code android.permission.ACCESS_NETWORK_STATE}, then a connectivity check will
 *             happen prior to connecting to the HTTP server. This consists of querying the
 *             {@link ConnectivityManager} to ensure there is an active network connection.</li>
 *         <li>A new property has been added which can be set with
 *             {@link Builder#setAllowHostRedirects(boolean)}. This allows host redirection to be
 *             detected and an exception will be thrown when a redirect between hosts happens. You
 *             may want to enable this behaviour to detect when network sign on is being enforced,
 *             so that the user can be prompted to sign in to a Wi-Fi network. See the
 *             {@link HttpURLConnection} documentation for more details.</li>
 *     </ul>
 * </p>
 *
 * <h3>Alternative implementations</h3>
 * OkHttp3 is supported by the {@link OkHttp3Fetcher} class. Its design is quite different to the
 * way this class works.
 *
 * <h3>TODO</h3>
 * TODO: implement HTTPS specific methods (HTTPS is supported, but not customisable).<br/>
 * TODO: implement posting content to a server.
 * 
 * @author Niall Scott
 */
public class HttpFetcher implements Fetcher {

    private final ConnectivityManager connMan;

    private final Context context;
    private final String url;
    private final Proxy proxy;
    private final boolean allowHostRedirects;
    //private final int chunkLength;
    //private final long contentLength;
    private final boolean followRedirects;
    private final String requestMethod;
    private final int connectTimeout;
    //private final boolean doInput;
    //private final boolean doOutput;
    private final long modifiedSince;
    private final int readTimeout;
    private final boolean useCaches;
    private final HashMap<String, String> customHeaders;

    private HttpURLConnection connection;

    static {
        // As recommended in the Javadoc for HttpUrlConnection.
        // https://developer.android.com/reference/java/net/HttpURLConnection.html
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Construct a {@code HttpFetcher} from a {@link Builder}. This constructor is private - it
     * should be called by {@link Builder#build()}.
     *
     * @param builder The instance of the {@link Builder} to construct from.
     */
    private HttpFetcher(@NonNull final Builder builder) {
        context = builder.context;
        url = builder.url;
        proxy = builder.proxy;
        allowHostRedirects = builder.allowHostRedirects;
        //chunkLength = builder.chunkLength;
        //contentLength = builder.contentLength;
        followRedirects = builder.followRedirects;
        requestMethod = builder.requestMethod;
        connectTimeout = builder.connectTimeout;
        //doInput = builder.doInput;
        //doOutput = builder.doOutput;
        modifiedSince = builder.modifiedSince;
        readTimeout = builder.readTimeout;
        useCaches = builder.useCaches;
        customHeaders = builder.customHeaders;

        connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public synchronized void executeFetcher(@NonNull final FetcherStreamReader reader)
            throws IOException {
        if (connection != null) {
            throw new IllegalStateException("This instance can only be used once. Please create " +
                    "a new instance.");
        }

        if (!isConnected()) {
            throw new ConnectivityUnavailableException();
        }

        HttpURLConnection conn = null;
        
        try {
            final URL u = new URL(url);
            conn = (HttpURLConnection) (proxy != null ? u.openConnection(proxy)
                    : u.openConnection());
            configureConnectionInstance(conn);
            InputStream in;

            try {
                in = conn.getInputStream();
            } catch (IOException e) {
                in = conn.getErrorStream();

                if (in == null) {
                    // This happens when the error happened before reaching the server
                    // (connectivity, DNS unresolvable etc). In this case, throw the exception down
                    // to the caller.
                    throw e;
                }
            }
            
            if (!allowHostRedirects && !u.getHost().equals(conn.getURL().getHost())) {
                conn.disconnect();
                throw new UrlMismatchException();
            }
            
            reader.readInputStream(in);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        connection = conn;
    }

    /*
     ******************
     * Configuration. *
     ******************
     */

    /**
     * Get the URL that this instance is configured for.
     *
     * @return The URL that this instance is configured for.
     * @see Builder#setUrl(String)
     */
    @NonNull
    public String getUrl() {
        return url;
    }

    /**
     * Get the {@link Proxy} configured for this instance, if one is configured.
     *
     * @return The {@link Proxy} configured for this insance, if one is configured. Otherwise,
     *         {@code null} is returned.
     * @see Builder#setProxy(java.net.Proxy)
     */
    @Nullable
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Does this instance allow redirects between different hosts?
     *
     * @return {@code true} if this instance allows redirects between different hosts, {@code false}
     *         if not.
     * @see Builder#setAllowHostRedirects(boolean)
     */
    public boolean isAllowHostRedirects() {
        return allowHostRedirects;
    }

    /* TODO: enable these methods when posting has been implemented.
    **
     * Get the chunk length configured for this instance.
     *
     * @return The chunk length configured for this instance, or {@code -1} if chunking mode is not
     *         used.
     * @see Builder#setChunkedStreamingMode(int)
     * @see HttpURLConnection#chunkLength
     *
    public int getChunkLength() {
        return chunkLength;
    }

    **
     * Get the fixed content length configured for this instance.
     *
     * @return The fixed content length configured for this instance, or {@code -1} if it not used
     *         or known.
     * @see Builder#setFixedLengthStreamingMode(long)
     * @see HttpURLConnection#fixedContentLength
     * @see HttpURLConnection#fixedContentLengthLong
     *
    public long getFixedContentLength() {
        return contentLength;
    }*/

    /**
     * Should this instance follow redirects?
     *
     * @return {@code true} if this instance should follow redirects, {@code false} if not.
     * @see Builder#setFollowRedirects(boolean)
     * @see HttpURLConnection#getInstanceFollowRedirects()
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * Get the request method configured for this instance.
     *
     * @return The request method configured for this instance. If this method returns {@code null},
     *         assume the configured request method is {@code GET}.
     * @see Builder#setRequestMethod(String)
     * @see HttpURLConnection#getRequestMethod()
     */
    @Nullable
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Get the connection timeout configured for this instance.
     *
     * @return The connection timeout configured for this instance, or {@code 0} if the default is
     *         used.
     * @see Builder#setConnectTimeout(int)
     * @see java.net.URLConnection#getConnectTimeout()
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /* TODO: enable these methods when posting has been implemented.
    **
     * Is this instance configured for outputting data?
     *
     * @return {@code true} if this instance is configured for outputting data, {@code false} if
     *         not.
     * @see Builder#setDoInput(boolean)
     * @see java.net.URLConnection#getDoInput()
     *
    public boolean isDoInput() {
        return doInput;
    }

    **
     * Is this instance configured for receiving data?
     *
     * @return {@code true} if this instance is configured for receiving data, {@code false} if not.
     * @see Builder#setDoOutput(boolean)
     * @see java.net.URLConnection#getDoOutput()
     *
    public boolean isDoOutput() {
        return doOutput;
    }*/

    /**
     * Get the modification timestamp configured for this instance.
     *
     * @return The modification timestamp configured for this instance, or {@code 0} if the default
     *         value is used.
     * @see Builder#setIfModifiedSince(long)
     * @see java.net.URLConnection#getIfModifiedSince()
     */
    public long getModifiedSince() {
        return modifiedSince;
    }

    /**
     * Get the read timeout configured for this instance.
     *
     * @return The read timeout configured for this instance, or {@code 0} if the default is used.
     * @see Builder#setReadTimeout(int)
     * @see java.net.URLConnection#getReadTimeout()
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Get the cache status configured for this instance.
     *
     * @return {@code true} if this instance uses caches, {@code false} if not.
     * @see Builder#setUseCaches(boolean)
     * @see java.net.URLConnection#getUseCaches()
     */
    public boolean isUseCaches() {
        return useCaches;
    }

    /**
     * Get the mapping of custom headers configured for this instance.
     *
     * @return The mapping of custom headers configured for this instance, or {@code null} if no
     *         custom headers have been set.
     * @see Builder#setCustomHeader(String, String)
     */
    @Nullable
    public Map<String, String> getCustomHeaders() {
        return customHeaders != null ? new HashMap<>(customHeaders) : null;
    }

    /**
     * Get a custom header configured for this instance.
     *
     * @param header The name of the custom header to get.
     * @param defaultValue The default value to return if the custom header does not exist.
     * @return The custom header for the given {@code header} name. If this is not found, then
     *         {@code defaultValue} will be returned. Be prepared for {@code null} if
     *         {@code defaultValue} is {@code null} or the custom header is {@code null}.
     */
    @Nullable
    public String getCustomHeader(@NonNull final String header,
                                  @Nullable final String defaultValue) {
        if (customHeaders != null) {
            return customHeaders.containsKey(header) ? customHeaders.get(header) : defaultValue;
        } else {
            return defaultValue;
        }
    }

    /*
     **************
     * Run state. *
     **************
     */

    /**
     * See {@link HttpURLConnection#getContentEncoding()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link HttpURLConnection#getContentEncoding()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see HttpURLConnection#getContentEncoding()
     */
    @Nullable
    public String getContentEncoding() {
        checkState();
        return connection.getContentEncoding();
    }

    /**
     * See {@link HttpURLConnection#getHeaderFieldDate(String, long)}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param field See {@link HttpURLConnection#getHeaderFieldDate(String, long)}.
     * @param defaultValue See {@link HttpURLConnection#getHeaderFieldDate(String, long)}.
     * @return See {@link HttpURLConnection#getHeaderFieldDate(String, long)}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see HttpURLConnection#getHeaderFieldDate(String, long)
     */
    public long getHeaderFieldDate(@NonNull final String field, final long defaultValue) {
        checkState();
        return connection.getHeaderFieldDate(field, defaultValue);
    }

    /**
     * See {@link HttpURLConnection#getResponseCode()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.HttpURLConnection#getResponseCode()}.
     * @throws IOException See {@link java.net.HttpURLConnection#getResponseCode()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see HttpURLConnection#getResponseCode()
     */
    public int getResponseCode() throws IOException {
        checkState();
        return connection.getResponseCode();
    }

    /**
     * See {@link java.net.URLConnection#getContentLength()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getContentLength()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getContentLength()
     */
    public int getContentLength() {
        checkState();
        return connection.getContentLength();
    }

    /**
     * See {@link java.net.URLConnection#getContentType()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getContentType()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getContentType()
     */
    @Nullable
    public String getContentType() {
        checkState();
        return connection.getContentType();
    }

    /**
     * See {@link java.net.URLConnection#getDate()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getDate()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getDate()
     */
    public long getDate() {
        checkState();
        return connection.getDate();
    }

    /**
     * See {@link java.net.URLConnection#getExpiration()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getExpiration()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getExpiration()
     */
    public long getExpiration() {
        checkState();
        return connection.getExpiration();
    }

    /**
     * See {@link java.net.URLConnection#getHeaderField(String)}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param key See {@link java.net.URLConnection#getHeaderField(String)}.
     * @return See {@link java.net.URLConnection#getHeaderField(String)}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getHeaderField(String)
     */
    @Nullable
    public String getHeaderField(@NonNull final String key) {
        checkState();
        return connection.getHeaderField(key);
    }

    /**
     * See {@link java.net.URLConnection#getHeaderField(int)}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param pos See {@link java.net.URLConnection#getHeaderField(int)}.
     * @return See {@link java.net.URLConnection#getHeaderField(int)}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getHeaderField(int)
     */
    @Nullable
    public String getHeaderField(final int pos) {
        checkState();
        return connection.getHeaderField(pos);
    }

    /**
     * See {@link java.net.URLConnection#getHeaderFieldInt(String, int)}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param field See {@link java.net.URLConnection#getHeaderFieldInt(String, int)}.
     * @param defaultValue See {@link java.net.URLConnection#getHeaderFieldInt(String, int)}.
     * @return See {@link java.net.URLConnection#getHeaderFieldInt(String, int)}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getHeaderFieldInt(String, int)
     */
    public int getHeaderFieldInt(@NonNull final String field, final int defaultValue) {
        checkState();
        return connection.getHeaderFieldInt(field, defaultValue);
    }

    /**
     * See {@link java.net.URLConnection#getHeaderFieldKey(int)}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param posn See {@link java.net.URLConnection#getHeaderFieldKey(int)}.
     * @return See {@link java.net.URLConnection#getHeaderFieldKey(int)}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getHeaderFieldKey(int)
     */
    @Nullable
    public String getHeaderFieldKey(final int posn) {
        checkState();
        return connection.getHeaderFieldKey(posn);
    }

    /**
     * See {@link java.net.URLConnection#getHeaderFields()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getHeaderFields()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getHeaderFields()
     */
    @Nullable
    public Map<String, List<String>> getHeaderFields() {
        checkState();
        return connection.getHeaderFields();
    }

    /**
     * See {@link java.net.URLConnection#getLastModified()}.
     *
     * <p>
     *     This should only be called after {@link #executeFetcher(FetcherStreamReader)}. If it is
     *     called before, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @return See {@link java.net.URLConnection#getLastModified()}.
     * @throws IllegalStateException When the instance has not been run yet.
     * @see java.net.URLConnection#getLastModified()
     */
    public long getLastModified() {
        checkState();
        return connection.getLastModified();
    }

    /**
     * Has this instance run yet? That is, has {@link #executeFetcher(FetcherStreamReader)} been
     * called?
     *
     * @return {@code true} if this instance has run, {@code false} if not.
     */
    public boolean hasRun() {
        return connection != null;
    }

    /**
     * Configure the supplied instance of {@link HttpURLConnection} with the fields set in the
     * {@link Builder}.
     *
     * @param connection The {@link HttpURLConnection} instance to configure.
     * @throws ProtocolException Where there is an error configuring the request method of this
     *                           instance.
     */
    private void configureConnectionInstance(@NonNull final HttpURLConnection connection)
            throws ProtocolException {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            connection.setFixedLengthStreamingMode(contentLength);
        } else {
            connection.setFixedLengthStreamingMode((int) Math.min(contentLength,
                    Integer.MAX_VALUE));
        }*/

        //connection.setChunkedStreamingMode(chunkLength);
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(connectTimeout);
        //connection.setDoInput(doInput);
        //connection.setDoOutput(doOutput);
        connection.setIfModifiedSince(modifiedSince);
        connection.setReadTimeout(readTimeout);
        connection.setUseCaches(useCaches);

        if (customHeaders != null) {
            for (String header : customHeaders.keySet()) {
                connection.setRequestProperty(header, customHeaders.get(header));
            }
        }
    }

    /**
     * Does the application that is calling us have the permission
     * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}?
     *
     * @return {@code true} if the application calling us requests the permission
     *         {@link android.Manifest.permission#ACCESS_NETWORK_STATE}, {@code false} if not.
     */
    private boolean hasAccessNetworkStatePermission() {
        return context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Is the device currently connected to any network?
     *
     * @return {@code true} if the device has a network connection or the calling application has
     *         not requested the permission
     *         {@link android.Manifest.permission#ACCESS_NETWORK_STATE}, {@code false} if it is
     *         determined the device has no active connection.
     */
    private boolean isConnected() {
        if (hasAccessNetworkStatePermission()) {
            final NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } else {
            // If we don't have permission to check our network connectivity, assume we have a
            // connection.
            return true;
        }
    }

    /**
     * This is called by certain getter methods to ensure that this instance has been run before the
     * getter returns its data. This is because many values are not available until the instance has
     * run.
     *
     * <p>
     *     Use {@link #hasRun()} to check if the instance has been run before calling the getter
     *     method.
     * </p>
     *
     * @throws IllegalStateException When the instance has not been run yet.
     * @see #hasRun()
     */
    private void checkState() {
        if (!hasRun()) {
            throw new IllegalStateException("executeFetcher() must be called before calling this " +
                    "method.");
        }
    }

    /**
     * This class is used to construct a new {@link HttpFetcher}. The URL is the only required
     * field, all other fields will use defaults. When building is done, call {@link #build()} to
     * get an instance of {@link HttpFetcher}.
     */
    public static class Builder {

        private final Context context;
        private String url;
        private Proxy proxy;
        private boolean allowHostRedirects = true;
        //private int chunkLength = -1;
        //private long contentLength = -1;
        private boolean followRedirects = true;
        private String requestMethod = "GET";
        private int connectTimeout = 0;
        //private boolean doInput = true;
        //private boolean doOutput = false;
        private long modifiedSince = 0;
        private int readTimeout = 0;
        private boolean useCaches = true;
        private HashMap<String, String> customHeaders;

        /**
         * Create a new {@code Builder} instance which can later be used to construct a
         * {@link HttpFetcher}.
         *
         * @param context A non-null {@link Context} instance.
         */
        public Builder(@NonNull final Context context) {
            this.context = context;
        }

        /**
         * Set the URL to request from the server. This is required - the fetcher won't work without
         * this.
         *
         * @param url The URL to request from this server.
         * @return A reference to this {@code Builder} for method chaining.
         * @see #build()
         */
        @NonNull
        public Builder setUrl(@NonNull final String url) {
            this.url = url;
            return this;
        }

        /**
         * Set a proxy to use for the connection. If this is set as non-{@code null}, then
         * {@link URL#openConnection(java.net.Proxy)} will be used for the connection. Otherwise,
         * {@link java.net.URL#openConnection()} will be called using system default settings.
         *
         * <p>
         *     By default, the proxy is set as {@code null}.
         * </p>
         *
         * @param proxy The proxy to use, or {@code null} to use system default settings.
         * @return A reference to this {@code Builder} for method chaining.
         * @see #build()
         */
        @NonNull
        public Builder setProxy(@NonNull final Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Should the fetcher allow redirects between hosts? This is used to detect if a Wi-Fi sign
         * in page has been encountered, in which case a {@link UrlMismatchException} is thrown.
         *
         * <p>
         *     If {@link #setFollowRedirects(boolean)} is set as {@code false}, then this method has
         *     no effect. It may be perfectly acceptable to allow redirects between hosts, in which
         *     case this method should be set to {@code true}. Similarly, it may not be acceptable
         *     to redirect between hosts but it is acceptable to redirect between paths on the same
         *     host. In which case, it's safe to set this method as {@code false} and
         *     {@link #setFollowRedirects(boolean)} as {@code true} - this will catch Wi-Fi sign on
         *     redirects.
         * </p>
         *
         * <p>
         *     By default, this method is set as {@code true}.
         * </p>
         *
         * @param allowHostRedirects {@code true} if host redirects are allowed, {@code false} if
         *                           not.
         * @return A reference to this {@code Builder} for method chaining.
         * @see #build()
         */
        @NonNull
        public Builder setAllowHostRedirects(final boolean allowHostRedirects) {
            this.allowHostRedirects = allowHostRedirects;
            return this;
        }

        /* TODO: enable these methods when posting has been implemented.
        **
         * See {@link HttpURLConnection#setChunkedStreamingMode(int)}.
         *
         * @param chunkLength See {@link HttpURLConnection#setChunkedStreamingMode(int)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see HttpURLConnection#setChunkedStreamingMode(int)
         * @see #build()
         *
        @NonNull
        public Builder setChunkedStreamingMode(final int chunkLength) {
            this.chunkLength = chunkLength;
            return this;
        }

        **
         * See {@link HttpURLConnection#setFixedLengthStreamingMode(long)} and
         * {@link HttpURLConnection#setFixedLengthStreamingMode(int)}.
         *
         * @param contentLength See {@link HttpURLConnection#setFixedLengthStreamingMode(long)} and
         *                      {@link HttpURLConnection#setFixedLengthStreamingMode(int)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see HttpURLConnection#setFixedLengthStreamingMode(long)
         * @see HttpURLConnection#setFixedLengthStreamingMode(int)
         * @see #build()
         *
        @NonNull
        public Builder setFixedLengthStreamingMode(final long contentLength) {
            this.contentLength = contentLength;
            return this;
        }*/

        /**
         * See {@link HttpURLConnection#setInstanceFollowRedirects(boolean)}.
         *
         * @param followRedirects See {@link HttpURLConnection#setInstanceFollowRedirects(boolean)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see HttpURLConnection#setInstanceFollowRedirects(boolean)
         * @see #build()
         */
        @NonNull
        public Builder setFollowRedirects(final boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * See {@link HttpURLConnection#setRequestMethod(String)}.
         *
         * @param requestMethod See {@link HttpURLConnection#setRequestMethod(String)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see HttpURLConnection#setRequestMethod(String)
         * @see #build()
         */
        @NonNull
        public Builder setRequestMethod(@Nullable final String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        /**
         * See {@link java.net.URLConnection#setConnectTimeout(int)}.
         *
         * @param timeoutMillis See {@link java.net.URLConnection#setConnectTimeout(int)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setConnectTimeout(int)
         * @see #build()
         */
        @NonNull
        public Builder setConnectTimeout(final int timeoutMillis) {
            connectTimeout = timeoutMillis;
            return this;
        }

        /* TODO: enable these methods when posting has been implemented.
        **
         * See {@link java.net.URLConnection#setDoInput(boolean)}.
         *
         * @param doInput See {@link java.net.URLConnection#setDoInput(boolean)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setDoInput(boolean)
         * @see #build()
         *
        @NonNull
        public Builder setDoInput(final boolean doInput) {
            this.doInput = doInput;
            return this;
        }

        /**
         * See {@link java.net.URLConnection#setDoOutput(boolean)}.
         *
         * @param doOutput See {@link java.net.URLConnection#setDoOutput(boolean)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setDoOutput(boolean)
         * @see #build()
         *
        @NonNull
        public Builder setDoOutput(final boolean doOutput) {
            this.doOutput = doOutput;
            return this;
        }*/

        /**
         * See {@link java.net.URLConnection#setIfModifiedSince(long)}.
         *
         * @param modifiedSince See {@link java.net.URLConnection#setIfModifiedSince(long)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setIfModifiedSince(long)
         * @see #build()
         */
        @NonNull
        public Builder setIfModifiedSince(final long modifiedSince) {
            this.modifiedSince = modifiedSince;
            return this;
        }

        /**
         * See {@link java.net.URLConnection#setReadTimeout(int)}.
         *
         * @param timeoutMillis See {@link java.net.URLConnection#setReadTimeout(int)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setReadTimeout(int)
         * @see #build()
         */
        @NonNull
        public Builder setReadTimeout(final int timeoutMillis) {
            this.readTimeout = timeoutMillis;
            return this;
        }

        /**
         * See {@link java.net.URLConnection#setUseCaches(boolean)}.
         *
         * @param useCaches See {@link java.net.URLConnection#setUseCaches(boolean)}.
         * @return A reference to this {@code Builder} for method chaining.
         * @see java.net.URLConnection#setUseCaches(boolean)
         * @see #build()
         */
        @NonNull
        public Builder setUseCaches(final boolean useCaches) {
            this.useCaches = useCaches;
            return this;
        }

        /**
         * Set a custom header to use in the request. This method may be called many times to
         * provide new mappings.
         *
         * <p>
         *     Calls to here are fed in to a {@link HashMap}. {@code header} is the key, so if
         *     multiple calls are made for the same key, only the last call will take effect.
         * </p>
         *
         * @param header The header to provide data for.
         * @param value  The value to map for the header.
         * @return A reference to this {@code Builder} for method chaining.
         * @see #build()
         */
        @NonNull
        public Builder setCustomHeader(@NonNull final String header, @NonNull final String value) {
            if (customHeaders == null) {
                customHeaders = new HashMap<>();
            }

            customHeaders.put(header, value);
            return this;
        }

        /**
         * Create a new instance of {@link HttpFetcher} based on the snapshot of data in this
         * {@code Builder} at the instance this method is called. After this method is called, the
         * data inside the newly created {@link HttpFetcher} won't be changed if any of the
         * {@code Builder}'s setter methods are called. Of course, data may be changed inside this
         * class and this method may be called again to build another new instance of
         * {@link HttpFetcher}.
         *
         * <p>
         *     At a minimum, {@link #setUrl(String)} needs to be called before calling this method.
         *     If a URL is not set, or it is {@code null} or empty, then an
         *     {@link IllegalArgumentException} will be thrown.
         * </p>
         *
         * @return A new instance of {@link HttpFetcher} based on the data inside this
         *         {@code Builder} at the instant this method was called.
         * @throws IllegalArgumentException When the  URL is {@code null} or empty.
         */
        @NonNull
        public HttpFetcher build() {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("The url must not be null or empty. Have you " +
                        "called setUrl()?");
            }

            return new HttpFetcher(this);
        }
    }
}