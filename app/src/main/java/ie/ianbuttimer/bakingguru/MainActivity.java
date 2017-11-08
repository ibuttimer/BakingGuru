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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.DbCacheIntentService;
import ie.ianbuttimer.bakingguru.data.adapter.IAdapterOnClickHandler;
import ie.ianbuttimer.bakingguru.idling_resource.SimpleIdlingResource;
import ie.ianbuttimer.bakingguru.utils.PreferenceControl;
import ie.ianbuttimer.bakingguru.utils.Tuple;
import ie.ianbuttimer.bakingguru.utils.Utils;

import static ie.ianbuttimer.bakingguru.data.DbCacheIntentService.PURGE_EXPIRED;
import static ie.ianbuttimer.bakingguru.data.adapter.AbstractBakeRecycleViewAdapter.DEFAULT_TAG_KEY;
import static ie.ianbuttimer.bakingguru.utils.PreferenceControl.PreferenceTypes.BOOLEAN;

/**
 * Main application activity
 */
@SuppressWarnings("unused")
public class MainActivity extends AbstractRecipeListActivity implements IAdapterOnClickHandler {

    @BindView(R.id.rv_item_list_mainA) RecyclerView mListRecyclerView;
    @BindView(R.id.pb_mainA) ProgressBar mProgressBar;
    @BindView(R.id.tv_error_mainA) TextView mErrorMsg;
    @BindView(R.id.button_retry_mainA) Button mRetry;

    private static boolean PREFERENCE_UPDATED = false;  // flag to indicate that preferences have changed
    private static HashMap<String, Object> PREFERENCES; // map of preferences and current values

    @SuppressWarnings("unchecked")
    private static final Tuple<Integer, Integer, PreferenceControl.PreferenceTypes>[] PREFERENCE_LIST =
            new Tuple[] {
                    new Tuple<>(R.string.pref_clear_cache_key, R.bool.pref_clear_cache_dflt_value, BOOLEAN)
            };

    /**
     * Preference change listener to refresh display
     */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    PREFERENCE_UPDATED = true;  // flag preference change
                }
            };


    // espresso test related
    @Nullable private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupRecyclerView(mListRecyclerView, this);

        // finish portion of onCreate that requires bound views
        onCreateBound(savedInstanceState);

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRecipesServer();
            }
        });

        startService(DbCacheIntentService.getLaunchIntent(this, PURGE_EXPIRED)); // purge expired entries from db

        // watch for preference changes
        setupPreferenceMap();
        PreferenceControl.registerOnSharedPreferenceChangeListener(this, preferenceChangeListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mErrorMsg.getVisibility() == View.VISIBLE) {
            outState.putString(ERROR_MSG_ARG, mErrorMsg.getText().toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCE_UPDATED) {
            processPreferenceChange();
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceControl.unregisterOnSharedPreferenceChangeListener(this, preferenceChangeListener);
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set intent for about activity
        MenuItem menuItem = menu.findItem(R.id.action_about);
        menuItem.setIntent(new Intent(this, AboutActivity.class));
        // set intent for settings activity
        menuItem = menu.findItem(R.id.action_settings);
        menuItem.setIntent(new Intent(this, SettingsActivity.class));

        return true;
    }

    ////// IAdapterOnClickHandler implementation //////

    @Override
    public void onItemClick(View view) {
        Recipe recipe = (Recipe) view.getTag(DEFAULT_TAG_KEY);
        Parcelable wrapped = Parcels.wrap(recipe);
        Context context = view.getContext();
        Intent intent = new Intent(context, ItemListActivity.class);

        intent.putExtra(ItemListActivity.ARG_ITEM, wrapped);
        intent.putExtra(ItemListActivity.ARG_ITEM_ID, recipe.getId());

        Utils.startActivity(context, intent);
    }

    @Override
    public void onItemClick(View view, AbstractBakeObject item) {
        onItemClick(view);
    }

    ////// AbstractRecipeListActivity implementation //////

    @Override
    protected Activity getActivityContext() {
        return MainActivity.this;
    }

    /**
     * Set recipe list
     * @param list  List to set
     */
    protected void setRecipeList(Recipe[] list) {

        mRecipeAdapter.setList(list);
        mRecipeAdapter.notifyDataSetChanged();
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
     * Initialise the map of current preference settings
     */
    private void setupPreferenceMap() {
        PREFERENCES = new HashMap<>();
        for (Tuple<Integer, Integer, PreferenceControl.PreferenceTypes> entry : PREFERENCE_LIST) {
            int keyId = entry.getT1();
            String key = getString(keyId);
            Object setting = PreferenceControl.getSharedPreference(this, entry.getT3(), keyId, entry.getT2());
            if (setting != null) {
                PREFERENCES.put(key, setting);
            }
        }
    }

    /**
     * Process a preference change
     */
    private void processPreferenceChange() {

        boolean request = false;    // request movies flag
        boolean redraw = false;     // redraw movie cards flag

        for (Tuple<Integer, Integer, PreferenceControl.PreferenceTypes> entry : PREFERENCE_LIST) {
            int keyId = entry.getT1();
            String key = getString(keyId);
            Object setting = PreferenceControl.getSharedPreference(this, entry.getT3(), keyId, entry.getT2());
            Object current = PREFERENCES.get(key);

            if ((setting != null) && !setting.equals(current)) {
                if (key.equals(getString(R.string.pref_clear_cache_key))) {
                    // cache cleared, clear display & request again
                    mRecipeAdapter.clear();
                    mRecipeAdapter.notifyDataSetChanged();
                    request = true;
                }

                PREFERENCES.put(key, setting);
            }
        }
        PREFERENCE_UPDATED = false;
        if (request) {
            requestRecipesServer();
        }
    }

}
