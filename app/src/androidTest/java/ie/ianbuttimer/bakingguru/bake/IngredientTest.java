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

/**
 * Test class for Ingredient class
 */
@RunWith(AndroidJUnit4.class)
public class IngredientTest extends ParcelTest {

    Ingredient original;

    @Before
    public void setUp() throws Exception {
        original = new Ingredient();
        original.setQuantity(100);
        original.setMeasure("Some measurement unit");
        original.setIngredient("Ingredient name");
    }

    @Test
    public void ingredientParcelableTest() {

        Parcelable wrapped = Parcels.wrap(original);

        Ingredient unwrapped = Parcels.unwrap(wrapped);

        assertIngredients(original, unwrapped, "");
    }


    /**
     * Assert two Ingredient object are the same
     * @param obj1      First Ingredient object
     * @param obj2      Second Ingredient object
     * @param msgPrefix Prefix for error message
     */
    public void assertIngredients(Ingredient obj1, Ingredient obj2, String msgPrefix) {
        assertEquals(makeAssertMessage(msgPrefix + "Quantity"), obj1.getQuantity(), obj2.getQuantity());
        assertEquals(makeAssertMessage(msgPrefix + "Measure"), obj1.getMeasure(), obj2.getMeasure());
        assertEquals(makeAssertMessage(msgPrefix + "Ingredient"), obj1.getIngredient(), obj2.getIngredient());
    }
}