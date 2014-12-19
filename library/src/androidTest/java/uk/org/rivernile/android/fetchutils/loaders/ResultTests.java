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

import junit.framework.TestCase;

/**
 * Tests for {@link Result}.
 */
public class ResultTests extends TestCase {

    /**
     * Test that the success constructor can accept {@code null} data and the other object methods
     * return correct values when the object is in the success state.
     */
    public void testSuccessConstructorWithNullObject() {
        final Result<String, IllegalArgumentException> result =
                new Result<String, IllegalArgumentException>((String) null);
        assertFalse(result.isError());
        assertNull(result.getSuccess());
        assertNull(result.getError());
    }

    /**
     * Test that the success constructor holds the success object, that the same object is returned
     * from the getter, and the other object methods return correct values when the object is in the
     * success state.
     */
    public void testSuccessConstructorWithNonNullObject() {
        final String successData = "This is success data.";
        final Result<String, IllegalArgumentException> result =
                new Result<String, IllegalArgumentException>(successData);
        assertFalse(result.isError());
        assertSame(successData, result.getSuccess());
        assertNull(result.getError());
    }

    /**
     * Test that the failure constructor holds the failure object, that the same object is returned
     * from the getter, and the other object methods return correct values when the object is in the
     * failure state.
     */
    public void testFailureConstructor() {
        final IllegalArgumentException ex = new IllegalArgumentException();
        final Result<String, IllegalArgumentException> result =
                new Result<String, IllegalArgumentException>(ex);
        assertTrue(result.isError());
        assertNull(result.getSuccess());
        assertSame(ex, result.getError());
    }
}
