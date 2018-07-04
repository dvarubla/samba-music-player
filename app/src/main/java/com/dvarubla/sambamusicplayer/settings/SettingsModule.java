package com.dvarubla.sambamusicplayer.settings;

import dagger.Module;
import dagger.Provides;

@Module
class SettingsModule {
    @SettingsScope
    @Provides
    ISettings getSettings(){
        return new Settings();
    }
}
