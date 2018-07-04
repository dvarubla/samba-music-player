package com.dvarubla.sambamusicplayer.settings;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
class SettingsModule {
    @SettingsScope
    @Provides
    ISettings getSettings(Context context){
        return new Settings(context);
    }
}
