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

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import ie.ianbuttimer.bakingguru.data.db.BakingContract;
import ie.ianbuttimer.bakingguru.utils.DbUtils;
import ie.ianbuttimer.bakingguru.utils.PreferenceControl;
import ie.ianbuttimer.bakingguru.utils.UriUtils;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.provider.BaseColumns._ID;
import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.ID_EQ_SELECTION;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.TIMESTAMP_LTEQ_SELECTION;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.columnInSelection;
import static ie.ianbuttimer.bakingguru.utils.DbUtils.DB_DELETE_ALL;
import static ie.ianbuttimer.bakingguru.utils.Utils.getParcelableArrayFromBundle;

/**
 * IntentService to handle database caching functionality
 */

public class DbCacheIntentService extends IntentService {

    /** Clear expired database entries action */
    public static final String PURGE_EXPIRED = "purge_expired";


    private static final String RECIPE = "recipe";
    /** Insert a recipe action */
    public static final String INSERT_RECIPE = "insert_" + RECIPE;
    /** Insert multiple recipes action */
    public static final String BULK_INSERT_RECIPE = "bulk_insert_" + RECIPE;
    /** Insert or update a recipe action */
    public static final String INSERT_OR_UPDATE_RECIPE = "insert_or_update_" + RECIPE;
    /** Update a recipe action */
    public static final String UPDATE_RECIPE = "update_" + RECIPE;
    /** Delete a recipe action */
    public static final String DELETE_RECIPE = "delete_" + RECIPE;
    /** Delete all recipe action */
    public static final String DELETE_ALL_RECIPES = "delete_all_" + RECIPE;
    /** Clear expired recipe cache action */
    public static final String PURGE_EXPIRED_RECIPES = PURGE_EXPIRED + "_" + RECIPE;

    /** Name for ContentValues in intent */
    public static final String CV_EXTRA = "cv_extra";
    /** Name for ContentValues array in intent */
    public static final String CV_ARRAY_EXTRA = "cv_array_extra";

    /** Name for result count in result bundle */
    public static final String RESULT_COUNT = "result_count";

    public static final String RESULT_RECEIVER = "resultReceiver";


    public DbCacheIntentService() {
        super(DbCacheIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        int id = intent.getIntExtra(_ID, 0);
        ContentValues cv = intent.getParcelableExtra(CV_EXTRA);
        Cursor cursor;
        int count = 0;
        ResultReceiver resultReceiver;

        if (intent.hasExtra(RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);
        } else {
            resultReceiver = null;
        }

        String action = intent.getAction();
        switch (action) {
            // recipe related actions
            case INSERT_OR_UPDATE_RECIPE:
                cursor = dbGetById(action, id);
                if (cursor != null) {
                    // row exists so update
                    dbUpdate(getWithIdUri(action, id), cv);
                    break;
                }
                // else fall through to insert
            case INSERT_RECIPE:
                dbInsert(action, cv);
                break;
            case BULK_INSERT_RECIPE:
                ContentValues[] cvArray = (ContentValues[]) getParcelableArrayFromBundle(
                        intent.getExtras(), CV_ARRAY_EXTRA, ContentValues[].class);
                count = dbBulkInsert(action, cvArray);
                break;
            case UPDATE_RECIPE:
                count = dbUpdate(getWithIdUri(action, id), cv);
                break;
            case DELETE_RECIPE:
                count = dbDeleteById(action, id);
                break;
            case DELETE_ALL_RECIPES:
                count = dbDeleteAll(action);
                break;
            case PURGE_EXPIRED:
            case PURGE_EXPIRED_RECIPES:
                purgeExpiredMovies();
                break;
            default:
                throw new UnsupportedOperationException("Unknown service action: " + action);
        }

        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(RESULT_COUNT, count);
            resultReceiver.send(RESULT_OK, bundle);
        }
    }

    /**
     * Get the base uri for the specified action
     * @param action    Action to get uri for
     * @return  'with id' uri or <cide>null</cide>
     */
    protected Uri getBaseUri(@NonNull String action) {
        Uri uri = null;
        if (action.contains(RECIPE)) {
            uri = BakingContract.RecipeEntry.CONTENT_URI;
        }
        return uri;
    }

    /**
     * Get the 'with id' uri for the specified action
     * @param action    Action to get uri for
     * @param id        Id to append to base uri
     * @return  'with id' uri or <cide>null</cide>
     */
    protected Uri getWithIdUri(@NonNull String action, int id) {
        Uri uri = null;
        if (action.contains(RECIPE)) {
            uri = UriUtils.getRecpeWithIdUri(id);
        }
        return uri;
    }

    /**
     * Add a row to the database
     * @param action    Action to do insert for
     * @param cv        Values to add
     * @return  Uri of new addition, or <code>null</code>
     */
    private Uri dbInsert(@NonNull String action, ContentValues cv) {
        Uri resultUri = null;
        Uri uri = getBaseUri(action);
        if ((uri != null) && (cv != null)) {
            resultUri = getContentResolver().insert(uri, cv);
        }
        return resultUri;
    }

    /**
     * Add multiple rows to the database
     * @param action    Action to do insert for
     * @param cv        Values to add
     * @return  Number of inserted items
     */
    private int dbBulkInsert(@NonNull String action, ContentValues[] cv) {
        int count = 0;
        Uri uri = getBaseUri(action);
        if ((uri != null) && (cv != null)) {
            count = getContentResolver().bulkInsert(uri, cv);
        }
        return count;
    }

    /**
     * Update an existing recipe in the database
     * @param uri   Uri to use for update
     * @param cv    Values to add
     * @return  <code>1</code> if update successful, <code>0</code> otherwise
     */
    private int dbUpdate(@NonNull Uri uri, ContentValues cv) {
        int count = 0;
        if (cv != null) {
            count = getContentResolver().update(uri, cv, null, null);
        }
        return count;
    }

    /**
     * Get a row from the database
     * @param action    Action to do get for
     * @param id        Id of row
     * @return  Cursor containing recipe info
     */
    private Cursor dbGetById(@NonNull String action, int id) {
        Cursor cursor = null;
        Uri uri = getBaseUri(action);
        if ((uri != null) && (id > 0)) {
            cursor = getContentResolver().query(uri, null, ID_EQ_SELECTION, DbUtils.idArgArray(id), null);
            if ((cursor != null) && (cursor.getCount() == 0)) {
                // doesn't exist so return null for convenience
                cursor.close();
                cursor = null;
            }
        }
        return cursor;
    }

    /**
     * Delete a row from the database
     * @param action    Action to do delete for
     * @param id        Id of recipe
     * @return  <code>1</code> if delete successful, <code>0</code> otherwise
     */
    private int dbDeleteById(@NonNull String action, int id) {
        return dbDeleteById(action, new int[] { id });
    }

    /**
     * Delete multiple rows from the database
     * @param action    Action to do delete for
     * @param ids       Array of recipe ids
     * @return  Number of deleted items
     */
    private int dbDeleteById(@NonNull String action, @NonNull int[] ids) {
        return dbDeleteById(action, DbUtils.idArgArray(ids));
    }

    /**
     * Delete multiple rows from the database
     * @param action    Action to do delete for
     * @param ids       Array of recipe ids
     * @return  Number of deleted items
     */
    private int dbDeleteById(@NonNull String action, @NonNull String[] ids) {
        int count = 0;
        Uri uri = getBaseUri(action);
        if ((uri != null) && (ids.length > 0)) {
            count = getContentResolver().delete(uri, columnInSelection(_ID, ids.length), ids);
        }
        return count;
    }

    /**
     * Delete all rows from the database
     * @param action    Action to do delete for
     * @return  Number of deleted items
     */
    private int dbDeleteAll(@NonNull String action) {
        int count = 0;
        Uri uri = getBaseUri(action);
        if (uri != null) {
            count = getContentResolver().delete(uri, DB_DELETE_ALL, null);

            Timber.i("Deleted " + count + " recipe(s) from db");
        }
        return count;
    }

    /**
     * Purge recipes which have expired from the db
     * @return  Number of deleted items
     */
    private int purgeExpiredMovies() {
        ContentResolver resolver = getContentResolver();
        int days = PreferenceControl.getCacheLengthPreference(getApplicationContext());
        long expiryMsec = new Date().getTime() - (days * DAY_IN_MILLIS);    // expiry date in msec
        Date expiryDate = new Date(expiryMsec);
        int count = 0;

        Cursor cursor = resolver.query(BakingContract.RecipeEntry.CONTENT_URI, new String[] {
            _ID     // only need id
        }, TIMESTAMP_LTEQ_SELECTION, new String[] {
            DbUtils.getTimestamp(expiryDate)
        }, null);

        if (cursor != null) {
            String[] toPurge = getSelectionArgs(cursor, cursor.getColumnIndex(_ID));
            if (toPurge != null) {
                count = dbDeleteById(PURGE_EXPIRED_RECIPES, toPurge);

                Timber.i("Purged " + count + " recipe(s) from db");
            }
        }
        return count;
    }

    /**
     * Make a selection args array, and closes cursor
     * @param cursor    Cursor to generate selection args from
     * @param colIdx    Index of cursor column
     * @return  selection args array
     */
    private String[] getSelectionArgs(Cursor cursor, int colIdx) {
        String[] argArray = null;
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                argArray = new String[count];

                while (cursor.moveToNext()) {
                    --count;    // use count var to avoid creating new var
                    argArray[count] = cursor.getString(colIdx);
                }
            }
            cursor.close();
        }
        return argArray;
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @param action    Action for the service to perform
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context, String action) {
        Intent intent = new Intent(context, DbCacheIntentService.class);
        intent.setAction(action);
        return intent;
    }
}
