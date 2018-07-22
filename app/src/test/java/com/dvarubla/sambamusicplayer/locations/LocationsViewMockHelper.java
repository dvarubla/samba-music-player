package com.dvarubla.sambamusicplayer.locations;

import org.mockito.Mockito;

import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.when;

public class LocationsViewMockHelper {
    private ILocationsView _view;
    private PublishSubject<Object>
            _onEditSubj = PublishSubject.create(),
            _onBackSubj = PublishSubject.create(),
            _onSaveSubj = PublishSubject.create(),
            _onAddSubj = PublishSubject.create(),
            _onSettingsSubj = PublishSubject.create();

    LocationsViewMockHelper(){
        _view = Mockito.mock(ILocationsView.class);
        when(_view.saveClicked()).thenReturn(_onSaveSubj);
        when(_view.backClicked()).thenReturn(_onBackSubj);
        when(_view.editClicked()).thenReturn(_onEditSubj);
        when(_view.addClicked()).thenReturn(_onAddSubj);
        when(_view.settingsClicked()).thenReturn(_onSettingsSubj);
    }

    public ILocationsView getView() {
        return _view;
    }

    public PublishSubject<Object> getOnEditSubj() {
        return _onEditSubj;
    }

    public PublishSubject<Object> getOnBackSubj() {
        return _onBackSubj;
    }

    public PublishSubject<Object> getOnSaveSubj() {
        return _onSaveSubj;
    }

    public PublishSubject<Object> getOnAddSubj() {
        return _onAddSubj;
    }

    public PublishSubject<Object> getOnSettingsSubj() {
        return _onSettingsSubj;
    }
}
