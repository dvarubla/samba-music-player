package com.dvarubla.sambamusicplayer.smbutils;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class SmbUtilsModule {
    @Provides
    @PerApplication
    ISmbUtils getSmbUtils(){
        return new SmbUtils();
    }
}
