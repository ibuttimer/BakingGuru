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

package ie.ianbuttimer.bakingguru.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.lang.ref.WeakReference;

import ie.ianbuttimer.bakingguru.bake.AbstractBakeObject;
import ie.ianbuttimer.bakingguru.image.AbstractImageLoader;
import ie.ianbuttimer.bakingguru.image.PicassoUtil;
import ie.ianbuttimer.bakingguru.network.NetworkStatusReceiver;
import ie.ianbuttimer.bakingguru.network.NetworkUtils;
import timber.log.Timber;


/**
 * A base RecyclerView.ViewHolder for AbstractBakeObject objects
 */
@SuppressWarnings("unused")
class BakeViewImageLoader<T extends AbstractBakeObject> {

    private T mItem;
    private ImageView mImageImageView;
    private IBakeViewImageLoader<T> mHost;

    private AbstractImageLoader<T> mImageLoader;
    @Nullable private NetworkStatusReceiver.NetworkStatusListener mNetworkStatus;

    /**
     * Constructor
     * @param imageView     ImageView to display image
     *
     */
    BakeViewImageLoader(@NonNull ImageView imageView, @NonNull IBakeViewImageLoader<T> host) {
        mImageImageView = imageView;
        mHost = host;
        mImageLoader = null;
        mNetworkStatus = null;
    }

    /**
     * Set the image specified by the recipe
     * @param info  Recipe object
     */
    public void setImage(T info) {
        cancel();
        mItem = info;

        if (info != null) {
            Context context = mImageImageView.getContext();

            if (mImageLoader == null) {
                mImageLoader = mHost.getNewImageLoader();
            }

            // load image
            Uri uri = mImageLoader.getImageUri(info);
            if (uri == null) {
                mHost.setDefaultImage();
            } else {
                // get image from cache & if na download
                Bitmap image = PicassoUtil.getImage(uri);
                if (image == null) {
                    if (NetworkUtils.isInternetAvailable(context)) {
                        loadImage();
                    } else {
                        // set default image and register listener to download proper image
                        mHost.setDefaultImage();
                        registerNetworkStatusListener();
                        Timber.d("registered listener for " + uri);
                    }
                } else {
                    mImageImageView.setImageBitmap(image);
                    mProcessLoaded.onSuccess();
                }
            }
        }
    }

    public void hideImage() {
        mImageImageView.setVisibility(View.INVISIBLE);
    }

    public void showImage() {
        mImageImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Load the image from the appropriate url
     */
    private void loadImage() {
        mImageLoader.loadImage(mImageImageView.getContext(), mItem, mProcessLoaded);
    }

    /** Cancel in progress or pending loads */
    public void cancel() {
        if (mImageLoader != null) {
            mImageLoader.cancelImageLoad();
        }
        unregisterNetworkStatusListener();
    }

    /**
     * Register a request recipes on network connection event listener
     */
    private void registerNetworkStatusListener() {
        if (mNetworkStatus == null) {
            mNetworkStatus = new ReconnectedNetworkStatusListener(this);
        }
        NetworkStatusReceiver.registerListener(mNetworkStatus);
    }

    /**
     * Unregister a request recipes on network connection event listener
     */
    void unregisterNetworkStatusListener() {
        if (mNetworkStatus != null) {
            NetworkStatusReceiver.unregisterListener(mNetworkStatus);
        }
    }

    /** Process Picasso callbacks */
    private Callback mProcessLoaded = new Callback() {
        @Override
        public void onSuccess() {
            if (mImageImageView != null) {
                mImageImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mImageImageView.setAdjustViewBounds(true);
            }
        }

        @Override
        public void onError() {
            mHost.setDefaultImage();
        }
    };


    /**
     * Class to request recipe image on network connection event
     */
    private class ReconnectedNetworkStatusListener implements NetworkStatusReceiver.NetworkStatusListener {

        private WeakReference<BakeViewImageLoader<T>> mViewHolder;

        ReconnectedNetworkStatusListener(BakeViewImageLoader<T> viewHolder) {
            this.mViewHolder = new WeakReference<>(viewHolder);
        }

        @Override
        public void onNetworkStatusChanged(boolean isConnected) {
            if (isConnected) {
                BakeViewImageLoader<T> viewHolder = mViewHolder.get();
                viewHolder.loadImage();
                viewHolder.unregisterNetworkStatusListener();
            }
        }
    }

    interface IBakeViewImageLoader<T extends AbstractBakeObject> {

        /**
         * Set the default image
         */
        void setDefaultImage();

        /**
         * Get a new image loader object
         * @return  Image loader object
         */
        AbstractImageLoader<T> getNewImageLoader();
    }
}
