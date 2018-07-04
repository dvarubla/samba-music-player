package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;

public interface ILocationsFixedCtrl extends LocationsCtrl {
    void onCreated(RecyclerView v);
    Observable<String> locationClicked();
}
