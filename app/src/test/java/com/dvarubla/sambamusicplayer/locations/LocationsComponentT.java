package com.dvarubla.sambamusicplayer.locations;

import dagger.Component;

@LocationsScope
@Component(modules = {LocationsModuleT.class})
public interface LocationsComponentT {
    void inject(LocationsPresenterEditT testObj);
}
