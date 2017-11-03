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

package ie.ianbuttimer.bakingguru.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.widget.RemoteViews;

import org.parceler.Parcels;

import ie.ianbuttimer.bakingguru.ItemListActivity;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.BakeWidgetViewsService;
import ie.ianbuttimer.bakingguru.data.JsonStringReader;

import static ie.ianbuttimer.bakingguru.ItemListActivity.ARG_ITEM;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakeWidgetConfigureActivity BakeWidgetConfigureActivity}
 */
public class BakeWidgetProvider extends AppWidgetProvider {

    /**
     * Get the id of the layout to use for the ingredient list
     * @param context           The current context
     * @param appWidgetManager  AppWidgetManager reference
     * @param appWidgetId       Id of widget
     * @return  Id of layout to use
     */
    @LayoutRes static int getListLayoutId(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        @LayoutRes int layoutId;
        Float narrowLimit = context.getResources().getDimension(R.dimen.widget_2_cell);
        Float margin = context.getResources().getDimension(R.dimen.widget_margin);
        narrowLimit += margin;
        if (width > narrowLimit.intValue()) {
            layoutId = R.layout.ingredient_list_item;
        } else {
            layoutId = R.layout.ingredient_list_item_narrow;
        }
        return layoutId;
    }

    /**
     * Update a widget
     * @param context           The current context
     * @param appWidgetManager  AppWidgetManager
     * @param appWidgetId       Widget id
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = BakeWidgetConfigureActivity.loadRecipePref(context, appWidgetId);
        Recipe recipe = null;
        if (!TextUtils.isEmpty(widgetText)) {
            JsonStringReader<Recipe, Recipe.Loader> reader = new JsonStringReader<>(widgetText.toString(), new Recipe.Loader());
            recipe = reader.readObject();
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bake_widget);
        if (recipe != null) {
            // determine which widget layout to use
            @LayoutRes int layoutId = getListLayoutId(context, appWidgetManager, appWidgetId);

            // Construct the RemoteViews object
            views.setTextViewText(R.id.appwidget_text, recipe.getName());

            Intent intent = BakeWidgetViewsService.getLaunchIntent(context, recipe.getId(), layoutId, appWidgetId);
            views.setRemoteAdapter(R.id.appwidget_listview, intent);

            // set the widget onclick to load the recipe
            Intent appIntent = new Intent(context, ItemListActivity.class);
            appIntent.putExtra(ARG_ITEM, Parcels.wrap(recipe));
            // set the request code to the widget id to ensure its unique and avoid issues with cached intents
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            views.setPendingIntentTemplate(R.id.appwidget_listview, pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Notify the AppWidgetManager that a dataset has changed
     * @param appWidgetManager  The AppWidgetManager
     * @param appWidgetId       Widget ids
     */
    static void notifyAppWidgetViewDataChanged(AppWidgetManager appWidgetManager, int appWidgetId) {
        appWidgetManager.notifyAppWidgetViewDataChanged(new int[] {
                    appWidgetId
            }, R.id.appwidget_listview);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakeWidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        // update widget to suit dimension change if applicable
        updateAppWidget(context, appWidgetManager, appWidgetId);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}

