package com.dvarubla.sambamusicplayer.locations;

import io.reactivex.Observable;

interface ILocationsView {
    void editLocations();
    Observable<Object> editClicked();
    Observable<Object> settingsClicked();
    Observable<Object> saveClicked();
    Observable<Object> backClicked();
    Observable<Object> addClicked();
    void showFileList(String str);
    void showSettings();
}
