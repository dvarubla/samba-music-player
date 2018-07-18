package com.dvarubla.sambamusicplayer.toastman;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class ToastManModule {

    @PerApplication
    @Provides
    IToastMan getToastMan(ToastMan tm){
        return tm;
    }
}
