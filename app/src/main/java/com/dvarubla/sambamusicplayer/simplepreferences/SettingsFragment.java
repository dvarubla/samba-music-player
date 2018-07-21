package com.dvarubla.sambamusicplayer.simplepreferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.dvarubla.sambamusicplayer.R;

import static com.dvarubla.sambamusicplayer.Common.SIMPLE_PREFS_NAME;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(SIMPLE_PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
    }
}
