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

package uk.org.rivernile.android.fetchutils.loaders;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.annotation.NonNull;

/**
 * This class defines an easy to use {@link Loader}, based specifically on the Android framework's
 * {@link AsyncTaskLoader}. Some of the logic that is similar to {@link CursorLoader} has been
 * pulled out in to this class so that the hard work is done - a subclass merely only needs to
 * implement the {@link #loadInBackground()} method and provide a constructor (although can further
 * customise the behaviour of the {@link Loader} by overriding its callback methods).
 *
 * <p>
 *     To use this class, you need to be familiar with {@link Loader}s, how and when to use them and
 *     their caveats. There is a guide on the Android Developer website:
 *     <a href="http://developer.android.com/guide/components/loaders.html">here</a>.
 * </p>
 *
 * <p>
 *     This implementation can only be used on API level 11 (Honeycomb) and above. To use a
 *     backwards compatible version, based on the compatibility library, use
 *     {@link uk.org.rivernile.android.fetchutils.loaders.support.SimpleAsyncTaskLoader} instead.
 *     You may also wish to use the compatible version if you're using other v4 compatibility
 *     library components. Also, the compatible version has the advantage of bugs being fixed that
 *     may exist on older platforms, and future API updates.
 * </p>
 *
 * <p>
 *     Here is a short code snippet showing how to extend this class;
 * </p>
 *
 * <pre>
 * <code>
 * import uk.org.rivernile.android.fetchutils.loaders.Result;
 * import uk.org.rivernile.android.fetchutils.loaders.SimpleAsyncTaskLoader;
 *
 * public class MyLoader extends SimpleAsyncTaskLoader&lt;Result&lt;String, Exception>> {
 *
 *     public MyLoader(Context context) {
 *         // Super needs to be called with a valid Context object.
 *         super(context);
 *     }
 *
 *     {@literal @}Override
 *     public Result<String, Exception> loadInBackground() {
 *         Result<String, Exception> result;
 *
 *         try {
 *             // Attempt to get the data.
 *             String str = myModel.someBlockingTask();
 *             // If successful, create a success Result object.
 *             result = new Result(str);
 *         } catch (SomeException e) {
 *             // If it failed, create a failure Result object.
 *             result = new Result(e);
 *         }
 *
 *         // Return the result. The LoaderManager will take care of delivering this back to the
 *         // correct component on the UI thread.
 *         return result;
 *     }
 * }
 * </code>
 * </pre>
 *
 * @author Niall Scott
 * @param <D> The type of data that will be returned from this {@link Loader}. For a type that
 *           encapsulates a success or failure result, see {@link Result}.
 * @see android.content.Loader
 * @see android.app.LoaderManager
 * @see android.content.AsyncTaskLoader
 * @see uk.org.rivernile.android.fetchutils.loaders.Result
 */
@TargetApi(11)
public abstract class SimpleAsyncTaskLoader<D> extends AsyncTaskLoader<D> {
    
    private D result;
    
    /**
     * Create a new {@code SimpleAsyncTaskLoader}. This must be called through to as
     * {@code super(Context)} by subclasses.
     * 
     * @param context A {@link Context} object.
     */
    public SimpleAsyncTaskLoader(@NonNull final Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            // If a result already exists, deliver it.
            deliverResult(result);
        }

        if (takeContentChanged() || result == null) {
            // If a result does not exist or there's a change in the data, force a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped.
        onStopLoading();
        
        // Reset to defaults.
        result = null;
    }

    @Override
    public void deliverResult(final D resultIn) {
        // If the loader has been reset, do not deliver a result.
        if (isReset()) {
            return;
        }
        
        result = resultIn;

        // Deliver the result only if the loader is in the started state.
        if (isStarted()) {
            super.deliverResult(resultIn);
        }
    }

    @Override
    public abstract D loadInBackground();
}