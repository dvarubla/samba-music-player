package com.dvarubla.sambamusicplayer.locations;

import io.reactivex.Observable;

interface ILocationsView {
    void editLocations();
    Observable<Object> editClicked();
    Observable<Object> saveClicked();
    Observable<Object> backClicked();
    void showFileList(String str);
    void showSettingsSaved();
}
