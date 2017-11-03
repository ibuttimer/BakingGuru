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

package ie.ianbuttimer.bakingguru.network;

import android.content.Context;
import android.support.annotation.StringRes;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import ie.ianbuttimer.bakingguru.R;
import ie.ianbuttimer.bakingguru.exception.HttpException;
import ie.ianbuttimer.bakingguru.utils.Dialog;
import timber.log.Timber;

/**
 * Class to perfoem a synchronous network request
 */
@SuppressWarnings("unused")
public class NetworkRequesterSync {

    private WeakReference<Context> mContext;

    /**
     * Constructor
     * @param context   The current context
     */
    public NetworkRequesterSync(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    /**
     * Perform a request
     * @param urlStr    url to request
     * @return  response string
     */
    public String request(String urlStr) {
        Context context = mContext.get();
        String result = null;

        if (NetworkUtils.isInternetAvailable(context)) {
            URL url = null;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                Timber.e("URL error", e);
            }

            try {
                result = NetworkUtils.getHttpResponseStringSync(url);
            } catch (IOException e) {
                Dialog.showAlertDialog(context, getErrorId(e));
                Timber.e("Network error", e);
            }
        } else {
            Dialog.showNoNetworkDialog(context);
        }
        return result;
    }

    /**
     * Perform a request
     * @param urlId    url to request
     * @return  response string
     */
    public String request(@StringRes int urlId) {
        String url = mContext.get().getString(urlId);
        return request(url);
    }

    /**
     * Get id of error message
     * @param e     Exception result of request
     * @return  error message resource id
     */
    protected int getErrorId(IOException e) {
        int msgId = R.string.invalid_response;
        if (e instanceof UnknownHostException) {
            msgId = R.string.cant_contact_server;
        } else if (e instanceof HttpException) {
            if (((HttpException) e).isUnauthorised()) {
                msgId = R.string.unauthorised_access;
            }
        }
        return msgId;
    }

}
