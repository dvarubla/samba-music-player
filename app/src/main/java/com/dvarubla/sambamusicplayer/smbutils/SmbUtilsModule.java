package com.dvarubla.sambamusicplayer.smbutils;

import dagger.Module;
import dagger.Provides;

@Module
public class SmbUtilsModule {
    @Provides
    @SmbUtilsScope
    ISmbUtils getSmbUtils(){
        return new SmbUtils();
    }
}
