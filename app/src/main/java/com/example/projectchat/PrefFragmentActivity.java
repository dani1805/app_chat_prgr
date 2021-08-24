package com.example.projectchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;


public class PrefFragmentActivity extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PreferenceScreen preferenceScreen;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Context context = getPreferenceManager().getContext();
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);

        // sacar una referencia a una preferencia, en este caso, al switch del modo oscuro
        SwitchPreference darkModeSwitch = findPreference("darkMode");
        darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean options = ((SwitchPreference) preference).isChecked();

                if (!options) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                return true;
            }
        });

        SwitchPreference changePref = findPreference("changeColor");
        ListPreference bubbleReceiver = findPreference("bubbleReceiver");
        ListPreference bubbleTransmitter = findPreference("bubbleTransmitter");
        changePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean activate = ((SwitchPreference) preference).isChecked();

                if (!activate) {
                    bubbleReceiver.setEnabled(true);
                    bubbleTransmitter.setEnabled(true);
                } else {

                    bubbleReceiver.setEnabled(false);
                    bubbleTransmitter.setEnabled(false);
                }
                return true;
            }
        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
