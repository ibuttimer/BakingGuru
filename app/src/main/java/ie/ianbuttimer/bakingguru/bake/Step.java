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

import android.text.TextUtils;
import android.util.JsonReader;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ie.ianbuttimer.bakingguru.data.ILoadable;

/**
 * Class representing a recipe step
 */
@SuppressWarnings("unused")
@Parcel
public class Step extends AbstractBakeObject {

    public static final String ID_KEY = "id";
    public static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    public static final String DESCRIPTION_KEY = "description";
    public static final String VIDEO_URL_KEY = "videoURL";
    public static final String THUMBNAIL_URL_KEY = "thumbnailURL";

    public static final int INVALID_ID = -1;

    private enum URL_TYPE { UNKNOWN, EMPTY, VIDEO, IMAGE };

    int id;
    String shortDescription;
    String description;
    String videoURL;
    String thumbnailURL;

    /**
     * Default constructor
     */
    public Step() {
        id = INVALID_ID;
        shortDescription = "";
        description = "";
        videoURL = "";
        thumbnailURL = "";
    }

    /**
     * Constructor
     * @param id                Step id
     * @param shortDescription  Short description
     * @param description       Description
     * @param videoURL          Video url
     * @param thumbnailURL      Thumbnail url
     */
    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    /**
     * Read a Step object from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static Step readStep(JsonReader reader) throws IOException {
        Step step = new Step();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(ID_KEY)) {
                step.id = nextInt(reader, 0);
            } else if (name.equals(SHORT_DESCRIPTION_KEY)) {
                step.shortDescription = nextString(reader, "");
            } else if (name.equals(DESCRIPTION_KEY)) {
                step.description = nextString(reader, "");
            } else if (name.equals(VIDEO_URL_KEY)) {
                step.videoURL = nextString(reader, "");
            } else if (name.equals(THUMBNAIL_URL_KEY)) {
                step.thumbnailURL = nextString(reader, "");
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return sanityCheck(step);
    }

    /**
     * Read a Step object list from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static List<Step> readStepsList(JsonReader reader) throws IOException {
        List<Step> list = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(readStep(reader));
        }
        reader.endArray();
        return list;
    }

    /**
     * Read a Step object array from the specified reader
     * @param reader    Reader to read object from
     * @return  new Step object
     * @throws IOException
     */
    public static Step[] readStepsArray(JsonReader reader) throws IOException {
        List<Step> list = readStepsList(reader);
        return list.toArray(new Step[list.size()]);
    }

    /**
     * Perform a sanity check on a Step object, and corrects any errors
     * @param step    Object to check
     * @return  object
     */
    public static Step sanityCheck(Step step) {
        URL_TYPE video = checkUrl(step.videoURL);
        URL_TYPE image = checkUrl(step.thumbnailURL);
        if (((video == URL_TYPE.IMAGE) && (image == URL_TYPE.VIDEO))
                || ((video == URL_TYPE.EMPTY) && (image == URL_TYPE.VIDEO))
                || ((video == URL_TYPE.IMAGE) && (image == URL_TYPE.EMPTY))) {
            // swap video & image urls
            String temp = step.videoURL;
            step.videoURL = step.thumbnailURL;
            step.thumbnailURL = temp;
        }
        return step;
    }

    /**
     * Check the specified url to see what it represents
     * @param url   Url to check
     * @return  Tuple object where
     * <ul>
     *     <li>T1: - <code>true</code> if url is empty</li>
     *     <li>T2: - <code>true</code> if url represents a video</li>
     *     <li>T3: - <code>true</code> if url represents an image</li>
     * </ul>
     */
    private static URL_TYPE checkUrl(String url) {
        String stdUrl = url.toLowerCase().trim();
        URL_TYPE type;
        if (TextUtils.isEmpty(url)) {
            type = URL_TYPE.EMPTY;
        } else if (stdUrl.endsWith(".mp4")) {
            type = URL_TYPE.VIDEO;
        } else if (stdUrl.endsWith(".png")) {
            type = URL_TYPE.IMAGE;
        } else {
            type = URL_TYPE.UNKNOWN;;
        }
        return type;
    }

    @Override
    public void set(Object original) throws IllegalArgumentException {
        checkObject(original, Step.class);

        Step from = (Step) original;

        this.id = from.id;
        this.shortDescription = from.shortDescription;
        this.description = from.description;
        this.videoURL = from.videoURL;
        this.thumbnailURL = from.thumbnailURL;
    }

    @Override
    public ILoadable<Step> getLoader() {
        return new Loader();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (id != step.id) return false;
        if (shortDescription != null ? !shortDescription.equals(step.shortDescription) : step.shortDescription != null)
            return false;
        if (description != null ? !description.equals(step.description) : step.description != null)
            return false;
        if (videoURL != null ? !videoURL.equals(step.videoURL) : step.videoURL != null)
            return false;
        return thumbnailURL != null ? thumbnailURL.equals(step.thumbnailURL) : step.thumbnailURL == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (shortDescription != null ? shortDescription.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (videoURL != null ? videoURL.hashCode() : 0);
        result = 31 * result + (thumbnailURL != null ? thumbnailURL.hashCode() : 0);
        return result;
    }

    /**
     * Loader class for Steps
     */
    public class Loader implements ILoadable<Step> {

        @Override
        public Step read(JsonReader reader) throws IOException {
            return readStep(reader);
        }

        @Override
        public List<Step> readList(JsonReader reader) throws IOException {
            return readStepsList(reader);
        }

        @Override
        public Step[] readArray(JsonReader reader) throws IOException {
            return readStepsArray(reader);
        }

        @Override
        public Step newInstance() {
            return new Step();
        }
    }
}
