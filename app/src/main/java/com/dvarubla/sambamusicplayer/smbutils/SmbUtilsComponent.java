package com.dvarubla.sambamusicplayer.smbutils;

import dagger.Component;

@SmbUtilsScope
@Component(modules = SmbUtilsModule.class)
public interface SmbUtilsComponent {
    ISmbUtils getSmbUtils();
}
