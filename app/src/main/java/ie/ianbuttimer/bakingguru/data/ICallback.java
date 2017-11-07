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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import java.net.URL;

import ie.ianbuttimer.bakingguru.utils.ErrorTuple;

import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.BULK_INSERT_HANDLER;
import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.DELETE_HANDLER;
import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.INSERT_HANDLER;
import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.QUERY_HANDLER;
import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.UPDATE_HANDLER;
import static ie.ianbuttimer.bakingguru.data.ICallback.ResponseHandler.CALL_HANDLER;

/**
 * Interface for handling content provider responses.<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 */
@SuppressWarnings("unused")
public interface ICallback {

    /** Sub id of loader used for content provider call method */
    int CONTENT_PROVIDER_CALL_LOADER = 1;
    /** Sub id of loader used for content provider insert method */
    int CONTENT_PROVIDER_INSERT_LOADER = 2;
    /** Sub id of loader used for content provider query method */
    int CONTENT_PROVIDER_QUERY_LOADER = 3;
    /** Sub id of loader used for content provider update method */
    int CONTENT_PROVIDER_UPDATE_LOADER = 4;
    /** Sub id of loader used for content provider delete method */
    int CONTENT_PROVIDER_DELETE_LOADER = 5;
    /** Sub id of loader used for content provider bulk insert method */
    int CONTENT_PROVIDER_BULK_INSERT_LOADER = 6;
    /** Factor to multiply client loader ids by to generate content provider loader ids */
    int LOADER_FACTOR = 100;

    /** Uri argument name for call & crud loaders */
    String CONTENT_PROVIDER_URI = "uri";
    /** Method argument name for call loader */
    String CONTENT_PROVIDER_METHOD = "method";
    /** Arg argument name for call loader */
    String CONTENT_PROVIDER_ARG = "arg";
    /** Extras argument name for call loader */
    String CONTENT_PROVIDER_EXTRAS = "extras";

    /** Content values argument name for crud loader */
    String CONTENT_PROVIDER_VALUES = "values";
    /** Content values array argument name for crud loader */
    String CONTENT_PROVIDER_VALUES_ARRAY = "values_array";
    /** Selection argument name for crud loader */
    String CONTENT_PROVIDER_SELECTION = "selection";
    /** Selection args argument name for crud loader */
    String CONTENT_PROVIDER_SELECTION_ARGS = "selection_args";
    /** Projection argument name for crud loader */
    String CONTENT_PROVIDER_PROJECTION = "projection";
    /** Sort order argument name for crud loader */
    String CONTENT_PROVIDER_SORT_ORDER = "sort_order";

    /** Type of result returned */
    String CONTENT_PROVIDER_RESULT_TYPE = "result_type";
    /** Error result returned */
    String CONTENT_PROVIDER_ERROR = "result_error";



    enum ResponseHandler { INSERT_HANDLER, QUERY_HANDLER, UPDATE_HANDLER, DELETE_HANDLER,
        BULK_INSERT_HANDLER, CALL_HANDLER
    }

    /*
    ///////// The methods below are for Uri requests //////////
    */

    /**
     * Insert a new entry using a content provider
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param contentValues A set of column_name/value pairs to add to the database.
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#insert(android.net.Uri, android.content.ContentValues)">ContentProvider.insert(Uri, ContentValues)</a>
     */
    void insert(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues);

    /**
     * Query rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param projection    The list of columns to put into the cursor
     * @param selection     A selection criteria to apply when filtering rows
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @param sortOrder     How the rows in the cursor should be sorted
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)">ContentProvider.query(Uri, String[], String, String[], String)</a>
     */
    void query(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder);

    /**
     * Update one or more rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param contentValues A set of column_name/value pairs to update in the database
     * @param selection     An optional filter to match rows to update
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])">ContentProvider.update(Uri, ContentValues, String, String[])</a>
     */
    void update(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs);

    /**
     * Delete one or more rows.
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param selection     An optional filter to match rows to update
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#delete(android.net.Uri, java.lang.String, java.lang.String[])">ContentProvider.delete(Uri, String, String[])</a>
     */
    void delete(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs);

    /**
     * Do a bulk insert
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param contentValues A set of column_name/value pairs to add to the database.
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#bulkInsert(android.net.Uri, android.content.ContentValues[])">ContentProvider.bulkInsert(Uri, ContentValues[])</a>
     */
    void bulkInsert (@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues[] contentValues);

    /**
     * Asynchronously call the specified method for the specified Uri
     * @param activity  Current activity
     * @param loaderId  Id of loader
     * @param uri       Uri to send
     * @param method    Provider-defined method to call
     * @param arg       Provider-defined String argument
     * @param extras    Provider-defined Bundle argument
     */
    void call(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras);

    /**
     * Process the response from an insert request
     * @param response  Response from the content provider
     */
    void processInsertResponse(@Nullable InsertResultWrapper response);

    /**
     * Process the response from a query request
     * @param response  Response from the content provider
     */
    void processQueryResponse(@Nullable QueryResultWrapper response);

    /**
     * Process the response from an update request
     * @param response  Response from the content provider
     */
    void processUpdateResponse(@Nullable UpdateResultWrapper response);

    /**
     * Process the response from a delete request
     * @param response  Response from the content provider
     */
    void processDeleteResponse(@Nullable DeleteResultWrapper response);

    /**
     * Process the response from a bulk insert request
     * @param response  Response from the content provider
     */
    void processBulkInsertResponse(@Nullable BulkInsertResultWrapper response);

    /**
     * Process the response from a call request
     * @param response  Response from the content provider
     */
    void processCallResponse(@Nullable CallResultWrapper response);

    /*
    ///////// Result wrapper classes //////////
    */

    /**
     * Response class for a Uri update request
     */
    class UpdateResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param intResult     Number of rows updated
         */
        public UpdateResultWrapper(Uri uriRequest, int intResult) {
            super(UPDATE_HANDLER, uriRequest, intResult);
        }
    }

    /**
     * Response class for a Uri delete request
     */
    class DeleteResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param intResult     Number of rows deleted
         */
        public DeleteResultWrapper(Uri uriRequest, int intResult) {
            super(DELETE_HANDLER, uriRequest, intResult);
        }
    }

    /**
     * Response class for a Uri query request
     */
    class QueryResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param cursorResult  Cursor which is positioned before the first entry, or null
         */
        public QueryResultWrapper(Uri uriRequest, @Nullable Cursor cursorResult) {
            super(QUERY_HANDLER, uriRequest, cursorResult);
        }
    }

    /**
     * Response class for a Uri insert request
     */
    class InsertResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param uriResult     Uri of the newly created row, may be <code>null</code>
         */
        public InsertResultWrapper(Uri uriRequest, @Nullable Uri uriResult) {
            super(INSERT_HANDLER, uriRequest, uriResult);
        }
    }

    /**
     * Response class for a Uri bulk insert request
     */
    class BulkInsertResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param intResult     Number of newly created rows
         */
        public BulkInsertResultWrapper(Uri uriRequest, int intResult) {
            super(BULK_INSERT_HANDLER, uriRequest, intResult);
        }
    }

    /**
     * Response class for a Url http request
     */
    class CallResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param stringResult  String response
         */
        public CallResultWrapper(Uri uriRequest, String stringResult) {
            super(CALL_HANDLER, uriRequest, stringResult);
        }
        /**
         * Constructor
         * @param uriRequest       Original request Uri
         * @param bundleResult     Bundle response
         */
        public CallResultWrapper(Uri uriRequest, Bundle bundleResult) {
            super(CALL_HANDLER, uriRequest, bundleResult);
        }
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param stringResult  String response
         */
        public CallResultWrapper(URL urlRequest, String stringResult) {
            super(CALL_HANDLER, urlRequest, stringResult);
        }
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param errorCode     Error code
         * @param errorString   Error string
         */
        public CallResultWrapper(URL urlRequest, int errorCode, String errorString) {
            this(urlRequest, errorCode, errorString, "");
        }
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param errorCode     Error code
         * @param errorString   Error string
         */
        public CallResultWrapper(Uri uriRequest, int errorCode, String errorString) {
            this(uriRequest, errorCode, errorString, "");
        }
        /**
         * Constructor
         * @param urlRequest        Original request URL
         * @param errorCode         Error code
         * @param errorString       Error string
         * @param errorDetailString Error detail string
         */
        public CallResultWrapper(URL urlRequest, int errorCode, String errorString, String errorDetailString) {
            super(CALL_HANDLER, urlRequest, errorCode, errorString, errorDetailString);
        }
        /**
         * Constructor
         * @param uriRequest        Original request Uri
         * @param errorCode         Error code
         * @param errorString       Error string
         * @param errorDetailString Error detail string
         */
        public CallResultWrapper(Uri uriRequest, int errorCode, String errorString, String errorDetailString) {
            super(CALL_HANDLER, uriRequest, errorCode, errorString, errorDetailString);
        }
        /**
         * Constructor
         * @param uriRequest        Original request Uri
         * @param error             Error info
         */
        public CallResultWrapper(Uri uriRequest, ErrorTuple error) {
            super(CALL_HANDLER, uriRequest, error.getErrorCode(), error.getErrorString(), error.getErrorDetail());
        }




    }
}
