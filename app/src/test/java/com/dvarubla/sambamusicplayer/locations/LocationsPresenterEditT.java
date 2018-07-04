package com.dvarubla.sambamusicplayer.locations;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

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
        _viewMockH.getOnEditObj().onNext(new Object());
        verify(_edCtrl, times(1)).setStrings(testLocs);
    }
}
