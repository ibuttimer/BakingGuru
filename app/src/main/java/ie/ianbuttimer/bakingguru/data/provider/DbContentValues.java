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

import static android.provider.BaseColumns._ID;

/**
 * Builder class for a baking ContentValue objects
 */
@SuppressWarnings("unused")
public class DbContentValues {

    public static class Builder {

        protected ContentValues cv;

        /**
         * Constructor
         */
        Builder() {
            cv = new ContentValues();
        }

        /**
         * Build a Movie ContentValue object
         * @return ContentValue object
         */
        public ContentValues build() {
            return new ContentValues(cv);
        }

        /**
         * Set the id field
         * @param id    Id to set
         * @return  Builder to facilitate chaining
         */
        public Builder setId(int id) {
            if (id > 0) {
                cv.put(_ID, id);
            }
            return this;
        }

        /**
         * Clear the builder contents
         * @return  Builder to facilitate chaining
         */
        public Builder clear() {
            cv.clear();
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

}
