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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Collections;

import static android.provider.BaseColumns._ID;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.COLUMN_TIMESTAMP;

/**
 * Contract class for database
 */
@SuppressWarnings("unused")
public class BakingContract {

    /** Content authority for database content provider */
    public static final String AUTHORITY = "ie.ianbuttimer.bakingguru";
    /** Base Uri for content provider */
    public static final Uri BASE_CONTENT_URI;

    /** Recipes path for content provider */
    public static final String PATH_RECIPES = "recipes";

    /** Ingredients path for content provider */
    public static final String PATH_INGREDIENTS = "ingredients";

    /** Individual item path for content provider */
    public static final String PATH_WITH_ID = "/#";

    static {
        Uri.Builder builder = new Uri.Builder().
                scheme(ContentResolver.SCHEME_CONTENT).
                encodedAuthority(AUTHORITY);
        BASE_CONTENT_URI = builder.build();
    }

    /**
     * Class to define the recipes table
     */
    public static final class RecipeEntry implements BaseColumns {

        /** Recipes Uri for content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        /** Recipe ingredients Uri for content provider */
        public static final Uri INGREDIENTS_CONTENT_URI = CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        // Recipe table and column names
        public static final String TABLE_NAME = "recipes";

        public static final String COLUMN_JSON = "json";    // json representation of object
        public static final String COLUMN_TIMESTAMP = "timestamp";       // timestamp of server response

        // call methods
        public static final String REQUEST_URL = "request_url";    // request url method
    }

    /**
     * Class to define the ingredients table
     */
    public static final class IngredientEntry implements BaseColumns {

        /** Ingredients Uri for content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        // Ingredients table and column names
        public static final String TABLE_NAME = "ingredients";

        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
    }

    /** String for a selection by id */
    public static final String ID_EQ_SELECTION = columnEqSelection(_ID);
    /** String for a selection by greater than or equal to timestamp */
    public static final String TIMESTAMP_GTEQ_SELECTION = columnGtEqSelection(COLUMN_TIMESTAMP);
    /** String for a selection by less than or equal to timestamp */
    public static final String TIMESTAMP_LTEQ_SELECTION = columnLtEqSelection(COLUMN_TIMESTAMP);
    /**
     * Make a column equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnEqSelection(String column) {
        return column + "=?";
    }

    /**
     * Make a column in selection argument
     * @param column    Column name
     * @param count     Number of items to delete
     * @return  Selection string
     */
    public static String columnInSelection(String column, int count) {
        return String.format(column + " IN (%s)",
                TextUtils.join(",", Collections.nCopies(count, "?")));
    }

    /**
     * Make a column greater than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnGtEqSelection(String column) {
        return column + ">=?";
    }

    /**
     * Make a column less than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnLtEqSelection(String column) {
        return column + "<=?";
    }



}
