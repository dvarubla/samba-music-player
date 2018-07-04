package com.dvarubla.sambamusicplayer.locations;

import io.reactivex.Observable;

public interface ILocationsPresenter {
    boolean isEditPressed();
    void setView(ILocationsView view);
}
