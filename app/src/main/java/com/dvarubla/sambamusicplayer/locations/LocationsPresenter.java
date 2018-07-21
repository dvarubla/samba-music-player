package com.dvarubla.sambamusicplayer.locations;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.toastman.IToastMan;

import javax.inject.Inject;

import dagger.Lazy;

public class LocationsPresenter implements ILocationsPresenter {
    @Inject
    IToastMan _toastMan;
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
        _fixedLocComponent.locationClicked().subscribe(s -> _view.showFileList(s));
    }

    @Override
    public boolean isEditPressed() {
        return _isEditPressed;
    }

    @SuppressLint("CheckResult")
    @Override
    public void setView(ILocationsView view) {
        _view = view;
        _view.editClicked().subscribe(o -> {
            _isEditPressed = true;
            _edLocComponent.get().setStrings(_fixedLocComponent.getStrings());
            _view.editLocations();
        });

        _view.backClicked().subscribe(o -> {
                _isEditPressed = false;
                _edLocComponent.get().setStrings(_fixedLocComponent.getStrings());
        });

        _view.saveClicked().subscribe(o -> {
                String [] locations = _edLocComponent.get().getStrings();
                _fixedLocComponent.setStrings(locations);
                _settings.saveLocations(locations);
                _toastMan.showSettingsSaved();
        });

        _view.addClicked().subscribe(o -> _edLocComponent.get().addNewString(""));

        _view.settingsClicked().subscribe(o -> _view.showSettings());
    }
}
