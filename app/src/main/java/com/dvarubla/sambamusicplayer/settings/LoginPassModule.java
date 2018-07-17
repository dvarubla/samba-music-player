package com.dvarubla.sambamusicplayer.settings;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginPassModule {
    @PerApplication
    @Provides
    ILoginPassMan getLoginPassMan(LoginPassMan lpm){
        return lpm;
    }
}
