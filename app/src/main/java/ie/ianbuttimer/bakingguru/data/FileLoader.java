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
import android.support.v4.content.AsyncTaskLoader;

import java.lang.ref.WeakReference;

import ie.ianbuttimer.bakingguru.bake.Recipe;


/**
 * Class to asynchronously load a file
 */

public class FileLoader<T, L extends ILoadable> {

    /** Key to use to pass path argument */
    public static final String PATH_ARG = "path";

    protected L loader;

    /**
     * Constructor
     * @param loader    Loader to use to read file
     */
    public FileLoader(L loader) {
        this.loader = loader;
    }

    /**
     * Create a new task loader
     * @param context   The current context
     * @param args      Argument bundle
     * @return new AsyncTaskLoader
     */
    public AsyncTaskLoader<T[]> getLoader(Context context, Bundle args) {
        return new Loader<>(context, args, this);
    }

    /**
     * Get the loader for this object
     * @return  Loader reference
     */
    private L getLoader() {
        return loader;
    }

    /**
     * AsyncTaskLoader for loader from file
     * @param <T>   type of object to return
     */
    private static class Loader<T> extends AsyncTaskLoader<T[]> {

        WeakReference<Context> context;
        Bundle args;
        FileLoader loader;

        T[] mRaw;   // raw results

        /**
         * Constructor
         * @param context   The current context
         * @param args      Arguments bundle
         */
        Loader(Context context, Bundle args, FileLoader loader) {
            super(context);
            this.context = new WeakReference<>(context);
            this.args = args;
            this.loader = loader;
        }

        @Override
        protected void onStartLoading() {

            if (args == null) {
                return; // no args, nothing to do
            }

        /*
         * If we already have cached results, just deliver them now. If we don't have any
         * cached results, force a load.
         */
            if (mRaw != null) {
                deliverResult(mRaw);
            } else {
                forceLoad();
            }
        }

        public void deliverResult(T[] result) {
            mRaw = result;
            super.deliverResult(result);
        }


        @Override
        @SuppressWarnings("unchecked")
        public T[] loadInBackground() {

            T[] result = null;

            if (args.containsKey(PATH_ARG)) {

                FileReader<Recipe, ILoadable> reader = new FileReader<>(context.get(), args.getString(PATH_ARG), loader.getLoader());

                result = (T[])reader.readArray();
            }

            return result;
        }
    }

}
