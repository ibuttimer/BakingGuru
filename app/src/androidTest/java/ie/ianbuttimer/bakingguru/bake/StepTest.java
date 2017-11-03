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
 * Test class for Step class
 */
@RunWith(AndroidJUnit4.class)
public class StepTest extends ParcelTest {

    Step original;

    @Before
    public void setUp() throws Exception {
        original = new Step();
        original.setId(100);
        original.setShortDescription("THis is a short description");
        original.setDescription("THis is a long description");
        original.setVideoURL("http://video/url");
        original.setThumbnailURL("http://thumbnail/url");
    }

    @Test
    public void stepParcelableTest() {

        Parcelable wrapped = Parcels.wrap(original);

        Step unwrapped = Parcels.unwrap(wrapped);

        assertSteps(original, unwrapped, "");
    }


    /**
     * Assert two Step object are the same
     * @param obj1      First Step object
     * @param obj2      Second Step object
     * @param msgPrefix Prefix for error message
     */
    public void assertSteps(Step obj1, Step obj2, String msgPrefix) {
        assertEquals(makeAssertMessage(msgPrefix + "Id"), obj1.getId(), obj2.getId());
        assertEquals(makeAssertMessage(msgPrefix + "ShortDescription"), obj1.getShortDescription(), obj2.getShortDescription());
        assertEquals(makeAssertMessage(msgPrefix + "Description"), obj1.getDescription(), obj2.getDescription());
        assertEquals(makeAssertMessage(msgPrefix + "VideoURL"), obj1.getVideoURL(), obj2.getVideoURL());
        assertEquals(makeAssertMessage(msgPrefix + "ThumbnailURL"), obj1.getThumbnailURL(), obj2.getThumbnailURL());
    }
}