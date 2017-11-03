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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Class providing basic handling of content provider responses.<br>
 * <br>
 * Requests may be initiated in a number of ways:
 * <ul>
 * <li>{@link ICallback#insert(FragmentActivity, int, Uri, ContentValues)} / {@link AsyncCallback#processInsertResponse(InsertResultWrapper)}<br>
 *     Utilises the insert() method of a ContentProvider to add data to a database.
 * </li>
 * <li>{@link ICallback#query(FragmentActivity, int, Uri, String[], String, String[], String)} / {@link AsyncCallback#processQueryResponse(QueryResultWrapper)}<br>
 *     Utilises the query() method of a ContentProvider to read data from a database.
 * </li>
 * <li>{@link ICallback#update(FragmentActivity, int, Uri, ContentValues, String, String[])} / {@link AsyncCallback#processUpdateResponse(UpdateResultWrapper)}<br>
 *     Utilises the update() method of a ContentProvider to update data in a database.
 * </li>
 * <li>{@link ICallback#delete(FragmentActivity, int, Uri, String, String[])} / {@link AsyncCallback#processDeleteResponse(DeleteResultWrapper)}<br>
 *     Utilises the delete() method of a ContentProvider to delete data from a database.
 * </li>
 * <li>{@link ICallback#bulkInsert(FragmentActivity, int, Uri, ContentValues[])} / {@link AsyncCallback##processBulkInsertResponse(BulkInsertResultWrapper)}<br>
 *     Utilises the bulkInsert() method of a ContentProvider to add data to a database.
 * </li>
 * </ul>
 */
@SuppressWarnings("unused")
public abstract class AsyncCallback implements ICallback, LoaderManager.LoaderCallbacks<AbstractResultWrapper> {

    //////// ICallback implementation ////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putContentValues(contentValues)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_INSERT_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void query(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putProjection(projection)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .putSortOrder(sortOrder)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_QUERY_LOADER));
    }

    /**
     * Convenience wrapper for {@link ICallback#query(FragmentActivity, int, Uri, String[], String, String[], String) )}
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     */
    public void query(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri) {
        query(activity, loaderId, uri, null, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putContentValues(contentValues)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_UPDATE_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_DELETE_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsert(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues[] contentValues) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putContentValues(contentValues)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_BULK_INSERT_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putMethod(method)
                .putArg(arg)
                .putExtras(extras)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_CALL_LOADER));
    }

    /**
     * Asynchronously call the specified method for the specified Uri
     * @param activity  Current activity
     * @param loaderId  Id of loader
     * @param uri       Uri to send
     * @param method    Provider-defined method to call
     */
    public void call(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @NonNull String method) {
        call(activity, loaderId, uri, method, null, null);
    }

    /**
     * Start a loader
     * @param activity  Current activity
     * @param args      Arguments bundle
     * @param loaderId  Id of loader to start
     */
    private void startLoader(@NonNull FragmentActivity activity, @NonNull Bundle args, int loaderId) {
        LoaderManager manager = activity.getSupportLoaderManager();
        Loader loader = manager.getLoader(loaderId);
        if (loader == null) {
            // Initialize the loader
            manager.initLoader(loaderId, args, this);
        } else {
            manager.restartLoader(loaderId, args, this);
        }
    }



    //////// LoaderManager.LoaderCallbacks implementation ////////

    @Override
    public Loader<AbstractResultWrapper> onCreateLoader(int id, final Bundle args) {
        ContentProviderLoader loader;
        Context context = getContext();
        switch (getSubLoaderId(id)) {
            case CONTENT_PROVIDER_CALL_LOADER:
                // create an AsyncTaskLoader to handle the call to the content provider
                loader = new ContentProviderCallLoader(context, args);
                break;
            case CONTENT_PROVIDER_INSERT_LOADER:
            case CONTENT_PROVIDER_QUERY_LOADER:
            case CONTENT_PROVIDER_UPDATE_LOADER:
            case CONTENT_PROVIDER_DELETE_LOADER:
            case CONTENT_PROVIDER_BULK_INSERT_LOADER:
                loader = new ContentProviderCrudLoader(context, args);
                break;
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<AbstractResultWrapper> loader, AbstractResultWrapper data) {

        if (data != null) {
            ResponseHandler handler = data.getHandler();
            switch (handler) {
                case INSERT_HANDLER:
                    processInsertResponse((ICallback.InsertResultWrapper)data);
                    break;
                case QUERY_HANDLER:
                    processQueryResponse((ICallback.QueryResultWrapper)data);
                    break;
                case UPDATE_HANDLER:
                    processUpdateResponse((ICallback.UpdateResultWrapper)data);
                    break;
                case DELETE_HANDLER:
                    processDeleteResponse((ICallback.DeleteResultWrapper)data);
                    break;
                case BULK_INSERT_HANDLER:
                    processBulkInsertResponse((ICallback.BulkInsertResultWrapper)data);
                    break;
                case CALL_HANDLER:
                    processCallResponse((ICallback.CallResultWrapper)data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // no op
    }


    @Override
    public void processInsertResponse(@Nullable InsertResultWrapper response) {
        // no op
    }

    @Override
    public void processQueryResponse(@Nullable QueryResultWrapper response) {
        // no op
    }

    @Override
    public void processUpdateResponse(@Nullable UpdateResultWrapper response) {
        // no op
    }

    @Override
    public void processDeleteResponse(@Nullable DeleteResultWrapper response) {
        // no op
    }

    @Override
    public void processBulkInsertResponse(@Nullable BulkInsertResultWrapper response) {
        // no op
    }

    @Override
    public void processCallResponse(@Nullable CallResultWrapper response) {
        // no op
    }

    //////// Additional methods ////////

    /**
     * Get the current context
     * @return  Current context
     */
    public abstract Context getContext();

    /**
     * Generate a loader id for the LoaderManager
     * @param clientId  Id provided by client
     * @param subId     Sub id i.e. method indicator
     * @return  Loader id
     */
    public static int generateLoaderId(int clientId, int subId) {
        return ((clientId * LOADER_FACTOR) + subId);
    }

    /**
     * Get the sub id from a LoaderManager id
     * @param loaderId  LoaderManager id
     * @return  sub id
     */
    public static int getSubLoaderId(int loaderId) {
        return (loaderId % LOADER_FACTOR);
    }

}
