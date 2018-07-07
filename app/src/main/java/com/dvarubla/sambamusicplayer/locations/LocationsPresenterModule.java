package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsPresenterModule {
    @PerActivity
    @Provides static ILocationsPresenter LocationsPresenter(LocationsPresenter presenter){
        return presenter;
    }
}
