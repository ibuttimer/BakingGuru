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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.bake.Step;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Test to step through a recipe starting at selection from the MainActivity<br>
 * Also tests the ExoPlayer previous & fast forward buttons
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class StepThroughRecipeTest extends AbstractRecipeTest {

    private static final int RECIPE_IDX = 0;
    // length of video at https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4
    private static final int FIRST_STEP_DURATION_SEC = 11;
    private static final String FIRST_STEP_DURATION = "00:11";
    private static final String STEP_START_POSITION = "00:00";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void mainActivityTest2() {
        // click recipe in MainActivity
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv_item_list_mainA),
                        withParent(withId(R.id.fl_list_mainA)),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(RECIPE_IDX, click()));

        Recipe recipe = sRecipes[RECIPE_IDX];
        int stepIndex = 0;                          // index of current step
        int numSteps = recipe.getSteps().length;    // number of step
        Step step = recipe.getStep(stepIndex);
        int firstStepAdapterIndex = recipe.getIngredients().length;

        // click first step in ItemListActivity
        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv_item_list),
                        withParent(withId(R.id.fl_itemListA)),
                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(firstStepAdapterIndex, click()));

        // text the title
        testTitle(step.getShortDescription());

        // duration text
        ViewInteraction textViewDuration = onView(
                allOf(withId(R.id.exo_duration),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                2),
                        isDisplayed()));

        // pause to allow video to start to play
        // NOTE controls are hidden after 3 sec so has to be less than that
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // duration is correct
        textViewDuration.check(matches(withText(FIRST_STEP_DURATION)));

        // position shouldn't be at start
        // position text
        ViewInteraction textViewPosition = onView(
                allOf(withId(R.id.exo_position),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textViewPosition.check(matches(not(withText(not(STEP_START_POSITION)))));

        // click prev button
        Context appContext = InstrumentationRegistry.getTargetContext();
        String contentDesc = appContext.getString(R.string.exo_controls_previous_description);
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.exo_prev), withContentDescription(contentDesc), isDisplayed()));
        appCompatImageButton.perform(click());

        // position should be at start
        textViewPosition.check(matches(withText(STEP_START_POSITION)));

        // click fast forward button
        contentDesc = appContext.getString(R.string.exo_controls_fastforward_description);
        appCompatImageButton = onView(
                allOf(withId(R.id.exo_ffwd), withContentDescription(contentDesc), isDisplayed()));
        appCompatImageButton.perform(click());

        // position should be at end
        textViewPosition.check(matches(withText(FIRST_STEP_DURATION)));

        // text next/prev buttons
        int clicksToPerform = (numSteps * 2) - 2;
        for (int i = 0; i < clicksToPerform; ++i) {

            if (i < numSteps) {
                stepIndex = i % numSteps;
            } else {
                stepIndex = (numSteps - 1) - (i % numSteps) - 1;
            }
            System.out.println("Testing step buttons: " + stepIndex);

            // title
            step = recipe.getStep(stepIndex);
            testTitle(step.getShortDescription());

            ViewInteraction buttonNext = onView(
                    allOf(withId(R.id.b_next_stepA)));
            if (stepIndex < (numSteps - 1)) {
                buttonNext.check(matches(isDisplayed()))
                            .check(matches(withText(R.string.next_step)));
            } else {
                buttonNext.check(matches(not(isDisplayed())));
            }

            ViewInteraction buttonPrev = onView(
                    allOf(withId(R.id.b_previous_stepA)));
            if (stepIndex > 0) {
                buttonPrev.check(matches(isDisplayed()))
                            .check(matches(withText(R.string.previous_step)));
            } else {
                buttonPrev.check(matches(not(isDisplayed())));
            }

            // move to next/prev step
            if (i < (numSteps - 1)) {
                ViewInteraction appCompatButton = onView(
                        allOf(withId(R.id.b_next_stepA)));
                appCompatButton.perform(click());
            } else {
                ViewInteraction appCompatButton = onView(
                        allOf(withId(R.id.b_previous_stepA)));
                appCompatButton.perform(click());
            }
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void testTitle(String expected) {
        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_step_title_stepA),
                        childAtPosition(
                                allOf(withId(R.id.item_detail),
                                        childAtPosition(
                                                withId(R.id.item_detail_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText(expected)));
    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
