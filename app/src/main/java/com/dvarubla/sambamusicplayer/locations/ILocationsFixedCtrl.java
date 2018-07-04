package com.dvarubla.sambamusicplayer.locations;

import io.reactivex.Observable;

public interface ILocationsFixedCtrl extends LocationsCtrl {
    Observable<String> locationClicked();
}
