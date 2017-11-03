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

import android.util.JsonReader;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import ie.ianbuttimer.bakingguru.data.ILoadable;

/**
 * Abstract base class for bake objects
 */
@SuppressWarnings("unused")
public abstract class AbstractBakeObject {

    /**
     * Returns the int value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  int value
     */
    public static int nextInt(JsonReader reader, int dfltValue) {
        int value = dfltValue;
        try {
            value = reader.nextInt();
        } catch (IllegalStateException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Returns the double value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  double value
     */
    public static double nextDouble(JsonReader reader, double dfltValue) {
        double value = dfltValue;
        try {
            value = reader.nextDouble();
        } catch (IllegalStateException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Returns the string value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  string value
     */
    public static String nextString(JsonReader reader, String dfltValue) {
        String value = dfltValue;
        try {
            value = reader.nextString();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Checks if an object is the correct type
     * @param obj   Object to check
     * @param clazz Expected class
     */
    protected void checkObject(Object obj, Class clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("Invalid null object");
        }
        if (obj.getClass() != clazz) {
            throw new IllegalArgumentException("Incorrect object type, expected "
                    + clazz.getName() + " but got "
                    + obj.getClass().getName());
        }
    }

    /**
     * Populate this object from the specified object
     * @param original    Object to copy from
     * @return  this object
     * @throws IllegalArgumentException if <code>original</code> is of incorrect type or null
     */
    public abstract void set(Object original) throws IllegalArgumentException;

    /**
     * Create a JSON string representation of this object
     * @return  JSON string
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Get a ILoadable instance corresponding to this object
     * @return  new ILoadable instance
     */
    public abstract ILoadable<? extends AbstractBakeObject> getLoader();

    enum ReadType { OBJECT, LIST, ARRAY }

    /**
     * Read an object from a JSON string
     * @param json  JSON string to read
     * @param type  Typr of object to read; one of {@link ReadType}
     * @return new object
     * @throws FileNotFoundException
     */
    protected Object read(String json, ReadType type) throws FileNotFoundException {
        Object result = null;

        // TODO static version of AbstractBakeObject.read() required to avoid unnecessary object creation

        ILoadable<? extends AbstractBakeObject> loader = getLoader();   // current reason it can't be static
        if (loader != null) {
            StringReader strReader = new StringReader(json);
            JsonReader jsonReader = null;
            try {
                jsonReader = new JsonReader(strReader);

                switch (type) {
                    case OBJECT:
                        result = loader.read(jsonReader);
                        break;
                    case LIST:
                        result = loader.readList(jsonReader);
                        break;
                    case ARRAY:
                        result = loader.readArray(jsonReader);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(strReader, jsonReader);
            }
        }
        return result;
    }

    /**
     * Read an object from a JSON string
     * @param json  JSON string to read
     * @param <T>   Type of object to return
     * @return  new object
     * @throws FileNotFoundException
     */
    public <T extends AbstractBakeObject> T read(String json) throws FileNotFoundException {
        return (T) read(json, ReadType.OBJECT);
    }

    /**
     * Read an object list from a JSON string
     * @param json  JSON string to read
     * @param <T>   Type of object to return
     * @return  new object list
     * @throws FileNotFoundException
     */
    public <T extends AbstractBakeObject> List<T> readList(String json) throws FileNotFoundException {
        return (List<T>) read(json, ReadType.OBJECT);
    }

    /**
     * Read an object array from a JSON string
     * @param json  JSON string to read
     * @param <T>   Type of object to return
     * @return  new object array
     * @throws FileNotFoundException
     */
    public <T extends AbstractBakeObject> T[] readArray(String json) throws FileNotFoundException {
        return (T[]) read(json, ReadType.OBJECT);
    }

    /**
     * Tidy up after json read
     * @param strReader     String reader
     * @param jsonReader    JSON reader
     */
    private void close(StringReader strReader, JsonReader jsonReader) {
        try {
            if (jsonReader != null) {
                jsonReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (strReader != null) {
            strReader.close();
        }
    }

}
