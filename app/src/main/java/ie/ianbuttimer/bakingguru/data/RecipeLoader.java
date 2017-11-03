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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import ie.ianbuttimer.bakingguru.bake.Recipe;

import static ie.ianbuttimer.bakingguru.data.FileLoader.PATH_ARG;

/**
 * Loader class for Recipes
 */

public class RecipeLoader implements LoaderManager.LoaderCallbacks<Recipe[]> {

    WeakReference<Context> context;
    IResultCallback<Recipe> callback;

    /**
     * Constructor
     * @param context   The current context
     * @param callback
     */
    public RecipeLoader(Context context, @NonNull IResultCallback<Recipe> callback) {
        this.context = new WeakReference<>(context);
        this.callback = callback;
    }

    /**
     * Start a loader
     * @param activity  Current activity
     * @param args      Arguments bundle
     * @param loaderId  Id of loader to start
     */
    public void startLoader(AppCompatActivity activity, @NonNull Bundle args, int loaderId) {
        LoaderManager manager = activity.getSupportLoaderManager();
        Loader loader = manager.getLoader(loaderId);
        if (loader == null) {
            // Initialize the loader
            manager.initLoader(loaderId, args, this);
        } else {
            manager.restartLoader(loaderId, args, this);
        }
    }

    /**
     * Make the argument bundle required by the loader
     * @param path  Path to file
     * @return  Argument bundle
     */
    public static Bundle makeArgsBundle(@NonNull String path) {
        Bundle bundle = new Bundle();
        bundle.putString(PATH_ARG, path);
        return bundle;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        FileLoader<Recipe, Recipe.Loader> loader = new FileLoader<>(new Recipe.Loader());
        return loader.getLoader(context.get(), bundle);
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] array) {
        callback.onResult(array);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // no op
    }
}
