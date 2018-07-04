package com.dvarubla.sambamusicplayer.locations;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsModuleT{
    @LocationsScope
    @Provides
    static ILocationsEditableCtrl getLocEdComp(){
        return Mockito.mock(ILocationsEditableCtrl.class);
    }

    @LocationsScope
    @Provides static ILocationsFixedCtrl getLocFixComp(){
        return Mockito.mock(ILocationsFixedCtrl.class);
    }
}
