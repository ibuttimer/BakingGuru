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

import java.io.StringReader;
import java.util.List;


/**
 * Class to load data from a json string
 */
@SuppressWarnings("unused")
public class JsonStringReader<T, L extends ILoadable> extends AbstractReader<T, L> {

    private String json;        // json string

    /**
     * Default constructor
     * @param json      String to read
     * @param loader    Loader to read file
     */
    public JsonStringReader(String json, L loader) {
        super(loader);
        this.json = json;
    }

    /**
     * Read an object list
     * @return  New object list
     */
    private Object read(ReadType type) {
        Object result = null;

        StringReader stream = new StringReader(json);
        switch (type) {
            case LIST:
                result = readReaderList(stream);
                break;
            case ARRAY:
                result = readReaderArray(stream);
                break;
            case OBJECT:
                result = readReaderObject(stream);
                break;
        }
        return result;
    }

    /**
     * Read an object list
     * @return  New object list
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> readList() {
        return (List<T>) read(ReadType.LIST);
    }

    /**
     * Read an object array
     * @return  New object array
     */
    @Override
    @SuppressWarnings("unchecked")
    public T[] readArray() {
        return (T[]) read(ReadType.ARRAY);
    }

    /**
     * Read an object
     * @return  New object
     */
    @Override
    @SuppressWarnings("unchecked")
    public T readObject() {
        return (T) read(ReadType.OBJECT);
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
