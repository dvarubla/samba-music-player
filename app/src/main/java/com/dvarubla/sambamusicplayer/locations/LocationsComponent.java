package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.settings.SettingsComponent;

import dagger.Component;

@LocationsScope
@Component(modules = {LocationsModule.class, LocationsPresenterModule.class}, dependencies = {SettingsComponent.class})
public interface LocationsComponent {
    LocationsEditableFragment getEditableFragment();
    LocationsFixedFragment getFixedFragment();
    LocationsFixedCtrl getFixedCtrl();
    LocationsEditableCtrl getEditableCtrl();
    void inject(LocationsActivity activity);
}
