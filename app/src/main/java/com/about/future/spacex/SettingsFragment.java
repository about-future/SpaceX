package com.about.future.spacex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.about.future.spacex.widget.UpdateIntentService;
//import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (p instanceof ListPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setListSummary(p, value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat && TextUtils.equals(key, getString(R.string.notifications_key))) {
                // Get the current state of the switch preference
                boolean switchIsOn = sharedPreferences.getBoolean(key, true);

                if (switchIsOn) {
                    // Subscribe to this topic
                    //FirebaseMessaging.getInstance().subscribeToTopic(key);
                } else {
                    // Otherwise, unsubscribe
                    //FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                }
            }

            if (preference instanceof ListPreference) {
                // Get the current preferred unit
                String value = sharedPreferences.getString(key, "");
                setListSummary(preference, value);
            }

            // Update widget
            UpdateIntentService.startActionUpdateMissionWidget(getContext());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setListSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            // Look up the correct display value in the list
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }
}
