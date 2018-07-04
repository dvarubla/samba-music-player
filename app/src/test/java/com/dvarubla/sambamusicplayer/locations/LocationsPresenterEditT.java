package com.dvarubla.sambamusicplayer.locations;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationsPresenterEditT {
    @Inject
    LocationsPresenter _presenter;

    @Inject
    ILocationsEditableCtrl _edCtrl;

    @Inject
    ILocationsFixedCtrl _fixedCtrl;

    private LocationsViewMockHelper _viewMockH = new LocationsViewMockHelper();

    @Before
    public void before(){
        DaggerLocationsComponentT.builder().build().inject(this);
        _presenter.setView(_viewMockH.getView());
    }

    @Test
    public void tEdit(){
        String[] testLocs = {
                "a",
                "b"
        };
        when(_fixedCtrl.getStrings()).thenReturn(testLocs);
        _viewMockH.getOnEditSubj().onNext(new Object());
        verify(_edCtrl, times(1)).setStrings(testLocs);
    }

    @Test
    public void tEditAndSave(){
        String[] testLocs = {
                "a",
                "b"
        };
        when(_fixedCtrl.getStrings()).thenReturn(testLocs);
        _viewMockH.getOnEditSubj().onNext(new Object());
        String[] testLocsEd = {
                "b",
                "d"
        };
        when(_edCtrl.getStrings()).thenReturn(testLocsEd);
        _viewMockH.getOnSaveSubj().onNext(new Object());
        verify(_fixedCtrl, times(1)).setStrings(testLocsEd);
        verify(_viewMockH.getView(), times(1)).showSettingsSaved();
    }

    @Test
    public void tEditAndDiscard(){
        String[] testLocs = {
                "a",
                "b"
        };
        when(_fixedCtrl.getStrings()).thenReturn(testLocs);
        _viewMockH.getOnEditSubj().onNext(new Object());
        String[] testLocsEd = {
                "b",
                "d"
        };
        when(_edCtrl.getStrings()).thenReturn(testLocsEd);
        _viewMockH.getOnBackSubj().onNext(new Object());
        verify(_fixedCtrl, never()).setStrings(any(String[].class));
    }
}
