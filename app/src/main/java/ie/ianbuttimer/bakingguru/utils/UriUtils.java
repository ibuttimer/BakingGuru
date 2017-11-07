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

package ie.ianbuttimer.bakingguru.utils;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import ie.ianbuttimer.bakingguru.data.db.BakingContract;
import ie.ianbuttimer.bakingguru.data.provider.BakingContentProvider;
import timber.log.Timber;


/**
 * Utility class for Uri functionality
 */
@SuppressWarnings("unused")
public class UriUtils {

    /**
     * Private constructor
     */
    private UriUtils() {
        // can't instantiate class
    }

    /**
     * Make a recipe with id uri
     * @param id    Id of movie
     * @return  Uri
     */
    public static Uri getRecpeWithIdUri(int id) {
        return ContentUris.withAppendedId(BakingContract.RecipeEntry.CONTENT_URI, id);
    }

    /**
     * Make a recipe with id uri
     * @param id    Id of movie
     * @return  Uri
     */
    public static Uri getRecipeWithIdAdditionalInfoUri(int id, String info) {
        return ContentUris.withAppendedId(BakingContract.RecipeEntry.CONTENT_URI, id).buildUpon()
                .appendPath(info).build();
    }

    /**
     * Match a uri
     * @param uri   Uri to match
     * @return  The code for the matched node, or -1 if there is no matched node.
     */
    public static int matchBakingUri(Uri uri) {
        return BakingContentProvider.sUriMatcher.match(uri);
    }

    /**
     * Get the id from a 'with id' uri
     * @param uri   Uri to get id from
     * @return  Id string
     */
    public static String getIdFromWithIdUri(@NonNull Uri uri) {
        String id = "";
        if (uri != null) {
            id = uri.getLastPathSegment();
        }
        return id;
    }

    /**
     * Get a selection args id array from a 'with id' uri
     * @param uri   Uri to get id from
     * @return  Id array
     */
    public static String[] getIdSelectionArgFromWithIdUri(@NonNull Uri uri) {
        String[] array;
        String id = getIdFromWithIdUri(uri);
        if (TextUtils.isEmpty(id)) {
            array = new String[] {};
        } else {
            array = new String[] { id };
        }
        return array;
    }


    /**
     * Convert a Url to a uri
     * @param urlString   Url to convert
     * @return  Equivalent uri or <code>null</code> if error
     */
    public static @Nullable Uri urlToUri(@NonNull String urlString) {
        Uri uri = null;
        try {
            if (!TextUtils.isEmpty(urlString)) {
                URL url = new URL(urlString);
                URI netUri = url.toURI();
                uri = Uri.parse(netUri.toString());
            }
        } catch (MalformedURLException e) {
            Timber.e("Invalid URL string", e);
        } catch (URISyntaxException e) {
            Timber.e("Invalid Uri", e);
        }
        return uri;
    }

    /**
     * Convert a Url to a uri
     * @param url   Url to convert
     * @return  Equivalent uri or <code>null</code> if error
     */
    public static @Nullable Uri urlToUri(@NonNull URL url) {
        Uri uri = null;
        try {
            URI netUri = url.toURI();
            uri = Uri.parse(netUri.toString());
        } catch (URISyntaxException e) {
            Timber.e(e);
        }
        return uri;
    }

    /**
     * Convert a uri to a Url
     * @param uri   Uri to convert
     * @return  Equivalent Url or <code>null</code> if error
     */
    public static @Nullable URL uriToUrl(@NonNull Uri uri) {
        URL url = null;
        try {
            URI netUri = URI.create(uri.toString());
            url = netUri.toURL();
        } catch (MalformedURLException e) {
            Timber.e(e);
        }
        return url;
    }

}
