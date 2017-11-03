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

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

import ie.ianbuttimer.bakingguru.R;

/**
 * Enum representing recipe type classifications
 */

public enum RecipeType implements Comparable<RecipeType>, Serializable {

    /* NOTE1: enum order & recipe_types/recipe_images array order must match
       NOTE2: when entries have common elements, longer entries should come first, e.g. cupcake before cake
     */
    FOOD, BREAD, BROWNIE, CUPCAKE, CAKE, COOKIES, DOUGHNUT, PIE;

    /**
     * Get the enum value corresponding to a name
     * @param context   The current context
     * @param name      Name to get enum for
     * @return  enum
     */
    public static RecipeType getType(Context context, String name) {
        String[] types = context.getResources().getStringArray(R.array.recipe_types);
        RecipeType type = FOOD;
        String lwrName = name.toLowerCase();
        for (int i = 1; i < types.length; i++) {    // start at ordinal 1 as food(0) is default
            if (lwrName.contains(types[i].toLowerCase())) {
                type = RecipeType.values()[i];
                break;
            }
        }
        return type;
    }

    /**
     * Get the drawable resource id corresponding to a type
     * @param context   The current context
     * @param type      Type to get drawable resource id for
     * @return  drawable resource id
     */
    public static @DrawableRes int getImage(Context context, RecipeType type) {
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.recipe_images);
        int resId = imgs.getResourceId(type.ordinal(), R.drawable.ic_food);

        imgs.recycle();

        return resId;
    }

    /**
     * Get the drawable resource id corresponding to this object
     * @param context   The current context
     * @return  drawable resource id
     */
    public @DrawableRes int getImage(Context context) {
        return getImage(context, this);
    }
}
