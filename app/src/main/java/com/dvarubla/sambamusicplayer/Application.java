package com.dvarubla.sambamusicplayer;

import com.dvarubla.sambamusicplayer.settings.DaggerSettingsComponent;
import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.settings.SettingsComponent;

public class Application extends android.support.multidex.MultiDexApplication {
    private static SettingsComponent _settingsComp;

    @Override
    public void onCreate() {
        super.onCreate();
        _settingsComp = DaggerSettingsComponent.builder().build();
    }

    public static SettingsComponent getSettingsComp() {
        return _settingsComp;
    }
}
