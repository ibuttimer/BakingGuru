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
package ie.ianbuttimer.bakingguru;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.MessageFormat;


import ie.ianbuttimer.bakingguru.data.DbCacheIntentService;
import ie.ianbuttimer.bakingguru.utils.PreferenceControl;

import static android.app.Activity.RESULT_OK;
import static ie.ianbuttimer.bakingguru.data.DbCacheIntentService.RESULT_COUNT;
import static ie.ianbuttimer.bakingguru.data.DbCacheIntentService.RESULT_RECEIVER;


/**
 * Settings fragment to display the general preferences
 *
 * NOTE: this class utilises copies of some on the methods created by the Create Settings Activity wizard
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int[] PREFERENCE_KEYS = new int[] {
        R.string.pref_caching_key,
        R.string.pref_cache_length_key
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_cache_length_key)));

        bindOnOffPreference(findPreference(getString(R.string.pref_caching_key)));

        Preference preference = findPreference(getString(R.string.pref_clear_cache_key));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // delete all cached movie info
                Context context = preference.getContext();
                Intent intent = DbCacheIntentService.getLaunchIntent(context, DbCacheIntentService.DELETE_ALL_RECIPES);

                intent.putExtra(RESULT_RECEIVER,
                        new ResultReceiver(new Handler()) {
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                if (resultCode == RESULT_OK) {
                                    int count = resultData.getInt(RESULT_COUNT, 0);
                                    String msg;
                                    if (count == 0) {
                                        msg = getString(R.string.no_recipes_to_del);
                                    } else {
                                        msg = getResources().getQuantityString(R.plurals.deleted_recipes, count, count);
                                    }
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );

                context.startService(intent);

                // change the preference value to trigger the onchange listener (don't care about value)
                String key = preference.getKey();
                boolean currentValue = PreferenceControl.getSharedBooleanPreference(context, key, R.bool.pref_clear_cache_dflt_value);
                PreferenceControl.setSharedBooleanPreference(context, key, !currentValue);

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                setSummary(preference, (index >= 0
                                ? listPreference.getEntries()[index]
                                : null));
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                setSummary(preference, stringValue);
            }

            return true;
        }
    };

    /**
     * A preference value change listener that handles on/off type preference changes
     */
    private Preference.OnPreferenceChangeListener onOffPreferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Context context = preference.getContext();
            boolean boolValue = Boolean.valueOf(value.toString());

            switch (getPreferenceKeyId(preference)) {
                case R.string.pref_caching_key:
                    // enable/disable caching-related preferences
                    for (int prefKey : new int[] {
                        R.string.pref_cache_length_key, R.string.pref_clear_cache_key
                    }) {
                        Preference cachePref = findPreference(context.getString(prefKey));
                        cachePref.setEnabled(boolValue);
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
    };

    /**
     * Set the summary for a preference
     * @param preference    Preference to set summary for
     * @param value         Value string
     */
    private static void setSummary(Preference preference, CharSequence value) {
        Context context = preference.getContext();
        CharSequence summary;
        switch (getPreferenceKeyId(preference)) {
            case R.string.pref_cache_length_key:
                summary = MessageFormat.format(context.getString(R.string.pref_cache_length_summary), value);
                break;
            default:
                summary = value;
                break;
        }
        preference.setSummary(summary);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Binds a on/off preference to an appropriate listener.
     *
     * @see #onOffPreferenceListener
     */
    private void bindOnOffPreference(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(onOffPreferenceListener);

        // Trigger the listener immediately with the preference's current value.
        onOffPreferenceListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    /**
     * Get the resource id of the specified preference
     * @param preference    The changed preference
     * @return  Resource id of preference key, or 0 if not found
     */
    private static int getPreferenceKeyId(Preference preference) {
        int id = 0;
        Context context = preference.getContext();
        String key = preference.getKey();
        for (int i = 0; i < PREFERENCE_KEYS.length; i++) {
            if (key.equals(context.getString(PREFERENCE_KEYS[i]))) {
                id = PREFERENCE_KEYS[i];
                break;
            }
        }
        return id;
    }
}
