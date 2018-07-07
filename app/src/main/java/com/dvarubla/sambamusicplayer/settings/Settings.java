package com.dvarubla.sambamusicplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.dvarubla.sambamusicplayer.smbutils.LoginPass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Settings implements ISettings {
    private Gson _gson;
    private SharedPreferences _prefs;
    @SuppressWarnings("FieldCanBeLocal")
    private final String PREFS_NAME = "com.dvarubla.sambamusicplayer";
    private final String LOCATIONS_NAME = "locations";
    private final String AUTH_DATA_NAME = "auth_data";

    Settings(Context context){
        _gson = new Gson();
        _prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String[] getLocations() {
        String str = _prefs.getString(LOCATIONS_NAME, null);
        if(str == null){
            return new String[0];
        } else {
            return _gson.fromJson(str, String[].class);
        }
    }

    @Override
    public void saveLocations(String[] locations) {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(LOCATIONS_NAME, _gson.toJson(locations));
        editor.apply();
    }

    @Override
    public HashMap<String, LoginPass> getAuthData() {
        String str = _prefs.getString(AUTH_DATA_NAME, null);
        if(str == null){
            return new HashMap<>();
        } else {
            Type collectionType = new TypeToken<HashMap<String, LoginPass>>(){}.getType();
            return _gson.fromJson(str, collectionType);
        }
    }

    @Override
    public void saveAuthData(HashMap<String, LoginPass> authData) {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(AUTH_DATA_NAME, _gson.toJson(authData));
        editor.apply();
    }
}
