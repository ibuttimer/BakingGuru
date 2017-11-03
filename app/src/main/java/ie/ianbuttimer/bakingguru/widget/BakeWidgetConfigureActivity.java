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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ie.ianbuttimer.bakingguru.AbstractRecipeListActivity;
import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.adapter.IAdapterOnClickHandler;

import static ie.ianbuttimer.bakingguru.data.adapter.AbstractBakeRecycleViewAdapter.DEFAULT_TAG_KEY;

/**
 * The configuration screen for the {@link BakeWidgetProvider BakeWidgetProvider} AppWidget.
 */
public class BakeWidgetConfigureActivity extends AbstractRecipeListActivity implements IAdapterOnClickHandler {

    private static final String PREFS_NAME;
    private static final String PREF_PREFIX_KEY = "appwidget_";

    static {
        PREFS_NAME = BakeWidgetProvider.class.getName();
    }

    @BindView(R.id.rv_item_list_cfgWidgetA) RecyclerView mListRecyclerView;
    @BindView(R.id.pb_cfgWidgetA) ProgressBar mProgressBar;
    @BindView(R.id.fab_cfgWidgetA) FloatingActionButton mFab;
    @BindView(R.id.tv_error_cfgWidgetA) TextView mErrorMsg;
    @BindView(R.id.button_retry_cfgWidgetA) Button mRetry;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String recipeJson;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BakeWidgetConfigureActivity.this;

            // save the selection
            saveRecipePref(context, mAppWidgetId, recipeJson);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakeWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);        // set name & views
            BakeWidgetProvider.notifyAppWidgetViewDataChanged(appWidgetManager, mAppWidgetId);  // populated ingredients

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BakeWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.bake_widget_configure);

        ButterKnife.bind(this);

        mFab.setOnClickListener(mOnClickListener);

        setupRecyclerView(mListRecyclerView, this);

        onCreateBound(icicle);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mErrorMsg.getVisibility() == View.VISIBLE) {
            outState.putString(ERROR_MSG_ARG, mErrorMsg.getText().toString());
        }
    }


    /**
     * Set recipe list
     * @param list  List to set
     */
    @Override
    protected void setRecipeList(Recipe[] list) {

        mRecipeAdapter.setList(list);
        mRecipeAdapter.notifyDataSetChanged();
    }

    ////// IAdapterOnClickHandler implementation //////

    @Override
    public void onItemClick(View view) {
        Recipe recipe = (Recipe) view.getTag(DEFAULT_TAG_KEY);
        recipeJson = recipe.toJson();

        // un-select any previous selection
        for (int i = 0; i < mRecipeAdapter.getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mListRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.itemView.setSelected(false);
            }
        }

        view.setSelected(true);

        mFab.show();
    }

    @Override
    public void onItemClick(View view, AbstractBakeObject item) {
        onItemClick(view);
    }

    ////// AbstractRecipeListActivity implementation //////

    @Override
    protected Activity getActivityContext() {
        return BakeWidgetConfigureActivity.this;
    }

    @Override
    protected void showRefreshInProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideRefreshInProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void showErrorMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            mErrorMsg.setText(message);
            mErrorMsg.setVisibility(View.VISIBLE);
            mRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void hideErrorMessage() {
        mErrorMsg.setVisibility(View.INVISIBLE);
        mRetry.setVisibility(View.INVISIBLE);
    }

    /**
     * Get the shared preferences
     * @param context   The current context
     * @return  shared preferences object
     */
    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    /**
     * Save the preference for the widget
     * @param context       The current context
     * @param appWidgetId   Widget id
     * @param text          Preference value
     */
    static void saveRecipePref(Context context, int appWidgetId, String text) {
        // Write the prefix to the SharedPreferences object for this widget
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.putString(getPrefKey(appWidgetId), text);
        prefs.apply();
    }

    /**
     * Get a widget preference key
     * @param appWidgetId   Widget id
     * @return  preference key
     */
    private static String getPrefKey(int appWidgetId) {
        return PREF_PREFIX_KEY + appWidgetId;
    }

    /**
     * Load a widget preference
     * @param context       The current context
     * @param appWidgetId   Widget id
     * @return  preference value
     */
    static String loadRecipePref(Context context, int appWidgetId) {
        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        SharedPreferences prefs = getSharedPreferences(context);
        String value = prefs.getString(getPrefKey(appWidgetId), null);
        if (value == null) {
            value = context.getString(R.string.appwidget_text);
        }
        return value;
    }

    /**
     * DElete a widget preference
     * @param context       The current context
     * @param appWidgetId   Widget id
     */
    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.remove(getPrefKey(appWidgetId));
        prefs.apply();
    }


}

