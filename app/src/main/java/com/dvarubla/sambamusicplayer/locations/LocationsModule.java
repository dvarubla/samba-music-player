package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.settings.ISettings;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsModule {
    @LocationsScope
    @Provides static ILocationsEditableCtrl getILocEdComp(LocationsEditableCtrl ctrl){
        return ctrl;
    }

    @LocationsScope
    @Provides static ILocationsFixedCtrl getILocFixComp(LocationsFixedCtrl ctrl){
        return ctrl;
    }

    @LocationsScope
    @Provides static LocationsEditableCtrl getLocEdComp(){
        return new LocationsEditableCtrl();
    }

    @LocationsScope
    @Provides static LocationsFixedCtrl getLocFixComp(ISettings settings){
        return new LocationsFixedCtrl(settings.getLocations());
    }

    @LocationsScope
    @Provides static LocationsEditableFragment getLocEdFrg(LocationsEditableCtrl ctrl) {
        LocationsEditableFragment frag = new LocationsEditableFragment();
        frag.setCtrl(ctrl);
        return frag;
    }

    @LocationsScope
    @Provides static LocationsFixedFragment getLocFixFrg(LocationsFixedCtrl ctrl) {
        LocationsFixedFragment frag = new LocationsFixedFragment();
        frag.setCtrl(ctrl);
        return frag;
    }
}
