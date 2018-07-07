package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {LocationsModuleT.class})
public interface LocationsComponentT {
    void inject(LocationsPresenterEditT testObj);
}
