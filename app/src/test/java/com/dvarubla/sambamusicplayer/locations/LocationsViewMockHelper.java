package com.dvarubla.sambamusicplayer.locations;

import org.mockito.Mockito;

import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.when;

public class LocationsViewMockHelper {
    private ILocationsView _view;
    private PublishSubject<Object>
            _onEditObj = PublishSubject.create(),
            _onBackSubj = PublishSubject.create(),
            _onSaveObj = PublishSubject.create()
    ;
    LocationsViewMockHelper(){
        _view = Mockito.mock(ILocationsView.class);
        when(_view.saveClicked()).thenReturn(_onSaveObj);
        when(_view.backClicked()).thenReturn(_onBackSubj);
        when(_view.editClicked()).thenReturn(_onEditObj);
    }

    public ILocationsView getView() {
        return _view;
    }

    public PublishSubject<Object> getOnEditObj() {
        return _onEditObj;
    }

    public PublishSubject<Object> getOnBackSubj() {
        return _onBackSubj;
    }

    public PublishSubject<Object> getOnSaveObj() {
        return _onSaveObj;
    }
}
