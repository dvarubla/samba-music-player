package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

public interface ILocationsFixedCtrl extends LocationsCtrl {
    void onCreated(RecyclerView v);

    void onDetach();
}
