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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Ingredient;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.adapter.IngredientsAdapterViewBinder;
import ie.ianbuttimer.bakingguru.data.db.BakingContract;
import ie.ianbuttimer.bakingguru.data.provider.RecipeCursorProcessor;
import ie.ianbuttimer.bakingguru.utils.UriUtils;
import timber.log.Timber;

import static ie.ianbuttimer.bakingguru.data.db.BakingContract.IngredientEntry.COLUMN_INGREDIENT;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.IngredientEntry.COLUMN_MEASURE;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.IngredientEntry.COLUMN_QUANTITY;


/**
 * RemoteView service for app widget
 */

public class BakeWidgetViewsService extends RemoteViewsService {

    public static final String ID_EXTRA = "id_extra";
    public static final String LAYOUT_EXTRA = "layout_extra";
    public static final String WIDGET_EXTRA = "widget_extra";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Uri uri;
        @LayoutRes int layoutId = intent.getIntExtra(LAYOUT_EXTRA, R.layout.ingredient_list_item);
        int widgetId = intent.getIntExtra(WIDGET_EXTRA, 0);
        if (intent.hasExtra(ID_EXTRA)) {
            uri = UriUtils.getRecpeWithIdUri(intent.getIntExtra(ID_EXTRA, 0));
        } else {
            uri = BakingContract.RecipeEntry.CONTENT_URI;
        }
        return new BakeWidgetViewsFactory(this, uri, layoutId, widgetId);
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, BakeWidgetViewsService.class);
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @param id        Id of recipe to return
     * @param layoutId  Id of layout to use
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context, int id, @LayoutRes int layoutId, int widgetId) {
        Intent intent = getLaunchIntent(context);
        // setting the data to a "unique" uri avoids caching issues
        intent.setData(Uri.fromParts(ContentResolver.SCHEME_CONTENT, String.valueOf(widgetId) + "/" + String.valueOf(layoutId), null));
        intent.putExtra(ID_EXTRA, id);
        intent.putExtra(LAYOUT_EXTRA, layoutId);
        intent.putExtra(WIDGET_EXTRA, widgetId);
        return intent;
    }


    private static final int[] sTextViewIds = new int[] {
            R.id.tv_quantity_ingredient_list_item,
            R.id.tv_measure_ingredient_list_item,
            R.id.tv_ingredient_ingredient_list_item
    };

    private static final String[] sCursorColumns = new String[] {
        COLUMN_QUANTITY, COLUMN_MEASURE, COLUMN_INGREDIENT
    };

    /**
     * Class to generate ingredient views for the app widget
     */
    private class BakeWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private Uri mUri;
        private MatrixCursor mCursor;
        @LayoutRes private int mLayoutId;
        private int mWidgetId;

        /**
         * Constructor
         * @param context  The current context
         */
        BakeWidgetViewsFactory(Context context, Uri uri, @LayoutRes int layoutId, int widgetId) {
            this.mContext = context.getApplicationContext();
            this.mUri = uri;
            this.mLayoutId = layoutId;
            this.mWidgetId = widgetId;
        }

        @Override
        public void onCreate() {
            // no op
        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }
            Cursor cursor = mContext.getContentResolver().query(mUri, null, null, null, null);
            Recipe recipe = null;

            if (cursor != null) {
                recipe = RecipeCursorProcessor.processSingle(cursor);
                cursor.close();
            }

            if (recipe != null) {
                // create ingredients cursor
                mCursor = new MatrixCursor(sCursorColumns, recipe.getIngredientCount());

                for (Ingredient ingredient : recipe.getIngredients()) {
                    mCursor.addRow(new Object[] {
                        ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient()
                    });
                }
            }
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mCursor != null) {
                count = mCursor.getCount();
            }
            Timber.d("Ingredient count " + count);
            return count;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = null;

            if (getCount() > 0) {
                if (mCursor.moveToPosition(position)) {
                    int colQuantity = mCursor.getColumnIndex(COLUMN_QUANTITY);
                    int colMeasure = mCursor.getColumnIndex(COLUMN_MEASURE);
                    int colName = mCursor.getColumnIndex(COLUMN_INGREDIENT);

                    views = new RemoteViews(mContext.getPackageName(), mLayoutId);

                    String quantity = IngredientsAdapterViewBinder.formatQuantity(
                            mCursor.getDouble(colQuantity));

                    views.setTextViewText(R.id.tv_quantity_ingredient_list_item, quantity);
                    views.setTextViewText(R.id.tv_measure_ingredient_list_item, mCursor.getString(colMeasure));
                    views.setTextViewText(R.id.tv_ingredient_ingredient_list_item, mCursor.getString(colName));

                    // set the template fill in intent, nothing required as recipe is in the template
                    Intent fillInIntent = new Intent();
                    for (int id : sTextViewIds) {
                        views.setOnClickFillInIntent(id, fillInIntent);
                    }
                }
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;   // only 1 view
        }

        @Override
        public long getItemId(int i) {
            return i;   // use index as id
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}

