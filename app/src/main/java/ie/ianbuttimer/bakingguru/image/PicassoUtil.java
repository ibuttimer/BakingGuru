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
package ie.ianbuttimer.bakingguru.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import ie.ianbuttimer.bakingguru.R;
import timber.log.Timber;


/**
 * This class contains miscellaneous image utility functions
 */

public class PicassoUtil {

    private static LruCache cache = null;

    /**
     * Private constructor
     */
    private PicassoUtil() {
        // can't instantiate class
    }

    /**
     * Get the global Picasso instance
     * @param context   The current context
     * @return Global Picasso instance
     */
    private static Picasso getPicasso(Context context) {
        if (cache == null) {
            Context appContext = context.getApplicationContext();
            cache = new LruCache(appContext);

            Picasso picasso = new Picasso.Builder(appContext)
                    .memoryCache(cache)
                    .build();
            Picasso.setSingletonInstance(picasso);
        }
        return Picasso.with(context);
    }

    /**
     * Loads an image into an ImageView
     * @param context   The current context
     * @param uri       URI for image
     * @param imageView ImageView to load into
     * @param callback  Callback to invoke when finished
     * @return Request tag
     * <p>
     * <b>Note: </b>The Callback param is a strong reference and will prevent your Activity or Fragment from being garbage collected. If you use this method, it is strongly recommended you invoke an adjacent <a href="https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/Picasso.html#cancelRequest-android.widget.ImageView-">Picasso.cancelRequest(ImageView)</a> call to prevent temporary leaking.
     * </p>
     * @see <a href="https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/RequestCreator.html#into-android.widget.ImageView-com.squareup.picasso.Callback-">Pacasso.into(ImageView, Callback)</a>
     */
    public static String loadImage(Context context, Uri uri, ImageView imageView, Callback callback){
        RequestCreator request = getPicasso(context)
                .load(uri)
                .placeholder(R.drawable.download_from_cloud)
                .error(R.drawable.no_image_available);
        String tag = makeRequestTag(uri);
        request.tag(tag);
        if (callback != null) {
            request.into(imageView, callback);
        } else {
            request.into(imageView);
        }
        return tag;
    }

    /**
     * Loads an image into an ImageView
     * @param context       The current context
     * @param resourceId    Drawable resource ID
     * @param imageView     ImageView to load into
     * @param callback      Callback to invoke when finished
     * @return Request tag
     * <p>
     * <b>Note: </b>The Callback param is a strong reference and will prevent your Activity or Fragment from being garbage collected. If you use this method, it is strongly recommended you invoke an adjacent <a href="https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/Picasso.html#cancelRequest-android.widget.ImageView-">Picasso.cancelRequest(ImageView)</a> call to prevent temporary leaking.
     * </p>
     * @see <a href="https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/RequestCreator.html#into-android.widget.ImageView-com.squareup.picasso.Callback-">Pacasso.into(ImageView, Callback)</a>
     */
    public static String loadImage(Context context, @DrawableRes int resourceId, ImageView imageView, Callback callback){
        RequestCreator request = getPicasso(context)
                .load(resourceId)
                .placeholder(R.drawable.download_from_cloud)
                .error(R.drawable.no_image_available);
        String tag = makeRequestTag(resourceId);
        request.tag(tag);
        if (callback != null) {
            request.into(imageView, callback);
        } else {
            request.into(imageView);
        }
        return tag;
    }

    /**
     * Cancel any existing requests for the specified target ImageView
     * @param context   The current context
     * @param imageView ImageView to load into
     */
    public static void cancelImageLoad(Context context, ImageView imageView){
        getPicasso(context).cancelRequest(imageView);
    }

    /**
     * Cancel any existing requests for the specified target ImageView
     * @param context   The current context
     * @param tag       Tag to cancel
     */
    public static void cancelTag(Context context, String tag){
        getPicasso(context).cancelTag(tag);
    }

    /**
     * Pause download any existing requests for the specified target ImageView
     * @param context   The current context
     * @param tag       Tag to cancel
     */
    public static void pauseTag(Context context, String tag){
        getPicasso(context).pauseTag(tag);
    }

    /**
     * Resume download any existing requests for the specified target ImageView
     * @param context   The current context
     * @param tag       Tag to cancel
     */
    public static void resumeTag(Context context, String tag){
        getPicasso(context).resumeTag(tag);
    }

    /**
     * Gets an image
     * @param context   The current context
     * @param uri       URI for image
     * @param callback  Callback to invoke when finished
     * @return Request tag
     * <p>
     * <b>Note: </b>The Callback param is a strong reference and will prevent your Activity or Fragment from being garbage collected. If you use this method, it is strongly recommended you invoke an adjacent <a href="https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/Picasso.html#cancelRequest-android.widget.ImageView-">Picasso.cancelRequest(ImageView)</a> call to prevent temporary leaking.
     * </p>
     * @see <a href="http://square.github.io/picasso/2.x/picasso/com/squareup/picasso/RequestCreator.html#fetch-android.widget.ImageView-com.squareup.picasso.Callback-">Pacasso.into(ImageView, Callback)</a>
     */
    public static String fetchImage(Context context, Uri uri, Callback callback){
        RequestCreator request = getPicasso(context)
                .load(uri);
        String tag = makeRequestTag(uri);
        request.tag(tag);
        request.fetch(callback);
        return tag;
    }

    /**
     * Make a request tag
     * @param uri       URI for image
     * @return Request tag
     */
    public static String makeRequestTag(Uri uri) {
        // the uri is used as the key for the cache
        return uri.toString() + "\n";   // picasso appends a lf
    }

    /**
     * Make a request tag
     * @param resourceId    Drawable resource ID
     * @return Request tag
     */
    public static String makeRequestTag(@DrawableRes int resourceId) {
        // the uri is used as the key for the cache
        return String.valueOf(resourceId) + "\n";   // picasso appends a lf
    }

    /**
     * Get an image from the cache
     * @param uri   Uri of image to get
     * @return  Image bitmap
     */
    public static Bitmap getImage(Uri uri) {
        Bitmap image = null;
        if (uri != null) {
            image = getImage(makeRequestTag(uri));
        }
        return image;
    }

    /**
     * Get an image from the cache
     * @param key   Key for image to get
     * @return  Image bitmap
     */
    public static Bitmap getImage(String key) {
        Bitmap image = null;
        if ((cache != null) && !TextUtils.isEmpty(key)) {
            image = cache.get(key);
        }
        if (image != null) {
            Timber.d("Loaded from image cache: " + key);
        }
        return image;
    }

}
