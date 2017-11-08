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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.parceler.Parcels;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.idling_resource.SimpleIdlingResource;

import static ie.ianbuttimer.bakingguru.ItemDetailFragment.ARG_STEP;
import static ie.ianbuttimer.bakingguru.ItemListActivity.ARG_ITEM;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity implements ItemDetailFragment.IItemDetail {

    private Parcelable mItem;       // recipe displayed
    private int mStep;              // current instruction step

    // espresso test related
    @Nullable
    private SimpleIdlingResource mIdlingResource;

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
        setContentView(R.layout.activity_item_detail);

        Bundle bundle = savedInstanceState;
        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();
        }
        mItem = bundle.getParcelable(ARG_ITEM);
        mStep = bundle.getInt(ARG_STEP);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            arguments.putParcelable(ARG_ITEM, mItem);
            arguments.putInt(ARG_STEP, mStep);

            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Intent getParentActivityIntent() {
        // required for back navigation
        Intent intent = super.getParentActivityIntent();
        intent.putExtra(ARG_ITEM, mItem);
        intent.putExtra(ARG_STEP, mStep);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_ITEM, mItem);
        outState.putInt(ARG_STEP, mStep);
    }

/////// IItemDetail implementation ///////


    @Override
    public void onStepChange(int step) {
        mStep = step;
    }

    @Override
    public void setData(Recipe recipe, int step) {
        mItem = Parcels.wrap(recipe);
        mStep = step;
    }
}
