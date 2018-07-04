package com.dvarubla.sambamusicplayer.locations;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsPresenterModule {
    @LocationsScope
    @Provides static ILocationsPresenter LocationsPresenter(LocationsPresenter presenter){
        return presenter;
    }
}
