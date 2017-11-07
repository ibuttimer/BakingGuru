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

package ie.ianbuttimer.bakingguru;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.FileReader;
import ie.ianbuttimer.bakingguru.data.provider.RecipeContentValues;
import ie.ianbuttimer.bakingguru.utils.Utils;
import timber.log.Timber;

import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.CONTENT_URI;
import static ie.ianbuttimer.bakingguru.utils.DbUtils.DB_DELETE_ALL;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;


/**
 * Test of the ItemListActivity
 */
@RunWith(AndroidJUnit4.class)
public class AbstractRecipeTest {

    /** Recipe sRecipes for test */
    static Recipe[] sRecipes;

    @BeforeClass
    public static void onlyOnceBefore() {
        // Read the contents of the json file used to initially populate the database
        Context testContext = InstrumentationRegistry.getContext();
        Context appContext = InstrumentationRegistry.getTargetContext();
        ContentResolver contentResolver = appContext.getContentResolver();
        String path = appContext.getString(R.string.json_asset_file);

        boolean populate = Utils.getManifestMetaDataBoolean(appContext, appContext.getString(R.string.prepopulate_db_key), false);

        FileReader<Recipe, Recipe.Loader> reader = new FileReader<>(testContext, path, new Recipe.Loader());

        try {
            sRecipes = reader.readArray();
        } catch (Exception e) {
            Timber.e(e);
        }

        assertNotNull("Sample sRecipes", sRecipes);
        assertTrue("Sample sRecipes length", (sRecipes.length > 0));

        // empty the database
        contentResolver.delete(CONTENT_URI, DB_DELETE_ALL, null);

        // populate db with test resources
        ContentValues[] cvArray = RecipeContentValues.buildArray(sRecipes);
        int inserted = contentResolver.bulkInsert(CONTENT_URI, cvArray);

        assertEquals("Test data not loaded", cvArray.length, inserted);
    }


    @AfterClass
    public static void onlyOnceAfter() {
        // clear the database
        Context appContext = InstrumentationRegistry.getTargetContext();
        ContentResolver contentResolver = appContext.getContentResolver();
        contentResolver.delete(CONTENT_URI, DB_DELETE_ALL, null);
    }
}