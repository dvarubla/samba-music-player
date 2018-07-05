package com.dvarubla.sambamusicplayer.locations;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.settings.ISettings;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.functions.Consumer;

public class LocationsPresenter implements ILocationsPresenter {
    @Inject
    Lazy<ILocationsEditableCtrl> _edLocComponent;
    @Inject
    ISettings _settings;
    private ILocationsFixedCtrl _fixedLocComponent;
    private boolean _isEditPressed;
    private ILocationsView _view;

    @SuppressLint("CheckResult")
    @Inject
    LocationsPresenter(ILocationsFixedCtrl fixedLocComponent){
        _fixedLocComponent = fixedLocComponent;
        _fixedLocComponent.locationClicked().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                _view.showFileList(s);
            }
        });
    }

    @Override
    public boolean isEditPressed() {
        return _isEditPressed;
    }

    @SuppressLint("CheckResult")
    @Override
    public void setView(ILocationsView view) {
        _view = view;
        _view.editClicked().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                _isEditPressed = true;
                _edLocComponent.get().setStrings(_fixedLocComponent.getStrings());
                _view.editLocations();
            }
        });

        _view.backClicked().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                _isEditPressed = false;
                _edLocComponent.get().setStrings(_fixedLocComponent.getStrings());
            }
        });

        _view.saveClicked().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                String [] locations = _edLocComponent.get().getStrings();
                _fixedLocComponent.setStrings(locations);
                _settings.saveLocations(locations);
                _view.showSettingsSaved();
            }
        });

        _view.addClicked().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                _edLocComponent.get().addNewString("");
            }
        });
    }
}
