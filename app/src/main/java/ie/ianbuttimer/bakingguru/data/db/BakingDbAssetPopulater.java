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

import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.FileReader;

/**
 * Implementation of a BakingDbHelper.Callbacks interface to populate the database on create from apk assets
 */

public class BakingDbAssetPopulater extends AbstractBakingDbPopulater {

    /**
     * Default constructor
     * @param context  The current context
     */
    public BakingDbAssetPopulater(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Load initial db content from apk
        Context context = getContext();
        FileReader<Recipe, Recipe.Loader> reader = new FileReader<>(context, context.getString(R.string.json_asset_file), new Recipe.Loader());
        Recipe[] array = reader.readArray();
        saveToDb(db, array);
    }
}
