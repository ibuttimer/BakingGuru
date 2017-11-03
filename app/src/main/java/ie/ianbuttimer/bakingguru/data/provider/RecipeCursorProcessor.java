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

import android.database.Cursor;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;

import ie.ianbuttimer.bakingguru.bake.Recipe;

import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.COLUMN_JSON;

/**
 * Class to process recipe cursors
 */
@SuppressWarnings("unused")
public class RecipeCursorProcessor {

    private Cursor cursor;

    public RecipeCursorProcessor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Read a Recipe object from a cursor
     * @param cursor    Cursor to read from
     * @param position  Zero-based cursor row to read
     * @return  new Recipe object or <code>null</code>
     */
    public static @Nullable Recipe processSingle(Cursor cursor, int position) {
        Recipe recipe = null;
        if (cursor != null) {
            int colIndex = cursor.getColumnIndex(COLUMN_JSON);

            if (cursor.moveToPosition(position)) {
                recipe = processOne(cursor, colIndex, new Recipe());
            }
        }
        return recipe;
    }

    /**
     * Read a Recipe object from a cursor
     * @param cursor    Cursor to read from
     * @return  new Recipe object or <code>null</code>
     */
    public static @Nullable Recipe processSingle(Cursor cursor) {
        return processSingle(cursor, 0);
    }

    /**
     * Read a Recipe object
     * @param position  Zero-based cursor row to read
     * @return  new Recipe object or <code>null</code>
     */
    public @Nullable Recipe processSingle(int position) {
        return processSingle(cursor, position);
    }

    /**
     * Read a Recipe object
     * @return  new Recipe object or <code>null</code>
     */
    public @Nullable Recipe processSingle() {
        return processSingle(cursor);
    }

    /**
     * Read a Recipe object array from a cursor
     * @param cursor    Cursor to read from
     * @return  new Recipe object array or <code>null</code>
     */
    public static @Nullable Recipe[] processArray(Cursor cursor) {
        Recipe[] recipes = null;
        if (cursor != null) {
            Recipe loadRecipe = new Recipe();
            int length = cursor.getCount();
            recipes = new Recipe[length];
            int colIndex = cursor.getColumnIndex(COLUMN_JSON);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < length; ++i) {
                    recipes[i] = processOne(cursor, colIndex, loadRecipe);
                    cursor.moveToNext();
                }
            }
        }
        return recipes;
    }

    /**
     * Read a Recipe object array
     * @return  new Recipe object array or <code>null</code>
     */
    public @Nullable Recipe[] processArray() {
        return processArray(cursor);
    }

    /**
     * Read a recipe from a cursor
     * @param cursor        Cursor to read from
     * @param colIndex      Data column index
     * @param loadRecipe    Object to process reading
     * @return  new Recipe object
     */
    private static @Nullable Recipe processOne(Cursor cursor, int colIndex, Recipe loadRecipe) {
        Recipe result = null;
        try {
            result = loadRecipe.read(cursor.getString(colIndex));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
