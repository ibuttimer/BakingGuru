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

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.FileReader;
import timber.log.Timber;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * Test of the ItemListActivity
 */
@RunWith(AndroidJUnit4.class)
public class AbstractRecipeTest {

    /** Recipe sRecipes for test */
    static Recipe[] sRecipes;

    @BeforeClass
    public static void onlyOnce() {
        // Read the contents of the json file used to initially populate the database
        Context context = InstrumentationRegistry.getTargetContext();
        String path = context.getString(R.string.json_asset_file);

        FileReader<Recipe, Recipe.Loader> reader = new FileReader<>(context, path, new Recipe.Loader());

        try {
            sRecipes = reader.readArray();
        } catch (Exception e) {
            Timber.e(e);
        }

        assertNotNull("Sample sRecipes", sRecipes);
        assertTrue("Sample sRecipes length", (sRecipes.length > 0));
    }

}