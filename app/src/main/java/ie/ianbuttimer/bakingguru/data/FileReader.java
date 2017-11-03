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

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;


/**
 * Class to load data from a json file
 */
@SuppressWarnings("unused")
public class FileReader<T, L extends ILoadable> extends AbstractReader<T, L> {

    private WeakReference<Context> context;
    private String path;        // path to file

    /**
     * Default constructor
     * @param context   The current context
     * @param path      Path to file to read
     * @param loader    Loader to read file
     */
    public FileReader(Context context, String path, L loader) {
        super(loader);
        this.context = new WeakReference<>(context);
        this.path = path;
    }

    /**
     * Read an object list
     * @return  New object list
     */
    private Object read(ReadType type) {
        Object result = null;

        InputStream stream = null;
        InputStreamReader in;
        try {
            stream = context.get().getAssets().open(path);
            in = new InputStreamReader(stream);

            switch (type) {
                case LIST:
                    result = readReaderList(in);
                    break;
                case ARRAY:
                    result = readReaderArray(in);
                    break;
                case OBJECT:
                    result = readReaderObject(in);
                    break;
            }
        } catch (FileNotFoundException e) {
            Timber.e("File unavailable: " + path, e);
        } catch (IOException e) {
            Timber.e("IO error", e);
        } finally {
            close(stream);
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

    /**
     * Close and release
     * @param stream    file input stream
     */
    protected void close(InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Timber.e("Stream close error", e);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
