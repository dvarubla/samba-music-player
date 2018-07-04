package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.settings.ISettings;

import org.mockito.Mockito;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.when;

@Module
public class LocationsModuleT{
    private static PublishSubject<String> _clickedSubj = PublishSubject.create();

    @LocationsScope
    @Provides
    static ISettings getSettings(){
        return Mockito.mock(ISettings.class);
    }

    @LocationsScope
    @Provides
    static ILocationsEditableCtrl getLocEdComp(){
        return Mockito.mock(ILocationsEditableCtrl.class);
    }

    @LocationsScope
    @Provides static ILocationsFixedCtrl getLocFixComp(){
        ILocationsFixedCtrl ctrl = Mockito.mock(ILocationsFixedCtrl.class);
        when(ctrl.locationClicked()).thenReturn(_clickedSubj);
        return ctrl;
    }

    @LocationsScope
    @Provides @Named("LocationsFixedClickSubj") static PublishSubject<String> getLocFixSubj(){
        return _clickedSubj;
    }
}
