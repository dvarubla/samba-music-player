package com.dvarubla.sambamusicplayer.toastman;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class ToastManModule {
    @PerApplication
    @Provides
    IToastManActivity getToastManActivity(ToastMan tm){
        return tm;
    }

    @PerApplication
    @Provides
    IToastMan getToastMan(IToastManActivity tm){
        return tm;
    }
}
