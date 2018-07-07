package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.settings.ISettings;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsModule {
    @PerActivity
    @Provides static ILocationsEditableCtrl getILocEdComp(LocationsEditableCtrl ctrl){
        return ctrl;
    }

    @PerActivity
    @Provides static ILocationsFixedCtrl getILocFixComp(LocationsFixedCtrl ctrl){
        return ctrl;
    }

    @PerActivity
    @Provides static LocationsEditableCtrl getLocEdComp(){
        return new LocationsEditableCtrl();
    }

    @PerActivity
    @Provides static LocationsFixedCtrl getLocFixComp(ISettings settings){
        return new LocationsFixedCtrl(settings.getLocations());
    }

    @PerActivity
    @Provides static LocationsEditableFragment getLocEdFrg(LocationsEditableCtrl ctrl) {
        LocationsEditableFragment frag = new LocationsEditableFragment();
        frag.setCtrl(ctrl);
        return frag;
    }

    @PerActivity
    @Provides static LocationsFixedFragment getLocFixFrg(LocationsFixedCtrl ctrl) {
        LocationsFixedFragment frag = new LocationsFixedFragment();
        frag.setCtrl(ctrl);
        return frag;
    }
}
