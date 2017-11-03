/*
 * Copyright (c) 2017 Ian Buttimer.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.bakingguru.data;


import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_ARG;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_EXTRAS;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_METHOD;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_PROJECTION;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_SELECTION;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_SELECTION_ARGS;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_SORT_ORDER;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_URI;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_VALUES;

/**
 * Builder class for AsyncCallback Bundle objects
 */
@SuppressWarnings("unused")
public class AsyncCallbackBundle {

    public static class Builder {

        private Bundle bundle;

        /**
         * Constructor
         */
        Builder() {
            bundle = new Bundle();
        }

        /**
         * Build a AsyncCallback Bundle object
         * @return Bundle object
         */
        public Bundle build() {
            return new Bundle(bundle);
        }

        /**
         * Add a Uri argument
         * @param uri    Uri to add
         * @return  Builder to facilitate chaining
         */
        public Builder putUri(@NonNull Uri uri) {
            bundle.putParcelable(CONTENT_PROVIDER_URI, uri);
            return this;
        }

        /**
         * Add a ContentValues argument
         * @param contentValues ContentValues to add
         * @return  Builder to facilitate chaining
         */
        public Builder putContentValues(@Nullable ContentValues contentValues) {
            if (contentValues != null) {
                bundle.putParcelable(CONTENT_PROVIDER_VALUES, contentValues);
            }
            return this;
        }

        /**
         * Add a ContentValues array argument
         * @param contentValues ContentValues array to add
         * @return  Builder to facilitate chaining
         */
        public Builder putContentValues(@Nullable ContentValues[] contentValues) {
            if (contentValues != null) {
                bundle.putParcelableArray(CONTENT_PROVIDER_VALUES, contentValues);
            }
            return this;
        }

        /**
         * Add a string using the specified key
         * @param key   Key to use
         * @param value Value to add
         * @return  Builder to facilitate chaining
         */
        private Builder putString(@NonNull String key, @Nullable String value) {
            if (value != null) {
                bundle.putString(key, value);
            }
            return this;
        }

        /**
         * Add a sort order argument
         * @param sortOrder Sort order to add
         * @return  Builder to facilitate chaining
         */
        public Builder putSortOrder(@Nullable String sortOrder) {
            return putString(CONTENT_PROVIDER_SORT_ORDER, sortOrder);
        }

        /**
         * Add a selection argument
         * @param selection Selection to add
         * @return  Builder to facilitate chaining
         */
        public Builder putSelection(@Nullable String selection) {
            return putString(CONTENT_PROVIDER_SELECTION, selection);
        }

        /**
         * Add a string array argument
         * @param key   Key to use
         * @param array Array to add
         * @return  Builder to facilitate chaining
         */
        private Builder putStringArray(@NonNull String key, @Nullable String[] array) {
            if (array != null) {
                bundle.putStringArray(key, array);
            }
            return this;
        }

        /**
         * Add a selection args argument
         * @param selectionArgs Selection args to add
         * @return  Builder to facilitate chaining
         */
        public Builder putSelectionArgs(@Nullable String[] selectionArgs) {
            return putStringArray(CONTENT_PROVIDER_SELECTION_ARGS, selectionArgs);
        }

        /**
         * Add a projection argument
         * @param projection    Projection to add
         * @return  Builder to facilitate chaining
         */
        public Builder putProjection(@Nullable String[] projection) {
            return putStringArray(CONTENT_PROVIDER_PROJECTION, projection);
        }

        /**
         * Add a methos argument
         * @param method    Methos to add
         * @return  Builder to facilitate chaining
         */
        public Builder putMethod(@NonNull String method) {
            return putString(CONTENT_PROVIDER_METHOD, method);
        }

        /***
         * Add an arg argument
         * @param arg   Arg ro add
         * @return  Builder to facilitate chaining
         */
        public Builder putArg(@Nullable String arg) {
            return putString(CONTENT_PROVIDER_ARG, arg);
        }

        /**
         * Add an extras argument
         * @param extras    Extras to add
         * @return  Builder to facilitate chaining
         */
        public Builder putExtras(@Nullable Bundle extras) {
            if (extras != null) {
                bundle.putBundle(CONTENT_PROVIDER_EXTRAS, extras);
            }
            return this;
        }

        /**
         * Clear the builder contents
         * @return  Builder to facilitate chaining
         */
        public Builder clear() {
            bundle.clear();
            return this;
        }
    }

    /**
     * Get a builder instance
     * @return  New builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

}
