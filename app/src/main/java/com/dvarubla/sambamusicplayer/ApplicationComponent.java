package com.dvarubla.sambamusicplayer;

import android.content.Context;

import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.settings.SettingsModule;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.SmbUtilsModule;

import dagger.BindsInstance;
import dagger.Component;

@PerApplication
@Component(modules = {SettingsModule.class, SmbUtilsModule.class})
public interface ApplicationComponent {
    ISettings getSettings();
    ISmbUtils getSmbUtils();

    @Component.Builder
    interface Builder {
        ApplicationComponent build();
        @BindsInstance
        Builder setContext(Context context);
    }
}
