package com.dvarubla.sambamusicplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Settings implements ISettings {
    private Gson _gson;
    private SharedPreferences _prefs;
    @SuppressWarnings("FieldCanBeLocal")
    private final String PREFS_NAME = "com.dvarubla.sambamusicplayer";
    private final String LOCATIONS_NAME = "locations";

    Settings(Context context){
        _gson = new Gson();
        _prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String[] getLocations() {
        String str = _prefs.getString(LOCATIONS_NAME, null);
        if(str == null){
            return new String[]{""};
        } else {
            return _gson.fromJson(str, String[].class);
        }
    }

    @Override
    public void saveLocations(String[] locations) {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(LOCATIONS_NAME, _gson.toJson(locations, String[].class));
        editor.apply();
    }
}
