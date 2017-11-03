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

package ie.ianbuttimer.bakingguru.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Enum to represent the screen mode the app is using
 */
@SuppressWarnings("unused")
public enum ScreenMode {

    NORMAL_PORTRAIT, NORMAL_LANDSCAPE, TWO_PANEL;

    /**
     * Get the current screen mode
     * @param landscapeTest     Landscape test result
     * @param twoPanelTest      Two panel test result
     * @return  screen mode
     */
    public static ScreenMode getScreenMode(boolean landscapeTest, boolean twoPanelTest) {
        ScreenMode mode = NORMAL_PORTRAIT;
        if (landscapeTest) {
            mode = NORMAL_LANDSCAPE;
        } else if (twoPanelTest) {
            mode = TWO_PANEL;
        }
        return mode;
    }

    /**
     * Get screen mode corresponding to an ordinal
     * @param ordinal   Ordinal of screen mode
     * @return  screen mode
     */
    public static ScreenMode getScreenMode(int ordinal) {
        ScreenMode mode = null;
        ScreenMode[] values = ScreenMode.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].ordinal() == ordinal) {
                mode = values[i];
                break;
            }
        }
        return mode;
    }

    /**
     * Save a ScreenMode to a bundle
     * @param bundle    Bundle to save to
     * @param key       Key to use
     * @param mode      ScreenMode to save
     */
    public static void saveToBundle(Bundle bundle, String key, ScreenMode mode) {
        bundle.putInt(key, mode.ordinal());
    }

    /**
     * Save this object to a bundle
     * @param bundle    Bundle to save to
     * @param key       Key to use
     */
    public void saveToBundle(Bundle bundle, String key) {
        saveToBundle(bundle, key, this);
    }

    /**
     * Get a ScreenMode from a bundle
     * @param bundle    Bundle to get object from
     * @param key       Key to use to retrieve object
     * @return  ScreenMode object or <code>null</code> if not found
     */
    public static @Nullable ScreenMode getFromBundle(Bundle bundle, String key) {
        ScreenMode mode = null;
        if (bundle.containsKey(key)) {
            mode = getScreenMode(bundle.getInt(key));
        }
        return mode;
    }

    public boolean isNormalMode() {
        return (this.equals(NORMAL_PORTRAIT) || this.equals(NORMAL_LANDSCAPE));
    }

    public boolean isPortraitMode() {
        return (this.equals(NORMAL_PORTRAIT));
    }

    public boolean isLandscapeMode() {
        return (this.equals(NORMAL_LANDSCAPE));
    }

    public boolean isTwoPanelMode() {
        return (this.equals(TWO_PANEL));
    }


}
