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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;

import static ie.ianbuttimer.bakingguru.utils.UriUtils.urlToUri;


/**
 * Image loading utility class
 */
@SuppressWarnings("unused")
public abstract class AbstractImageLoader<T extends AbstractBakeObject> implements Callback {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Callback callback;
    private Context context;
    private String tag;             // tag used in image cache

    /**
     * Default constructor
     */
    public AbstractImageLoader() {
        init();
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     * @param progressBar   In progress bar
     */
    public AbstractImageLoader(ImageView imageView, ProgressBar progressBar) {
        init();
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    /**
     * Initialise internal state
     */
    private void init() {
        this.imageView = null;
        this.progressBar = null;
        clear();
    }

    /**
     * Clear the download state
     */
    private void clear() {
        this.callback = null;
        this.context = null;
        this.tag = "";
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     */
    public AbstractImageLoader(ImageView imageView) {
        this(imageView, null);
    }

    /**
     * Get an image Uri
     * @param path  Image url string
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public static @Nullable Uri getImageUri(String path) {
		return urlToUri(path);
    }

    /**
     * Get a image Uri
     * @param obj     Object to get Uri for
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public @Nullable Uri getImageUri(T obj) {
        return getImageUri(getImagePath(obj));
    }

    /**
     * Get a image path
     * @param obj     Object to get path for
     */
    public abstract String getImagePath(T obj);

    /**
     * Return the ImageView for this object
     * @return  ImageView object
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Set the ImageView for this object
     * @param imageView ImageView to set
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Return the ProgressBar for this object
     * @return ProgressBar object
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Set the ProgressBar for this object
     * @param progressBar   ProgressBar to set
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param obj       Object to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String loadImage(Context context, T obj, Callback callback) {
        Uri uri = getImageUri(obj);
        if (uri != null) {
            start(context, callback);
            tag = PicassoUtil.loadImage(context, uri, imageView, this);
        } else {
            tag = "";
        }
        return tag;
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param obj       Object to get image for
     * @return Request tag
     */
    public String loadImage(Context context, T obj) {
        return loadImage(context, obj, null);
    }

    /**
     * Loads a image into an ImageView
     * @param context       The current context
     * @param resourceId    Drawable resource ID
     * @param callback      Callback to invoke when finished
     * @return Request tag
     */
    public String loadImage(Context context, @DrawableRes int resourceId, Callback callback) {
        start(context, callback);
        tag = PicassoUtil.loadImage(context, resourceId, imageView, this);
        return tag;
    }

    /**
     * Loads a image into an ImageView
     * @param context       The current context
     * @param resourceId    Drawable resource ID
     * @return Request tag
     */
    public String loadImage(Context context, @DrawableRes int resourceId) {
        return loadImage(context, resourceId, null);
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param obj       Object to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String fetchImage(Context context, T obj, Callback callback) {
        Uri uri = getImageUri(obj);
        start(context, callback);
        tag = PicassoUtil.fetchImage(context, uri, this);
        return tag;
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param obj       Object to get image for
     * @return Request tag
     */
    public String fetchImage(Context context, T obj) {
        return fetchImage(context, obj, null);
    }

    /**
     * Gets an image from cache
     * @param obj       Object to get image for
     * @return Request tag
     */
    public Bitmap getImage(T obj) {
        Uri uri = getImageUri(obj);
        return PicassoUtil.getImage(uri);
    }

    /**
     * Cancel any existing requests for the target ImageView
     */
    public void cancelImageLoad() {
        if (context != null) {
            PicassoUtil.cancelImageLoad(context, imageView);
        }
        end();
    }

    /**
     * Cancel any existing requests for the request tag
     */
    public void cancel() {
        if (context != null) {
            PicassoUtil.cancelTag(context, tag);
        }
        end();
    }

    /**
     * Pause any existing requests for the request tag
     */
    public void pause() {
        PicassoUtil.pauseTag(context, tag);
    }

    /**
     * Resume any existing requests for the request tag
     */
    public void resume() {
        PicassoUtil.resumeTag(context, tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess() {
        if (callback != null) {
            callback.onSuccess();
        }
        end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError() {
        if (callback != null) {
            callback.onError();
        }
        end();
    }

    /**
     * Start download
     * @param context   The current context
     * @param callback  Picasso callback
     */
    private void start(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        showProgress();
    }

    /**
     * End download
     */
    private void end() {
        hideProgress();
        clear();
    }

    /**
     * Show progress indicator
     */
    private void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide progress indicator
     */
    private void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Get the tag for this object
     * @return  tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the tag for this object
     * @param tag   Tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }
}
