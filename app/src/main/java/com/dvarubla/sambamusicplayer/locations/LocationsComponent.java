package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.ApplicationComponent;
import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.toastman.IToastManActivity;

import dagger.Component;

@PerActivity
@Component(modules = {LocationsModule.class, LocationsPresenterModule.class}, dependencies = {ApplicationComponent.class})
public interface LocationsComponent {
    LocationsEditableFragment getEditableFragment();
    LocationsFixedFragment getFixedFragment();
    LocationsFixedCtrl getFixedCtrl();
    LocationsEditableCtrl getEditableCtrl();
    IToastManActivity getToastManActivity();
    void inject(LocationsActivity activity);
}
