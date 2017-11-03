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

package ie.ianbuttimer.bakingguru.bake;

import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Test class for Recipe class
 */
@RunWith(AndroidJUnit4.class)
public class RecipeTest extends ParcelTest {

    Recipe original;

    @Before
    public void setUp() throws Exception {
        original = new Recipe();
        original.setId(100);
        original.setName("Best recipe ever");
        original.setIngredients(new Ingredient[] {
                new Ingredient(1, "m", "m's"),
                new Ingredient(2, "x", "x's")
        });
        original.setSteps(new Step[] {
                new Step(1, "step 1", "how to do step 1", "http://step1/video", "http://step1/thumgnail"),
                new Step(2, "step 2", "how to do step 2", "http://step2/video", "http://step2/thumgnail"),
                new Step(3, "step 3", "how to do step 3", "http://step3/video", "http://step3/thumgnail")
        });
        original.setServings(25);
        original.setImage("imagine an image");
    }

    @Test
    public void recipeParcelableTest() {

        Parcelable wrapped = Parcels.wrap(original);

        Recipe unwrapped = Parcels.unwrap(wrapped);

        assertRecipes(original, unwrapped, "");
    }

    @Test
    public void recipeValuesTest() {
        String name = "banana CUPCAKES";
        original.setName(name);
        assertEquals(makeAssertMessage("Name value test"), original.getName(), name);
        assertEquals(makeAssertMessage("Type value test"), original.getType(), RecipeType.CUPCAKE);
    }

    /**
     * Assert two Step object are the same
     * @param obj1      First Step object
     * @param obj2      Second Step object
     * @param msgPrefix Prefix for error message
     */
    public void assertRecipes(Recipe obj1, Recipe obj2, String msgPrefix) {
        assertEquals(makeAssertMessage(msgPrefix + "Id"), obj1.getId(), obj2.getId());
        assertEquals(makeAssertMessage(msgPrefix + "Name"), obj1.getName(), obj2.getName());
        assertEquals(makeAssertMessage(msgPrefix + "Type"), obj1.getType(), obj2.getType());
        assertEquals(makeAssertMessage(msgPrefix + "Servings"), obj1.getServings(), obj2.getServings());
        assertEquals(makeAssertMessage(msgPrefix + "Image"), obj1.getImage(), obj2.getImage());
        assertArrayEquals(makeAssertMessage(msgPrefix + "Ingredients"), obj1.getIngredients(), obj2.getIngredients());
        assertArrayEquals(makeAssertMessage(msgPrefix + "Steps"), obj1.getSteps(), obj2.getSteps());
    }

}