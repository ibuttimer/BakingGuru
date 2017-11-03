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

import java.lang.ref.WeakReference;

/**
 * Base implementation of a BakingDbHelper.Callbacks interface
 */

public class BakingDbHelperCallbacksImpl implements BakingDbHelper.Callbacks {

    private WeakReference<Context> mContext;

    /**
     * Default constructor
     * @param context  The current context
     */
    public BakingDbHelperCallbacksImpl(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        // no op
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // no op
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no op
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no op
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // no op
    }

    @Override
    public Context getContext() {
        return mContext.get();
    }
}
