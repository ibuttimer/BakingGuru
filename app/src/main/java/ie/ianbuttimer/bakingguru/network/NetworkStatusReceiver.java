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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

import ie.ianbuttimer.bakingguru.BakingGuruApp;
import timber.log.Timber;

/**
 * Broadcast receiver class for network status
 */
@SuppressWarnings("unused")
public class NetworkStatusReceiver extends BroadcastReceiver {

    private static ArrayList<NetworkStatusListener> listeners;

    /**
     * Default constructor
     */
    public NetworkStatusReceiver() {
        super();
        listeners = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = isInternetAvailable(context);
        for (NetworkStatusListener listener : listeners) {
            listener.onNetworkStatusChanged(connected);
        }
    }

    /**
     * Register a network status change listener
     * @param listener  Listener to register
     * @return <code>true</code> if the listener collection changed as a result of the call
     */
    public static boolean registerListener(NetworkStatusListener listener) {
        boolean changed = false;
        if ((listeners != null) && (listener != null)) {
            if (!listeners.contains(listener)) {
                changed = listeners.add(listener);
            }
        }
        return changed;
    }

    /**
     * Unregister a network status change listener
     * @param listener  Listener to unregister
     * @return <code>true</code> if the listener collection changed as a result of the call
     */
    public static boolean unregisterListener(NetworkStatusListener listener) {
        boolean changed = false;
        if ((listeners != null) && (listener != null)) {
            changed = listeners.remove(listener);
        }
        return changed;
    }

    /**
     * Check if an internet connection is available
     * @return  true if internet connection is available
     */
    public boolean isInternetAvailable() {
        return isInternetAvailable(BakingGuruApp.getWeakApplicationContext().get());
    }

    /**
     * Check if an internet connection is available
     * @param context   The current context
     * @return  true if internet connection is available
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean available = ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
        Timber.d("Internet available: " + (available ? "yes" : " no"));
        return available;
    }

    /**
     * Interface to be implemented in order to receive status updates
     */
    public interface NetworkStatusListener {
        /**
         * Called when network state has changed
         * @param isConnected   <code>true</code> if connected, <code>false</code> otherwise
         */
        void onNetworkStatusChanged(boolean isConnected);
    }
}
