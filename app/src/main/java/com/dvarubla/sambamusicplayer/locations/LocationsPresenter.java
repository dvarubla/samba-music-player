package com.dvarubla.sambamusicplayer.locations;

import android.annotation.SuppressLint;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class LocationsPresenter implements ILocationsPresenter {
    @Inject
    Lazy<ILocationsEditableCtrl> _edLocComponent;
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
    public ILocationsFixedCtrl getLocFixComp() {
        return _fixedLocComponent;
    }

    @Override
    public ILocationsEditableCtrl getLocEdComp() {
        return _edLocComponent.get();
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
                _fixedLocComponent.setStrings(_edLocComponent.get().getStrings());
                _view.showSettingsSaved();
            }
        });
    }
}
