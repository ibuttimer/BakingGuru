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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.parceler.Parcels;

import java.util.ArrayList;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.data.AsyncCallback;
import ie.ianbuttimer.bakingguru.data.DbCacheIntentService;
import ie.ianbuttimer.bakingguru.data.JsonStringReader;
import ie.ianbuttimer.bakingguru.data.adapter.IAdapterOnClickHandler;
import ie.ianbuttimer.bakingguru.data.adapter.RecipeAdapter;
import ie.ianbuttimer.bakingguru.data.provider.RecipeContentValues;
import ie.ianbuttimer.bakingguru.data.provider.RecipeCursorProcessor;
import ie.ianbuttimer.bakingguru.network.NetworkStatusReceiver;
import ie.ianbuttimer.bakingguru.network.NetworkUtils;
import ie.ianbuttimer.bakingguru.utils.ErrorTuple;
import ie.ianbuttimer.bakingguru.utils.PreferenceControl;
import ie.ianbuttimer.bakingguru.utils.ResponseHandler;
import ie.ianbuttimer.bakingguru.utils.UriUtils;
import ie.ianbuttimer.bakingguru.utils.Utils;

import static ie.ianbuttimer.bakingguru.data.DbCacheIntentService.BULK_INSERT_RECIPE;
import static ie.ianbuttimer.bakingguru.data.DbCacheIntentService.CV_ARRAY_EXTRA;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.CONTENT_URI;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.REQUEST_URL;

public abstract class AbstractRecipeListActivity extends AppCompatActivity {

    protected RecipeAdapter mRecipeAdapter;
    protected ArrayList<Recipe> mRecipeList;

    @Nullable protected NetworkStatusReceiver.NetworkStatusListener mNetworkStatus;

    /** Recipe list argument for instance state bundle */
    private static final String LIST_ARG = "recipe_list";
    /** Setup network status listener argument for instance state bundle */
    private static final String NETWORK_STATUS_ARG = "network_status";
    /** Setup network status listener argument for instance state bundle */
    protected static final String ERROR_MSG_ARG = "error_msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIST_ARG)) {
                mRecipeList = Parcels.unwrap(savedInstanceState.getParcelable(LIST_ARG));
            }
        }
        if (mRecipeList == null) {
            mRecipeList = new ArrayList<>();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNetworkStatusListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(LIST_ARG, Parcels.wrap(mRecipeList));
        outState.putBoolean(NETWORK_STATUS_ARG, (mNetworkStatus != null));
    }

    /**
     * Called when the view binding portion of onCreate has been completed
     * @param savedInstanceState    SAved instance state
     */
    protected void onCreateBound(Bundle savedInstanceState) {
        boolean setupListener = false;
        String errorMsg = null;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(NETWORK_STATUS_ARG)) {
                setupListener = savedInstanceState.getBoolean(NETWORK_STATUS_ARG);
            }
            if (savedInstanceState.containsKey(ERROR_MSG_ARG)) {
                errorMsg = savedInstanceState.getString(ERROR_MSG_ARG);
            }
        }

        if (mRecipeList.size() == 0) {
            boolean available = NetworkUtils.isInternetAvailable(this);
            if (!available && setupListener) {
                // internet not currently available & was listening for connection, so start listening again
                registerNetworkStatusListener();

                if (!TextUtils.isEmpty(errorMsg)) {
                    showErrorMessage(errorMsg); // redisplay error message
                }
            } else {
                requestRecipes();
            }
        }
    }

    /**
     * Setup the recycler view
     * @param recyclerView  Recycer view to configure
     */
    protected void setupRecyclerView(@NonNull RecyclerView recyclerView, IAdapterOnClickHandler clickHandler) {
        GridLayoutManager mLayoutManager =
                new GridLayoutManager(this, calcNumColumns(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // get adapter to responsible for linking data with the Views that display it
        mRecipeAdapter = new RecipeAdapter(mRecipeList, clickHandler);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        recyclerView.setAdapter(mRecipeAdapter);
    }

    /**
     * Calculate the number of columns for the Recycler view
     * @return  Number of columns to display
     */
    private int calcNumColumns() {
        int columns = 1;
        boolean isXLarge = Utils.isXLargeScreen(this);
        boolean isLarge = Utils.isLargeScreen(this);
        boolean isNormal = Utils.isNormalScreen(this);
        boolean isPortrait = Utils.isPotraitScreen(this);

        if (isXLarge) {
            if (isPortrait) {
                columns = 3;    // 2 columns on xlarge portrait
            } else {
                columns = 4;    // 3 columns on xlarge landscape
            }
        } else if (isLarge) {
            if (isPortrait) {
                columns = 2;    // 2 columns on large portrait
            } else {
                columns = 3;    // 3 columns on large landscape
            }
        } else if (isNormal) {
            if (!isPortrait) {
                columns = 2;    // 2 columns on normal landscape
            }
        }
        return columns;
    }

    /** Return the activity */
    protected abstract Activity getActivityContext();

    /** Hook to handle error in call response */
    protected void onCallError() {
        registerNetworkStatusListener();
    }

    /**
     * Async request/response handler for recipe lists
     */
    private AsyncCallback mProvider = new AsyncCallback() {
        @Override
        public Context getContext() {
            return getActivityContext();
        }

        @Override
        public void processQueryResponse(@Nullable QueryResultWrapper response) {
            Recipe[] recipes = null;
            boolean processResponse = true;

            if (response != null) {
                Cursor cursor = response.getCursorResult();
                recipes = RecipeCursorProcessor.processArray(cursor);

                if ((recipes != null) && (recipes.length == 0)) {
                    // nothing in db, request from server
                    requestRecipesServer();
                    processResponse = false;
                }
            }
            if (processResponse) {
                onRecipesResponse(recipes, 0, "");
            }
        }

        @Override
        public void processCallResponse(@Nullable CallResultWrapper response) {
            Recipe[] recipes = null;
            int msgId = 0;
            String errorMsg = "";
            boolean isError = true;
            if (response != null) {
                if (response.isString()) {
                    String json = response.getStringResult();
                    if (!TextUtils.isEmpty(json)) {
                        JsonStringReader<Recipe, Recipe.Loader> reader = new JsonStringReader<>(json, new Recipe.Loader());
                        recipes = reader.readArray();

                        boolean cache = PreferenceControl.getCachePreference(getContext());
                        if (cache) {
                            startDbCacheIntentService(BULK_INSERT_RECIPE, recipes);
                        }

                        isError = false;
                    } else {
                        msgId = R.string.no_response;
                    }
                } else if (response.isError()) {
                    ErrorTuple error = response.getErrorResult();
                    errorMsg = error.getErrorString();
                }
            }

            if (isError) {
                onCallError();
            } else {
                unregisterNetworkStatusListener();
            }
            onRecipesResponse(recipes, msgId, errorMsg);
        }
    };

    /**
     * Start the DbCacheIntentService
     *
     * @param action    Service action to perform
     * @param recipes   Recipes to save
     */
    private void startDbCacheIntentService(@NonNull String action, Recipe[] recipes) {
        if ((recipes != null) && (recipes.length > 0)) {
            int length = recipes.length;
            Intent intent = DbCacheIntentService.getLaunchIntent(this, action);
            ContentValues[] cvArray = new ContentValues[length];
            RecipeContentValues.Builder builder = RecipeContentValues.builder()
                    .setTimestamp();

            for (int i = 0; i < length; i++) {
                builder.setJson(recipes[i].toJson())
                        .setId(recipes[i].getId());
                cvArray[i] = builder.build();
            }

            intent.putExtra(CV_ARRAY_EXTRA, cvArray);
            this.startService(intent);
        }
    }

    /**
     * Response handler for recipe list response
     */
    private class RecipesResponseHandler extends ResponseHandler<Recipe[]> implements Runnable {
        RecipesResponseHandler(Activity activity, Recipe[] response, int errorId, String errorMsg) {
            super(activity, response, errorId, errorMsg);
        }

        @Override
        public void run() {
            super.run();
            setRecipeList(getResponse());

            String message = getMessageToDisplay();
            if (!TextUtils.isEmpty(message)) {
                // error to display
                showErrorMessage(message);
            }
        }
    }

    /**
     * Set recipe list
     * @param list  List to set
     */
    protected abstract void setRecipeList(Recipe[] list);

    /**
     * Handle a recipes response
     * @param response Response object
     */
    protected void onRecipesResponse(Recipe[] response, int msgId, String errorMsg) {
        hideRefreshInProgress();

        if (response == null) {
            response = new Recipe[0];
        }

        // ui updates need to be on ui thread
        Activity activity = getActivityContext();
        activity.runOnUiThread(new RecipesResponseHandler(activity, response, msgId, errorMsg));
    }

    /**
     * Request recipes from the server
     */
    protected void requestRecipesServer() {
        hideErrorMessage();
        showRefreshInProgress();
        mProvider.call(this, UriUtils.matchBakingUri(CONTENT_URI), CONTENT_URI, REQUEST_URL, getString(R.string.json_network_resource), null);
    }

    /**
     * Request recipes from the db
     */
    protected void requestRecipesDb() {
        hideErrorMessage();
        showRefreshInProgress();
        mProvider.query(this, UriUtils.matchBakingUri(CONTENT_URI), CONTENT_URI);
    }

    /**
     * Request recipes
     */
    protected void requestRecipes() {
        if (PreferenceControl.getCachePreference(this)) {
            requestRecipesDb();
        } else {
            requestRecipesServer();
        }
    }

    /**
     * Show refresh in progress indicator
     */
    protected abstract void showRefreshInProgress();

    /**
     * Hide refresh in progress indicator
     */
    protected abstract void hideRefreshInProgress();

    /**
     * Show error message after request failure
     */
    protected abstract void showErrorMessage(String message);

    /**
     * Hide error message
     */
    protected abstract void hideErrorMessage();

    /**
     * Class to request recipes on network connection event
     */
    private class ReconnectedNetworkStatusListener implements NetworkStatusReceiver.NetworkStatusListener {
        @Override
        public void onNetworkStatusChanged(boolean isConnected) {
            if (isConnected) {
                requestRecipesServer();
            }
        }
    }

    /**
     * Register a request recipes on network connection event listener
     */
    private void registerNetworkStatusListener() {
        if (mNetworkStatus == null) {
            mNetworkStatus = new ReconnectedNetworkStatusListener();
        }
        NetworkStatusReceiver.registerListener(mNetworkStatus);
    }

    /**
     * Unregister a request recipes on network connection event listener
     */
    private void unregisterNetworkStatusListener() {
        if (mNetworkStatus != null) {
            NetworkStatusReceiver.unregisterListener(mNetworkStatus);
        }
    }

}
