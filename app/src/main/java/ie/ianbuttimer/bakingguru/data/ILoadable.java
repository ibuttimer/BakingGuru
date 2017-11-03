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

package ie.ianbuttimer.bakingguru.data;

import android.util.JsonReader;

import java.io.IOException;
import java.util.List;

/**
 * An interface for objects that may be loaded from a json file
 */
@SuppressWarnings("unused")
public interface ILoadable<T> {

    /**
     * Read an object object from the specified reader
     * @param reader    Reader to read object from
     * @return  new object
     * @throws IOException
     */
    T read(JsonReader reader) throws IOException;

    /**
     * Read an object list from the specified reader
     * @param reader    Reader to read object from
     * @return  new object
     * @throws IOException
     */
    List<T> readList(JsonReader reader) throws IOException;

    /**
     * Read an object array from the specified reader
     * @param reader    Reader to read object from
     * @return  new object
     * @throws IOException
     */
    T[] readArray(JsonReader reader) throws IOException;

    /**
     * Create a new instance
     * @return  New object
     */
    T newInstance();
}
