package com.dvarubla.sambamusicplayer.settings;

import android.content.Context;

import dagger.BindsInstance;
import dagger.Component;

@SettingsScope
@Component(modules = SettingsModule.class)
public interface SettingsComponent {
    ISettings getSettings();

    @Component.Builder
    interface Builder {
        SettingsComponent build();
        @BindsInstance
        Builder setContext(Context context);
    }
}
