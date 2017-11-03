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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import org.parceler.Parcels;

import ie.ianbuttimer.bakingguru.utils.ErrorTuple;

import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_ARG;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_ERROR;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_EXTRAS;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_METHOD;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_RESULT_TYPE;
import static ie.ianbuttimer.bakingguru.data.ICallback.CONTENT_PROVIDER_URI;


/**
 * Class to asynchronously handle a call to the ContentProvider <code>call</code> interface.<br>
 */

public class ContentProviderCallLoader extends ContentProviderLoader {

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     */
    public ContentProviderCallLoader(Context context, Bundle args) {
        super(context, args);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractResultWrapper loadInBackground() {

        // Extract the call arguments from the args
        Uri uri = args.getParcelable(CONTENT_PROVIDER_URI);
        String method = args.getString(CONTENT_PROVIDER_METHOD);
        String arg = args.getString(CONTENT_PROVIDER_ARG);
        Bundle extras = args.getBundle(CONTENT_PROVIDER_EXTRAS);

        if ((uri == null) || TextUtils.isEmpty(method)) {
            return null;    // can't do anything
        }

        Context context = getContext();
        Bundle bundle = context.getContentResolver().call(uri, method, arg, extras);

        AbstractResultWrapper result = null;
        if (bundle != null) {
            // get result of type appropriate to result data
            int resultType = bundle.getInt(CONTENT_PROVIDER_RESULT_TYPE, -1);
            if (resultType == AbstractResultWrapper.ResultType.STRING.ordinal()) {
                result = new ICallback.CallResultWrapper(uri, bundle.getString(method));
            } else if (resultType == AbstractResultWrapper.ResultType.BUNDLE.ordinal()) {
                result = new ICallback.CallResultWrapper(uri, bundle.getBundle(method));
            } else if (resultType == AbstractResultWrapper.ResultType.ERROR.ordinal()) {
                ErrorTuple tuple = Parcels.unwrap(bundle.getParcelable(CONTENT_PROVIDER_ERROR));
                result = new ICallback.CallResultWrapper(uri, tuple);
            }
        }
        return result;
    }
}
