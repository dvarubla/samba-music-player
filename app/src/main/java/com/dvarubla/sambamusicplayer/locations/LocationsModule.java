package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.settings.ISettings;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsModule {
    @LocationsScope
    @Provides static ILocationsEditableCtrl getLocEdComp(){
        return new LocationsEditableCtrl();
    }

    @LocationsScope
    @Provides static ILocationsFixedCtrl getLocFixComp(ISettings settings){
        return new LocationsFixedCtrl(settings.getLocations());
    }

    @LocationsScope
    @Provides static LocationsEditableFragment getLocEdFrg(ILocationsEditableCtrl ctrl) {
        LocationsEditableFragment frag = new LocationsEditableFragment();
        frag.setCtrl(ctrl);
        return frag;
    }

    @LocationsScope
    @Provides static LocationsFixedFragment getLocFixFrg(ILocationsFixedCtrl ctrl) {
        LocationsFixedFragment frag = new LocationsFixedFragment();
        frag.setCtrl(ctrl);
        return frag;
    }
}
