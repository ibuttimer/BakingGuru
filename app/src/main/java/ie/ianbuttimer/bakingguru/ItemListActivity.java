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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ahamed.multiviewadapter.ItemViewHolder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ie.ianbuttimer.bakingguru.bake.Ingredient;
import ie.ianbuttimer.bakingguru.bake.IngredientsStep;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.bake.Step;
import ie.ianbuttimer.bakingguru.data.adapter.AdapterOnClickHandlerImpl;
import ie.ianbuttimer.bakingguru.data.adapter.RecipeItemsAdapter;
import ie.ianbuttimer.bakingguru.data.adapter.StepAdapter;
import ie.ianbuttimer.bakingguru.idling_resource.SimpleIdlingResource;
import ie.ianbuttimer.bakingguru.utils.ITester;
import ie.ianbuttimer.bakingguru.utils.ScreenMode;
import ie.ianbuttimer.bakingguru.utils.Utils;

import static ie.ianbuttimer.bakingguru.ItemDetailFragment.ARG_STEP;
import static ie.ianbuttimer.bakingguru.bake.IngredientsStep.INGREDIENTS_STEP_ID;
import static ie.ianbuttimer.bakingguru.data.adapter.AbstractBakeRecycleViewAdapter.DEFAULT_TAG_KEY;


/**
 * An activity representing a recipe listing. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements ItemDetailFragment.IItemDetail {

    /** The fragment argument representing the item ID that this fragment represents.*/
    public static final String ARG_ITEM_ID = "item_id";

    /** The fragment argument representing the item that this fragment represents. */
    public static final String ARG_ITEM = "item";

    private Recipe mRecipe;
    private int mStep;

    @BindView(R.id.pb_itemListA) ProgressBar mProgressBar;
    @BindView(R.id.rv_item_list) RecyclerView mRecyclerView;
    @Nullable @BindView(R.id.item_detail_container) View mDetailContainer;
    @Nullable @BindView(R.id.rv_ingredient_list_item_detail) RecyclerView mIngredientsRecyclerView;

    private Unbinder mUnbinder;

    private RecyclerView.Adapter mAdapter;

    private ScreenMode mScreenMode;

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
        setContentView(R.layout.activity_item_list);

        // Get the IdlingResource instance
        getIdlingResource();

        mUnbinder = ButterKnife.bind(this);

        mScreenMode = ScreenMode.getScreenMode(
                false,                          // no normal landscape test
                (mDetailContainer != null));    // detail container layout only available in 2 panel mode

        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        mRecipe = Parcels.unwrap(bundle.getParcelable(ARG_ITEM));
        mStep = 0;

        String name = mRecipe.getName();
        setTitle(name);

        setupRecyclerView(mRecyclerView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mScreenMode.isTwoPanelMode() && isChangingConfigurations()) {
            /* remove detail fragment as after the orientation change, app will go back to
                just the list, and don't want the fragment being recreated when there is nowhere
                to display it (also don't want the media player playing in the background)
             */
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.item_detail_container);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }

        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_ITEM, Parcels.wrap(mRecipe));
        outState.putInt(ARG_ITEM_ID, mRecipe.getId());
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

    /**
     * Setup the recycler view
     * @param recyclerView  View to setup
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        GridLayoutManager mLayoutManager =
                new GridLayoutManager(this, calcNumColumns(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // get adapter to responsible for linking data with the Views that display it
        if (mScreenMode.isTwoPanelMode()) {
            ArrayList<Step> stepsList = new ArrayList<>();

            IngredientsStep ingredientsStep = new IngredientsStep(INGREDIENTS_STEP_ID, getString(R.string.ingredient_step_desc));
            ingredientsStep.setIngredients(mRecipe.getIngredients());

            // add ingredients as 1st step
            stepsList.add(ingredientsStep);
            stepsList.addAll(Arrays.asList(mRecipe.getSteps()));

            // make recipe with extra step for tag
            Recipe tagRecipe = new Recipe();
            tagRecipe.set(mRecipe);
            tagRecipe.setSteps(stepsList.toArray(new Step[stepsList.size()]));

            StepAdapter stepAdapter = new StepAdapter(stepsList, new TwoPaneStepClickHandler());
            // add the recipe object as the tag for use by click listener
            stepAdapter.setCommonTag(DEFAULT_TAG_KEY, tagRecipe);

            mAdapter = stepAdapter;
        } else {
            // in single pane mode, ingredients & steps as listed in same recycler view
            RecipeItemsAdapter recipeItemsAdapter = new RecipeItemsAdapter();

            RecipeItemsAdapter.RecyclerAdapterControl<Ingredient> ingredientCtrl =
                    recipeItemsAdapter.getIngredientCtrl();
            ingredientCtrl.addAll(mRecipe.getIngredients());

            RecipeItemsAdapter.RecyclerAdapterControl<Step> stepCtrl =
                    recipeItemsAdapter.getStepCtrl();
            // add the recipe object as the tag for use by click listener
            stepCtrl.setTag(DEFAULT_TAG_KEY, mRecipe);

            stepCtrl.setClickListener(new ItemViewHolder.OnItemClickListener<Step>() {
                @Override
                public void onItemClick(View view, Step item) {
                    hsndleOnePaneStepClick(view, item);
                }
            });

            stepCtrl.addAll(mRecipe.getSteps());

            mAdapter = recipeItemsAdapter;
        }
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Process a click on a list item in two panel mode
     * @param view  View that was clicked
     * @param item  Object associated with the view
     */
    private void hsndleTwoPanelStepClick(View view, Step item) {
        Parcelable wrapped = Parcels.wrap(view.getTag(DEFAULT_TAG_KEY));
        final int itemId = item.getId();

        StepAdapter stepAdapter = (StepAdapter) mAdapter;
        mStep = stepAdapter.findItemIndex(new ITester<Step>() {
            @Override
            public boolean test(Step obj) {
                return (obj.getId() == itemId);
            }
        });

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ITEM, wrapped);
        arguments.putInt(ARG_STEP, mStep);

        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    /**
     * Process a click on a list item in one (normal) panel mode
     * @param view  View that was clicked
     * @param item  Object associated with the view
     */
    private void hsndleOnePaneStepClick(View view, Step item) {
        Parcelable wrapped = Parcels.wrap(view.getTag(DEFAULT_TAG_KEY));
        mStep = item.getId();

        Context context = view.getContext();
        Intent intent = new Intent(context, ItemDetailActivity.class);

        intent.putExtra(ARG_ITEM, wrapped);
        intent.putExtra(ARG_STEP, mStep);

        Utils.startActivity(context, intent);
    }


    /**
     * Calculate the number of columns for the Recycler view
     * @return  Number of columns to display
     */
    private int calcNumColumns() {
        int columns = 1;
//        if (mScreenMode.isNormalMode()) {
//            boolean isXLarge = Utils.isXLargeScreen(this);
//            boolean isLarge = Utils.isLargeScreen(this);
//            boolean isPortrait = Utils.isPotraitScreen(this);
//
//            if (isXLarge) {
//                if (isPortrait) {
//                    columns = 2;    // 2 columns on xlarge portrait
//                } else {
//                    columns = 3;    // 3 columns on xlarge landscape
//                }
//            } else if (isLarge) {
//                if (!isPortrait) {
//                    columns = 2;    // 2 columns on large landscape
//                }
//            }
//        }
        return columns;
    }


    ////// Implementation of ItemDetailFragment.IItemDetail //////

    @Override
    public void onStepChange(int step) {
        mStep = step;
        if (mScreenMode.isTwoPanelMode()) {
            // update selected step
            for (int i = 0; i < mAdapter.getItemCount(); ++i) {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.itemView.setSelected(i == step);
                }
            }
        }
    }

    @Override
    public void setData(Recipe recipe, int step) {
        // no op
    }


    private class TwoPaneStepClickHandler extends AdapterOnClickHandlerImpl<Step> {
        @Override
        public void onItemClick(View view, Step item) {
            hsndleTwoPanelStepClick(view, item);
        }
    }

}
