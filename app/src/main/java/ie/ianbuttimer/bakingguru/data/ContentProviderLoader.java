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


/**
 * Baqse class to asynchronously handle calls to a ContentProvider.<br>
 */

public abstract class ContentProviderLoader extends AsyncTaskLoader<AbstractResultWrapper> {

    protected Bundle args;

    protected AbstractResultWrapper mRaw;   // raw results

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     */
    public ContentProviderLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
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

    @Override
    public void deliverResult(AbstractResultWrapper result) {
        mRaw = result;
        super.deliverResult(result);
    }

}
