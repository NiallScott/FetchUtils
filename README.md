[![Build Status](https://travis-ci.org/NiallScott/FetchUtils.svg)](https://travis-ci.org/NiallScott/FetchUtils)

FetchUtils
==========

*A lightweight library written for Android to make common data loading tasks easy and simple.*

Purpose
-------

The library started out as a refactor, in to reusable components, of data fetching code in the
[My Bus Edinburgh](http://github.com/NiallScott/MyBusEdinburgh) project.

The problem was that HTTP fetching and file reading code was re-implemented wherever it was used,
which of course is bad programming practice. The code was tightly coupled in to its implementation
and was not unit tested.

Work was undertaken to refactor this code out in to re-usable components, and for the components to
be unit tested as much as possible. This was done, but the code still lived within the project. This
project is the extraction of this code in to its own library which will be maintained independently
and can easily be imported in to future projects.

Forking and pull requests are welcome.

It is acknowledged that there are better developed libraries out there for doing this sort of thing.
This library does not attempt to compete with these libraries. This libraries merely attempts to do
a small amount of things well, reliably and is easy to use with only basic knowledge required for
most things.

Licence
-------

This project uses the Apache 2.0 licence, as defined below.

    Copyright (C) 2014 - 2015 Niall Scott
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Author
------

This library is created by [Niall Scott](http://github.com/NiallScott).
[Twitter](http://twitter.com/NiallScott)

In forks, credit to original source is appreciated.

Concepts
--------

### Fetcher - Reader

A **fetcher** describes the mechanism for retrieving data. For example, this might mean connecting
to a remote server or opening a file.

A **reader** describes the process that is performed on the data once it reaches the device. For
example, the data may be interpreted as plain text, a bitmap image, JSON or even piped straight out
to something else (such as downloading a file over HTTP and piped straight out to a file on the
device). The reader retains the data or a reference to it throughout its lifecycle.

The idea is that fetchers and readers can be used in any combination with each other. Both conform
to their respective interfaces and a reader is passed in to a fetcher. When the fetcher receives
data, it passes this data in to a reader. Generally, fetchers and readers are immutable - new
instances should be created for new fetches.

In code, a **fetcher** is defined as;

```java
public interface Fetcher {

    public void executeFetcher(FetcherStreamReader reader) throws IOException;
}
```

...and a **reader** is defined as;

```java
public interface FetcherStreamReader {

    public void readInputStream(InputStream stream) throws IOException;
}
```

When `executeFetcher(FetcherStreamReader reader)` is called on the Fetcher, it will deal with the
set up of the stream. When the stream is ready, `readInputStream(InputStream stream)` will be called
on the reader and will block until the reader no longer requires the data stream. Once this
unblocks, the Fetcher will deal with tearing down the stream.

Caveat: the whole process is blocking. There is no attempt at dealing with threading in this part of
the library. Android already has good ways at dealing with threading, such as using
[Loaders](http://developer.android.com/guide/components/loaders.html) in UI code, and
[AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) in non-UI code. You
may wish to use `SimpleAsyncTaskLoader` included in this library to perform asynchronous operations
from the UI.

### Loaders

Read the Android documentation about Loaders
[here](http://developer.android.com/guide/components/loaders.html). Understanding this is critical
before using the Loader helper classes in this library.

Loaders are an advanced Android topic. They were introduced in Android 3.0 (Honeycomb) to get around
the problem of asynchronously loading data in to a user interface that can be destroyed and
recreated. This is because of Android's UI lifecycle - Activities can be destroyed and recreated due
to system configuration changes. For example, this may happen when the device is rotated or the
system language is changed. This is default behaviour and can be overridden - but this is
[discouraged by the engineers at Google](http://developer.android.com/guide/topics/manifest/activity-element.html#config).

When the user interface is destroyed and recreated, the reference to the loaded data is lost in the
process as new object instances are used. It could be possible to get the data again by initiating
another load (typically AsyncTasks were used), persisting the data on disk (SQLite database, files)
or some convoluted and complicated solution to keep it in memory during rotation.

Loaders makes this much easier for us. It deals with the persistence of the data in memory during
the re-creation of the user interface. Once the user interface is ready again, it simply connects
back to the Loader and it can get the previously loaded data.

There are other benefits to Loaders and documenting them extensively here is beyond the scope of
this document.

Loaders are also available in the v4 compatibility library, making their functionality available
from Android 1.6 (Donut).

**Why are Loaders mentioned here?**

Good question.

Many of Google's examples of usage of Loaders depends on using a
[CursorLoader](http://developer.android.com/reference/android/content/CursorLoader.html). What if
you want to load data from another source, such as a JSON document over the network, and not persist
it in a database? Then you may use an
[AsyncTaskLoader](http://developer.android.com/reference/android/content/AsyncTaskLoader.html)
(`CursorLoader` actually extends from `AsyncTaskLoader`). The default
[Loader](http://developer.android.com/reference/android/content/Loader.html) class does not provide
any background threading at all - this is provided by `AsyncTaskLoader`, which, as you might have
guessed, is backed by an
[AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html).

But there's a problem with `AsyncTaskLoader`. It does not deliver its data as expected with its
lifecycle - and is poorly documented. Provided by this library is an extended `AsyncTaskLoader` that
does deliver the data correctly by doing what `CursorLoader` does (albeit without the `Cursor`
stuff).

Please see the documentation for `SimpleAyncTaskLoader` to see how it works and for a code example.
Two versions of the class has been provided: one that extends the platform's `AsyncTaskLoader` (can
be used on API 11+) and one that extends `AsyncTaskLoader` from the compatibility library (can be
used on API 4+). It goes without saying that the compatibility library needs to be included before
the compatibility `SimpleAsyncTaskLoader` can be used.

Usage
-----

This project only supports the Gradle build system from Android Studio version 1.0 (and possibly
any newer versions).

At the moment, to include this library in your project, you will need to check out the source tree
as a sub directory inside your existing Android project and add a reference to the project inside
your `settings.gradle` and `build.gradle` for the module that is using it. In the near future, the
project will be made available in Maven for easy including within a project.

For example, suppose your project is structured as so (irrelevant directories and files not shown)
and you want to add FetchUtils as a dependency of the `app` module;

```
YourProject/
    ` app/
        ` build.gradle
    ` FetchUtils
        ` library/
    ` settings.gradle
```

Then you would do the following;

- Add `':FetchUtils:library'` to the `include` line of `YourProject/settings.gradle`
- Add `'compile project(':FetchUtils:library')` under the `dependencies` block inside
  `YourProject/app/build.gradle`
- Synchronise the project with the Gradle files

### Fetcher - Reader

Select a **fetcher** class;

- `AssetFileFetcher` - used to fetch the contents of an Android asset file
- `FileFetcher` - used to fetch the contents of a
  [File](http://developer.android.com/reference/java/io/File.html) on disk
- `HttpFetcher` - used to fetch the contents of data at a given URL from a HTTP server
- Or create your own `Fetcher` by creating a class and implementing `Fetcher`

...then select a **reader** class;

- `BitmapFetcherStreamReader` - used to read a stream of data when it describes a bitmap image
- `FileWriterStreamReader` - used to read a stream of data and instantly output it to a file on disk
- `JSONFetcherStreamReader` - used to read a stream of data and create a
  [JSONArray](http://developer.android.com/reference/org/json/JSONArray.html) or a
  [JSONObject](http://developer.android.com/reference/org/json/JSONObject.html) out of it
- `StringFetcherStreamReader` - used to read a stream of data and hold the data as a
  [String](http://developer.android.com/reference/java/lang/String.html) in memory
- Or create your own `FetcherStreamReader` by creating a class and implementing
  `FetcherStreamReader`

Here is a code sample to open a file within the application's sandbox and load the entire contents
of the file in to memory as a `String`;

```java
// Create a File object pointing to the file on disk to read from. someContext is an instance of
// Android's Context class.
final File fileToOpen = new File(someContext.getFilesDir(), "yourfile.txt");
// Create an instance of the FileFetcher to read from a file on disk.
final FileFetcher fetcher = new FileFetcher(fileToOpen);
// Create an instance of StringFetcherStreamReader to handle the data that is read in.
final StringFetcherStreamReader reader = new StringFetcherStreamReader();

// Fetcher.executeFetcher() mandates that IOException should be caught.
try {
    fetcher.executeFetcher(reader); // This blocks while executing. Don't run on UI thread.
    System.out.println(reader.getData()); // Access the data from reader.getData()
} catch (IOException e) {
    // Catch errors here. You could also catch errors such as FileNotFoundException here if you
    // wanted to.
}
```

Here is a code sample to copy a file from Android assets and output it straight to a file in the
application's sandbox;

```java
// The name of the file to read from the assets directory.
final String fileName = "assetfile.txt";
// Create a File object pointing to the file on disk that the data will be written to.
final File fileToWrite = new File(someContext.getFilesDir(), "writtenfile.txt");
// Create an instance of the AssetFileFetcher  to read from a file in the application assets.
final AssetFileFetcher fetcher = new AssetFileFetcher(someContext, fileName);
// Create an instance of FileWriterStreamReader to output the data to the application sandbox.
final FileWriterStreamReader reader = new FileWriterStreamReader(fileToWrite, false);

try {
    fetcher.executeFetcher(reader); // Remember, this blocks.

    // File is now copied from assets to application sandbox.
} catch (IOException e) {
    // Catch errors here.
}
```

Again, the **fetchers** and **readers** can be used in any combination that makes sense, so they are
flexible.

### SimpleAsyncTaskLoader

This class exists in two forms: one which extends from the platform's `AsyncTaskLoader` (API level
11+) and one which extends from the v4 compatibility library `AsyncTaskLoader`. Be sure to use the
correct one for your needs when importing the class.

```java
import uk.org.rivernile.android.fetchutils.loaders.SimpleAsyncTaskLoader;
```

**or**

```java
import uk.org.rivernile.android.fetchutils.loaders.support.SimpleAsyncTaskLoader;
```

`SimpleAsyncTaskLoader` is an abstract class - it's up to you to provide the implementation for what
you want to load asynchronously. It may be a call to the network or to read from a file. **Tip:**
you may want to use the Fetcher-Reader mechanism in your implementation.

`Result` is a class used to encapsulate the result of a load. Loaders can only return one object to
their caller upon completion, so a `Result` object is used to hold a success object and a failure
object. It's up to you what data is returned from the `Loader` - `Result` merely provides an easy
way to return a success object and a failure object. In this object, the failure state can only
accept an object that is of the type `Exception` or its descendants. If this is not suitable, then
you may create your own class that can be returned by the `Loader`.

Here is a code example of a `SimpleAsyncTaskLoader` returning a `String`;

```java
public class HttpAsyncTaskLoader extends AsyncTaskLoader<String> {

    // Context is required.
    private final Context context;
    // The URL to connect to.
    private final String url;

    public HttpAsyncTaskLoader(final Context context, final String url) {
        // Super needs to be called with a valid Context object.
        super(context);

        // Arguments are sent in to the constructor.
        this.context = context;
        this.url = url;
    }

    @Override
    public String loadInBackground() {
        // The Fetcher using the URL passed in to the constructor.
        final HttpFetcher.Builder builder = new HttpFetcher.Builder(context);
        builder.setUrl(url)
                .setAllowHostRedirects(false);
        final HttpFetcher fetcher = builder.build();
        final StringFetcherStreamReader reader = new StringFetcherStreamReader();

        try {
            fetcher.executeFetcher(reader);
            // If fetching was successful, return the data in the reader.
            return reader.getData();
        } catch (IOException e) {
            // If it failed, return null.
            return null;
        }
    }
}
```

Here is a code example, similar to above, that uses a `Result` instead;

```java
public class HttpResultAsyncTaskLoader extends AsyncTaskLoader<Result<String, IOException>> {

    // Context is required.
    private final Context context;
    // The URL to connect to.
    private final String url;

    public HttpResultAsyncTaskLoader(final Context context, final String url) {
        // Super needs to be called with a valid Context object.
        super(context);

        // Arguments are sent in to the constructor.
        this.context = context;
        this.url = url;
    }

    @Override
    public Result<String, IOException> loadInBackground() {
        // The Fetcher using the URL passed in to the constructor.
        final HttpFetcher.Builder builder = new HttpFetcher.Builder(context);
        builder.setUrl(url)
                .setAllowHostRedirects(false);
        final HttpFetcher fetcher = builder.build();
        final StringFetcherStreamReader reader = new StringFetcherStreamReader();

        try {
            fetcher.executeFetcher(reader);
            // If fetching was successful, return the data in the reader via the Result object.
            return new Result<String, IOException>(reader.getData());
        } catch (IOException e) {
            // If it failed, pass the Exception in to the Result object.
            return new Result<String, IOException>(e);
        }
    }
}
```

...and where you handle your `Loader` callbacks, most probably in your `Activity` or `Fragment`;

```java
...
public void onLoadFinished(Loader<Result<String, IOException>> loader,
                           Result<String, IOException> data) {
    if (data.isError()) {
        doSuccessThing(data.getSuccess());
    } else {
        doErrorThing(data.getError());
    }
}
...
```

To do
-----

- Distribute the library via Maven as an AAR (Android Archive) for easily including in projects
- Generate Javadoc
- Add ability to make `HttpFetcher` also accept data to be posted up to the server
- Improve unit tests
- Add an example project

Versions
--------

### 1.1 (in development)

- More comprehensive API for `HttpFetcher` to match
  [HttpURLConnection](https://developer.android.com/reference/java/net/HttpURLConnection.html) as
  much as possible
- If the calling application has the permission `ACCESS_NETWORK_STATE`, then `HttpFetcher` will
  check connectivity with
  [ConnectivityManager](https://developer.android.com/reference/android/net/ConnectivityManager.html)
  prior to attempting the connection
- Target latest Android version (API level 22)
- Update to latest Android Gradle version and latest build tools


### 1.0.1 (development - never released)

- Added `SimpleAsyncTaskLoader`
- Added Javadoc generation task to `build.gradle`
- Added integration with Travis CI server
- Added Maven Central push support

### 1.0

Initial release.