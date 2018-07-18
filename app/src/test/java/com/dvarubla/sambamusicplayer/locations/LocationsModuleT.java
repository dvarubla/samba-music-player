package com.dvarubla.sambamusicplayer.locations;

import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.toastman.IToastMan;

import org.mockito.Mockito;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.when;

@Module
public class LocationsModuleT{
    private static PublishSubject<String> _clickedSubj = PublishSubject.create();

    @PerActivity
    @Provides
    static ISettings getSettings(){
        return Mockito.mock(ISettings.class);
    }

    @PerActivity
    @Provides
    static ILocationsEditableCtrl getLocEdComp(){
        return Mockito.mock(ILocationsEditableCtrl.class);
    }

    @PerActivity
    @Provides static ILocationsFixedCtrl getLocFixComp(){
        ILocationsFixedCtrl ctrl = Mockito.mock(ILocationsFixedCtrl.class);
        when(ctrl.locationClicked()).thenReturn(_clickedSubj);
        return ctrl;
    }

    @PerActivity
    @Provides @Named("LocationsFixedClickSubj") static PublishSubject<String> getLocFixSubj(){
        return _clickedSubj;
    }

    @PerActivity
    @Provides
    public IToastMan getToastMan(){
        return Mockito.mock(IToastMan.class);
    }
}
