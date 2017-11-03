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
import android.database.sqlite.SQLiteException;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.provider.RecipeContentValues;
import timber.log.Timber;

/**
 * Implementation of a BakingDbHelper.Callbacks interface to populate the database on create from apk assets
 */

public abstract class AbstractBakingDbPopulater extends BakingDbHelperCallbacksImpl {

    /**
     * Default constructor
     * @param context  The current context
     */
    public AbstractBakingDbPopulater(Context context) {
        super(context);
    }

    protected void saveToDb(SQLiteDatabase db, Recipe[] array) {

        if ((array != null) && (array.length > 0)) {
            db.beginTransaction();
            RecipeContentValues.Builder builder = RecipeContentValues.builder();
            try {
                for (Recipe recipe : array) {
                    builder.clear()
                            .setId(recipe.getId())
                            .setJson(recipe.toJson());
                    db.insertOrThrow(BakingContract.RecipeEntry.TABLE_NAME, null, builder.build());
                }
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Timber.e("Unable to insert row", e);
            } finally {
                db.endTransaction();
            }
        }
    }
}
