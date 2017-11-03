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
import java.io.Reader;
import java.util.List;

import timber.log.Timber;


/**
 * Class to load data from a json file
 */
@SuppressWarnings("unused")
public abstract class AbstractReader<T, L extends ILoadable> {

    protected L loader;           // loader to parse json

    /**
     * Default constructor
     * @param loader    Loader to read file
     */
    public AbstractReader(L loader) {
        this.loader = loader;
    }

    /**
     * Read an object list
     * @return  New object list
     */
    public abstract List<T> readList();

    enum ReadType { LIST, ARRAY, OBJECT };

    /**
     * Read an object list
     * @return  New object list
     */
    private Object read(Reader ioReader, ReadType type) {
        Object result = null;

        JsonReader reader = new JsonReader(ioReader);
        try {
            switch (type) {
                case LIST:
                    result = loader.readList(reader);
                    break;
                case ARRAY:
                    result = loader.readArray(reader);
                    break;
                case OBJECT:
                    result = loader.read(reader);
                    break;
            }
        } catch (IOException e) {
            Timber.e("JSON read error", e);
        } finally {
            close(ioReader, reader);
        }
        return result;
    }

    /**
     * Read an object list
     * @return  New object list
     */
    @SuppressWarnings("unchecked")
    protected List<T> readReaderList(Reader ioReader) {
        return (List<T>) read(ioReader, ReadType.LIST);
    }

    /**
     * Read an object array
     * @return  New object array
     */
    public abstract T[] readArray();

    /**
     * Read an object array
     * @param ioReader  Input reader
     * @return  New object array
     */
    @SuppressWarnings("unchecked")
    protected T[] readReaderArray(Reader ioReader) {
        return (T[]) read(ioReader, ReadType.ARRAY);
    }

    /**
     * Read an object
     * @return  New object
     */
    public abstract T readObject();

    /**
     * Read an object
     * @param ioReader  Input reader
     * @return  New object
     */
    @SuppressWarnings("unchecked")
    protected T readReaderObject(Reader ioReader) {
        return (T) read(ioReader, ReadType.OBJECT);
    }

    /**
     * Close and release
     * @param ioReader  Input reader
     * @param reader    json reader
     */
    protected void close(Reader ioReader, JsonReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
            if (ioReader != null) {
                ioReader.close();
            }
        } catch (IOException e) {
            Timber.e("Reader close error", e);
        }
    }
}
