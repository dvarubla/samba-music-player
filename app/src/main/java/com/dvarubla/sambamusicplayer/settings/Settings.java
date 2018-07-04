package com.dvarubla.sambamusicplayer.settings;

public class Settings implements ISettings {
    @Override
    public String[] getLocations() {
        return new String[]{
            "Location 1",
            "Location 2",
            "Location 3"
        };
    }
}
