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
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;

import ie.ianbuttimer.bakingguru.bake.Ingredient;
import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.bake.Step;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test of the recipe listing activity ItemListActivity
 */
@RunWith(AndroidJUnit4.class)
public class ItemListActivityTest extends AbstractRecipeTest {


    @Rule
    public ActivityTestRule<ItemListActivity> mActivityTestRule = new ActivityTestRule<>(ItemListActivity.class, false, false);

    static Recipe sRecipe;

    @IdRes
    private static int sRecyclerId = R.id.rv_item_list;

    @Before
    public void setupTest() {
        sRecipe = sRecipes[0];
    }

    @Test
    public void recipeTest() {

        Intent intent = new Intent();
        Parcelable wrapped = Parcels.wrap(sRecipe);

        intent.putExtra(ItemListActivity.ARG_ITEM, wrapped);
        intent.putExtra(ItemListActivity.ARG_ITEM_ID, sRecipe.getId());

        mActivityTestRule.launchActivity(intent);

        Ingredient[] ingredients = sRecipe.getIngredients();

        ViewInteraction viewInteraction = onView(ViewMatchers.withId(sRecyclerId));
        // Check the items at each position have the correct data
        int length = ingredients.length;
        for (int i = 0; i < length; ++i) {
            viewInteraction.perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(hasDescendant(withText(ingredients[i].getIngredient()))))
                    .check(matches(hasDescendant(withText(ingredients[i].getMeasure()))));
        }

        Step[] steps = sRecipe.getSteps();
        length += steps.length;
        for (int i = ingredients.length, j = 0; i < length; ++i, ++j) {
            viewInteraction.perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(hasDescendant(withText(steps[j].getShortDescription()))));
        }

        onView(ViewMatchers.withId(sRecyclerId))
                .perform(RecyclerViewActions.actionOnItemAtPosition(length - 1, click()));

    }


}