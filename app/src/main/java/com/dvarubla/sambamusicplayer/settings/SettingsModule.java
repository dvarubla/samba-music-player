package com.dvarubla.sambamusicplayer.settings;

import android.content.Context;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {
    @PerApplication
    @Provides
    ISettings getSettings(Context context){
        return new Settings(context);
    }
}
