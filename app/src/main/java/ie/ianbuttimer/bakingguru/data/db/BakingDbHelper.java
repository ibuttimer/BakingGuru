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

package ie.ianbuttimer.bakingguru.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


/**
 * Movie database helper class
 */
@SuppressWarnings("unused")
public class BakingDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "bakingDb.db";

    // The database version
    private static final int VERSION = 1;

    private Callbacks mCallback;


    /**
     * Default constructor
     * @param context   The current context
     */
    public BakingDbHelper(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     * @param context   The current context
     * @param callback  Callback implementation
     */
    public BakingDbHelper(Context context, @Nullable Callbacks callback) {
        super(context, DATABASE_NAME, null, VERSION);
        mCallback = callback;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        createTables(sqLiteDatabase);

        if (mCallback != null) {
            mCallback.onCreate(sqLiteDatabase);
        }
    }

    /**
     * Create the db tables
     * @param sqLiteDatabase    Db reference
     */
    private void createTables(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE " + BakingContract.RecipeEntry.TABLE_NAME + " (" +
                /* use recipe id as the primary key */
                BakingContract.RecipeEntry._ID              + " INTEGER, " +
                BakingContract.RecipeEntry.COLUMN_JSON      + " STRING NOT NULL, " +
                BakingContract.RecipeEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (" + BakingContract.RecipeEntry._ID + ") ON CONFLICT REPLACE" +
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // recreate db
        dropTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);

        if (mCallback != null) {
            mCallback.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }

    /**
     * Drop all the tables in the db
     * @param sqLiteDatabase    Db reference
     */
    private void dropTables(SQLiteDatabase sqLiteDatabase) {
        for (String table : getTableNames()) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table + ";");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // recreate db
        dropTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);

        if (mCallback != null) {
            mCallback.onDowngrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }

    /**
     * Get a list of database table names
     * @return  Array of tables
     */
    public String[] getTableNames() {
        return new String []{
            BakingContract.RecipeEntry.TABLE_NAME
        };
    }

    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {
        super.onConfigure(sqLiteDatabase);

        if (mCallback != null) {
            mCallback.onConfigure(sqLiteDatabase);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        super.onOpen(sqLiteDatabase);

        if (mCallback != null) {
            mCallback.onOpen(sqLiteDatabase);
        }
    }

    /**
     * Interface to reflect the SQLiteOpenHelper callbacks
     */
    public interface Callbacks {

        void onConfigure(SQLiteDatabase db);

        void onCreate(SQLiteDatabase db);

        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

        void onOpen(SQLiteDatabase db);

        Context getContext();
    }
}
