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

package ie.ianbuttimer.bakingguru.data.provider;

import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

import ie.ianbuttimer.bakingguru.bake.Recipe;
import ie.ianbuttimer.bakingguru.utils.DbUtils;

import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.COLUMN_JSON;
import static ie.ianbuttimer.bakingguru.data.db.BakingContract.RecipeEntry.COLUMN_TIMESTAMP;

/**
 * Builder class for Recipe ContentValue objects
 */
@SuppressWarnings("unused")
public class RecipeContentValues extends DbContentValues {

    public static class Builder extends DbContentValues.Builder {

        /**
         * Constructor
         */
        Builder() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Builder setId(int id) {
            super.setId(id);
            return this;
        }

        /**
         * Set the JSON string
         * @param json  Json string to set
         * @return  Builder to facilitate chaining
         */
        public Builder setJson(String json) {
            if (!TextUtils.isEmpty(json)) {
                cv.put(COLUMN_JSON, json);
            }
            return this;
        }

        /**
         * Set the timestamp to the current date & time
         * @return  Builder to facilitate chaining
         */
        public Builder setTimestamp() {
            return setTimestamp(new Date());
        }

        /**
         * Set the timestamp to the specified date & time
         * @param timestamp     Timestamp to set
         * @return  Builder to facilitate chaining
         */
        public Builder setTimestamp(Date timestamp) {
            cv.put(COLUMN_TIMESTAMP, DbUtils.getTimestamp(timestamp));
            return this;
        }

        @Override
        public Builder clear() {
            super.clear();
            return this;
        }
    }

    /**
     * Get a builder instance
     * @return  New builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Build an array of recipes ContentValues
     * @param recipes   Recipe array
     * @return  ContentValues array or <code>null</code> if error
     */
    @Nullable public static ContentValues[] buildArray(Recipe[] recipes) {
        ContentValues[] cvArray = null;
        if ((recipes != null) && (recipes.length > 0)) {
            int length = recipes.length;
            cvArray = new ContentValues[length];
            RecipeContentValues.Builder builder = RecipeContentValues.builder()
                    .setTimestamp();

            for (int i = 0; i < length; i++) {
                builder.setJson(recipes[i].toJson())
                        .setId(recipes[i].getId());
                cvArray[i] = builder.build();
            }
        }
        return cvArray;
    }

}
