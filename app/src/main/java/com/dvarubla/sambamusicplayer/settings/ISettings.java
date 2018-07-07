package com.dvarubla.sambamusicplayer.settings;

import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import java.util.HashMap;

public interface ISettings {
    String [] getLocations();
    void saveLocations(String [] locations);
    HashMap<String, LoginPass> getAuthData();
    void saveAuthData(HashMap<String, LoginPass> authData);
}
