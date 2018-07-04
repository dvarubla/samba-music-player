package com.dvarubla.sambamusicplayer.settings;

import dagger.Component;

@SettingsScope
@Component(modules = SettingsModule.class)
public interface SettingsComponent {
    ISettings getSettings();
}
