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

package ie.ianbuttimer.bakingguru.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.parceler.Parcels;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.data.AbstractResultWrapper;
import ie.ianbuttimer.bakingguru.data.ICallback;
import ie.ianbuttimer.bakingguru.data.db.AbstractBakingDbPopulater;
import ie.ianbuttimer.bakingguru.data.db.BakingContract;
import ie.ianbuttimer.bakingguru.data.db.BakingDbAssetPopulater;
import ie.ianbuttimer.bakingguru.data.db.BakingDbHelper;
import ie.ianbuttimer.bakingguru.exception.HttpException;
import ie.ianbuttimer.bakingguru.utils.ErrorTuple;
import ie.ianbuttimer.bakingguru.network.NetworkUtils;
import ie.ianbuttimer.bakingguru.utils.UriUtils;
import ie.ianbuttimer.bakingguru.utils.Utils;
import timber.log.Timber;

import static ie.ianbuttimer.bakingguru.data.AbstractResultWrapper.INVALID_ERROR_CODE;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_ERROR;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_RESULT_TYPE;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.AUTHORITY;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.BASE_CONTENT_URI;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.ID_EQ_SELECTION;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.PATH_RECIPES;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.PATH_WITH_ID;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.REQUEST_URL;

/**
 * ContentProvider class for baking-related
 */

public class BakingContentProvider extends ContentProvider {

    /** Recipe match constant */
    public static final int RECIPE_MATCH = 100;
    /** Individual Recipe match constant */
    public static final int RECIPE_WITH_ID_MATCH = RECIPE_MATCH + 1;

    /** Movie content provider Uri matcher */
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Build the UriMatcher for use by this ContentProvider
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, PATH_RECIPES, RECIPE_MATCH);
        matcher.addURI(AUTHORITY, PATH_RECIPES + PATH_WITH_ID, RECIPE_WITH_ID_MATCH);

        return matcher;
    }


    private BakingDbHelper dbHelper; // database helper


    @Override
    public boolean onCreate() {
        Context context = getContext();
        AbstractBakingDbPopulater populater = null;
        boolean populate = false;

        if (context != null) {
            populate = Utils.getManifestMetaDataBoolean(context, context.getString(R.string.prepopulate_db_key), false);
        }
        if (populate) {
            populater = new BakingDbAssetPopulater(context);
        }

        dbHelper = new BakingDbHelper(context, populater);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE_WITH_ID_MATCH:
                // ignore selection & selectionArgs arguments as have id in uri
                selection = ID_EQ_SELECTION;
                selectionArgs = UriUtils.getIdSelectionArgFromWithIdUri(uri);
                // fall through
            case RECIPE_MATCH:
                // process query
                break;
            default:
                throwUnsupportedException(uri, "query");
        }

        cursor = db.query(getTable(match), projection, selection, selectionArgs, null, null, sortOrder);

        // Set a notification URI on the Cursor
        Context context = getContext();
        if ((cursor!= null) && (context != null)) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri resultUri = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE_MATCH:
            case RECIPE_WITH_ID_MATCH:
                long id = db.insert(getTable(match), null, contentValues);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(uri, id);
                }
                break;
            default:
                throwUnsupportedException(uri, "insert");
        }

        // Notify the resolver if the uri has been changed
        if (resultUri != null) {
            notifyChange(uri, null);
        }

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE_MATCH:
            case RECIPE_WITH_ID_MATCH:
                count = db.delete(getTable(match), selection, selectionArgs);
                break;
            default:
                throwUnsupportedException(uri, "delete");
        }

        // Notify the resolver of a change
        if (count > 0) {
            notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE_MATCH:
            case RECIPE_WITH_ID_MATCH:
                count = db.update(getTable(match), contentValues, selection, selectionArgs);
                break;
            default:
                throwUnsupportedException(uri, "update");
        }

        // Notify the resolver of a change
        if (count > 0) {
            notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long[] ids = null;
        int inserted = 0;
        db.beginTransaction();
        try {
            int match = sUriMatcher.match(uri);
            switch (match) {
                case RECIPE_MATCH: {
                    ids = insertValues(db, getTable(match), values);
                    break;
                }
            }
            if (ids != null) {
                notifyChange(uri, null);
                for (long id : ids) {
                    if (id != 0) {
                        ++inserted;
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return inserted;
    }

    private long[] insertValues(SQLiteDatabase db, String table, ContentValues[] values) {
        long[] ids = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            ids[i] = db.insertOrThrow(table, null, values[i]);
        }
        return ids;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle bundle = new Bundle();
        URL url = null;

        switch (method) {
            case REQUEST_URL:    // request the popular movies list
                try {
                    url = new URL(arg);
                } catch (MalformedURLException e) {
                    Timber.e("Invalid url", e);
                }
                break;
            default:
                throwUnsupportedException(null, method);
        }
        if (url != null) {
            // put the response from the url into the bundle
            ICallback.CallResultWrapper result = getHttpResponseStringSync(url);
            int resultType = AbstractResultWrapper.ResultType.STRING.ordinal(); // string result by default

            if (result.isError()) {
                resultType = AbstractResultWrapper.ResultType.ERROR.ordinal();

                ErrorTuple tuple = result.getErrorResult();
                bundle.putParcelable(CONTENT_PROVIDER_ERROR, Parcels.wrap(tuple));
            } else {
                if (result.isString()) {
                    bundle.putString(method, result.getStringResult());
                } else {
                    bundle.putString(method, "");
                }
            }
            bundle.putInt(CONTENT_PROVIDER_RESULT_TYPE, resultType);
        }
        return bundle;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        Uri contentUri = getContentUri(match);
        String type = contentUri.toString().substring(BASE_CONTENT_URI.toString().length());

        switch (match) {
            case RECIPE_MATCH:
                type = "vnd.android.cursor.dir" + type;
                break;
            case RECIPE_WITH_ID_MATCH:
                type = "vnd.android.cursor.item" + type;
                break;
            default:
                throwUnsupportedException(uri, "getType");
        }
        return type;
    }

    /**
     * Get the table name corresponding to the specified match id
     * @param match     Match id
     * @return  Table name
     */
    private String getTable(int match) {
        String table;
        switch (match) {
            case RECIPE_MATCH:
            case RECIPE_WITH_ID_MATCH:
                table = BakingContract.RecipeEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("No table for unknown match: " + match);
        }
        return table;
    }

    /**
     * Get the Content Uri corresponding to the specified match id
     * @param match     Match id
     * @return  Content Uri
     */
    private Uri getContentUri(int match) {
        Uri uri;
        switch (match) {
            case RECIPE_MATCH:
            case RECIPE_WITH_ID_MATCH:
                uri = BakingContract.RecipeEntry.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException("No uri for unknown match: " + match);
        }
        return uri;
    }


    /**
     * This method synchronously returns the entire result from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, or <code>null</code>
     * @see NetworkUtils#getHttpResponseStringSync
     */
    private ICallback.CallResultWrapper getHttpResponseStringSync(URL url) {
        ICallback.CallResultWrapper result;
        try {
            result = new ICallback.CallResultWrapper(url, NetworkUtils.getHttpResponseStringSync(url));
        } catch (HttpException e) {
            result = new ICallback.CallResultWrapper(url, e.getCode(), getErrorMsg(e), e.getResponseMessage());
            Timber.e(e);
        } catch (IOException e) {
            result = new ICallback.CallResultWrapper(url, INVALID_ERROR_CODE, getErrorMsg(e), e.getMessage());
            Timber.e(e);
        }
        return result;
    }

    /**
     * Get error message
     * @param e     Exception to get message for
     * @return  error string
     */
    private String getErrorMsg(IOException e) {
        String msg = "";
        Context context = getContext();
        if (context != null) {
            msg = context.getString(NetworkUtils.getErrorId(e));
        }
        return msg;
    }

    /**
     * Notify registered observers that a row was updated and attempt to sync changes to the network.
     * @param uri       The uri of the content that was changed.
     * @param observer  The observer that originated the change
     */
    private void notifyChange(@NonNull Uri uri, ContentObserver observer) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, observer);
        }
    }

    /**
     * Throw a new UnsupportedOperationException
     * @param uri       Uri which caused exception
     * @param method    Method uri was involked on
     */
    private void throwUnsupportedException(Uri uri, String method) {
        String message = "Unknown ";
        if (uri != null) {
            message += " uri " + uri;
        }
        if (!TextUtils.isEmpty(method)) {
            if (uri == null) {
                message += " method " + method;
            } else {
                message += " for " + method;
            }
        }
        throw new UnsupportedOperationException(message);
    }

}
