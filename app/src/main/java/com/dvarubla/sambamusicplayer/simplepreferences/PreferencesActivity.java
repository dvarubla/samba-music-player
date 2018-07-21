package com.dvarubla.sambamusicplayer.simplepreferences;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.dvarubla.sambamusicplayer.R;

public class PreferencesActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().add(R.id.preferences_layout, new SettingsFragment())
                .commit();
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setTitle(R.string.settings);
        }
    }
}
